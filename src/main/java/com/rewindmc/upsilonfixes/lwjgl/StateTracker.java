package com.rewindmc.upsilonfixes.lwjgl;

import static org.lwjgl.opengl.GL13.*;

public class StateTracker {

	private static final int[] boundTextures = new int[GL_TEXTURE31-GL_TEXTURE0];
	private static int activeTexture = 0;
	
	public static void bindTexture(int tex) {
		boundTextures[activeTexture] = tex;
	}
	
	public static void activeTexture(int idx) {
		activeTexture = idx-GL_TEXTURE0;
	}
	
	public static int getBoundTexture() {
		return boundTextures[activeTexture];
	}
	
}
