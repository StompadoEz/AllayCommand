package allayplugins.stompado.target;

import org.bukkit.entity.Player;

public class Target {

    private final Player player;

    public Target(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}