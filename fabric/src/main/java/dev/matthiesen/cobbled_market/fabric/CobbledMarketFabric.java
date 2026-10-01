package dev.matthiesen.cobbled_market.fabric;

import dev.matthiesen.cobbled_market.common.CobbledMarketCommon;
import net.fabricmc.api.ModInitializer;

public final class CobbledMarketFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        var instance = CobbledMarketCommon.INSTANCE;
        instance.createInfoLog("Loading for Fabric Mod Loader");
        instance.initialize();
    }
}
