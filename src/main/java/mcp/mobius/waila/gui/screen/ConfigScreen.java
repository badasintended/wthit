package mcp.mobius.waila.gui.screen;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import mcp.mobius.waila.gui.widget.value.ConfigValue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static mcp.mobius.waila.util.DisplayUtil.createButton;

public abstract class ConfigScreen extends YesIAmSureTheClientInstanceIsPresentByTheTimeIUseItScreen {

    protected final Screen parent;
    protected final @Nullable Runnable saver;
    protected final @Nullable Runnable canceller;

    private boolean showEscWarning = true;
    private long lastEscPressTime = 0;
    private int escPressed = 0;

    @SuppressWarnings("unchecked")
    private final List<GuiEventListener> children = (List<GuiEventListener>) children();
    protected ConfigListWidget options;

    protected boolean cancelled;

    public ConfigScreen(Screen parent, Component title, @Nullable Runnable saver, @Nullable Runnable canceller) {
        super(title);

        this.parent = parent;
        this.saver = saver;
        this.canceller = canceller;
    }

    public ConfigScreen(Screen parent, Component title) {
        this(parent, title, null, null);
    }

    @Override
    public void init() {
        super.init();

        if (options == null) {
            options = getOptions();
        }

        options.init();
        addWidget(options);

        if (saver != null && canceller != null) {
            addRenderableWidget(createButton(width / 2 - 102, height - 25, 100, 20, CommonComponents.GUI_DONE, w -> {
                if (options.save(false)) {
                    saver.run();
                    onClose();
                }
            }));
            addRenderableWidget(createButton(width / 2 + 2, height - 25, 100, 20, CommonComponents.GUI_CANCEL, w -> {
                cancelled = true;
                canceller.run();
                onClose();
            }));
        } else {
            addRenderableWidget(createButton(width / 2 - 50, height - 25, 100, 20, CommonComponents.GUI_DONE, w -> {
                if (options.save(false)) {
                    onClose();
                }
            }));
        }
    }

    @Override
    public void setInitialFocus(GuiEventListener widget) {
        super.setInitialFocus(widget);
    }

    protected void renderForeground(GuiGraphics ctx, int rowLeft, int rowWidth, int mouseX, int mouseY, float partialTicks) {
        ctx.drawString(font, title, rowLeft, 12, 0xFFFFFFFF);
    }

    @Override
    public void tick() {
        options.tick();
    }

    @Override
    public void render(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTicks) {
        super.render(ctx, mouseX, mouseY, partialTicks);

        options.render(ctx, mouseX, mouseY, partialTicks);
        renderForeground(ctx, options.getRowLeft(), options.getRowWidth(), mouseX, mouseY, partialTicks);

        if (mouseY < 32 || mouseY > height - 32) return;

        options.getChildAt(mouseX, mouseY).ifPresent(element -> {
            if (element instanceof ConfigValue<?, ?> value) {
                value.renderTooltip(ctx, mouseX, mouseY);
            }
        });
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (showEscWarning) {
            var now = System.currentTimeMillis();
            if ((now - lastEscPressTime) > 2 * 1000) {
                escPressed = 0;
            }

            lastEscPressTime = now;
            escPressed++;
            if (escPressed > 5) {
                minecraft.getToastManager().addToast(new SystemToast(
                    SystemToast.SystemToastId.PACK_COPY_FAILURE,
                    Component.translatable(Tl.Gui.EscWarning.UMM),
                    Component.translatable(Tl.Gui.EscWarning.LMAO,
                        CommonComponents.GUI_DONE.copy().withStyle(ChatFormatting.GOLD),
                        CommonComponents.GUI_CANCEL.copy().withStyle(ChatFormatting.DARK_PURPLE))
                ));
                showEscWarning = false;
                escPressed = 0;
            }
        }

        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (options.enableSearchBox && event.hasControlDown() && event.key() == InputConstants.KEY_F) {
            options.search();
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        if (event.key() == InputConstants.KEY_ESCAPE) {
            showEscWarning = true;
        }

        return super.keyReleased(event);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    public void addListener(GuiEventListener listener) {
        children.add(listener);
    }

    public abstract ConfigListWidget getOptions();

}
