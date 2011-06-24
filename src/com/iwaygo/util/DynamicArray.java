
package com.iwaygo.util;

import android.os.Bundle;
import android.util.Log;

public class DynamicArray {
	private Object[] data;
	
	private int count = 0;
	
	public DynamicArray() {
		data = new Object[0];
	}
	
	public Object get(int position) {
		if (position >= data.length)
			return 0;
		else
			return data[position];
	}
	
	public int getLength() {
		return data.length;
	}
	
	public String[] getStrings() {
		if (data.length > 0) {
			String[] strings = new String[data.length];
			for (int i = 0; i < data.length; i++) {
				//Log.v("iWayGo", "toStringCount" + i);
				strings[i] = data[i].toString();
			}
			return strings;
		} else
			return null;
	}
	public Bundle[] getBundles() {
		if (data.length > 0) {
			Bundle[] bundles = new Bundle[data.length];
			for (int i = 0; i < data.length; i++) {
				bundles[i] = (Bundle) data[i];
			}
			return bundles;
		} else
			return null;
	}
	public String[] getStringsDesc() {
		if (data.length > 0) {
			Log.v("iWayGo", "getStringCount:::" + data.length);
			String[] strings = new String[data.length];
			for (int i = 0; i < data.length; i++) {
				//Log.v("iWayGo", "toStringCount" + i);
				strings[data.length - i - 1] = data[i].toString();
			}
			return strings;
		} else
			return null;
	}
	
	public int[] getInt() {
		if (data.length > 0) {
			int[] ints = new int[data.length];
			for (int i = 0; i < data.length; i++) {
				ints[i] = (Integer) data[i];
			}
			return ints;
		} else
			return null;
	}
	
	public void put(int position, Object value) {
		if (position >= data.length) {
			int newSize = 1 + data.length;
			if (position >= newSize)
				newSize = 1 + position;
			Object[] newData = new Object[newSize];
			System.arraycopy(data, 0, newData, 0, data.length);
			data = newData;
			//Log.v("x", "Size of dynamic array increased to " + newSize);
			count++;
			//Log.v("x", "Count of dynamic array increased to " + count);
			//Log.v("x", "position:" + position);
		} else {
			Log.v("x", "position worng!!!!!->>>>" + position);
		}
		
		data[position] = value;
	}
	
}
