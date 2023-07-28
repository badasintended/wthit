# Syncing Server Data

???+ danger
     This section uses Mojang Mappings.

By default, `I*ComponentProvider` can only access client data. To access server data, you need to
sync the data from the server. To do that, WTHIT provides an `IDataProvider` interface.

`IDataProvider` provides a way to sync server data via either an NBT object for simple data, or
specific `IData` object.

## Simple NBT Data

Using NBT data is pretty straightforward, in your `IDataProvider` implementation call
`IDataWriter#raw` and modify the returned NBT object.
```java
public class SimpleDataProvider implements IDataProvider<MyBlockEntity> {
    @Override
    public void appendData(IDataWriter data, IServerAccessor<MyBlockEntity> accessor, IPluginConfig config) {
        data.raw().putInt("someInt", accessor.getTarget().getSomeInt());
    }
}
```

Then in your `I*ComponentProvider` implementation, call `IDataReader#raw` to get the synced data
```java
public class SimpleComponentProvider implements IBlockComponentProvider {
    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getData().raw();
        if (data.contains("someInt")) {
            int someInt = data.getInt("someInt");
        }
    }
}
```

???+ warning "Data Key"
     Use a uniqe key to identify your data as it may conflict with data from other mod's provider.


## Complex Data Type

`IData` interface allows synced data to have type safety when modifying/accessing it. It also
allows implementations to decide how the data is structured directly in the packet byte buffer.


## Built-in Data Types

WTHIT contains built-in data types that can be used to attach informations in a standarized way.
The built-in data types that WTHIT provides are `EnergyData`, `FluidData`, `ItemData`, and 
`ProgressData`.

### `EnergyData`
`EnergyData` provides information regarding target object's energy storage. By default, it supports 
the modding platform standard APIs:

- Forge `IEnergyStorage` Capability
- Team Reborn Energy

`EnergyData#describe` can be used to customize how the energy data is shown in the client.

### `FluidData`
`FluidData` provides information regarding target object's fluid storage. By default, it supports
the modding platform standard APIs:

- Forge `IFluidHandler` Capability
- Fabric Transfer API

`FluidData#describeFluid` can be used to customize how the fluid is shown in the client, but if you
already used the standard API it should already work out of the box.

`FluidData#describeCauldron` can be used if you have a custom blockstate-based cauldron for your
fluids. Not needed on Fabric as Fabric API's `CauldronFluidContent` already provides this 
functionality.

### `ItemData`
`ItemData` provides information regarding target object's item storage. By default, it supports the
modding platform standard APIs:

- Forge `IItemHandler` Capability
- Fabric Transfer API
- Vanilla `Container`

### `ProgressData`
`ProgressData` provides information regarding target object's crafting progress status. This is the
only data type that needs explicit implementation for the target object.

???+ warning "Disabling built-in data for your object"
     You can use the block methods from the section below to block the data, or you can use datapack
     tags to do so:

     | Data Type      | Tag ID                           |    
     | -------------- | -------------------------------- |    
     | `EnergyData`   | `waila:extra/energy_blacklist`   |    
     | `FluidData`    | `waila:extra/fluid_blacklist`    |
     | `ItemData`     | `waila:extra/item_blacklist`     |    
     | `ProgressData` | `waila:extra/progress_blacklist` |    

     The tags is available for blocks, block entity types, and entity types.


## Attaching Complex Data

First check if your objects already show some data and if the data are correct. To attach the data
call `IDataWriter#add` with the type that you want to attach.
```java
public class ComplexDataProvider implements IDataProvider<MyBlockEntity> {
    @Override
    public void appendData(IDataWriter data, IServerAccessor<MyBlockEntity> accessor, IPluginConfig config) {
        data.add(EnergyData.class, res -> {
            MyBlockEntity be = accessor.getTarget();
            res.add(EnergyData.of(be.getStoredEnergy(), be.getEnergyCapacity()));
        });
    }
}
```

`IDataWriter.Result#block` can be used to block the data from being synced to the client. 
WTHIT also provides the `IDataWriter#blockAll` method and `BlockingDataProvider` class that can be
used to block all data unconditionally.


## Creating Custom Data Type

First, create an object that implements the `IData` interface. You also need to create a 
constructor that accepts a buffer for the client side.
```java
public class StringData implements IData {
    private final String value;

    public StringData(String value) {
        this.value = value;
    }

    public StringData(FriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(value);
    }
}
```

Then, register the data type in your main plugin class.
```java
public class MyWailaPlugin implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.addDataType(new ResourceLocation("mymod:str"), 
                StringData.class, StringData::new);
    }
}
```


## Getting Complex Data on Client

???+ warning
     Note that built-in data types are not designed to be retreived by other mods on the client,
     they simply for attaching data on the server.

Getting complex data is actually simpler as you don't have to deal with string key.
To get the data on the client, simply call `IDataReader#get` with the type of data that you want.

```java
public class SimpleComponentProvider implements IBlockComponentProvider {
    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        StringData data = accessor.getData().get(StringData.class);
        if (data != null) {
            // do something with data
        }
    }
}
```
