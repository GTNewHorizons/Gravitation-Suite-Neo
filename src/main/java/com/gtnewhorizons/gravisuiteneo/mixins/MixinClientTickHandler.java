package com.gtnewhorizons.gravisuiteneo.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import gravisuite.GraviSuite;
import gravisuite.ItemAdvancedJetPack;
import gravisuite.ItemAdvancedLappack;
import gravisuite.ItemGraviChestPlate;
import gravisuite.ItemUltimateLappack;
import gravisuite.client.ClientTickHandler;
import ic2.api.item.IElectricItem;

@Mixin(ClientTickHandler.class)
public class MixinClientTickHandler {

    @Unique
    private static final String KEY_ENERGY_LEVEL = "message.hud.energyLevel";
    @Unique
    private static final String KEY_LEVITATION_MODE = "message.graviChestPlate.levitationMode.short";
    @Unique
    private static final String KEY_GRAVITATION_ENGINE = "message.graviChestPlate.gravitationEngine";
    @Unique
    private static final String KEY_HOVER_MODE = "message.advElJetpack.hoverMode";
    @Unique
    private static final String KEY_JETPACK_ENGINE = "message.advElJetpack.jetpackEngine";
    @Unique
    private static final String KEY_ENGINE_ON = "message.hud.engineOn";
    @Unique
    private static final String KEY_ENGINE_ON_WITH_MODE = "message.hud.engineOnWithMode";

    @Unique
    private static final int HUD_OFFSET = 3;
    @Unique
    private static final int WHITE = 0xFFFFFF;

    @Unique
    private static EntityPlayer gravisuiteneo$cachedPlayer;
    @Unique
    private static int gravisuiteneo$cachedTick = -1;
    @Unique
    private static Item gravisuiteneo$cachedItem;
    @Unique
    private static boolean gravisuiteneo$hasStatusText;
    @Unique
    private static String gravisuiteneo$statusText;
    @Unique
    private static String gravisuiteneo$energyText;
    @Unique
    private static int gravisuiteneo$statusFontWidth;
    @Unique
    private static int gravisuiteneo$energyFontWidth;
    @Unique
    private static int gravisuiteneo$cachedDisplayWidth = -1;
    @Unique
    private static int gravisuiteneo$cachedDisplayHeight = -1;
    @Unique
    private static int gravisuiteneo$cachedGuiScale = -1;
    @Unique
    private static boolean gravisuiteneo$cachedUnicode;
    @Unique
    private static ScaledResolution gravisuiteneo$scaledResolution;

    /**
     * @author GraviSuiteNeo
     * @reason Getting the IDs of the IC2 hotkeys is useless now
     */
    @Overwrite(remap = false)
    public static void onTickClient() {
        if (ClientTickHandler.mc.theWorld != null) {
            GraviSuite.keyboard.sendKeyUpdate(ClientTickHandler.mc.thePlayer);
        }
    }

