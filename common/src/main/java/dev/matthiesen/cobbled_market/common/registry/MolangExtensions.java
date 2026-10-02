package dev.matthiesen.cobbled_market.common.registry;

import com.bedrockk.molang.runtime.MoParams;
import com.bedrockk.molang.runtime.value.DoubleValue;
import com.cobblemon.mod.common.api.molang.ObjectValue;
import com.cobblemon.mod.common.api.molang.function.PlayerMoLangFunctions;
import dev.matthiesen.cobbled_market.common.menu.MarketMenus;
import kotlin.jvm.functions.Function1;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class MolangExtensions {
    public static void register() {
        PlayerMoLangFunctions.INSTANCE.getCustom().add(player -> {
            Map<String, Function1<MoParams, Object>> map = new java.util.HashMap<>();

            // q.player.market()
            // q.player.market.open()
            // q.player.market.shop(<shopId>)
            map.put("market", moParams -> new PlayerMarketExt(player).asMolangValue());

            return map;
        });
    }

    public record PlayerMarketExt(Player player) {
        public String makeString(PlayerMarketExt player) {
            return "{" + "\"playerUUID\": \"" + player.player().getUUID() + "\"" + "}";
        }

        public ObjectValue<PlayerMarketExt> asMolangValue() {
            ObjectValue<PlayerMarketExt> value = new ObjectValue<>(this, this::makeString, d -> 1.0);

            value.functions.putAll(buildFunctionMap());

            return value;
        }

        public Map<String, ? extends Function<MoParams, Object>> buildFunctionMap() {
            HashMap<String, Function<MoParams, Object>> map = new HashMap<>();

            // q.player.market.open()
            map.put("open", moParams ->
                    MarketMenus.openMarket((ServerPlayer) player) ? DoubleValue.ONE : DoubleValue.ZERO);

            // q.player.market.shop(<shopId>)
            map.put("shop", moParams -> {
                String shopId = moParams.getString(0);
                return MarketMenus.openShop((ServerPlayer) player, shopId) ? DoubleValue.ONE : DoubleValue.ZERO;
            });

            return map;
        }
    }
}
