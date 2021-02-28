package mcp.mobius.waila.gui;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.gui.config.OptionsListWidget;
import mcp.mobius.waila.gui.config.value.OptionsEntryValueInput;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringEscapeUtils;

public class GuiConfigWaila extends GuiOptions {

    private static WailaConfig config() {
        return Waila.CONFIG.get();
    }

    public GuiConfigWaila(Screen parent) {
        super(parent, new TranslatableText("gui.waila.configuration", Waila.NAME), Waila.CONFIG::save, Waila.CONFIG::invalidate);
    }

    @Override
    public OptionsListWidget getOptions() {
        return new OptionsListWidget(this, client, width + 45, height, 32, height - 32, 30, Waila.CONFIG::save)
            .withButton("config.waila.general", 100, 20, w -> client.openScreen(new GuiOptions(this, new TranslatableText("config.waila.general")) {
                @Override
                public OptionsListWidget getOptions() {
                    return new OptionsListWidget(this, client, width + 45, height, 32, height - 32, 30)
                        .withBoolean("config.waila.display_tooltip",
                            config().getGeneral().shouldDisplayTooltip(),
                            val -> config().getGeneral().setDisplayTooltip(val))
                        .withBoolean("config.waila.sneaky_details",
                            config().getGeneral().shouldShiftForDetails(),
                            val -> config().getGeneral().setShiftForDetails(val))
                        .withEnum("config.waila.display_mode",
                            WailaConfig.DisplayMode.values(),
                            config().getGeneral().getDisplayMode(),
                            val -> config().getGeneral().setDisplayMode(val))
                        .withBoolean("config.waila.hide_from_players",
                            config().getGeneral().shouldHideFromPlayerList(),
                            val -> config().getGeneral().setHideFromPlayerList(val))
                        .withBoolean("config.waila.hide_from_debug",
                            config().getGeneral().shouldHideFromDebug(),
                            val -> config().getGeneral().setHideFromDebug(val))
                        .withBoolean("config.waila.tts",
                            config().getGeneral().shouldEnableTextToSpeech(),
                            val -> config().getGeneral().setEnableTextToSpeech(val))
                        .withInput("config.waila.max_health_for_render",
                            config().getGeneral().getMaxHealthForRender(),
                            val -> config().getGeneral().setMaxHealthForRender(val),
                            OptionsEntryValueInput.INTEGER)
                        .withInput("config.waila.max_hearts_per_line",
                            config().getGeneral().getMaxHeartsPerLine(),
                            val -> config().getGeneral().setMaxHeartsPerLine(val),
                            OptionsEntryValueInput.INTEGER);
                }
            }))
            .withButton("config.waila.overlay", 100, 20, w -> client.openScreen(new GuiOptions(this, new TranslatableText("config.waila.overlay")) {
                @Override
                public OptionsListWidget getOptions() {
                    return new OptionsListWidget(this, client, width + 45, height, 32, height - 32, 30)
                        .withInput("config.waila.overlay_pos_x",
                            config().getOverlay().getPosition().getX(),
                            val -> config().getOverlay().getPosition().setX(val), OptionsEntryValueInput.INTEGER)
                        .withInput("config.waila.overlay_pos_y",
                            config().getOverlay().getPosition().getY(),
                            val -> config().getOverlay().getPosition().setY(val),
                            OptionsEntryValueInput.INTEGER)
                        .withInput("config.waila.overlay_scale",
                            config().getOverlay().getScale(),
                            val -> config().getOverlay().setScale(Math.max(val, 0.0F)),
                            OptionsEntryValueInput.FLOAT)
                        .withEnum("config.waila.overlay_anchor_x",
                            WailaConfig.ConfigOverlay.Position.HorizontalAlignment.values(),
                            config().getOverlay().getPosition().getAnchorX(),
                            val -> config().getOverlay().getPosition().setAnchorX(val))
                        .withEnum("config.waila.overlay_anchor_y",
                            WailaConfig.ConfigOverlay.Position.VerticalAlignment.values(),
                            config().getOverlay().getPosition().getAnchorY(),
                            val -> config().getOverlay().getPosition().setAnchorY(val))
                        .withEnum("config.waila.overlay_align_x",
                            WailaConfig.ConfigOverlay.Position.HorizontalAlignment.values(),
                            config().getOverlay().getPosition().getAlignX(),
                            val -> config().getOverlay().getPosition().setAlignX(val))
                        .withEnum("config.waila.overlay_align_y",
                            WailaConfig.ConfigOverlay.Position.VerticalAlignment.values(),
                            config().getOverlay().getPosition().getAlignY(),
                            val -> config().getOverlay().getPosition().setAlignY(val))
                        .withInput("config.waila.overlay_alpha",
                            config().getOverlay().getColor().getRawAlpha(),
                            val -> config().getOverlay().getColor().setAlpha(Math.min(100, Math.max(0, val))),
                            OptionsEntryValueInput.INTEGER)
                        .withCycle("config.waila.overlay_theme",
                            config().getOverlay().getColor().getThemes().stream().map(t -> t.getId().toString()).sorted(String::compareToIgnoreCase).toArray(String[]::new),
                            config().getOverlay().getColor().getTheme().getId().toString(),
                            val -> config().getOverlay().getColor().applyTheme(new Identifier(val)));
                }
            }))
            .withButton("config.waila.formatting", 100, 20, w -> client.openScreen(new GuiOptions(this, new TranslatableText("config.waila.overlay")) {
                @Override
                public OptionsListWidget getOptions() {
                    return new OptionsListWidget(this, client, width + 45, height, 32, height - 32, 30)
                        .withInput("config.waila.format_mod_name",
                            StringEscapeUtils.escapeJava(config().getFormatting().getModName()),
                            val -> config().getFormatting().setModName(val.isEmpty() || !val.contains("%s") ? config().getFormatting().getModName() : val))
                        .withInput("config.waila.format_block_name",
                            StringEscapeUtils.escapeJava(config().getFormatting().getBlockName()),
                            val -> config().getFormatting().setBlockName(val.isEmpty() || !val.contains("%s") ? config().getFormatting().getBlockName() : val))
                        .withInput("config.waila.format_fluid_name",
                            StringEscapeUtils.escapeJava(config().getFormatting().getFluidName()),
                            val -> config().getFormatting().setFluidName(val.isEmpty() || !val.contains("%s") ? config().getFormatting().getFluidName() : val))
                        .withInput("config.waila.format_entity_name",
                            StringEscapeUtils.escapeJava(config().getFormatting().getEntityName()),
                            val -> config().getFormatting().setEntityName(val.isEmpty() || !val.contains("%s") ? config().getFormatting().getEntityName() : val))
                        .withInput("config.waila.format_registry_name",
                            StringEscapeUtils.escapeJava(config().getFormatting().getRegistryName()),
                            val -> config().getFormatting().setRegistryName(val.isEmpty() || !val.contains("%s") ? config().getFormatting().getRegistryName() : val));
                }
            }));
    }

}
