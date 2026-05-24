package com.rewindmc.upsilonfixes.forge;

import java.awt.Dimension;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonFixesPremain;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("cpw.mods.fml.client.TextureFXManager")
@ConfigOptions("forgeTextureFXWorkaround")
public class TextureFXManagerTransformer extends UpsilonMiniTransformer {

	@Patch.Method("getTextureDimensions(Lnet/minecraft/client/renderer/texturefx/TextureFX;)Ljava/awt/Dimension;")
	public void patchGetTextureDimensions(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(1),
			INVOKESTATIC(hooks(), "checkNull", "(Ljava/awt/Dimension;Ljava/lang/Object;)Ljava/awt/Dimension;")
		);
	}

	@Patch.Method("getTextureDimensions(I)Ljava/awt/Dimension;")
	public void patchGetTextureDimensionsRaw(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.add(
			ILOAD(1),
			INVOKESTATIC(hooks(), "checkNull", "(Ljava/awt/Dimension;I)Ljava/awt/Dimension;")
		);
	}
	
	public static class Hooks {
		
		public static Dimension checkNull(Dimension dim, int ctx) {
			return checkNull(dim, (Object)ctx);
		}
		
		public static Dimension checkNull(Dimension dim, Object ctx) {
			if (dim == null) {
				UpsilonFixesPremain.log.warn("Dimensions were requested for an improperly registered TextureFX: "+ctx, new Throwable("Stack trace").fillInStackTrace());
				return new Dimension(0, 0);
			}
			return dim;
		}
		
	}
	
}
