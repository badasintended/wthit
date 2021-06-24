package mcp.mobius.waila.gui;

import java.util.List;

import com.google.common.collect.Lists;
import mcp.mobius.waila.gui.config.OptionsListWidget;
import mcp.mobius.waila.gui.config.value.OptionsEntryValue;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public abstract class GuiOptions extends Screen {

    private final Screen parent;
    private final Runnable saver;
    private final Runnable canceller;
    private OptionsListWidget options;
    private final List<Element> children = (List<Element>) children();

    public GuiOptions(Screen parent, Text title, Runnable saver, Runnable canceller) {
        super(title);

        this.parent = parent;
        this.saver = saver;
        this.canceller = canceller;
    }

    public GuiOptions(Screen parent, Text title) {
        this(parent, title, null, null);
    }

    @Override
    public void init() {
        super.init();

        options = getOptions();
        children.add(options);
        setFocused(options);

        if (saver != null && canceller != null) {
            addDrawableChild(new ButtonWidget(width / 2 - 102, height - 25, 100, 20, new TranslatableText("gui.done"), w -> {
                options.save();
                saver.run();
                onClose();
            }));
            addDrawableChild(new ButtonWidget(width / 2 + 2, height - 25, 100, 20, new TranslatableText("gui.cancel"), w -> {
                canceller.run();
                onClose();
            }));
        } else {
            addDrawableChild(new ButtonWidget(width / 2 - 50, height - 25, 100, 20, new TranslatableText("gui.done"), w -> {
                options.save();
                onClose();
            }));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrices);
        options.render(matrices, mouseX, mouseY, partialTicks);
        drawCenteredText(matrices, textRenderer, title.getString(), width / 2, 12, 16777215);
        super.render(matrices, mouseX, mouseY, partialTicks);

        if (mouseY < 32 || mouseY > height - 32)
            return;

        options.hoveredElement(mouseX, mouseY).ifPresent(element -> {
            if (element instanceof OptionsEntryValue<?> value) {

                if (I18n.hasTranslation(value.getDescription())) {
                    String title = value.getTitle().getString();
                    List<OrderedText> tooltip = Lists.newArrayList(new LiteralText(title).asOrderedText());
                    tooltip.addAll(textRenderer.wrapLines(new TranslatableText(value.getDescription()).formatted(Formatting.GRAY), 200));
                    renderOrderedTooltip(matrices, tooltip, mouseX, mouseY);
                }
            }
        });
    }

    @Override
    public void onClose() {
        client.openScreen(parent);
    }

    public void addListener(Element listener) {
        children.add(listener);
    }

    public abstract OptionsListWidget getOptions();

}
