# What The Hell Is That?
[![License](https://img.shields.io/badge/license-CC%20BY--NC--SA%204.0-blue.svg)](https://bit.ly/cc-by-nc-sa-40)

**WTHIT** - a fork of [HWYLA](https://minecraft.curseforge.com/projects/hwyla) by [TehNut](https://www.curseforge.com/members/tehnut) which is a fork of [WAILA](https://minecraft.curseforge.com/projects/waila) by [ProfMobius](https://minecraft.curseforge.com/members/ProfMobius).

*This fork is permitted under the [CC BY-NC-SA 4.0](LICENSE.md) license. Usage of this mod is permitted in all modpacks.*

---

- **Add the bad maven to your `build.gradle`**

```groovy
repositories {  
    // blame github for requiring an auth for accessing artifacts
    maven { url "https://gitlab.com/api/v4/projects/25106863/packages/maven" }
}
```

### Fabric    
- **Add WTHIT as a dependency**
  ```groovy
  dependencies {
    modImplementation "mcp.mobius.waila:wthit-fabric:${wthit_version}"
  }
  ```

- **Make a class that implements `IWailaPlugin`**
  ```java
  public class ExamplePlugin implements IWailaPlugin
  ```

- **Add `custom` object on `fabric.mod.json` with following data**
  ```json
  {
    "waila:plugins": {
      "id": "mymod:my_plugin",
      "initializer": "com.example.ExamplePlugin"
    }
  }
  ```
  `waila:plugins` can also be an array of objects instead of a singular object.    
  A `required` field can be added to specify mod ids required.    
  It can either be a string or an array of strings.    


### Forge    
- **Add WTHIT as a dependency**
  ```groovy
  dependencies {
    modImplementation "mcp.mobius.waila:wthit-forge:${wthit_version}"
  }
  ```

- **Make a class that implements `IWailaPlugin` and annotate it with `WailaForgePlugin`**
  ```java
  @WailaForgePlugin("mymod:my_plugin")
  public class ExamplePlugin implements IWailaPlugin
  ```
  `@WailaPlugin` was deprecated to make a value id mandatory and make `requires` accepts an array.


### Architectury
- **In your common module, add WTHIT as a compile dependency**
  ```groovy
  dependencies {
    modCompileOnly "mcp.mobius.waila:wthit-common:${wthit_version}"
  }
  ```

- **In your platform module, add WTHIT as a runtime dependency**
  ```groovy
  dependencies {
    modRuntimeOnly "mcp.mobius.waila:wthit-${name}:${wthit_version}"
  }
  ```

- **Do the same thing as in [Fabric](#fabric) and [Forge](#forge)**    
  If you need to listen to WTHIT events, do it in your platform modules and
  replace `modRuntimeOnly` with `modImplementation`
