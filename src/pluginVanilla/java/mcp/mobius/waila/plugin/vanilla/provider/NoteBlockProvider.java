package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.mixin.NoteBlockAccess;
import mcp.mobius.waila.plugin.vanilla.config.NoteDisplayMode;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.NoteBlock;

import static net.minecraft.util.Mth.clamp;
import static net.minecraft.util.Mth.sin;

public enum NoteBlockProvider implements IBlockComponentProvider {

    INSTANCE;

    private static final int[] COLORS = Util.make(new int[25], arr -> {
        var twoPi = (float) (Math.PI * 2);
        var onePerThree = 1f / 3f;
        var twoPerThree = 2f / 3f;
        for (var i = 0; i < arr.length; i++) {
            var levelF = i / 24f;
            var r = (int) (255 * clamp(sin(levelF * twoPi) * 0.65f + 0.35f, 0f, 1f));
            var g = (int) (255 * clamp(sin((levelF + onePerThree) * twoPi) * 0.65f + 0.35f, 0f, 1f));
            var b = (int) (255 * clamp(sin((levelF + twoPerThree) * twoPi) * 0.65f + 0.35f, 0f, 1f));
            arr[i] = (r << 16) + (g << 8) + b;
        }
    });

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.NOTE_BLOCK_TYPE)) {
            var state = accessor.getBlockState();
            var instrument = state.getValue(NoteBlock.INSTRUMENT);
            var line = tooltip.setLine(Options.NOTE_BLOCK_TYPE);

            Component instrumentText;
            if (instrument.hasCustomSound()) {
                var soundId = ((NoteBlockAccess) accessor.getBlock()).wthit_getCustomSoundId(accessor.getWorld(), accessor.getPosition());
                instrumentText = soundId == null
                    ? Component.translatable(Tl.Tooltip.Instrument.NONE)
                    : Component.literal(soundId.toString()).withStyle(ChatFormatting.DARK_PURPLE);
            } else {
                instrumentText = Component.translatable(Tl.Tooltip.INSTRUMENT + "." + instrument.getSerializedName());
            }
            line.with(instrumentText);

            if (instrument.isTunable()) {
                int level = state.getValue(NoteBlock.NOTE);
                var note = Note.get(level);
                var builder = new StringBuilder()
                    .append(" (")
                    .append(config.<NoteDisplayMode>getEnum(Options.NOTE_BLOCK_NOTE) == NoteDisplayMode.SHARP ? note.sharp : note.flat)
                    .append(")");
                if (config.getBoolean(Options.NOTE_BLOCK_INT_VALUE)) {
                    builder
                        .append(" (")
                        .append(level)
                        .append(")");
                }
                line.with(Component.literal(builder.toString()).withStyle(style -> style.withColor(COLORS[level])));
            }
        }
    }

    private enum Note {
        FS("F♯", "G♭"), G("G"), GS("G♯", "A♭"), A("A"), AS("A♯", "B♭"), B("B"),
        C("C"), CS("C♯", "D♭"), D("D"), DS("D♯", "E♭"), E("E"), F("F");

        static final Note[] VALUES = values();
        final String sharp;
        final String flat;

        Note(String sharp, String flat) {
            this.sharp = sharp;
            this.flat = flat;
        }

        Note(String note) {
            this(note, note);
        }

        static Note get(int level) {
            return VALUES[level % VALUES.length];
        }
    }

}
