package com.gtnewhorizons.gravisuiteneo.mixins;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.gtnewhorizons.gravisuiteneo.common.PacketQuantumShield;
import com.gtnewhorizons.gravisuiteneo.common.PacketSortingPoints;

import gravisuite.network.PacketHandler;

@Mixin(PacketHandler.class)
public class MixinPacketHandler {

    @Inject(
            at = @At(remap = false, target = "Ljava/io/DataInputStream;readByte()B", value = "INVOKE_ASSIGN"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            method = "onPacketData",
            remap = false)
    private void gravisuiteneo$handleQuantumShieldPacket(InputStream is, EntityPlayer player, CallbackInfo ci,
            DataInputStream data, int packetId) throws IOException {
        if (packetId == 3) {
            PacketQuantumShield packetQShield = new PacketQuantumShield();
            packetQShield.readData(data);
            packetQShield.execute(player);
            ci.cancel();
        } else if (packetId == 4) {
            PacketSortingPoints packetSorting = new PacketSortingPoints();
            packetSorting.readData(data);
            packetSorting.execute(player);
            ci.cancel();
        }
    }
}
