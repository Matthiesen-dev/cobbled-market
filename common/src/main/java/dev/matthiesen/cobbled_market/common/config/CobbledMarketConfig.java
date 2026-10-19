package dev.matthiesen.cobbled_market.common.config;

import com.electronwill.nightconfig.core.file.FileConfig;
import dev.matthiesen.cobbled_market.common.CobbledMarketCommon;
import dev.matthiesen.cobbled_market.common.config.defs.ShopConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class CobbledMarketConfig {
    public static final ServerConfig SERVER_CONFIG;
    public static final ModConfigSpec SERVER_SPEC;
    public static final PermissionsConfig PERMISSIONS_CONFIG;
    public static final ModConfigSpec PERMISSIONS_SPEC;

    private static final Map<String, FileConfig> LOADED_SHOP_CONFIGS = new HashMap<>();

    static {
        Pair<ServerConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_CONFIG = specPair.getLeft();
        SERVER_SPEC = specPair.getRight();

        Pair<PermissionsConfig, ModConfigSpec> permissionsSpecPair = new ModConfigSpec.Builder().configure(PermissionsConfig::new);
        PERMISSIONS_CONFIG = permissionsSpecPair.getLeft();
        PERMISSIONS_SPEC = permissionsSpecPair.getRight();
    }

    private static void loadDynamicShopConfigs() {
        LOADED_SHOP_CONFIGS.values().forEach(FileConfig::close);
        LOADED_SHOP_CONFIGS.clear();

        Path shopFolder = CobbledMarketCommon.INSTANCE.getCommonUtils().getModConfig(CobbledMarketCommon.MOD_ID, "shops");

        if (!Files.exists(shopFolder)) {
            try {
                Files.createDirectories(shopFolder);
            } catch (IOException e) {
                CobbledMarketCommon.INSTANCE.createErrorLog("Failed to create shops folder: " + e.getMessage(), e);
                return;
            }
        }

        try (Stream<Path> stream = Files.list(shopFolder)) {
            stream.filter(path -> path.toString().endsWith(".toml"))
                    .forEach(CobbledMarketConfig::loadSingleShopFile);
        } catch (IOException e) {
            CobbledMarketCommon.INSTANCE.createErrorLog("Failed to load shop configs: " + e.getMessage(), e);
        }
    }

    private static void loadSingleShopFile(Path file) {
        try {
            String filename = file.getFileName().toString();
            String shopId = filename.substring(0, filename.lastIndexOf('.'));
            FileConfig config = FileConfig.builder(file)
                    .autosave()
                    .preserveInsertionOrder()
                    .build();
            config.load();
            LOADED_SHOP_CONFIGS.put(shopId, config);
            CobbledMarketCommon.INSTANCE.createInfoLog("Loaded shop config: " + shopId);
        } catch (Exception e) {
            CobbledMarketCommon.INSTANCE.createErrorLog("Failed to load shop config file '" + file.getFileName() + "': " + e.getMessage(), e);
        }
    }

    private static List<ShopConfig.Live> loadDynamicShops() {
        if (LOADED_SHOP_CONFIGS.isEmpty()) {
            loadDynamicShopConfigs();
        }
        List<ShopConfig.Live> shops = new ArrayList<>();
        for (Map.Entry<String, FileConfig> entry : LOADED_SHOP_CONFIGS.entrySet()) {
            String shopId = entry.getKey();
            FileConfig config = entry.getValue();
            try {
                shops.add(ShopConfig.deserialize(config, shopId).toLiveShop());
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
        LOADED_SHOP_CONFIGS.values().forEach(FileConfig::close);
        LOADED_SHOP_CONFIGS.clear();
        cachedShopConfigs = loadShopConfigs();
    }

    public static void invalidateCachedShopConfigs() {
        cachedShopConfigs = null;
        LOADED_SHOP_CONFIGS.values().forEach(FileConfig::close);
        LOADED_SHOP_CONFIGS.clear();
    }

    public static ShopConfig.Live getShop(String shopId) {
        return getShopConfigs().stream()
                .filter(shopConfig -> shopConfig.shopId().equals(shopId))
                .findFirst()
                .orElse(null);
    }
}
