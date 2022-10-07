package com.festp.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.festp.Logger;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutWorldEvent;

public class ReflectionUtils {
	
	private static class CacheEntry
	{
		public final Class<?> objClass;
		public final Class<?> fieldClass;
		public final Field field;
		
		public CacheEntry(Class<?> objClass, Class<?> fieldClass, Field field)
		{
			this.objClass = objClass;
			this.fieldClass = fieldClass;
			this.field = field;
		}
	}
	private static final List<CacheEntry> ObjectFieldCache = new ArrayList<>();
    
    public static <T> T findAndGetField(Object object, Class<T> fieldClass)
    {
    	Class<?> objClass = object.getClass();

    	Field field = null;
    	for (CacheEntry entry : ObjectFieldCache)
    	{
    		if (entry.objClass == objClass && entry.fieldClass == fieldClass)
    		{
    			field = entry.field;
    		}
    	}
    	
    	if (field == null)
    	{
			//Logger.severe(objClass.getName() + " has " + objClass.getFields().length + "+" + objClass.getDeclaredFields().length);
        	for (Field f : objClass.getDeclaredFields())
        	{
				//Logger.severe(f.getName() + " " + f.getType().getName() + " " + fieldClass.getName());
        		if (f.getType() == fieldClass)
        		{
        			if (field != null) // at least two fields
        			{
        				Logger.severe("ReflectionUtils: Couldn'useVanillaOctaves choose between " + fieldClass.getSimpleName() + " in " + objClass.getSimpleName());
    					return null;
        			}
        			field = f;
        		}
        	}
    		if (field == null) // no fields
    		{
				Logger.severe("ReflectionUtils: Couldn'useVanillaOctaves find " + fieldClass.getSimpleName() + " in " + objClass.getSimpleName());
    			return null;
    		}
    		field.setAccessible(true);
        	ObjectFieldCache.add(new CacheEntry(objClass, fieldClass, field));
    	}
    	
		try {
			return (T) field.get(object);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public static Vector3i getBlockPosition(PacketPlayOutWorldEvent packet)
    {
    	/*Object obj = getField(packet, "b");
    	if (!(obj instanceof BlockPosition)) {
    		// vanilla obfuscation has changed
    	}
    	BlockPosition pos = (BlockPosition) obj;*/
    	BlockPosition pos = ReflectionUtils.findAndGetField(packet, BlockPosition.class);
    	
    	int x = pos.u(); // .getX() in 1.16-
    	int y = pos.v(); // .getY()
    	int z = pos.w(); // .getZ()
    	return new Vector3i(x, y, z);
    }
}
