package com.Polarice3.Goety.common.entities.ally.illager.raider;

import com.Polarice3.Goety.common.entities.ai.MinionFollowGoal;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.Minion;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class AllyVex extends Minion {

    public AllyVex(EntityType<? extends Minion> p_i50190_1_, Level p_i50190_2_) {
        super(p_i50190_1_, p_i50190_2_);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MinionFollowGoal(this, 0.5D, 6.0f, 3.0f, true));
        this.goalSelector.addGoal(4, new VexChargeAttackGoal());
        this.goalSelector.addGoal(8, new VexRandomMoveGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Vex.class, false, vex -> !this.isHostile() && !MobUtil.areAllies(this, vex)));
        this.targetSelector.addGoal(1, new SummonTargetGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SummonedVexHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SummonedVexDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.SummonedVexHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.SummonedVexDamage.get());
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.VEX_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.VEX_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.VEX_HURT;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34002_, DifficultyInstance p_34003_, MobSpawnType p_34004_, @Nullable SpawnGroupData p_34005_, @Nullable CompoundTag p_34006_) {
        RandomSource randomsource = p_34002_.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, p_34003_);
        this.populateDefaultEquipmentEnchantments(randomsource, p_34003_);
        return super.finalizeSpawn(p_34002_, p_34003_, p_34004_, p_34005_, p_34006_);
    }

    protected void populateDefaultEquipmentSlots(RandomSource p_219135_, DifficultyInstance p_219136_) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity owner = this.getTrueOwner();
        if (MobsConfig.ServantOwnedServantPlayerBenefit.get()) {
            owner = this.getMasterOwner();
        }
        if (owner != null) {
            boolean crown = CuriosFinder.hasCurio(owner, ModItems.GRAND_TURBAN.get());
            if (!crown) {
                if (this.getLifespan() > 0) {
                    this.setHasLifespan(true);
                }
            } else {
                this.setHasLifespan(false);
            }
        }
    }
}
