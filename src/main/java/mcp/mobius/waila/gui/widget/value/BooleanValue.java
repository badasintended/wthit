package mcp.mobius.waila.gui.widget.value;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.buildconst.Tl;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class BooleanValue extends ConfigValue<Boolean> {

    private final Button button;

    public BooleanValue(String optionName, boolean value, @Nullable Boolean defaultValue, Consumer<Boolean> save) {
        super(optionName, value, defaultValue, save);

        this.button = new Button(0, 0, 100, 20, Component.empty(), w -> setValue(!getValue()));
        setMessage();
    }

    @Override
    protected void drawValue(PoseStack matrices, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        setMessage();
        button.active = !isDisabled();
        button.x = x + width - button.getWidth();
        button.y = y + (height - button.getHeight()) / 2;
        button.render(matrices, mouseX, mouseY, partialTicks);
    }

    private void setMessage() {
        button.setMessage(Component.translatable(getValue() ? Tl.Config.TRUE : Tl.Config.FALSE)
            .withStyle(isDisabled() ?
                ChatFormatting.GRAY :
                getValue() ? ChatFormatting.GREEN : ChatFormatting.RED));
    }

    @Override
    protected void buildSearchKey(StringBuilder sb) {
        super.buildSearchKey(sb);
        sb.append(" ").append(getValue());
    }

    @Override
    public GuiEventListener getListener() {
        return button;
    }

}
