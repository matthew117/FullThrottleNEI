/*
 * IRenamable.java
 * by bluedart
 * 
 * Implementing this interface on an Item will allow you to tie-in to the FTA
 * renaming GUI and allow for recolroing and renaming of your items just like
 * Alchemy Packs and Clipboards.
 * 
 * After successfully implementing this interface on your item, simply call
 * FTAAPI.callRenameGUI(EntityPlayer) on the Client while the player is holding
 * your item and the rename GUI should show up.  A good place to do this is inside
 * your item's right-click function, possible while the player is sneaking.
 */

package pa.api;

import net.minecraft.item.ItemStack;

public interface IRenamable
{
	//This method is called when the item is successfully renamed in the GUI.
	//It is called on both the Client and Server.  Do whatever you want to
	//the tag data and meta of the item to save your changes.
	public void rename(ItemStack item, String name, int color);
	
	//FTA's rename GUI calls this method to determine the maxiumum value for
	//'color.'  This allows for various color or type variations within your
	//item and for recoloring to occur.  The GUI always starts at 0 and goes to
	//whatever value you return here.  The GUI will render an ItemStack in the
	//color slot that changes the damage value of the ItemStack, so if you're
	//going to use this then make sure they render accordingly.
	//A value of 0 will prevent the recoloring feature from being perceived.
	public int maxColor();
}