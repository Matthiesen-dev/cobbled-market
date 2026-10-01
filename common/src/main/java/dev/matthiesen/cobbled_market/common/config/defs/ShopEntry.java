package dev.matthiesen.cobbled_market.common.config.defs;

import com.electronwill.nightconfig.core.Config;
import dev.matthiesen.matthiesen_core.common.utility.item.ItemDecoder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public record ShopEntry(String itemId, int quantity, int price) {
    public static Item parseItem(String itemRaw) {
        Item item = ItemDecoder.stringToItem(itemRaw, Items.AIR);
        if (item == null) {
            return Items.AIR;
        }
        return item;
    }

    public static ShopEntry deserialize(Config config) {
        String itemRaw = config.get("itemId");
        int quantity = config.get("quantity");
        int price = config.get("price");

        Item item = parseItem(itemRaw);
        if (item == Items.AIR) {
            throw new IllegalArgumentException("Invalid item: " + itemRaw);
        }

        return new ShopEntry(itemRaw, quantity, price);
    }

    public static boolean isValid(Config config) {
        String itemRaw = config.get("itemId");
        int quantity = config.get("quantity");
        int price = config.get("price");

        if (itemRaw == null || itemRaw.isEmpty()) {
            return false;
        }

        return quantity > 0 && price >= 0;
    }

    public Config serialize() {
        Config config = Config.inMemory();
        config.set("itemId", this.itemId);
        config.set("quantity", this.quantity);
        config.set("price", this.price);
        return config;
    }

    public LiveShopEntry getLiveShopEntry() {
        return new LiveShopEntry(ItemDecoder.stringToItem(this.itemId, Items.AIR), this.quantity, this.price);
    }

    public record LiveShopEntry(Item item, int quantity, int price) {
        public boolean purchase(ServerPlayer player) {
            return false; // TODO: Implement purchase logic
        }
    }
}
