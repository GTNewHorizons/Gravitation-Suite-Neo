package com.gtnewhorizons.gravisuiteneo.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;

public final class ChatUtil {

    private ChatUtil() {}

    /**
     * Send a chat message to the player from the logical server only, restoring GraviSuite's original
     * ServerProxy.sendPlayerMessage behavior. Item and armor code such as onArmorTick and onItemRightClick runs on both
     * sides, so emitting the message directly would duplicate it and, while the client-side energy value is desynced,
     * print false low-power warnings. The component is still localized on the receiving client.
     */
    public static void sendToPlayer(EntityPlayer player, IChatComponent message) {
        if (!player.worldObj.isRemote) {
            player.addChatMessage(message);
        }
    }
}
