package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.config.Options.NoteDisplayMode;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import static net.minecraft.util.Mth.clamp;
import static net.minecraft.util.Mth.sin;

public enum NoteBlockProvider implements IBlockComponentProvider {

    INSTANCE;

    private static final int[] COLORS = Util.make(new int[25], arr -> {
        float twoPi = (float) (Math.PI * 2);
        float onePerThree = 1f / 3f;
        float twoPerThree = 2f / 3f;
        for (int i = 0; i < arr.length; i++) {
            float levelF = i / 24f;
            int r = (int) (255 * clamp(sin(levelF * twoPi) * 0.65f + 0.35f, 0f, 1f));
            int g = (int) (255 * clamp(sin((levelF + onePerThree) * twoPi) * 0.65f + 0.35f, 0f, 1f));
            int b = (int) (255 * clamp(sin((levelF + twoPerThree) * twoPi) * 0.65f + 0.35f, 0f, 1f));
            arr[i] = (r << 16) + (g << 8) + b;
        }
    });

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.NOTE_BLOCK_TYPE)) {
            BlockState state = accessor.getBlockState();
            NoteBlockInstrument instrument = state.getValue(NoteBlock.INSTRUMENT);
            int level = state.getValue(NoteBlock.NOTE);
            Note note = Note.get(level);
            StringBuilder builder = new StringBuilder()
                .append(" (")
                .append(config.<NoteDisplayMode>getEnum(Options.NOTE_BLOCK_NOTE) == NoteDisplayMode.SHARP ? note.sharp : note.flat)
                .append(")");
            if (config.getBoolean(Options.NOTE_BLOCK_INT_VALUE)) {
                builder
                    .append(" (")
                    .append(level)
                    .append(")");
            }
            tooltip.addLine()
                .with(Component.translatable("tooltip.waila.instrument." + instrument.getSerializedName()))
                .with(Component.literal(builder.toString()).withStyle(style -> style.withColor(COLORS[level])));
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
