package pa.api.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/*
 * This class is used by almost all DartCraft items at some point, but most notably Force Packs.
 * This is a bridge class that allows for items to be stored inside other items by easily loading and
 * saving their "contents" from the ItemStack's NBTTagCompound through an IInventory interface.
 * 
 * To use this class properly the ItemStack in question must already have an NBTTagCompound with an
 * NBTTagList of serialized ItemStacks (use ItemStack.writeToNBT() for this) inside it and you ought to
 * pass in the appropriate size of the 'inventory' you are loading.
 * 
 * Note that this class does not save by itself, if you wish to save your changes to the ItemStack in
 * IInventory form you should call this class's save() method which will do the work for you.
 * It's worth noting that if you have multiple instances of this class instantiated for any single
 * ItemStack at the same time they will not cooperate, so caution must be taken.
 */
public class ItemInventory implements IInventory
{
	public ItemStack origin;
	protected ItemStack[] contents;
	private Container handler;
	private String contentName;
	
	public ItemInventory(int size)
	{
		this.contents = new ItemStack[size];
		contentName = "contents";
	}
	
	public ItemInventory(int size, ItemStack stack)
	{
		this(size);
		this.origin = stack;
		//this.contents = new ItemStack[size];
		
		readFromNBT(stack.getTagCompound());
	}
	
	public ItemInventory(int size, ItemStack stack, String name)
	{
		this(size);
		this.origin = stack;
		//this.contents = new ItemStack[size];
		this.contentName = name;
		
		if (contentName == null)
			contentName = "contents";
		
		readFromNBT(stack.getTagCompound());
	}
	
	public void onGuiSaved(EntityPlayer player)
	{
		this.origin = findOrigin(player);
		if (this.origin != null)
			save();
	}
	
	//Look through the specified player's inventory and return the instance of this
	//simulated inventory if present.
	public ItemStack findOrigin(EntityPlayer player)
	{
		if (this.origin == null)
			return null;
		
		NBTTagCompound comp = this.origin.getTagCompound();
		if (comp == null)
			return null;
		
		int id = comp.getInteger("ID");
		
		//Check to see if this (hopefully) unique ID is present in the player's inventory.
		for (int i = 0; i < player.inventory.getSizeInventory(); i ++)
		{
			if (player.inventory.getStackInSlot(i) != null)
			{
				NBTTagCompound playerComp = player.inventory.getStackInSlot(i).getTagCompound();
				if (playerComp != null)
				{
					if (id == playerComp.getInteger("ID"))
						return player.inventory.getStackInSlot(i);
				}
			}
		}
		
		//Also check the stack currently held by the mouse.
		if (player.inventory.getItemStack() != null)
		{
			NBTTagCompound playerComp = player.inventory.getItemStack().getTagCompound();
			if (playerComp != null && id == playerComp.getInteger("ID"))
				return player.inventory.getItemStack();
		}
		
		return null;
	}
	
	//Match the ID of the saved ItemStack to the one passed.
	public boolean matchesID(int secondID)
	{
		if (this.origin == null)
			return false;
		
		NBTTagCompound comp = this.origin.getTagCompound();
		if (comp == null)
			return false;
		
		int id = comp.getInteger("ID");
		return id == secondID;
	}
	
	//Save the ItemInventory.  This must be done when something inside is changed if you want
	//those changes to save.
	public void save()
	{
		if (this.origin == null)
			return;
		
		NBTTagCompound comp = this.origin.getTagCompound();
		if (comp == null)
			comp = new NBTTagCompound();
		writeToNBT(comp);
		this.origin.setTagCompound(comp);
	}
	
	//Read object from NBTTagCompound.
	public void readFromNBT(NBTTagCompound comp)
	{
		if (comp == null || contentName == null)
			return;
		if (comp.hasKey(contentName))
		{
			NBTTagList contentList = comp.getTagList(contentName, (byte)10);
			this.contents = new ItemStack[getSizeInventory()];
			for (int i = 0; i < contentList.tagCount(); i ++)
			{
				NBTTagCompound tempComp = (NBTTagCompound) contentList.getCompoundTagAt(i);
				byte slotByte = tempComp.getByte("Slot");
				
				if (slotByte >= 0 && slotByte < this.contents.length)
				{
					this.contents[slotByte] = ItemStack.loadItemStackFromNBT(tempComp);
				}
			}
		}
	}
	
	//Serialize object to NBTTagCompound.
	public void writeToNBT(NBTTagCompound comp)
	{
		if (contentName == null)
			contentName = "contents";
		
		NBTTagList contentList = new NBTTagList();
		for (int i = 0; i < this.contents.length; i ++)
		{
			if (this.contents[i] != null)
			{
				NBTTagCompound tempComp = new NBTTagCompound();
				tempComp.setByte("Slot", (byte) i);
				
				this.contents[i].writeToNBT(tempComp);
				contentList.appendTag(tempComp);
			}
			
			comp.setTag(contentName, contentList);
		}
	}
	
