package dev.matthiesen.cobbled_market.neoforge;

import dev.matthiesen.cobbled_market.common.CobbledMarketCommon;
import net.neoforged.fml.common.Mod;

@Mod(CobbledMarketCommon.MOD_ID)
public final class CobbledMarketNeoForge {
    public static final CobbledMarketCommon INSTANCE = CobbledMarketCommon.INSTANCE;

    public CobbledMarketNeoForge() {
        INSTANCE.createInfoLog("Loading for NeoForge Mod Loader");
        INSTANCE.initialize();
    }
}
