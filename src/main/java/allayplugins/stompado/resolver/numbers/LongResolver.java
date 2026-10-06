package allayplugins.stompado.resolver.numbers;

import allayplugins.stompado.resolver.ArgumentResolver;
import allayplugins.stompado.resolver.result.ResolveResult;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;

public class LongResolver implements ArgumentResolver<Long> {

    @Override
    public Class<Long> type() {
        return Long.class;
    }

    @Override
    public ResolveResult<Long> resolve(CommandSender sender, Parameter parameter, String argument) {
        if (argument == null) {
            return ResolveResult.error("Nenhum número decimal informado.");
        }

        try {
            return ResolveResult.success(Long.parseLong(argument));
        } catch (NumberFormatException e) {
            return ResolveResult.error("Número decimal inválido: " + argument);
        }
    }
}
