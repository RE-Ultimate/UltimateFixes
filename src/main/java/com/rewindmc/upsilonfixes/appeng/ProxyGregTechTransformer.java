package com.rewindmc.upsilonfixes.appeng;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("appeng.proxy.ProxyGregTech")
@ConfigOptions("fixAppengQChest")
public class ProxyGregTechTransformer extends UpsilonMiniTransformer {

    @Patch.Method("isQuantumChest(Lnet/minecraft/tileentity/TileEntity;)Z")
    public void patchIsQuantumChest(PatchContext ctx) {
        ctx.jumpToStart();
        ctx.add(
                ALOAD(1),
                INSTANCEOF("gregtechmod/common/tileentities/GT_TileEntity_Quantumchest"),
                IRETURN()
        );
    }
}
