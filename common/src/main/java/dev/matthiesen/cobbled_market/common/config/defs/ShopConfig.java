package dev.matthiesen.cobbled_market.common.config.defs;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import com.cobblemon.mod.common.CobblemonSounds;
import com.electronwill.nightconfig.core.Config;
import dev.matthiesen.cobbled_market.common.menu.ShopScreen;
import dev.matthiesen.cobbled_market.common.utility.MenuUtilities;
import dev.matthiesen.cobbled_market.common.utility.NBTSerializer;
import dev.matthiesen.matthiesen_core.common.utility.SoundsPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public record ShopConfig(
        String shopId,
        String shopName,
        String shopIcon,
        List<ShopEntry> entries
) {
    public static final String DEFAULT_SHOP_ICON = "minecraft:chest";
    public static final ShopConfig DEFAULT_SHOP_CONFIG = new ShopConfig(
            "example_shop",
            "Example Shop",
            DEFAULT_SHOP_ICON,
            ShopEntry.DEFAULT_ENTRIES
    );
    public static final List<Config> DEFAULT_ENTRIES = List.of(DEFAULT_SHOP_CONFIG.serialize());

    public static ShopConfig deserialize(Config config) {
        String shopId = config.get("shopId");
        String shopName = config.get("shopName");
        String shopIcon = config.getOrElse("shopIcon", DEFAULT_SHOP_ICON);
        List<? extends Config> entryConfigs = config.get("entries");

        List<ShopEntry> entries = entryConfigs.stream()
                .map(ShopEntry::deserialize)
                .toList();

        return new ShopConfig(shopId, shopName, shopIcon, entries);
    }

    public static boolean isValid(Config config) {
        if (!(config.get("shopId") instanceof String shopId) || shopId.isEmpty()) {
            return false;
        }

        if (!(config.get("shopName") instanceof String shopName) || shopName.isEmpty()) {
            return false;
        }

        Object shopIcon = config.get("shopIcon");
        if (shopIcon != null && !(shopIcon instanceof String)) {
            return false;
        }

        if (!(config.get("entries") instanceof List<?> entryConfigs)) {
            return false;
        }

        for (Object entryConfig : entryConfigs) {
            if (!(entryConfig instanceof Config entry) || !ShopEntry.isValid(entry)) {
                return false;
            }
        }

        return true;
    }

    public Config serialize() {
        Config config = Config.inMemory();
        config.set("shopId", this.shopId);
        config.set("shopName", this.shopName);
        config.set("shopIcon", this.shopIcon);
        List<Config> entryConfigs = this.entries.stream()
                .map(ShopEntry::serialize)
                .toList();
        config.set("entries", entryConfigs);
        return config;
    }

    public Live toLiveShop() {
        List<ShopEntry.Live> liveEntries = this.entries.stream()
                .map(ShopEntry::toLiveShopEntry)
                .toList();
        ItemStack icon = NBTSerializer.stringToItemStack(this.shopIcon);
        if (icon.isEmpty() || icon.getItem() == Items.AIR) {
            icon = new ItemStack(Items.CHEST);
        }
        return new Live(this.shopId, this.shopName, icon, liveEntries);
    }

    public record Live(String shopId, String shopName, ItemStack shopIcon, List<ShopEntry.Live> entries) {
        public Button toShopButton() {
            return GooeyButton.builder()
                    .display(MenuUtilities.getShopItem(this))
                    .onClick(action -> {
                        ServerPlayer sender = action.getPlayer();
                        new SoundsPlayer(CobblemonSounds.PC_CLICK).play(sender);
                        UIManager.openUIForcefully(sender, new ShopScreen(sender, this, true).getPage());
                    })
                    .build();
        }
    }
}
