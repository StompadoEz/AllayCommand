package allayplugins.stompado.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandContext {

    String name();
    String[] aliases() default {};
    String description() default "";
    String usage() default "";
    String permission() default "";
    boolean allowedConsole() default true;

}