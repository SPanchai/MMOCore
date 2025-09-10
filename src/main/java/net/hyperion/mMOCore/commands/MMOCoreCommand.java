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

            // --- NEW, DETAILED DISPLAY FORMAT ---
            player.sendMessage(ChatColor.GOLD + "--- Your MMOCore Data ---");
            player.sendMessage(ChatColor.YELLOW + "Character: " + ChatColor.WHITE + mmoPlayer.getCharacterName());
            player.sendMessage(ChatColor.YELLOW + "Level: " + ChatColor.WHITE + mmoPlayer.getLevel());
            player.sendMessage(ChatColor.YELLOW + "EXP: " + ChatColor.WHITE + String.format("%.0f / %.0f", mmoPlayer.getExperience(), mmoPlayer.getRequiredExperience()));
            player.sendMessage(ChatColor.YELLOW + "Attribute Points: " + ChatColor.WHITE + mmoPlayer.getAttributePoints());
            player.sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.WHITE + mmoPlayer.getPlayerClassId());
            player.sendMessage(ChatColor.YELLOW + "Learned Skills: " + ChatColor.WHITE + mmoPlayer.getLearnedSkills().toString());
            player.sendMessage(""); // Spacer

            player.sendMessage(ChatColor.GOLD + "--- Attributes ---");
            player.sendMessage(ChatColor.YELLOW + "Points to Spend: " + ChatColor.WHITE + mmoPlayer.getAttributePoints());

            // Format each attribute line with Permanent (+Bonus) -> Total
            String strText = String.format("STR: %d (+%d) -> %d", mmoPlayer.getPermanentAttribute("STRENGTH"), mmoPlayer.getBonusAttribute("STRENGTH"), mmoPlayer.getTotalAttribute("STRENGTH"));
            String dexText = String.format("DEX: %d (+%d) -> %d", mmoPlayer.getPermanentAttribute("DEXTERITY"), mmoPlayer.getBonusAttribute("DEXTERITY"), mmoPlayer.getTotalAttribute("DEXTERITY"));
            String intText = String.format("INT: %d (+%d) -> %d", mmoPlayer.getPermanentAttribute("INTELLIGENCE"), mmoPlayer.getBonusAttribute("INTELLIGENCE"), mmoPlayer.getTotalAttribute("INTELLIGENCE"));
            String vitText = String.format("VIT: %d (+%d) -> %d", mmoPlayer.getPermanentAttribute("VITALITY"), mmoPlayer.getBonusAttribute("VITALITY"), mmoPlayer.getTotalAttribute("VITALITY"));
            String lukText = String.format("LUK: %d (+%d) -> %d", mmoPlayer.getPermanentAttribute("LUCK"), mmoPlayer.getBonusAttribute("LUCK"), mmoPlayer.getTotalAttribute("LUCK"));

            player.sendMessage(ChatColor.WHITE + strText);
            player.sendMessage(ChatColor.WHITE + dexText);
            player.sendMessage(ChatColor.WHITE + intText);
            player.sendMessage(ChatColor.WHITE + vitText);
            player.sendMessage(ChatColor.WHITE + lukText);
            player.sendMessage(""); // Spacer

            player.sendMessage(ChatColor.GOLD + "--- Functional Stats ---");
            player.sendMessage(ChatColor.WHITE + "Max Health: " + String.format("%.1f", mmoPlayer.getFunctionalStat("MAX_HEALTH")));
            player.sendMessage(ChatColor.WHITE + "Max Mana: " + String.format("%.1f", mmoPlayer.getFunctionalStat("MAX_MANA")));
            player.sendMessage(ChatColor.WHITE + "Phys Damage: " + String.format("%.1f - %.1f", mmoPlayer.getFunctionalStat("MIN_PHYSICAL_DAMAGE"), mmoPlayer.getFunctionalStat("MAX_PHYSICAL_DAMAGE")));
            player.sendMessage(ChatColor.WHITE + "Magic Damage: " + String.format("%.1f - %.1f", mmoPlayer.getFunctionalStat("MIN_MAGICAL_DAMAGE"), mmoPlayer.getFunctionalStat("MAX_MAGICAL_DAMAGE")));
            player.sendMessage(ChatColor.WHITE + "Phys Defense: " + String.format("%.1f", mmoPlayer.getFunctionalStat("PHYSICAL_DEFENSE")));
            player.sendMessage(ChatColor.WHITE + "Magic Defense: " + String.format("%.1f", mmoPlayer.getFunctionalStat("MAGICAL_DEFENSE")));
            player.sendMessage(ChatColor.WHITE + "Attack Speed: " + String.format("%.2f", mmoPlayer.getFunctionalStat("ATTACK_SPEED")));
            player.sendMessage(ChatColor.WHITE + "Movement Speed: " + String.format("%.3f", mmoPlayer.getFunctionalStat("MOVEMENT_SPEED")));
            player.sendMessage(ChatColor.WHITE + "Crit Rate: " + String.format("%.1f%%", mmoPlayer.getFunctionalStat("CRITICAL_RATE")));
            player.sendMessage(ChatColor.WHITE + "Crit Damage: " + String.format("%.1f%%", mmoPlayer.getFunctionalStat("CRITICAL_DAMAGE")));
            player.sendMessage(ChatColor.GOLD + "-------------------------");
            return true;
        }

        player.sendMessage(ChatColor.GRAY + "Usage: /mmocore <check|reload>");
        return true;
    }
}