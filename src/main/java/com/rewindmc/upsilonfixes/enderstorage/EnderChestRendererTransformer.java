package com.rewindmc.upsilonfixes.enderstorage;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import nilloader.api.lib.asm.tree.LabelNode;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("codechicken/enderstorage/storage/item/EnderChestRenderer")
@ConfigOptions("fixEnderStorageUnconditionalRender")
public class EnderChestRendererTransformer extends UpsilonMiniTransformer {

	@Patch.Method("renderChest(IIZDDDIF)V")
	@Patch.Method.AffectsControlFlow
	public void patchRenderChest(PatchContext ctx) {
		ctx.search(
			GETSTATIC("codechicken/enderstorage/storage/item/EnderChestRenderer", "renderEndPortal", "Lcodechicken/core/render/RenderCustomEndPortal;")
		).jumpBefore();
		
		LabelNode LafterRender = new LabelNode();
		
		ctx.add(
			FLOAD(10),
			INVOKESTATIC(hooks(), "shouldRenderEndPortal", "(F)Z"),
			IFZ(LafterRender)
		);
		
		ctx.search(
			INVOKEVIRTUAL("codechicken/core/render/RenderCustomEndPortal", "render", "(DDDFDDDLbba;)V")
		).jumpAfter();
		
		ctx.add(LafterRender);
	}
	
	public static class Hooks {
		
		public static boolean shouldRenderEndPortal(float lidAngle) {
			return lidAngle < -0.001;
		}
		
	}
	
}
