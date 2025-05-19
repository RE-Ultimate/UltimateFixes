package com.rewindmc.upsilonfixes.newjava;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("codechicken.core.asm.FeatureHackTransformer")
@ConfigOptions("modernJavaSupport")
public class FeatureHackTransformerTransformer extends UpsilonMiniTransformer {

	@Patch.Method("usp(Ljava/lang/String;)V")
	public void patchTransformer003(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			RETURN()
		);
	}
	
}
