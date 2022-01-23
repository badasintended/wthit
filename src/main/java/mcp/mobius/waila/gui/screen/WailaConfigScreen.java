package mcp.mobius.waila.gui.screen;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.gui.widget.CategoryEntry;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import mcp.mobius.waila.gui.widget.value.BooleanValue;
import mcp.mobius.waila.gui.widget.value.CycleValue;
import mcp.mobius.waila.gui.widget.value.EnumValue;
import mcp.mobius.waila.gui.widget.value.InputValue;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class WailaConfigScreen extends ConfigScreen {

    private final WailaConfig defaultConfig = new WailaConfig();

    public WailaConfigScreen(Screen parent) {
        super(parent, new TranslatableComponent("gui.waila.configuration", WailaConstants.MOD_NAME), WailaConfigScreen::save, WailaConfigScreen::invalidate);
    }

    private static WailaConfig get() {
        return Waila.CONFIG.get();
    }

    private static void save() {
        Waila.CONFIG.save();
    }

    private static void invalidate() {
        Waila.CONFIG.invalidate();
    }

    @Override
    public ConfigListWidget getOptions() {
        ConfigListWidget options = new ConfigListWidget(this, minecraft, width, height, 32, height - 32, 26, WailaConfigScreen::save);
        options.with(new CategoryEntry("config.waila.general"))
            .with(new BooleanValue("config.waila.display_tooltip",
                get().getGeneral().isDisplayTooltip(),
                defaultConfig.getGeneral().isDisplayTooltip(),
                val -> get().getGeneral().setDisplayTooltip(val)))
            .with(new BooleanValue("config.waila.sneaky_details",
                get().getGeneral().isShiftForDetails(),
                defaultConfig.getGeneral().isShiftForDetails(),
                val -> get().getGeneral().setShiftForDetails(val)))
            .with(new EnumValue<>("config.waila.display_mode",
                IWailaConfig.General.DisplayMode.values(),
                get().getGeneral().getDisplayMode(),
                defaultConfig.getGeneral().getDisplayMode(),
                val -> get().getGeneral().setDisplayMode(val)))
            .with(new BooleanValue("config.waila.hide_from_players",
                get().getGeneral().isHideFromPlayerList(),
                defaultConfig.getGeneral().isHideFromPlayerList(),
                val -> get().getGeneral().setHideFromPlayerList(val)))
            .with(new BooleanValue("config.waila.hide_from_debug",
                get().getGeneral().isHideFromDebug(),
                defaultConfig.getGeneral().isHideFromDebug(),
                val -> get().getGeneral().setHideFromDebug(val)))
            .with(new BooleanValue("config.waila.tts",
                get().getGeneral().isEnableTextToSpeech(),
                defaultConfig.getGeneral().isEnableTextToSpeech(),
                val -> get().getGeneral().setEnableTextToSpeech(val)))
            .with(new InputValue<>("config.waila.rate_limit",
                get().getGeneral().getRateLimit(),
                defaultConfig.getGeneral().getRateLimit(),
                val -> get().getGeneral().setRateLimit(Math.max(val, 250)),
                InputValue.POSITIVE_INTEGER))
            .with(new InputValue<>("config.waila.max_health_for_render",
                get().getGeneral().getMaxHealthForRender(),
                defaultConfig.getGeneral().getMaxHealthForRender(),
                val -> get().getGeneral().setMaxHealthForRender(val),
                InputValue.POSITIVE_INTEGER))
            .with(new InputValue<>("config.waila.max_hearts_per_line",
                get().getGeneral().getMaxHeartsPerLine(),
                defaultConfig.getGeneral().getMaxHeartsPerLine(),
                val -> get().getGeneral().setMaxHeartsPerLine(val),
                InputValue.POSITIVE_INTEGER));

        options.with(new CategoryEntry("config.waila.overlay"))
            .with(new EnumValue<>("config.waila.overlay_anchor_x",
                IWailaConfig.Overlay.Position.Align.X.values(),
                get().getOverlay().getPosition().getAnchor().getX(),
                defaultConfig.getOverlay().getPosition().getAnchor().getX(),
                val -> get().getOverlay().getPosition().getAnchor().setX(val)))
            .with(new EnumValue<>("config.waila.overlay_anchor_y",
                IWailaConfig.Overlay.Position.Align.Y.values(),
                get().getOverlay().getPosition().getAnchor().getY(),
                defaultConfig.getOverlay().getPosition().getAnchor().getY(),
                val -> get().getOverlay().getPosition().getAnchor().setY(val)))
            .with(new EnumValue<>("config.waila.overlay_align_x",
                IWailaConfig.Overlay.Position.Align.X.values(),
                get().getOverlay().getPosition().getAlign().getX(),
                defaultConfig.getOverlay().getPosition().getAlign().getX(),
                val -> get().getOverlay().getPosition().getAlign().setX(val)))
            .with(new EnumValue<>("config.waila.overlay_align_y",
                IWailaConfig.Overlay.Position.Align.Y.values(),
                get().getOverlay().getPosition().getAlign().getY(),
                defaultConfig.getOverlay().getPosition().getAlign().getY(),
                val -> get().getOverlay().getPosition().getAlign().setY(val)))
            .with(new InputValue<>("config.waila.overlay_pos_x",
                get().getOverlay().getPosition().getX(),
                defaultConfig.getOverlay().getPosition().getX(),
                val -> get().getOverlay().getPosition().setX(val),
                InputValue.INTEGER))
            .with(new InputValue<>("config.waila.overlay_pos_y",
                get().getOverlay().getPosition().getY(),
                defaultConfig.getOverlay().getPosition().getY(),
                val -> get().getOverlay().getPosition().setY(val),
                InputValue.INTEGER))
            .with(new BooleanValue("config.waila.boss_bars_overlap",
                get().getOverlay().getPosition().isBossBarsOverlap(),
                defaultConfig.getOverlay().getPosition().isBossBarsOverlap(),
                val -> get().getOverlay().getPosition().setBossBarsOverlap(val)))
            .with(new InputValue<>("config.waila.overlay_scale",
                get().getOverlay().getScale(),
                defaultConfig.getOverlay().getScale(),
                val -> get().getOverlay().setScale(Math.max(val, 0.0F)),
                InputValue.POSITIVE_DECIMAL))
            .with(new InputValue<>("config.waila.overlay_alpha",
                get().getOverlay().getColor().rawAlpha(),
                defaultConfig.getOverlay().getColor().rawAlpha(),
                val -> get().getOverlay().getColor().setAlpha(Math.min(100, Math.max(0, val))),
                InputValue.POSITIVE_INTEGER))
            .with(new CycleValue("config.waila.overlay_theme",
                get().getOverlay().getColor().themes().stream().map(t -> t.getId().toString()).sorted(String::compareToIgnoreCase).toArray(String[]::new),
                get().getOverlay().getColor().theme().getId().toString(),
                val -> get().getOverlay().getColor().applyTheme(new ResourceLocation(val)),
                false));

        options.with(new CategoryEntry("config.waila.formatting"))
            .with(new InputValue<>("config.waila.format_mod_name",
                get().getFormatting().getModName(),
                defaultConfig.getFormatting().getModName(),
                val -> get().getFormatting().setModName(val.isEmpty() || !val.contains("%s") ? get().getFormatting().getModName() : val),
                InputValue.ANY))
            .with(new InputValue<>("config.waila.format_block_name",
                get().getFormatting().getBlockName(),
                defaultConfig.getFormatting().getBlockName(),
                val -> get().getFormatting().setBlockName(val.isEmpty() || !val.contains("%s") ? get().getFormatting().getBlockName() : val),
                InputValue.ANY))
            .with(new InputValue<>("config.waila.format_fluid_name",
                get().getFormatting().getFluidName(),
                defaultConfig.getFormatting().getFluidName(),
                val -> get().getFormatting().setFluidName(val.isEmpty() || !val.contains("%s") ? get().getFormatting().getFluidName() : val),
                InputValue.ANY))
            .with(new InputValue<>("config.waila.format_entity_name",
                get().getFormatting().getEntityName(),
                defaultConfig.getFormatting().getEntityName(),
                val -> get().getFormatting().setEntityName(val.isEmpty() || !val.contains("%s") ? get().getFormatting().getEntityName() : val),
                InputValue.ANY))
            .with(new InputValue<>("config.waila.format_registry_name",
                get().getFormatting().getRegistryName(),
                defaultConfig.getFormatting().getRegistryName(),
                val -> get().getFormatting().setRegistryName(val.isEmpty() || !val.contains("%s") ? get().getFormatting().getRegistryName() : val),
                InputValue.ANY));

        return options;
    }

}
