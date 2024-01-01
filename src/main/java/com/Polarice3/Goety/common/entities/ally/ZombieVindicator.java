package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.NeutralZombieAttackGoal;
import com.Polarice3.Goety.common.entities.neutral.IRavager;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Objects;

public class ZombieVindicator extends ZombieServant{
    public ZombieVindicator(EntityType<? extends ZombieServant> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new ModMeleeAttackGoal(this));
        this.goalSelector.addGoal(8, new WanderGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public void targetSelectGoal(){
        super.targetSelectGoal();
        this.targetSelector.addGoal(1, new NaturalAttackGoal<>(this, AbstractVillager.class));
        this.targetSelector.addGoal(1, new NaturalAttackGoal<>(this, IronGolem.class));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ZombieVindicatorHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, (double)0.23F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ZombieVindicatorDamage.get())
                .add(Attributes.ARMOR, 2.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.ZombieVindicatorHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.ZombieVindicatorDamage.get());
    }

    public boolean isUnderWaterConverting() {
        return false;
    }

    public boolean isBaby() {
        return false;
    }

    public void setBaby(boolean pChildZombie) {
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ZOMBIE_VILLAGER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_VILLAGER_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_VILLAGER_STEP;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    protected boolean convertsInWater() {
        return false;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34088_, DifficultyInstance p_34089_, MobSpawnType p_34090_, @Nullable SpawnGroupData p_34091_, @Nullable CompoundTag p_34092_) {
        SpawnGroupData spawngroupdata = super.finalizeSpawn(p_34088_, p_34089_, p_34090_, p_34091_, p_34092_);
        if (this.isNatural()){
            this.setHostile(true);
        }
        return spawngroupdata;
    }

    protected void handleAttributes(float difficulty) {
        Objects.requireNonNull(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE)).addPermanentModifier(new AttributeModifier("random spawn bonus", this.random.nextDouble() * (double)0.05F, AttributeModifier.Operation.ADDITION));
        double d0 = this.random.nextDouble() * 1.5D * (double)difficulty;
        if (d0 > 1.0D) {
            Objects.requireNonNull(this.getAttribute(Attributes.FOLLOW_RANGE)).addPermanentModifier(new AttributeModifier("random zombie-spawn bonus", d0, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }

    }

    protected void populateDefaultEquipmentSlots(RandomSource p_219149_, DifficultyInstance p_219150_) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
    }

    public boolean killedEntity(ServerLevel world, LivingEntity killedEntity) {
        boolean flag = super.killedEntity(world, killedEntity);
        float random = this.level.random.nextFloat();
        if (this.isUpgraded()){
            if (killedEntity instanceof Vindicator vindicator){
                if (random <= 0.5F && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(killedEntity, ModEntityType.ZOMBIE_VINDICATOR.get(), (timer) -> {})) {
                    EntityType<? extends Mob> entityType = ModEntityType.ZOMBIE_VINDICATOR.get();
                    ZombieVindicator zombieVindicator = (ZombieVindicator) vindicator.convertTo(entityType, false);
                    if (zombieVindicator != null) {
                        zombieVindicator.finalizeSpawn(world, level.getCurrentDifficultyAt(zombieVindicator.blockPosition()), MobSpawnType.CONVERSION, null, null);
                        zombieVindicator.setLimitedLife(10 * (15 + this.level.random.nextInt(45)));
                        if (this.getTrueOwner() != null){
                            zombieVindicator.setTrueOwner(this.getTrueOwner());
                        }
                        net.minecraftforge.event.ForgeEventFactory.onLivingConvert(killedEntity, zombieVindicator);
                        if (!this.isSilent()) {
                            world.levelEvent((Player) null, 1026, this.blockPosition(), 0);
                        }
                    }
                }
            }
        }
        return flag;
    }

    static class ModMeleeAttackGoal extends NeutralZombieAttackGoal {
        public ModMeleeAttackGoal(ZombieVindicator p_34123_) {
            super(p_34123_, 1.0D, false);
        }

        protected double getAttackReachSqr(LivingEntity p_34125_) {
            if (this.mob.getVehicle() instanceof IRavager) {
                float f = this.mob.getVehicle().getBbWidth() - 0.1F;
                return (double)(f * 2.0F * f * 2.0F + p_34125_.getBbWidth());
            } else {
                return super.getAttackReachSqr(p_34125_);
            }
        }
    }

    public static class NaturalAttackGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        protected ZombieVindicator zombieVindicator;

        public NaturalAttackGoal(ZombieVindicator zombieVindicator, Class<T> p_26061_) {
            super(zombieVindicator, p_26061_, true);
            this.zombieVindicator = zombieVindicator;
        }

        public boolean canUse() {
            return super.canUse() && this.zombieVindicator.isNatural() && (this.zombieVindicator.getTrueOwner() == null || this.zombieVindicator.getTrueOwner() instanceof AbstractIllager) && this.target != null;
        }
    }
}
