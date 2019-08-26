package com.selfcoders.signwarper;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginEnableEvent;

import java.io.IOException;
import java.util.logging.Logger;

public class EventListener implements Listener {
    private final SignWarper plugin;

    EventListener(SignWarper plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (plugin.getConfig().getBoolean("enable-dynmap-markers") && event.getPlugin().getDescription().getName().equals("dynmap")) {
            plugin.activateMarkers();
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) throws IOException {
        SignData signData = new SignData(event.getLines());

        if (!signData.isWarpSign()) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.hasPermission("signwarper.create")) {
            player.sendMessage(ChatColor.RED + "You do not have the required permissions to create warp signs!");
            event.getBlock().breakNaturally();
            return;
        }

        if (!signData.isValidWarpName()) {
            player.sendMessage(ChatColor.RED + "No warp name defined!\nPlease specify the name for the warp on the second line.");
            event.getBlock().breakNaturally();
            return;
        }

        Warp existingWarp = Warp.getByName(plugin.getConfig(), plugin, signData.warpName);

        if (signData.isWarp()) {
            if (existingWarp == null) {
                player.sendMessage(ChatColor.RED + "The specified warp target does not exist!");
                event.getBlock().breakNaturally();
                return;
            }

            event.setLine(0, ChatColor.BLUE + SignData.HEADER_WARP);

            player.sendMessage(ChatColor.GREEN + "The warp sign has been placed successfully.");
        } else {
            if (existingWarp != null) {
                player.sendMessage(ChatColor.RED + "A warp target with the same name already exists!");
                event.getBlock().breakNaturally();
                return;
            }

            Warp warp = new Warp(plugin.getConfig(), plugin, signData.warpName, player.getLocation());
            warp.save();
            plugin.updateDynmapMarkers();

            event.setLine(0, ChatColor.BLUE + SignData.HEADER_TARGET);

            player.sendMessage(ChatColor.GREEN + "The warp target sign has been placed successfully.");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) throws IOException {
        Block block = event.getBlock();

        Sign signBlock = getSignFromBlock(block);

        if (signBlock == null) {
            return;
        }

        SignData signData = new SignData(signBlock.getLines());

        if (!signData.isWarpTarget()) {
            return;
        }

        if (!signData.isValidWarpName()) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.hasPermission("signwarper.create")) {
            player.sendMessage(ChatColor.RED + "You do not have the required permissions to destroy warp signs!");
            event.setCancelled(true);
            return;
        }

        Warp warp = Warp.getByName(plugin.getConfig(), plugin, signData.warpName);

        if (warp == null) {
            return;
        }

        warp.remove();
        plugin.updateDynmapMarkers();

        player.sendMessage(ChatColor.GREEN + "Warp destroyed." + ChatColor.RESET);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        Sign signBlock = getSignFromBlock(block);

        if (signBlock == null) {
            return;
        }

        SignData signData = new SignData(signBlock.getLines());

        if (!signData.isWarp() || !signData.isValidWarpName()) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.hasPermission("signwarper.use")) {
            player.sendMessage(ChatColor.RED + "You do not have the required permissions to use warp signs!");
            return;
        }

        FileConfiguration config = plugin.getConfig();

        String useItem = config.getString("use-item", "none");
        int useCost = config.getInt("use-cost", 0);

        // "none" should be equally to null
        if (useItem != null && useItem.equalsIgnoreCase("none")) {
            useItem = null;
        }

        Material itemInHand = event.getMaterial();

        if (useItem != null) {
            if (!itemInHand.name().equalsIgnoreCase(useItem)) {
                player.sendMessage(ChatColor.RED + "You have to use " + useItem + " for this warp!");
                return;
            }

            if (useCost > event.getItem().getAmount()) {
                player.sendMessage(ChatColor.RED + "You need " + useCost + " of " + useItem + " for this warp!");
                return;
            }
        }

        Warp warp = Warp.getByName(config, plugin, signData.warpName);

        if (warp == null) {
            player.sendMessage(ChatColor.RED + "The specified warp does not exist!");
            return;
        }

        if (useItem != null && useCost > 0) {
            event.getItem().setAmount(event.getItem().getAmount() - useCost);
        }

        Location targetLocation = warp.getLocation();

        player.teleport(targetLocation);

        World world = targetLocation.getWorld();
        world.playSound(targetLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        world.playEffect(targetLocation, Effect.ENDER_SIGNAL, 10);

        player.sendMessage(ChatColor.YELLOW + "Warped to " + warp.getName());
    }

    private Sign getSignFromBlock(Block block) {
        BlockData blockData = block.getBlockData();

        if (!(blockData instanceof WallSign) && !(blockData instanceof org.bukkit.block.data.type.Sign)) {
            return null;
        }

        BlockState blockState = block.getState();

        Logger.getLogger("Minecraft").info("block state: " + blockState.toString());

        if (!(blockState instanceof Sign)) {
            return null;
        }

        return (Sign) blockState;
    }
}
