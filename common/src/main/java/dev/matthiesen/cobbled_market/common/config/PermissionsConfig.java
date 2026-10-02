package dev.matthiesen.cobbled_market.common.config;

import dev.matthiesen.matthiesen_core.common.api.permissions.PermissionLevel;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class PermissionsConfig {
    public final ModConfigSpec.EnumValue<PermissionLevel> permission_market;
    public final ModConfigSpec.EnumValue<PermissionLevel> permission_market_shop;
    public final ModConfigSpec.EnumValue<PermissionLevel> permission_market_reload;

    public PermissionsConfig(ModConfigSpec.Builder builder) {
        builder.comment("Permission Levels for Commands").push("permissions");

        permission_market = builder.comment("Permission Level for /market (shop directory)")
                .defineEnum("command.market", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        permission_market_shop = builder.comment("Permission Level for /market <shopId>")
                .defineEnum("command.market.shop", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        permission_market_reload = builder.comment("Permission Level for /market reload")
                .defineEnum("command.market.reload", PermissionLevel.ALL_COMMANDS);

        builder.pop();
    }
}
