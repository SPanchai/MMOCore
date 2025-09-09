package net.hyperion.mMOCore.database;

import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.entity.Player;

public interface IDataSource {
    void init();
    void savePlayer(MMOPlayer mmoPlayer);
    MMOPlayer loadPlayer(Player player);
    void shutdown();
}