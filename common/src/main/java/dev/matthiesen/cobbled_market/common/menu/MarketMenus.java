package dev.matthiesen.cobbled_market.common.menu;

import ca.landonjw.gooeylibs2.api.UIManager;
import dev.matthiesen.cobbled_market.common.config.CobbledMarketConfig;
import dev.matthiesen.cobbled_market.common.config.defs.ShopConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Entry points for opening market menus generated from the current shop configuration.
 */
public final class MarketMenus {
    private MarketMenus() {}

    /**
     * Opens the shop directory, or the shop itself when exactly one shop is configured.
     */
    public static boolean openMarket(ServerPlayer player) {
        List<ShopConfig.Live> shops = CobbledMarketConfig.getShopConfigs();
        if (shops.isEmpty()) {
            player.sendSystemMessage(Component.literal("There are no shops configured.").withStyle(ChatFormatting.RED));
            return false;
        }
        if (shops.size() == 1) {
            UIManager.openUIForcefully(player, new ShopScreen(player, shops.getFirst(), false).getPage());
        } else {
            UIManager.openUIForcefully(player, new ShopDirectoryScreen(player, shops).getPage());
        }
        return true;
    }

    public static boolean openShop(ServerPlayer player, String shopId) {
        ShopConfig.Live shop = CobbledMarketConfig.getShop(shopId);
        if (shop == null) {
            player.sendSystemMessage(Component.literal("Unknown shop: " + shopId).withStyle(ChatFormatting.RED));
            return false;
        }
        boolean showBack = CobbledMarketConfig.getShopConfigs().size() > 1;
        UIManager.openUIForcefully(player, new ShopScreen(player, shop, showBack).getPage());
        return true;
    }
}
