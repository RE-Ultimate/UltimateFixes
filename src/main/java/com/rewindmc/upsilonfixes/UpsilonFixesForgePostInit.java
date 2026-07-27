package com.rewindmc.upsilonfixes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import com.rewindmc.upsilonfixes.lang.LanguageRegistryTransformer;
import com.rewindmc.upsilonfixes.tubestuff.AutoCraftingMk2CraftingRecipeProvider;
import com.rewindmc.upsilonfixes.vanilla.ModernHotbarBinds;
import com.rewindmc.upsilonfixes.xycraft.FabricatorCraftingRecipeProvider;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import gregtechmod.common.items.GT_Jackhammer_Item;
import ic2.core.IC2;
import ic2.core.util.KeyboardClient;
import info.jbcs.minecraft.chisel.Chisel;
import logisticspipes.proxy.SimpleServiceLocator;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraftforge.common.MinecraftForge;
import nilloader.api.lib.nanojson.JsonObject;
import nilloader.api.lib.nanojson.JsonParser;
import nilloader.api.lib.nanojson.JsonParserException;

public class UpsilonFixesForgePostInit implements Runnable {

	@Override
	public void run() {
		ClassLoader loader = callInRelauncher("com.rewindmc.upsilonfixes.UpsilonFixesForgePostInit$InRelauncher");
		if (Boolean.getBoolean("upsilonfixes.loadAllTargets")) {
			for (String s : UpsilonFixesPremain.transformerTargets) {
				try {
					Class.forName(s.replace('/', '.'), true, loader);
					UpsilonFixesPremain.log.info("Loaded {} successfully!", s);
				} catch (Throwable e) {
					UpsilonFixesPremain.log.warn("Error while loading {}", s, e);
				}
			}
		}
	}
	
	public static ClassLoader callInRelauncher(String clazz) {
		try {
			Class<?> relauncherClazz = Class.forName("cpw.mods.fml.relauncher.FMLRelauncher");
			Method instance = relauncherClazz.getDeclaredMethod("instance");
			instance.setAccessible(true);
			Object relauncher = instance.invoke(null);
			Field classLoader = relauncherClazz.getDeclaredField("classLoader");
			classLoader.setAccessible(true);
			ClassLoader loader = (ClassLoader)classLoader.get(relauncher);
			Class.forName(clazz, true, loader).getMethod("run").invoke(null);
			return loader;
		} catch (Exception e) {
			UpsilonFixesPremain.log.warn("Failed to invoke {} inside relaunch class loader", clazz, e);
			return null;
		}
	}
	
	public static class InRelauncher {
		
		private static class ChiselIndirection {
			public static void init() {
				try {
					GT_Jackhammer_Item.mineableBlocks.add(Chisel.blockLimestone);
					GT_Jackhammer_Item.mineableBlocks.add(Chisel.blockMarble);
				} catch (Throwable t) {}
			}
		}
		
		private static class GTIndirection {
			public static void init() {
				ChiselIndirection.init();
			}
		}
		
		private static class LPIndirection {
			public static void init() {
				try {
					if (UpsilonFixesConfig.logisticsPipesFabricatorImport) {
						SimpleServiceLocator.addCraftingRecipeProvider(new FabricatorCraftingRecipeProvider());
					}
				} catch (Throwable t) {}
				try {
					if (UpsilonFixesConfig.logisticsPipesAutocrafterMk2Import) {
						SimpleServiceLocator.addCraftingRecipeProvider(new AutoCraftingMk2CraftingRecipeProvider());
					}
				} catch (Throwable t) {}
			}
		}
		
		public static void run() {
			if (UpsilonFixesConfig.localizeRejuvenatingEffect) {
				LanguageRegistryTransformer.Hooks.modidOverride.set("ThaumicBees");
				try {
					LanguageRegistry.instance().addStringLocalization("thaumicbees.effectNodeCharge", "Aura Charge");
				} finally {
					
				}
			}
			if (UpsilonFixesConfig.localizeTradeOMatStock) {
				LanguageRegistryTransformer.Hooks.modidOverride.set("IC2");
				try {
					LanguageRegistry.instance().addStringLocalization("container.personalTrader.stock", "Stock");
				} finally {
					LanguageRegistryTransformer.Hooks.modidOverride.set(null);
				}
			}
			try {
				GTIndirection.init();
			} catch (Throwable t) {}
			if (UpsilonFixesConfig.increaseChatLimit) {
				Packet3Chat.maxChatLength = Math.max(Packet3Chat.maxChatLength, 384);
			}
			try {
				LPIndirection.init();
			} catch (Throwable t) {}
			MinecraftForge.EVENT_BUS.register(new UpsilonForgeEvents());
			if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
				try {
					Client.init();
				} catch (Throwable t) {}
			}
			if (UpsilonFixesConfig.loadLang) {
				File dir = new File("lang");
				File[] list = dir.listFiles();
				if (list != null) {
					int files = 0;
					int keys = 0;
					for (File mod : list) {
						String modid = mod.getName();
						File[] langlist = mod.listFiles();
						if (langlist != null) {
							LanguageRegistryTransformer.Hooks.modidOverride.set(modid);
							try {
								for (File langFile : langlist) {
									if (langFile.isFile() && langFile.getName().endsWith(".json")) {
										String lang = langFile.getName();
										lang = lang.substring(0, lang.length()-5);
										try (FileInputStream in = new FileInputStream(langFile)) {
											JsonObject obj = JsonParser.object().from(in);
											files++;
											for (Map.Entry<String, Object> en : obj.entrySet()) {
												if (en.getValue() instanceof String) {
													keys++;
													LanguageRegistry.instance().addStringLocalization(en.getKey(), lang, (String)en.getValue());
												} else {
													UpsilonFixesPremain.log.warn("Invalid value type {} for key {} in {}", en.getValue().getClass().getSimpleName(), en.getKey(), langFile);
												}
											}
										} catch (IOException | JsonParserException e) {
											UpsilonFixesPremain.log.error("Failed to load {}", langFile, e);
										}
									}
								}
							} finally {
								LanguageRegistryTransformer.Hooks.modidOverride.set(null);
							}
						}
					}
					if (files > 0) {
						UpsilonFixesPremain.log.info("Injected {} lang key{} from {} file{}", keys, keys == 1 ? "" : "s", files, files == 1 ? "" : "s");
					} else {
						UpsilonFixesPremain.log.debug("Found no lang files to inject from");
					}
				}
			}
		}

		public static class Client {
			
			private static class IC2ClientIndirection {
				public static void init() {
					((KeyboardClient)IC2.keyboard).boostKey.keyDescription = "Sprint/Boost Key";
				}
			}
			
			public static void init() {
				try {
					IC2ClientIndirection.init();
				} catch (Throwable t) {}
				
				if (UpsilonFixesConfig.modernHotbarBinds) {
					ModernHotbarBinds.init();
				}
			}
			
		}
		
	}
	
}
