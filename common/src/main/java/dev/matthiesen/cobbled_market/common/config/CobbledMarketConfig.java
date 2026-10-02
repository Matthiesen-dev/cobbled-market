package dev.matthiesen.cobbled_market.common.config;

import com.electronwill.nightconfig.core.Config;
import dev.matthiesen.cobbled_market.common.CobbledMarketCommon;
import dev.matthiesen.cobbled_market.common.config.defs.ShopConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public final class CobbledMarketConfig {
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

    private static List<ShopConfig.Live> cachedShopConfigs = null;

    public static List<ShopConfig.Live> getShopConfigs() {
        if (cachedShopConfigs == null) {
            cachedShopConfigs = loadShopConfigs();
        }
        return cachedShopConfigs;
    }

    public static void reloadCachedShopConfigs() {
        cachedShopConfigs = loadShopConfigs();
    }

    public static void invalidateCachedShopConfigs() {
        cachedShopConfigs = null;
    }

    private static List<ShopConfig.Live> loadShopConfigs() {
        List<ShopConfig.Live> shops = new ArrayList<>();
        for (Config config : SERVER_CONFIG.shops.get()) {
            try {
                shops.add(ShopConfig.deserialize(config).toLiveShop());
            } catch (Exception e) {
                CobbledMarketCommon.INSTANCE.createErrorLog(
                        "Skipping invalid shop configuration '" + config.get("shopId") + "': " + e.getMessage(), e
                );
            }
        }
        return List.copyOf(shops);
    }

    public static ShopConfig.Live getShop(String shopId) {
        return getShopConfigs().stream()
                .filter(shopConfig -> shopConfig.shopId().equals(shopId))
                .findFirst()
                .orElse(null);
    }
}
