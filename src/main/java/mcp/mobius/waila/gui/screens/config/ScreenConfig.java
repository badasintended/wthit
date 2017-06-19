package mcp.mobius.waila.gui.screens.config;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.screens.ScreenBase;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.LayoutBase;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import mcp.mobius.waila.gui.widgets.buttons.ButtonContainer;
import mcp.mobius.waila.gui.widgets.buttons.ButtonLabel;
import mcp.mobius.waila.gui.widgets.buttons.ButtonScreenChange;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Loader;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class ScreenConfig extends ScreenBase {
    public ScreenConfig(GuiScreen parent) {
        super(parent);

        LayoutBase titleInfo = new LayoutBase(getRoot());
        titleInfo.setGeometry(new WidgetGeometry(50.0, 20.0, 50.0, 30.0, CType.RELXY, CType.REL_X, WAlign.CENTER, WAlign.CENTER));
        titleInfo.addWidget("HwylaName", new LabelFixedFont(titleInfo, "HWYLA - Here's What You're Looking At"));
        titleInfo.getWidget("HwylaName").setGeometry(new WidgetGeometry(50.0, 0.0F, 50.0, 30.0, CType.RELXY, CType.REL_X, WAlign.CENTER, WAlign.CENTER));
        titleInfo.addWidget("HwylaVersion", new LabelFixedFont(titleInfo, Loader.MC_VERSION + " - " + Waila.VERSION));
        titleInfo.getWidget("HwylaVersion").setGeometry(new WidgetGeometry(50.0, 40.0F, 50.0, 30.0, CType.RELXY, CType.REL_X, WAlign.CENTER, WAlign.CENTER));
        List<String> warningCut = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(I18n.format("screen.hwyla.warning"), 200);
        for (int i = 0; i < warningCut.size(); i++) {
            String warning = warningCut.get(i);
            titleInfo.addWidget("HwylaWarning" + i, new LabelFixedFont(titleInfo, warning));
            titleInfo.getWidget("HwylaWarning" + i).setGeometry(new WidgetGeometry(50.0, 90.0F + (32.0F * i), 50.0, 30.0, CType.RELXY, CType.REL_X, WAlign.CENTER, WAlign.CENTER));
        }
        this.getRoot().addWidget("TitleInfo", titleInfo);

        this.getRoot().addWidget("HwylaChanges", new ButtonLabel(getRoot(), "?") {
            @Override
            public void onMouseClick(MouseEvent event) {
                try {
                    if (event.button == 0) {
                        String[] versionSplit = ForgeVersion.mcVersion.split("\\.");
                        String branch = versionSplit[0] + "." + versionSplit[1];
                        try {
                            Desktop.getDesktop().browse(new URI("https://github.com/TehNut/HWYLA/blob/" + branch + "/CHANGES.md"));
                        } catch (URISyntaxException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.getRoot().getWidget("HwylaChanges").setGeometry(new WidgetGeometry(2.5D, 5.0D, 20.0D, 20.0D, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));

        this.getRoot().addWidget("ButtonContainer", new ButtonContainer(this.getRoot(), 2, 100, 25.0));
        this.getRoot().getWidget("ButtonContainer").setGeometry(new WidgetGeometry(50.0, 60.0, 50.0, 30.0, CType.RELXY, CType.REL_X, WAlign.CENTER, WAlign.CENTER));

        ButtonContainer buttonContainer = ((ButtonContainer) this.getRoot().getWidget("ButtonContainer"));

        buttonContainer.addButton(new ButtonScreenChange(this.getRoot(), "screen.button.waila", new ScreenWailaConfig(this)));
        buttonContainer.addButton(new ButtonScreenChange(this.getRoot(), "screen.button.modules", new ScreenModuleChoice(this)));

        this.getRoot().addWidget("LayoutBack", new LayoutBase(this.getRoot()));
        this.getRoot().getWidget("LayoutBack").setGeometry(new WidgetGeometry(0.0, 80.0, 100.0, 20.0, CType.RELXY, CType.RELXY));
        this.getRoot().getWidget("LayoutBack").addWidget("ButtonBack", new ButtonScreenChange(this.getRoot().getWidget("LayoutBack"), "screen.button.back", this.parent));
        this.getRoot().getWidget("LayoutBack").getWidget("ButtonBack").setGeometry(new WidgetGeometry(50.0, 50.0, 100.0, 20.0, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
    }
}
