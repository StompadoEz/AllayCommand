package allayplugins.stompado.text;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class Messages {

    private Messages() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void send(CommandSender sender, String message) {
        Objects.requireNonNull(sender, "CommandSender cannot be null.");
        Objects.requireNonNull(message, "Message cannot be null.");

        sender.sendMessage(color(message));
    }


    public static String color(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
