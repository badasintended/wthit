package mcp.mobius.waila.plugin.vanilla;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Util;

public enum EntityIconComponent implements IEntityComponentProvider {

    INSTANCE;

    static final Map<AbstractMinecartEntity.Type, ItemStack> MINECART_STACKS = Util.make(new Object2ObjectOpenHashMap<>(), m -> {
        m.put(AbstractMinecartEntity.Type.RIDEABLE, new ItemStack(Items.MINECART));
        m.put(AbstractMinecartEntity.Type.CHEST, new ItemStack(Items.CHEST_MINECART));
        m.put(AbstractMinecartEntity.Type.FURNACE, new ItemStack(Items.FURNACE_MINECART));
        m.put(AbstractMinecartEntity.Type.HOPPER, new ItemStack(Items.HOPPER_MINECART));
        m.put(AbstractMinecartEntity.Type.TNT, new ItemStack(Items.CHEST_MINECART));
        m.put(AbstractMinecartEntity.Type.COMMAND_BLOCK, new ItemStack(Items.COMMAND_BLOCK_MINECART));
        m.defaultReturnValue(ItemStack.EMPTY);
    });

    static final Map<BoatEntity.Type, ItemStack> BOAT_STACKS = Util.make(new Object2ObjectOpenHashMap<>(), m -> {
        m.put(BoatEntity.Type.OAK, new ItemStack(Items.OAK_BOAT));
        m.put(BoatEntity.Type.SPRUCE, new ItemStack(Items.SPRUCE_BOAT));
        m.put(BoatEntity.Type.BIRCH, new ItemStack(Items.BIRCH_BOAT));
        m.put(BoatEntity.Type.JUNGLE, new ItemStack(Items.JUNGLE_BOAT));
        m.put(BoatEntity.Type.ACACIA, new ItemStack(Items.ACACIA_BOAT));
        m.put(BoatEntity.Type.DARK_OAK, new ItemStack(Items.DARK_OAK_BOAT));
        m.defaultReturnValue(ItemStack.EMPTY);
    });

    static final ItemStack ITEM_FRAME_STACK = new ItemStack(Items.ITEM_FRAME);
    static final ItemStack PAINTING_STACK = new ItemStack(Items.PAINTING);
    static final ItemStack LEAD_STACK = new ItemStack(Items.LEAD);

    @Override
    public ItemStack getDisplayItem(IEntityAccessor accessor, IPluginConfig config) {
        Entity entity = accessor.getEntity();
        if (entity instanceof AbstractMinecartEntity) {
            return MINECART_STACKS.get(((AbstractMinecartEntity) entity).getMinecartType());
        } else if (entity instanceof ItemFrameEntity) {
            ItemStack held = ((ItemFrameEntity) entity).getHeldItemStack();
            return held.isEmpty() ? ITEM_FRAME_STACK : held;
        } else if (entity instanceof PaintingEntity) {
            return PAINTING_STACK;
        } else if (entity instanceof LeashKnotEntity) {
            return LEAD_STACK;
        } else if (entity instanceof BoatEntity) {
            return BOAT_STACKS.get(((BoatEntity) entity).getBoatType());
        }
        return ItemStack.EMPTY;
    }

}
