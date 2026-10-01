package dev.matthiesen.cobbled_market.common;

import dev.matthiesen.libs.faststats.Token;
import dev.matthiesen.matthiesen_core.common.AbstractCommonMod;
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

    public void initialize() {
        super.initialize();

        createInfoLog("Initialized");
    }
}
