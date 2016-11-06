# Here's What You're Looking At [![Build Status](http://tehnut.info/jenkins/buildStatus/icon?job=HWYLA/1.10)](http://tehnut.info/jenkins/job/HWYLA/job/1.10/) [![License](https://img.shields.io/badge/license-CC%20BY--NC--SA%204.0-blue.svg)](https://tldrlegal.com/license/creative-commons-attribution-noncommercial-sharealike-4.0-international-(cc-by-nc-sa-4.0))

**HWYLA** (Pronounced "Hwhy-la", similar to "Coo-Hwhip")

A fork of [WAILA](https://minecraft.curseforge.com/projects/waila) by [ProfMobius](https://minecraft.curseforge.com/members/ProfMobius).
This fork is permitted under the [CC BY-NC-SA 4.0](https://github.com/TehNut/HWYLA/blob/1.10/LICENSE.md) license.

Usage of this mod is allowed in all modpacks, including those hosted on Curse using it as a third party mod.

I intend to update this fork quickly and often.

## Changes

This is a list of the primary changes and differences between the official Waila and this fork. 

***Note***: This list may be incomplete. You can see a full list of changes (and more) in the commit list.

* Added a new plugin registration system
    * Use `@WailaPlugin` on an instance of `IWailaPlugin`
    * The annotation can take a String value for a required modid. Blank for any
* If fluid tooltips are enabled, they will now attempt to display the fluid inside a bucket.
* Forge Capability support (Information is obtained using a `null` facing. Mods who do not handle this correctly are not supported)
    * Native support for displaying tank information
        * Disabled by default so as to not clash with mods adding their own support
        * Limited to 5 tanks displayed to avoid mishaps where hundreds would get added
        * Only works on tanks using the Forge Capability system
    * Native support for displaying inventory contents
        * Disabled by default so as to not clash with mods adding their own support
        * Displays up to 5 items. If sneaking, the full inventory will be displayed
        * Items that are stack-able are condensed down. So if you have 2 stacks of Cobblestone, it will show with a single stack with a count of 128.
            * When a count reaches a high enough number, it will be condensed down. (eg: `1357` -> `1.3K`)
        * Works on all blocks that provide `IInventory`, `IItemHandler`, or `instanceof TileEntityEnderChest`
    * Native support for displaying Forge Energy storage
        * Disabled by default so as to not clash with mods adding their own support
        * Only works on energy handlers that provide `IEnergyStorage`
* Fixed the Furnace progress handler
    * Did any of you even know this was a thing? I sure didn't
* Maturity tooltips for crops now work correctly if the crop has a different max age.
* Added config for the format used to display Block, Entity, and Mod names
    * You can now wrap custom text around the name, with support for MOTD-like formatting codes.
    * Due to limitations presented by item rarity, the block name itself will always be a fixed colour, and reset formatting to default after itself.
    * See image below
* Fixed the ID + Meta tooltip
    * No more `< UNIMPLEMENTED >` in your tooltips
* Re-implemented in-game recipe lookups via key-bind
    * Now with 100% more JEI
* Changed the ItemStack lookup method to use `getPickBlock(...)` like everybody has though it's been for a long time
* Re-formatted the `/dumphandlers` command
    * Now prints the dump text to `WailaHandlerDump.md`
    * Formatted as markdown to increase readability
* Re-formatted all core handlers to the plugin format
    * Registered manually to make sure they are loaded first
* Removed lots of Vanilla special casing by fixing things *correctly*
* General code cleanup
* Removed plugin code for mods that have not updated
* Removed NEI handler code
* Removed all coremod related code
    * I think it was completely unused, but still
    
![Tooltip](http://i.imgur.com/0HwUV5b.png)