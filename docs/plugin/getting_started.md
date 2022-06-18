# Getting Started

## Gradle Setup
To get started making a WTHIT plugin, add the following to your `build.gradle`

### Adding Repository
```groovy
repositories {
  maven { url "https://maven.bai.lol" }
}
```

### Declaring Dependencies
=== "Fabric"
    ```gradle
    dependencies {
      // compile against the API
      modCompileOnly "mcp.mobius.waila:wthit-api:fabric-${wthitVersion}"

      // run against the full jar
      modRuntimeOnly "mcp.mobius.waila:wthit:fabric-${wthitVersion}"
      modRuntimeOnly "lol.bai:badpackets:fabric-0.1.2"
    }
    ```
=== "Forge"
    ```gradle 
    buildscript {
      dependencies {
        classpath "org.spongepowered:mixingradle:0.7.+"
      }
    }

    apply plugin: "org.spongepowered.mixin"
    
    dependencies {
      // compile against the API
      compileOnly fg.deobf("mcp.mobius.waila:wthit-api:forge-${wthitVersion}")

      // run against the full jar
      runtimeOnly fg.deobf("mcp.mobius.waila:wthit:forge-${wthitVersion}")
      runtimeOnly fg.deobf("lol.bai:badpackets:forge-0.1.2")
    }
    ```
=== "Architectury"
    ```gradle title="Common Project"
    dependencies {
      modCompileOnly "mcp.mobius.waila:wthit-api:fabric-${wthitVersion}"
    }
    ```
    ```gradle title="Fabric Project"
    dependencies {
      modRuntimeOnly "mcp.mobius.waila:wthit:fabric-${wthitVersion}"
      modRuntimeOnly "lol.bai:badpackets:fabric-0.1.2"
    }
    ```
    ```gradle title="Forge Project"
    dependencies {
      // needed for @WailaPlugin annotation
      modCompileOnly "mcp.mobius.waila:wthit-api:forge-${wthitVersion}"
      modRuntimeOnly "mcp.mobius.waila:wthit:forge-${wthitVersion}"
      modRuntimeOnly "lol.bai:badpackets:forge-0.1.2"
    }
    ```
=== "VanillaGradle Multiplatform"
    ```gradle title="Common Project"
    dependencies {
      compileOnly "mcp.mobius.waila:wthit-api:mojmap-${wthitVersion}"
    }
    ```
    ```gradle title="Fabric Project"
    dependencies {
      modRuntimeOnly "mcp.mobius.waila:wthit:fabric-${wthitVersion}"
      modRuntimeOnly "lol.bai:badpackets:fabric-0.1.2"
    }
    ```
    ```gradle title="Forge Project"
    buildscript {
      dependencies {
        classpath "org.spongepowered:mixingradle:0.7.+"
      }
    }

    apply plugin: "org.spongepowered.mixin"
    
    dependencies {
      // needed for @WailaPlugin annotation
      compileOnly fg.deobf("mcp.mobius.waila:wthit-api:forge-${wthitVersion}")
      runtimeOnly fg.deobf("mcp.mobius.waila:wthit:forge-${wthitVersion}")
      runtimeOnly fg.deobf("lol.bai:badpackets:forge-0.1.2")
    }
    ```

???+ note "Why compiling against the API jar?"
     When you compile against the full jar and use non API classes, your mod could break any time WTHIT updates.
     On the other hand, the API jar is guaranteed to be stable. No breaking changes without deprecation time.

     If you found yourself needing to touch non API classes, [open an issue on GitHub](https://github.com/badasintended/wthit/issues/new?assignees=&labels=api&template=api.md&title=).


## Creating Plugins

### Making a Plugin Class
Make a class that implements `IWailaPlugin`
```java
public class MyWailaPlugin implements IWailaPlugin {
  @Override
  public void register(IRegistrar registrar) {
      // register your component here
  }
}
```


### Registering Plugins
=== "Fabric"
    In your `fabric.mod.json` add a custom value
    ```json
    "custom": {
      "waila:plugins": {
        "id": "mymod:my_plugin",
        "initializer": "mymod.waila.MyModWailaPlugin",
      }
    }
    ```
    `waila:plugins` can also be an array of objects instead of a singular object.    
    
    A `required` field can be added to specify mods required for that plugin to be loaded.
    It can either be a single string or an array of strings.
    ```json
    "custom": {
      "waila:plugins": {
        "id": "mymod:my_plugin",
        "initializer": "mymod.waila.MyModWailaPlugin",
        "required": "mod_a" 
      }
    }
    ```
    
    An `environment` field can be added to specify what environment the plugin would be loaded.
    Possible values are `client` for client-only plugin, `server` for **dedicated** server plugin,
    and `both` for common plugin.
    ```json
    "custom": {
      "waila:plugins": {
        "id": "mymod:client_only_plugin",
        "initializer": "mymod.waila.ClientOnlyPlugin",
        "environment": "client"
      }
    }
    ```

=== "Forge"
    Annotate your plugin class with `@WailaPlugin`:
    ```java
    @WailaPlugin(id = "mymod:waila_plugin")
    public class MyWailaPlugin implements IWailaPlugin {}
    ```
    A `required` array can be added to specify mods required for that plugin to be loaded.
    ```java
    @WailaPlugin(id = "mymod:waila_plugin", required = "jei")
    public class MyWailaPlugin implements IWailaPlugin {}
    ```

    A `side` value can be added to specify what environment the plugin would be loaded.
    ```java
    @WailaPlugin(id = "mymod:client_only_plugin", side = IPluginInfo.Side.CLIENT)
    public class ClientOnlyPlugin implements IWailaPlugin {}
    ```
    !!! warning "For multiplatform projects"
        If you put your plugin class in the common subproject,
        you'd need to extend your plugin class in the forge project and annotate it:
        ```java
        @WailaPlugin(id = "mymod:waila_plugin")
        public class ForgeWailaPlugin extends CommonWailaPlugin {}
        ```
