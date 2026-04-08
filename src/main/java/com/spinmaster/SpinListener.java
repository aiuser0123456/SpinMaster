package com.spinmaster;

import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpinListener implements Listener {

    private final SpinManager manager;

    public SpinListener(SpinManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {

        if (e.getClickedBlock() == null) return;

        if (manager.getSpinLocation() == null) return;

        if (e.getClickedBlock().getLocation().equals(manager.getSpinLocation())) {

            e.setCancelled(true);
            manager.trySpin(e.getPlayer());
        }
    }
}