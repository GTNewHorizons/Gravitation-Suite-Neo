package com.gtnewhorizons.gravisuiteneo.mixins;

import com.gtnewhorizons.gravisuiteneo.common.PacketQuantumShield;
import gravisuite.network.PacketHandler;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PacketHandler.class)
public class MixinPacketHandler {

    @Inject(
            at = @At(remap = false, target = "Ljava/io/DataInputStream;readByte()B", value = "INVOKE_ASSIGN"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            method = "onPacketData",
            remap = false)
    private void gravisuiteneo$handleQuantumShieldPacket(
            InputStream is, EntityPlayer player, CallbackInfo ci, DataInputStream data, int packetId)
            throws IOException {
        if (packetId == 4) {
            PacketQuantumShield packetQShield = new PacketQuantumShield();
            packetQShield.readData(data);
            packetQShield.execute(player);
            ci.cancel();
        }
    }
}
