# Getting Started

## Gradle Setup
To get started making a WTHIT plugin, add the following to your `build.gradle`

### Adding Repository
```groovy
repositories {
  maven {
    url "https://maven2.bai.lol"
    content {
      includeGroup "lol.bai"
      includeGroup "mcp.mobius.waila"
    }
  }
}
```
    

### Declaring Dependencies
Mod versions can be found on Modrinth: [WTHIT](https://modrinth.com/mod/wthit/versions), [Bad Packets](https://modrinth.com/mod/badpackets/versions)

=== "Fabric"
    ```gradle
    dependencies {
      // compile against the API
      modCompileOnly "mcp.mobius.waila:wthit-api:fabric-${wthitVersion}"

      // run against the full jar
      modRuntimeOnly "mcp.mobius.waila:wthit:fabric-${wthitVersion}"
      modRuntimeOnly "lol.bai:badpackets:fabric-${badpacketsVersion}"
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
      runtimeOnly fg.deobf("lol.bai:badpackets:forge-${badpacketsVersion}")
    }
    ```
=== "NeoForge"
    ```gradle
    dependencies {
      // compile against the API
      compileOnly "mcp.mobius.waila:wthit-api:neo-${wthitVersion}"

      // run against the full jar
      runtimeOnly "mcp.mobius.waila:wthit:neo-${wthitVersion}"
      runtimeOnly "lol.bai:badpackets:neo-${badpacketsVersion}"
    }
    ```
=== "Quilt"
    ```gradle
    dependencies {
      // compile against the API
      modCompileOnly "mcp.mobius.waila:wthit-api:quilt-${wthitVersion}"

      // run against the full jar
      modRuntimeOnly "mcp.mobius.waila:wthit:quilt-${wthitVersion}"
      modRuntimeOnly "lol.bai:badpackets:fabric-${badpacketsVersion}"
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
      modRuntimeOnly "lol.bai:badpackets:fabric-${badpacketsVersion}"
    }
    ```
    ```gradle title="Forge Project"
    dependencies {
      modRuntimeOnly "mcp.mobius.waila:wthit:forge-${wthitVersion}"
      modRuntimeOnly "lol.bai:badpackets:forge-${badpacketsVersion}"
    }
    ```
    ```gradle title="Quilt Project"
    dependencies {
      modRuntimeOnly "mcp.mobius.waila:wthit:quilt-${wthitVersion}"
      modRuntimeOnly "lol.bai:badpackets:fabric-${badpacketsVersion}"
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
      modRuntimeOnly "lol.bai:badpackets:fabric-${badpacketsVersion}"
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
      runtimeOnly fg.deobf("mcp.mobius.waila:wthit:forge-${wthitVersion}")
      runtimeOnly fg.deobf("lol.bai:badpackets:forge-${badpacketsVersion}")
    }
    ```
    ```gradle title="NeoForge Project"
    dependencies {
      runtimeOnly "mcp.mobius.waila:wthit:neo-${wthitVersion}"
      runtimeOnly "lol.bai:badpackets:neo-${badpacketsVersion}"
    }
    ```
    ```gradle title="Quilt Project"
    dependencies {
      modRuntimeOnly "mcp.mobius.waila:wthit:quilt-${wthitVersion}"
      modRuntimeOnly "lol.bai:badpackets:fabric-${badpacketsVersion}"
    }
    ```

???+ note "Why compiling against the API jar?"
     When you compile against the full jar and use non API classes, your mod could break any time WTHIT updates.
     On the other hand, the API jar is guaranteed to be stable. No breaking changes without deprecation time.

     If you found yourself needing to touch non API classes, [open an issue on GitHub](https://github.com/badasintended/wthit/issues/new?assignees=&labels=api&template=api.md&title=).

??? note "Available packages"
    All packages has `mcp.mobius.waila` as their group.

    | Package                       | Description                                                      |
    |:------------------------------|:-----------------------------------------------------------------|
    | `wthit-api:fabric-${version}` | Intermediary API jar for Loom projects                           |
    | `wthit-api:forge-${version}`  | SRG API jar for ForgeGradle projects                             |
    | `wthit-api:neo-${version}`    | Mojang Mappings API jar for NeoGradle projects                   |
    | `wthit-api:quilt-${version}`  | Intermediary API jar for Quilt-Loom projects                     |
    | `wthit-api:mojmap-${version}` | Mojang Mappings API jar for VanillaGradle projects               |
    | `wthit:fabric-${version}`     | Full runtime jar for Fabric                                      |
    | `wthit:forge-${version}`      | Full runtime jar for Forge                                       |
    | `wthit:neo-${version}`        | Full runtime jar for NeoForge                                    |
    | `wthit:quilt-${version}`      | Full runtime jar for Quilt                                       |
    | `wthit:mojmap-${version}`     | Full platform independent jar for internal implementation access |
    

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
Create a file called `waila_plugins.json` or `wthit_plugins.json` in the root of your mod, commonly in `src/main/resources` folder on your project.
```json
{
  // the plugin identifier, [namespace:path]
  "yourmodid:plugin": {
    // the path to the implementation class
    "initializer": "package.YourWailaPlugin",

    // optional, decide the environment the plugin will loaded, options:
    // client    load plugin only on client and integrated server
    // server    load plugin only on dedicated server
    // *         load plugin on both client and dedicated server
    "side": "*",

    // optional, the required mods that this plugin needs
    "required": ["othermodid", "anotherone"]
  },

  // register multiple plugins!
  "yourmodid:another": { /*...*/ }
}
```
