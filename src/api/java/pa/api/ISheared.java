/*
 * ISheared.java
 * by bluedart
 * 
 * This is an interface used for Entitys such as Cold Cows and Cold Pigs.
 * If you implement this on your EntityLiving FTA will check their shouldRevert()
 * method upon living tick and replace them with their 'replacement' entity
 * given by the getReplacement() method should that method return true.
 * 
 * As for actual shearing of new entities, you will have to handle that yourself
 * if you wish to implement it.
 */

package pa.api;

import net.minecraft.entity.EntityLivingBase;

public interface ISheared
{
	public boolean shouldRevert();
	public EntityLivingBase getReplacement();
	
	//This is called when the entity is spawned, generally as a means of
	//'freaking out' the sheared creature.  Due to an odd glitch, Cold Cows and
	//Pigs do not actually flee when this method is called.  I am unsure as to why.
	public void setFleeing();
}