package com.rewindmc.upsilonfixes.lwjgl;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("org.lwjgl.opengl.GL13")
@ConfigOptions("optimizeStateAccess")
public class GL13Transformer extends UpsilonMiniTransformer {

	@Patch.Method("glActiveTexture(I)V")
	public void patchActiveTexture(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ILOAD(0),
			INVOKESTATIC(hooks(), "activeTexture", "(I)V")
		);
	}
	
	public static class Hooks {
		public static void activeTexture(int tex) {
			StateTracker.activeTexture(tex);
		}
	}
	
}
