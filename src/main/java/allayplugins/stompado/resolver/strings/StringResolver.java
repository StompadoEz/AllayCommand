package allayplugins.stompado.resolver.strings;

import allayplugins.stompado.resolver.ArgumentResolver;
import allayplugins.stompado.resolver.result.ResolveResult;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;

public class StringResolver implements ArgumentResolver<String> {

    @Override
    public Class<String> type() {
        return String.class;
    }

    @Override
    public ResolveResult<String> resolve(CommandSender sender, Parameter parameter, String argument) {
        return argument == null
                ? ResolveResult.error("Nenhum texto informado.")
                : ResolveResult.success(argument);
    }
}