package mcp.mobius.waila.mixin;

import java.util.function.Predicate;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TextFieldWidget.class)
public interface AccessorTextFieldWidget {

    @Accessor
    int getSelectionStart();

    @Accessor
    int getSelectionEnd();

    @Accessor
    Predicate<String> getTextPredicate();

    @Accessor
    int getMaxLength();

    @Accessor("text")
    void set(String text);

    @Invoker
    void callOnChanged(String text);

}
