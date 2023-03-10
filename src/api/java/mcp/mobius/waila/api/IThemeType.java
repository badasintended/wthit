package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IApiService;
import org.jetbrains.annotations.ApiStatus;

/**
 * A description of a theme. Decides how a theme is serialized.
 *
 * @param <T> the theme type.
 */
@ApiSide.ClientOnly
@ApiStatus.NonExtendable
@ApiStatus.Experimental
public interface IThemeType<T extends ITheme> {

    static <T extends ITheme> Builder<T> of(Class<T> clazz) {
        return IApiService.INSTANCE.createThemeTypeBuilder(clazz);
    }

    interface Builder<T extends ITheme> {

        /**
         * Creates a property of this theme type.
         * <p>
         * A property is a field member of the {@linkplain T theme} implementation that will be set based on the theme configuration file.
         * <p>
         * An input field will be generated in the theme editor screen with {@code theme.waila.plugin_[typeNamespace].[typePath].[fieldName]}
         * as the translation key.
         *
         * @param name         the name of the property
         * @param defaultValue the default value of this property
         *
         * @see ITheme#processProperties(IThemeAccessor)
         */
        Builder<T> property(String name, int defaultValue);

        /**
         * Creates a property of this theme type.
         * <p>
         * A property is a field member of the {@linkplain T theme} implementation that will be set based on the theme configuration file.
         * <p>
         * An input field will be generated in the theme editor screen with {@code theme.waila.plugin_[typeNamespace].[typePath].[fieldName]}
         * as the translation key.
         *
         * @param name         the name of the property
         * @param format       the format to use on the input field on the theme editor
         * @param defaultValue the default value of this property
         *
         * @see ITheme#processProperties(IThemeAccessor)
         */
        Builder<T> property(String name, IntFormat format, int defaultValue);

        /**
         * Creates a property of this theme type.
         * <p>
         * A property is a field member of the {@linkplain T theme} implementation that will be set based on the theme configuration file.
         * <p>
         * An input field will be generated in the theme editor screen with {@code theme.waila.plugin_[typeNamespace].[typePath].[fieldName]}
         * as the translation key.
         *
         * @param name         the name of the property
         * @param defaultValue the default value of this property
         *
         * @see ITheme#processProperties(IThemeAccessor)
         */
        Builder<T> property(String name, boolean defaultValue);

        /**
         * Creates a property of this theme type.
         * <p>
         * A property is a field member of the {@linkplain T theme} implementation that will be set based on the theme configuration file.
         * <p>
         * An input field will be generated in the theme editor screen with {@code theme.waila.plugin_[typeNamespace].[typePath].[fieldName]}
         * as the translation key.
         *
         * @param name         the name of the property
         * @param defaultValue the default value of this property
         *
         * @see ITheme#processProperties(IThemeAccessor)
         */
        Builder<T> property(String name, double defaultValue);

        /**
         * Creates a property of this theme type.
         * <p>
         * A property is a field member of the {@linkplain T theme} implementation that will be set based on the theme configuration file.
         * <p>
         * An input field will be generated in the theme editor screen with {@code theme.waila.plugin_[typeNamespace].[typePath].[fieldName]}
         * as the translation key.
         *
         * @param name         the name of the property
         * @param defaultValue the default value of this property
         *
         * @see ITheme#processProperties(IThemeAccessor)
         */
        Builder<T> property(String name, String defaultValue);

        /**
         * Creates a property of this theme type.
         * <p>
         * A property is a field member of the {@linkplain T theme} implementation that will be set based on the theme configuration file.
         * <p>
         * An input field will be generated in the theme editor screen with {@code theme.waila.plugin_[typeNamespace].[typePath].[fieldName]}
         * as the translation key.
         *
         * @param name         the name of the property
         * @param defaultValue the default value of this property
         *
         * @see ITheme#processProperties(IThemeAccessor)
         */
        <E extends Enum<E>> Builder<T> property(String name, E defaultValue);

        IThemeType<T> build();

    }

}
