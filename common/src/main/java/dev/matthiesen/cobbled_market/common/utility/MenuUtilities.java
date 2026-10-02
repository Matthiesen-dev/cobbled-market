package dev.matthiesen.cobbled_market.common.utility;

import dev.matthiesen.cobbled_market.common.config.CobbledMarketConfig;
import dev.matthiesen.cobbled_market.common.config.defs.ShopConfig;
import dev.matthiesen.cobbled_market.common.config.defs.ShopEntry;
import dev.matthiesen.matthiesen_core.common.utility.item.ItemBuilder;
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

    private static ItemStack builder(Item item, Component name) {
        return new ItemBuilder(item)
                .hideAdditional()
                .setCustomName(name)
                .build();
    }

    public static ItemStack getFrameItem() {
        return builder(BACKGROUND, Component.literal(" "));
    }

    public static ItemStack getPageItem(int currentPage, int pageLength) {
        return builder(PAGE_PLACEHOLDER, Component.literal("Page " + currentPage + "/" + pageLength)
                .withStyle(ChatFormatting.GOLD));
    }

    public static ItemStack getNavItem(String label) {
        return builder(NAV_ITEM, Component.literal(label).withStyle(ChatFormatting.AQUA));
    }

    public static ItemStack getBackToDirectoryItem() {
        return builder(BACK_ITEM, Component.literal("Back to Market").withStyle(ChatFormatting.RED));
    }

    public static ItemStack getShopItem(ShopConfig.Live shop) {
        int count = shop.entries().size();
        return new ItemBuilder(shop.shopIcon())
                .hideAdditional()
                .setCustomName(Component.literal(shop.shopName())
                        .withStyle(style -> style.withColor(ChatFormatting.GREEN).withItalic(false)))
                .addLore(new Component[]{
                        Component.literal(count + (count == 1 ? " item" : " items")).withStyle(ChatFormatting.GRAY),
                        Component.literal("Click to browse").withStyle(ChatFormatting.YELLOW)
                })
                .build();
    }

    public static ItemStack getShopEntryItem(ShopEntry.Live entry) {
        String currency = CobbledMarketConfig.SERVER_CONFIG.currencyDisplayName.get();
        ItemStack stack = entry.item().getDefaultInstance();
        stack.setCount(Math.clamp(entry.quantity(), 1, stack.getMaxStackSize()));
        return new ItemBuilder(stack)
                .addLore(new Component[]{
                        Component.literal("Quantity: " + entry.quantity()).withStyle(ChatFormatting.GRAY),
                        Component.literal("Price: " + entry.price() + " " + currency).withStyle(ChatFormatting.GOLD),
                        Component.literal("Click to purchase").withStyle(ChatFormatting.YELLOW)
                })
                .build();
    }
}
