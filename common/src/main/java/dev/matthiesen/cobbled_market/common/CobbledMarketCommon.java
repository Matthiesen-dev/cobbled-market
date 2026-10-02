package dev.matthiesen.cobbled_market.common;

import dev.matthiesen.cobbled_market.common.command.MarketCommand;
import dev.matthiesen.cobbled_market.common.config.CobbledMarketConfig;
import dev.matthiesen.cobbled_market.common.economy_providers.CobbleDollarsCompat;
import dev.matthiesen.cobbled_market.common.registry.MolangExtensions;
import dev.matthiesen.cobbled_market.common.registry.PermissionRegistry;
import dev.matthiesen.libs.faststats.Token;
import dev.matthiesen.matthiesen_core.common.AbstractCommonMod;
import dev.matthiesen.matthiesen_core.common.api.events.PlatformEvents;
import dev.matthiesen.matthiesen_core.common.api.platform.loader.ModConfigType;
import org.jetbrains.annotations.NotNull;

public final class CobbledMarketCommon extends AbstractCommonMod {
    public static final String MOD_ID = "cobbled_market";
    public static final String MOD_NAME = "Cobbled Market";
    public static @Token final String METRICS_TOKEN = "406219fa1736ab6061ac707050302085";
    public static final CobbledMarketCommon INSTANCE = new CobbledMarketCommon();

    public CobbledMarketCommon() {
        super(MOD_ID, MOD_NAME);
    }

    @Override
    public @Token @NotNull String getMetricsToken() {
        return METRICS_TOKEN;
    }

    private static String configPath(String path) {
        return "cobbled_market/" + path + ".toml";
    }

    public void initialize() {
        super.initialize();

        registerModConfig(MOD_ID, ModConfigType.SERVER, CobbledMarketConfig.SERVER_SPEC, configPath("server"));
        registerModConfig(MOD_ID, ModConfigType.STARTUP, CobbledMarketConfig.PERMISSIONS_SPEC, configPath("permissions"));

        PermissionRegistry.init();
        getCommandsRegistryManager().registerCommand(MarketCommand.CMD);

        PlatformEvents.CONFIG_RELOADING(MOD_ID).subscribe(event -> CobbledMarketConfig.invalidateCachedShopConfigs());
        PlatformEvents.SERVER_STOPPED.subscribe(event -> CobbledMarketConfig.invalidateCachedShopConfigs());

        MolangExtensions.register();
        CobbleDollarsCompat.register();

        createInfoLog("Initialized");
    }
}
