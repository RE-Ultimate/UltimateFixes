package com.rewindmc.upsilonfixes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import nilloader.api.NilLogger;
import nilloader.api.lib.qdcss.BadValueException;
import nilloader.api.lib.qdcss.QDCSS;

public class UpsilonFixesConfig {

	public enum AutoReference {
		MACOS,
		NEW_JAVA,
		OLD_JAVA
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface Comment {
		String value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Default {
		AutoReference value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface Key {
		String value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface OldKey {
		String value();
	}
	
	public enum Trilean {
		AUTO,
		OFF,
		ON,
		;
		
		public boolean resolve(boolean def) {
			if (this == AUTO) return def;
			return this == ON;
		}
	}
	
	@OldKey("aer-from-flux-bee")
	@Key("tweaks.thaumicbees-aer-from-flux-bee")
 	@Comment("Allows the Flux bee from Thaumic Bees to provide Aer-type flux. The Aer effect is "
 			+ "often thought to be unused, but there are some obscure ways to get it in Thaumcraft, "
 			+ "so the Flux bee should be able to provide it.")
 	public static boolean aerFromFluxBee = true;
 	
	@OldKey("asm-workaround")
	@Key("hacks.asm-workaround")
 	@Comment("Makes ASM 4.0 pretend to be able to understand Java 8 class files. Necessary when "
 			+ "using NilLoader via hijacking. Breaks on newer Java, as it causes Forge to strip "
 			+ "all interface hints on invokes.\n\n"
 			+ "\"auto\" acts as \"on\" on Java 8, and \"off\" otherwise.")
 	@Default(AutoReference.OLD_JAVA)
 	public static Trilean asmWorkaround = Trilean.AUTO;
 	
	@OldKey("attacker-yaw-syncing")
	@Key("fixes.vanilla-attacker-yaw-syncing")
 	@Comment("Fixes the attacker yaw not syncing from server to client, to restore the proper "
 			+ "camera tilt animation when damaged.")
 	public static boolean attackerYawSyncing = true;
 	
	@OldKey("better-projecttable-nei-overlay")
	@Key("tweaks.rp2-better-projecttable-nei-overlay")
 	@Comment("Replaces the NEI overlay handler for shift-clicking recipes in Project Tables to "
 			+ "allow the insertion from its inventory. Previously only items in your inventory were used, not the ProjectTable inventory.")
 	public static boolean betterProjectTableNEIOverlay = true;
 	
	@OldKey("buff-gregtech-jackhammers")
	@Key("tweaks.gregtech-buff-jackhammers")
 	@Comment("Makes GregTech Jack Hammers a lot better, making them competitive with other forms of "
 			+ "bulk block mining even into endgame, but only for the typical \"natural\" stone "
 			+ "blocks.")
 	public static boolean buffGregTechJackHammers = true;
 	
	@OldKey("buff-wrath-forge")
	@Key("tweaks.fz-buff-wrath-forge")
 	@Comment("Buffs Factorization Wrath Forge construction by allowing the fire to last forever.")
 	public static boolean buffWrathForge = true;
 	
	@OldKey("disable-codechicken-stencil")
	@Key("fixes.codechicken-disable-stencil")
 	@Comment("Prevents CodeChickenCore from allocating a stencil buffer. May cause rendering "
 			+ "issues. Fixes the game not rendering at all on macOS.\n\n"
 			+ "\"auto\" acts as \"on\" on macOS, and \"off\" otherwise.")
 	@Default(AutoReference.MACOS)
 	public static Trilean disableCodeChickenStencil = Trilean.AUTO;
 
	@OldKey("disable-xrandr")
	@Key("hacks.disable-xrandr")
 	@Comment("Linux-only. Disables buggy XRandR support in LWJGL2. Causes various weird issues like "
 			+ "monitor flickering when closing the game or >60Hz getting messed up.")
 	public static boolean disableXrandr = true;
 	
	@OldKey("disable-xycraft-quartz-crystal-worldgen")
	@Key("tweaks.xycraft-disable-quartz-crystal-worldgen")
 	@Comment("Prevents XyCraft's quartz crystals from generating. They're a huge performance hit and "
 			+ "nobody likes them.")
 	public static boolean disableXycraftQuartzCrystalWorldgen = true;
 
	@OldKey("drop-key-in-inventories")
	@Key("backports.drop-key-in-inventories")
 	@Comment("Allows pressing the drop key over a slot in an inventory to drop the contents of the "
 			+ "slot on the ground. 1.5 backport.")
 	public static boolean dropKeyInInventories = true;
 	
	@Key("features.dump-lang")
 	@Comment("If on, dumps json files containing each mods' localizations to a \"langdumps\""
 			+ "directory.")
 	public static boolean dumpLang = false;
 	
	@OldKey("fix-anvil-peripheral")
	@Key("fixes.miscperipherals-anvil")
	@Comment("Fixes the MiscPeripherals anvil being able to repair with insufficient XP and "
			+ "sometimes voiding items in the first slot.")
 	public static boolean fixAnvilPeripheral = true;
 	
	@OldKey("fix-appeng-qchest")
	@Key("fixes.appeng-qchest")
 	@Comment("Prevents a crash when connecting a storage bus to GregTech machines, caused by AppEng "
 			+ "calling an API that no longer exists.")
 	public static boolean fixAppengQChest = true;
 	
	@OldKey("fix-chickenchunks-away-timeout")
	@Key("fixes.chickenchunks-away-timeout")
 	@Comment("Fixes ChickenChunks awayTimeout feature. If enabled in the config it should keep " +
 			"chunkloaders running for a specified time after the player logged off. This never " +
 			"worked, deactivating all chunk loaders of offline players instantly.")
 	public static boolean fixChickenChunksAwayTimeout = true;
 
	@OldKey("fix-codechickencore-hierarchy-check")
	@Key("fixes.codechickencore-hierarchy-check")
 	@Comment("Prevents CodeChickenCore from exploding when prematurely reading classes.")
 	public static boolean fixCodeChickenCoreHierarchyCheck = true;
 	
	@OldKey("fix-dartcraft-force-disabling-gregtech-tweaks")
	@Key("fixes.dartcraft-force-disabling-gregtech-tweaks")
 	@Comment("Removes DartCraft forcefully undoing GregTech's recipe tweaks. This is misguided in "
 			+ "the first place as GregTech lets you disable its changes in its config.")
 	public static boolean fixDartCraftForceDisablingGregTechTweaks = true;
 
	@OldKey("fix-dartcraft-force-engine-limit")
	@Key("fixes.dartcraft-force-engine-limit")
 	@Comment("Fixes the DartCraft Force Engine being effectively limited to 10MJ/t due to an "
 			+ "oversight. This is fixed in the 1.6 version of DartCraft.")
 	public static boolean fixDartCraftForceEngineLimit = true;
 
	@OldKey("fix-dartcraft-force-infuser-dimension")
	@Key("fixes.dartcraft-force-infuser-dimension")
 	@Comment("Fixes the DartCraft Force Infuser only working in the overworld.")
 	public static boolean fixDartCraftForceInfuserDimension = true;
 	
	@OldKey("fix-dartcraft-mobspawn-registration")
	@Key("fixes.dartcraft-mobspawn-registration")
 	@Comment("Prevents DartCraft mobs from spawning in wrong biomes. Moves the registration from "
 			+ "post init to init and prevents spawn in Mushroom Island biomes.")
 	public static boolean fixDartCraftMobSpawnRegistration = true;
 	
	@Key("fixes.dye-ore-dictionary")
 	@Comment("Transforms many recipes that accept vanilla dyes to also accept modded dyes. "
 			+ "Recipes that use certain dyes as their actual item (Lapis, Cocoa Beans, etc.) "
			+ "are left as is.")
 	public static boolean fixDyeOreDict = true;
 	
	@OldKey("fix-ee-transmute-recipes")
	@Key("fixes.ee-transmute-recipes")
 	@Comment("Fixes transmutation crafting recipes in EE3 only working once, and being broken by "
 			+ "NEI.")
 	public static boolean fixEETransmuteRecipes = true;
 	
	@OldKey("fix-enderstorage-unconditional-render")
	@Key("fixes.enderstorage-unconditional-render")
 	@Comment("Prevents the inner end portal animation in Ender Chests from rendering when closed. "
 			+ "Majorly improves performance.")
 	public static boolean fixEnderStorageUnconditionalRender = true;
 	
	@OldKey("fix-fonts-in-texture-packs")
	@Key("fixes.vanilla-fonts-in-texture-packs")
 	@Comment("Allow texture packs to override the font.")
 	public static boolean fixFontsInTexturePacks = true;
 	
	@OldKey("fix-forestry-fermenter-npe")
	@Key("fixes.forestry-fermenter-npe")
 	@Comment("Prevents registering null-fluid recipes in the Forestry fermenter.")
 	public static boolean fixForestryFermenterNPE = true;
 	
	@OldKey("fix-fz-barrel-lighting")
	@Key("fixes.fz-barrel-lighting")
 	@Comment("Fixes lighting on Factorization barrels.")
 	public static boolean fixFzBarrelLighting = true;
 	
	@OldKey("fix-fz-mixer-infinite-loop")
	@Key("fixes.fz-mixer-infinite-loop")
 	@Comment("Fixes an infinite loop with the Factorization mixer caused by a minor oversight.")
 	public static boolean fixFzMixerInfiniteLoop = true;

	@Key("fixes.ic2-network-crash")
	@Comment("Fixes a crash and console spam when large amounts of IC2 tile entity updates are send.")
	public static boolean fixIC2NetworkCrash = true;

	@OldKey("fix-ic2-seasonal-crash")
	@Key("fixes.ic2-seasonal-crash")
 	@Comment("Fixes a crash when IC2 tries to apply seasonal easter eggs to certain entities.")
 	public static boolean fixIC2SeasonalCrash = true;
 	
	@OldKey("fix-miscperipherals-asm")
	@Key("fixes.miscperipherals-asm-misuse")
 	@Comment("Fixes misuse of the ASM API by MiscPeripherals. Allows running with newer ASM "
 			+ "libraries that have stricter checking.")
 	public static boolean fixMiscPeripheralsASM = true;
	
	@Key("fixes.nei-tooltip-offscreen")
 	@Comment("Flips the NEI tooltip to the left when it would go off-screen.")
 	public static boolean fixNeiTooltipOffscreen = true;
 	
	@OldKey("fix-portal-gun-resources")
	@Key("fixes.portalgun-resources")
 	@Comment("Fixes sounds and music for Portal Gun by replacing the asset index.")
 	public static boolean fixPortalGunResources = true;
 	
	@OldKey("fix-rp2-hitboxes")
	@Key("fixes.rp2-hitboxes")
 	@Comment("Fixes RedPower2 non-full-block hitboxes frequently glitching out, causing players "
 			+ "to jitter and/or get stuck inside blocks. Makes lamp fixtures less scary to exist "
 			+ "near.")
 	public static boolean fixRedPowerHitboxes = true;
 	
	@OldKey("fix-rp2-manager-permanent-backstuff")
	@Key("fixes.rp2-manager-permanent-backstuff")
 	@Comment("Fixes RedPower2 managers getting permanently backstuffed in complex networks that use "
 			+ "multiple priorities.")
 	public static boolean fixRedPowerManagerPermanentBackstuff = true;
 
	@OldKey("fix-rp2-missing-inventory-npe")
	@Key("fixes.rp2-missing-inventory-npe")
 	@Comment("Fixes a NullPointerException crash caused by some cases of stale Inventory references "
 			+ "in RedPower2, primarily caused by Managers")
 	public static boolean fixRedPowerMissingInventoryNPE = true;
 
	@Key("fixes.thaumcraft-tag-npe")
 	@Comment("Prevents addition of null aspects to Thaumcraft flux and item data.")
 	public static boolean fixThaumcraftTagNpe = true;
 	
	@Key("fixes.thread-safety")
 	@Comment("Fixes random crashes caused by various mods accessing the world from the wrong thread.")
 	public static boolean fixThreadSafety = true;
 	
	@OldKey("fix-thermal-tesseract-cast")
	@Key("fixes.thermal-tesseract-cast")
 	@Comment("Fixes Thermal Expansion tesseract ClassCastException crashes when tile entity types "
 			+ "are desynced - can be caused by frame machines, turtles, etc.")
 	public static boolean fixThermalTesseractCast = true;
 	
	@OldKey("fix-uncrafting-exploits")
	@Key("fixes.twifor-uncrafting-exploits")
 	@Comment("Fixes Twilight Forest uncrafting exploits.")
 	public static boolean fixUncraftingExploits = true;
 	
	@OldKey("fix-xycraft-item-lighting")
	@Key("fixes.xycraft-item-lighting")
 	@Comment("Fixes XyCraft ore items messing up the light state, making all items rendered after "
 			+ "them in the GUI draw with wrong lighting.")
 	public static boolean fixXycraftItemLighting = true;
 	
	@OldKey("fix-xycraft-ore-textures")
	@Key("fixes.xycraft-ore-textures")
 	@Comment("Fixes XyCraft ore textures to actually use the correct parts of the block atlas, "
 			+ "instead of just recoloring the white one.")
 	public static boolean fixXycraftOreTextures = true;
 	
	@Key("hacks.forge-texturefx-workaround")
 	@Comment("Prevents crashes when the dimensions of an improperly-registered TextureFX are "
 			+ "queried.")
 	public static boolean forgeTextureFXWorkaround = true;
 	
	@OldKey("guis-in-portals")
	@Key("fixes.vanilla-guis-in-portals")
 	@Comment("Allows opening GUIs in Nether portals. This was a fix for a dupe bug in Beta that "
 			+ "is long gone.")
 	public static boolean guisInPortals = true;
 	
	@OldKey("improve-xreliquary-coin-pickup")
	@Key("tweaks.xreliquary-coin-pickup")
 	@Comment("Improves the item pickup mechanic from XReliquary's Coin of Fortune. Prevents a bug "
 			+ "where items would be floating at your feet but you can't pick them up.")
 	public static boolean improveXReliquaryCoinPickup = true;
 	
	@OldKey("increase-chat-limit")
	@Key("tweaks.vanilla-increase-chat-limit")
	@Comment("Increase the chat length limit to match modern versions.")
 	public static boolean increaseChatLimit = true;
 	
	@OldKey("keep-tesseract-frequency-on-dismantle")
	@Key("features.thermal-keep-tesseract-config-on-dismantle")
	@Comment("Stores the configuration of a dismantled Tesseract in the resulting item, and restores "
 			+ "it on place.")
 	public static boolean keepTesseractFrequencyOnDismantle = true;
 
	@OldKey("layered-texture-packs")
	@Key("features.vanilla-layered-texture-packs")
 	@Comment("Allows using multiple texture packs at once if they're marked as layerable.")
 	public static boolean layeredTexturePacks = true;
 
	@Key("features.load-lang")
 	@Comment("If on, loads lang files in a similar format to dump-lang (lang/modid/language.json)"
 			+ "into the FML LanguageRegistry.")
 	public static boolean loadLang = true;
 	
	@OldKey("localize-rejuvenating-effect")
	@Key("fixes.thaumicbees-localize-rejuvenating-effect")
 	@Comment("Adds a missing lang entry for the Rejuvenating bee's effect.")
 	public static boolean localizeRejuvenatingEffect = true;
 	
	@OldKey("localize-tradeomat-stock")
	@Key("fixes.ic2-localize-tradeomat-stock")
 	@Comment("Adds a missing lang entry for the Trade-O-Mat's stock.")
 	public static boolean localizeTradeOMatStock = true;
 	
	@OldKey("logistics-pipes-autocrafter-mk2-import")
	@Key("features.tubestuff-autocrafter-mk2-logisticspipes-import")
 	@Comment("Adds Tubestuff's Automatic Crafting Table MkII as an importable tile entity in "
 			+ "crafting logistics pipes.")
 	public static boolean logisticsPipesAutocrafterMk2Import = true;
 	
	@OldKey("logistics-pipes-fabricator-import")
	@Key("features.xycraft-fabricator-logisticspipes-import")
 	@Comment("Adds XyCraft's Fabricator as an importable tile entity in crafting logistics pipes.")
 	public static boolean logisticsPipesFabricatorImport = true;
 	
	@OldKey("modern-durability-color")
	@Key("backports.vanilla-modern-durability-color")
 	@Comment("Use a more legible hue-based gradient for the durability bar, rather than raw RGB. "
 			+ "1.11 backport.")
 	public static boolean modernDurabilityColor = true;
 
	@OldKey("modern-fps-slider")
	@Key("backports.vanilla-modern-fps-slider")
 	@Comment("Replace the \"Performance\" option with an FPS slider. 1.7 backport.")
 	public static boolean modernFpsSlider = true;
 	
	@OldKey("modern-gui-scale")
	@Key("backports.vanilla-modern-gui-scale")
 	@Comment("Replaces GUI Scale with a slider, instead of just Auto/Small/Normal/Large. 1.13 "
 			+ "backport, sort of. Vanilla still uses a cycling button in 1.21.5, the usage of a "
 			+ "slider is a Sodium thing.")
 	public static boolean modernGuiScale = true;
 
	@OldKey("modern-hotbar-binds")
	@Key("backports.vanilla-modern-hotbar-binds")
 	@Comment("Allows individually rebinding every hotbar key, instead of always using number keys. "
 			+ "1.7 backport.")
 	public static boolean modernHotbarBinds = true;
 	
	@OldKey("modern-java-support")
	@Key("features.modern-java-support")
 	@Comment("Patch FML and various mods to be compatible with newer versions of the JVM.\n\n"
 			+ "\"auto\" acts as \"off\" on Java 8, and \"on\" otherwise.")
 	@Default(AutoReference.NEW_JAVA)
 	public static Trilean modernJavaSupport = Trilean.AUTO;
 	
	@OldKey("optimize-state-access")
	@Key("fixes.forge-optimize-state-access")
 	@Comment("Optimizes some cases of mods (especially Forge item rendering) accessing slow OpenGL "
 			+ "state by caching it CPU-side.")
 	public static boolean optimizeStateAccess = true;
 	
	@OldKey("quantum-solar-helm-can-fix")
	@Key("fixes.gravisuite-quantum-solar-helm-feeding")
 	@Comment("Ultimate/Hybrid Solar Helmets will eat filled tin cans just like the quantum helmet "
 			+ "normally would.")
 	public static boolean quantumSolarHelmCans = true;
 	
	@OldKey("redesign-menus")
	@Key("features.redesign-menus")
 	@Comment("Redesign the main menu, pause menu, etc in a way very similar to VoxelMenu.")
 	public static boolean redesignMenus = false;
 	
	@OldKey("rp2-pipe-integration")
	@Key("features.rp2-buildcraft-integration")
 	@Comment("Add support for BuildCraft pipes to RedPower2.")
 	public static boolean redpowerBCCompat = true;
 	
	@Key("remove.all-version-checkers")
 	@Comment("Even remove update checkers that still work. Allows a fully-offline game launch.")
 	public static boolean removeAllVersionCheckers = false;
 	
	@OldKey("remove-dead-cosmetics")
	@Key("remove.dead-cosmetics")
 	@Comment("Prevent various mods from contacting dead servers for cosmetic features, such as capes.")
 	public static boolean removeDeadCosmetics = true;
 
	@Key("remove.portalgun-resource-downloader")
 	@Comment("Prevent Portal Gun from attempting to download resources. Alternative to "
 			+ "fixes.portalgun-resources that prevents server connections, but is incapable of "
 			+ "bootstrapping. Used in Rewind Upsilon as that distributes the Portal Gun resources "
 			+ "via unsup.")
 	public static boolean removePortalGunResourceDownload = false;
	 
	@OldKey("remove-snooper")
	@Key("remove.vanilla-snooper")
 	@Comment("Removes the vanilla snooper. The server is gone anyway.")
 	public static boolean removeSnooper = true;
	 
	@Key("remove.vanilla-resource-downloader")
 	@Comment("Prevent vanilla from attempting to contact a dead S3 bucket on launch to check for "
 			+ "resources to download. This is handled by launchers nowadays, and sounds are "
 			+ "prevented from playing until a response is received from the server.")
 	public static boolean removeVanillaResourceDownload = true;
 	
	@OldKey("remove-version-checkers")
	@Key("remove.dead-version-checkers")
 	@Comment("Removes attempts to contact dead version servers by mods that no longer receive "
 			+ "updates. (As such, does not disable BlockHelper or BuildCraft.)")
 	public static boolean removeVersionCheckers = true;
 	
	@OldKey("smearing")
	@Key("backports.vanilla-smearing")
 	@Comment("Allows holding left/right click and dragging with an item on your cursor to spread "
 			+ "it between all the passed slots. 1.5 backport.")
 	public static boolean smearing = true;
 
	@OldKey("sprint-key")
	@Key("features.sprint-key-over-ic2-boost-key")
 	@Comment("Overloads the IC2 boost key to also work like the modern vanilla sprint key.")
 	public static boolean sprintKey = true;
 
	@OldKey("swap-red-blue")
	@Key("hacks.swap-red-blue")
 	@Comment("Swap the red and blue channels in the main framebuffer. Fixes the game rendering "
 			+ "with incorrect colors on Apple Silicon Macs.\n\n"
 			+ "\"auto\" acts as \"on\" on macOS, and \"off\" otherwise.")
 	@Default(AutoReference.MACOS)
 	public static Trilean swapRedBlue = Trilean.AUTO;
 
	@Key("features.tattle-thread-safety")
 	@Comment("If on, prints errors to the log whenever the world is accessed from a thread other than "
 			+ "the game thread.")
 	public static boolean tattleThreadSafety = false;
 
	@Key("tweaks.miscperipherals-bee-analyzer")
 	@Comment("Adds additional methods to the MiscPeripherals Bee Analyzer to allow reading both "
 			+ "primary and secondary alleles. Previously, only primary traits would be read.")
 	public static boolean tweakBeeAnalyzer = true;
	 
	@OldKey("upsilon-branding")
	@Key("features.upsilon-branding")
 	@Comment("Enables the Rewind Upsilon modpack branding.")
 	public static boolean upsilonBranding = false;
	 
	@Key("backports.vanilla-whirlpool-fix")
 	@Comment("Fixes a typo in the water propagation code that prevents water sources from forming "
 			+ "above existing water sources, resulting in \"whirlpools\" when buckets are used in "
 			+ "lakes and such. 1.5 backport.")
 	public static boolean whirlpoolFix = true;

	static {
		Class<?> me = UpsilonFixesConfig.class;
		if (me.getClassLoader() != ClassLoader.getSystemClassLoader()) {
			NilLogger.get("UpsilonFixes").debug("Delegating UpsilonFixesConfig within relaunch class loader");
			try {
				Class<?> real = Class.forName(UpsilonFixesConfig.class.getName(), true, ClassLoader.getSystemClassLoader());
				for (Field f : real.getFields()) {
					if (Modifier.isStatic(f.getModifiers())) {
						Field mine = me.getField(f.getName());
						if (f.getType() == boolean.class) {
							mine.set(null, f.get(null));
						} else if (f.getType().isEnum()) {
							mine.set(null, Enum.valueOf((Class)mine.getType(), ((Enum<?>)f.get(null)).name()));
						}
					}
				}
			} catch (Throwable t) {
				throw new AssertionError(t);
			}
		} else {
			boolean deleteLoad = false;
			File cfgLoad = new File("config/upsilonfixes/patches.css");
			File cfgSave = cfgLoad;
			if (!cfgLoad.exists()) {
				deleteLoad = true;
				cfgLoad = new File("config/upsilonfixes.css");
			}
			QDCSS css = QDCSS.load("", "");
			try {
				css = QDCSS.load(cfgLoad);
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
				UpsilonFixesPremain.log.error("Failed to load upsilonfixes.css", e);
			}
			class Entry {
				String section, key, value, comment;
			}
			List<Entry> entries = new ArrayList<>();
			StringBuilder out = new StringBuilder("/*\r\n * UpsilonFixes configuration file\r\n * Any unrecognized keys or comments you add will be lost!\r\n */\r\n\r\n");
			try {
				for (Field f : UpsilonFixesConfig.class.getDeclaredFields()) {
					String k = f.getAnnotation(Key.class).value();
					String ok = f.getAnnotation(OldKey.class) != null ? f.getAnnotation(OldKey.class).value() : null;
					String valueStr;
					if (f.getType() == boolean.class) {
						Optional<Boolean> opt;
						try {
							opt = css.getBoolean(k);
							if (ok != null && !opt.isPresent()) opt = css.getBoolean("features."+ok);
						} catch (BadValueException e) {
							UpsilonFixesPremain.log.warn("Config file is malformed", e);
							opt = Optional.empty();
						}
						boolean v;
						if (opt.isPresent()) {
							v = opt.get();
							f.set(null, v);
						} else {
							v = f.getBoolean(null);
						}
						valueStr = v ? "on" : "off";
					} else if (f.getType() == Trilean.class) {
						Optional<Trilean> opt;
						try {
							opt = css.getEnum(k, Trilean.class);
							if (ok != null && !opt.isPresent()) opt = css.getEnum("features."+ok, Trilean.class);
						} catch (BadValueException e) {
							UpsilonFixesPremain.log.warn("Config file is malformed", e);
							opt = Optional.empty();
						}
						Trilean v;
						if (opt.isPresent()) {
							v = opt.get();
							f.set(null, v);
						} else {
							v = (Trilean)f.get(null);
						}
						valueStr = v.name().toLowerCase(Locale.ROOT);
					} else {
						UpsilonFixesPremain.log.warn("Unknown field type {}", f.getType());
						continue;
					}
					Comment comment = f.getAnnotation(Comment.class);
					Entry e = new Entry();
					e.section = k.substring(0, k.indexOf('.'));
					e.key = k.substring(k.indexOf('.')+1);
					e.value = valueStr;
					if (comment != null) {
						e.comment = comment.value();
					}
					entries.add(e);
				}
			} catch (IllegalAccessException e) {
				throw new AssertionError(e);
			}
			Collections.sort(entries, (a, b) -> {
				int sect = a.section.compareTo(b.section);
				if (sect != 0) return sect;
				return a.key.compareTo(b.key);
			});
			String prevSect = null;
			for (Entry e : entries) {
				if (!e.section.equals(prevSect)) {
					if (prevSect != null) out.append("}\r\n\r\n");
					out.append(e.section);
					out.append(" {");
					prevSect = e.section;
				}
				out.append("\r\n");
				if (e.comment != null) {
					out.append("\t/*\r\n\t * "+fold(e.comment, 80).replace("\n", "\r\n\t * ")+"\r\n\t */\r\n");
				}
				out.append("\t"+e.key+": "+e.value+";\r\n");
			}
			out.append("}\r\n");
			cfgSave.getParentFile().mkdirs();
			try (FileOutputStream fos = new FileOutputStream(cfgSave)) {
				fos.write(out.toString().getBytes("UTF-8"));
				if (deleteLoad) cfgLoad.delete();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Failed to save UpsilonFixes config");
			}
		}
	}
	
	// stolen from Apache Commons Lang
	public static String _fold(String str, int wrapLength) {
		if (str == null) {
			return null;
		}
		String newLineStr = "\n";
		if (wrapLength < 1) {
			wrapLength = 1;
		}
		int inputLineLength = str.length();
		int offset = 0;
		StringBuilder wrappedLine = new StringBuilder(inputLineLength + 32);

		while (inputLineLength - offset > wrapLength) {
			if (str.charAt(offset) == ' ') {
				offset++;
				continue;
			}
			int spaceToWrapAt = str.lastIndexOf(' ', wrapLength + offset);

			if (spaceToWrapAt >= offset) {
				// normal case
				wrappedLine.append(str.substring(offset, spaceToWrapAt));
				wrappedLine.append(newLineStr);
				offset = spaceToWrapAt + 1;

			} else {
				// really long word or URL
				// wrap really long word one line at a time
				wrappedLine.append(str.substring(offset, wrapLength + offset));
				wrappedLine.append(newLineStr);
				offset += wrapLength;
			}
		}

		// Whatever is left in line is short enough to just pass through
		wrappedLine.append(str.substring(offset));

		return wrappedLine.toString();
	}
	
	public static String fold(String str, int wrapLength) {
		String[] spl = str.split("\n");
		int len = 0;
		for (int i = 0; i < spl.length; i++) {
			spl[i] = _fold(spl[i], wrapLength);
			len += spl[i].length();
		}
		StringBuilder sb = new StringBuilder(len+spl.length);
		for (String l : spl) {
			sb.append(l);
			sb.append("\n");
		}
		sb.setLength(sb.length()-1);
		return sb.toString();
	}

}
