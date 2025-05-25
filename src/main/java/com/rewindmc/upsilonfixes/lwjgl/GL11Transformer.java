package com.rewindmc.upsilonfixes.lwjgl;

import org.lwjgl.opengl.GL11;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import nilloader.api.lib.asm.tree.LabelNode;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("org.lwjgl.opengl.GL11")
@ConfigOptions("optimizeStateAccess")
public class GL11Transformer extends UpsilonMiniTransformer {

	@Patch.Method("glBindTexture(II)V")
	public void patchBindTexture(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ILOAD(0),
			ILOAD(1),
			INVOKESTATIC(hooks(), "bindTexture", "(II)V")
		);
	}
	
	@Patch.Method("glGetInteger(I)I")
	@Patch.Method.AffectsControlFlow
	public void patchGetInteger(PatchContext ctx) {
		ctx.jumpToStart();
		LabelNode LafterShortcut = new LabelNode();
		ctx.add(
			ILOAD(0),
			LDC(GL11.GL_TEXTURE_BINDING_2D),
			IF_ICMPNE(LafterShortcut),
			INVOKESTATIC(hooks(), "getTextureBinding2D", "()I"),
			IRETURN(),
			LafterShortcut
		);
		
	}
	
	public static class Hooks {
		
		public static void bindTexture(int target, int name) {
			if (target == GL11.GL_TEXTURE_2D) {
				StateTracker.bindTexture(name);
			}
		}
		
		public static int getTextureBinding2D() {
			return StateTracker.getBoundTexture();
		}
		
	}
	
}
