/*
 * Recipe.java
 * by bluedart
 * 
 * An abstract class that all Atelier recipes must extend.  You can make your own, or simply
 * use BasicRecipe which is recommended.
 */

package pa.api.recipe;

import java.util.HashMap;

import net.minecraft.item.ItemStack;

public abstract class Recipe
{
	public abstract String getName();
	
	public abstract boolean matches(HashMap<String, Float> ingredients, ItemStack[] items);
	public abstract ItemStack craft(HashMap<String, Float> ingredients);
	
	public abstract void setDefaultItem(ItemStack stack);
	public abstract ItemStack getDefaultItem();
	public abstract HashMap<String, Float> getIngredients();
	public abstract float getDiscoveryModifier(ItemStack stack);
}