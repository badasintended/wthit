# Overrides

If you have special blocks that shows itself as another block (example: facades),
you would want that block to show the imitated block on WTHIT as well. 
To do that, you need to register a block override provider.

## Block Override
In this example we wanted to show powder snow block as regular snow block.

First create a class that implements `IBlockComponentProvider` and override the `getOverride` method.
In there you return the block state that you want to immitate into.
=== "Yarn"
    ```java
    public class BlockOverride implements IBlockComponentProvider {
      @Override
      public BlockState getOverride(IBlockAccessor accessor, IPluginConfig config) {
        return Blocks.SNOW_BLOCK.getDefaultState();
      }
    }
    ```
=== "Mojang"
    ```java
    public class BlockOverride implements IBlockComponentProvider {
      @Override
      public BlockState getOverride(IBlockAccessor accessor, IPluginConfig config) {
        return Blocks.SNOW_BLOCK.defaultBlockState();
      }
    }
    ```

Then you register the class on your main plugin class.
```java
public class MyWailaPlugin implements IWailaPlugin {
  @Override
  public void register(IRegistrar registrar) {
      registrar.addOverride(new BlockOverride(), PowderSnowBlock.class);
  }
}
```
??? note "Priority"
    WTHIT will choose the first override with lower priority number.
    `a <= b ? a : b`


## Entity Override

It's also applicable for entities, simply make a class implementing `IEntityComponentProvider`
```java
public class EntityOverride implements IEntityComponentProvider {
  @Override
  public Entity getOverride(IEntityAccessor accessor, IPluginConfig config) {
    return EntityType.SHEEP.create(accessor.getWorld());
  }
}
```

```java
public class MyWailaPlugin implements IWailaPlugin {
  @Override
  public void register(IRegistrar registrar) {
      registrar.addOverride(new EntityOverride(), PigEntity.class);
  }
}
```
??? danger "Caching"
    Unlike `BlockState`, an `Entity` instance is not cached on any way. This can possibly cause
    performance issue if you have a many overrides. To mimimalize this, you need to cache it yourself.
    Since `*ComponentProvider` is a client-only class, caching based on world and/or position should 
    be enough for most cases.
