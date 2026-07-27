package com.rewindmc.upsilonfixes;

import com.rewindmc.upsilonfixes.dartcraft.DartCraftCoreTransformer;

public class UpsilonFixesForgeLoadComplete implements Runnable {

	@Override
	public void run() {
		UpsilonFixesForgePostInit.callInRelauncher("com.rewindmc.upsilonfixes.UpsilonFixesForgeLoadComplete$InRelauncher");
	}
	
	public static class InRelauncher {
		
		public static void run() {
			if (UpsilonFixesConfig.additionalDartCraftForceEngineThrottles) {
				// need to do this late because MFR registers its fluids in postinit.
				DartCraftCoreTransformer.Hooks.addAdditionalThrottles();
			}
		}
		
	}
	
}
