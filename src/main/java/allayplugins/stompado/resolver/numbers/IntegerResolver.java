package allayplugins.stompado.resolver.numbers;

import allayplugins.stompado.resolver.ArgumentResolver;
import allayplugins.stompado.resolver.result.ResolveResult;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;

public class IntegerResolver implements ArgumentResolver<Integer> {

    @Override
    public Class<Integer> type() {
        return Integer.class;
    }

    @Override
    public ResolveResult<Integer> resolve(CommandSender sender, Parameter parameter, String argument) {
        if (argument == null) {
            return ResolveResult.error("Nenhum número decimal informado.");
        }

        try {
            return ResolveResult.success(Integer.parseInt(argument));
        } catch (NumberFormatException e) {
            return ResolveResult.error("Número decimal inválido: " + argument);
        }
    }
}