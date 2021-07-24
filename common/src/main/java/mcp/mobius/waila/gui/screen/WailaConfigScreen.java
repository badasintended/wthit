package mcp.mobius.waila.gui.screen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import mcp.mobius.waila.gui.widget.value.InputValue;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class WailaConfigScreen extends ConfigScreen {

    private static WailaConfig get() {
        return Waila.config.get();
    }

    private static void save() {
        Waila.config.save();
    }

    private static void invalidate() {
        Waila.config.invalidate();
    }

    public WailaConfigScreen(Screen parent) {
        super(parent, new TranslatableComponent("gui.waila.configuration", WailaConstants.MOD_NAME), WailaConfigScreen::save, WailaConfigScreen::invalidate);
    }

    @Override
    public ConfigListWidget getOptions() {
        return new ConfigListWidget(this, minecraft, width, height, 32, height - 32, 30, WailaConfigScreen::save)
            .withButton("config.waila.general", 100, 20, w -> minecraft.setScreen(new ConfigScreen(this, new TranslatableComponent("config.waila.general")) {
                @Override
                public ConfigListWidget getOptions() {
                    return new ConfigListWidget(this, minecraft, width, height, 32, height - 32, 30)
                        .withBoolean("config.waila.display_tooltip",
                            get().getGeneral().shouldDisplayTooltip(),
                            val -> get().getGeneral().setDisplayTooltip(val))
                        .withBoolean("config.waila.sneaky_details",
                            get().getGeneral().shouldShiftForDetails(),
                            val -> get().getGeneral().setShiftForDetails(val))
                        .withEnum("config.waila.display_mode",
                            WailaConfig.General.DisplayMode.values(),
                            get().getGeneral().getDisplayMode(),
                            val -> get().getGeneral().setDisplayMode(val))
                        .withBoolean("config.waila.hide_from_players",
                            get().getGeneral().shouldHideFromPlayerList(),
                            val -> get().getGeneral().setHideFromPlayerList(val))
                        .withBoolean("config.waila.hide_from_debug",
                            get().getGeneral().shouldHideFromDebug(),
                            val -> get().getGeneral().setHideFromDebug(val))
                        .withBoolean("config.waila.tts",
                            get().getGeneral().shouldEnableTextToSpeech(),
                            val -> get().getGeneral().setEnableTextToSpeech(val))
                        .withInput("config.waila.rate_limit",
                            get().getGeneral().getRateLimit(),
                            val -> get().getGeneral().setRateLimit(Math.max(val, 250)),
                            InputValue.POSITIVE_INTEGER)
                        .withInput("config.waila.max_health_for_render",
                            get().getGeneral().getMaxHealthForRender(),
                            val -> get().getGeneral().setMaxHealthForRender(val),
                            InputValue.POSITIVE_INTEGER)
                        .withInput("config.waila.max_hearts_per_line",
                            get().getGeneral().getMaxHeartsPerLine(),
                            val -> get().getGeneral().setMaxHeartsPerLine(val),
                            InputValue.POSITIVE_INTEGER);
                }
            }))
            .withButton("config.waila.overlay", 100, 20, w -> minecraft.setScreen(new ConfigScreen(this, new TranslatableComponent("config.waila.overlay")) {
                @Override
                public ConfigListWidget getOptions() {
                    return new ConfigListWidget(this, minecraft, width, height, 32, height - 32, 30)
                        .withInput("config.waila.overlay_pos_x",
                            get().getOverlay().getPosition().getX(),
                            val -> get().getOverlay().getPosition().setX(val),
                            InputValue.INTEGER)
                        .withInput("config.waila.overlay_pos_y",
                            get().getOverlay().getPosition().getY(),
                            val -> get().getOverlay().getPosition().setY(val),
                            InputValue.INTEGER)
                        .withInput("config.waila.overlay_scale",
                            get().getOverlay().getScale(),
                            val -> get().getOverlay().setScale(Math.max(val, 0.0F)),
                            InputValue.POSITIVE_FLOAT)
                        .withEnum("config.waila.overlay_anchor_x",
                            WailaConfig.Overlay.Position.X.values(),
                            get().getOverlay().getPosition().getAnchorX(),
                            val -> get().getOverlay().getPosition().setAnchorX(val))
                        .withEnum("config.waila.overlay_anchor_y",
                            WailaConfig.Overlay.Position.Y.values(),
                            get().getOverlay().getPosition().getAnchorY(),
                            val -> get().getOverlay().getPosition().setAnchorY(val))
                        .withEnum("config.waila.overlay_align_x",
                            WailaConfig.Overlay.Position.X.values(),
                            get().getOverlay().getPosition().getAlignX(),
                            val -> get().getOverlay().getPosition().setAlignX(val))
                        .withEnum("config.waila.overlay_align_y",
                            WailaConfig.Overlay.Position.Y.values(),
                            get().getOverlay().getPosition().getAlignY(),
                            val -> get().getOverlay().getPosition().setAlignY(val))
                        .withInput("config.waila.overlay_alpha",
                            get().getOverlay().getColor().getRawAlpha(),
                            val -> get().getOverlay().getColor().setAlpha(Math.min(100, Math.max(0, val))),
                            InputValue.POSITIVE_INTEGER)
                        .withCycle("config.waila.overlay_theme",
                            get().getOverlay().getColor().getThemes().stream().map(t -> t.getId().toString()).sorted(String::compareToIgnoreCase).toArray(String[]::new),
                            get().getOverlay().getColor().getTheme().getId().toString(),
                            val -> get().getOverlay().getColor().applyTheme(new ResourceLocation(val)));
                }
            }))
            .withButton("config.waila.formatting", 100, 20, w -> minecraft.setScreen(new ConfigScreen(this, new TranslatableComponent("config.waila.overlay")) {
                @Override
                public ConfigListWidget getOptions() {
                    return new ConfigListWidget(this, minecraft, width, height, 32, height - 32, 30)
                        .withInput("config.waila.format_mod_name",
                            get().getFormatting().getModName(),
                            val -> get().getFormatting().setModName(val.isEmpty() || !val.contains("%s") ? get().getFormatting().getModName() : val))
                        .withInput("config.waila.format_block_name",
                            get().getFormatting().getBlockName(),
                            val -> get().getFormatting().setBlockName(val.isEmpty() || !val.contains("%s") ? get().getFormatting().getBlockName() : val))
                        .withInput("config.waila.format_fluid_name",
                            get().getFormatting().getFluidName(),
                            val -> get().getFormatting().setFluidName(val.isEmpty() || !val.contains("%s") ? get().getFormatting().getFluidName() : val))
                        .withInput("config.waila.format_entity_name",
                            get().getFormatting().getEntityName(),
                            val -> get().getFormatting().setEntityName(val.isEmpty() || !val.contains("%s") ? get().getFormatting().getEntityName() : val))
                        .withInput("config.waila.format_registry_name",
                            get().getFormatting().getRegistryName(),
                            val -> get().getFormatting().setRegistryName(val.isEmpty() || !val.contains("%s") ? get().getFormatting().getRegistryName() : val));
                }
            }))
            .withButton("config.waila.dump", 100, 20, w -> {
                File file = new File("waila_dump.md");
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(DumpGenerator.generateInfoDump());
                    SystemToast.addOrUpdate(minecraft.getToasts(), SystemToast.SystemToastIds.WORLD_BACKUP, new TranslatableComponent("command.waila.dump_success"), TextComponent.EMPTY);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }

}
