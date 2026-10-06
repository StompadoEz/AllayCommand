package allayplugins.stompado.handler;

import allayplugins.stompado.manager.CommandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DynamicCommand extends Command {
    private final CommandManager manager;

    public DynamicCommand(String name, List<String> aliases, CommandManager manager) {
        super(name);
        this.manager = manager;
        this.setAliases(aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return manager.handleCommand(sender, label, args);
    }
}