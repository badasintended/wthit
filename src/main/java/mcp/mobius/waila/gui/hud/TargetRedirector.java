package mcp.mobius.waila.gui.hud;

import mcp.mobius.waila.api.ITargetRedirector;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class TargetRedirector implements ITargetRedirector {

    private static final TargetRedirector INSTANCE = new TargetRedirector();
    private static final Result RESULT = new Result();

    public boolean self, nowhere, behind;
    public @Nullable HitResult to;

    public static TargetRedirector get() {
        INSTANCE.self = false;
        INSTANCE.nowhere = false;
        INSTANCE.behind = false;
        INSTANCE.to = null;
        return INSTANCE;
    }

    @Override
    public Result toSelf() {
        self = true;
        return RESULT;
    }

    @Override
    public Result toNowhere() {
        nowhere = true;
        return RESULT;
    }

    @Override
    public Result toBehind() {
        behind = true;
        return RESULT;
    }

    @Override
    public Result to(HitResult hitResult) {
        to = hitResult;
        return RESULT;
    }

    public static class Result implements ITargetRedirector.Result {

    }

}
