package mcp.mobius.waila.jaded.j2w;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.Jade;

public class JPostWailaPlugin implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        Jade.FROZEN = false;
        Jade.loadComplete();

        registrar.addIcon(JBlockComponentProvider.INSTANCE, Block.class);
        registrar.addComponent(JBlockComponentProvider.INSTANCE, TooltipPosition.BODY, Block.class);
        registrar.addBlockData(JBlockComponentProvider.INSTANCE, BlockEntity.class);

        registrar.addEntityData(JEntityComponentProvider.INSTANCE, Entity.class);
    }

}
