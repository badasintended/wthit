package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface IWailaConfig {

    static IWailaConfig get() {
        return IApiService.INSTANCE.getConfig();
    }

    General getGeneral();

    Overlay getOverlay();

    Formatter getFormatter();

    interface General {

        boolean isDisplayTooltip();

        boolean isShiftForDetails();

        boolean isHideShiftText();

        DisplayMode getDisplayMode();

        boolean isHideFromPlayerList();

        boolean isHideFromDebug();

        boolean isEnableTextToSpeech();

        int getRateLimit();

        enum DisplayMode {
            HOLD_KEY,
            TOGGLE
        }

    }

    interface Overlay {

        Position getPosition();

        float getScale();

        Color getColor();

        interface Position {

            int getX();

            int getY();

            Align getAlign();

            Align getAnchor();

            boolean isBossBarsOverlap();

            interface Align {

                X getX();

                Y getY();

                enum X {
                    LEFT(0.0),
                    CENTER(0.5),
                    RIGHT(1.0);

                    public final double multiplier;

                    X(double multiplier) {
                        this.multiplier = multiplier;
                    }
                }

                enum Y {
                    TOP(0.0),
                    MIDDLE(0.5),
                    BOTTOM(1.0);

                    public final double multiplier;

                    Y(double multiplier) {
                        this.multiplier = multiplier;
                    }

                }

            }

        }

        interface Color {

            int getBackgroundAlpha();

            ITheme getTheme();

        }

    }

    interface Formatter {

        Component modName(Object modName);

        Component blockName(Object blockName);

        Component fluidName(Object fluidName);

        Component entityName(Object entityName);

        Component registryName(Object registryName);

    }

}
