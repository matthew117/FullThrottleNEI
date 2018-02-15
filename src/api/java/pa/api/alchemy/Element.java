/*
 * Element.java
 * by bluedart
 * 
 * The building blocks of Alchemy.  Elements are stored on the Periodic Table,
 * which you do not have direct access to, as their given name on the actual
 * Periodic Table of elements.
 * 
 * To access an Element, you will want to call FTAAPI.getElement(name).
 * All element names are camel-cased with a Class infrastructure ruleset.
 * Example: Hydrogen, Carbon, Nitrogen, Oxygen.
 */

package pa.api.alchemy;

import net.minecraft.potion.PotionEffect;

public class Element
{
	protected String name;
	protected String description;
	protected String symbol;
	protected float density, atomicWeight;
	protected int number;
	protected int level;
	protected int color;
	
	protected PotionEffect potionEffect;
	
	public Element(String name, String symbol, int number, float atomicWeight, int color, int level)//, float density, String description)
	{
		this.name = name;
		this.symbol = symbol;
		this.atomicWeight = atomicWeight;
		this.number = number;
		this.color = color;
		//this.density = density;
		//this.description = description;
	}
	
	public void addPotionEffect(PotionEffect effect)
	{
		this.potionEffect = effect;
	}
	
	public PotionEffect getPotionEffect() { return this.potionEffect; }
	
	public String getName() { return this.name; }
	public String getSymbol() { return this.symbol; }
	public float getAtomicWeight() { return this.atomicWeight; }
	public int getNumber() { return this.number; }
	public float getDensity() { return this.density; }
	public String getDescription() { return this.description; }
	public int getLevel() { return this.level; }
	public int getColor() { return this.color; }
	
	public void addDescription(String description) { this.description = description; }
}