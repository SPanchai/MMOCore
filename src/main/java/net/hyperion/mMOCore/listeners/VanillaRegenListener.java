package net.hyperion.mMOCore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class VanillaRegenListener implements Listener {
    @EventHandler
    public void onVanillaRegen(EntityRegainHealthEvent event) {
        // Cancel the default health regen that comes from having a full hunger bar
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            event.setCancelled(true);
        }
    }
}