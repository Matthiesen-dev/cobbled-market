package dev.matthiesen.cobbled_market.fabric;

import dev.matthiesen.cobbled_market.common.CobbledMarketCommon;
import net.fabricmc.api.ModInitializer;

public final class CobbledMarketFabric implements ModInitializer {
    public static final CobbledMarketCommon INSTANCE = CobbledMarketCommon.INSTANCE;

    @Override
    public void onInitialize() {
        INSTANCE.createInfoLog("Loading for Fabric Mod Loader");
        INSTANCE.initialize();
    }
}
