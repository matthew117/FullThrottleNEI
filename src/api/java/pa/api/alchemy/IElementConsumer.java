/*
 * IElementConsumer.java
 * by bluedart
 * 
 * Implementing this on your Item will allow FTA's Consumer hotkey to open the
 * Consumer GUI.  Of course, you will need to call FTAAPI.initConsumer(ItemStack)
 * before this or it will do nothing.
 * 
 * Keep in mind that you are directly responsible for actually using the
 * stored elements on the item and this is only a base framework to save you
 * time and allow it to feel like an extension of FTA.
 */
package pa.api.alchemy;

import net.minecraft.item.ItemStack;

public interface IElementConsumer
{
	//This method determines the tooltip text in the Consumer GUI.
	//It can also be used in your own code for usage calculations.
	public float getEfficiency(ItemStack stack, Element element);
	
	//This is something you will have to call for yourself when you want
	//the consumer to actually consume elements.
	//Generally speaking passing false as the third parameter means you should
	//not actually change the tag data and is simply for calculation purposes.
	//You should return true if there was sufficient 'charge' and false if
	//there is not.
	public boolean use(ItemStack stack, float amount, boolean use);
}