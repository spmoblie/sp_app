package com.spshop.stylistpark.collageviews;

import java.util.Stack;

public class SizedStack<T> extends Stack<T> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4156618715685798430L;
	
	private int maxSize;

    public SizedStack(int size) {
        super();
        this.maxSize = size;
    }

	@Override
    public T push(T object) {
        //If the stack is too big, remove elements until it's the right size.
        while (this.size() + 1 > maxSize) {
            this.remove(0);
        }
        return super.push(object);
    }
}
