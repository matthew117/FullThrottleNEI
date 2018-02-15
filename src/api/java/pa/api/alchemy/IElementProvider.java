/*
 * IElementProvider.java
 * by bluedart
 * 
 * Pass an implementation of this interface into FTAAPI.addElementProvider() in
 * order to add it to the list of Element Providers.
 * 
 * All Providers added in this way are unable to change or remove elements on
 * items that FTA adds itself.  Those are, and shall remain, immutable.
 */

package pa.api.alchemy;

import java.util.HashMap;

import net.minecraft.item.ItemStack;

public interface IElementProvider
{
	//Returning a non-empty HashMap here will indicate to FTA that the given
	//ItemStack has elements on it.
	//The String entries are the names of Elements, and the floats are the
	//amount of the corresponding Element.  Keep in mind that FTA only displays
	//the 8 largest Elements on any given ItemStack and only those 8 are used
	//in relevant calculations.  The rest will simply be hidden and irrelevant.
	public HashMap<String, Float> getElements(ItemStack stack);
}