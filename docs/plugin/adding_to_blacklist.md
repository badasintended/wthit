# Adding to Blacklist

Unlike overriding object to disable the tooltip, adding it 
to the blacklist means the player can override those values.

You can add blocks, block entity types, and entity types to the blacklist.

```java
public class MyWailaPlugin implements IWailaPlugin {
  @Override
  public void register(IRegistrar registrar) {
      registrar.addBlacklist(
        Blocks.OAK_PLANKS,
        Blocks.CRAFTING_TABLE
      );

      registrar.addBlacklist(
        EntityType.MINECART
      );
  }
}
```

## Using tag to add to blacklist
This method only viable if your mod also adds new blocks and/or entities since this
would require the tag to be present on the server. Create a tag called `waila:blacklist`.

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
