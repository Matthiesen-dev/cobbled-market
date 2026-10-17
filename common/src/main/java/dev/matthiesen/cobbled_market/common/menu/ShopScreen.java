package dev.matthiesen.cobbled_market.common.menu;

import com.cobblemon.mod.common.CobblemonSounds;
import dev.matthiesen.cobbled_market.common.config.CobbledMarketConfig;
import dev.matthiesen.cobbled_market.common.config.defs.*;
import dev.matthiesen.cobbled_market.common.utility.MenuUtilities;
import dev.matthiesen.matthiesen_core.common.api.server_menu.ServerUIManager;
import dev.matthiesen.matthiesen_core.common.api.server_menu.button.*;
import dev.matthiesen.matthiesen_core.common.utility.SoundsPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ShopScreen extends PaginatedScreen {
    private final ShopConfig.Live shop;
    private final boolean showBackButton;

    public ShopScreen(ServerPlayer player, ShopConfig.Live shop, boolean showBackButton) {
        super(player);
        this.shop = shop;
        this.showBackButton = showBackButton;
    }

    @Override
    public Component getDisplayTitle() {
        return Component.literal(shop.shopName());
    }

    @Override
    public List<Button> getContentButtons() {
        return shop.entries().stream()
                .map(this::getEntryButton)
                .toList();
    }

    private Button getEntryButton(ShopEntry.Live entry) {
        if (!entry.hasOptions()) {
            return entry.toPurchaseButton(entry.options().getFirst());
        }
        return entry.toPurchaseOptionButton(shop);
    }

    @Override
    protected @Nullable Button getBackButton() {
        if (!showBackButton) {
            return null;
        }
        return ServerButton.builder()
                .display(MenuUtilities.getBackToDirectoryItem())
                .onClick(action -> {
                    ServerPlayer sender = action.getPlayer();
                    new SoundsPlayer(CobblemonSounds.PC_CLICK).play(sender);
                    ServerUIManager.openUIForcefully(sender,
                            new ShopDirectoryScreen(sender, CobbledMarketConfig.getShopConfigs()).getPage());
                })
                .build();
    }
}
