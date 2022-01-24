package mcp.mobius.waila.plugin.test;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.minecraft.world.level.block.Block;

public class WailaTest implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.addConfig(ConfigTest.ENABLED, true);

        registrar.addConfig(ConfigTest.BOOL, true);
        registrar.addConfig(ConfigTest.INT, 69);
        registrar.addConfig(ConfigTest.DOUBLE, 42.0);
        registrar.addConfig(ConfigTest.STRING, "<empty>");
        registrar.addConfig(ConfigTest.ENUM, TooltipPosition.HEAD);

        registrar.addSyncedConfig(ConfigTest.SYNC_BOOL, true);
        registrar.addSyncedConfig(ConfigTest.SYNC_INT, 69);
        registrar.addSyncedConfig(ConfigTest.SYNC_DOUBLE, 42.0);
        registrar.addSyncedConfig(ConfigTest.SYNC_STRING, "<empty>");
        registrar.addSyncedConfig(ConfigTest.SYNC_ENUM, TooltipPosition.HEAD);

        registrar.addComponent(ConfigTest.INSTANCE, TooltipPosition.HEAD, Block.class);

        registrar.addConfig(OverrideTest.MOD_NAME, false);
        registrar.addComponent(OverrideTest.INSTANCE, TooltipPosition.TAIL, Block.class);

        registrar.addConfig(EventListenerTest.HANDLE_TOOLTIP, false);
        registrar.addConfig(EventListenerTest.BEFORE_RENDER, false);
        registrar.addConfig(EventListenerTest.AFTER_RENDER, false);
        registrar.addConfig(EventListenerTest.ITEM_MOD_NAME, false);
        registrar.addEventListener(EventListenerTest.INSTANCE);

        registrar.addConfig(DeprecatedTest.ENABLED, true);
        registrar.addRenderer(DeprecatedTest.RENDERER, DeprecatedTest.INSTANCE);
        registrar.addComponent(DeprecatedTest.INSTANCE, TooltipPosition.HEAD, Block.class);

        registrar.addConfig(CustomIconTest.ENABLED, true);
        registrar.addIcon(CustomIconTest.INSTANCE, Block.class);

        registrar.addConfig(OffsetTest.ENABLED, true);
        registrar.addComponent(OffsetTest.INSTANCE, TooltipPosition.BODY, Block.class);
    }

}
