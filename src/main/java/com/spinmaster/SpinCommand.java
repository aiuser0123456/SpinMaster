package com.spinmaster;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class SpinCommand implements CommandExecutor {

    private final SpinManager manager;

    public SpinCommand(SpinManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;

        if (args.length == 1 && args[0].equalsIgnoreCase("setlocation")) {
            manager.setLocation(p.getLocation());
            p.sendMessage("§aSpin location set!");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("edit")) {
            p.openInventory(manager.getEditInventory());
            return true;
        }

        return false;
    }
}