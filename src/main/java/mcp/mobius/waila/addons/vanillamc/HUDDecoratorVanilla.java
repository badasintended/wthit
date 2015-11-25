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

                t.func_181668_a(7, DefaultVertexFormats.field_181705_e);

                //t.setColorRGBA(255, 255, 255, 150); //TODO Fix color

                t.func_181662_b(x, y + 0.2, z).func_181675_d();
                t.func_181662_b(x, y + 0.2, z + delta / 2 - 0.1).func_181675_d();
                t.func_181662_b(x + offset, y + 0.2, z + delta / 2 - 0.1).func_181675_d();
                t.func_181662_b(x + offset, y + 0.2, z).func_181675_d();

                t.func_181662_b(x, y + 0.2, z + delta / 2 + 0.1).func_181675_d();
                t.func_181662_b(x, y + 0.2, z + delta).func_181675_d();
                t.func_181662_b(x + offset, y + 0.2, z + delta).func_181675_d();
                t.func_181662_b(x + offset, y + 0.2, z + delta / 2 + 0.1).func_181675_d();

                t.func_181662_b(x + delta - 0.1, y + 0.2, z + 0.1).func_181675_d();
                t.func_181662_b(x + delta - 0.1, y + 0.2, z + delta / 2 - 0.1).func_181675_d();
                t.func_181662_b(x + delta + offset - 0.1, y + 0.2, z + delta / 2 - 0.1).func_181675_d();
                t.func_181662_b(x + delta + offset - 0.1, y + 0.2, z + 0.1).func_181675_d();

                t.func_181662_b(x + delta - 0.1, y + 0.2, z + delta / 2 + 0.1).func_181675_d();
                t.func_181662_b(x + delta - 0.1, y + 0.2, z + delta).func_181675_d();
                t.func_181662_b(x + delta + offset - 0.1, y + 0.2, z + delta).func_181675_d();
                t.func_181662_b(x + delta + offset - 0.1, y + 0.2, z + delta / 2 + 0.1).func_181675_d();


                t.func_181662_b(x + 0.1, y + 0.2, z).func_181675_d();
                t.func_181662_b(x + 0.1, y + 0.2, z + offset).func_181675_d();
                t.func_181662_b(x + delta / 2 - 0.1, y + 0.2, z + offset).func_181675_d();
                t.func_181662_b(x + delta / 2 - 0.1, y + 0.2, z).func_181675_d();

                t.func_181662_b(x + delta / 2 + 0.1, y + 0.2, z).func_181675_d();
                t.func_181662_b(x + delta / 2 + 0.1, y + 0.2, z + offset).func_181675_d();
                t.func_181662_b(x + delta, y + 0.2, z + offset).func_181675_d();
                t.func_181662_b(x + delta, y + 0.2, z).func_181675_d();

                t.func_181662_b(x + 0.1, y + 0.2, z + delta - 0.1).func_181675_d();
                t.func_181662_b(x + 0.1, y + 0.2, z + offset + delta - 0.1).func_181675_d();
                t.func_181662_b(x + delta / 2 - 0.1, y + 0.2, z + offset + delta - 0.1).func_181675_d();
                t.func_181662_b(x + delta / 2 - 0.1, y + 0.2, z + delta - 0.1).func_181675_d();

                t.func_181662_b(x + delta / 2 + 0.1, y + 0.2, z + delta - 0.1).func_181675_d();
                t.func_181662_b(x + delta / 2 + 0.1, y + 0.2, z + offset + delta - 0.1).func_181675_d();
                t.func_181662_b(x + delta - 0.1, y + 0.2, z + offset + delta - 0.1).func_181675_d();
                t.func_181662_b(x + delta - 0.1, y + 0.2, z + delta - 0.1).func_181675_d();

                tessellator.draw();
        }
}