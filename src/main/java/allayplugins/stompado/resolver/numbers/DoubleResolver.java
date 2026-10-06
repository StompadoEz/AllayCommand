package allayplugins.stompado.resolver.numbers;

import allayplugins.stompado.resolver.ArgumentResolver;
import allayplugins.stompado.resolver.result.ResolveResult;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;

public class DoubleResolver implements ArgumentResolver<Double> {

    @Override
    public Class<Double> type() {
        return Double.class;
    }

    @Override
    public ResolveResult<Double> resolve(CommandSender sender, Parameter parameter, String argument) {
        if (argument == null) {
            return ResolveResult.error("Nenhum número decimal informado.");
        }

        try {
            return ResolveResult.success(Double.parseDouble(argument));
        } catch (NumberFormatException e) {
            return ResolveResult.error("Número decimal inválido: " + argument);
        }
    }
}