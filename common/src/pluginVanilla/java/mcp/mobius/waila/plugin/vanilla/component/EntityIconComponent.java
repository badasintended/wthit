package mcp.mobius.waila.plugin.vanilla.component;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum EntityIconComponent implements IEntityComponentProvider {

    INSTANCE;

    static final Map<AbstractMinecart.Type, ItemStack> MINECART_STACKS = Util.make(new Object2ObjectOpenHashMap<>(), m -> {
        m.put(AbstractMinecart.Type.RIDEABLE, new ItemStack(Items.MINECART));
        m.put(AbstractMinecart.Type.CHEST, new ItemStack(Items.CHEST_MINECART));
        m.put(AbstractMinecart.Type.FURNACE, new ItemStack(Items.FURNACE_MINECART));
        m.put(AbstractMinecart.Type.HOPPER, new ItemStack(Items.HOPPER_MINECART));
        m.put(AbstractMinecart.Type.TNT, new ItemStack(Items.CHEST_MINECART));
        m.put(AbstractMinecart.Type.COMMAND_BLOCK, new ItemStack(Items.COMMAND_BLOCK_MINECART));
        m.defaultReturnValue(ItemStack.EMPTY);
    });

    static final Map<Boat.Type, ItemStack> BOAT_STACKS = Util.make(new Object2ObjectOpenHashMap<>(), m -> {
        m.put(Boat.Type.OAK, new ItemStack(Items.OAK_BOAT));
        m.put(Boat.Type.SPRUCE, new ItemStack(Items.SPRUCE_BOAT));
        m.put(Boat.Type.BIRCH, new ItemStack(Items.BIRCH_BOAT));
        m.put(Boat.Type.JUNGLE, new ItemStack(Items.JUNGLE_BOAT));
        m.put(Boat.Type.ACACIA, new ItemStack(Items.ACACIA_BOAT));
        m.put(Boat.Type.DARK_OAK, new ItemStack(Items.DARK_OAK_BOAT));
        m.defaultReturnValue(ItemStack.EMPTY);
    });

    static final ItemStack ITEM_FRAME_STACK = new ItemStack(Items.ITEM_FRAME);
    static final ItemStack PAINTING_STACK = new ItemStack(Items.PAINTING);
    static final ItemStack LEAD_STACK = new ItemStack(Items.LEAD);

    @Override
    public ItemStack getDisplayItem(IEntityAccessor accessor, IPluginConfig config) {
        Entity entity = accessor.getEntity();
        if (entity instanceof AbstractMinecart) {
            return MINECART_STACKS.get(((AbstractMinecart) entity).getMinecartType());
        } else if (entity instanceof ItemFrame) {
            ItemStack held = ((ItemFrame) entity).getItem();
            return held.isEmpty() ? ITEM_FRAME_STACK : held;
        } else if (entity instanceof Painting) {
            return PAINTING_STACK;
        } else if (entity instanceof LeashFenceKnotEntity) {
            return LEAD_STACK;
        } else if (entity instanceof Boat) {
            return BOAT_STACKS.get(((Boat) entity).getBoatType());
        }
        return ItemStack.EMPTY;
    }

}
