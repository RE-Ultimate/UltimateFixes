package com.rewindmc.upsilonfixes.redesign;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonFixesConfig;
import com.rewindmc.upsilonfixes.UpsilonFixesPremain;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ScreenIngameMenu;
import net.minecraft.client.gui.ScreenMainMenu;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;
import paulscode.sound.SoundSystem;

@Patch.Class("net.minecraft.client.Minecraft")
@ConfigOptions("redesignMenus")
public class MinecraftTransformer extends UpsilonMiniTransformer {
	
	@Patch.Method("displayScreen(Lnet/minecraft/client/gui/Screen;)V")
	public void patchDisplayScreen(PatchContext ctx) {
		if (UpsilonFixesConfig.redesignMenus) {
			ctx.jumpToStart();
			ctx.add(
				ALOAD(1),
				INVOKESTATIC(hooks(), "replaceScreen", "(Lnet/minecraft/client/gui/Screen;)Lnet/minecraft/client/gui/Screen;"),
				ASTORE(1)
			);
		}
	}
	
	@Patch.Method("runTick()V")
	public void patchRunTick(PatchContext ctx) {
		if (UpsilonFixesConfig.redesignMenus) {
			ctx.jumpToStart();
			ctx.add(
				INVOKESTATIC(hooks(), "tick", "()V")
			);
		}
	}
	
	public static class Hooks {
		
		private static boolean wasPlayingMusic = false;
		
		public static Screen replaceScreen(Screen orig) {
			if ((orig instanceof ScreenMainMenu || (orig == null && Minecraft.instance().world == null)) && !(orig instanceof ScreenMainMenuUpsilon)) {
				return new ScreenMainMenuUpsilon();
			}
			if (orig instanceof ScreenIngameMenu && !(orig instanceof ScreenIngameMenuUpsilon)) {
				return new ScreenIngameMenuUpsilon();
			}
			return orig;
		}
		
		public static void tick() {
			SoundSystem sys = SoundManager.sndSystem;
			if (sys != null && sys.playing("BgMusic")) {
				if (Minecraft.instance.world != null) {
					if (wasPlayingMusic) {
						UpsilonFixesPremain.log.debug("Stopping music for game entry");
						sys.stop("BgMusic");
						wasPlayingMusic = false;
					}
				} else {
					wasPlayingMusic = true;
				}
			} else {
				wasPlayingMusic = false;
			}
		}
		
	}

}
