package com.rewindmc.upsilonfixes.minefactory;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AutoEnchantmentHelperTest {

	@Test
	public void generatedEnchantmentsAreUniqueAndCompatible() throws Exception {
		Class.forName("net.minecraft.block.Block");
		Class<?> helper = new ClassLoader(getClass().getClassLoader()) {
			Class<?> loadPatchedHelper() throws Exception {
				String name = "powercrystals.minefactoryreloaded.core.AutoEnchantmentHelper";
				try (InputStream in = getResourceAsStream(name.replace('.', '/') + ".class")) {
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					byte[] buffer = new byte[4096];
					int read;
					while ((read = in.read(buffer)) != -1) bytes.write(buffer, 0, read);
					byte[] patched = new AutoEnchantmentHelperTransformer().transform(name, bytes.toByteArray());
					return defineClass(name, patched, 0, patched.length);
				}
			}
		}.loadPatchedHelper();
		Method build = helper.getMethod("buildEnchantmentList", Random.class, ItemStack.class, int.class);
		for (Item item : new Item[] {Item.pickaxeDiamond, Item.swordDiamond, Item.plateDiamond, Item.book}) {
			for (int seed = 0; seed < 1000; seed++) {
				List<EnchantmentData> enchantments = (List<EnchantmentData>)build.invoke(null, new Random(seed), new ItemStack(item), 30);
				if (enchantments == null) continue;
				Set<Integer> ids = new HashSet<>();
				for (EnchantmentData enchantment : enchantments) {
					assertTrue("Duplicate enchantment " + enchantment.enchantmentobj.effectId + " for seed " + seed,
							ids.add(enchantment.enchantmentobj.effectId));
					for (EnchantmentData other : enchantments) {
						if (other != enchantment) {
							assertTrue("Incompatible enchantments for seed " + seed,
									enchantment.enchantmentobj.canApplyTogether(other.enchantmentobj));
						}
					}
				}
			}
		}
	}
}
