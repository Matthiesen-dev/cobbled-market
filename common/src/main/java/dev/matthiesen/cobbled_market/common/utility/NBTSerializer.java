package dev.matthiesen.cobbled_market.common.utility;

import com.mojang.serialization.DataResult;
import dev.matthiesen.cobbled_market.common.CobbledMarketCommon;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

public final class NBTSerializer {
    public static final String FALLBACK_ITEM = "minecraft:air";

    private static HolderLookup.Provider getRegistries() {
        return CobbledMarketCommon.INSTANCE.getCommonUtils().getServer().registryAccess();
    }

    public static String serialize(ItemStack stack) {
        HolderLookup.Provider registries = getRegistries();
        if (stack.isEmpty()) {
            return FALLBACK_ITEM;
        }
        DataResult<Tag> result = ItemStack.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), stack);
        return result.map(Tag::toString).result().orElse(FALLBACK_ITEM);
    }

    public static ItemStack stringToItemStack(String rawStack) {
        HolderLookup.Provider registries = getRegistries();
        try {
            Tag tag = TagParser.parseTag(rawStack);
            return ItemStack.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag)
                    .result()
                    .orElse(ItemStack.EMPTY);
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }
}
