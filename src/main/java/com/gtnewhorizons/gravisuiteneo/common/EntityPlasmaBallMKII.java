package com.gtnewhorizons.gravisuiteneo.common;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeo;
import com.gtnewhorizons.gravisuiteneo.mixins.MixinEntityPlasmaBall;

import gravisuite.BlockRelocatorPortal;
import gravisuite.EntityPlasmaBall;
import gravisuite.GraviSuite;
import gravisuite.Helpers;
import gravisuite.ItemRelocator;
import gravisuite.ItemRelocator.TeleportPoint;
import gravisuite.ServerProxy;
import gravisuite.TileEntityRelocatorPortal;
import ic2.api.item.ElectricItem;

public class EntityPlasmaBallMKII extends EntityPlasmaBall {

    public static final float PLASMA_DAMAGE = 100.0F;

    private float charge = 0.0f;
    private int finalTicksForDestruction = -1;

    public EntityPlasmaBallMKII(World world, EntityPlasmaBall origin, float charge) {
        this(world, ((MixinEntityPlasmaBall) origin).getOwnerEntity(), null, (byte) 3);
        this.charge = charge;
        this.finalTicksForDestruction = this.ticksExisted + 10;
        ((MixinEntityPlasmaBall) this).setMaxRange(512.0);
        ((MixinEntityPlasmaBall) this).setSpeedPerTick(1.0);
        ((MixinEntityPlasmaBall) this).setActionType((byte) 3);
        this.setVelocity(0.0D, 0.0D, 0.0D);
        this.posX = origin.posX;
        this.posY = origin.posY;
        this.posZ = origin.posZ;
    }

    /**
     * Spawn a plasmaBall that is used as weapon
     */
    public EntityPlasmaBallMKII(World world, EntityLivingBase entityLiving, float charge, byte entityType) {
        this(world, entityLiving, null, entityType);
        this.charge = charge;
        this.dataWatcher.updateObject(29, this.charge);
        ((MixinEntityPlasmaBall) this).setMaxRange(512.0);
        ((MixinEntityPlasmaBall) this).setSpeedPerTick(1.33);
        if (entityType == 2) {
            this.changeEntitySpeed(3.0F);
        }
    }

    private void changeEntitySpeed(float newSpeed) {
        // Get the current length/speed of the vector
        double magnitude = Math
                .sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);

        // Get the unit vector of the motion vector (length/speed of 1)
        this.motionX /= magnitude;
        this.motionY /= magnitude;
        this.motionZ /= magnitude;

