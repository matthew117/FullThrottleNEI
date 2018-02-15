/*
 * BasicRecipe.java
 * by bluedart
 * 
 * This is a useful implementation of the abstract Recipe class.  Make one of these and
 * pass it into FTAAPI.addRecipe() and it will be added to the list of Atelier Recipes.
 */

package pa.api.recipe;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class BasicRecipe extends Recipe
{
	//The required elements to craft this recipe.
	protected HashMap<String, Float> required;
	
	//The default output this recipe will create.  If you wish to get more creative you may
	//want to implement your own Recipe class.
	protected ItemStack output;
	//The name of the recipe must be a unique identifier.
	protected String name;
	
	protected ArrayList<ItemStack> chances = new ArrayList<ItemStack>();
	
	public BasicRecipe(HashMap<String, Float> required, ItemStack output, String name, ArrayList<ItemStack> chances)
	{
		this.output = output;
		
		this.required = required;
		this.name = name;
		
		if (chances != null)
			this.chances = chances;
	}
	
	public BasicRecipe(HashMap<String, Float> required, ItemStack output, String name)
	{
		this(required, output, name, null);
	}
	
	@Override
	public void setDefaultItem(ItemStack stack) { this.output = stack; }
	
	@Override
	public ItemStack getDefaultItem() { return output.copy(); }
	
	@Override
	public String getName() { return name; }
	
	@Override
	public HashMap<String, Float> getIngredients()
	{
		return required;
	}
	
	//This is a basic calculation that the Atelier calls to see if it has the required
	//elements to craft this recipe.  Can be overridden in case you want to do something
	//crazy.
	@Override
	public boolean matches(HashMap<String, Float> ingredients, ItemStack[] items)
	{
		if (ingredients != null && required != null)
		{
			if (required.size() > 0)
			for (String reqName : required.keySet())
			{
				Float reqAmount = required.get(reqName);
				
				if (reqAmount == null)
					continue;
				
				Float amount = ingredients.get(reqName);
				
				if (amount != null && amount.floatValue() >= reqAmount.floatValue())
					continue;
				
				return false;
			}
		} else
			return false;
		
		return true;
	}
	
	//Returns a modifier from 0.0f-1.0f of how likely this recipe is to be discovered by
	//the elements that are present on the given ItemStack.  Mostly just used by the Atelier.
	//BasicRecipe just returns 1.0f if the ItemStack is one of the "chances" and 0.0f if
	//it is not.
	@Override
	public float getDiscoveryModifier(ItemStack stack)
	{
		if (stack != null && chances != null && chances.size() > 0)
		{
			for (int i = 0; i < chances.size(); i ++)
			{
				ItemStack chanceStack = chances.get(i);
				
				if (chanceStack != null && chanceStack.getItem() == stack.getItem() &&
				(chanceStack.getItemDamage() == stack.getItemDamage() || chanceStack
				.getItemDamage() == OreDictionary.WILDCARD_VALUE))
					return 1.0f;
			}
			
			return 0.0f;
		}
		
		return 1.0f;
	}
	
	//Just returns a copy of the recipe's default item.  If you were making a custom
	//dynamic Recipe you'd want to make it do something different to the ItemStack here.
	@Override
	public ItemStack craft(HashMap<String, Float> ingredients)
	{
		ItemStack output = this.getDefaultItem();
		
		if (output != null)
			output = output.copy();
		
		return output;
	}
}