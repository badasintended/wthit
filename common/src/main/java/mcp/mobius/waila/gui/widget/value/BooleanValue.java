package mcp.mobius.waila.gui.widget.value;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.TranslatableComponent;

public class BooleanValue extends ConfigValue<Boolean> {

    private final Button button;

    public BooleanValue(String optionName, boolean value, Consumer<Boolean> save) {
        super(optionName, save);

        this.button = new Button(0, 0, 100, 20, new TranslatableComponent("gui." + (value ? "yes" : "no")), w ->
            this.value = !this.value);
        this.value = value;
    }

    @Override
    protected void drawValue(PoseStack matrices, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        this.button.x = x + width - button.getWidth();
        this.button.y = y + (height - button.getHeight()) / 2;
        this.button.setMessage(new TranslatableComponent("gui." + (value ? "yes" : "no")));
        this.button.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public GuiEventListener getListener() {
        return button;
    }

}
