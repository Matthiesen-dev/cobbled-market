package dev.matthiesen.cobbled_market.common.config.defs;

import com.electronwill.nightconfig.core.Config;

import java.util.List;

public record ShopConfig(
        String shopId,
        String shopName,
        List<ShopEntry> entries
) {
    public static ShopConfig deserialize(Config config) {
        String shopId = config.get("shopId");
        String shopName = config.get("shopName");
        List<? extends Config> entryConfigs = config.get("entries");

        List<ShopEntry> entries = entryConfigs.stream()
                .map(ShopEntry::deserialize)
                .toList();

        return new ShopConfig(shopId, shopName, entries);
    }

    public static boolean isValid(Config config) {
        String shopId = config.get("shopId");
        String shopName = config.get("shopName");
        List<? extends Config> entryConfigs = config.get("entries");

        if (shopId == null || shopId.isEmpty()) {
            return false;
        }

        if (shopName == null || shopName.isEmpty()) {
            return false;
        }

        for (Config entryConfig : entryConfigs) {
            if (!ShopEntry.isValid(entryConfig)) {
                return false;
            }
        }

        return true;
    }

    public Config serialize() {
        Config config = Config.inMemory();
        config.set("shopId", this.shopId);
        config.set("shopName", this.shopName);
        List<Config> entryConfigs = this.entries.stream()
                .map(ShopEntry::serialize)
                .toList();
        config.set("entries", entryConfigs);
        return config;
    }
}
