package com.Polarice3.Goety.common.entities.hostile.illagers;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.MinisterTooth;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class Minister extends HuntingIllagerEntity{
    private final ModServerBossInfo bossInfo = new ModServerBossInfo(this.getUUID(), this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(false).setCreateWorldFog(false);

    public Minister(EntityType<? extends HuntingIllagerEntity> p_i48551_1_, Level p_i48551_2_) {
        super(p_i48551_1_, p_i48551_2_);
        if (this.level.isClientSide){
            Goety.PROXY.addBoss(this);
        }
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, 128.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.EnviokerDamage.get());
    }

    public AttributeSupplier.Builder getConfiguredAttributes(){
        return setCustomAttributes();
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
        this.bossInfo.setId(this.getUUID());
    }

    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setVisible(this.getTarget() != null);
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        if (MainConfig.SpecialBossBar.get()) {
            this.bossInfo.addPlayer(pPlayer);
        }
    }

    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossInfo.removePlayer(pPlayer);
    }

    @Override
    public void remove(Entity.RemovalReason p_146834_) {
        if (this.level.isClientSide) {
            Goety.PROXY.removeBoss(this);
        }
        super.remove(p_146834_);
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return null;
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_) {

    }

    @Override
    public SoundEvent getCelebrateSound() {
        return null;
    }

    class CastingSpellGoal extends SpellcasterCastingSpellGoal {
        private CastingSpellGoal() {
        }

        public void tick() {
            if (Minister.this.getTarget() != null) {
                Minister.this.getLookControl().setLookAt(Minister.this.getTarget(), (float)Minister.this.getMaxHeadYRot(), (float)Minister.this.getMaxHeadXRot());
            }

        }
    }

    class TeethSpellGoal extends SpellcasterUseSpellGoal {

        private TeethSpellGoal() {
        }

        protected int getCastingTime() {
            return 40;
        }

        protected int getCastingInterval() {
            return 100;
        }

        protected void performSpellCasting() {
            if (Minister.this.getTarget() != null) {
                BlockPos blockPos = Minister.this.getTarget().blockPosition();
                for (int length = 0; length < 12; length++) {
                    blockPos = blockPos.offset(-4 + Minister.this.getRandom().nextInt(8), 0, -4 + Minister.this.getRandom().nextInt(8));
                    BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());

                    while(blockpos$mutable.getY() < blockPos.getY() + 8.0D && !Minister.this.level.getBlockState(blockpos$mutable).getMaterial().blocksMotion()) {
                        blockpos$mutable.move(Direction.UP);
                    }

                    if (Minister.this.level.noCollision(new AABB(blockpos$mutable).inflate(0.5D))){
                        MinisterTooth ministerTooth = new MinisterTooth(ModEntityType.MINISTER_TOOTH.get(), Minister.this.level);
                        ministerTooth.setPos(Vec3.atCenterOf(blockpos$mutable));
                        ministerTooth.setOwner(Minister.this);
                        Minister.this.level.addFreshEntity(ministerTooth);
                    }
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        protected IllagerSpell getSpell() {
            return IllagerSpell.FANGS;
        }
    }


}
