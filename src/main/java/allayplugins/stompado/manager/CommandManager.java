package allayplugins.stompado.manager;

import allayplugins.stompado.annotations.Default;
import allayplugins.stompado.annotations.optional.Optional;
import allayplugins.stompado.annotations.string.Remaining;
import allayplugins.stompado.command.CommandContext;
import allayplugins.stompado.command.CommandMatch;
import allayplugins.stompado.handler.DynamicCommand;
import allayplugins.stompado.method.CommandMethod;
import allayplugins.stompado.registry.ResolverRegistry;
import allayplugins.stompado.resolver.ArgumentResolver;
import allayplugins.stompado.resolver.enchant.EnchantResolver;
import allayplugins.stompado.resolver.gamemode.GameModeResolver;
import allayplugins.stompado.resolver.numbers.DoubleResolver;
import allayplugins.stompado.resolver.numbers.IntegerResolver;
import allayplugins.stompado.resolver.numbers.LongResolver;
import allayplugins.stompado.resolver.target.TargetResolver;
import allayplugins.stompado.resolver.result.ResolveResult;
import allayplugins.stompado.resolver.strings.StringResolver;
import allayplugins.stompado.target.Target;
import allayplugins.stompado.text.Messages;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class CommandManager {

    private final JavaPlugin plugin;
    private final Map<String, List<CommandMethod>> commandsRegister = new HashMap<>();
    private final ResolverRegistry resolverRegistry;

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;

        resolverRegistry = new ResolverRegistry();

        registerDefaultResolvers();
    }

    public void registerCommands(Object... objects) {
        for (Object obj : objects) {
            for (Method method : obj.getClass().getDeclaredMethods()) {
                if (!method.isAnnotationPresent(CommandContext.class)) continue;

                CommandContext annotation = method.getAnnotation(CommandContext.class);
                String name = annotation.name().toLowerCase().trim();

                CommandMethod commandMethod = new CommandMethod(obj, method, annotation);
                commandsRegister.computeIfAbsent(name, k -> new ArrayList<>()).add(commandMethod);

                for (String alias : annotation.aliases()) {
                    String aliasName = alias.toLowerCase().trim();
                    commandsRegister.computeIfAbsent(aliasName, k -> new ArrayList<>()).add(commandMethod);
                }
            }
        }

        Set<String> rootCommands = new HashSet<>();
        for (String cmd : commandsRegister.keySet()) {
            String root = cmd.split(" ")[0];
            rootCommands.add(root);
        }

        try {
            java.lang.reflect.Field bukkitCommandMap = plugin.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            org.bukkit.command.CommandMap commandMap = (org.bukkit.command.CommandMap) bukkitCommandMap.get(plugin.getServer());

            for (String root : rootCommands) {
                List<String> aliases = new ArrayList<>();
                for (String key : commandsRegister.keySet()) {
                    if (key.startsWith(root)) {
                        List<CommandMethod> cms = commandsRegister.get(key);
                        for (CommandMethod cm : cms) {
                            Collections.addAll(aliases, cm.annotation.aliases());
                        }
                    }
                }
                DynamicCommand dynamicCommand = new DynamicCommand(root, aliases, this);
                commandMap.register(plugin.getDescription().getName(), dynamicCommand);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Erro ao registrar comandos dinamicamente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean handleCommand(CommandSender sender, String label, String[] args) {
        String base = label.toLowerCase();
        List<CommandMethod> methods = null;
        int usedArgs = 0;

        String currentKey = base;
        for (int i = 0; i <= args.length; i++) {
            if (i > 0) {
                currentKey += " " + args[i - 1].toLowerCase();
            }
            if (commandsRegister.containsKey(currentKey)) {
                methods = commandsRegister.get(currentKey);
                usedArgs = i;
            }
        }

        if (methods == null || methods.isEmpty()) {
            sender.sendMessage("§c[ ! ] Comando não encontrado.");
            return true;
        }

        String[] remainingArgs = Arrays.copyOfRange(args, usedArgs, args.length);

        CommandMatch match = findBestMethod(methods, sender, remainingArgs);
        if (match == null) {
            for (CommandMethod cm : methods) {
                if (hasPermission(sender, cm)) {
                    String usage = cm.annotation.usage();
                    if (!usage.isEmpty()) sender.sendMessage("§c[ ! ] Uso correto: " + usage);
                }
            }
            return true;
        }

        CommandMethod chosenMethod = match.getCommandMethod();

        if (!hasPermission(sender, chosenMethod)) return true;
        if (!isAllowedConsole(sender, chosenMethod)) return true;

        try {
            chosenMethod.method.invoke(
                    chosenMethod.instance,
                    match.getParameters()
            );
        } catch (IllegalArgumentException e) {
            String usage = chosenMethod.annotation.usage();
            if (!usage.isEmpty()) sender.sendMessage("§c[ ! ] Utilize: " + usage);
        } catch (Exception e) {
            plugin.getLogger().severe("Erro ao executar comando '" + currentKey + "': " + e.getMessage());
            e.printStackTrace();
            sender.sendMessage("§c[ ! ] Ocorreu um erro interno ao executar o comando.");
        }
        return true;
    }

    private CommandMatch findBestMethod(List<CommandMethod> methods, CommandSender sender, String[] args) {
        for (CommandMethod method : methods) {
            if (!matchesArgumentCount(method, args)) continue;

            Object[] parameters = resolveParameters(method, sender, args);

            if (parameters != null) return new CommandMatch(method, parameters);

        }

        return null;
    }
    private boolean hasPermission(CommandSender sender, CommandMethod commandMethod) {
        String perm = commandMethod.annotation.permission();
        if (!perm.isEmpty() && !sender.hasPermission(perm)) {
            sender.sendMessage("§c[ ! ] Você não tem permissão para isso.");
            return false;
        }
        return true;
    }

    private boolean isAllowedConsole(CommandSender sender, CommandMethod commandMethod) {
        if (!commandMethod.annotation.allowedConsole() && !(sender instanceof Player)) {
            sender.sendMessage("§c[ ! ] Esse comando só pode ser usado por jogadores.");
            return false;
        }
        return true;
    }

    private boolean matchesArgumentCount(CommandMethod method, String[] args) {

        Class<?>[] params = method.method.getParameterTypes();
        Parameter[] parameters = method.method.getParameters();

        int offset = getParameterOffset(params);

        int expectedArgs = params.length - offset;

        if (hasRemaining(parameters, offset)) {

            int minimumArgs = expectedArgs - 1;

            return args.length >= minimumArgs;
        }

        return expectedArgs == args.length;
    }

    private int getParameterOffset(Class<?>[] parameterTypes) {
        return parameterTypes.length > 0
                && CommandSender.class.isAssignableFrom(parameterTypes[0])
                ? 1
                : 0;
    }

    private boolean hasRemaining(Parameter[] parameters, int offset) {
        for (int i = offset; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(Remaining.class)) {
                return true;
            }
        }

        return false;
    }

    private String resolveArgument(Parameter parameter, String[] args, int argIndex) {
        if (parameter.isAnnotationPresent(Remaining.class)) {
            return String.join(
                    " ",
                    Arrays.copyOfRange(args, argIndex, args.length)
            );
        }

        String argument = argIndex < args.length
                ? args[argIndex]
                : null;

        if (argument == null && parameter.isAnnotationPresent(Default.class)) {
            return parameter.getAnnotation(Default.class).value();
        }

        if (argument == null && parameter.isAnnotationPresent(allayplugins.stompado.annotations.optional.Optional.class)) {

            String value = parameter.getAnnotation(Optional.class).value();

            return value.isEmpty()
                    ? null
                    : value;
        }

        return argument;
    }

    private Object[] resolveParameters(CommandMethod commandMethod, CommandSender sender, String[] args) {
        Class<?>[] parameterTypes = commandMethod.method.getParameterTypes();
        Parameter[] parameters = commandMethod.method.getParameters();

        int offset = getParameterOffset(parameterTypes);
        int expectedArgs = parameterTypes.length - offset;

        int argIndex = 0;

        List<Object> resolvedParameters = new ArrayList<>();

        // Injeta o sender
        if (offset == 1) {
            resolvedParameters.add(sender);
        }

        for (int i = 0; i < expectedArgs; i++) {

            Parameter parameter = parameters[i + offset];

            String argument = resolveArgument(parameter, args, argIndex);

            ResolveResult<?> result = resolveResult(parameter, sender, argument);

            if (!result.success()) {
                if (result.message() != null) {
                    Messages.send(sender, result.message());
                }

                return null;
            }

            resolvedParameters.add(result.value());

            if (!parameter.isAnnotationPresent(Remaining.class)) {
                argIndex++;
            }
        }

        return resolvedParameters.toArray();
    }

    public ResolveResult<?> resolveResult(Parameter parameter, CommandSender sender, String argument) {
        ArgumentResolver<?> resolver = resolverRegistry.get(parameter.getType());

        if (resolver == null) {
            return ResolveResult.error("");
        }

        return resolver.resolve(sender, parameter, argument);
    }


    private void registerDefaultResolvers() {
        resolverRegistry.register(String.class, new StringResolver());

        resolverRegistry.register(Target.class, new TargetResolver());

        resolverRegistry.register(Integer.class, new IntegerResolver());
        resolverRegistry.register(int.class, new IntegerResolver());

        resolverRegistry.register(Double.class, new DoubleResolver());
        resolverRegistry.register(double.class, new DoubleResolver());

        resolverRegistry.register(Long.class, new LongResolver());
        resolverRegistry.register(long.class, new LongResolver());

        resolverRegistry.register(GameMode.class, new GameModeResolver());
        resolverRegistry.register(Enchantment.class, new EnchantResolver());
    }

    public <T> void registerResolver(ArgumentResolver<T> resolver) {
        resolverRegistry.register(resolver.type(), resolver);
    }
}