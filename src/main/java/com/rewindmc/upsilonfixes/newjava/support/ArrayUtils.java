package com.rewindmc.upsilonfixes.newjava.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayUtils {

	public static <T> T[] remove(T[] arr, int idx) {
		T[] nw = Arrays.copyOf(arr, arr.length-1);
		if (nw.length == 0) return nw;
		System.arraycopy(arr, idx+1, nw, idx, arr.length-idx-1);
		return nw;
	}

	public static <T> T[] add(T[] arr, int idx, T val) {
		T[] nw = Arrays.copyOf(arr, arr.length+1);
		System.arraycopy(arr, idx, nw, idx+1, arr.length-idx);
		nw[idx] = val;
		return nw;
	}

	public static <T> T[] add(T[] arr, T val) {
		T[] nw = Arrays.copyOf(arr, arr.length+1);
		nw[arr.length] = val;
		return nw;
	}
	
	public static void main(String[] args) {
		List<Integer> li = new ArrayList<Integer>();
		li.add(8);
		Integer[] a = (Integer[])li.toArray();
		System.out.println(a);
	}
	
}
