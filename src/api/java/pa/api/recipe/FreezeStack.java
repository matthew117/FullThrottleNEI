/*
 * FreezeStack.java
 * by bluedart
 * 
 * A useful implementation of IFreezeRecipe.  Pass this into FTAAPI.addFreeze() to add the
 * recipe to FTA's list of freezables.
 */

package pa.api.recipe;

import net.minecraft.item.ItemStack;
import pa.api.inventory.ItemInventoryUtils;

public class FreezeStack implements IFreezeRecipe
{
	public ItemStack input;
	public ItemStack output;
	public ItemStack bonus;
	
	//Chance should be set to 0.0f-1.0f.  This is the basic chance that the bonus ItemStack
	//will be created as well.
	public float chance;
	public boolean hidden;
	
	public FreezeStack(ItemStack input, ItemStack output, boolean hidden)
	{
		this(input, output, 0.0f, null, hidden);
	}
	
	public FreezeStack(ItemStack input, ItemStack output, float chance, ItemStack bonus, boolean hidden)
	{
		this.input = input;
		this.output = output;
		
		this.chance = chance;
		this.bonus = bonus;
		
		this.hidden = hidden;
	}
	
	@Override
	public boolean matches(ItemStack input)
	{
		return ItemInventoryUtils.areStacksSame(this.input, input);
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
	public float getChance() { return chance; }
	
	@Override
	public boolean isHidden() { return hidden; }
}