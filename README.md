# What The Hell Is That?
[![License](https://img.shields.io/badge/license-CC%20BY--NC--SA%204.0-blue.svg)](https://bit.ly/cc-by-nc-sa-40)

**WTHIT** - a fork of [HWYLA](https://minecraft.curseforge.com/projects/hwyla) by [TehNut](https://www.curseforge.com/members/tehnut) which is a fork of [WAILA](https://minecraft.curseforge.com/projects/waila) by [ProfMobius](https://minecraft.curseforge.com/members/ProfMobius).

*This fork is permitted under the [CC BY-NC-SA 4.0](LICENSE.md) license. Usage of this mod is permitted in all modpacks.*

---

## Creating a WTHIT Plugin

### Add the bad maven to your `build.gradle`

```groovy
repositories {  
  maven { url "https://bai.jfrog.io/artifactory/maven" }
}
```

### Add WTHIT as a dependency
<details>
  <summary>Fabric</summary>

  ```groovy
  dependencies {
    modImplementation "mcp.mobius.waila:wthit-fabric:${wthit_version}"
  }
  ```
 
</details>
  
<details>
  <summary>Forge</summary>

  ```groovy
  dependencies {
    compile fg.deobf("mcp.mobius.waila:wthit-forge:${wthit_version}")
  }
  ```

</details>

<details>
  <summary>Architectury</summary>

  in common subproject:
  ```groovy
  dependencies {
    modCompileOnly "mcp.mobius.waila:wthit-common:${wthit_version}"
  }
  ```
  in patform subprojects:
  ```groovy
  dependencies {
    modRuntimeOnly "mcp.mobius.waila:wthit-${name}:${wthit_version}"
  }
  ```

</details>

### Make a class that implements `IWailaPlugin`
```java
public class ExamplePlugin implements IWailaPlugin {
  @Override
  public void register(IRegistrar registrar) {
      // register your component here
  }
}
```

### Register your plugin

<details>
  <summary>Fabric</summary>

  In your `fabric.mod.json` add a custom value
  ```json5
  {
    "waila:plugins": {
      "id": "mymod:my_plugin",
      "initializer": "foo.bar.Baz",
    }
  }
  ```
  `waila:plugins` can also be an array of objects instead of a singular object.    
  A required field can be added to specify mods required for that plugin to be loaded.
  It can either be a single string or an array of strings.
  ```json5
  {
    "waila:plugins": {
      "id": "mymod:my_plugin",
      "initializer": "foo.bar.Baz",
      "required": "mod_a" 
    }
  }
  ```

</details>

<details>
  <summary>Forge</summary>

  In your `mods.toml`
  ```toml
  [[wailaPlugins]]
  id = "mymod:my_plugin1"
  initializer = "com.example.MyPlugin1"

  # with dependency
  [[wailaPlugins]]
  id = "mymod:my_plugin2"
  initializer = "com.example.MyPlugin2"
  required = "mod_a"

  # also accept an array of dependencies
  [[wailaPlugins]]
  id = "mymod:my_plugin3"
  initializer = "com.example.MyPlugin3"
  required = ["mod_a", "mod_b"]
  ```

  **`@WailaPlugin` annotation is deprecated and will be removed in future releases**

</details>

### (Optional) Add WTHIT as a mod dependency
Both on fabric and forge, you can use either `wthit` or `waila` as a dependency in your mod metadata.
Though if you use any WTHIT specific APIs, I suggest depending on `wthit` instead.

<details>
  <summary><code>fabric.mod.json</code></summary>

  ```json5
  "depends": {
    "wthit": ">=2",

    // or
    "waila": ">=2"
  }
  ```

</details>

<details>
  <summary><code>mods.toml</code></summary>

  ```toml
  [[dependencies.examplemod]]
  modId = "wthit" # or waila
  versionRange="[2,)"
  ```

</details>
