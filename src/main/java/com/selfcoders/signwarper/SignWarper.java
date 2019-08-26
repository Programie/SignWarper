package com.selfcoders.signwarper;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerAPI;

public class SignWarper extends JavaPlugin {
    private DynmapAPI dynmapAPI;
    private DynmapMarkers dynmapMarkers;

    public void onEnable() {
        saveDefaultConfig();

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new EventListener(this), this);

        if (getConfig().getBoolean("enable-dynmap-markers")) {
            Plugin dynmap = pluginManager.getPlugin("dynmap");
            if (dynmap == null) {
                getLogger().severe("Dynmap markers enabled but Dynmap plugin is not available! Markers will be disabled.");
            } else {
                dynmapAPI = (DynmapAPI) dynmap;
                getLogger().info("Dynmap markers enabled");

                if (dynmap.isEnabled()) {
                    activateMarkers();
                }
            }
        }
    }

    public void onDisable() {
        if (dynmapMarkers != null) {
            dynmapMarkers.cleanup();
        }
    }

    void activateMarkers() {
        MarkerAPI markerAPI = dynmapAPI.getMarkerAPI();

        if (markerAPI == null) {
            getLogger().severe("Error getting Dynmap marker API!");
            return;
        }

        dynmapMarkers = new DynmapMarkers(markerAPI, getLogger());
        updateDynmapMarkers();
    }

    void updateDynmapMarkers() {
        if (dynmapMarkers == null) {
            return;
        }

        dynmapMarkers.updateMarkerSet(Warp.getAll(getConfig(), this));
    }
}
