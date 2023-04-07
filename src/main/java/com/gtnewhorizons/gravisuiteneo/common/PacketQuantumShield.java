package com.gtnewhorizons.gravisuiteneo.common;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeo;

import gravisuite.network.IPacket;
import gravisuite.network.PacketHandler;

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
        GraviSuiteNeo.proxy.createQuantumShieldFX(this.source, this.target);
    }
}
