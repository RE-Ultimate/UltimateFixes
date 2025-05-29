package com.rewindmc.upsilonfixes.updater.all;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("buildcraft.core.Version")
@ConfigOptions("removeAllVersionCheckers")
public class BuildCraftVersionTransformer extends UpsilonMiniTransformer {

	@Patch.Method("check()V")
	public void patchCheck(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			RETURN()
		);
	}
	
}
