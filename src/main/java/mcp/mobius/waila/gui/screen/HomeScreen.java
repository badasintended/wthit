package mcp.mobius.waila.gui.screen;

import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.buildconst.Tl;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static mcp.mobius.waila.util.DisplayUtil.createButton;

public class HomeScreen extends Screen {

    private final Screen parent;

    public HomeScreen(Screen parent) {
        super(Component.literal(WailaConstants.MOD_NAME));

        this.parent = parent;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void init() {
        addRenderableWidget(createButton(width / 2 - 100, height / 2 - 48, 200, 20, Component.translatable(Tl.Gui.WAILA_SETTINGS, WailaConstants.MOD_NAME), w ->
            minecraft.setScreen(new WailaConfigScreen(this))));
        addRenderableWidget(createButton(width / 2 - 100, height / 2 - 24, 200, 20, Component.translatable(Tl.Gui.PLUGIN_SETTINGS), w ->
            minecraft.setScreen(new PluginConfigScreen(this))));
        addRenderableWidget(createButton(width / 2 - 100, height / 2, 200, 20, Component.translatable(Tl.Gui.CREDITS), w ->
            minecraft.setScreen(new CreditsScreen(this))));
        addRenderableWidget(createButton(width / 2 - 50, height / 2 + 24, 100, 20, CommonComponents.GUI_DONE, w ->
            minecraft.setScreen(parent)));
    }

    @Override
    public void render(@NotNull GuiGraphics ctx, int x, int y, float partialTicks) {
        ctx.drawCenteredString(font, title.getString(), width / 2, height / 2 - 50 - font.lineHeight, 0xFFFFFF);
        super.render(ctx, x, y, partialTicks);
    }

}
