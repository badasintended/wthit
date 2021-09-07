package mcp.mobius.waila.api;

import mcp.mobius.waila.impl.Impl;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface IModInfo {

    static IModInfo get(String namespace) {
        return Impl.get(IModInfo.class, namespace);
    }

    static IModInfo get(ResourceLocation id) {
        return get(id.getNamespace());
    }

    static IModInfo get(Block block) {
        return get(Registry.BLOCK.getKey(block));
    }

    static IModInfo get(Item item) {
        return get(Registry.ITEM.getKey(item));
    }

    static IModInfo get(Entity entity) {
        ResourceLocation id = Registry.ENTITY_TYPE.getKey(entity.getType());
        return get(id);
    }

    String getId();

    String getName();

}
