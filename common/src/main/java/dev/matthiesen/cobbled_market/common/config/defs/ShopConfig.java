package dev.matthiesen.cobbled_market.common.config.defs;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public record ShopConfig(
        String shopId,
        String shopName,
        String shopIcon,
        List<ShopEntry> entries
) {
    public static final String DEFAULT_SHOP_ICON = "minecraft:chest";

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
        Item icon = ShopEntry.parseItem(this.shopIcon);
        if (icon == Items.AIR) {
            icon = Items.CHEST;
        }
        return new Live(this.shopId, this.shopName, icon, liveEntries);
    }

    public record Live(String shopId, String shopName, Item shopIcon, List<ShopEntry.Live> entries) {
    }
}
