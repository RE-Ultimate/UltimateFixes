package com.rewindmc.upsilonfixes.vanilla.decorationqueue;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;

import java.util.Random;

class BiomeDecoratorInstance {
	final BiomeDecorator decorator;
	final World var1;
	final Random var2;
	final int var3;
	final int var4;

	BiomeDecoratorInstance(BiomeDecorator decorator, World var1, Random var2, int var3, int var4) {
		this.decorator = decorator;
		this.var1 = var1;
		this.var2 = var2;
		this.var3 = var3;
		this.var4 = var4;
	}
}
