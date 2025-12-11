package com.Polarice3.Goety.common.entities.ally.spider;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class SpiderServant extends AbstractSpiderServant {
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(SpiderServant.class, EntityDataSerializers.BYTE);
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("2a3ee720-61cb-402c-affa-2eef9343910d");
    public static final AttributeModifier STOP_MODIFIER = new AttributeModifier(SPEED_MODIFIER_UUID, "Stop Moving Dammit", -1.0D, AttributeModifier.Operation.ADDITION);
    private static final UUID DETECTION_MODIFIER_UUID = UUID.fromString("858f6b2f-73e3-45a0-8bef-bb31e0d55be4");
    public static final AttributeModifier DETECTION_MODIFIER = new AttributeModifier(DETECTION_MODIFIER_UUID, "Light Is Blinding", -1.0D, AttributeModifier.Operation.ADDITION);

    public SpiderServant(EntityType<? extends SpiderServant> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(5, new Summoned.WanderGoal<>(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.attackGoal();
    }

    public void attackGoal(){
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new SpiderAttackGoal(this));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            AttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.FOLLOW_RANGE);
            if (MobUtil.isInBrightLight(this)){
                if (modifiableattributeinstance != null) {
                    if (this.getAttribute(Attributes.FOLLOW_RANGE) != null) {
                        modifiableattributeinstance.removeModifier(DETECTION_MODIFIER);
                        modifiableattributeinstance.addTransientModifier(DETECTION_MODIFIER);
                    }
                }
            } else {
                if (modifiableattributeinstance != null) {
                    if (modifiableattributeinstance.hasModifier(DETECTION_MODIFIER)) {
                        modifiableattributeinstance.removeModifier(DETECTION_MODIFIER);
                    }
                }
            }
        }

    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SpiderServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SpiderServantDamage.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.SpiderServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.SpiderServantDamage.get());
    }

    public void makeStuckInBlock(BlockState p_33796_, Vec3 p_33797_) {
        if (!p_33796_.is(Blocks.COBWEB) && !p_33796_.is(Blocks.AIR)) {
            super.makeStuckInBlock(p_33796_, p_33797_);
        }

    }

    public boolean spawnWithEffects(){
        return true;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33790_, DifficultyInstance p_33791_, MobSpawnType p_33792_, @Nullable SpawnGroupData p_33793_, @Nullable CompoundTag p_33794_) {
        p_33793_ = super.finalizeSpawn(p_33790_, p_33791_, p_33792_, p_33793_, p_33794_);
        RandomSource randomsource = p_33790_.getRandom();
        if (this.spawnWithEffects()) {
            if (p_33793_ == null) {
                p_33793_ = new SpiderEffectsGroupData();
                if (p_33790_.getDifficulty() == Difficulty.HARD && randomsource.nextFloat() < 0.1F * p_33791_.getSpecialMultiplier()) {
                    ((SpiderEffectsGroupData) p_33793_).setRandomEffect(randomsource);
                }
            }

            if (p_33793_ instanceof SpiderEffectsGroupData) {
                MobEffect mobeffect = ((SpiderEffectsGroupData) p_33793_).effect;
                if (mobeffect != null) {
                    this.addEffect(new MobEffectInstance(mobeffect, EffectsUtil.infiniteEffect()));
                }
            }
        }

        return p_33793_;
    }

    @Nullable
    @Override
    public EntityType<?> getVariant(Level level, BlockPos blockPos) {
        EntityType<?> entityType = ModEntityType.SPIDER_SERVANT.get();
        if (level instanceof ServerLevel serverLevel) {
            if (BlockFinder.findStructure(serverLevel, blockPos, ModTags.Structures.CRYPT)){
                entityType = ModEntityType.BONE_SPIDER_SERVANT.get();
            } else if (RitualRequirements.frostRitual(null, blockPos, level)){
                entityType = ModEntityType.ICY_SPIDER_SERVANT.get();
            } else if (BlockFinder.findStructure(serverLevel, blockPos, StructureTags.MINESHAFT)){
                entityType = ModEntityType.CAVE_SPIDER_SERVANT.get();
            }
        }
        return entityType;
    }

    static class SpiderAttackGoal extends MeleeAttackGoal {
        public SpiderAttackGoal(SpiderServant p_33822_) {
            super(p_33822_, 1.0D, true);
        }

        public SpiderAttackGoal(SpiderServant p_33822_, double speed) {
            super(p_33822_, speed, true);
        }

        public boolean canUse() {
            return super.canUse() && !this.mob.isVehicle();
        }

        protected double getAttackReachSqr(LivingEntity p_33825_) {
            return (double)(4.0F + p_33825_.getBbWidth());
        }
    }

    public static class SpiderEffectsGroupData implements SpawnGroupData {
        @Nullable
        public MobEffect effect;

        public void setRandomEffect(RandomSource p_219119_) {
            int i = p_219119_.nextInt(5);
            if (i <= 1) {
                this.effect = MobEffects.MOVEMENT_SPEED;
            } else if (i == 2) {
                this.effect = MobEffects.DAMAGE_BOOST;
            } else if (i == 3) {
                this.effect = MobEffects.REGENERATION;
            } else if (i == 4) {
                this.effect = MobEffects.INVISIBILITY;
            }

        }
    }
}
