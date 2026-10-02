package dev.matthiesen.cobbled_market.common.economy_providers;

import dev.matthiesen.matthiesen_core.common.api.economy.BuiltInEconomyProviders;

public enum CobbledMarketProviders {
    ITEM(BuiltInEconomyProviders.ITEM),
    IMPACTOR(BuiltInEconomyProviders.IMPACTOR),
    COBBLEDOLLARS(CobbleDollarsCompat.MOD_ID);

    private final String id;

    CobbledMarketProviders(BuiltInEconomyProviders provider) {
        this.id = provider.getId();
    }

    CobbledMarketProviders(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
