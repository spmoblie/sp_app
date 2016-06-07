package com.spshop.stylistpark.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.util.Log;

@SuppressWarnings("serial")
public class LimitedHashMap<K, V> extends LinkedHashMap<K, V>
{
    private final int maxSize;

    public LimitedHashMap(int maxSize) {
        this.maxSize = maxSize;
    }
    
    @Override
    public V get(Object key)
    {
        return super.get(key);
    }

    @Override
    public V put(K key, V value)
    {
        V bitmap = super.put(key, value);
        Log.d("raydebug", "put size " + size());
        Log.d("raydebug", "put maxSize " + maxSize);
        if (size() > maxSize)
        {
            V v = entrySet().iterator().next().getValue();
            if(v instanceof Bitmap)
            {
                Log.d("raydebug", "removeEldestEntry" + " recycle");
                ((Bitmap) v).recycle();
            }
        }
        return bitmap;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        super.removeEldestEntry(eldest);
        return size() > maxSize;
    }
}
