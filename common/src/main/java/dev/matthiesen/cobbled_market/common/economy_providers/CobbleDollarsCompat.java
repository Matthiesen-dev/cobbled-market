package dev.matthiesen.cobbled_market.common.economy_providers;

import dev.matthiesen.cobbled_market.common.CobbledMarketCommon;
import dev.matthiesen.matthiesen_core.common.api.economy.EconomyProvider;
import dev.matthiesen.matthiesen_core.common.utility.player_data.ServerUser;
import fr.harmex.cobbledollars.common.utils.extensions.PlayerExtensionKt;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class CobbleDollarsCompat {
    public static final String MOD_ID = "cobbledollars";

    public static void register() {
        if (CobbledMarketCommon.INSTANCE.getCommonUtils().isModLoaded(MOD_ID)) {
            CobbledMarketCommon.INSTANCE.createInfoLog("Registering CobbleDollars economy provider");
            CobbledMarketCommon.INSTANCE.getEconomyManager().registerEconomyProvider(Provider.INSTANCE);
        }
    }

    public static class Provider implements EconomyProvider {
        public static final Provider INSTANCE = new Provider();

        @Override
        public String providerId() {
            return MOD_ID;
        }

        @Override
        public String providerDisplayName() {
            return "CobbleDollars";
        }

        @Override
        public int getBalance(ServerUser serverUser, String s) throws IllegalArgumentException {
            BigInteger balance = PlayerExtensionKt.getCobbleDollars(serverUser.getOfflinePlayer());
            return balance.intValue();
        }

        @Override
        public void deposit(ServerUser serverUser, int i, String s) throws IllegalArgumentException {
            BigInteger toDeposit = new BigInteger(String.valueOf(i));
            PlayerExtensionKt.addOfflineCobbleDollars(serverUser.getUUID(), CobbledMarketCommon.INSTANCE.getCommonUtils().getServer(), toDeposit);
        }

        @Override
        public boolean withdraw(ServerUser serverUser, int i, String s) throws IllegalArgumentException {
            try {
                BigInteger balance = PlayerExtensionKt.getCobbleDollars(serverUser.getOfflinePlayer());
                if (balance.intValue() < i) {
                    return false;
                }

                var subtractedBalance = balance.subtract(new BigDecimal(i).toBigInteger());
                PlayerExtensionKt.setCobbleDollars(serverUser.getOfflinePlayer(), subtractedBalance);
                return true;
            } catch (RuntimeException e) {
                CobbledMarketCommon.INSTANCE.createErrorLog("Error processing CobbleDollars transaction for player %player%"
                        .replace("%player%", serverUser.getUsername()), e);
                return false;
            }
        }

        @Override
        public boolean hasEnough(ServerUser serverUser, int i, String s) throws IllegalArgumentException {
            BigInteger balance = PlayerExtensionKt.getCobbleDollars(serverUser.getOfflinePlayer());
            return balance.intValue() >= i;
        }
    }
}
