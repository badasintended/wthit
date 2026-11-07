package mezz.jei.api.runtime;

import mezz.jei.api.recipe.IFocus;

public interface IRecipesGui {

    default <V> void show(IFocus<V> focus) {
    }

}
