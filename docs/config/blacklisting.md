# Blacklisting Blocks and Entities

## Using Blacklist File
To blacklist object from WTHIT, edit `.minecraft/config/waila/blacklist.json` file.

## Using Tags
Datapack tags can also be used to blacklist objects.
To blacklist some blocks from WTHIT, create a block tag called `waila:blacklist`.
See an entry about datapack tags on Minecraft Wiki [here](https://minecraft.fandom.com/wiki/Tag).

For example, to make stone block blacklisted you make the tag contains the following
=== "`data/waila/tags/blocks/blacklist.json`"
    ```json
    {
      "values": [
        "minecraft:stone"
      ]
    }
    ```

The same can be done for entity types
=== "`data/waila/tags/entity_types/blacklist.json`"
    ```json
    {
      "values": [
        "minecraft:pig"
      ]
    }
    ```
