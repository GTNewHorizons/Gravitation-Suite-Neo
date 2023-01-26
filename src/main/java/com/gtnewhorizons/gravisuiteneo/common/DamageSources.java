package com.gtnewhorizons.gravisuiteneo.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;

public class DamageSources {

    private DamageSources() {}

    public static final DamageSource PLASMA = new DamageSource("plazma").setDamageBypassesArmor();

    public static DamageSource causePlayerPlazmaDamage(EntityPlayer player) {
        return new EntityDamageSourcePlazma(player);
    }

    public static class EntityDamageSourcePlazma extends EntityDamageSource {

        public EntityDamageSourcePlazma(Entity damageSourceEntity) {
            super("player", damageSourceEntity);
            this.setDamageBypassesArmor();
        }

        @Override
        public IChatComponent func_151519_b(EntityLivingBase target) {
            Entity source = this.getEntity();
            IChatComponent customItemName = null;
            if (source instanceof EntityLivingBase) {
                ItemStack heldItem = ((EntityLivingBase) source).getHeldItem();
                if (heldItem != null && heldItem.hasDisplayName()) {
                    customItemName = heldItem.func_151000_E();
                }
            }

            if (target == source) {
                if (target instanceof EntityPlayer) {
                    ((EntityPlayer) target).triggerAchievement(Achievements.VAPORIZE_SELF);
                }

                if (customItemName == null) {
                    return new ChatComponentTranslation("death.attack.plazma_player.self", target.func_145748_c_());
                }
                return new ChatComponentTranslation(
                        "death.attack.plazma.self.item", target.func_145748_c_(), customItemName);
            }

            if (customItemName == null) {
                return new ChatComponentTranslation(
                        "death.attack.plazma", target.func_145748_c_(), source.func_145748_c_());
            }
            return new ChatComponentTranslation(
                    "death.attack.plazma.item", target.func_145748_c_(), source.func_145748_c_(), customItemName);
        }
    }
}
