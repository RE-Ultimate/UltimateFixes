package com.rewindmc.upsilonfixes.portalgun;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonFixesConfig;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("portalgun.client.thread.ThreadDownloadResources")
@ConfigOptions({"removePortalGunResourceDownload", "fixPortalGunResources"})
public class ThreadDownloadResourcesTransformer extends UpsilonMiniTransformer {

	@Patch.Method("run()V")
	public void patchRun(PatchContext ctx) {
		if (UpsilonFixesConfig.removePortalGunResourceDownload) {
			ctx.jumpToStart();
			ctx.add(
				ALOAD(0),
				INVOKEVIRTUAL("portalgun/client/thread/ThreadDownloadResources", "reloadResources", "()V"),
				RETURN()
			);
		} else {
			// The URL used here long since doesn't work anymore, and the working assets list used by
			// newer versions of PortalGun is in a format that confuses 1.4 PortalGun, so we redirect it
			// to an alternate resource index I made
			ctx.search(LDC("http://repo.creeperhost.net/static/ichun/portalgun.xml")).jumpAfter();
			ctx.add(
				POP(), // just yeet the old URL off the stack instead of erasing the insn
				LDC("https://re-ultimate.github.io/Re-Ultimate/portalgun.xml")
			);
		}
	}
	
}
