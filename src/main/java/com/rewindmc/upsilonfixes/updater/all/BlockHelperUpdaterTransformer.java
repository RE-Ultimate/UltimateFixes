package com.rewindmc.upsilonfixes.updater.all;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.updater.ThreadDisableTransformer;

import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("de.thexxturboxx.blockhelper.BlockHelperUpdater")
@ConfigOptions("removeAllVersionCheckers")
public class BlockHelperUpdaterTransformer extends ThreadDisableTransformer {
	
}
