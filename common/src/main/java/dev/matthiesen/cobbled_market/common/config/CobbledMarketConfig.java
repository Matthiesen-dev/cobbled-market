package dev.matthiesen.cobbled_market.common.config;

import com.electronwill.nightconfig.core.file.FileConfig;
import dev.matthiesen.cobbled_market.common.CobbledMarketCommon;
import dev.matthiesen.cobbled_market.common.config.defs.ShopConfig;
import dev.matthiesen.matthiesen_core.common.utility.config.ConfigFolderLoader;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CobbledMarketConfig {
    private static final ConfigFolderLoader DYNAMIC_SHOP_LOADER = new ConfigFolderLoader(
            CobbledMarketCommon.INSTANCE, "shops"
    );

    public static final ServerConfig SERVER_CONFIG;
    public static final ModConfigSpec SERVER_SPEC;
    public static final PermissionsConfig PERMISSIONS_CONFIG;
    public static final ModConfigSpec PERMISSIONS_SPEC;

    static {
        Pair<ServerConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_CONFIG = specPair.getLeft();
        SERVER_SPEC = specPair.getRight();

        Pair<PermissionsConfig, ModConfigSpec> permissionsSpecPair = new ModConfigSpec.Builder().configure(PermissionsConfig::new);
        PERMISSIONS_CONFIG = permissionsSpecPair.getLeft();
        PERMISSIONS_SPEC = permissionsSpecPair.getRight();
    }

    private static List<ShopConfig.Live> loadDynamicShops() {
        if (DYNAMIC_SHOP_LOADER.getLoadedConfigs().isEmpty()) {
            DYNAMIC_SHOP_LOADER.loadConfigs();
        }
        List<ShopConfig.Live> shops = new ArrayList<>();
        for (Map.Entry<String, FileConfig> entry : DYNAMIC_SHOP_LOADER.getLoadedConfigs().entrySet()) {
            String shopId = entry.getKey();
            FileConfig config = entry.getValue();
            try {
                ShopConfig.Live liveShop = ShopConfig.deserialize(config, shopId).toLiveShop();
                shops.add(liveShop);
            } catch (Exception e) {
                CobbledMarketCommon.INSTANCE.createErrorLog(
                        "Skipping invalid shop configuration '" + shopId + "': " + e.getMessage(), e
                );
            }
        }
        return List.copyOf(shops);
    }

    private static List<ShopConfig.Live> loadShopConfigs() {
        var dynamicShops = loadDynamicShops();
        var staticShops = SERVER_CONFIG.shops.get().stream()
                .map(ShopConfig::deserialize)
                .map(ShopConfig::toLiveShop)
                .toList();
        List<ShopConfig.Live> allShops = new ArrayList<>(dynamicShops);
        allShops.addAll(staticShops);
        return List.copyOf(allShops);
    }

    private static List<ShopConfig.Live> cachedShopConfigs = null;

    public static List<ShopConfig.Live> getShopConfigs() {
        if (cachedShopConfigs == null) {
            cachedShopConfigs = loadShopConfigs();
        }
        return cachedShopConfigs;
    }

    public static void reloadCachedShopConfigs() {
        DYNAMIC_SHOP_LOADER.clearConfigs();
        cachedShopConfigs = loadShopConfigs();
    }

    public static void invalidateCachedShopConfigs() {
        cachedShopConfigs = null;
        DYNAMIC_SHOP_LOADER.clearConfigs();
    }

    public static ShopConfig.Live getShop(String shopId) {
        return getShopConfigs().stream()
                .filter(shopConfig -> shopConfig.shopId().equals(shopId))
                .findFirst()
                .orElse(null);
    }
}
