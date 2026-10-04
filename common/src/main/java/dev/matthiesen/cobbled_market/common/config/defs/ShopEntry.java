package dev.matthiesen.cobbled_market.common.config.defs;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import com.cobblemon.mod.common.util.PlayerExtensionsKt;
import com.electronwill.nightconfig.core.Config;
import dev.matthiesen.cobbled_market.common.CobbledMarketCommon;
import dev.matthiesen.cobbled_market.common.config.CobbledMarketConfig;
import dev.matthiesen.cobbled_market.common.utility.MenuUtilities;
import dev.matthiesen.matthiesen_core.common.api.economy.EconomyProvider;
import dev.matthiesen.matthiesen_core.common.utility.SoundsPlayer;
import dev.matthiesen.matthiesen_core.common.utility.item.ItemDecoder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public record ShopEntry(String itemId, List<PurchaseOption> options, boolean hasOptions) {
    public static final ShopEntry DEFAULT_1 = new ShopEntry("minecraft:stone", 64, 10);
    public static final List<PurchaseOption> DEFAULT_2_OPTIONS = List.of(
            new PurchaseOption(1, 1),
            new PurchaseOption(32, 15),
            new PurchaseOption(64, 30)
    );
    public static final ShopEntry DEFAULT_2 = new ShopEntry("minecraft:dirt", DEFAULT_2_OPTIONS);
    public static final List<ShopEntry> DEFAULT_ENTRIES = List.of(DEFAULT_1, DEFAULT_2);

    public ShopEntry {
        if (itemId == null || itemId.isEmpty()) {
            throw new IllegalArgumentException("Shop entry must have an item ID");
        }
        options = List.copyOf(options);
        if (options.isEmpty() || (!hasOptions && options.size() != 1)) {
            throw new IllegalArgumentException("Shop entry must have purchase options, or exactly one legacy purchase");
        }
    }

    public ShopEntry(String itemId, int quantity, int price) {
        this(itemId, List.of(new PurchaseOption(quantity, price)), false);
    }

    public ShopEntry(String itemId, List<PurchaseOption> options) {
        this(itemId, options, true);
    }

    public static Item parseItem(String itemRaw) {
        Item item = ItemDecoder.stringToItem(itemRaw, Items.AIR);
        if (item == null) {
            return Items.AIR;
        }
        return item;
    }

    public static ShopEntry deserialize(Config config) {
        if (!isValid(config)) {
            throw new IllegalArgumentException("Invalid shop entry: specify quantity/price or a nonempty options list");
        }
        String itemRaw = config.get("itemId");
        if (config.contains("options")) {
            List<? extends Config> optionConfigs = config.get("options");
            return new ShopEntry(itemRaw, optionConfigs.stream().map(PurchaseOption::deserialize).toList());
        }
        return new ShopEntry(itemRaw, List.of(PurchaseOption.deserialize(config)), false);
    }

    public static boolean isValid(Config config) {
        if (!(config.get("itemId") instanceof String itemRaw) || itemRaw.isEmpty()) {
            return false;
        }

        if (config.contains("options")) {
            if (config.contains("quantity") || config.contains("price")
                    || !(config.get("options") instanceof List<?> optionConfigs) || optionConfigs.isEmpty()) {
                return false;
            }
            return optionConfigs.stream().allMatch(option -> option instanceof Config optionConfig
                    && PurchaseOption.isValid(optionConfig));
        }
        return PurchaseOption.isValid(config);
    }

    public Config serialize() {
        Config config = Config.inMemory();
        config.set("itemId", this.itemId);
        if (hasOptions) {
            config.set("options", options.stream().map(PurchaseOption::serialize).toList());
        } else {
            config.set("quantity", options.getFirst().quantity());
            config.set("price", options.getFirst().price());
        }
        return config;
    }

    public Live toLiveShopEntry() {
        Item item = parseItem(itemId);
        if (item == Items.AIR) {
            throw new IllegalArgumentException("Invalid item: " + itemId);
        }
        return new Live(item, options, hasOptions);
    }

    public record Live(Item item, List<PurchaseOption> options, boolean hasOptions) {
        public Button toPurchaseButton(PurchaseOption option) {
            return GooeyButton.builder()
                    .display(MenuUtilities.getPurchaseOptionItem(item, option))
                    .onClick(action -> {
                        ServerPlayer sender = action.getPlayer();
                        boolean success = purchase(sender, option);
                        new SoundsPlayer(success ? SoundEvents.EXPERIENCE_ORB_PICKUP : SoundEvents.VILLAGER_NO)
                                .play(sender);
                    })
                    .build();
        }

        public boolean purchase(ServerPlayer player, PurchaseOption option) {
            if (!options.contains(option)) {
                throw new IllegalArgumentException("Purchase option does not belong to this shop entry");
            }
            var config = CobbledMarketConfig.SERVER_CONFIG;
            try {
                EconomyProvider provider = CobbledMarketCommon.INSTANCE.getEconomyManager().getEconomyProvider(config.currencyProvider.get().getId());
                String currencyId = config.currencyId.get();
                String currencyDisplayName = config.currencyDisplayName.get();
                boolean hasFunds = provider.hasEnough(player, option.price(), currencyId);

                if (!hasFunds) {
                    String messageFormat = config.messages_notEnoughFunds.get();
                    player.sendSystemMessage(Component.literal(String.format(messageFormat, currencyDisplayName, option.price(), currencyDisplayName)));
                    return false;
                }

                boolean success = provider.withdraw(player, option.price(), currencyId);
                if (!success) {
                    String messageFormat = config.messages_withdrawError.get();
                    player.sendSystemMessage(Component.literal(messageFormat));
                    return false;
                }

                var stack = this.item.getDefaultInstance();
                int remaining = option.quantity();
                while (remaining > 0) {
                    int count = Math.min(remaining, stack.getMaxStackSize());
                    PlayerExtensionsKt.giveOrDropItemStack(player, stack.copyWithCount(count), true);
                    remaining -= count;
                }
                String messageFormat = config.messages_purchaseSuccess.get();
                player.sendSystemMessage(Component.literal(String.format(messageFormat, option.quantity(), stack.getDisplayName().getString(), option.price(), currencyDisplayName)));
                return true;
            } catch (Exception e) {
                String messageFormat = CobbledMarketConfig.SERVER_CONFIG.messages_purchaseFailure.get();
                player.sendSystemMessage(
                        Component.literal(messageFormat)
                                .withStyle(net.minecraft.ChatFormatting.RED)
                );
                CobbledMarketCommon.INSTANCE.createErrorLog(
                        "Error while processing purchase for player %player%: %error%"
                                .replaceAll("%player%", player.getName().getString())
                                .replaceAll("%error%", e.getMessage()),
                        e
                );
                return false;
            }
        }
    }
}
