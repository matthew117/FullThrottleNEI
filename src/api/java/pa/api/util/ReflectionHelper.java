package pa.api.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import cpw.mods.fml.common.FMLLog;
/*
 * ReflectionHelper.java
 * by bluedart
 * 
 * This is just a small utility I whipped up to help clean up the API's code.  This also serves
 * to prevent unnecessary API deprecation in that each object retrieved can fail without preventing
 * the rest of them from loading.
 */
public class ReflectionHelper
{
	public static boolean debug = false;
	
	//Attempts to find the given class.
	public static Class getClass(String name)
	{
		try
		{
			return Class.forName(name);
		} catch (Exception e) {}
		
		if (debug)
			FMLLog.getLogger().info("ReflectionHelper unable to retrieve class: " + name);
		
		return null;
	}
	
	//Attempts to find and return the specified method from the given class.
	public static Method getMethod(Class parent, String name, Class[] params)
	{
		try
		{
			return parent.getDeclaredMethod(name, params);
		} catch (Exception e) {}
		
		if (debug)
			FMLLog.getLogger().info("ReflectionHelper unable to retrieve method: " + name);
		
		return null;
	}
	
	//Attempts to find and return the specified Field from the given class.
	public static Field getField(Class parent, String name)
	{
		try
		{
			return parent.getDeclaredField(name);
		} catch (Exception e) {}
		
		if (debug)
			FMLLog.getLogger().info("ReflectionHelper unable to retrieve field: " + name);
		
		return null;
	}
	
	public static boolean areMethodsEqual(Method input, Method check)
	{
		try
		{
			if (input == null || check == null)
				return false;
			
			if (!input.getName().equals(check.getName()))
				return false;
			
			if (!input.getGenericReturnType().getClass().equals(check.getGenericReturnType().getClass()))
				return false;
			
			Type[] inputs = input.getGenericParameterTypes();
			Type[] checks = check.getGenericParameterTypes();
			
			if (inputs.length == 0 && checks.length == 0)
				return true;
			
			if (inputs.length == checks.length)
			{
				for (int i = 0; i < inputs.length; i ++)
					if (!inputs[i].getClass().equals(checks[i].getClass()))
						return false;
			}
			
			return true;
		} catch (Exception e) {}
		
		return false;
	}
}