package dev.matthiesen.cobbled_market.common.utility;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.matthiesen.cobbled_market.common.CobbledMarketCommon;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

/**
 * Converts between {@link ItemStack}s and item strings using the vanilla {@code /give} syntax,
 * e.g. {@code cobblemon:poke_ball} or {@code cobblemon:poke_ball[custom_name='"Foo"']}.
 * Legacy SNBT compounds (e.g. {@code {id:"minecraft:stone",count:1}}) are still accepted when parsing.
 */
public final class NBTSerializer {
    public static final String FALLBACK_ITEM = "minecraft:air";

    private static HolderLookup.Provider getRegistries() {
        return CobbledMarketCommon.INSTANCE.getCommonUtils().getServer().registryAccess();
    }

    public static String serialize(ItemStack stack) {
        if (stack.isEmpty()) {
            return FALLBACK_ITEM;
        }
        return new ItemInput(stack.getItemHolder(), stack.getComponentsPatch()).serialize(getRegistries());
    }

    public static ItemStack stringToItemStack(String rawStack) {
        if (rawStack == null || rawStack.isBlank()) {
            return ItemStack.EMPTY;
        }
        String input = rawStack.trim();
        HolderLookup.Provider registries = getRegistries();
        try {
            return input.startsWith("{") ? parseSnbt(input, registries) : parseItemString(input, registries);
        } catch (Exception e) {
            CobbledMarketCommon.INSTANCE.createWarnLog("Failed to parse item '" + rawStack + "': " + e.getMessage());
            return ItemStack.EMPTY;
        }
    }

    private static ItemStack parseItemString(String input, HolderLookup.Provider registries) throws CommandSyntaxException {
        StringReader reader = new StringReader(input);
        ItemParser.ItemResult result = new ItemParser(registries).parse(reader);
        if (reader.canRead()) {
            throw new IllegalArgumentException("Unexpected trailing characters at position " + reader.getCursor());
        }
        return new ItemStack(result.item(), 1, result.components());
    }

    private static ItemStack parseSnbt(String input, HolderLookup.Provider registries) throws CommandSyntaxException {
        Tag tag = TagParser.parseTag(input);
        return ItemStack.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag)
                .getOrThrow(IllegalArgumentException::new);
    }
}
