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
import java.util.Locale;
import java.util.Optional;

import nilloader.api.NilLogger;
import nilloader.api.lib.qdcss.BadValueException;
import nilloader.api.lib.qdcss.QDCSS;

public class UpsilonFixesConfig {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface Comment {
		String value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface Key {
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
	
	@Key("aer-from-flux-bee")
	@Comment("Allows the Flux bee from Thaumic Bees to provide Aer-type flux. The Aer effect is "
			+ "often thought to be unused, but there are some obscure ways to get it in Thaumcraft, "
			+ "so the Flux bee should be able to provide it.")
	public static boolean aerFromFluxBee = true;
	
	@Key("asm-workaround")
	@Comment("Makes ASM 4.0 pretend to be able to understand Java 8 class files. Necessary when "
			+ "using NilLoader via hijacking.")
	public static boolean asmWorkaround = true;
	
	@Key("attacker-yaw-syncing")
	@Comment("Fixes the attacker yaw not syncing from server to client, to restore the proper "
			+ "camera tilt animation when damaged.")
	public static boolean attackerYawSyncing = true;
	
	@Key("better-projecttable-nei-overlay")
	@Comment("Replaces the NEI overlay handler for shift-clicking recipes in Project Tables to "
			+ "allow the insertion from its inventory. Previously only items in your inventory were used, not the ProjectTable inventory.")
	public static boolean betterProjectTableNEIOverlay = true;
	
	@Key("buff-gregtech-jackhammers")
	@Comment("Makes GregTech Jack Hammers a lot better.")
	public static boolean buffGregTechJackHammers = true;

	@Key("buff-wrath-forge")
	@Comment("Buffs the Factorization Wrath Forge by making the fire last forever.")
	public static boolean buffWrathForge = true;
	
	@Key("disable-codechicken-stencil")
	@Comment("Prevents CodeChickenCore from allocating a stencil buffer. May cause rendering "
			+ "issues. Fixes the game not rendering at all on macOS.")
	public static Trilean disableCodeChickenStencil = Trilean.AUTO;

	@Key("disable-xrandr")
	@Comment("Linux-only. Disables buggy XRandR support in LWJGL2. Causes various weird issues like "
			+ "monitor flickering when closing the game or >60Hz getting messed up.")
	public static boolean disableXrandr = true;
	
	@Key("disable-xycraft-quartz-crystal-worldgen")
	@Comment("Prevents XyCraft's quartz crystals from generating. They're a huge performance hit and "
			+ "nobody likes them.")
	public static boolean disableXycraftQuartzCrystalWorldgen = true;
	
	@Key("drop-key-in-inventories")
	@Comment("Allows pressing the drop key over a slot in an inventory to drop the contents of the "
			+ "slot on the ground. 1.5 backport.")
	public static boolean dropKeyInInventories = true;

	@Key("fix-anvil-peripheral")
	@Comment("MiscPeripheral's Anvil repairs items even if the turtle doesn't have enough XP. It "
			+ "also voids the first item if the second slot is empty.")
	public static boolean fixAnvilPeripheral = true;
	
	@Key("fix-appeng-qchest")
	@Comment("Prevents a crash when connecting a storage bus to GregTech machines as AppEng called "
			+ "a non-existent API endpoint.")
	public static boolean fixAppengQChest = true;

	@Key("fix-chickenchunks-away-timeout")
	@Comment("Fixes ChickenChunks awayTimeout feature. If enabled in the config it should keep " +
			"chunkloaders running for a specified time after the player logged off. This never " +
			"worked, deactivating all chunk loaders of offline players instantly.")
	public static boolean fixChickenChunksAwayTimeout = true;

	@Key("fix-codechickencore-hierarchy-check")
	@Comment("Prevents CodeChickenCore from exploding when prematurely reading classes.")
	public static boolean fixCodeChickenCoreHierarchyCheck = true;
	
	@Key("fix-dartcraft-force-disabling-gregtech-tweaks")
	@Comment("Removes DartCraft forcefully undoing GregTech's recipe tweaks. This is misguided in "
			+ "the first place as GregTech lets you disable its changes in its config.")
	public static boolean fixDartCraftForceDisablingGregTechTweaks = true;
	
	@Key("fix-dartcraft-force-engine-limit")
	@Comment("Fixes the DartCraft Force Engine being effectively limited to 10MJ/t due to an "
			+ "oversight. This is fixed in the 1.6 version of DartCraft.")
	public static boolean fixDartCraftForceEngineLimit = true;
	
	@Key("fix-dartcraft-force-infuser-dimension")
	@Comment("Fixes the DartCraft Force Infuser only working in the overworld.")
	public static boolean fixDartCraftForceInfuserDimension = true;
	
	@Key("fix-dartcraft-mobspawn-registration")
	@Comment("Prevents DartCraft mobs from spawning in wrong biomes. Moves the registration from "
			+ "post init to init and prevents spawn in Mushroom Island biomes.")
	public static boolean fixDartCraftMobSpawnRegistration = true;
	
	@Key("fix-ee-transmute-recipes")
	@Comment("Fixes transmutation crafting recipes in EE3 only working once, and being broken by "
			+ "NEI.")
	public static boolean fixEETransmuteRecipes = true;
	
	@Key("fix-fonts-in-texture-packs")
	@Comment("Allow texture packs to override the font.")
	public static boolean fixFontsInTexturePacks = true;
	
	@Key("fix-forestry-fermenter-npe")
	@Comment("Prevents registering null-fluid recipes in the Forestry fermenter.")
	public static boolean fixForestryFermenterNPE = true;
	
	@Key("fix-fz-barrel-lighting")
	@Comment("Fixes lighting on Factorization barrels.")
	public static boolean fixFzBarrelLighting = true;
	
	@Key("fix-fz-mixer-infinite-loop")
	@Comment("Fixes an infinite loop with the Factorization mixer caused by a minor oversight.")
	public static boolean fixFzMixerInfiniteLoop = true;
	
	@Key("fix-gregtech-cell-remainder")
	@Comment("Configures remainder items for GregTech cells.")
	public static boolean fixGregTechCellRemainder = true;
	
	@Key("fix-ic2-seasonal-crash")
	@Comment("Fixes a crash when IC2 tries to apply seasonal easter eggs to certain entities.")
	public static boolean fixIC2SeasonalCrash = true;
	
	@Key("fix-miscperipherals-asm")
	@Comment("Fixes misuse of the ASM API by MiscPeripherals breaking with newer ASM libraries.")
	public static boolean fixMiscPeripheralsASM = true;

	@Key("fix-portal-gun-resources")
	@Comment("Fixes sounds and music for Portal Gun by replacing the asset index.")
	public static boolean fixPortalGunResources = true;

	@Key("fix-redpower-hitboxes")
	@Comment("Fixes RedPower2 non-full-block hitboxes.")
	public static boolean fixRedPowerHitboxes = true;
	
	@Key("fix-redpower-missing-inventory-npe")
	@Comment("Fixes a NullPointerException crash caused by some cases of stale Inventory references "
			+ "in RedPower2, primarily caused by Managers")
	public static boolean fixRedPowerMissingInventoryNPE = true;
	
	@Key("fix-thermal-tesseract-cast")
	@Comment("Fixes Thermal Expansion tesseract ClassCastException crashes when tile entity types "
			+ "are desynced - can be caused by frame machines, turtles, etc.")
	public static boolean fixThermalTesseractCast = true;
	
	@Key("fix-uncrafting-exploits")
	@Comment("Fixes Twilight Forest uncrafting exploits.")
	public static boolean fixUncraftingExploits = true;
	
	@Key("fix-xycraft-item-lighting")
	@Comment("Fixes XyCraft ore items messing up the light state, making all items rendered after "
			+ "them in the GUI draw with wrong lighting.")
	public static boolean fixXycraftItemLighting = true;
	
	@Key("fix-xycraft-ore-textures")
	@Comment("Fixes XyCraft ore textures to actually use the correct parts of the block atlas, "
			+ "instead of just recoloring the white one.")
	public static boolean fixXycraftOreTextures = true;
	
	@Key("guis-in-portals")
	@Comment("Allows opening GUIs in Nether portals. This was a fix for a dupe bug in Beta that "
			+ "is long gone.")
	public static boolean guisInPortals = true;
	
	@Key("improve-xreliquary-coin-pickup")
	@Comment("Improves the item pickup mechanic from XReliquary's Coin of Fortune. Prevents a bug "
			+ "where items would be floating at your feet but you can't pick them up.")
	public static boolean improveXReliquaryCoinPickup = true;

	@Key("increase-chat-limit")
	@Comment("Increase the chat length limit from 100 to 256 to match modern versions.")
	public static boolean increaseChatLimit = true;

	@Key("keep-tesseract-frequency-on-dismantle")
	@Comment("Stores the frequency of a dismantled Tesseract in the resulting item, and restores "
			+ "it on place.")
	public static boolean keepTesseractFrequencyOnDismantle = true;
	
	@Key("layered-texture-packs")
	@Comment("Allows using multiple texture packs at once if they're marked as layerable.")
	public static boolean layeredTexturePacks = true;
	
	@Key("localize-rejuvenating-effect")
	@Comment("Adds a missing lang entry for the Rejuvenating bee's effect.")
	public static boolean localizeRejuvenatingEffect = true;
	
	@Key("localize-tradeomat-stock")
	@Comment("Adds a missing lang entry for the Trade-O-Mat's stock.")
	public static boolean localizeTradeOMatStock = true;
	
	@Key("logistics-pipes-autocrafter-mk2-import")
	@Comment("Adds Tubestuff's Automatic Crafting Table MkII as an importable tile entity in "
			+ "crafting logistics pipes.")
	public static boolean logisticsPipesAutocrafterMk2Import = true;
	
	@Key("logistics-pipes-fabricator-import")
	@Comment("Adds XyCraft's Fabricator as an importable tile entity in crafting logistics pipes.")
	public static boolean logisticsPipesFabricatorImport = true;

	@Key("modern-durability-color")
	@Comment("Backports the modern hue-based durability bar color - it's more legible.")
	public static boolean modernDurabilityColor = true;
	
	@Key("modern-fps-slider")
	@Comment("Replace the \"Performance\" option with an FPS slider like modern versions.")
	public static boolean modernFpsSlider = true;

	@Key("modern-gui-scale")
	@Comment("Backports the modern GUI Scale option that lets you set it to anything that fits, "
			+ "instead of only Auto/Small/Normal/Large.")
	public static boolean modernGuiScale = true;
	
	@Key("modern-hotbar-binds")
	@Comment("Backports the modern individual configuration for hotbar keybinds.")
	public static boolean modernHotbarBinds = true;
	
	@Key("quantum-solar-helm-can-fix")
	@Comment("Ultimate/Hybrid Solar Helmets will eat filled tin cans just like the quantum helmet "
			+ "normally would.")
	public static boolean quantumSolarHelmCans = true;
	
	@Key("redesign-menus")
	@Comment("Redesign the main menu, pause menu, etc in a way very similar to VoxelMenu.")
	public static boolean redesignMenus = false;
	
	@Key("redpower-pipe-integration")
	@Comment("RedPower2 can insert into BuildCraft pipes.")
	public static boolean redpowerBCCompat = true;
	
	@Key("remove-dead-cosmetics")
	@Comment("Some mods add cosmetics (usually capes) that try to contact dead servers.")
	public static boolean removeDeadCosmetics = true;
	
	@Key("remove-snooper")
	@Comment("Removes the vanilla snooper. The server is gone anyway.")
	public static boolean removeSnooper = true;
	
	@Key("remove-version-checkers")
	@Comment("1.4.7 mods don't really get updates anymore, and many version checkers try to "
			+ "contact dead servers.")
	public static boolean removeVersionCheckers = true;

	@Key("smearing")
	@Comment("Allows holding left/right click and dragging with an item on your cursor to spread "
			+ "it between all the passed slots. 1.5 backport.")
	public static boolean smearing = true;

	@Key("sprint-key")
	@Comment("Overloads the IC2 boost key to also work like the modern vanilla sprint key.")
	public static boolean sprintKey = true;

	@Key("swap-red-blue")
	@Comment("Swap the red and blue channels in the main framebuffer. Fixes the game rendering "
			+ "with incorrect colors on Apple Silicon Macs.")
	public static Trilean swapRedBlue = Trilean.AUTO;

	@Key("upsilon-branding")
	@Comment("Enables the Rewind Upsilon modpack branding.")
	public static boolean upsilonBranding = false;

	@Key("whirlpool-fix")
	@Comment("Backports the 1.5 'water source blocks fill in above water' fix.")
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
			File cfg = new File("config/upsilonfixes.css");
			QDCSS css = QDCSS.load("", "");
			try {
				css = QDCSS.load(cfg);
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
				UpsilonFixesPremain.log.error("Failed to load upsilonfixes.css", e);
			}
			StringBuilder out = new StringBuilder("/*\r\n * UpsilonFixes configuration file\r\n * Any unrecognized keys or comments you add will be lost!\r\n */\r\n\r\nfeatures {\r\n");
			try {
				for (Field f : UpsilonFixesConfig.class.getDeclaredFields()) {
					String k = f.getAnnotation(Key.class).value();
					String valueStr;
					if (f.getType() == boolean.class) {
						Optional<Boolean> opt;
						try {
							opt = css.getBoolean("features."+k);
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
							opt = css.getEnum("features."+k, Trilean.class);
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
					if (comment != null) {
						out.append("\t/*\r\n\t * "+fold(comment.value(), 80).replace("\n", "\r\n\t * ")+"\r\n\t */\r\n");
					}
					out.append("\t"+k+": "+valueStr+";\r\n\r\n");
				}
			} catch (IllegalAccessException e) {
				throw new AssertionError(e);
			}
			out.append("}\r\n");
			try (FileOutputStream fos = new FileOutputStream(cfg)) {
				cfg.getParentFile().mkdirs();
				fos.write(out.toString().getBytes("UTF-8"));
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Failed to save UpsilonFixes config");
			}
		}
	}
	
	// stolen from Apache Commons Lang
	public static String fold(String str, int wrapLength) {
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

}
