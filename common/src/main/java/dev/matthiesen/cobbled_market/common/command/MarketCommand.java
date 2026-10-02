package dev.matthiesen.cobbled_market.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.matthiesen.cobbled_market.common.config.CobbledMarketConfig;
import dev.matthiesen.cobbled_market.common.config.defs.ShopConfig;
import dev.matthiesen.cobbled_market.common.menu.MarketMenus;
import dev.matthiesen.cobbled_market.common.registry.PermissionRegistry;
import dev.matthiesen.matthiesen_core.common.api.command.CoreCommand;
import dev.matthiesen.matthiesen_core.common.utility.commands.CommandBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Command Structure:
 * - /market            Opens the shop directory (or the only shop, if just one is configured)
 * - /market reload     Reloads the cached shop configurations
 * - /market [shopId]   Opens a specific shop
 */
public final class MarketCommand implements CoreCommand {
    public static final MarketCommand CMD = new MarketCommand();

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection context) {
        CommandBuilder reload = CommandBuilder.create("reload")
                .requires(src -> PermissionRegistry.checkPermission(src, PermissionRegistry.MARKET_RELOAD_PERMISSION))
                .executes(this::reload);

        CommandBuilder root = CommandBuilder.create("market")
                .requires(src -> PermissionRegistry.checkPermission(src, PermissionRegistry.MARKET_PERMISSION))
                .executes(this::openMarket)
                .then(reload)
                .argument("shopId", StringArgumentType.word(), shopId -> shopId
                        .requires(src -> PermissionRegistry.checkPermission(src, PermissionRegistry.MARKET_SHOP_PERMISSION))
                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                CobbledMarketConfig.getShopConfigs().stream().map(ShopConfig.Live::shopId),
                                builder))
                        .executes(this::openShop)
                );

        dispatcher.register(root.build());
    }

    private int openMarket(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        return MarketMenus.openMarket(player) ? 1 : 0;
    }

    private int openShop(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String shopId = StringArgumentType.getString(context, "shopId");
        return MarketMenus.openShop(player, shopId, false) ? 1 : 0;
    }

    private int reload(CommandContext<CommandSourceStack> context) {
        CobbledMarketConfig.reloadCachedShopConfigs();
        int count = CobbledMarketConfig.getShopConfigs().size();
        context.getSource().sendSuccess(() -> Component.literal("Reloaded " + count + " shop(s)."), true);
        return 1;
    }
}
