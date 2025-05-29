package com.rewindmc.upsilonfixes.branding;

import java.util.List;

import com.rewindmc.upsilonfixes.UpsilonFixesConfig;

import nilloader.api.lib.asm.tree.LabelNode;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import net.minecraft.client.gui.ScreenMainMenu;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("net.minecraft.client.gui.ScreenMainMenu")
public class ScreenMainMenuTransformer extends UpsilonMiniTransformer {

	private static final String[] addSplashes = {
		"Trans rights!",
		"The work of many people!",
		"Contains infinite genders!",
		
		// I'm gonna include these from the new versions just to piss off Those Kind Of People
		"Black lives matter!",
		"Be anti-racist!",
		"Learn about allyship!",
		"Speak OUT against injustice and UP for equality!",
		"Amplify and listen to BIPOC voices!",
		"Educate your friends on anti-racism!",
		"Support the BIPOC community and creators!",
		"Stand up for equality in your community!",
	};

	private static final String[] addSplashesBrand = {
		"Just like you misremember!",
		"From the future, to the past!",
		"Yesterday's tomorrow, today!",
		"Comes in a can!",
		"Rose-tinted!",
		"Smearing!",
		"Now with oceanic consistency!",
		"Not one, not two, but THREE mod loaders!",
		"Now understands Java 8!",
		"Fits on a VHS!",
		"§bCo§dlo§frm§dat§bic",
		"Vertical!",
		"You hear about video games?",
		"Powered by complementary colors!",
		"Perfect is the enemy of good!",
		"Composed of blobs!",
		"Optionally difficult!",
		"Could have been interesting!",
		"Treatment for your burn!",
		"1.18?",
		"1.19?",
		"1.20??",
		"1.21???",
		"Order-dependent transparency!",
		"Now with additional biomes!",
		"Contains a variety of nuts!",
		"Sleepypack didn't go far enough",
		"This is not an Error, it's just an Information.",
		"Ain't nothin' like a funky beat!",
		"Take us back!",
		"Keep steppin' at your own pace!",
		"Made of parts!",
		"Absolutely comparator-free!",
		"Vanilla hoppers? You mean from BuildCraft?",
		"Overengineered!",
		"Why? Why not!",
		"Official NilLoader proof of concept!",
		"Here is there!",
		"Inaccurately represents VHS!",
		"§rContains §mMPEG-1§r §oTheora!§r", // reset spam is to avoid weird state leaks
		"Not designed for OLED displays!",
		"Toot toot sonic warrior!",
		"Forever in your mind!",
		"Shape the planets and conquer time!",
		"You can do anything!",
		"It came from space.",
		"Bootleg redstone blocks!",
		"N I K O L I T E",
		"Try the tank cart!",
		"Hover the buttons... to the beat!",
		"Now with lost Railcraft updates!",
		"Powered by Voldeloom!",
		"Relatively fixed absolutely broken coordinates",
		"Try the forum!",
		"\"Stock up on your Tungsten, you'll be glad you did.\" - Eloraam, 2012",
		"Reminiscent of VoxelMenu!",
		"Layerpacks!",
		"Absolutely no endianness issues!",
		"Mixels!",
		"Poor Unicode support!",
		"No antialiasing!",
		"Now a valid LLVM target!",
		"100% Fractureiser-free!",
		"The magic and the mystery of potato knishes",
		"There are many things that need to be erased",
		"Weird psuedo-textures!",
		"BlanketCon!",
		"ModFest!",
		"OverClocked ReMix!",
		"Aurora borealis",
		"Pre-glass ceiling",
		"",
		"If only!",
		"The real Ultimate Anniversary Edition",
		"I'm sorry that the menu music is only 64kbps Ogg Vorbis there's nothing I can do about it the author has been missing for years",
		"Nostalgic!",
		"We're finally landing!",
		"Now understands Java 21!?",
		"HTTP is more practical than Rednet",
		"Anachronistic!",
		"Probably no remote code execution vulnerabilities!",
		"Logistics Pipes over EnderStorage over HTTP",
		"16 colors!",
		"1 2 3 dontcha think, this is how it ought to be?",
		"Don't you find it all romantic, the way things used to be?",
		"Classic, ecstatic, it's magic",
		"We can skip the sign-off!",
		"Digital redstone!",
		"Bundled cable!",
		"Perfect full combo!",
		"Doesn't know what a UUID is!",

		"Try the clones!",
		"Also try Minetest!",
		"Also try Terasology!",
		"Also try Vintage Story!",
		"Also try ZZT!",
		"Also try MegaZeux!",
		"Also try Tinkerer's Quilt!",
		"Also try Xonotic!",
		"Also try RedPower3!",

		"#minecraftfarms",
	};

