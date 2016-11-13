# HWYLA Changelog

#### This is a list of primary changes between HWYLA and WAILA itself as of the creation of this fork.

###### Please note that this list may be incomplete or out-dated. You can view a full list of changes within the commit history.

---

### Introduced a new plugin system.
This feature functions on-top of the existing registration system.

* All core handlers are now managed as plugins.

* Handlers are registered manually to make sure they are loaded first.

* Added a plugin registration system.
  * Can be accessed by using `@WailaPlugin` on an instance of `IWailaPlugin`.
  * The annotation can take a String value for the required `modid`.
  * The String value can be left blank for `any`.


*In-built functionality has been removed for mods that have not updated to Minecraft 1.9 or newer.*

---
### Re-worked fluid tooltips.
When enabled, HWYLA will attempt to display the type of fluid the player is looking at, alongside it's Forge bucket model.

---
### Added native support for Forge Capabilities.
Information is obtained using a `null` facing. Mods that do not handle this correctly are unsupported.

* #### Tank fluid contents.

  * Displays the fluid type, currently stored amount in mB, and the total capacity of the tank.
  * Limited to 5 tanks per tooltip display to avoid potential issues.
  * This feature only works on tanks that use the Forge Capabilities system.


* #### Item contents of an inventory.

  * This feature will function on any block that:

    * Implements `IInventory`

    * Provides `IItemHandler`

    * Or is an `instanceof TileEntityEnderChest`

  * Displays a maximum of 5 items in the tooltip by default. When sneaking, the full inventory will be displayed.
  * Multiple stacks of an item are condensed to their total value.
    * *For example, 2 stacks of 64 items will be displayed as 128 items in the tooltip.*
  * When a count reaches a high enough number, it will be condensed down.
    * *For example, 1337 of an item will be displayed as 1.3K of an item.*


* #### Forge Energy storage and capacity.

    * Displays the blocks currently stored energy and maximum capacity.
    * This feature only works on energy handlers that provide `IEnergyStorage`.

*These features are disabled by default to prevent conflicts with other mods that introduce similar functionality.*

---
### Implemented the existing furnace handler.
Did any of you even know this was a thing? I sure didn't.

* Displays item stacks for the input, fuel, and output.
* Renders an arrow to show the progress of the current item being smelted.
* You don't ever need to open the furnace GUI again. Technology is wonderful.

*This feature is disabled by default to prevent conflicts with other mods that introduce similar functionality.*

---
### Fixed crop maturity tooltips.
This feature now works correctly when the crop has a non-standard maximum age.

---
### Introduced a string formatting system for Blocks, Entities, and Mod Names.
You can now wrap custom text around the tooltip strings, with support for [MOTD-like formatting codes](http://minecraft.gamepedia.com/Formatting_codes#Use_in_server.properties_and_pack.mcmeta).

*Due to limitations presented by item rarity colouring, the block name itself will always be a fixed colour, and also reset the formatting after itself. To avoid the format resetting, reapply your format codes after `%s` in the config string.*

---
### Fixed the ID + Metadata information display.
You will see useful information instead of `< UNIMPLEMENTED >` in your tooltips now.

---
### Re-implemented recipe and usage lookups.

Provides quick in-world access to recipes and/or usages of the block you're looking at.

* Recipe and usage key-binds are assigned to `NUM3` and `NUM4` by default.
* This feature requires the mod "Just Enough Items" to function. You can find it [here](https://minecraft.curseforge.com/projects/just-enough-items-jei).

*The out-dated NEI handler has since been removed.*

---
### Changed the ItemStack lookup method to use `getPickBlock(...)`.
Everyone in the developer community assumed it was already handled this way. Nope.

---
### Re-formatted the `/dumphandlers` command.
Now prints the dump text to `WailaHandlerDump.md` to the instance's root - formatted as [Markdown](https://en.wikipedia.org/wiki/Markdown) for increased readability.

---
### Removed various code segments.
Relevant notes on the general code clean-up that was done.

* Vanilla special casing has been stripped out, it has now been fixed to work *properly*.
* Core-mod related code has been deleted. It was pretty much unused and out-dated.
