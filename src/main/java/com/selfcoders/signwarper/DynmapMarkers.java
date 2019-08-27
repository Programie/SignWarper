package com.selfcoders.signwarper;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

class DynmapMarkers {
    private ConfigurationSection config;
    private MarkerSet markerSet;
    private MarkerIcon icon;
    private Map<String, Marker> markers = new HashMap<>();

    DynmapMarkers(MarkerAPI markerAPI, Logger logger, ConfigurationSection config) {
        this.config = config;

        markerSet = markerAPI.getMarkerSet("signwarper");

        String markerSetLabel = config.getString("layer-name", "Warps");

        if (markerSet == null) {
            markerSet = markerAPI.createMarkerSet("signwarper", markerSetLabel, null, false);
        } else {
            markerSet.setMarkerSetLabel(markerSetLabel);
        }

        if (markerSet == null) {
            logger.severe("Unable to create Dynmap marker set!");
            return;
        }

        markerSet.setLabelShow(config.getBoolean("show-labels"));
        markerSet.setHideByDefault(config.getBoolean("hide-by-default"));

        String iconName = config.getString("icon", "portal");
        icon = markerAPI.getMarkerIcon(iconName);

        if (icon == null) {
            logger.severe("Dynmap marker icon '" + iconName + "' is invalid! Falling back to default icon 'portal'.");

            icon = markerAPI.getMarkerIcon("portal");
        }
    }

    void cleanup() {
        if (markerSet != null) {
            markerSet.deleteMarkerSet();
            markerSet = null;
        }

        markers.clear();
    }

    void updateMarkerSet(List<Warp> warps) {
        Map<String, Marker> newMarkers = new HashMap<>();

        for (Warp warp : warps) {
            String name = warp.getName();
            Location location = warp.getLocation();

            World world = location.getWorld();

            if (world == null) {
                continue;
            }

            String worldName = world.getName();

            Marker marker = markers.remove(name);

            String label = config.getString("label-format", "%name%").replaceAll("%name%", name);

            if (marker == null) {
                marker = markerSet.createMarker(name, label, worldName, location.getX(), location.getY(), location.getZ(), icon, false);
            } else {
                marker.setLocation(worldName, location.getX(), location.getY(), location.getZ());
                marker.setLabel(label);
                marker.setMarkerIcon(icon);
            }

            newMarkers.put(name, marker);
        }

        for (Marker oldMarker : markers.values()) {
            oldMarker.deleteMarker();
        }

        markers.clear();
        markers = newMarkers;
    }
}
