package pa.api;

import net.minecraft.block.Block;

/*
 * IMagneticBlock.java
 * by bluedart
 * 
 * This class is responsible for telling the Magnet Glove which 'ores' it can move and how
 * difficult that task is per block.  To add an IMagneticBlock to the list, call
 * DartAPI.addMagneticBlock() and pass it one of these.
 */
public class IMagneticBlock
{
	/*
	 * The main constructor for IMagneticBlock.
	 * 
	 * int blockID - the BlockID of the block that will be pulled toward the player.
	 * 
	 * int meta - the meta that will respond to this recipe.  If OreDictionary.WILDCARD_VALUE
	 * is passed then any meta will be valid.
	 * 
	 * float level - The attractiveness of this block to the Magnet Glove.  A higher value
	 * will prioritize movement of this block closer to the player than others.
	 * A low value like lapis ore would be around 1.0f, and a high value like an anvil would
	 * be around 5.0f.
	 */
	public IMagneticBlock(Block block, int meta, float level)
	{
		this.block = block;
		this.blockMeta = meta;
		this.level = level;
	}
	
	public IMagneticBlock(Block block, float level)
	{
		this(block, 0, level);
	}
	
	public Block getBlock() { return this.block; }
	public int getMeta() { return this.blockMeta; }
	public float getLevel() { return this.level; }
	
	private Block block;
	private int blockMeta;
	private float level;
}
