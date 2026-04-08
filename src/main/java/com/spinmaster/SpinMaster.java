package com.spinmaster;

import org.bukkit.plugin.java.JavaPlugin;

public class SpinMaster extends JavaPlugin {

    private SpinManager manager;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        manager = new SpinManager(this);

        getServer().getPluginManager().registerEvents(new SpinListener(manager), this);

        getCommand("spinwheel").setExecutor(new SpinCommand(manager));
    }
}