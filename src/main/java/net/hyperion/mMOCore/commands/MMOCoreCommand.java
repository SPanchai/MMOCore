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
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                return handleReload(sender);
            } else if (args[0].equalsIgnoreCase("check") && sender instanceof Player) {
                return handleCheck((Player) sender);
            } else if (args[0].equalsIgnoreCase("stats") && sender instanceof Player) {
                return handleDetailedStats((Player) sender);
            } else if (args[0].equalsIgnoreCase("help")) {
                return handleHelp(sender);
            }
        }

        return handleHelp(sender);
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("mmocore.admin.reload")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to reload the config.");
            return true;
        }

        plugin.reloadPluginConfig();
        String message = plugin.getConfig().getString("messages.config-reloaded", "&aMMOCore configuration reloaded successfully!");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        return true;
    }

    private boolean handleCheck(Player player) {
        MMOPlayer mmoPlayer = plugin.getPlayerManager().getMMOPlayer(player);
        if (mmoPlayer == null) {
            player.sendMessage(ChatColor.RED + "Error: Your data is not loaded!");
            return true;
        }

        // Basic character information
        player.sendMessage(ChatColor.GOLD + "--- Your MMOCore Data ---");
        player.sendMessage(ChatColor.YELLOW + "Character: " + ChatColor.WHITE + mmoPlayer.getCharacterName());
        player.sendMessage(ChatColor.YELLOW + "Level: " + ChatColor.WHITE + mmoPlayer.getLevel());
        player.sendMessage(ChatColor.YELLOW + "EXP: " + ChatColor.WHITE + String.format("%.0f / %.0f", mmoPlayer.getExperience(), mmoPlayer.getRequiredExperience()));
        player.sendMessage(ChatColor.YELLOW + "Attribute Points: " + ChatColor.WHITE + mmoPlayer.getAttributePoints());
        player.sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.WHITE + mmoPlayer.getPlayerClassId());

        // Core Stats
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "--- Core Stats ---");
        player.sendMessage(ChatColor.WHITE + "Health: " + String.format("%.0f / %.0f", mmoPlayer.getCurrentHealth(), mmoPlayer.getFunctionalStat("MAX_HEALTH")));
        player.sendMessage(ChatColor.WHITE + "Mana: " + String.format("%.0f / %.0f", 0.0, mmoPlayer.getFunctionalStat("MAX_MANA")));
        player.sendMessage(ChatColor.WHITE + "Health Regen: " + String.format("%.1f/s", mmoPlayer.getFunctionalStat("HEALTH_REGEN")));
        player.sendMessage(ChatColor.WHITE + "Mana Regen: " + String.format("%.1f/s", mmoPlayer.getFunctionalStat("MANA_REGEN")));

        // Combat Stats
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "--- Combat Stats ---");
        player.sendMessage(ChatColor.WHITE + "Physical Damage: " + String.format("%.1f - %.1f",
                mmoPlayer.getFunctionalStat("MIN_PHYSICAL_DAMAGE"),
                mmoPlayer.getFunctionalStat("MAX_PHYSICAL_DAMAGE")));
        player.sendMessage(ChatColor.WHITE + "Magical Damage: " + String.format("%.1f - %.1f",
                mmoPlayer.getFunctionalStat("MIN_MAGICAL_DAMAGE"),
                mmoPlayer.getFunctionalStat("MAX_MAGICAL_DAMAGE")));
        player.sendMessage(ChatColor.WHITE + "Critical Rate: " + String.format("%.1f%%", mmoPlayer.getFunctionalStat("CRITICAL_RATE")));
        player.sendMessage(ChatColor.WHITE + "Critical Damage: " + String.format("%.1f%%", mmoPlayer.getFunctionalStat("CRITICAL_DAMAGE")));

        player.sendMessage(ChatColor.GRAY + "Use '/mmocore stats' for detailed stats");
        player.sendMessage(ChatColor.GOLD + "-------------------------");

        return true;
    }

    private boolean handleDetailedStats(Player player) {
        MMOPlayer mmoPlayer = plugin.getPlayerManager().getMMOPlayer(player);
        if (mmoPlayer == null) {
            player.sendMessage(ChatColor.RED + "Error: Your data is not loaded!");
            return true;
        }

        // Attributes
        player.sendMessage(ChatColor.GOLD + "=== DETAILED STATS ===");
        player.sendMessage(ChatColor.YELLOW + "--- Attributes ---");
        String[] attributes = {"STRENGTH", "DEXTERITY", "INTELLIGENCE", "VITALITY", "LUCK"};
        String[] shortNames = {"STR", "DEX", "INT", "VIT", "LUK"};

        for (int i = 0; i < attributes.length; i++) {
            String attrText = String.format("%s: %d (+%d) = %d",
                    shortNames[i],
                    mmoPlayer.getPermanentAttribute(attributes[i]),
                    mmoPlayer.getBonusAttribute(attributes[i]),
                    mmoPlayer.getTotalAttribute(attributes[i]));
            player.sendMessage(ChatColor.WHITE + attrText);
        }

        // Defensive Stats
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "--- Defense ---");
        player.sendMessage(ChatColor.WHITE + "Physical Defense: " + String.format("%.1f", mmoPlayer.getFunctionalStat("PHYSICAL_DEFENSE")));
        player.sendMessage(ChatColor.WHITE + "Magical Defense: " + String.format("%.1f", mmoPlayer.getFunctionalStat("MAGICAL_DEFENSE")));
        player.sendMessage(ChatColor.WHITE + "Ranged Defense: " + String.format("%.1f", mmoPlayer.getFunctionalStat("RANGED_DEFENSE")));
        player.sendMessage(ChatColor.WHITE + "Dodge Rate: " + String.format("%.1f%%", mmoPlayer.getFunctionalStat("DODGE_RATE")));
        player.sendMessage(ChatColor.WHITE + "Block Rate: " + String.format("%.1f%%", mmoPlayer.getFunctionalStat("BLOCK_RATE")));
        player.sendMessage(ChatColor.WHITE + "Block Value: " + String.format("%.1f", mmoPlayer.getFunctionalStat("BLOCK_VALUE")));

        // Utility Stats
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "--- Utility ---");
        player.sendMessage(ChatColor.WHITE + "Attack Speed: " + String.format("%.2f", mmoPlayer.getFunctionalStat("ATTACK_SPEED")));
        player.sendMessage(ChatColor.WHITE + "Cast Speed: " + String.format("%.2f", mmoPlayer.getFunctionalStat("CAST_SPEED")));
        player.sendMessage(ChatColor.WHITE + "Movement Speed: " + String.format("%.3f", mmoPlayer.getFunctionalStat("MOVEMENT_SPEED")));
        player.sendMessage(ChatColor.WHITE + "Accuracy: " + String.format("%.1f%%", mmoPlayer.getFunctionalStat("ACCURACY")));

        // Special Stats
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "--- Special ---");
        player.sendMessage(ChatColor.WHITE + "Lifesteal: " + String.format("%.1f%%", mmoPlayer.getFunctionalStat("LIFESTEAL")));
        player.sendMessage(ChatColor.WHITE + "Armor Penetration: " + String.format("%.1f", mmoPlayer.getFunctionalStat("ARMOR_PENETRATION")));
        player.sendMessage(ChatColor.WHITE + "Magic Penetration: " + String.format("%.1f", mmoPlayer.getFunctionalStat("MAGIC_PENETRATION")));
        player.sendMessage(ChatColor.WHITE + "Experience Rate: " + String.format("+%.1f%%", mmoPlayer.getFunctionalStat("EXPERIENCE_RATE")));
        player.sendMessage(ChatColor.WHITE + "Drop Rate: " + String.format("+%.1f%%", mmoPlayer.getFunctionalStat("DROP_RATE")));

        player.sendMessage(ChatColor.GOLD + "======================");

        return true;
    }

    private boolean handleHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "--- MMOCore Commands ---");
        sender.sendMessage(ChatColor.YELLOW + "/mmocore check - View basic character stats");
        sender.sendMessage(ChatColor.YELLOW + "/mmocore stats - View detailed stats");
        sender.sendMessage(ChatColor.YELLOW + "/mmocore help - Show this help message");

        if (sender.hasPermission("mmocore.admin.reload")) {
            sender.sendMessage(ChatColor.YELLOW + "/mmocore reload - Reload configuration");
        }

        sender.sendMessage(ChatColor.YELLOW + "/attribute spend <attribute> <amount> - Spend attribute points");
        return true;
    }
}
