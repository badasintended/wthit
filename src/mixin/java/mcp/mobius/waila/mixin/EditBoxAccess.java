package mcp.mobius.waila.mixin;

import java.util.function.Predicate;

import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EditBox.class)
public interface EditBoxAccess {

    @Accessor("value")
    String wthit_value();

    @Accessor("value")
    void wthit_value(String value);

    @Accessor("highlightPos")
    int wthit_highlightPos();

    @Accessor("maxLength")
    int wthit_maxLength();

    @Accessor("filter")
    Predicate<String> wthit_filter();

    @Invoker("onValueChange")
    void wthit_onValueChange(String $$0);

    @Invoker("isEditable")
    boolean wthit_isEditable();

}
