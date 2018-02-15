package pa.api.inventory;

import java.lang.reflect.Method;
import java.util.ArrayList;

import com.google.common.base.Objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.oredict.OreDictionary;
/*
 * ItemInventoryUtils.java
 * by bluedart
 * 
 * A specific set of functions dealing with the creation and modification of DartCraft's special
 * ItemInventory items such as Clipboards, Force Packs and Item Cards.  Using these methods it is
 * possible to retrieve one of these items saved to an ItemStack by simply passing in some ItemStacks,
 * saving a serious amount of time and hassle, as well as preventing unnecessary errors.
 */
public class ItemInventoryUtils
{
	/*
	 * This method will return an ItemStack of a Spoils Bag containing the ArrayList of ItemStacks
	 * passed in.  Spoils Bags have a maximum size of 8, therefore only the first 8 ItemStacks
	 * passed into this method will be stored, and the rest will be ignored.
	 */
	public static ItemStack getSpoilsBag(ArrayList<ItemStack> contained)
	{
		//Create the ItemStack.
		ItemStack loot = new ItemStack(bagItem, 1, 0);
		initializeInv(loot, bagSize, bagSize);
		
		//Make an ItemInventory from this newly-created stack to easily modify its contents.
		ItemInventory inv = new ItemInventory(bagSize, loot);
		
		if (contained != null && contained.size() > 0)
		{
			for (int i = 0; i < contained.size() && i < 8; i ++)
			{
				ItemStack tempStack = contained.get(i);
				
				if (tempStack == null || tempStack.getItem() == bagItem)
					continue;
				
				inv.setInventorySlotContents(i, tempStack);
			}
			
			//Save ItemInventory modification.
			inv.save();
		}
		
		return loot;
	}
	
	//Used to read an arry of ItemStack from a particular NBTTagCompound.
	//Returns the array if the syntax was correct, or null otherwise.
	public static ItemStack[] readItemsFromNBT(NBTTagCompound comp)
	{
		if (comp == null)
			return null;
		
		//ItemStack[] returnVal = new ItemStack[]
		ItemStack[] returnVal = null;
		ArrayList<ItemStack> buffer = new ArrayList<ItemStack>();
		
		NBTTagList contentList = comp.getTagList("contents", (byte)10);
		for (int i = 0; i < contentList.tagCount(); i ++)
		{
			//boolean append = !(contentList.tagAt(i) == null ||
			//	!(contentList.tagAt(i) instanceof NBTTagCompound));
			ItemStack tempStack = null;
			
			if (contentList.getCompoundTagAt(i) != null && contentList.getCompoundTagAt(i) instanceof
			NBTTagCompound)
				tempStack = ItemStack.loadItemStackFromNBT((NBTTagCompound)contentList.getCompoundTagAt(i));
			
			buffer.add(tempStack);
		}
		
		if (buffer.size() > 0)
		{
			returnVal = new ItemStack[buffer.size()];
			
			for (int i = 0; i < buffer.size(); i ++)
				returnVal[i] = buffer.get(i) != null ? buffer.get(i).copy() : null;
		}
		
		return returnVal;
	}
	
	//Used to save an entire array of ItemStack to an NBTTagCompound.
	//The compound is then returned.
	public static NBTTagCompound saveItemsToNBT(ItemStack[] items)
	{
		NBTTagCompound comp = new NBTTagCompound();
		
		if (items == null || items.length < 1)
			return comp;
		
		NBTTagList contents = new NBTTagList();
		
		for (int i = 0; i < items.length; i ++)
		{
			NBTTagCompound tempComp = new NBTTagCompound();
			if (items[i] != null)
				items[i].writeToNBT(tempComp);
			
			contents.appendTag(tempComp);
		}
		
		comp.setTag("contents", contents);
		
		return comp;
	}
	/*
	 * Returns true if the given ItemStack is registered on the Forge Ore Dictionary and
	 * there is at least one other entry with the same name.
	 */
	public static boolean isForgeItem(ItemStack stack)
	{
		if (stack == null)
			return false;
		
		int id = OreDictionary.getOreID(stack);
		if (id >= 0)
		{
			ArrayList<ItemStack> ores = OreDictionary.getOres(id);
			return ores != null && ores.size() > 1;
		}
		
		return false;
	}
	/*
	 * Returns true if the first ItemStack and second ItemStack are either literally equivalent
	 * or marked equivalent by the OreDictionary.  The first ItemStack will ignore meta when
	 * checking the second if the meta passed for the first is OreDictionary.WILDCARD_VALUE.
	 */
	public static boolean isItemEquivalent(ItemStack first, ItemStack second)
	{
		if (OreDictionary.itemMatches(first, second, false))
			return true;
		
		if (first == null || second == null)
			return false;
		
		ItemStack contained = null;
		ItemStack contained2 = null;
		
		try
		{
			contained = (ItemStack)getContainerItem.invoke(utilsClass, new Object[] { first });
			contained2 = (ItemStack)getContainerItem.invoke(utilsClass, new Object[] { second });
		} catch (Exception e) {}
		
		if (contained != null && contained2 != null)
		{
			if (contained.getItem() == first.getItem() && contained2.getItem() == second.getItem() &&
			contained.getItem()== contained2.getItem())
				return true;
		}
		
		int firstID = OreDictionary.getOreID(first);
		//int secondID = OreDictionary.getOreID(second);
		
		if (firstID > 0)
		{
			ArrayList<ItemStack> firstOres = OreDictionary.getOres(firstID);
			
			if (firstOres != null && firstOres.size() > 0)
				for (ItemStack tempStack : firstOres)
					if (OreDictionary.itemMatches(tempStack, second, false))
						return true;
		}
		
		return false;
	}
	
