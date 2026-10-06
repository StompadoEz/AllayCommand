package allayplugins.stompado.resolver.target;

import allayplugins.stompado.resolver.ArgumentResolver;
import allayplugins.stompado.resolver.result.ResolveResult;
import allayplugins.stompado.target.Target;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Parameter;

public class TargetResolver implements ArgumentResolver<Target> {

    @Override
    public Class<Target> type() {
        return Target.class;
    }

    @Override
    public ResolveResult<Target> resolve(CommandSender sender, Parameter parameter, String argument) {
        if (argument == null) {

            if (!(sender instanceof Player)) {
                return ResolveResult.error(
                        "Este comando deve ser executado por um jogador."
                );
            }

            return ResolveResult.success(
                    new Target((Player) sender)
            );
        }

        Player player = Bukkit.getPlayer(argument);

        if (player == null) {
            return ResolveResult.error(
                    "Jogador não encontrado: " + argument
            );
        }

        return ResolveResult.success(
                new Target(player)
        );
    }
}