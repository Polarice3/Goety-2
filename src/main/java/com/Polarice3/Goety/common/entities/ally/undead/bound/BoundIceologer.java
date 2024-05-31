package com.Polarice3.Goety.common.entities.ally.undead.bound;

import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.spells.frost.IceChunkSpell;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BoundIceologer extends AbstractBoundIllager{
    public AnimationState chunkAnimationState = new AnimationState();

    public BoundIceologer(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new IceologerCastingSpellGoal());
        this.goalSelector.addGoal(2, new AvoidTargetGoal<>(this, LivingEntity.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(4, new ChunkSpellGoal());
        this.goalSelector.addGoal(8, new WanderGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FLYING_SPEED, 0.15D)
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.BoundIceologerFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.BoundIceologerArmor.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.BoundIceologerHealth.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.BoundIceologerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.BoundIceologerArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.BoundIceologerFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public void readAdditionalSaveData(CompoundTag p_32642_) {
        super.readAdditionalSaveData(p_32642_);
    }

    public void addAdditionalSaveData(CompoundTag p_32646_) {
        super.addAdditionalSaveData(p_32646_);
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.ICEOLOGER_AMBIENT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.ICEOLOGER_DEATH.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_32654_) {
        return ModSounds.ICEOLOGER_HURT.get();
    }

    @Override
    public float getVoicePitch() {
        return 0.45F;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return null;
    }

    public AbstractBoundIllager.BoundArmPose getArmPose() {
        return BoundArmPose.NEUTRAL;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide){
            for(int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.CLOUD, this.getRandomX(0.5D), this.getY() + 0.5D, this.getRandomZ(0.5D), (0.5D - this.random.nextDouble()) * 0.15D, 0.01F, (0.5D - this.random.nextDouble()) * 0.15D);
            }
            if (this.isCastingSpell()){
                this.chunkAnimationState.startIfStopped(this.tickCount);
            } else {
                this.chunkAnimationState.stop();
            }
        }
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0){
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    class IceologerCastingSpellGoal extends BoundCastingSpellGoal {
        public void tick() {
            if (BoundIceologer.this.getTarget() != null) {
                BoundIceologer.this.getLookControl().setLookAt(BoundIceologer.this.getTarget(), (float)BoundIceologer.this.getMaxHeadYRot(), (float)BoundIceologer.this.getMaxHeadXRot());
            }

        }
    }

    class ChunkSpellGoal extends BoundUseSpellGoal {

        public void start() {
            super.start();
            BoundIceologer.this.playSound(ModSounds.ICEOLOGER_ATTACK.get(), 1.0F, BoundIceologer.this.getVoicePitch());
            if (BoundIceologer.this.getTarget() != null){
                new IceChunkSpell().SpellResult(BoundIceologer.this, new ItemStack(ModItems.FROST_STAFF.get()));
            }
        }

        @Override
        public void tick() {
            super.tick();
        }

        @Override
        protected void performSpellCasting() {
        }

        @Override
        protected int getCastWarmupTime() {
            return 50;
        }

        @Override
        protected int getCastingTime() {
            return 50;
        }

        @Override
        protected int getCastingInterval() {
            return 120;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return null;
        }

        @Override
        protected BoundSpell getSpell() {
            return BoundSpell.CLOUDLESS;
        }
    }
}
