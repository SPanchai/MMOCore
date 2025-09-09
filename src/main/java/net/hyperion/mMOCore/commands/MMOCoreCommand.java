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
            // Get the MMOPlayer object from the cache
            MMOPlayer mmoPlayer = plugin.getPlayerManager().getMMOPlayer(player);

            if (mmoPlayer == null) {
                player.sendMessage(ChatColor.RED + "Error: Your data is not loaded!");
                return true;
            }

            // Display the data to the player
            player.sendMessage(ChatColor.GOLD + "--- Your MMOCore Data ---");
            player.sendMessage(ChatColor.YELLOW + "Character: " + ChatColor.WHITE + mmoPlayer.getCharacterName());
            player.sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.WHITE + mmoPlayer.getPlayerClassId());
            player.sendMessage(ChatColor.YELLOW + "Learned Skills: " + ChatColor.WHITE + mmoPlayer.getLearnedSkills().toString());
            player.sendMessage(ChatColor.GOLD + "-------------------------");
            return true;
        }

        player.sendMessage(ChatColor.GRAY + "Usage: /mmocore check");
        return true;
    }
}