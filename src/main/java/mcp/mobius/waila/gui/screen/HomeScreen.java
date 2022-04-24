package mcp.mobius.waila.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

public class HomeScreen extends Screen {

    private final Screen parent;

    public HomeScreen(Screen parent) {
        super(new TextComponent(WailaConstants.MOD_NAME));

        this.parent = parent;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void init() {
        addRenderableWidget(new Button(width / 2 - 100, height / 2 - 48, 200, 20, new TranslatableComponent("gui.waila.waila_settings", WailaConstants.MOD_NAME), w ->
            minecraft.setScreen(new WailaConfigScreen(this))));
        addRenderableWidget(new Button(width / 2 - 100, height / 2 - 24, 200, 20, new TranslatableComponent("gui.waila.plugin_settings"), w ->
            minecraft.setScreen(new PluginConfigScreen(this))));
        addRenderableWidget(new Button(width / 2 - 100, height / 2, 200, 20, new TranslatableComponent("gui.waila.credits"), w ->
            minecraft.setScreen(new CreditsScreen(this))));
        addRenderableWidget(new Button(width / 2 - 50, height / 2 + 24, 100, 20, new TranslatableComponent("gui.done"), w -> {
            Waila.CONFIG.save();
            PluginConfig.INSTANCE.save();
            minecraft.setScreen(parent);
        }));
    }

    @Override
    public void render(@NotNull PoseStack matrices, int x, int y, float partialTicks) {
        renderBackground(matrices);
        drawCenteredString(matrices, font, title.getString(), width / 2, height / 2 - 50 - font.lineHeight, 0xFFFFFF);
        super.render(matrices, x, y, partialTicks);
    }

}
