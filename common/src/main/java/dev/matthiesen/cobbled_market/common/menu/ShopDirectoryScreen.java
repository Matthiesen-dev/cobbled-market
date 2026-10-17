package dev.matthiesen.cobbled_market.common.menu;

import dev.matthiesen.cobbled_market.common.CobbledMarketCommon;
import dev.matthiesen.cobbled_market.common.config.defs.ShopConfig;
import dev.matthiesen.matthiesen_core.common.api.server_menu.button.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public final class ShopDirectoryScreen extends PaginatedScreen {
    private final List<ShopConfig.Live> shops;

    public ShopDirectoryScreen(ServerPlayer player, List<ShopConfig.Live> shops) {
        super(player);
        this.shops = shops;
    }

    @Override
    public Component getDisplayTitle() {
        return Component.literal(CobbledMarketCommon.MOD_NAME);
    }

    @Override
    public List<Button> getContentButtons() {
        return shops.stream()
                .map(ShopConfig.Live::toShopButton)
                .toList();
    }
}
