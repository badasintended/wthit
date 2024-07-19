package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;

/**
 * Since Mojang decided to hardcode the boat type using {@link Boat.Type},
 * mods will use one of the enum and override the {@link Boat#getPickResult()}.
 * This causes the core component method unreliable resulting the object name showing as {@code Minecraft}.
 * <p>
 * For that, we'll be using the item name instead.
 */
public enum BoatProvider implements IEntityComponentProvider {

    INSTANCE;

    ItemStack stack;

    @Override
    public void appendHead(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        //noinspection DataFlowIssue
        stack = accessor.<Boat>getEntity().getPickResult();

        var formatter = IWailaConfig.get().getFormatter();
        tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.entityName(stack.getHoverName().getString()));

        if (config.getBoolean(WailaConstants.CONFIG_SHOW_REGISTRY)) {
            tooltip.setLine(WailaConstants.REGISTRY_NAME_TAG, (formatter.registryName(BuiltInRegistries.ITEM.getKey(stack.getItem()))));
        }
    }

    @Override
    public void appendTail(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(WailaConstants.CONFIG_SHOW_MOD_NAME)) {
            tooltip.setLine(WailaConstants.MOD_NAME_TAG, IWailaConfig.get().getFormatter().modName(IModInfo.get(stack).getName()));
        }
    }

}
