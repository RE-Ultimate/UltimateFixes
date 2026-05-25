package com.rewindmc.upsilonfixes;

import java.util.ArrayList;
import java.util.Collection;

public class NonNullList<E> extends ArrayList<E> {

	@Override
	public boolean add(E e) {
		if (e == null) throw new IllegalArgumentException("Cannot add null to this list!");
		return super.add(e);
	}
	
	@Override
	public void add(int index, E e) {
		if (e == null) throw new IllegalArgumentException("Cannot add null to this list!");
		super.add(index, e);
	}
	
	@Override
	public E set(int index, E e) {
		if (e == null) throw new IllegalArgumentException("Cannot add null to this list!");
		return super.set(index, e);
	}
	
	// impls from AbstractList so they go through add
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean modified = false;
		for (E e : c)
			if (add(e))
				modified = true;
		return modified;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		rangeCheckForAdd(index);
		boolean modified = false;
		for (E e : c) {
			add(index++, e);
			modified = true;
		}
		return modified;
	}

	private void rangeCheckForAdd(int index) {
		if (index < 0 || index > size())
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	private String outOfBoundsMsg(int index) {
		return "Index: " + index + ", Size: " + size();
	}
	
}
