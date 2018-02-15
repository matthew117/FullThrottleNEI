package pa.api.recipe;

import net.minecraft.item.ItemStack;

/*
 * IFreezableBlock.java
 * by bluedart
 * 
 * A class that holds the data responsible for telling Ice Arrows what to freeze.
 * Currently this triggers when an Ice Arrow collides with or passes through a block.
 */
public interface IFreezeRecipe
{
	public boolean matches(ItemStack input);
	
	public ItemStack getInput();
	
	public ItemStack getOutput(ItemStack input);
	
	public ItemStack getOutput();
	
	public ItemStack getBonus();
	
	public float getChance();
	
	public boolean isHidden();
	/*
	public int blockID, blockMeta;
	public int resultID, resultMeta;*/
	
	/*
	 * The constructor for this class, pass this object into DartAPI.addFreezable() to
	 * add functionality to Ice Arrows.
	 * 
	 * int blockID - the BlockID of the target block.  This must be within range of
	 * Block.blocksList or it will refuse to add.
	 * 
	 * int blockMeta - the meta of the block to be frozen.  If this is OreDictionary.WILDCARD_VALUE
	 * any meta will do, otherwise it must match exactly.
	 * 
	 * int resultID - the resultant blockID the block in the world will be changed to.  Must be
	 * within range of Block.blocksList or this recipe will not add.
	 * 
	 * int resultMeta - the resultant meta the block in the world will be changed to.
	 */
	/*
	public IFreezeRecipe(int blockID, int blockMeta, int resultID, int resultMeta)
	{
		this.blockID = blockID;
		this.blockMeta = blockMeta;
		this.resultID = resultID;
		this.resultMeta = resultMeta;
	}*/
	
	/*
	 * An additional constructor that bypasses target meta altogether in favor of declaring
	 * all meta values valid targets for this recipe.
	 */
	/*
	public IFreezeRecipe(int blockID, int resultID, int resultMeta)
	{
		this(blockID, OreDictionary.WILDCARD_VALUE, resultID, resultMeta);
	}
	
	public boolean getBlockMatches(int id, int meta)
	{
		return id == this.blockID && (meta == this.blockMeta || this.blockMeta == OreDictionary.WILDCARD_VALUE);
	}*/
}
