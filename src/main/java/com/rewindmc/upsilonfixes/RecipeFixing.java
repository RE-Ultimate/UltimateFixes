package com.rewindmc.upsilonfixes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import buildcraft.BuildCraftTransport;
import ic2.core.AdvRecipe;
import ic2.core.AdvShapelessRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeFixing {
	private static final Map<Proto, String> REPLACEMENTS = new HashMap<>();
	private static final Set<Proto> IGNORED = new HashSet<>();

	static {
		if (UpsilonFixesConfig.fixDyeOreDict) {
			REPLACEMENTS.put(new Proto(Item.dyePowder, 0), "dyeBlack");
			REPLACEMENTS.put(new Proto(Item.dyePowder, 1), "dyeRed");
			REPLACEMENTS.put(new Proto(Item.dyePowder, 2), "dyeGreen");
			REPLACEMENTS.put(new Proto(Item.dyePowder, 3), "dyeBrown");
			REPLACEMENTS.put(new Proto(Item.dyePowder, 4), "dyeBlue");
			REPLACEMENTS.put(new Proto(Item.dyePowder, 5), "dyePurple");
			REPLACEMENTS.put(new Proto(Item.dyePowder, 6), "dyeCyan");
			REPLACEMENTS.put(new Proto(Item.dyePowder, 7), "dyeLightGray");
			REPLACEMENTS.put(new Proto(Item.dyePowder, 8), "dyeGray");
			REPLACEMENTS.put(new Proto(Item.dyePowder, 9), "dyePink");
			REPLACEMENTS.put(new Proto(Item.dyePowder, 10), "dyeLime");
			REPLACEMENTS.put(new Proto(Item.dyePowder, 11), "dyeYellow");
			REPLACEMENTS.put(new Proto(Item.dyePowder, 12), "dyeLightBlue");
			REPLACEMENTS.put(new Proto(Item.dyePowder, 13), "dyeMagenta");
			REPLACEMENTS.put(new Proto(Item.dyePowder, 14), "dyeOrange");
			REPLACEMENTS.put(new Proto(Item.dyePowder, 15), "dyeWhite");

			// Unconverted black dye recipes
			IGNORED.add(new Proto(Item.itemsList[1226], 3)); // Printer
			IGNORED.add(new Proto(Item.itemsList[25266], 0)); // Scribing Tools
			IGNORED.add(new Proto(Item.itemsList[27542], 21)); // Elixer of Breathing
			// Unconverted green dye recipes
			IGNORED.add(new Proto(BuildCraftTransport.pipeWaterproof, 0));
			// Unconverted brown dye recipes
			IGNORED.add(new Proto(Item.cookie, 0));
			IGNORED.add(new Proto(Item.itemsList[8816], 0)); // Chocolate Frame
			// Unconverted blue dye recipes
			IGNORED.add(new Proto(Block.blockLapis, 0));
			IGNORED.add(new Proto(Item.ingotIron, 0)); // Alkahestry
			IGNORED.add(new Proto(Item.dyePowder, 4)); // Alkahestry
			IGNORED.add(new Proto(Item.itemsList[7777], 0)); // RC Circuit
			IGNORED.add(new Proto(Item.itemsList[7777], 1)); // RC Circuit
			IGNORED.add(new Proto(Item.itemsList[7777], 2)); // RC Circuit
			IGNORED.add(new Proto(Item.itemsList[27527], 7)); // Seeker Shot
			IGNORED.add(new Proto(Item.itemsList[27527], 9)); // Storm Shot
			IGNORED.add(new Proto(Item.itemsList[30213], 0)); // Dynamite-o-mote
			IGNORED.add(new Proto(Item.itemsList[30356], 8)); // Irradiated Plate
			// Unconverted white dye recipes
			IGNORED.add(new Proto(Item.bone, 0));
			IGNORED.add(new Proto(Item.itemsList[27542], 10)); // Splash Fertilizer Serum
			IGNORED.add(new Proto(Item.itemsList[12246], 0)); // Industrial Fertilizer
			IGNORED.add(new Proto(Item.itemsList[29997], 18)); // Fertilizer
			IGNORED.add(new Proto(Item.itemsList[30121], 0)); // Fertilizer
		}
	}
	
	public static void fixRecipes() {
		if (REPLACEMENTS.isEmpty()) {
			return;
		}
		List<IRecipe> recipes = CraftingManager.getInstance().recipes;
		for (int i = 0; i < recipes.size(); i++) {
			IRecipe recipe = recipes.get(i);
			ItemStack output = recipe.getRecipeOutput();
			if (output != null && IGNORED.contains(new Proto(output.getItem(), output.getMetadata()))) {
				continue;
			}
			if (recipe instanceof ShapedRecipes) {
				ShapedRecipes shaped = (ShapedRecipes) recipe;
				if (shouldCoerce(shaped.recipeItems)) {
					recipes.set(i, new ShapedOreRecipe(shaped, getReplacements(Arrays.asList(shaped.recipeItems))));
				}
			} else if (recipe instanceof ShapelessRecipes) {
				ShapelessRecipes shapeless = (ShapelessRecipes) recipe;
				if (shouldCoerce(shapeless.recipeItems)) {
					recipes.set(i, new ShapelessOreRecipe(shapeless, getReplacements(shapeless.recipeItems)));
				}
			} else if (recipe instanceof AdvRecipe) {
				AdvRecipe adv = (AdvRecipe) recipe;
				if (shouldCoerce(adv.input)) {
					replaceStacks(adv.input);
				}
			} else if (recipe instanceof AdvShapelessRecipe) {
				AdvShapelessRecipe adv = (AdvShapelessRecipe) recipe;
				if (shouldCoerce(adv.input)) {
					replaceStacks(adv.input);
				}
			} else if (recipe instanceof ShapedOreRecipe) {
				ShapedOreRecipe shaped = (ShapedOreRecipe) recipe;
				if (shouldCoerce(shaped.input)) {
					replaceStacks(shaped.input);
				}
			} else if (recipe instanceof ShapelessOreRecipe) {
				ShapelessOreRecipe shapeless = (ShapelessOreRecipe) recipe;
				if (shouldCoerce(shapeless.input)) {
					replaceStacks(shapeless.input);
				}
			}
		}
	}

	private static boolean shouldCoerce(Object[] stacks) {
		for (Object obj : stacks) {
			if (obj instanceof ItemStack) {
				ItemStack stack = (ItemStack) obj;
				Proto proto = new Proto(stack.getItem(), stack.getMetadata());
				if (REPLACEMENTS.containsKey(proto)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean shouldCoerce(Iterable<Object> stacks) {
		for (Object obj : stacks) {
			if (obj instanceof ItemStack) {
				ItemStack stack = (ItemStack) obj;
				Proto proto = new Proto(stack.getItem(), stack.getMetadata());
				if (REPLACEMENTS.containsKey(proto)) {
					return true;
				}
			}
		}
		return false;
	}

	private static void replaceStacks(Object[] stacks) {
		for (int i = 0; i < stacks.length; i++) {
			Object obj = stacks[i];
			if (obj instanceof ItemStack) {
				ItemStack stack = (ItemStack) obj;
				Proto proto = new Proto(stack.getItem(), stack.getMetadata());
				if (REPLACEMENTS.containsKey(proto)) {
					stacks[i] = REPLACEMENTS.get(proto);
				}
			}
		}
	}

	private static void replaceStacks(List<Object> stacks) {
		for (int i = 0; i < stacks.size(); i++) {
			Object obj = stacks.get(i);
			if (obj instanceof ItemStack) {
				ItemStack stack = (ItemStack) obj;
				Proto proto = new Proto(stack.getItem(), stack.getMetadata());
				if (REPLACEMENTS.containsKey(proto)) {
					stacks.set(i, REPLACEMENTS.get(proto));
				}
			}
		}
	}

	private static Map<ItemStack, String> getReplacements(List<ItemStack> stacks) {
		Map<ItemStack, String> map = new HashMap<>();
		for (ItemStack stack : stacks) {
			if (stack != null) {
				Proto proto = new Proto(stack.getItem(), stack.getMetadata());
				if (REPLACEMENTS.containsKey(proto)) {
					map.put(stack, REPLACEMENTS.get(proto));
				}
			}
		}
		return map;
	}

	private static class Proto {
		public final Item item;
		public final int meta;

		public Proto(Item item, int meta) {
			this.item = item;
			this.meta = meta;
		}

		public Proto(Block block, int meta) {
			this(Item.itemsList[block.blockID], meta);
		}

		@Override
		public int hashCode() {
			return item == null ? 0 : item.hashCode() * 31 + meta;
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof Proto && ((Proto) other).item == this.item && ((Proto) other).meta == this.meta;
		}
	}
}
