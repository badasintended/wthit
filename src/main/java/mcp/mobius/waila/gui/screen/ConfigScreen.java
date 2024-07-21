package mcp.mobius.waila.gui.screen;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigScreen extends YesIAmSureTheClientInstanceIsPresentByTheTimeIUseItScreen {

    private final Screen parent;
    private final @Nullable Runnable saver;
    private final @Nullable Runnable canceller;

    private boolean showEscWarning = true;
    private long lastEscPressTime = 0;
    private int escPressed = 0;

    @SuppressWarnings("unchecked")
    private final List<GuiEventListener> children = (List<GuiEventListener>) children();
    private ConfigListWidget options;

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

        if (options.enableSearchBox) {
            var searchBox = options.getSearchBox();
            addWidget(searchBox);
            setInitialFocus(searchBox);
        }

        addWidget(options);

        if (saver != null && canceller != null) {
            addRenderableWidget(new Button(width / 2 - 102, height - 25, 100, 20, CommonComponents.GUI_DONE, w -> {
                if (options.save(false)) {
                    saver.run();
                    onClose();
                }
            }));
            addRenderableWidget(new Button(width / 2 + 2, height - 25, 100, 20, CommonComponents.GUI_CANCEL, w -> {
                cancelled = true;
                canceller.run();
                onClose();
            }));
        } else {
            addRenderableWidget(new Button(width / 2 - 50, height - 25, 100, 20, CommonComponents.GUI_DONE, w -> {
                if (options.save(false)) {
                    onClose();
                }
            }));
        }
    }

    protected void renderForeground(PoseStack matrices, int rowLeft, int rowWidth, int mouseX, int mouseY, float partialTicks) {
        drawString(matrices, font, title, rowLeft, 12, 0xFFFFFF);
    }

    @Override
    public void tick() {
        options.tick();
    }

    @Override
    public void render(@NotNull PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrices);
        options.render(matrices, mouseX, mouseY, partialTicks);

        if (options.enableSearchBox) {
            options.getSearchBox().render(matrices, mouseX, mouseY, partialTicks);
        }

        super.render(matrices, mouseX, mouseY, partialTicks);
        renderForeground(matrices, options.getRowLeft(), options.getRowWidth(), mouseX, mouseY, partialTicks);

        if (mouseY < 32 || mouseY > height - 32) {
            return;
        }

        options.getChildAt(mouseX, mouseY).ifPresent(element -> {
            if (element instanceof ConfigListWidget.Entry entry) {
                entry.renderTooltip(this, matrices, mouseX, mouseY, partialTicks);
            }
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (var child : children) {
            if (child instanceof EditBox editBox) {
                editBox.setFocus(false);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
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
                minecraft.getToasts().addToast(new SystemToast(
                    SystemToast.SystemToastIds.PACK_COPY_FAILURE,
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
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_ESCAPE) {
            showEscWarning = true;
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
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
