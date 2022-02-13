package mcp.mobius.waila.api;

import java.awt.Dimension;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated use {@link IDrawableComponent}
 */
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.19")
public interface IDrawableText extends MutableComponent {

    static IDrawableText of(ResourceLocation id, CompoundTag data) {
        return create().with(id, data);
    }

    static IDrawableText create() {
        return IApiService.INSTANCE.createDrawableText();
    }

    IDrawableText with(ResourceLocation id, CompoundTag data);

    Dimension getSize();

    void render(PoseStack matrices, int x, int y, float delta);

}
