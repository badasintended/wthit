# Waila-Reborn [![Build Status](http://tehnut.info/jenkins/buildStatus/icon?job=Waila-Reborn/1.10)](http://tehnut.info/jenkins/job/Waila-Reborn/1.10) [![License](https://img.shields.io/badge/license-CC%20BY--NC--SA%204.0-blue.svg)](https://tldrlegal.com/license/creative-commons-attribution-noncommercial-sharealike-4.0-international-(cc-by-nc-sa-4.0))

A fork of [Waila by ProfMobius](https://minecraft.curseforge.com/projects/waila). This fork is allowable under the [license](https://github.com/TehNut/Waila-Reborn/blob/1.10/LICENSE.md).

I intend to update this fork quickly and often.

## Changes

This is a list of the primary changes and differences between the official Waila and this fork. 

***Note***: This list may be incomplete. You can see a full list of changes (and more) in the commit list.

* Added a new plugin registration system
    * Use `@WailaPlugin` on an instance of `IWailaPlugin`
    * The annotation can take a String value for a required modid. Blank for any
* If fluid tooltips are enabled, they will now attempt to display the fluid inside a bucket.
* Added native support for displaying tank information
    * Disabled by default so as to not clash with mods adding their own support
    * Limited to 5 tanks displayed to avoid mishaps where hundreds would get added
    * Only works on tanks using the Forge Capability system
* Fixed the Furnace progress handler
    * Did any of you even know this was a thing? I sure didn't
* Maturity tooltips for crops now work correctly if the crop has a different max age.
* Added config for the format used to display Block, Entity, and Mod names
    * You can now change the color, add other text, etc.
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