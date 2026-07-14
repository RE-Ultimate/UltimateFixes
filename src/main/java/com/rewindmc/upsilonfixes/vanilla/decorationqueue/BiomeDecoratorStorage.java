package com.rewindmc.upsilonfixes.vanilla.decorationqueue;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BiomeDecoratorStorage {
	public static final BiomeDecoratorStorage INSTANCE = new BiomeDecoratorStorage();

	private final List<BiomeDecoratorInstance> decorators = new ArrayList<>();

	public void add(BiomeDecorator decorator, World var1, Random var2, int var3, int var4) {
		this.decorators.add(new BiomeDecoratorInstance(decorator, var1, var2, var3, var4));
	}

	public void decorate() {
		BiomeDecoratorInstance inst = this.decorators.get(0);

		inst.decorator.decorate(inst.var1, inst.var2, inst.var3, inst.var4);

		this.decorators.remove(0);
	}

	public boolean isReady() {
		return !this.decorators.isEmpty() && this.decorators.get(0).decorator.currentWorld == null;
	}
}
