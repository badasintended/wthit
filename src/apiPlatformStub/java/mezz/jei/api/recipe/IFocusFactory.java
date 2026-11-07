package mezz.jei.api.recipe;

import mezz.jei.api.ingredients.IIngredientType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface IFocusFactory {

    <V> IFocus<V> createFocus(RecipeIngredientRole role, IIngredientType<V> ingredientType, V ingredient);

}
