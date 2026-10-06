package allayplugins.stompado.command;

import allayplugins.stompado.method.CommandMethod;

public class CommandMatch {

    private final CommandMethod commandMethod;
    private final Object[] parameters;

    public CommandMatch(CommandMethod commandMethod, Object[] parameters) {
        this.commandMethod = commandMethod;
        this.parameters = parameters;
    }

    public CommandMethod getCommandMethod() {
        return commandMethod;
    }

    public Object[] getParameters() {
        return parameters;
    }
}