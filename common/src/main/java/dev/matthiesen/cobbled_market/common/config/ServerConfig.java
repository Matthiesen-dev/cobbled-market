package dev.matthiesen.cobbled_market.common.config;

import com.electronwill.nightconfig.core.Config;
import dev.matthiesen.cobbled_market.common.config.defs.ShopConfig;
import dev.matthiesen.matthiesen_core.common.api.economy.BuiltInEconomyProviders;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class ServerConfig {
    public ModConfigSpec.EnumValue<BuiltInEconomyProviders> currencyProvider;
    public ModConfigSpec.ConfigValue<String> currencyId;
    public ModConfigSpec.ConfigValue<String> currencyDisplayName;

    public ModConfigSpec.ConfigValue<List<? extends Config>> shops;

    public ServerConfig(ModConfigSpec.Builder builder) {
        builder.comment("Cobbled Market Configuration").push("server");

        currencyProvider = builder.comment("Currency provider for the market")
                .defineEnum("currencyProvider", BuiltInEconomyProviders.IMPACTOR);
        currencyId = builder.comment("Currency ID for the market")
                .define("currencyId", "impactor:dollars");
        currencyDisplayName = builder.comment("Currency display name for the market")
                .define("currencyDisplayName", "Dollars");

        shops = builder.comment("List of shop configurations")
                .defineListAllowEmpty(
                        List.of("shops"),
                        ShopConfig.DEFAULT_ENTRIES,
                        null,
                        obj -> obj instanceof Config config && ShopConfig.isValid(config)
                );

        builder.pop(); // End of server configuration
    }
}
