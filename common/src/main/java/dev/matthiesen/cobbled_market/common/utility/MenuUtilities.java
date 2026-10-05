package dev.matthiesen.cobbled_market.common.utility;

import dev.matthiesen.cobbled_market.common.config.CobbledMarketConfig;
import dev.matthiesen.cobbled_market.common.config.ServerConfig;
import dev.matthiesen.cobbled_market.common.config.defs.PurchaseOption;
import dev.matthiesen.cobbled_market.common.config.defs.ShopConfig;
import dev.matthiesen.cobbled_market.common.config.defs.ShopEntry;
import dev.matthiesen.matthiesen_core.common.utility.item.ItemBuilder;
import dev.matthiesen.matthiesen_core.common.utility.item.ItemDecoder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class MenuUtilities {
    public static final Item BACKGROUND = Items.GRAY_STAINED_GLASS_PANE;
    public static final Item PAGE_PLACEHOLDER = Items.PAPER;
    public static final Item NAV_ITEM = Items.ARROW;
    public static final Item BACK_ITEM = Items.BARRIER;
    public static final Item CURRENCY_ITEM = Items.EMERALD;

    private static ItemStack builder(Item item, Component name) {
        return new ItemBuilder(item)
                .hideAdditional()
                .setCustomName(name)
                .build();
    }

    public static ServerConfig getServerConfig() {
        return CobbledMarketConfig.SERVER_CONFIG;
    }

    public static Item getBackground() {
        return ItemDecoder.stringToItem(getServerConfig().menu_backgroundItemId.get(), BACKGROUND);
    }

    public static Item getPageIndicator() {
        return ItemDecoder.stringToItem(getServerConfig().menu_pageIndicatorItemId.get(), PAGE_PLACEHOLDER);
    }

    public static Item getNavNext() {
        return ItemDecoder.stringToItem(getServerConfig().menu_navNextItemId.get(), NAV_ITEM);
    }

    public static Item getNavPrev() {
        return ItemDecoder.stringToItem(getServerConfig().menu_navPrevItemId.get(), NAV_ITEM);
    }

    public static Item getNavBack() {
        return ItemDecoder.stringToItem(getServerConfig().menu_navBackItemId.get(), BACK_ITEM);
    }

    public static Item getCurrencyItem() {
        return ItemDecoder.stringToItem(getServerConfig().menu_currencyItemId.get(), CURRENCY_ITEM);
    }

    public static ItemStack getCurrencyItemStack(int balance) {
        String currencyText = getServerConfig().menu_currencyText.get()
                .replace("%balance%", String.valueOf(balance))
                .replace("%currency%", getServerConfig().currencyDisplayName.get());
        return builder(getCurrencyItem(), Component.literal(currencyText).withStyle(ChatFormatting.GOLD));
    }

    public static ItemStack getFrameItem() {
        return builder(getBackground(), Component.literal(" "));
    }

    public static ItemStack getPageItem(int currentPage, int pageLength) {
        String pageIndicatorText = getServerConfig().menu_pageIndicatorText.get();
        pageIndicatorText = pageIndicatorText.replace("%current%", String.valueOf(currentPage))
                                             .replace("%total%", String.valueOf(pageLength));
        return builder(getPageIndicator(), Component.literal(pageIndicatorText)
                .withStyle(ChatFormatting.GOLD));
    }

    public static ItemStack getNavNextItem(String label) {
        return builder(getNavNext(), Component.literal(label).withStyle(ChatFormatting.AQUA));
    }

    public static ItemStack getNavPrevItem(String label) {
        return builder(getNavPrev(), Component.literal(label).withStyle(ChatFormatting.AQUA));
    }

    public static ItemStack getBackToDirectoryItem() {
        return builder(getNavBack(), Component.literal(getServerConfig().menu_navBackText.get()).withStyle(ChatFormatting.RED));
    }

    public static ItemStack getBackToShopItem(String shopName) {
        String label = getServerConfig().menu_navBackToShopText.get().replace("%shop%", shopName);
        return builder(getNavBack(), Component.literal(label).withStyle(ChatFormatting.RED));
    }

    public static ItemStack getShopItem(ShopConfig.Live shop) {
        int count = shop.entries().size();

        String loreLine1 = count == 1 ? getServerConfig().menu_shopLoreLn1_single.get() : getServerConfig().menu_shopLoreLn1_multiple.get();
        String loreLine2 = getServerConfig().menu_shopLoreLn2.get();

        Component lore1 = Component.literal(count + " " + loreLine1).withStyle(ChatFormatting.GRAY);
        Component lore2 = Component.literal(loreLine2).withStyle(ChatFormatting.YELLOW);

        return new ItemBuilder(shop.shopIcon())
                .hideAdditional()
                .setCustomName(Component.literal(shop.shopName())
                        .withStyle(style -> style.withColor(ChatFormatting.GREEN).withItalic(false)))
                .addLore(new Component[]{
                        lore1,
                        lore2
                })
                .build();
    }

    public static ItemStack getShopEntryItem(ShopEntry.Live entry) {
        if (!entry.hasOptions()) {
            return getPurchaseOptionItem(entry.item(), entry.options().getFirst());
        }
        String countLore = getServerConfig().menu_shopOptionsLoreLn1.get()
                .replace("%count%", String.valueOf(entry.options().size()));
        return new ItemBuilder(entry.item())
                .hideAdditional()
                .addLore(new Component[]{
                        Component.literal(countLore).withStyle(ChatFormatting.GRAY),
                        Component.literal(getServerConfig().menu_shopOptionsLoreLn2.get()).withStyle(ChatFormatting.YELLOW)
                })
                .build();
    }

    public static ItemStack getPurchaseOptionItem(ItemStack item, PurchaseOption option) {
        String currency = CobbledMarketConfig.SERVER_CONFIG.currencyDisplayName.get();
        ItemStack stack = item.copy();
        stack.setCount(Math.clamp(option.quantity(), 1, stack.getMaxStackSize()));

        String loreLine1 = CobbledMarketConfig.SERVER_CONFIG.menu_shopItemLoreLn1.get()
                .replace("%quantity%", String.valueOf(option.quantity()));
        String loreLine2 = CobbledMarketConfig.SERVER_CONFIG.menu_shopItemLoreLn2.get()
                .replace("%price%", String.valueOf(option.price()))
                .replace("%currency%", currency);
        String loreLine3 = CobbledMarketConfig.SERVER_CONFIG.menu_shopItemLoreLn3.get();

        return new ItemBuilder(stack)
                .hideAdditional()
                .addLore(new Component[]{
                        Component.literal(loreLine1).withStyle(ChatFormatting.GRAY),
                        Component.literal(loreLine2).withStyle(ChatFormatting.GOLD),
                        Component.literal(loreLine3).withStyle(ChatFormatting.YELLOW)
                })
                .build();
    }
}
