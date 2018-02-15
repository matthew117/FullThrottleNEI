/*
 * IOwnedTile.java
 * by bluedart
 * 
 * This is a simple interface used by FTA TileEntities that are ownable.
 * Mostly implementing this means that people are not allowed to Alchemy Wrench
 * up something someone else owns.  You will have to control access rules
 * yourself.
 */

package pa.api;

public interface IOwnedTile
{
	public String getOwner();
	public int getAccessLevel();
}