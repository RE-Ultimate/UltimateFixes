package com.rewindmc.upsilonfixes.miscperipherals;

import java.util.HashMap;
import java.util.Map;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleFlowers;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.genetics.IAlleleEffect;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleSpecies;
import forestry.core.genetics.AlleleArea;
import forestry.core.genetics.AlleleBoolean;
import forestry.core.genetics.AlleleTolerance;
import forestry.core.utils.Vect;
import miscperipherals.tile.TileBeeAnalyzer;
import miscperipherals.util.Util;
import net.minecraft.item.ItemStack;
import nilloader.api.lib.asm.tree.LabelNode;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("miscperipherals.tile.TileBeeAnalyzer")
@ConfigOptions("tweakBeeAnalyzer")
public class TileBeeAnalyzerTransformer extends UpsilonMiniTransformer {

    @Patch.Method("getMethodNames()[Ljava/lang/String;")
    public void patchGetMethodNames(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			INVOKESTATIC(hooks(), "getMethodNames", "()[Ljava/lang/String;"),
			ARETURN()
		);
    }

    @Patch.Method("callMethod(Ldan200/computer/api/IComputerAccess;I[Ljava/lang/Object;)[Ljava/lang/Object; throws java/lang/Exception")
    public void patchCallMethod(PatchContext ctx) {
		ctx.jumpToStart();
		LabelNode label = new LabelNode();
		ctx.add(
			ALOAD(0),
			ILOAD(2),
			INVOKESTATIC(hooks(), "callMethod", "(Lmiscperipherals.tile.TileBeeAnalyzer;I)[Ljava/lang/Object;"),
			DUP(),
			IFNULL(label),
			ARETURN(),
			label,
			POP()
		);
    }

    public static class Hooks {
		
		public static String[] getMethodNames() {
			return new String[] {
				"analyze",
				"isBee",
				"analyzePrimary",
				"analyzeSecondary"
			};
		}

		public static Object[] callMethod(TileBeeAnalyzer tile, int method) {
			if (method == 2 || method == 3) {
				ItemStack stack = tile.a(0);
				if (stack != null) {
					IBee bee = BeeManager.beeInterface.getBee(stack);
					if (bee != null) {
						IBeeGenome genome = bee.getGenome();
						Map<String, Object> ret = new HashMap<>();
						ret.put("type", BeeManager.beeInterface.isDrone(stack) ? "drone" : (BeeManager.beeInterface.isMated(stack) ? "queen" : "princess"));
						if (method == 2) {
							ret.put("species", ((IAlleleSpecies) genome.getActiveAllele(EnumBeeChromosome.SPECIES.ordinal())).getName());
							ret.put("speed", ((IAlleleFloat) genome.getActiveAllele(EnumBeeChromosome.SPEED.ordinal())).getValue());
							ret.put("lifespan", ((IAlleleInteger) genome.getActiveAllele(EnumBeeChromosome.LIFESPAN.ordinal())).getValue());
							ret.put("fertility", ((IAlleleInteger) genome.getActiveAllele(EnumBeeChromosome.FERTILITY.ordinal())).getValue());
							ret.put("nocturnal", ((AlleleBoolean) genome.getActiveAllele(EnumBeeChromosome.NOCTURNAL.ordinal())).getValue());
							ret.put("tolerantFlyer", ((AlleleBoolean) genome.getActiveAllele(EnumBeeChromosome.TOLERANT_FLYER.ordinal())).getValue());
							ret.put("caveDwelling", ((AlleleBoolean) genome.getActiveAllele(EnumBeeChromosome.CAVE_DWELLING.ordinal())).getValue());
							ret.put("flower", ((IAlleleFlowers) genome.getActiveAllele(EnumBeeChromosome.FLOWER_PROVIDER.ordinal())).getProvider().getDescription());
							Vect area = ((AlleleArea) genome.getActiveAllele(EnumBeeChromosome.TERRITORY.ordinal())).getArea();
							ret.put("territory", Util.arrayToMap(new int[] {
								area.x, area.y, area.z
							}));
							ret.put("effect", ((IAlleleEffect) genome.getActiveAllele(EnumBeeChromosome.EFFECT.ordinal())).getIdentifier());
							ret.put("temperature", genome.getPrimary().getTemperature().toString());
							ret.put("toleranceTemperature", ((AlleleTolerance) genome.getActiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal())).toString());
							ret.put("humidity", genome.getPrimary().getHumidity().toString());
							ret.put("toleranceHumidity", ((AlleleTolerance) genome.getActiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal())).toString());
						} else {
							ret.put("species", ((IAlleleSpecies) genome.getInactiveAllele(EnumBeeChromosome.SPECIES.ordinal())).getName());
							ret.put("speed", ((IAlleleFloat) genome.getInactiveAllele(EnumBeeChromosome.SPEED.ordinal())).getValue());
							ret.put("lifespan", ((IAlleleInteger) genome.getInactiveAllele(EnumBeeChromosome.LIFESPAN.ordinal())).getValue());
							ret.put("fertility", ((IAlleleInteger) genome.getInactiveAllele(EnumBeeChromosome.FERTILITY.ordinal())).getValue());
							ret.put("nocturnal", ((AlleleBoolean) genome.getInactiveAllele(EnumBeeChromosome.NOCTURNAL.ordinal())).getValue());
							ret.put("tolerantFlyer", ((AlleleBoolean) genome.getInactiveAllele(EnumBeeChromosome.TOLERANT_FLYER.ordinal())).getValue());
							ret.put("caveDwelling", ((AlleleBoolean) genome.getInactiveAllele(EnumBeeChromosome.CAVE_DWELLING.ordinal())).getValue());
							ret.put("flower", ((IAlleleFlowers) genome.getInactiveAllele(EnumBeeChromosome.FLOWER_PROVIDER.ordinal())).getProvider().getDescription());
							Vect area = ((AlleleArea) genome.getInactiveAllele(EnumBeeChromosome.TERRITORY.ordinal())).getArea();
							ret.put("territory", Util.arrayToMap(new int[] {
								area.x, area.y, area.z
							}));
							ret.put("effect", ((IAlleleEffect) genome.getInactiveAllele(EnumBeeChromosome.EFFECT.ordinal())).getIdentifier());
							ret.put("temperature", genome.getSecondary().getTemperature().toString());
							ret.put("toleranceTemperature", ((AlleleTolerance) genome.getInactiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal())).toString());
							ret.put("humidity", genome.getSecondary().getHumidity().toString());
							ret.put("toleranceHumidity", ((AlleleTolerance) genome.getInactiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal())).toString());
						}
						return new Object[] {
							ret
						};
					}
				}
			}
			return null;
		}
    }
}
