# Here's What You're Looking At

[![License](https://img.shields.io/badge/license-CC%20BY--NC--SA%204.0-blue.svg)](https://bit.ly/cc-by-nc-sa-40) [![](http://cf.way2muchnoise.eu/HWYLA.svg)](https://minecraft.curseforge.com/projects/HWYLA)

##### **HWYLA** (pronounced "Hwhy-la", similar to "Coo-Hwhip") - a fork of [WAILA](https://minecraft.curseforge.com/projects/waila) by [ProfMobius](https://minecraft.curseforge.com/members/ProfMobius).

##### The IMC registration system requires you to change the `Waila` modid to `waila` as Forge now enforces lowercase modids. If your plugin magically stops working during your 1.10 -> 1.11 transition, this is likely why. 

###### *This fork is permitted under the [CC BY-NC-SA 4.0](LICENSE.md) license. Usage of this mod is permitted in all modpacks.*

##### A full list of significant changes since this fork's creation can be found in the **[CHANGES.md](CHANGES.md)** document.

---

### Information For Developers

##### To use this fork in your workspace, add the following to your `build.gradle`:

```groovy
repositories {  
    maven { url "https://maven.tehnut.info" }
}

dependencies {
    // Compile against the Hwyla API, but do not include it at runtime
    compileOnly fg.deobf("mcp.mobius.waila:Hwyla:<HWYLA_VERSION>:api")
    // At runtime, use the full Hwyla jar
    runtimeOnly fg.deobf("mcp.mobius.waila:Hwyla:<HWYLA_VERSION>")
}
```

> `HWYLA_VERSION` can be found by checking CurseForge.