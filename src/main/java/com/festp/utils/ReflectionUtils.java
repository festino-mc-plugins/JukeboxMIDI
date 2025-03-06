package com.festp.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.festp.Logger;

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
    	Field field = findField(object.getClass(), fieldClass);
    	if (field == null) {
    		Logger.severe("ReflectionUtils: Couldn't find " + fieldClass.getSimpleName() + " in " + object.getClass().getSimpleName());
			return null;
    	}
    	
		try {
			return (T) field.get(object);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    public static Field findField(Class<?> objClass, Class<?> fieldClass)
    {
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
    		if (objClass.getSuperclass() != null) {
    			field = findField(objClass.getSuperclass(), fieldClass);
    		}
        	for (Field f : objClass.getDeclaredFields())
        	{
        		if (f.getType() == fieldClass)
        		{
        			if (field != null) // at least two fields
        			{
        				Logger.severe("ReflectionUtils: Couldn't choose between " + fieldClass.getSimpleName() + " in " + objClass.getSimpleName());
    					return null;
        			}
        			field = f;
        		}
        	}
    		if (field == null) // no fields
    		{
    			return null;
    		}
    		field.setAccessible(true);
        	ObjectFieldCache.add(new CacheEntry(objClass, fieldClass, field));
    	}
    	
    	return field;
    }

	public static void printAllFields(Class<?> clazz) {
		Logger.severe(clazz.getName() + " has " + clazz.getDeclaredFields().length + " fields:");
    	for (Field f : clazz.getDeclaredFields())
    	{
    		Logger.severe("  " + f.getType().getName() + " " + f.getName());
    	}
    	if (clazz.getSuperclass() != null) {
    		printAllFields(clazz.getSuperclass());
        }
	}
    
    public static Class<?> getPacketPlayOutWorldEventClass() {
    	try {
    		return Class.forName("net.minecraft.network.protocol.game.PacketPlayOutWorldEvent");
    	}
    	catch (Exception e) {
			e.printStackTrace();
			return null;
    	}
    }
    
    public static Vector3i getBlockPosition(Object packetPlayOutWorldEvent)
    {
		try {
			Class<?> packetPlayOutWorldEventClass = getPacketPlayOutWorldEventClass();
			Class<?> blockPositionClass = Class.forName("net.minecraft.core.BlockPosition");
			// TODO getX, getY, getZ before 1.17
			Method getXMethod = blockPositionClass.getMethod("u");
			Method getYMethod = blockPositionClass.getMethod("v");
			Method getZMethod = blockPositionClass.getMethod("w");
			
	    	Object blockPosition = ReflectionUtils.findAndGetField(packetPlayOutWorldEvent, blockPositionClass);

			int x = (Integer)getXMethod.invoke(blockPosition);
			int y = (Integer)getYMethod.invoke(blockPosition);
			int z = (Integer)getZMethod.invoke(blockPosition);
	    	return new Vector3i(x, y, z);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
    }
}
