package com.selfcoders.signwarper;

import org.bukkit.plugin.java.JavaPlugin;

public class SignWarper extends JavaPlugin {
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }
}
