package dev.matthiesen.cobbled_market.common.menu;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.Page;
import com.cobblemon.mod.common.CobblemonSounds;
import dev.matthiesen.cobbled_market.common.config.CobbledMarketConfig;
import dev.matthiesen.cobbled_market.common.config.defs.ShopEntry;
import dev.matthiesen.cobbled_market.common.utility.MenuUtilities;
import dev.matthiesen.matthiesen_core.common.utility.SoundsPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public final class PurchaseOptionsScreen extends PaginatedScreen {
    private final ShopEntry.Live entry;
    private final Page shopPage;
    private final String shopName;

    public PurchaseOptionsScreen(ServerPlayer player, ShopEntry.Live entry, Page shopPage, String shopName) {
        super(player);
        this.entry = entry;
        this.shopPage = shopPage;
        this.shopName = shopName;
    }

    @Override
    public Component getDisplayTitle() {
        String title = CobbledMarketConfig.SERVER_CONFIG.menu_purchaseOptionsTitle.get()
                .replace("%item%", entry.item().getHoverName().getString())
                .replace("%shop%", shopName);
        return Component.literal(title);
    }

    @Override
    public List<Button> getContentButtons() {
        return entry.options().stream().map(entry::toPurchaseButton).toList();
    }

    @Override
    protected Button getBackButton() {
        return GooeyButton.builder()
                .display(MenuUtilities.getBackToShopItem(shopName))
                .onClick(action -> {
                    new SoundsPlayer(CobblemonSounds.PC_CLICK).play(action.getPlayer());
                    UIManager.openUIForcefully(action.getPlayer(), shopPage);
                })
                .build();
    }
}
