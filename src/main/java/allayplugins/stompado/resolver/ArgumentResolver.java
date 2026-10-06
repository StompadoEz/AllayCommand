package allayplugins.stompado.resolver;

import allayplugins.stompado.resolver.result.ResolveResult;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;

public interface ArgumentResolver<T> {

    Class<T> type();

    ResolveResult<T> resolve(
            CommandSender sender,
            Parameter parameter,
            String arg
    );

}