	/*
	 * Use this method to create a Force Pack with the given size, color and contents.
	 * This method is intelligent enough to extrapolate the appropriate size of the pack.
	 * If more than 40 items are passed into this function they will be truncated.
	 * Color must be a valid integer from 0-15.
	 */
	public static ItemStack getDartPack(ArrayList<ItemStack> items, String name, int color, boolean sturdy)
	{
		int size = items != null ? items.size() : 0;
		
		if (size % 8 != 0)
			size = size - (size % 8) + 8;
		
		if (size < 8)
			size = 8;
		
		if (size > 40)
			size = 40;
		
		ItemStack packStack = new ItemStack(packItem, 1, color);
		initializeInv(packStack, maxPackSize, size);
		
		if (name != null && !name.equals(""))
			packStack.getTagCompound().setString("name", name);
		
		if (items == null || size < items.size())
			return packStack;
		
		ItemInventory inv = new ItemInventory(size, packStack);
		
		if (items != null && items.size() > 0)
			for (int i = 0; i < items.size(); i ++)
				inv.setInventorySlotContents(i, items.get(i));
		
		inv.save();
		packStack.getTagCompound().removeTag("ID");
		
		//if (sturdy)
		//	UpgradeHelper.setUpgradeData(packStack, "Sturdy", 1);
		
		return packStack;
	}
	
	/*
	 * Initializes a Force Pack with the given initial size.
	 */
	public static void initializeInv(ItemStack pack, int maxSize, int size)
	{
		if (pack == null || maxSize <= 0 || size <= 0)
			return;
		
		//Initialize the NBTTagCompound as a series of ItemStacks.
		if (!pack.hasTagCompound())
			pack.setTagCompound(new NBTTagCompound());
		
		NBTTagCompound comp = pack.getTagCompound();
		
		if (maxSize > 0)
			comp.setInteger("size", size);
		
		NBTTagList contents = new NBTTagList();
		
		for (int i = 0; i < maxSize; i ++)
		{
			NBTTagCompound itemComp = new NBTTagCompound();
			itemComp.setByte("Slot", (byte) i);
			contents.appendTag(itemComp);
		}
		
		comp.setTag("contents", contents);
	}
	
	/*
	 * Returns an ItemTileBox serialized Chest with the given contents stored inside it.
	 */
	public static ItemStack getBoxedChest(ArrayList<ItemStack> items)
	{
		ItemStack chestStack = new ItemStack(tileBoxItem, 1, 0);
		chestStack.setTagCompound(new NBTTagCompound());
		
		TileEntityChest chest = new TileEntityChest();
		IInventory inv = (IInventory)chest;
		
		if (items != null && items.size() > 0)
			for (int i = 0; i < (items.size() < 28 ? items.size() : 27); i ++)
				inv.setInventorySlotContents(i, items.get(i));
		
		NBTTagCompound chestComp = new NBTTagCompound();
		chest.writeToNBT(chestComp);
		chestStack.getTagCompound().setTag("tile", chestComp);
		
		ItemStack blockStack = new ItemStack(Blocks.chest);
		chestStack.getTagCompound().setTag("block", blockStack.writeToNBT(new NBTTagCompound()));
		
		return chestStack;
	}
	
	/*
	 * Returns an upgrade core of the specified type.
	 */
	public static ItemStack getCore(String type, int level, int num)
	{
		//if (Item.itemsList == null || Item.itemsList.length <= coreID || coreID < 0)
		//	return null;
		
		if (num > coreItem.getItemStackLimit())
			num = coreItem.getItemStackLimit();
		
		ItemStack coreStack = new ItemStack(coreItem, num, 0);
		coreStack.setTagCompound(new NBTTagCompound());
		coreStack.getTagCompound().setTag("upgrades", new NBTTagCompound());
		
		//NBTTagCompound upgrades = UpgradeHelper.getUpgradeCompound(coreStack);
		//upgrades.setInteger(type, level);
		
		return coreStack;
	}
	
