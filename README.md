# Here's What You're Looking At
[![Build Status](http://tehnut.info/jenkins/buildStatus/icon?job=HWYLA/1.10)](http://tehnut.info/jenkins/job/HWYLA/job/1.10/) [![License](https://img.shields.io/badge/license-CC%20BY--NC--SA%204.0-blue.svg)](https://bit.ly/cc-by-nc-sa-40) [![](http://cf.way2muchnoise.eu/HWYLA.svg)](https://minecraft.curseforge.com/projects/HWYLA)

##### **HWYLA** (pronounced "Hwhy-la", similar to "Coo-Hwhip") - a fork of [WAILA](https://minecraft.curseforge.com/projects/waila) by [ProfMobius](https://minecraft.curseforge.com/members/ProfMobius).

###### *This fork is permitted under the [CC BY-NC-SA 4.0](LICENSE.md) license. Usage of this mod is permitted in all modpacks.*

##### A full list of significant changes since this fork's creation can be found in the **[CHANGES.md](CHANGES.md)** document.

---

### Information For Developers

##### To use this fork in your workspace, add the following to your `build.gradle`:

```groovy
repositories {  
    maven {url "http://tehnut.info/maven"}
}

dependencies {
    deobfCompile "mcp.mobius.waila:Hwyla:<HWYLA_VERSION>"
}
```

> `HWYLA_VERSION` can be found by browsing through the maven.

> **!!!** Builds 8 through 12 use `mcp.mobius.waila:Waila:<HWYLA_VERSION>`.

As of Fabric Loader 0.4.0, Waila plugins are discovered from the `fabric.mod.json` file. Simply add a `custom` object field
to that file with the following data:

```json
{
  "waila:plugins": {
    "id": "mymod:my_plugin",
    "initializer": "foo.bar.Baz"
  }
}
```

`waila:plugins` can also be an array of objects instead of a singular object. 

A `required` field can be added to specify mods required for that plugin to be loaded. It can either be a single string 
or an array of strings.