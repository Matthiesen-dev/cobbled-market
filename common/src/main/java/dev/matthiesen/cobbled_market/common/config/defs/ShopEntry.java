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

public record ShopEntry(String itemId, int quantity, int price) {
    public static Item parseItem(String itemRaw) {
        Item item = ItemDecoder.stringToItem(itemRaw, Items.AIR);
        if (item == null) {
            return Items.AIR;
        }
        return item;
    }

    public static ShopEntry deserialize(Config config) {
        String itemRaw = config.get("itemId");
        int quantity = config.<Number>get("quantity").intValue();
        int price = config.<Number>get("price").intValue();

        Item item = parseItem(itemRaw);
        if (item == Items.AIR) {
            throw new IllegalArgumentException("Invalid item: " + itemRaw);
        }

        return new ShopEntry(itemRaw, quantity, price);
    }

    public static boolean isValid(Config config) {
        if (!(config.get("itemId") instanceof String itemRaw) || itemRaw.isEmpty()) {
            return false;
        }

        if (!(config.get("quantity") instanceof Number quantity) || !(config.get("price") instanceof Number price)) {
            return false;
        }

        return quantity.intValue() > 0 && price.intValue() >= 0;
    }

    public Config serialize() {
        Config config = Config.inMemory();
        config.set("itemId", this.itemId);
        config.set("quantity", this.quantity);
        config.set("price", this.price);
        return config;
    }

    public Live toLiveShopEntry() {
        return new Live(ItemDecoder.stringToItem(this.itemId, Items.AIR), this.quantity, this.price);
    }

    public record Live(Item item, int quantity, int price) {
        public Button toShopEntryButton() {
            return GooeyButton.builder()
                    .display(MenuUtilities.getShopEntryItem(this))
                    .onClick(action -> {
                        ServerPlayer sender = action.getPlayer();
                        boolean success = purchase(sender);
                        new SoundsPlayer(success ? SoundEvents.EXPERIENCE_ORB_PICKUP : SoundEvents.VILLAGER_NO)
                                .play(sender);
                    })
                    .build();
        }

        public boolean purchase(ServerPlayer player) {
            try {
                var config = CobbledMarketConfig.SERVER_CONFIG;
                EconomyProvider provider = CobbledMarketCommon.INSTANCE.getEconomyManager().getEconomyProvider(config.currencyProvider.get());
                String currencyId = config.currencyId.get();
                String currencyDisplayName = config.currencyDisplayName.get();
                boolean hasFunds = provider.hasEnough(player, this.price, currencyId);

                if (!hasFunds) {
                    String messageFormat = "You do not have enough %s to purchase this item. You need %d %s.";
                    player.sendSystemMessage(Component.literal(String.format(messageFormat, currencyDisplayName, this.price, currencyDisplayName)));
                    return false;
                }

                boolean success = provider.withdraw(player, this.price, currencyId);
                if (!success) {
                    String messageFormat = "An error occurred while processing your purchase. Please try again later.";
                    player.sendSystemMessage(Component.literal(messageFormat));
                    return false;
                }

                PlayerExtensionsKt.giveOrDropItemStack(player, this.item.getDefaultInstance().copyWithCount(this.quantity), true);
                String messageFormat = "You have purchased %d x %s for %d %s.";
                player.sendSystemMessage(Component.literal(String.format(messageFormat, this.quantity, this.item.getDefaultInstance().getDisplayName().getString(), this.price, currencyDisplayName)));
                return true;
            } catch (Exception e) {
                player.sendSystemMessage(
                        Component.literal("An error occurred while processing the purchase. Please try again later.")
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
