package mcp.mobius.waila.addons.vanillamc;

import mcp.mobius.waila.api.IWailaBlockDecorator;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.gui.helpers.UIHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;

public class HUDDecoratorVanilla implements IWailaBlockDecorator {

        @Override
        public void decorateBlock(ItemStack stack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer t = tessellator.getWorldRenderer();

                //UIHelper.drawBillboardText(stack.getDisplayName(), accessor.getRenderingPosition(), 0.5F, 1.5F, 0.5F, accessor.getPartialFrame());
                UIHelper.drawFloatingText("IN", accessor.getRenderingPosition(), 0.5F, 0.2F, -0.2F, 90F, 0F, 0F);
                UIHelper.drawFloatingText("OUT", accessor.getRenderingPosition(), -0.2F, 0.2F, 0.5F, 90F, 90F, 0F);
                UIHelper.drawFloatingText("OUT", accessor.getRenderingPosition(), 1.2F, 0.2F, 0.5F, 90F, -90F, 0F);
                UIHelper.drawFloatingText("OUT", accessor.getRenderingPosition(), 0.5F, 0.2F, 1.2F, 90F, -180F, 0F);

                double offset = 0.1;
                double delta = 1 + 2 * offset;

                double x = accessor.getRenderingPosition().xCoord - offset;
                double y = accessor.getRenderingPosition().yCoord - offset;
                double z = accessor.getRenderingPosition().zCoord - offset;

                t.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

                t.pos(x, y + 0.2, z).endVertex();
                t.pos(x, y + 0.2, z + delta / 2 - 0.1).endVertex();
                t.pos(x + offset, y + 0.2, z + delta / 2 - 0.1).endVertex();
                t.pos(x + offset, y + 0.2, z).endVertex();

                t.pos(x, y + 0.2, z + delta / 2 + 0.1).endVertex();
                t.pos(x, y + 0.2, z + delta).endVertex();
                t.pos(x + offset, y + 0.2, z + delta).endVertex();
                t.pos(x + offset, y + 0.2, z + delta / 2 + 0.1).endVertex();

                t.pos(x + delta - 0.1, y + 0.2, z + 0.1).endVertex();
                t.pos(x + delta - 0.1, y + 0.2, z + delta / 2 - 0.1).endVertex();
                t.pos(x + delta + offset - 0.1, y + 0.2, z + delta / 2 - 0.1).endVertex();
                t.pos(x + delta + offset - 0.1, y + 0.2, z + 0.1).endVertex();

                t.pos(x + delta - 0.1, y + 0.2, z + delta / 2 + 0.1).endVertex();
                t.pos(x + delta - 0.1, y + 0.2, z + delta).endVertex();
                t.pos(x + delta + offset - 0.1, y + 0.2, z + delta).endVertex();
                t.pos(x + delta + offset - 0.1, y + 0.2, z + delta / 2 + 0.1).endVertex();


                t.pos(x + 0.1, y + 0.2, z).endVertex();
                t.pos(x + 0.1, y + 0.2, z + offset).endVertex();
                t.pos(x + delta / 2 - 0.1, y + 0.2, z + offset).endVertex();
                t.pos(x + delta / 2 - 0.1, y + 0.2, z).endVertex();

                t.pos(x + delta / 2 + 0.1, y + 0.2, z).endVertex();
                t.pos(x + delta / 2 + 0.1, y + 0.2, z + offset).endVertex();
                t.pos(x + delta, y + 0.2, z + offset).endVertex();
                t.pos(x + delta, y + 0.2, z).endVertex();

                t.pos(x + 0.1, y + 0.2, z + delta - 0.1).endVertex();
                t.pos(x + 0.1, y + 0.2, z + offset + delta - 0.1).endVertex();
                t.pos(x + delta / 2 - 0.1, y + 0.2, z + offset + delta - 0.1).endVertex();
                t.pos(x + delta / 2 - 0.1, y + 0.2, z + delta - 0.1).endVertex();

                t.pos(x + delta / 2 + 0.1, y + 0.2, z + delta - 0.1).endVertex();
                t.pos(x + delta / 2 + 0.1, y + 0.2, z + offset + delta - 0.1).endVertex();
                t.pos(x + delta - 0.1, y + 0.2, z + offset + delta - 0.1).endVertex();
                t.pos(x + delta - 0.1, y + 0.2, z + delta - 0.1).endVertex();

                tessellator.draw();
        }
}