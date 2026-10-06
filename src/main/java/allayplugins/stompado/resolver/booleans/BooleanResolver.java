package allayplugins.stompado.resolver.booleans;

import allayplugins.stompado.resolver.ArgumentResolver;
import allayplugins.stompado.resolver.result.ResolveResult;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

public class BooleanResolver implements ArgumentResolver<Boolean> {

    private final List<String> trueValues = Arrays.asList("true", "yes", "on", "sim", "1");
    private final List<String> falseValues = Arrays.asList("false", "no", "off", "nao", "não", "0");


    @Override
    public Class<Boolean> type() {
        return Boolean.class;
    }

    @Override
    public ResolveResult<Boolean> resolve(CommandSender sender, Parameter parameter, String argument) {
        if (argument == null) {
            return ResolveResult.error("Valor booleano não informado.");
        }

        String lower = argument.toLowerCase();

        if (trueValues.contains(lower)) {
            return ResolveResult.success(true);
        }

        if (falseValues.contains(lower)) {
            return ResolveResult.success(false);
        }

        return ResolveResult.error(
                "Valor booleano inválido: " + argument
        );
    }
}