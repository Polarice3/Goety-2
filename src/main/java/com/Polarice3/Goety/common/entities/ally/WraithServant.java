package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.common.entities.neutral.AbstractWraith;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WraithServant extends AbstractWraith {
    private static final EntityDataAccessor<Boolean> DATA_INTERESTED_ID = SynchedEntityData.defineId(WraithServant.class, EntityDataSerializers.BOOLEAN);
    private float interestTime;

    public WraithServant(EntityType<? extends Summoned> p_i48553_1_, Level p_i48553_2_) {
        super(p_i48553_1_, p_i48553_2_);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new WanderGoal(this, 1.0D, 10));
    }

    public void tick() {
        if (this.isAlive()) {
            if (this.isInterested()) {
                --this.interestTime;
            }
            if (this.interestTime <= 0){
                this.setIsInterested(false);
            }
        }
        super.tick();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_INTERESTED_ID, false);
    }

    public void setIsInterested(boolean pBeg) {
        this.entityData.set(DATA_INTERESTED_ID, pBeg);
    }

    public boolean isInterested() {
        return this.entityData.get(DATA_INTERESTED_ID);
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (!this.isInterested()) {
                InteractionResult actionresulttype = super.mobInteract(pPlayer, pHand);
                if (!actionresulttype.consumesAction() && (itemstack.isEmpty() || itemstack == ItemStack.EMPTY)) {
                    this.setIsInterested(true);
                    this.interestTime = 40;
                    this.level.broadcastEntityEvent(this, (byte) 102);
                    this.playSound(ModSounds.WRAITH_AMBIENT.get(), 1.0F, 2.0F);
                    this.heal(1.0F);
                    return InteractionResult.SUCCESS;
                }
                return actionresulttype;
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public void handleEntityEvent(byte pId) {
        super.handleEntityEvent(pId);
        if (pId == 102){
            this.setIsInterested(true);
            this.interestTime = 40;
            this.playSound(ModSounds.WRAITH_AMBIENT.get(), 1.0F, 2.0F);
            this.addParticlesAroundSelf(ParticleTypes.HEART);
        }
    }

    protected void addParticlesAroundSelf(ParticleOptions pParticleData) {
        for(int i = 0; i < 5; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(pParticleData, this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
        }

    }

}
