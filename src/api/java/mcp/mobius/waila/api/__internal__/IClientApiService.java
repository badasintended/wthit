package mcp.mobius.waila.api.__internal__;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

/** @hidden */
@ApiStatus.Internal
public interface IClientApiService {

    IClientApiService INSTANCE = Internals.loadService(IClientApiService.class);

    void renderComponent(PoseStack matrices, ITooltipComponent component, int x, int y, float delta);

    void fillGradient(Matrix4f matrix, VertexConsumer buf, int x, int y, int w, int h, int start, int end);

    void renderRectBorder(Matrix4f matrix, VertexConsumer buf, int x, int y, int w, int h, int s, int gradStart, int gradEnd);

    void renderItem(int x, int y, ItemStack stack);

    IWailaConfig getConfig();

}
