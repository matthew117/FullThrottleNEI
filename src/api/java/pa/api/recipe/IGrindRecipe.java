package pa.api.recipe;

import net.minecraft.item.ItemStack;

/*
 * IForceGrindRecipe.java
 * by bluedart
 * 
 * IForceGrindRecipes are basically "macerator" or "pulverizer" recipes that can exist without other
 * mods installed and, in fact, take precedence over external mod recipes when applied.
 * 
 * Currently the only way to "Grind" things in DartCraft is to imbue a Force Tool with grinding and
 * manually break the block.  Due to implementations of Chunks and Dusts this is never meant to change.
 * To add a Force Grinding recipe pass an instance of this class into DartAPI.addForceGrindable().
 */
public interface IGrindRecipe
{
	public boolean matches(ItemStack stack);
	
	public ItemStack getInput();
	
	public ItemStack getOutput();
	
	public ItemStack getOutput(ItemStack input);
	
	public ItemStack getBonus();
	
	public float getChance();
	/*
	 * The main constructor of the IForceGrindRecipe.
	 * 
	 * ItemStack input - An ItemStack containing the exact itemID and damage (meta) that will result
	 * in triggering the output of this recipe.  It should be noted that if the meta is
	 * OreDictionary.WILDCARD_VALUE then any meta of the itemID would trigger the output.
	 * The stack size of the input is irrelevant, and is always 1.
	 * 
	 * ItemStack output - The exact output of this recipe, which will be multiplied by the actual
	 * input's stackSize to find the new stackSize.  NBTTagCompounds on this stack will carry over
	 * to the actual items dropped as well.
	 * 
	 * ItemStack bonus - A bonus ItemStack, that has a certain chance to drop when this recipe is
	 * triggered.  This can be, and often is, null.  Unlike output, bonus's practical stackSize
	 * is always the one passed through here.
	 * 
	 * float chance - The chance (0.0f - 1.0f) that the bonus will be dropped when this
	 * recipe is triggered.  This value is modified by another 0.1f per Luck upgrade on the tool.
	 * The base chance for most IForceGrindRecipes is 0.2f.
	 */
	/*
	public IForceGrindRecipe(ItemStack input, ItemStack output, ItemStack bonus, float chance)
	{
		this.input = input;
		this.output = output;
		this.bonus = bonus;
		this.chance = chance;
	}*/
	
	/*
	 * A simple, shorter version of the above constructor that has no chance for bonus output.
	 */
	/*
	public IForceGrindRecipe(ItemStack input, ItemStack output)
	{
		this(input, output, null, 0.0f);
	}
	
	//'getter' methods return a copy of the ItemStack to prevent accidental modification.
	public ItemStack getInput() { return input != null ? input.copy() : null; }
	public ItemStack getOutput() { return output != null ? output.copy() : null; }
	public ItemStack getBonus() { return bonus != null ? bonus.copy() : null; }
	public float getChance() { return chance; }
	
	public void setBonusAndChance(ItemStack bonus, float chance)
	{
		this.bonus = bonus;
		this.chance = chance;
	}
	public void setBonus(ItemStack bonus) { this.bonus = bonus; }
	public void setChance(float chance) { this.chance = chance; }
	
	private ItemStack input, output, bonus;
	private float chance;*/
}
