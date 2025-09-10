package net.hyperion.mMOCore.listeners;

import net.hyperion.mMOCore.MMOCore;
import net.hyperion.mMOCore.data.MMOPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobKillListener implements Listener {
    private final MMOCore plugin;
    public MobKillListener(MMOCore plugin) { this.plugin = plugin; }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) return;

        Player killer = event.getEntity().getKiller();
        MMOPlayer mmoPlayer = plugin.getPlayerManager().getMMOPlayer(killer);
        if (mmoPlayer == null) return;

        double expToGrant = 25;
        mmoPlayer.addExperience(expToGrant);

        // Update the main UI bar first
        plugin.getUiManager().updateActionBar(mmoPlayer);

        // Then, temporarily flash an "EXP GAIN" message over it
        killer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + "+ " + expToGrant + " EXP"));
    }
}