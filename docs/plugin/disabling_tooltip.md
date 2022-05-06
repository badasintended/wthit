# Disabling Tooltip

## Disabling Tooltip for Certain Objects
Sometimes you want to disable the tooltip for showing altogether.
To do that, WTHIT contains a static variable on `I*ComponentProvider`
that we can use as a return value for `getOverride`.

```java
public class BlockOverride implements IBlockComponentProvider {
  @Override
  public BlockState getOverride(IBlockAccessor accessor, IPluginConfig config) {
    return EMPTY_BLOCK_STATE;
  }
}
```

```java
public class EntityOverride implements IEntityComponentProvider {
  @Override
  public Entity getOverride(IEntityAccessor accessor, IPluginConfig config) {
    return EMPTY_ENTITY;
  }
}
```
