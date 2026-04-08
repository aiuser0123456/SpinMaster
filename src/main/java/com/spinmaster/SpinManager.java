package com.spinmaster;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SpinManager {

    private final SpinMaster plugin;
    private final Map<UUID, Long> lastSpin = new HashMap<>();
    private final List<Reward> rewards = new ArrayList<>();
    private boolean spinning = false;

    public SpinManager(SpinMaster plugin) {
        this.plugin = plugin;
        loadRewards();
    }

    public void loadRewards() {
        rewards.clear();

        var section = plugin.getConfig().getConfigurationSection("rewards");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            String cmd = section.getString(key + ".command");
            double chance = section.getDouble(key + ".chance");
            String rarity = section.getString(key + ".rarity");

            rewards.add(new Reward(cmd, chance, rarity));
        }
    }

    public Location getSpinLocation() {
        if (!plugin.getConfig().contains("spin.location")) return null;
        return (Location) plugin.getConfig().get("spin.location");
    }

    public void setLocation(Location loc) {
        plugin.getConfig().set("spin.location", loc);
        plugin.saveConfig();
    }

    public void trySpin(Player player) {

        if (spinning) {
            player.sendMessage("§cSomeone is already spinning!");
            return;
        }

        if (!canSpin(player)) {
            player.sendMessage("§cCome back tomorrow!");
            return;
        }

        spinning = true;
        startSpin(player);
    }

    public boolean canSpin(Player player) {
        long last = lastSpin.getOrDefault(player.getUniqueId(), 0L);
        return System.currentTimeMillis() - last > 86400000;
    }

    public void setUsed(Player player) {
        lastSpin.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public Reward getRandomReward() {
        double total = rewards.stream().mapToDouble(r -> r.chance).sum();
        double rand = Math.random() * total;

        double current = 0;
        for (Reward r : rewards) {
            current += r.chance;
            if (rand <= current) return r;
        }
        return rewards.get(0);
    }

    public void startSpin(Player player) {

        Inventory inv = Bukkit.createInventory(null, 27, "§6Spinning...");

        player.openInventory(inv);

        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {

                ticks++;

                inv.clear();

                for (int i = 0; i < 27; i++) {
                    inv.setItem(i, new ItemStack(Material.values()[new Random().nextInt(Material.values().length)]));
                }

                if (ticks >= 40) {

                    Reward reward = getRandomReward();
                    giveReward(player, reward);

                    setUsed(player);
                    spinning = false;

                    cancel();
                }
            }

        }.runTaskTimer(plugin, 0, 2);
    }

    public void giveReward(Player player, Reward reward) {

        String cmd = reward.command.replace("%player%", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);

        switch (reward.rarity.toLowerCase()) {
            case "legendary":
                player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation(), 100);
                break;
            case "epic":
                player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 60);
                break;
            default:
                player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation(), 30);
        }
    }

    public Inventory getEditInventory() {
        Inventory inv = Bukkit.createInventory(null, 27, "§cEdit Rewards");
        return inv;
    }
}