    /**
     * @author boubou_19
     * @reason The vanilla HUD renderer rebuilds every string, translates it via MessageFormat and allocates a new
     *         ScaledResolution on every single frame. Cache the rendered HUD lines and only recompute them once per
     *         client tick (or when the item/resolution changes). This reduces per-frame allocation spam drastically
     *         while keeping the exact same visuals.
     */
    @Overwrite(remap = false)
    public static void onTickRedner() {
        Minecraft mc = ClientTickHandler.mc;
        EntityPlayer player = mc.thePlayer;

        if (!GraviSuite.displayHud || mc.theWorld == null
                || !mc.inGameHasFocus
                || mc.gameSettings.showDebugInfo
                || player == null) {
            gravisuiteneo$invalidateCache();
            return;
        }

        ItemStack item = player.inventory.armorItemInSlot(2);
        if (item == null || !gravisuiteneo$isSupportedItem(item)) {
            gravisuiteneo$invalidateCache();
            return;
        }

        if (gravisuiteneo$cachedPlayer != player || gravisuiteneo$cachedItem != item.getItem()
                || gravisuiteneo$cachedTick != player.ticksExisted) {
            gravisuiteneo$rebuildLines(item);
            gravisuiteneo$cachedPlayer = player;
            gravisuiteneo$cachedItem = item.getItem();
            gravisuiteneo$cachedTick = player.ticksExisted;
        }

        ScaledResolution scaled = gravisuiteneo$getScaledResolution(mc);
        if (scaled == null) {
            gravisuiteneo$invalidateCache();
            return;
        }

        int scaledWidth = scaled.getScaledWidth();
        int scaledHeight = scaled.getScaledHeight();

        int xPos = 0;
        int yPos = 0;
        int xPos2 = 0;
        int yPos2 = 0;

        switch (GraviSuite.hudPos) {
            case 1:
                xPos = 2;
                yPos = 2;
                xPos2 = 2;
                yPos2 = HUD_OFFSET + ClientTickHandler.mc.fontRenderer.FONT_HEIGHT;
                break;
            case 2:
                if (gravisuiteneo$hasStatusText) {
                    xPos = scaledWidth - gravisuiteneo$statusFontWidth - 2;
                }
                xPos2 = scaledWidth - gravisuiteneo$energyFontWidth - 2;
                yPos = 2;
                yPos2 = HUD_OFFSET + ClientTickHandler.mc.fontRenderer.FONT_HEIGHT;
                break;
            case 3:
                xPos = 2;
                xPos2 = 2;
                yPos = scaledHeight - 2 - ClientTickHandler.mc.fontRenderer.FONT_HEIGHT;
                yPos2 = yPos - HUD_OFFSET - ClientTickHandler.mc.fontRenderer.FONT_HEIGHT;
                break;
            case 4:
                if (gravisuiteneo$hasStatusText) {
                    xPos = scaledWidth - gravisuiteneo$statusFontWidth - 2;
                }
                xPos2 = scaledWidth - gravisuiteneo$energyFontWidth - 2;
                yPos = scaledHeight - 2 - ClientTickHandler.mc.fontRenderer.FONT_HEIGHT;
                yPos2 = yPos - HUD_OFFSET - ClientTickHandler.mc.fontRenderer.FONT_HEIGHT;
                break;
            default:
                break;
        }

        if (gravisuiteneo$hasStatusText) {
            mc.ingameGUI.drawString(mc.fontRenderer, gravisuiteneo$statusText, xPos, yPos, WHITE);
            mc.ingameGUI.drawString(mc.fontRenderer, gravisuiteneo$energyText, xPos2, yPos2, WHITE);
        } else {
            mc.ingameGUI.drawString(mc.fontRenderer, gravisuiteneo$energyText, xPos2, yPos, WHITE);
        }
    }

    @Unique
    private static boolean gravisuiteneo$isSupportedItem(ItemStack item) {
        Item it = item.getItem();
        return it == GraviSuite.graviChestPlate || it == GraviSuite.ultimateLappack
                || it == GraviSuite.advLappack
                || it == GraviSuite.advJetpack
                || it == GraviSuite.advNanoChestPlate;
    }

    @Unique
    private static ScaledResolution gravisuiteneo$getScaledResolution(Minecraft mc) {
        int guiScale = mc.gameSettings.guiScale;
        boolean unicode = mc.func_152349_b();
        if (gravisuiteneo$scaledResolution == null || gravisuiteneo$cachedDisplayWidth != mc.displayWidth
                || gravisuiteneo$cachedDisplayHeight != mc.displayHeight
                || gravisuiteneo$cachedGuiScale != guiScale
                || gravisuiteneo$cachedUnicode != unicode) {
            gravisuiteneo$scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            gravisuiteneo$cachedDisplayWidth = mc.displayWidth;
            gravisuiteneo$cachedDisplayHeight = mc.displayHeight;
            gravisuiteneo$cachedGuiScale = guiScale;
            gravisuiteneo$cachedUnicode = unicode;
        }
        return gravisuiteneo$scaledResolution;
    }

