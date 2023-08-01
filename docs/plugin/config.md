# Plugin Configuration

Not everyone wanted every feature from your plugin, so making them configurable is recommended.

## Config Types

`IRegistrar` has multiple methods for registering a config option:

### `addConfig`
This method registers a local-only option. This is recommended for cosmetic-only options.

### `addSyncedConfig`
This method registers a server option. The server will force connected clients to have the same value. 
If the client is connected to a server that doesn't have WTHIT, its value will be locked to the specified client-only value.

### `addMergedConfig`
Like `addSyncedConfig`, the server will send its option value to connected clients but it won't force the value.
When the server enabled the option, the client can freely disable it for their side. Recommended for toggle for a feature that works client-only.

### `addMergedSyncedConfig`
This is pretty much the same as `addMergedConfig` but with the additional rule that the server needs to have WTHIT installed, 
otherwise, the option will be locked to a predetermined value. Recommended for toggle for a feature that needs server-synced data to work properly.

## Translation Keys
These translation keys will be used on the plugin config screen:

- `config.waila.plugin_[namespace].[path]` for the config name.
- `config.waila.plugin_[namespace].[path]_desc` for the config description.
  This one is optional and can be left missing.

Config options can also be grouped with the same prefix on its path followed by a period [`.`],
e.g. `my_plugin:group.option1` and `my_plugin:group.option2`.

Then, add a translation for `config.waila.plugin_[namespace].[group]` with the group name
for it to be visible in the config screen.
