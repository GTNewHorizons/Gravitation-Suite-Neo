package com.gtnewhorizons.gravisuiteneo.common;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import gravisuite.GraviSuite;
import gravisuite.ItemRelocator;
import gravisuite.ItemRelocator.TeleportPoint;
import gravisuite.network.IPacket;
import gravisuite.network.PacketHandler;

public class PacketSortingPoints extends IPacket {

    public byte dst;
    public String pointName;

    @Override
    public void readData(DataInputStream data) throws IOException {
        this.dst = data.readByte();
        this.pointName = data.readUTF();
        this.packetID = 4;
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException {}

    public static void issue(EntityPlayer player, byte dst, String pointName) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(buffer);
            os.writeByte(4);
            os.writeByte(dst);
            os.writeUTF(pointName);
            os.close();
            PacketHandler.sendPacket(buffer.toByteArray());
        } catch (IOException var5) {
            throw new RuntimeException(var5);
        }
    }

    public static void movePoint(EntityPlayer player, byte dst, String pointName) {
        if (player != null) {
            ItemStack stack = player.inventory.getCurrentItem();

            if (stack != null && stack.getItem() == GraviSuite.relocator) {
                List<TeleportPoint> tpList = new ArrayList<>(ItemRelocator.loadTeleportPoints(stack));
                int src = 0;

                for (int i = 0; i < tpList.size(); ++i) {
                    if (tpList.get(i).pointName.equalsIgnoreCase(pointName)) {
                        src = i;
                        break;
                    }
                }

                tpList.add(dst, tpList.remove(src));
                ItemRelocator.saveTeleportPoints(stack, tpList);
            }
        }
    }

    @Override
    public void execute(EntityPlayer player) {
        movePoint(player, this.dst, this.pointName);
    }
}
