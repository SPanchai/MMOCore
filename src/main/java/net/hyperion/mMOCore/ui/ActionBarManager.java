package net.hyperion.mMOCore.ui;

import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatMessageType;

public class ActionBarManager {
    public void updateActionBar(MMOPlayer mmoPlayer) {
        Player player = Bukkit.getPlayer(mmoPlayer.getPlayerUUID());
        if (player == null) return;

        // Format the title string: <HP | MP | Lv | Exp | Class>
        String hp = ChatColor.GREEN + String.format("HP %.0f/%.0f", player.getHealth() * (mmoPlayer.getFunctionalStat("MAX_HEALTH")/20.0), mmoPlayer.getFunctionalStat("MAX_HEALTH"));
        String mp = ChatColor.BLUE + String.format("MP %.0f/%.0f", 0.0, mmoPlayer.getFunctionalStat("MAX_MANA")); // Placeholder for now
        String level = ChatColor.AQUA + "Lv " + mmoPlayer.getLevel();
        String exp = ChatColor.GOLD + String.format("XP %.0f/%.0f", mmoPlayer.getExperience(), mmoPlayer.getRequiredExperience());
        String playerClass = ChatColor.YELLOW + mmoPlayer.getPlayerClassId();

        String title = String.format("%s §7| %s §7| %s §7| %s §7| %s", hp, mp, level, exp, playerClass);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(title));
    }
}