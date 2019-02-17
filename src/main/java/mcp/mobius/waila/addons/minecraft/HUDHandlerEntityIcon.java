package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class HUDHandlerEntityIcon implements IEntityComponentProvider {

    public static final IEntityComponentProvider INSTANCE = new HUDHandlerEntityIcon();

    @Override
    public ItemStack getDisplayItem(IEntityAccessor accessor, IPluginConfig config) {
        if (accessor.getEntity() instanceof EntityMinecart) {
            EntityMinecart minecartEntity = (EntityMinecart) accessor.getEntity();
            EntityMinecart.Type type = minecartEntity.getMinecartType();
            switch (type) {
                case RIDEABLE:
                    return new ItemStack(Items.MINECART);
                case CHEST:
                    return new ItemStack(Items.CHEST_MINECART);
                case FURNACE:
                    return new ItemStack(Items.FURNACE_MINECART);
                case HOPPER:
                    return new ItemStack(Items.HOPPER_MINECART);
                case TNT:
                    return new ItemStack(Items.TNT_MINECART);
                case COMMAND_BLOCK:
                    return new ItemStack(Items.COMMAND_BLOCK_MINECART);
            }
        } else if (accessor.getEntity() instanceof EntityItemFrame) {
            ItemStack held = ((EntityItemFrame) accessor.getEntity()).getDisplayedItem();
            return held.isEmpty() ? new ItemStack(Items.ITEM_FRAME) : held;
        } else if (accessor.getEntity() instanceof EntityPainting) {
            return new ItemStack(Items.PAINTING);
        } else if (accessor.getEntity() instanceof EntityLeashKnot) {
            return new ItemStack(Items.LEAD);
        }
        return ItemStack.EMPTY;
    }
}