    @Unique
    private static void gravisuiteneo$rebuildLines(ItemStack item) {
        Item it = item.getItem();
        int charge = 0;
        float energy = 0.0F;

        if (it == GraviSuite.graviChestPlate) {
            charge = ItemGraviChestPlate.getCharge(item);
            energy = charge / (float) ((IElectricItem) it).getMaxCharge(item) * 100.0F;
            if (ItemGraviChestPlate.readFlyStatus(item)) {
                gravisuiteneo$setStatusText(
                        KEY_GRAVITATION_ENGINE,
                        KEY_LEVITATION_MODE,
                        ItemGraviChestPlate.readWorkMode(item));
            } else {
                gravisuiteneo$statusText = "";
                gravisuiteneo$hasStatusText = false;
            }
        } else if (it == GraviSuite.ultimateLappack) {
            charge = ItemUltimateLappack.getCharge(item);
            energy = charge / (float) ((IElectricItem) it).getMaxCharge(item) * 100.0F;
            gravisuiteneo$statusText = "";
            gravisuiteneo$hasStatusText = false;
        } else if (it == GraviSuite.advLappack) {
            charge = ItemAdvancedLappack.getCharge(item);
            energy = charge / (float) ((IElectricItem) it).getMaxCharge(item) * 100.0F;
            gravisuiteneo$statusText = "";
            gravisuiteneo$hasStatusText = false;
        } else if (it == GraviSuite.advJetpack || it == GraviSuite.advNanoChestPlate) {
            charge = ItemAdvancedJetPack.getCharge(item);
            energy = charge / (float) ((IElectricItem) it).getMaxCharge(item) * 100.0F;
            if (ItemAdvancedJetPack.readFlyStatus(item)) {
                gravisuiteneo$setStatusText(KEY_JETPACK_ENGINE, KEY_HOVER_MODE, ItemAdvancedJetPack.readWorkMode(item));
            } else {
                gravisuiteneo$statusText = "";
                gravisuiteneo$hasStatusText = false;
            }
        }

        gravisuiteneo$energyText = I18n.format(KEY_ENERGY_LEVEL, getEnergyStatusText(energy));
        gravisuiteneo$energyFontWidth = ClientTickHandler.mc.fontRenderer.getStringWidth(gravisuiteneo$energyText);
    }

    @Unique
    private static void gravisuiteneo$setStatusText(String engineKey, String modeKey, boolean modeEnabled) {
        String engine = I18n.format(engineKey);
        gravisuiteneo$statusText = EnumChatFormatting.GREEN
                + (modeEnabled ? I18n.format(KEY_ENGINE_ON_WITH_MODE, engine, I18n.format(modeKey))
                        : I18n.format(KEY_ENGINE_ON, engine));
        gravisuiteneo$statusFontWidth = ClientTickHandler.mc.fontRenderer.getStringWidth(gravisuiteneo$statusText);
        gravisuiteneo$hasStatusText = true;
    }

    @Unique
    private static String getEnergyStatusText(float energy) {
        int rounded = Math.round(energy);
        if (energy > 5.0F && energy <= 10.0F) {
            return EnumChatFormatting.GOLD + "" + rounded + "%";
        } else if (energy <= 5.0F) {
            return EnumChatFormatting.RED + "" + rounded + "%";
        }
        return "" + rounded + "%";
    }

    @Unique
    private static void gravisuiteneo$invalidateCache() {
        gravisuiteneo$cachedPlayer = null;
        gravisuiteneo$cachedItem = null;
        gravisuiteneo$cachedTick = -1;
        gravisuiteneo$hasStatusText = false;
        gravisuiteneo$statusText = "";
        gravisuiteneo$energyText = "";
        gravisuiteneo$statusFontWidth = 0;
        gravisuiteneo$energyFontWidth = 0;
    }

}
