/*
 * GrindStack.java
 * by bluedart
 * 
 * A useful implementation of IGrindRecipe.  Pass this into FTAAPI.addGrind() to add the
 * recipe to FTA's list of grindables.
 */

package pa.api.recipe;

import net.minecraft.item.ItemStack;
import pa.api.inventory.ItemInventoryUtils;

public class GrindStack implements IGrindRecipe
{
	public ItemStack input, output, bonus;
	public float chance;
	
	public GrindStack(ItemStack input, ItemStack output)
	{
		this(input, output, 0.0f, null);
	}
	
	public GrindStack(ItemStack input, ItemStack output, float chance, ItemStack bonus)
	{
		this.input = input;
		this.output = output;
		this.chance = chance;
		this.bonus = bonus;
	}
	
	public void setBonusAndChance(ItemStack bonus, float chance)
	{
		this.bonus = bonus;
		this.chance = chance;
	}
	
	@Override
	public boolean matches(ItemStack stack)
	{
		return ItemInventoryUtils.areStacksSame(input, stack);
	}
	
	@Override
	public ItemStack getInput()
	{
		return (input != null) ? input.copy() : null;
	}
	
	@Override
	public ItemStack getOutput()
	{
		return (output != null) ? output.copy() : null;
	}
	
	@Override
	public ItemStack getOutput(ItemStack input)
	{
		return (output != null) ? output.copy() : null;
	}
	
	@Override
	public ItemStack getBonus()
	{
		return (bonus != null) ? bonus.copy() : null;
	}
	
	@Override
	public float getChance()
	{
		return chance;
	}
}