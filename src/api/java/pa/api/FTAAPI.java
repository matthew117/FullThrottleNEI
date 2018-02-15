/*
 * FTAAPI.java
 * by bluedart
 * 
 * Version 0.1.0
 * 
 * This is the main part of FullThrottle Alchemist's API and contains all of the methods you
 * will want to call to add content to FTA.  Please use your own mod for this and do not
 * attempt to hook into FTA's internal plugin system as this is not, and will not, be supported.
 * 
 * Make sure you require your mod to load after "Project_Alchemy", which is the ModID of FTA
 * and make your calls to these methods in your postInit() method.  Happy modding!
 */

package pa.api;

import java.lang.reflect.Method;
import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import pa.api.alchemy.Element;
import pa.api.alchemy.IElementProvider;
import pa.api.recipe.IFreezeRecipe;
import pa.api.recipe.IGrindRecipe;
import pa.api.recipe.Recipe;

public class FTAAPI
{
	//Call this to add your custom IElementProvider to the list.
	public static void addElementProvider(IElementProvider provider)
	{
		if (provider == null)
			return;
		
		try
		{
			Class dataClass = Class.forName("pa.alchemy.AlchemyData");
			ArrayList<IElementProvider> providers = (ArrayList<IElementProvider>)dataClass
				.getField("providers").get(null);
			
			if (!providers.contains(provider))
				providers.add(provider);
		} catch (Exception e) {}
	}
	
	//Adds the specified recipe to the Atelier.
	public static void addRecipe(Recipe recipe)
	{
		if (recipe == null)
			return;
		
		try
		{
			Class bookClass = Class.forName("pa.alchemy.RecipeBook");
			Method getRecipe = bookClass.getMethod("getRecipe", new Class[] { String.class });
			ArrayList<Recipe> recipes = (ArrayList<Recipe>)bookClass.getField("list").get(null);
			
			if (getRecipe.invoke(null, new Object[] { recipe.getName() }) == null)
			{
				recipes.add(recipe);
			}
		} catch (Exception e) {}
	}
	
	//Adds a Grinding recipe.  Will work with both the upgrade on a tool and
	//an upgraded Alchemical furnace.  GrindStack objects are useful here.
	public static void addGrind(IGrindRecipe grind)
	{
		try
		{
			Class grindClass = Class.forName("pa.main.plugin.PluginGrinding");
			Method addGrind = grindClass.getMethod("addForceGrindable", new Class[] { IGrindRecipe.class });
			
			addGrind.invoke(null, new Object[] { grind });
		} catch (Exception e) {}
	}
	
	//Adds a Freezing recipe.  Will work with both the upgrade on a tool and
	//an upgraded Alchemical furnace.  FreezeStack objects are useful here.
	public static void addFreeze(IFreezeRecipe freeze)
	{
		try
		{
			Class freezeClass = Class.forName("pa.main.plugin.PluginFreezing");
			Method addFreeze = freezeClass.getMethod("addFreezable", new Class[] { IFreezeRecipe.class });
			
			addFreeze.invoke(null, new Object[] { freeze });
		} catch (Exception e) {}
	}
	
	//Returns the specified Element object as defined by the Periodic Table.
	//Ex. "Carbon", "Oxygen".
	public static Element getElement(String name)
	{
		try
		{
			Class tableClass = Class.forName("pa.alchemy.PeriodicTable");
			Method getElement = tableClass.getMethod("getElement", new Class[] { String.class });
			
			return (Element)getElement.invoke(null, new Object[] { name });
		} catch (Exception e) {}
		
		return null;
	}
	
	//If you have an item that implements the IRenamable interface, call this
	//method on the Client to open the renaming GUI.  when the save button is
	//pressed in this GUI your item's rename() method will be called on both
	//Client and Server.  Simple.
	public static void callRenameGUI(EntityPlayer player)
	{
		try
		{
			Object modObject = Class.forName("pa.main.ProjectAlchemy").getField("INSTANCE").get(null);
			int guiID = Class.forName("pa.data.Constants").getField("GUI_RENAME").getInt(null);
			
			player.openGui(modObject, guiID, player.worldObj, (int)player.posX,
				(int)player.posY, (int)player.posZ);
		} catch (Exception e) {}
	}
	
	//Call this to initialize an ItemStack whose Item implements the IElementConsumer
	//interface.  This should be done as soon as the ItemStack is available to
	//a player.  A good candidate for this is onUpdate().
	public static void initConsumer(ItemStack stack)
	{
		try
		{
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			
			NBTTagCompound comp = stack.getTagCompound();
			comp.setInteger("size", 1);
			
			NBTTagList contents = new NBTTagList();
			
			for (int i = 0; i < 1; i ++)
			{
				NBTTagCompound itemComp = new NBTTagCompound();
				itemComp.setByte("Slot", (byte) i);
				contents.appendTag(itemComp);
			}
			
			comp.setTag("contents", contents);
		} catch (Exception e) {}
	}
}