package net.hyperion.mMOCore.commands;

import net.hyperion.mMOCore.MMOCore;
import net.hyperion.mMOCore.data.MMOPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AttributeCommand implements CommandExecutor {
    private final MMOCore plugin;

    public AttributeCommand(MMOCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;
        MMOPlayer mmoPlayer = plugin.getPlayerManager().getMMOPlayer(player);

        if (mmoPlayer == null) {
            player.sendMessage(ChatColor.RED + "Your data is not loaded yet. Please wait a moment.");
            return true;
        }

        // Usage: /attribute spend <type> <amount>
        if (args.length == 3 && args[0].equalsIgnoreCase("spend")) {
            String attribute = args[1].toUpperCase();
            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Amount must be a number.");
                return true;
            }

            if (amount <= 0) {
                player.sendMessage(ChatColor.RED + "You must spend a positive number of points.");
                return true;
            }


            if (!mmoPlayer.getPermanentAttributes().containsKey(attribute)) {
                player.sendMessage(ChatColor.RED + "Invalid attribute. Use STRENGTH, DEXTERITY, or INTELLIGENCE.");
                return true;
            }

            if (mmoPlayer.getAttributePoints() < amount) {
                player.sendMessage(ChatColor.RED + "You don't have enough attribute points. You have " + mmoPlayer.getAttributePoints() + ".");
                return true;
            }


            // Spend the points
            mmoPlayer.setAttributePoints(mmoPlayer.getAttributePoints() - amount);
            int currentAttrValue = mmoPlayer.getPermanentAttributes().get(attribute);
            mmoPlayer.getPermanentAttributes().put(attribute, currentAttrValue + amount);
            plugin.getStatManager().recalculateStats(mmoPlayer);

            player.sendMessage(ChatColor.GREEN + "You have spent " + amount + " points on " + attribute + ".");
            player.sendMessage(ChatColor.GRAY + "You have " + mmoPlayer.getAttributePoints() + " points remaining.");
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "--- Attribute Command ---");
        player.sendMessage(ChatColor.YELLOW + "Your Points: " + ChatColor.WHITE + mmoPlayer.getAttributePoints());
        player.sendMessage(ChatColor.YELLOW + "Usage: /attribute spend <str|dex|int> <amount>");
        return true;
    }
}