#!/bin/bash
classes=(
	factorization.api.recipe.GenericRecipeManager
	binnie.craftgui.controls.page.ControlPages
	com.eloraam.redpower.core.RedPowerLib
	com.eloraam.redpower.world.WorldGenVolcano
	com.eloraam.redpower.world.WorldGenMarble
)
for e in "${classes[@]}"; do
	sn=${e##*.}
	sed "s/CLASS.NAME/$e/g;s/CLASS.SIMPLE_NAME/$sn/g" tmpl.txt > "${sn}Transformer.java"
done