        // Set the length/speed of our unit vector
        this.motionX *= newSpeed;
        this.motionY *= newSpeed;
        this.motionZ *= newSpeed;
        ((MixinEntityPlasmaBall) this).setSpeedPerTick(((MixinEntityPlasmaBall) this).getSpeedPerTick() * newSpeed);
    }

    public EntityPlasmaBallMKII(World world, EntityLivingBase entityLiving, TeleportPoint tpPoint, byte entityType) {
        super(world, entityLiving, tpPoint, entityType);
    }

    public EntityPlasmaBallMKII(World world) {
        super(world);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(29, this.charge);
        this.dataWatcher.setObjectWatched(29);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.worldObj.isRemote && this.finalTicksForDestruction > -1
                && this.ticksExisted >= this.finalTicksForDestruction) {
            this.setDead();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setFloat("charge", this.charge);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.charge = nbt.getFloat("charge");
    }

    public float getCharge() {
        return this.dataWatcher.getWatchableObjectFloat(29);
    }

    public static double getImpactRadius(float plasmaEfficiency) {
        // Nominal radius for 100% plasma is 6; That's a 12x12x12 cube
        return 6.0 * plasmaEfficiency;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onImpact(MovingObjectPosition mop) {
        if (this.finalTicksForDestruction != -1) {
            return;
        }
        byte actionType = ((MixinEntityPlasmaBall) this).getActionType();

        if (actionType == 2) // PlazmaLauncher ball
        {
            // Cap the radius at 10, that's 30x30x30 and pretty .. yeah.. multikill :P
            // Also limit to 1. That would be a small area of 2x2x2
            double tImpactRadius = MathHelper.clamp_double(getImpactRadius(this.charge), 1.0, 15.0);
            boolean tTerrainDamage = this.charge >= 2;
            List<EntityLivingBase> e;
            EntityPlayer ownerEntity = (EntityPlayer) ((MixinEntityPlasmaBall) this).getOwnerEntity();
            if (mop.entityHit != null) {
                e = this.worldObj.getEntitiesWithinAABB(
                        EntityLivingBase.class,
                        AxisAlignedBB.getBoundingBox(
                                mop.entityHit.posX - tImpactRadius,
                                mop.entityHit.posY - tImpactRadius,
                                mop.entityHit.posZ - tImpactRadius,
                                mop.entityHit.posX + tImpactRadius,
                                mop.entityHit.posY + tImpactRadius,
                                mop.entityHit.posZ + tImpactRadius));
            } else {
                e = this.worldObj.getEntitiesWithinAABB(
                        EntityLivingBase.class,
                        AxisAlignedBB.getBoundingBox(
                                mop.blockX - tImpactRadius,
                                mop.blockY - tImpactRadius,
                                mop.blockZ - tImpactRadius,
                                mop.blockX + tImpactRadius,
                                mop.blockY + tImpactRadius,
                                mop.blockZ + tImpactRadius));
            }

            float tFinalDamage = this.charge * PLASMA_DAMAGE;

            if (tTerrainDamage) {
                this.worldObj.createExplosion(null, mop.blockX, mop.blockY, mop.blockZ, this.charge, true);
            }

            long tDamageDealt = 0;
            long tEntitiesHitDeadly = 0;
            for (EntityLivingBase el : e) {
                if (el.getHealth() < tFinalDamage) {
                    tEntitiesHitDeadly++;
                }

                el.attackEntityFrom(DamageSources.causePlayerPlazmaDamage(ownerEntity), tFinalDamage);
                tDamageDealt += tFinalDamage;
            }
            if (tDamageDealt > 9000) {
                ownerEntity.triggerAchievement(Achievements.OVER9000);
            }
            if (tEntitiesHitDeadly > 100) {
                ownerEntity.triggerAchievement(Achievements.ULTRAKILL);
            }

            final EntityPlasmaBall explosionBall = new EntityPlasmaBallMKII(this.worldObj, this, this.charge);
            if (!this.worldObj.isRemote) {
                this.worldObj.playSoundEffect(
                        mop.blockX,
                        mop.blockY,
                        mop.blockZ,
                        GraviSuiteNeo.MODID + ":plasmaImpact",
                        1.25F,
                        1.0F);
                this.worldObj.spawnEntityInWorld(explosionBall);
                // setVelocity(0.0D, 0.0D, 0.0D);
                // actionType = 3;
                this.setDead();
                // dataWatcher.updateObject(30, actionType); // Don't send the update; This causes weird issues when
                // more than one plasmaball is flying
                // _mFinalTicksForDestruction = ticksExisted + 10;
            }
            return;
        }

        if (mop.entityHit != null) {
            if (actionType == 0) {
                TeleportPoint targetTpPoint = ((MixinEntityPlasmaBall) this).getTargetTpPoint();
                if (mop.entityHit instanceof EntityPlayer player) {
                    final ItemStack itemstack = player.inventory.armorInventory[2];
                    if (itemstack != null && itemstack.getItem() == GraviSuite.graviChestPlate) {
                        double dischargeArmorValue = ((MixinEntityPlasmaBall) this).getDischargeArmorValue();
                        if (ElectricItem.manager.getCharge(itemstack) < dischargeArmorValue) {
                            Helpers.teleportEntity(mop.entityHit, targetTpPoint);
                        } else if (GraviSuite.isSimulating()) {
                            ServerProxy.sendPlayerMessage(
                                    player,
                                    ((MixinEntityPlasmaBall) this).getOwnerEntity().getCommandSenderName() + " "
                                            + StatCollector.translateToLocal("message.relocator.text.messageToTarget"));
                            ElectricItem.manager
                                    .discharge(itemstack, dischargeArmorValue, Integer.MAX_VALUE, true, false, false);
                        }
                    } else {
                        Helpers.teleportEntity(mop.entityHit, targetTpPoint);
                    }
                } else {
                    Helpers.teleportEntity(mop.entityHit, targetTpPoint);
                }
            }
        } else if (actionType == 1) {
            int curPosX = mop.blockX;
            int curPosY = mop.blockY;
            int curPosZ = mop.blockZ;
            switch (mop.sideHit) {
                case 0:
                    curPosY -= 1;
                    break;
                case 1:
                    curPosY += 1;
                    break;
                case 2:
                    curPosZ -= 1;
                    break;
                case 3:
                    curPosZ += 1;
                    break;
                case 4:
                    curPosX -= 1;
                    break;
                case 5:
                    curPosX += 1;
            }
            if (GraviSuite.isSimulating()) {
                try {
                    this.worldObj.setBlockToAir(curPosX, curPosY, curPosZ);
                    this.worldObj.setBlock(curPosX, curPosY, curPosZ, GraviSuite.blockRelocatorPortal);
                    this.worldObj.markBlockForUpdate(curPosX, curPosY, curPosZ);

                    final MinecraftServer minecraftserver = MinecraftServer.getServer();
                    TeleportPoint targetTpPoint = ((MixinEntityPlasmaBall) this).getTargetTpPoint();
                    final WorldServer targetServer = minecraftserver.worldServerForDimension(targetTpPoint.dimID);

                    targetServer.theChunkProviderServer
                            .loadChunk((int) targetTpPoint.x >> 4, (int) targetTpPoint.z >> 4);

                    final Block block = targetServer
                            .getBlock((int) targetTpPoint.x, (int) targetTpPoint.y, (int) targetTpPoint.z);

                    if (!(block instanceof BlockRelocatorPortal)) {
                        targetServer.setBlock(
                                (int) targetTpPoint.x,
                                (int) targetTpPoint.y,
                                (int) targetTpPoint.z,
                                GraviSuite.blockRelocatorPortal);
                        targetServer.markBlockForUpdate(
                                (int) targetTpPoint.x,
                                (int) targetTpPoint.y,
                                (int) targetTpPoint.z);
                    }
                    final TileEntity tileEntity = targetServer
                            .getTileEntity((int) targetTpPoint.x, (int) targetTpPoint.y, (int) targetTpPoint.z);

                    if (tileEntity instanceof TileEntityRelocatorPortal portal) {
                        final ItemRelocator.TeleportPoint tmpPoint = new ItemRelocator.TeleportPoint();
                        tmpPoint.dimID = this.worldObj.provider.dimensionId;
                        tmpPoint.x = curPosX;
                        tmpPoint.y = curPosY;
                        tmpPoint.z = curPosZ;
                        portal.setParentPortal(tmpPoint);
                    }
                    final TileEntity currentTileEntity = this.worldObj.getTileEntity(curPosX, curPosY, curPosZ);
                    if (currentTileEntity instanceof TileEntityRelocatorPortal currentPortal) {
                        currentPortal.setParentPortal(targetTpPoint);
                    }
                } catch (final Exception ignored) {}
            }
        }
        if (!this.worldObj.isRemote) {
            this.setDead();
        }
    }
}