	//Allow for crafting matrixes to be supported.
	public void setCraftingListener(Container container)
	{
		this.handler = container;
	}
	
	//IInventory methods.
	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		if (this.contents[i] == null)
			return null;
		
		if (this.contents[i].stackSize <= j)
		{
			ItemStack product = this.contents[i];
			this.contents[i] = null;
			this.onInventoryChanged();
			return product;
		}
		
		ItemStack product = this.contents[i].splitStack(j);
		
		if (this.contents[i].stackSize == 0)
			this.contents[i] = null;
		
		this.onInventoryChanged();
		return product;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		if (this.contents[slot] == null)
			return null;
		ItemStack returnVal = this.contents[slot];
		this.contents[slot] = null;
		
		this.onInventoryChanged();
		return returnVal;
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		this.contents[index] = stack;
		this.onInventoryChanged();
	}
	
	//Methods ported from InventoryPlayer.
	public int getFirstEmptyStack()
	{
		for (int i = 0; i < this.contents.length; ++i)
			if (this.contents[i] == null)
				return i;
		
		return -1;
	}
	
	//Returns how many free spaces are in this inventory.
	public int getFreeSlots()
	{
		int free = 0;
		
		if (this.contents != null && this.contents.length > 0)
			for (ItemStack checkStack : this.contents)
				if (checkStack == null)
					free ++;
		
		return free;
	}
	
	//Internally used.
	private int storePartialItemStack(ItemStack stack)
	{
		//int i = stack.itemID;
		int j = stack.stackSize;
		int k;
		
		if (stack.getMaxStackSize() == 1)
		{
			k = this.getFirstEmptyStack();
			
			if (k < 0)
				return j;
			else
			{
				if (this.contents[k] == null)
					this.contents[k] = ItemStack.copyItemStack(stack);
				
				return 0;
			}
		}
		else
		{
			k = this.storeItemStack(stack);
			
			if (k < 0)
				k = this.getFirstEmptyStack();
			
			if (k < 0)
				return j;
			else
			{
				if (this.contents[k] == null)
				{
					this.contents[k] = new ItemStack(stack.getItem(), 0, stack.getItemDamage());
					
					if (stack.hasTagCompound())
						this.contents[k].setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
				}
				
				int l = j;
				
				if (j > this.contents[k].getMaxStackSize() - this.contents[k].stackSize)
					l = this.contents[k].getMaxStackSize() - this.contents[k].stackSize;
				
				if (l > this.getInventoryStackLimit() - this.contents[k].stackSize)
					l = this.getInventoryStackLimit() - this.contents[k].stackSize;
				
				if (l == 0)
					return j;
				else
				{
					j -= l;
					this.contents[k].stackSize += l;
					this.contents[k].animationsToGo = 5;
					return j;
				}
			}
		}
		
		//return stack.stackSize;
	}
	
	//Internally used.
	public int storeItemStack(ItemStack stack)
	{
		for (int i = 0; i < this.contents.length; ++i)
			if (this.contents[i] != null && this.contents[i].getItem() == stack.getItem() &&
			this.contents[i].isStackable() && this.contents[i].stackSize < this.contents[i]
			.getMaxStackSize() && this.contents[i].stackSize < this.getInventoryStackLimit() &&
			(!this.contents[i].getHasSubtypes() || this.contents[i].getItemDamage() == stack
			.getItemDamage()) && ItemStack.areItemStackTagsEqual(this.contents[i], stack))
				return i;
		
		return -1;
	}
	
	//Adds an ItemStack to this inventory.  Returns true if the item was added at least partially.
	public boolean addItemToInventory(ItemStack stack)
	{
		if (stack == null)
			return false;
		else
		{
			int i;
			if (stack.isItemDamaged())
			{
				i = this.getFirstEmptyStack();
				
				if (i >= 0)
				{
					this.contents[i] = ItemStack.copyItemStack(stack);
					this.contents[i].animationsToGo = 5;
					stack.stackSize = 0;
					this.onInventoryChanged();
					return true;
				}
				else
					return false;
			}
			else
			{
				do
				{
					i = stack.stackSize;
					stack.stackSize = this.storePartialItemStack(stack);
				}
				
				while (stack.stackSize > 0 && stack.stackSize < i);
				
				return stack.stackSize < i;
			}
		}
	}
	
	//Obligatory 'getters'
	@Override
	public ItemStack getStackInSlot(int index) { return this.contents[index]; }
	
	@Override
	public int getSizeInventory() { return this.contents.length; }
	
	@Override
	public String getInventoryName() { return "inventory.simulated"; }
	
	@Override
	public int getInventoryStackLimit() { return 64; }
	
	//@Override
	public void onInventoryChanged()
	{
		if (handler != null)
			handler.onCraftMatrixChanged(this);
	}
	
	//@Override
	//public void openChest() {}
	
	//@Override
	//public void closeChest() {}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) { return true; }
	
	/*
	@Override
	public boolean isInvNameLocalized()
	{
		return false;
	}*/
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		return false;
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}
	
	@Override
	public void markDirty() {}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}
}