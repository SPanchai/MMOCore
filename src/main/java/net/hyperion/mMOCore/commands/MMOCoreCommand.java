package net.hyperion.mMOCore.commands;

import net.hyperion.mMOCore.MMOCore;
import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MMOCoreCommand implements CommandExecutor {
    private final MMOCore plugin;

    public MMOCoreCommand(MMOCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("check")) {
            MMOPlayer mmoPlayer = plugin.getPlayerManager().getMMOPlayer(player);

            if (mmoPlayer == null) {
                player.sendMessage(ChatColor.RED + "Error: Your data is not loaded!");
                return true;
            }
            player.sendMessage(ChatColor.GOLD + "--- Your MMOCore Data ---");
            player.sendMessage(ChatColor.YELLOW + "Character: " + ChatColor.WHITE + mmoPlayer.getCharacterName());
            player.sendMessage(ChatColor.YELLOW + "Level: " + ChatColor.WHITE + mmoPlayer.getLevel());
            player.sendMessage(ChatColor.YELLOW + "EXP: " + ChatColor.WHITE + String.format("%.0f / %.0f", mmoPlayer.getExperience(), mmoPlayer.getRequiredExperience()));
            player.sendMessage(ChatColor.YELLOW + "Attribute Points: " + ChatColor.WHITE + mmoPlayer.getAttributePoints());
            player.sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.WHITE + mmoPlayer.getPlayerClassId());
            player.sendMessage(ChatColor.YELLOW + "Learned Skills: " + ChatColor.WHITE + mmoPlayer.getLearnedSkills().toString());
            player.sendMessage(ChatColor.GOLD + "-------------------------");
            player.sendMessage(ChatColor.GOLD + "--- Attributes ---");
            player.sendMessage(ChatColor.YELLOW + "Points to Spend: " + ChatColor.WHITE + mmoPlayer.getAttributePoints());

            String strText = String.format("STR: %d (+%d) -> %d", mmoPlayer.getPermanentAttribute("STRENGTH"), mmoPlayer.getBonusAttribute("STRENGTH"), mmoPlayer.getTotalAttribute("STRENGTH"));
            String dexText = String.format("DEX: %d (+%d) -> %d", mmoPlayer.getPermanentAttribute("DEXTERITY"), mmoPlayer.getBonusAttribute("DEXTERITY"), mmoPlayer.getTotalAttribute("DEXTERITY"));
            String intText = String.format("INT: %d (+%d) -> %d", mmoPlayer.getPermanentAttribute("INTELLIGENCE"), mmoPlayer.getBonusAttribute("INTELLIGENCE"), mmoPlayer.getTotalAttribute("INTELLIGENCE"));
            player.sendMessage(ChatColor.WHITE + strText);
            player.sendMessage(ChatColor.WHITE + dexText);
            player.sendMessage(ChatColor.WHITE + intText);

            player.sendMessage(ChatColor.GOLD + "--- Functional Stats ---");
            player.sendMessage(ChatColor.WHITE + "Max Health: " + String.format("%.1f", mmoPlayer.getFunctionalStat("MAX_HEALTH")));
            player.sendMessage(ChatColor.WHITE + "Physical Damage: " + String.format("%.1f", mmoPlayer.getFunctionalStat("PHYSICAL_DAMAGE")));
            player.sendMessage(ChatColor.WHITE + "Crit Rate: " + String.format("%.1f%%", mmoPlayer.getFunctionalStat("CRITICAL_RATE")));
            player.sendMessage(ChatColor.WHITE + "Crit Damage: " + String.format("%.1f%%", mmoPlayer.getFunctionalStat("CRITICAL_DAMAGE")));

            player.sendMessage(ChatColor.GOLD + "-------------------------");
            return true;
        }

        player.sendMessage(ChatColor.GRAY + "Usage: /mmocore check");
        return true;
    }
}