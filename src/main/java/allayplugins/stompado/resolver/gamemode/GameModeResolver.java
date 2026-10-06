package allayplugins.stompado.resolver.gamemode;

import allayplugins.stompado.resolver.ArgumentResolver;
import allayplugins.stompado.resolver.result.ResolveResult;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class GameModeResolver implements ArgumentResolver<GameMode> {

    private static final Map<String, GameMode> ALIASES = new HashMap<>();

    static {
        for (GameMode gm : GameMode.values()) {
            ALIASES.put(gm.name(), gm);
        }

        ALIASES.put("0", GameMode.SURVIVAL);
        ALIASES.put("1", GameMode.CREATIVE);
        ALIASES.put("2", GameMode.ADVENTURE);
        ALIASES.put("3", GameMode.SPECTATOR);

        ALIASES.put("CRIATIVO", GameMode.CREATIVE);
    }

    @Override
    public Class<GameMode> type() {
        return GameMode.class;
    }

    @Override
    public ResolveResult<GameMode> resolve(CommandSender sender, Parameter parameter, String argument) {
        if (argument == null) {
            return ResolveResult.error("Nenhum modo de jogo informado.");
        }

        String input = argument.trim().toUpperCase();

        GameMode gameMode = ALIASES.get(input);
        if (gameMode != null) {
            return ResolveResult.success(gameMode);
        }

        for (Map.Entry<String, GameMode> entry : ALIASES.entrySet()) {
            if (entry.getKey().startsWith(input)) {
                return ResolveResult.success(entry.getValue());
            }
        }

        return ResolveResult.error(
                "Modo de jogo inválido! Use: survival, creative, adventure, spectator ou 0-3."
        );
    }
}