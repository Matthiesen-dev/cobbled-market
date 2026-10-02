package dev.matthiesen.cobbled_market.common.registry;

import dev.matthiesen.cobbled_market.common.CobbledMarketCommon;
import dev.matthiesen.cobbled_market.common.config.CobbledMarketConfig;
import dev.matthiesen.matthiesen_core.common.api.permissions.Permission;
import dev.matthiesen.matthiesen_core.common.api.permissions.PermissionLevel;
import dev.matthiesen.matthiesen_core.common.utility.AbstractPermission;
import net.minecraft.commands.CommandSourceStack;

public final class PermissionRegistry {
    public static final Permission MARKET_PERMISSION = register("command.market",
            CobbledMarketConfig.PERMISSIONS_CONFIG.permission_market.get());
    public static final Permission MARKET_SHOP_PERMISSION = register("command.market.shop",
            CobbledMarketConfig.PERMISSIONS_CONFIG.permission_market_shop.get());
    public static final Permission MARKET_RELOAD_PERMISSION = register("command.market.reload",
            CobbledMarketConfig.PERMISSIONS_CONFIG.permission_market_reload.get());

    public static void init() {}

    public static boolean checkPermission(CommandSourceStack source, Permission permission) {
        return CobbledMarketCommon.INSTANCE.getPermissionsManager().getPermissionValidator().hasPermission(source, permission);
    }

    private static Permission register(String node, PermissionLevel level) {
        Permission permission = new AbstractPermission(node, level) {
            @Override
            protected String getModId() {
                return CobbledMarketCommon.MOD_ID;
            }

            @Override
            protected String getPermissionNamespace() {
                return "CobbledMarket";
            }
        };
        CobbledMarketCommon.INSTANCE.getPermissionsManager().registerPermission(permission);
        return permission;
    }
}
