package com.terraformersmc.modmenu.api;

public interface ModMenuApi {

    default ConfigScreenFactory<?> getModConfigScreenFactory() {
        throw new AssertionError("stub");
    }

}
