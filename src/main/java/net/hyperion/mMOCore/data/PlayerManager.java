package net.hyperion.mMOCore.data;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private final Map<UUID, MMOPlayer> onlinePlayers = new HashMap<>();

    public void addPlayer(MMOPlayer mmoPlayer) {
        onlinePlayers.put(mmoPlayer.getPlayerUUID(), mmoPlayer);
    }

    public void removePlayer(Player player) {
        onlinePlayers.remove(player.getUniqueId());
    }

    public MMOPlayer getMMOPlayer(Player player) {
        return onlinePlayers.get(player.getUniqueId());
    }

    public boolean isPlayerLoaded(Player player) {
        return onlinePlayers.containsKey(player.getUniqueId());
    }

    public Map<UUID, MMOPlayer> getOnlinePlayers() {
        return onlinePlayers;
    }
}