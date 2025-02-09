package mcp.mobius.waila.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccess {

    @Accessor("bufferSource")
    MultiBufferSource.BufferSource wthit_bufferSource();

}
