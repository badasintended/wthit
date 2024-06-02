package mcp.mobius.waila.plugin.test;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITargetRedirector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public enum RedirectTest implements IBlockComponentProvider {

    INSTANCE;

    public static final ResourceLocation TARGET = ResourceLocation.parse("test:redirect.target");

    public enum Target {
        NONE, SELF, BEHIND, HIT, NOWHERE
    }

    @Override
    public @Nullable ITargetRedirector.Result redirect(ITargetRedirector redirect, IBlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlock() != Blocks.BLACK_WOOL) return null;

        Target target = config.getEnum(TARGET);
        return switch (target) {
            case NONE -> null;
            case SELF -> redirect.toSelf();
            case BEHIND -> redirect.toBehind();
            case HIT -> redirect.to(accessor.getBlockHitResult().withPosition(accessor.getPosition().above()));
            case NOWHERE -> redirect.toNowhere();
        };
    }

}
