#!/bin/bash
classes=(
	# Forge
	net.minecraft.block.EnumMobType
	net.minecraft.enchantment.EnumEnchantmentType
	net.minecraft.entity.EnumCreatureAttribute
	net.minecraft.entity.EnumCreatureType
	net.minecraft.entity.EnumEntitySize
	net.minecraft.entity.player.EnumStatus
	net.minecraft.item.EnumAction
	net.minecraft.item.EnumArmorMaterial
	net.minecraft.item.EnumToolMaterial
	net.minecraft.util.EnumArt
	net.minecraft.util.RayTraceType
	net.minecraft.world.EnumSkyBlock
	net.minecraft.world.gen.structure.EnumDoor
	
	# EE3
	net.minecraft.item.EnumRarity
)
for e in "${classes[@]}"; do
	sn=${e##*.}
	sed "s/CLASS.NAME/$e/g;s/CLASS.SIMPLE_NAME/$sn/g" tmpl.txt > "${sn}Transformer.java"
done