	/*
	 * Returns a Force Rod with the specified upgrade.
	 */
	public static ItemStack getRod(String type, int level)
	{
		ItemStack rodStack = new ItemStack(rodItem, 1, 0);
		rodStack.setTagCompound(new NBTTagCompound());
		rodStack.getTagCompound().setTag("upgrades", new NBTTagCompound());
		
		//NBTTagCompound upgrades = UpgradeHelper.getUpgradeCompound(rodStack);
		//upgrades.setInteger(type, level);
		
		return rodStack;
	}
	
	/*
	 * Returns a clipboard filled with the given items.
	 */
	public static ItemStack getClipboard(ItemStack[] items)
	{
		ItemStack clipboard = new ItemStack(clipItem, 1, 0);
		ItemInventory inv = new ItemInventory(9, clipboard);
		
		if (items != null && items.length > 0)
			for (int i = 0; i < (items.length < 9 ? items.length : 9); i ++)
				inv.setInventorySlotContents(i, items[i]);
		
		inv.save();
		
		clipboard.getTagCompound().removeTag("ID");
		
		return clipboard;
	}
	
	/*
	 * Returns an item card filled with the given items.
	 */
	public static ItemStack getCard(ItemStack[] items)
	{
		ItemStack cardStack = new ItemStack(cardItem, 1, 0);
		initializeInv(cardStack, 0, 16);
		
		ItemInventory inv = new ItemInventory(cardSize, cardStack);
		
		if (items != null && items.length > 0)
			for (int i = 0; i < items.length; i ++)
			{
				inv.setInventorySlotContents(i, items[i]);
				
				if (i > cardSize - 1)
					break;
			}
		
		inv.save();
		cardStack.getTagCompound().removeTag("ID");
		
		return cardStack;
	}
	
	/*
	 * Sets the mode data for an ItemStack inside an Item Card.
	 * Returns the ItemStack for convenience.
	 */
	public static ItemStack setCardData(ItemStack stack, int num)
	{
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		
		stack.getTagCompound().setByte("mode", (byte)num);
		
		return stack;
	}
	
	/*
	 * Attempt to change a Force Pack's contents into various forge equivalents,
	 * as directed by a Forge Card.
	 */
	public static boolean forgify(EntityPlayer player, ItemInventory packInv, ItemInventory cardInv)
	{
		if (packInv == null || player == null || cardInv == null)
			return false;
		
		boolean changed = false;
		
		for (int i = 0; i < cardInv.getSizeInventory(); i ++)
		{
			ItemStack cardStack = cardInv.getStackInSlot(i);
			if (cardStack == null)
				continue;
			
			int id = OreDictionary.getOreID(cardStack);
			if (id < 0)
				continue;
			
			ArrayList<ItemStack> ores = OreDictionary.getOres(id);
			if (ores == null || ores.size() < 2)
				continue;
			
			for (int j = 0; j < packInv.getSizeInventory(); j ++)
			{
				ItemStack invStack = packInv.getStackInSlot(j);
				if (invStack == null || OreDictionary.itemMatches(cardStack, invStack, false))
					continue;
				
				for (ItemStack oreCheck : ores)
				{
					if (OreDictionary.itemMatches(oreCheck, invStack, false))
					{
						ItemStack bufferStack = invStack.copy();
						packInv.setInventorySlotContents(j, new ItemStack(cardStack.getItem(),
							invStack.stackSize, cardStack.getItemDamage()));
						if (bufferStack.hasTagCompound())
							packInv.getStackInSlot(j).setTagCompound((NBTTagCompound)
								bufferStack.getTagCompound().copy());
						changed = true;
						break;
					}
				}
			}
		}
		
		if (changed)
			packInv.save();
		
		return changed;
	}
	
	//Returns whether or not two ItemStacks are equivalent.  (Not including stackSize.)
	public static boolean areStacksSame(ItemStack stack1, ItemStack stack2)
	{
		if (stack1 == null || stack2 == null)
			return false;
		
		return stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage()
			&& Objects.equal(stack1.getTagCompound(), stack2.getTagCompound());
	}
	
	//Variables used to allow this to function in the API alone.
	public static Item packItem, tileBoxItem, coreItem, rodItem, clipItem, cardItem, bagItem;
	public static int maxPackSize, cardSize, bagSize;
	
	public static Class utilsClass;
	public static Method getContainerItem;
}