package mcp.mobius.waila.api.impl;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.addons.core.HUDHandlerEntities;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.config.FormattingConfig;
import mcp.mobius.waila.config.OverlayConfig;
import mcp.mobius.waila.utils.Constants;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.util.*;

public class ConfigHandler implements IWailaConfigHandler {

    /* SINGLETON */
    private static ConfigHandler _instance = null;
    public Map<String, Boolean> forcedConfigs = new HashMap<String, Boolean>();
    public Configuration config = null;
    /* === */
    private LinkedHashMap<String, ConfigModule> modules = new LinkedHashMap<String, ConfigModule>();
    private ArrayList<String> serverconfigs = new ArrayList<String>();
    private ConfigHandler() {
        _instance = this;
    }

    public void addModule(String modName, HashMap<String, String> options) {
        this.addModule(modName, new ConfigModule(modName, options));
    }

    public void addModule(String modName, ConfigModule options) {
        this.modules.put(modName, options);
    }

    @Override
    public Set<String> getModuleNames() {
        return this.modules.keySet();
    }

    @Override
    public HashMap<String, String> getConfigKeys(String modName) {
        if (this.modules.containsKey(modName))
            return this.modules.get(modName).options;
        else
            return null;
    }

    private void saveModuleKey(String modName, String key) {
        this.saveModuleKey(modName, key, Constants.CFG_DEFAULT_VALUE);
    }

    private void saveModuleKey(String modName, String key, boolean defvalue) {
        config.get(Constants.CATEGORY_MODULES, key, defvalue);
        config.get(Constants.CATEGORY_SERVER, key, Constants.SERVER_FREE);
        config.save();
    }

    public void addConfig(String modName, String key, String name) {
        this.addConfig(modName, key, name, Constants.CFG_DEFAULT_VALUE);
    }

    public void addConfig(String modName, String key, String name, boolean defvalue) {
        this.saveModuleKey(modName, key, defvalue);

        if (!this.modules.containsKey(modName))
            this.modules.put(modName, new ConfigModule(modName));

        this.modules.get(modName).addOption(key, name);
    }

    public void addConfigServer(String modName, String key, String name) {
        this.addConfigServer(modName, key, name, Constants.CFG_DEFAULT_VALUE);
    }

    public void addConfigServer(String modName, String key, String name, boolean defvalue) {
        this.saveModuleKey(modName, key, defvalue);

        if (!this.modules.containsKey(modName))
            this.modules.put(modName, new ConfigModule(modName));

        this.modules.get(modName).addOption(key, name);
        this.serverconfigs.add(key);
    }

    @Override
    public boolean getConfig(String key) {
        return this.getConfig(key, Constants.CFG_DEFAULT_VALUE);
    }

    @Override
    public boolean getConfig(String key, boolean defvalue) {
        if (this.serverconfigs.contains(key) && !Waila.instance.serverPresent)
            return false;

        if (this.forcedConfigs.containsKey(key))
            return this.forcedConfigs.get(key);

        Property prop = config.get(Constants.CATEGORY_MODULES, key, defvalue);
        return prop.getBoolean(defvalue);
    }

    public boolean isServerRequired(String key) {
        return this.serverconfigs.contains(key);
    }

    public boolean getConfig(String category, String key, boolean default_) {
        Property prop = config.get(category, key, default_);
        return prop.getBoolean(default_);
    }

	
	
	
	
	
	/* GENERAL ACCESS METHODS TO GET/SET VALUES IN THE CONFIG FILE */

    public void setConfig(String category, String key, boolean state) {
        config.getCategory(category).put(key, new Property(key, String.valueOf(state), Property.Type.BOOLEAN));
        config.save();
    }

    public String getConfig(String category, String key, String default_) {
        Property prop = config.get(category, key, default_);
        return prop.getString();
    }

    public void setConfig(String category, String key, String value) {
        config.getCategory(category).put(key, new Property(key, value, Property.Type.STRING));
        config.save();
    }

    public int getConfig(String category, String key, int default_) {
        Property prop = config.get(category, key, default_);
        return prop.getInt();
    }

    public void setConfig(String category, String key, int state) {
        config.getCategory(category).put(key, new Property(key, String.valueOf(state), Property.Type.INTEGER));
        config.save();
    }

    /* Some accessor helpers */
    public boolean showTooltip() {
        return getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, true);
    }

    public boolean hideFromList() {
        return getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_HIDEFROMLIST, true);
    }

    public void loadDefaultConfig(FMLPreInitializationEvent event) {
        File configFile = new File(Waila.configDir, "waila.cfg");
        try {
            FileUtils.forceMkdir(Waila.configDir);
            File file = event.getSuggestedConfigurationFile();
            if (file.exists() && !configFile.exists())
                FileUtils.moveFile(file, configFile);
        } catch (Exception e) {
            Waila.LOGGER.error("Error migrating config to new location");
        }

        config = new Configuration(configFile);

        config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, true);
        config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE, true);
        config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, false);
        config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_METADATA, false);
        config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_KEYBIND, true);
        config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_NEWFILTERS, true);
        config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_HIDEFROMLIST, true);

        OverlayConfig.posX = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSX, 5000).getInt();
        OverlayConfig.posY = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSY, 100).getInt();
        OverlayConfig.scale = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SCALE, 100).getInt() / 100.0f;
        OverlayConfig.updateColors();

        FormattingConfig.modNameFormat = StringEscapeUtils.unescapeJava(config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODNAMEFORMAT, StringEscapeUtils.escapeJava("\u00A79\u00A7o%s")).getString());
        FormattingConfig.blockFormat = StringEscapeUtils.unescapeJava(config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_BLOCKNAMEFORMAT, StringEscapeUtils.escapeJava("\u00a7f%s")).getString());
        FormattingConfig.fluidFormat = StringEscapeUtils.unescapeJava(config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_FLUIDNAMEFORMAT, StringEscapeUtils.escapeJava("\u00a7f%s")).getString());
        FormattingConfig.entityFormat = StringEscapeUtils.unescapeJava(config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_ENTITYNAMEFORMAT, StringEscapeUtils.escapeJava("\u00a7f%s")).getString());
        FormattingConfig.metaFormat = StringEscapeUtils.unescapeJava(config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_METADATAFORMAT, StringEscapeUtils.escapeJava("\u00a77[%s@%d]")).getString());

        HUDHandlerEntities.nhearts = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_NHEARTS, 20).getInt();
        HUDHandlerEntities.maxhpfortext = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MAXHP, 40).getInt();

        config.getCategory(Constants.CATEGORY_MODULES).setComment("Those are the config keys defined in modules.\nServer side, it is used to enforce keys client side using the next section.");
        config.getCategory(Constants.CATEGORY_SERVER).setComment("Any key set to true here will ensure that the client is using the configuration set in the 'module' section above.\nThis is useful for enforcing false to 'cheating' keys like silverfish.");

        config.save();
    }
	
	
	
	/* Default config loading */

    public static ConfigHandler instance() {
        return _instance == null ? new ConfigHandler() : _instance;
    }
}
