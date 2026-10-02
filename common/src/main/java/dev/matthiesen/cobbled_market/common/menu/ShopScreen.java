package dev.matthiesen.cobbled_market.common.menu;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import com.cobblemon.mod.common.CobblemonSounds;
import dev.matthiesen.cobbled_market.common.config.CobbledMarketConfig;
import dev.matthiesen.cobbled_market.common.config.defs.ShopConfig;
import dev.matthiesen.cobbled_market.common.utility.MenuUtilities;
import dev.matthiesen.matthiesen_core.common.utility.SoundsPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
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
                .<Button>map(entry -> GooeyButton.builder()
                        .display(MenuUtilities.getShopEntryItem(entry))
                        .onClick(action -> {
                            ServerPlayer sender = action.getPlayer();
                            boolean success = entry.purchase(sender);
                            new SoundsPlayer(success ? SoundEvents.EXPERIENCE_ORB_PICKUP : SoundEvents.VILLAGER_NO)
                                    .play(sender);
                        })
                        .build())
                .toList();
    }

    @Override
    protected @Nullable Button getBackButton() {
        if (!showBackButton) {
            return null;
        }
        return GooeyButton.builder()
                .display(MenuUtilities.getBackToDirectoryItem())
                .onClick(action -> {
                    ServerPlayer sender = action.getPlayer();
                    new SoundsPlayer(CobblemonSounds.PC_CLICK).play(sender);
                    UIManager.openUIForcefully(sender,
                            new ShopDirectoryScreen(sender, CobbledMarketConfig.getShopConfigs()).getPage());
                })
                .build();
    }
}
