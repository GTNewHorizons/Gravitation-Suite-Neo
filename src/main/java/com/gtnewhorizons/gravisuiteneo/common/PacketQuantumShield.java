package com.gtnewhorizons.gravisuiteneo.common;

import com.gtnewhorizons.gravisuiteneo.client.FXQuantumShield;
import cpw.mods.fml.client.FMLClientHandler;
import gravisuite.network.IPacket;
import gravisuite.network.PacketHandler;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class PacketQuantumShield extends IPacket {

    private int source;
    private int target;

    public PacketQuantumShield() {
        this.packetID = 3;
    }

    @Override
    public void readData(DataInputStream data) throws IOException {
        this.source = data.readInt();
        this.target = data.readInt();
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException {
        data.writeInt(this.source);
        data.writeInt(this.target);
    }

    public static void issue(int source, int target) {
        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final DataOutputStream os = new DataOutputStream(buffer);
            os.writeByte(3);
            os.writeInt(source);
            os.writeInt(target);
            os.close();
            PacketHandler.sendPacket(buffer.toByteArray());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute(EntityPlayer player) {
        Minecraft tMC = FMLClientHandler.instance().getClient();

        Entity p = tMC.theWorld.getEntityByID(this.source);
        if (p == null) {
            return;
        }

        float pitch;
        float yaw;
        if (this.target >= 0) {
            Entity t = tMC.theWorld.getEntityByID(this.target);
            if (t != null) {
                double d0 = p.posX - t.posX;
                double d1 = (p.boundingBox.minY + p.boundingBox.maxY) / 2.0D
                        - (t.boundingBox.minY + t.boundingBox.maxY) / 2.0D;
                double d2 = p.posZ - t.posZ;
                double d3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
                float f = (float) (Math.atan2(d2, d0) * 180.0D / 3.141592653589793D) - 90.0F;
                pitch = (float) -(Math.atan2(d1, d3) * 180.0D / 3.141592653589793D);
                yaw = f;
            } else {
                pitch = 90.0F;
                yaw = 0.0F;
            }
            FXQuantumShield qs = new FXQuantumShield(
                    tMC.theWorld, p.posX, p.posY, p.posZ, p, 8, yaw, pitch, FXQuantumShield.EShieldMode.IMPACT);

            FMLClientHandler.instance().getClient().effectRenderer.addEffect(qs);
        } else if (this.target == -1) {
            FXQuantumShield qs = new FXQuantumShield(
                    tMC.theWorld, p.posX, p.posY, p.posZ, p, 8, 0.0F, 90.0F, FXQuantumShield.EShieldMode.IMPACT);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(qs);
            qs = new FXQuantumShield(
                    tMC.theWorld, p.posX, p.posY, p.posZ, p, 8, 0.0F, 270.0F, FXQuantumShield.EShieldMode.IMPACT);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(qs);
        } else if (this.target == -2) {
            FXQuantumShield qs = new FXQuantumShield(
                    tMC.theWorld, p.posX, p.posY, p.posZ, p, 8, 0.0F, 270.0F, FXQuantumShield.EShieldMode.IMPACT);

            FMLClientHandler.instance().getClient().effectRenderer.addEffect(qs);
        } else if (this.target == -3) {
            FXQuantumShield qs = new FXQuantumShield(
                    tMC.theWorld, p.posX, p.posY, p.posZ, p, 8, 0.0F, 90.0F, FXQuantumShield.EShieldMode.IMPACT);

            FMLClientHandler.instance().getClient().effectRenderer.addEffect(qs);
        } else if (this.target == -4) {
            FXQuantumShield qs = new FXQuantumShield(
                    tMC.theWorld, p.posX, p.posY, p.posZ, p, 8, 0.0F, 90.0F, FXQuantumShield.EShieldMode.POWER_UP);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(qs);
            qs = new FXQuantumShield(
                    tMC.theWorld, p.posX, p.posY, p.posZ, p, 8, 0.0F, 270.0F, FXQuantumShield.EShieldMode.POWER_UP);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(qs);
        } else if (this.target == -5) {
            FXQuantumShield qs = new FXQuantumShield(
                    tMC.theWorld, p.posX, p.posY, p.posZ, p, 8, 0.0F, 90.0F, FXQuantumShield.EShieldMode.POWER_DOWN);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(qs);
            qs = new FXQuantumShield(
                    tMC.theWorld, p.posX, p.posY, p.posZ, p, 8, 0.0F, 270.0F, FXQuantumShield.EShieldMode.POWER_DOWN);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(qs);
        }
    }
}
