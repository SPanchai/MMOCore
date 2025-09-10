package net.hyperion.mMOCore.scoreboard;

import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardManager {
    public void updateScoreboard(MMOPlayer mmoPlayer) {
        Player player = Bukkit.getPlayer(mmoPlayer.getPlayerUUID());
        if (player == null) return;

        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null || scoreboard.getObjective("mmo_stats") == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("mmo_stats", "dummy", ChatColor.GOLD + "" + ChatColor.BOLD + "Your Stats");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            player.setScoreboard(scoreboard);
        }
        Objective objective = scoreboard.getObjective("mmo_stats");

        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        setScore(objective, " ", 12);
        setScore(objective, ChatColor.WHITE + "HP: " + ChatColor.GREEN + String.format("%.0f / %.0f", player.getHealth(), mmoPlayer.getFunctionalStat("MAX_HEALTH")), 11);
        setScore(objective, "  ", 10);
        setScore(objective, ChatColor.WHITE + "Level: " + ChatColor.AQUA + mmoPlayer.getLevel() + " " + mmoPlayer.getPlayerClassId(), 9);
        setScore(objective, ChatColor.WHITE + "EXP: " + ChatColor.AQUA + String.format("%.0f / %.0f", mmoPlayer.getExperience(), mmoPlayer.getRequiredExperience()), 8);
        setScore(objective, "   ", 7);
        setScore(objective, ChatColor.RED + "STR: " + mmoPlayer.getTotalAttribute("STRENGTH"), 6);
        setScore(objective, ChatColor.GREEN + "DEX: " + mmoPlayer.getTotalAttribute("DEXTERITY"), 5);
        setScore(objective, ChatColor.BLUE + "INT: " + mmoPlayer.getTotalAttribute("INTELLIGENCE"), 4);
        setScore(objective, ChatColor.YELLOW + "VIT: " + mmoPlayer.getTotalAttribute("VITALITY"), 3);
        setScore(objective, ChatColor.LIGHT_PURPLE + "LUK: " + mmoPlayer.getTotalAttribute("LUCK"), 2);
        setScore(objective, "    ", 1);
        setScore(objective, ChatColor.GRAY + "hyperion.net", 0);
    }

    private void setScore(Objective objective, String text, int score) {
        while (objective.getScoreboard().getEntries().contains(text)) {
            text += ChatColor.RESET;
        }
        objective.getScore(text).setScore(score);
    }
}