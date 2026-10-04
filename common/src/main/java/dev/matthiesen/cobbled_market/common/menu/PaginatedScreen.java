package dev.matthiesen.cobbled_market.common.menu;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.button.linked.LinkType;
import ca.landonjw.gooeylibs2.api.button.linked.LinkedPageButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.slot.TemplateSlotDelegate;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.CobblemonSounds;
import dev.matthiesen.cobbled_market.common.CobbledMarketCommon;
import dev.matthiesen.cobbled_market.common.config.CobbledMarketConfig;
import dev.matthiesen.cobbled_market.common.config.ServerConfig;
import dev.matthiesen.cobbled_market.common.utility.MenuUtilities;
import dev.matthiesen.matthiesen_core.common.utility.SoundsPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Six-row chest menu with the top five rows used for content and the bottom row used for navigation.
 */
public abstract class PaginatedScreen {
    private static final int PREVIOUS_SLOT = 45;
    private static final int BACK_SLOT = 47;
    private static final int INFO_SLOT = 49;
    private static final int BALANCE_SLOT = 51;
    private static final int NEXT_SLOT = 53;

    protected final ServerPlayer player;

    protected PaginatedScreen(ServerPlayer player) {
        this.player = player;
    }

    public abstract Component getDisplayTitle();

    public abstract List<Button> getContentButtons();

    /**
     * @return a button placed in the bottom-left of the navigation bar, or null for none.
     */
    protected @Nullable Button getBackButton() {
        return null;
    }

    private Button getPlayerBalanceButton() {
        var config = CobbledMarketConfig.SERVER_CONFIG;
        var ecoProvider = CobbledMarketCommon.INSTANCE.getEconomyManager().getEconomyProvider(config.currencyProvider.get().getId());

        ItemStack balanceItem;
        if (ecoProvider == null) {
            balanceItem = MenuUtilities.getCurrencyItemStack(0);
        } else {
            int balance = ecoProvider.getBalance(player, config.currencyId.get());
            balanceItem = MenuUtilities.getCurrencyItemStack(balance);
        }
        return GooeyButton.builder()
                .display(balanceItem)
                .build();
    }

    public Page getPage() {
        Button frame = GooeyButton.builder()
                .display(MenuUtilities.getFrameItem())
                .build();

        ServerConfig config = CobbledMarketConfig.SERVER_CONFIG;

        LinkedPageButton previous = LinkedPageButton.builder()
                .display(MenuUtilities.getNavPrevItem(config.menu_navPrevText.get()))
                .linkType(LinkType.Previous)
                .onClick(action -> new SoundsPlayer(CobblemonSounds.PC_CLICK).play(action.getPlayer()))
                .build();

        LinkedPageButton next = LinkedPageButton.builder()
                .display(MenuUtilities.getNavNextItem(config.menu_navNextText.get()))
                .linkType(LinkType.Next)
                .onClick(action -> new SoundsPlayer(CobblemonSounds.PC_CLICK).play(action.getPlayer()))
                .build();

        ChestTemplate.Builder templateBuilder = ChestTemplate.builder(6)
                .rectangle(0, 0, 5, 9, new PlaceholderButton())
                .set(PREVIOUS_SLOT, previous)
                .set(INFO_SLOT, getInfoButton(1, 1))
                .set(BALANCE_SLOT, getPlayerBalanceButton())
                .set(NEXT_SLOT, next);

        Button back = getBackButton();
        if (back != null) {
            templateBuilder.set(BACK_SLOT, back);
        }

        ChestTemplate template = templateBuilder.fill(frame).build();

        LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, getContentButtons(), null);
        decoratePages(page);
        return page;
    }

    private Button getInfoButton(int currentPage, int totalPages) {
        return GooeyButton.builder()
                .display(MenuUtilities.getPageItem(currentPage, totalPages))
                .build();
    }

    private void decoratePages(LinkedPage first) {
        int totalPages = Math.max(1, first.getTotalPages());
        Component title = getDisplayTitle();
        for (LinkedPage page = first; page != null; page = page.getNext()) {
            page.setTitle(title);
            page.getTemplate().setSlot(INFO_SLOT,
                    new TemplateSlotDelegate(getInfoButton(Math.max(1, page.getCurrentPage()), totalPages), INFO_SLOT));
        }
    }
}
