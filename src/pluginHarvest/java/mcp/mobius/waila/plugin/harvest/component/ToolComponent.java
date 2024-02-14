package mcp.mobius.waila.plugin.harvest.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@ApiSide.ClientOnly
public class ToolComponent extends GuiComponent implements ITooltipComponent {

    private final @Nullable ItemStack icon;

    private final int v0;
    private final int width;
    private final int xo;

    private int x;

    public ToolComponent(@Nullable ItemStack icon, @Nullable Boolean matches) {
        this.icon = icon;

        if (matches != null) v0 = matches ? 7 : 0;
        else v0 = -1;

        if (icon == null) {
            width = 6;
            xo = 0;
        } else {
            width = 9;
            xo = 3;
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return 9;
    }

    @Override
    public void render(PoseStack matrices, int x, int y, float delta) {
        this.x = x;
    }

    public void actuallyRender(PoseStack matrices, int y) {
        if (icon != null) {
            var itemMatrices = RenderSystem.getModelViewStack();
            itemMatrices.pushPose();
            itemMatrices.translate(-2, -2, 0);
            itemMatrices.scale(0.85f, 0.85f, 1f);
            itemMatrices.translate(x / 0.85f, y / 0.85f, 0);
            Minecraft.getInstance().getItemRenderer().renderGuiItem(icon, 0, 0);
            itemMatrices.popPose();
        }

        if (v0 == -1) return;
        matrices.pushPose();
        matrices.translate(0, 0, ItemRenderer.ITEM_COUNT_BLIT_OFFSET);
        RenderSystem.setShaderTexture(0, WailaConstants.COMPONENT_TEXTURE);
        blit(matrices, x + xo, y + 3, 122, v0, 7, 7);
        matrices.popPose();
    }

}
