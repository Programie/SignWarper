# SignWarper

A Minecraft Bukkit plugin to warp using signs.

![](screenshot.jpg)

[![pipeline status](https://gitlab.com/Programie/SignWarper/badges/master/pipeline.svg)](https://gitlab.com/Programie/SignWarper/commits/master)
[![download from GitLab](https://img.shields.io/badge/download-Releases-blue?logo=gitlab)](https://gitlab.com/Programie/SignWarper/-/releases)
[![download from Modrinth](https://img.shields.io/badge/download-Modrinth-blue?logo=modrinth)](https://modrinth.com/plugin/signwarper)
[![download from CurseForge](https://img.shields.io/badge/download-CurseForge-blue?logo=curseforge)](https://www.curseforge.com/minecraft/bukkit-plugins/sign-warper)

## What is it?

SignWarper allows players to place signs to teleport between them by simply right clicking on them.

By default, teleports have a cost of one ender pearl which must be in the players hand while interacting with the sign, but it can also be disabled in the configuration.

## Permissions

* `signwarper.create` - Allow to create and destroy warp signs (Default: op)
* `signwarper.use` - Allow to use warp signs (Default: everyone)
* `signwarper.*` - Allow access to all features (Default: op)

## How to use it?

Simply place a sign at the location you want to warp to with the following content:

* First line: `[WarpTarget]`
* Second line: The name you want to use

This will create a warp target sign which defines the location a player is getting warped to.

After that create one or more warp signs from which you want to be able to warp to the target sign. This is done by placing a sign with the following content:

* First line: `[Warp]`
* Second line: The same name as used on the warp target sign

After that you are able to right click with the `use-item` in your hand (defaults to ender pearl). Each warp will cost the number of items configured in `use-cost` (defaults to 1).

You can remove the `use-item` option in the [config.yml](src/main/resources/config.yml) or set it to "none" to allow any item to be used without actually using the item (i.e. each warp is free to use).

Removing the `use-item` option in the [config.yml](src/main/resources/config.yml) or setting it to 0 results in the item not being used (i.e. each warp is free to use).

## Dynmap markers

SignWarper supports for showing the warp targets as markers in Dynmap. All you need to do is install the Dynmap plugin and enable the markers using the `dynmap.enable-markers` option in the [config.yml](src/main/resources/config.yml) file.

## Know issues

A warp won't be removed if the block is removed on which the warp target sign has been placed. The warp will continue to function but it can only be removed by manually editing the [config.yml](src/main/resources/config.yml) and reloading/restarting the server.

## Build

You can build the project in the following 2 steps:

 * Check out the repository
 * Build the jar file using maven: *mvn clean package*

**Note:** JDK 1.8 and Maven is required to build the project!