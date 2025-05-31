package com.rewindmc.upsilonfixes;

import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

public class UpsilonForgeEvents {
	
	@ForgeSubscribe
	public void worldLoadedEvent(WorldEvent.Load load) {
		RecipeFixing.fixRecipes();
	}
}
