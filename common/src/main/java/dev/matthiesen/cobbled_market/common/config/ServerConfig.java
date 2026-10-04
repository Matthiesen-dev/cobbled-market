package dev.matthiesen.cobbled_market.common.config;

import com.electronwill.nightconfig.core.Config;
import dev.matthiesen.cobbled_market.common.config.defs.ShopConfig;
import dev.matthiesen.cobbled_market.common.economy_providers.CobbledMarketProviders;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class ServerConfig {
    public ModConfigSpec.EnumValue<CobbledMarketProviders> currencyProvider;
    public ModConfigSpec.ConfigValue<String> currencyId;
    public ModConfigSpec.ConfigValue<String> currencyDisplayName;
    public ModConfigSpec.ConfigValue<List<? extends Config>> shops;

    public ModConfigSpec.ConfigValue<String> menu_backgroundItemId;
    public ModConfigSpec.ConfigValue<String> menu_pageIndicatorItemId;
    public ModConfigSpec.ConfigValue<String> menu_navNextItemId;
    public ModConfigSpec.ConfigValue<String> menu_navPrevItemId;
    public ModConfigSpec.ConfigValue<String> menu_navBackItemId;
    public ModConfigSpec.ConfigValue<String> menu_pageIndicatorText;
    public ModConfigSpec.ConfigValue<String> menu_navBackText;
    public ModConfigSpec.ConfigValue<String> menu_navPrevText;
    public ModConfigSpec.ConfigValue<String> menu_navNextText;
    public ModConfigSpec.ConfigValue<String> menu_shopLoreLn1_single;
    public ModConfigSpec.ConfigValue<String> menu_shopLoreLn1_multiple;
    public ModConfigSpec.ConfigValue<String> menu_shopLoreLn2;
    public ModConfigSpec.ConfigValue<String> menu_shopItemLoreLn1;
    public ModConfigSpec.ConfigValue<String> menu_shopItemLoreLn2;
    public ModConfigSpec.ConfigValue<String> menu_shopItemLoreLn3;
    public ModConfigSpec.ConfigValue<String> menu_shopOptionsLoreLn1;
    public ModConfigSpec.ConfigValue<String> menu_shopOptionsLoreLn2;
    public ModConfigSpec.ConfigValue<String> menu_purchaseOptionsTitle;
    public ModConfigSpec.ConfigValue<String> menu_navBackToShopText;
    public ModConfigSpec.ConfigValue<String> menu_errorNoShopsConfigured;
    public ModConfigSpec.ConfigValue<String> menu_errorUnknownShop;

    public ModConfigSpec.ConfigValue<String> messages_notEnoughFunds;
    public ModConfigSpec.ConfigValue<String> messages_withdrawError;
    public ModConfigSpec.ConfigValue<String> messages_purchaseSuccess;
    public ModConfigSpec.ConfigValue<String> messages_purchaseFailure;
    public ModConfigSpec.ConfigValue<String> messages_reloadConfig;

    public ServerConfig(ModConfigSpec.Builder builder) {
        builder.comment("Cobbled Market Configuration").push("server");

        currencyProvider = builder.comment("Currency provider for the market")
                .defineEnum("currencyProvider", CobbledMarketProviders.IMPACTOR);
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

        builder.comment("Menu configuration").push("menu");

        menu_backgroundItemId = builder.comment("Menu background item ID")
                .define("backgroundItemId", "minecraft:gray_stained_glass_pane");
        menu_pageIndicatorItemId = builder.comment("Menu page indicator item ID")
                .define("pageIndicatorItemId", "minecraft:paper");
        menu_navNextItemId = builder.comment("Menu navigation next item ID")
                .define("navNextItemId", "minecraft:arrow");
        menu_navPrevItemId = builder.comment("Menu navigation previous item ID")
                .define("navPrevItemId", "minecraft:arrow");
        menu_navBackItemId = builder.comment("Menu navigation back item ID")
                .define("navBackItemId", "minecraft:barrier");
        menu_pageIndicatorText = builder.comment("Menu page indicator text")
                .define("pageIndicatorText", "Page %current%/%total%");
        menu_navBackText = builder.comment("Menu navigation back text")
                .define("navBackText", "Back to Market");
        menu_navPrevText = builder.comment("Menu navigation previous text")
                .define("navPrevText", "Previous");
        menu_navNextText = builder.comment("Menu navigation next text")
                .define("navNextText", "Next");
        menu_shopLoreLn1_single = builder.comment("Menu shop lore line 1 for single item entries")
                .define("shopLoreLn1_single", "item");
        menu_shopLoreLn1_multiple = builder.comment("Menu shop lore line 1 for multiple item entries")
                .define("shopLoreLn1_multiple", "items");
        menu_shopLoreLn2 = builder.comment("Menu shop lore line 2")
                .define("shopLoreLn2", "Click to browse");
        menu_shopItemLoreLn1 = builder.comment("Menu shop item lore line 1")
                .define("shopItemLoreLn1", "Quantity: %quantity%");
        menu_shopItemLoreLn2 = builder.comment("Menu shop item lore line 2")
                .define("shopItemLoreLn2", "Price: %price% %currency%");
        menu_shopItemLoreLn3 = builder.comment("Menu shop item lore line 3")
                .define("shopItemLoreLn3", "Click to purchase");
        menu_shopOptionsLoreLn1 = builder.comment("Option-based entry lore; %count% is the number of purchase options")
                .define("shopOptionsLoreLn1", "Purchase options: %count%");
        menu_shopOptionsLoreLn2 = builder.comment("Option-based entry browse prompt")
                .define("shopOptionsLoreLn2", "Click to choose quantity");
        menu_purchaseOptionsTitle = builder.comment("Purchase options title; supports %item% and %shop%")
                .define("purchaseOptionsTitle", "%item% - Purchase Options");
        menu_navBackToShopText = builder.comment("Back to shop button text; supports %shop%")
                .define("navBackToShopText", "Back to %shop%");
        menu_errorNoShopsConfigured = builder.comment("Menu error message for no shops configured")
                .define("errorNoShopsConfigured", "There are no shops configured.");
        menu_errorUnknownShop = builder.comment("Menu error message for unknown shop")
                .define("errorUnknownShop", "Unknown shop: %shopId%");

        builder.pop(); // End of menu configuration

        builder.comment("Messages configuration").push("messages");

        messages_notEnoughFunds = builder.comment("Message for not enough funds")
                .define("notEnoughFunds", "You do not have enough %s to purchase this item. You need %d %s.");
        messages_withdrawError = builder.comment("Message for withdraw error")
                .define("withdrawError", "An error occurred while processing your purchase. Please try again later.");
        messages_purchaseSuccess = builder.comment("Message for successful purchase")
                .define("purchaseSuccess", "You have purchased %d x %s for %d %s.");
        messages_purchaseFailure = builder.comment("Message for failed purchase")
                .define("purchaseFailure", "An error occurred while processing the purchase. Please try again later.");
        messages_reloadConfig = builder.comment("Message for config reload")
                .define("reloadConfig", "Cobbled Market configuration reloaded successfully. Reloaded %d shops.");

        builder.pop(); // End of messages configuration

        builder.pop(); // End of server configuration
    }
}
