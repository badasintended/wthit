package mcp.mobius.waila.plugin.vanilla;

import java.util.List;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.enums.Instrument;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

import static net.minecraft.util.math.MathHelper.clamp;
import static net.minecraft.util.math.MathHelper.sin;

public enum NoteBlockComponent implements IBlockComponentProvider {

    INSTANCE;

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
            return VALUES[level % (VALUES.length - 1)];
        }
    }

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
    public void appendBody(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.get(WailaVanilla.CONFIG_NOTE_BLOCK)) {
            BlockState state = accessor.getBlockState();
            Instrument instrument = state.get(NoteBlock.INSTRUMENT);
            int level = state.get(NoteBlock.NOTE);
            Note note = Note.get(level);
            tooltip.add(new TranslatableText("tooltip.waila.instrument." + instrument.asString())
                .append(new LiteralText(" (" + (config.get(WailaVanilla.CONFIG_NOTE_BLOCK_FLAT) ? note.flat : note.sharp) + ")")
                    .styled(style -> style.withColor(COLORS[level]))));
        }
    }

}
