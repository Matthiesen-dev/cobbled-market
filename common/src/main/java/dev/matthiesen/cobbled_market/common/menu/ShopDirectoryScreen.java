package dev.matthiesen.cobbled_market.common.menu;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import com.cobblemon.mod.common.CobblemonSounds;
import dev.matthiesen.cobbled_market.common.CobbledMarketCommon;
import dev.matthiesen.cobbled_market.common.config.defs.ShopConfig;
import dev.matthiesen.cobbled_market.common.utility.MenuUtilities;
import dev.matthiesen.matthiesen_core.common.utility.SoundsPlayer;
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
                .<Button>map(shop -> GooeyButton.builder()
                        .display(MenuUtilities.getShopItem(shop))
                        .onClick(action -> {
                            ServerPlayer sender = action.getPlayer();
                            new SoundsPlayer(CobblemonSounds.PC_CLICK).play(sender);
                            UIManager.openUIForcefully(sender, new ShopScreen(sender, shop, true).getPage());
                        })
                        .build())
                .toList();
    }
}
