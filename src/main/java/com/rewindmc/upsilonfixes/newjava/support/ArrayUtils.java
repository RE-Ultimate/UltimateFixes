package com.rewindmc.upsilonfixes.newjava.support;

import java.util.Arrays;

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
	
}
