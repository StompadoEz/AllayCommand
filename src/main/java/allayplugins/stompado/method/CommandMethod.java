package allayplugins.stompado.method;

import allayplugins.stompado.command.CommandContext;

import java.lang.reflect.Method;

public class CommandMethod {

    public final Object instance;
    public final Method method;
    public final CommandContext annotation;

    public CommandMethod(Object instance, Method method, CommandContext annotation) {
        this.instance = instance;
        this.method = method;
        this.annotation = annotation;
        this.method.setAccessible(true);
    }
}