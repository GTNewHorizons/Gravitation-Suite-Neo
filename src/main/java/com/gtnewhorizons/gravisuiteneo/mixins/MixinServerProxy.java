package com.gtnewhorizons.gravisuiteneo.mixins;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import gravisuite.ServerProxy;

/**
 * Intercepts ServerProxy.sendPlayerMessage to replace hardcoded English strings from GraviSuite with
 * ChatComponentTranslation so translation happens client-side with the correct locale.
 *
 * <p>
 * GraviSuite constructs messages server-side by concatenating StatCollector.translateToLocal() results (e.g. "§a" +
 * trans("message.graviChestPlate.gravitationEngine") + " " + trans("message.text.enabled")), then passes the
 * already-translated English string to sendPlayerMessage. We reverse-map these known English strings back to compound
 * ChatComponentTranslation objects.
 */
@Mixin(ServerProxy.class)
public class MixinServerProxy {

    /**
     * Maps stripped English text (no §-codes) to a pair of [translationKey1, translationKey2]. If key2 is null, only
     * key1 is used (single-key message). If key2 is non-null, the message is: trans(key1) + " " + trans(key2).
     */
    private static final Map<String, String[]> REVERSE_MAP = new HashMap<>();

    static {
        // GraviChestPlate gravity engine toggle
        // "§a" + trans(gravitationEngine) + " " + trans(text.enabled)
        REVERSE_MAP.put(
                "Gravitation engine enabled",
                new String[] { "message.graviChestPlate.gravitationEngine", "message.text.enabled" });
        // "§c" + trans(gravitationEngine) + " " + trans(text.disabled)
        REVERSE_MAP.put(
                "Gravitation engine disabled",
                new String[] { "message.graviChestPlate.gravitationEngine", "message.text.disabled" });

        // GraviChestPlate single-key messages
        REVERSE_MAP.put(
                "Not enough energy to run Gravitation engine !",
                new String[] { "message.graviChestPlate.lowEnergy" });
        REVERSE_MAP.put(
                "Not enough energy to run Gravitation engine",
                new String[] { "message.graviChestPlate.lowEnergy" });
        REVERSE_MAP.put(
                "Warning ! Your's energy cell is depleted ! Gravitation engine off",
                new String[] { "message.graviChestPlate.shutdown" });
        REVERSE_MAP.put("Not enough energy to boost !", new String[] { "message.graviChestPlate.noEnergyToBoost" });
        REVERSE_MAP.put("Not enough energy to boost", new String[] { "message.graviChestPlate.noEnergyToBoost" });

        // AdvancedJetPack hover mode toggle
        // "§e" + trans(hoverMode) + " " + trans(text.enabled/disabled)
        REVERSE_MAP
                .put("Hover mode enabled", new String[] { "message.advElJetpack.hoverMode", "message.text.enabled" });
        REVERSE_MAP
                .put("Hover mode disabled", new String[] { "message.advElJetpack.hoverMode", "message.text.disabled" });

        // AdvancedJetPack engine toggle
        REVERSE_MAP.put(
                "Jetpack engine enabled",
                new String[] { "message.advElJetpack.jetpackEngine", "message.text.enabled" });
        REVERSE_MAP.put(
                "Jetpack engine disabled",
                new String[] { "message.advElJetpack.jetpackEngine", "message.text.disabled" });
    }

    /**
     * @author GraviSuiteNeo
     * @reason Replace hardcoded English strings with client-side translated components
     */
    @Overwrite(remap = false)
    public static void sendPlayerMessage(EntityPlayer player, String message) {
        if (message == null || message.isEmpty()) return;

        String stripped = stripFormattingCodes(message);
        String[] keys = REVERSE_MAP.get(stripped);

        if (keys != null) {
            // Detect the primary color from the first §-code in the message
            EnumChatFormatting color = detectPrimaryColor(message);

            IChatComponent comp;
            if (keys.length == 1) {
                comp = new ChatComponentTranslation(keys[0]);
            } else {
                // Compound: "key1 key2"
                comp = new ChatComponentTranslation(keys[0]).appendText(" ")
                        .appendSibling(new ChatComponentTranslation(keys[1]));
            }
            if (color != null) {
                comp.getChatStyle().setColor(color);
            }
            player.addChatMessage(comp);
        } else {
            player.addChatMessage(new ChatComponentText(message));
        }
    }

    /** Strips §x formatting codes from a string (replaces EnumChatFormatting.getTextWithoutFormattingCodes). */
    private static String stripFormattingCodes(String text) {
        if (text == null) return "";
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '§' && i + 1 < text.length()) {
                i++; // skip the code character
            } else {
                sb.append(text.charAt(i));
            }
        }
        return sb.toString();
    }

    private static EnumChatFormatting detectPrimaryColor(String message) {
        for (int i = 0; i < message.length() - 1; i++) {
            if (message.charAt(i) == '§') {
                char code = message.charAt(i + 1);
                for (EnumChatFormatting fmt : EnumChatFormatting.values()) {
                    if (fmt.isColor() && fmt.toString().charAt(1) == code) {
                        return fmt;
                    }
                }
            }
        }
        return null;
    }
}
