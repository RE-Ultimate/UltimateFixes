package com.rewindmc.upsilonfixes.vanilla;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("net.minecraft.util.ThreadDownloadResources")
@ConfigOptions("removeVanillaResourceDownload")
public class ThreadDownloadResourcesTransformer extends UpsilonMiniTransformer {

	@Patch.Method("run()V")
	public void patchRun(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(0),
			INVOKEVIRTUAL("net/minecraft/util/ThreadDownloadResources", "reloadResources", "()V"),
			RETURN()
		);
	}
	
}
