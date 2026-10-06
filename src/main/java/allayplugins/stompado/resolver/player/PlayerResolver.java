package allayplugins.stompado.resolver.player;

import allayplugins.stompado.resolver.ArgumentResolver;
import allayplugins.stompado.resolver.result.ResolveResult;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Parameter;

public class PlayerResolver implements ArgumentResolver<Player> {

    @Override
    public Class<Player> type() {
        return Player.class;
    }

    @Override
    public ResolveResult<Player> resolve(
            CommandSender sender,
            Parameter parameter,
            String argument
    ) {
        if (argument == null) {

            if (!(sender instanceof Player)) {
                return ResolveResult.error(
                        "Este comando deve ser executado por um jogador."
                );
            }

            return ResolveResult.success((Player) sender);
        }

        Player player = Bukkit.getPlayer(argument);

        if (player == null) {
            return ResolveResult.error(
                    "Jogador não encontrado: " + argument
            );
        }

        return ResolveResult.success(player);
    }
}