package dev.matthiesen.cobbled_market.common.config;

import dev.matthiesen.matthiesen_core.common.api.permissions.PermissionLevel;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class PermissionsConfig {
    public final ModConfigSpec.EnumValue<PermissionLevel> permission_market;
    public final ModConfigSpec.EnumValue<PermissionLevel> permission_market_shop;
    public final ModConfigSpec.EnumValue<PermissionLevel> permission_market_reload;
    public final ModConfigSpec.EnumValue<PermissionLevel> permission_market_debug;

    public PermissionsConfig(ModConfigSpec.Builder builder) {
        builder.comment("Permission Levels for Cobbled Market").push("permissions");

        builder.comment("Command Permissions").push("command");
        permission_market = builder.comment("Permission Level for /market (shop directory)")
                .defineEnum("market", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        permission_market_shop = builder.comment("Permission Level for /market <shopId>")
                .defineEnum("market_shop", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        permission_market_reload = builder.comment("Permission Level for /market reload")
                .defineEnum("market_reload", PermissionLevel.ALL_COMMANDS);
        permission_market_debug = builder.comment("Permission Level for /market debug")
                .defineEnum("market_debug", PermissionLevel.ALL_COMMANDS);

        builder.pop();
    }
}
