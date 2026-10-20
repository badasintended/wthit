package mcp.mobius.waila.util;

import java.awt.Desktop;
import java.nio.file.Path;

import net.minecraft.util.Util;

public final class FileUtil {

    private static final Log LOG = Log.create();

    /**
     * Opens the given file or directory with the platform's default program.
     *
     * <p>Vanilla used to expose {@code Util.OS#openFile(File)} for this, that method is gone since 26.3,
     * so the platform routine has to be picked manually here.
     */
    public static void openFile(Path path) {
        try {
            if (Desktop.isDesktopSupported()) {
                var desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    desktop.open(path.toFile());
                    return;
                }
            }
        } catch (Exception ignored) {
            // fall through to the platform specific routine
        }

        try {
            var command = switch (Util.getPlatform()) {
                case WINDOWS -> new String[]{"rundll32", "url.dll,FileProtocolHandler", path.toString()};
                case OSX -> new String[]{"open", path.toString()};
                default -> new String[]{"xdg-open", path.toString()};
            };

            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            LOG.error("Failed to open file at " + path, e);
        }
    }

}
