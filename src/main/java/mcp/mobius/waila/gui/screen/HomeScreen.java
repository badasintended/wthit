package mcp.mobius.waila.gui.screen;

import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.buildconst.Tl;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static mcp.mobius.waila.util.DisplayUtil.createButton;

public class HomeScreen extends YesIAmSureTheClientInstanceIsPresentByTheTimeIUseItScreen {

    private final @Nullable Screen parent;

    public HomeScreen(@Nullable Screen parent) {
        super(Component.literal(WailaConstants.MOD_NAME));

        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(createButton(width / 2 - 100, height / 2 - 60, 200, 20, Component.translatable(Tl.Gui.WAILA_SETTINGS, WailaConstants.MOD_NAME), w ->
            minecraft.setScreen(new WailaConfigScreen(this))));
        addRenderableWidget(createButton(width / 2 - 100, height / 2 - 36, 200, 20, Component.translatable(Tl.Gui.Plugin.TOGGLE), w ->
            minecraft.setScreen(new PluginToggleScreen(this))));
        addRenderableWidget(createButton(width / 2 - 100, height / 2 - 12, 200, 20, Component.translatable(Tl.Gui.Plugin.SETTINGS), w ->
            minecraft.setScreen(new PluginConfigScreen(this))));
        addRenderableWidget(createButton(width / 2 - 100, height / 2 + 12, 200, 20, Component.translatable(Tl.Gui.CREDITS), w ->
            minecraft.setScreen(new CreditsScreen(this))));
        addRenderableWidget(createButton(width / 2 - 50, height / 2 + 36, 100, 20, CommonComponents.GUI_DONE, w ->
            minecraft.setScreen(parent)));
    }

    @Override
    public void render(@NotNull GuiGraphics ctx, int x, int y, float partialTicks) {
        renderBackground(ctx);
        ctx.drawCenteredString(font, title.getString(), width / 2, height / 2 - 62 - font.lineHeight, 0xFFFFFF);
        super.render(ctx, x, y, partialTicks);
    }

}