	private static final String[] rmSplashes = {
		"Notch <3 ez!",
		"Made by Notch!",
		"The Work of Notch!",
		"110813!",
		"Woo, /v/!",
		"Привет Россия!",
		"Hobo humping slobo babe!",
		"Lewd with two dudes with food!",
		// "Switches and ores!" gets to stay because it's an actually funny subversion, and on its own it's thematic
	};

	private static final String[] rmSplashesBrand = {
		"Don't bother with the clones!",
		"May contain nuts!", // Forestry adds a variety of nuts
	};

	@Patch.Method("<init>()V")
	public void patchInit(PatchContext ctx) {
		ctx.search(
			ALOAD(0),
			ALOAD(2),
			GETSTATIC("net/minecraft/client/gui/ScreenMainMenu", "rand", "Ljava/util/Random;")
		).jumpBefore();
		
		ctx.add(
			ALOAD(0),
			ALOAD(2),
			INVOKESTATIC(hooks(), "contributeSplashes", "(Lnet/minecraft/client/gui/ScreenMainMenu;Ljava/util/List;)V")
		);
	}

	@Patch.Method("initGui()V")
	public void patchInitGui(PatchContext ctx) {
		ctx.search(
			LDC("Happy birthday, ez!")
		).jumpAfter();
		ctx.add(
			POP(),
			ALOAD(0),
			GETFIELD("net/minecraft/client/gui/ScreenMainMenu", "splashText", "Ljava/lang/String;")
		);

		ctx.search(
			LDC("Happy birthday, Notch!")
		).jumpAfter();
		ctx.add(
			POP(),
			ALOAD(0),
			GETFIELD("net/minecraft/client/gui/ScreenMainMenu", "splashText", "Ljava/lang/String;")
		);
	}
	
	@Patch.Method("drawScreen(IIF)V")
	@Patch.Method.AffectsControlFlow
	public void patchDrawScreen(PatchContext ctx) {
		ctx.search(
			LDC(-20f)
		).jumpAfter();
		
		LabelNode L1 = new LabelNode();
		
		ctx.add(
			LDC("Vertical!"),
			ALOAD(0),
			GETFIELD("net/minecraft/client/gui/ScreenMainMenu", "splashText", "Ljava/lang/String;"),
			INVOKEVIRTUAL("java/lang/String", "equals", "(Ljava/lang/Object;)Z"),
			IFZ(L1),
			POP(),
			LDC(70f),
			L1
		);
	}
	
	public static final class Hooks {
		private Hooks() {}
		
		public static void contributeSplashes(ScreenMainMenu gui, List<String> splashes) {
			// add new splashes twice to make them more likely to be seen
			// everyone's seen the vanilla splashes infinity times
			for (String s : addSplashes) {
				splashes.add(s);
				splashes.add(s);
			}
			for (String s : rmSplashes) {
				splashes.remove(s);
			}
			
			if (UpsilonFixesConfig.upsilonBranding) {
				for (String s : addSplashesBrand) {
					splashes.add(s);
					splashes.add(s);
				}
				for (String s : rmSplashesBrand) {
					splashes.remove(s);
				}
			}
			
			if (Boolean.getBoolean("upsilonfixes.debugSplashes")) {
				for (String s : splashes) {
					System.out.println(s);
				}
			}
		}
	}

}
