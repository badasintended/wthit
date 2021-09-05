package mcp.mobius.waila.api;

import mcp.mobius.waila.api.internal.ApiSide;
import mcp.mobius.waila.impl.Impl;
import org.jetbrains.annotations.ApiStatus;

@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface IWailaConfig {

    static IWailaConfig get() {
        return Impl.get(IWailaConfig.class);
    }

    General getGeneral();

    Overlay getOverlay();

    Formatting getFormatting();

    interface General {

        boolean isDisplayTooltip();

        boolean isShiftForDetails();

        DisplayMode getDisplayMode();

        boolean isHideFromPlayerList();

        boolean isHideFromDebug();

        boolean isEnableTextToSpeech();

        int getRateLimit();

        int getMaxHealthForRender();

        int getMaxHeartsPerLine();

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

            int getAlpha();

            int getBackgroundColor();

            int getGradientStart();

            int getGradientEnd();

            int getFontColor();

        }

    }

    interface Formatting {

        String formatModName(Object modName);

        String formatBlockName(Object blockName);

        String formatFluidName(Object fluidName);

        String formatEntityName(Object entityName);

        String formatRegistryName(Object registryName);

    }

}
