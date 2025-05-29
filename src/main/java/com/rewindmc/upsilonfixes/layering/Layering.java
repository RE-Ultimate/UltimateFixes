package com.rewindmc.upsilonfixes.layering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.rewindmc.upsilonfixes.UpsilonFixesPremain;

import net.minecraft.client.texturepacks.ITexturePack;

public class Layering {

	private static final Map<ITexturePack, Boolean> cache = new WeakHashMap<>();
	
	public static final Set<ITexturePack> enabledLayerPacks = new HashSet<>();
	
	private static boolean reentering;
	
	public static boolean isLayerPack(ITexturePack pack) {
		if (reentering) return false;
		try {
			reentering = true;
			Boolean b = cache.get(pack);
			if (b == null) {
				InputStream in = pack.getResourceAsStream("/upsilon-layerable");
				if (in == null) {
					b = false;
				} else {
					try {
						in.close();
					} catch (IOException e) {}
					b = true;
				}
			}
			cache.put(pack, b);
			return b;
		} finally {
			reentering = false;
		}
	}
	
	public static void saveEnabledLayers() {
		File dir = new File("config/upsilonfixes");
		dir.mkdirs();
		File f = new File(dir, "enabled-layers.txt");
		try (FileOutputStream out = new FileOutputStream(f)) {
			for (ITexturePack pack : enabledLayerPacks) {
				out.write(pack.getTexturePackFileName().getBytes(StandardCharsets.UTF_8));
				out.write('\n');
			}
		} catch (Exception e) {
			UpsilonFixesPremain.log.error("Failed to save enabled layers", e);
		}
		new File("config/upsilonfixes-enabled-layers.txt").delete();
	}
	
	public static void refresh(List<ITexturePack> avail) {
		enabledLayerPacks.clear();
		Set<String> enabledNames = new HashSet<>();
		File dir = new File("config/upsilonfixes");
		File f = new File(dir, "enabled-layers.txt");
		if (!f.exists()) {
			f = new File("config/upsilonfixes-enabled-layers.txt");
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
			while (true) {
				String line = br.readLine();
				if (line == null) break;
				enabledNames.add(line);
			}
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			UpsilonFixesPremain.log.error("Failed to load enabled layers", e);
		}
		for (ITexturePack pack : avail) {
			if (enabledNames.contains(pack.getTexturePackFileName()) && isLayerPack(pack)) {
				enabledLayerPacks.add(pack);
			}
		}
	}
	
	
}
