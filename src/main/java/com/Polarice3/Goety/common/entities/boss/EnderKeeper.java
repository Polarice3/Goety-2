package com.Polarice3.Goety.common.entities.boss;

import com.Polarice3.Goety.client.particles.*;
import com.Polarice3.Goety.common.blocks.entities.VoidShrineBlockEntity;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ai.path.ModGroundNavigation;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractEnderling;
import com.Polarice3.Goety.common.entities.projectiles.VoidShock;
import com.Polarice3.Goety.common.entities.projectiles.VoidShockBomb;
import com.Polarice3.Goety.common.entities.projectiles.VoidSlash;
import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.Polarice3.Goety.common.entities.util.ModFallingBlock;
import com.Polarice3.Goety.common.entities.util.VoidLightningTrap;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.common.network.server.SPlayFollowSoundPacket;
import com.Polarice3.Goety.common.network.server.SRepositionPacket;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class EnderKeeper extends AbstractEnderling implements Enemy {
    protected static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(EnderKeeper.class, EntityDataSerializers.INT);
    private final ModServerBossInfo bossInfo;
    public static String INTRO = "intro";
    public static String IDLE = "idle";
    public static String SWING = "swing";
    public static String SWING_COMBO = "swing_combo";
    public static String SWING_COMBO_TRIPLE = "swing_combo_triple";
    public static String RAPID_SWING = "rapid_swing";
    public static String CHARGE = "charge";
    public static String SPELL_1 = "spell_1";
    public static String SPELL_2 = "spell_2";
    public static String SPELL_3 = "spell_3";
    public static String LIFE_STEAL = "life_steal";
    public static String GROUND_POUND = "ground_pound";
    public static String GROUND_POUND_SPIN = "ground_pound_spin";
    public static String BACK_AWAY = "back_away";
    public static String SLICE_START = "slice_start";
    public static String SLICE_1 = "slice_1";
    public static String SLICE_2 = "slice_2";
    public static String DEATH = "death";
    public int introTick;
    public int attackTick;
    public int sliceAmount = 0;
    public int swingCool = 0;
    public int swingComboCool = 0;
    public int rapidSwingCool = 0;
    public int chargeCool = 0;
    public int spell1Cool = 0;
    public int spell2Cool = 0;
    public int spell3Cool = 0;
    public int lifeStealCool = 0;
    public int groundPoundCool = 0;
    public int backAwayCool = 0;
    public int slicingCool = 0;
    public int groundPoundSize = 6;
    public int shakeSword = 0;
    public int moddedInvul = 0;
    public int deathTime;
    public float deathRotation = 0.0F;
    private BlockPos lastSafePosition;
    public AnimationState introAnimationState = new AnimationState();
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState swingAnimationState = new AnimationState();
    public AnimationState swingComboAnimationState = new AnimationState();
    public AnimationState swingComboTripleAnimationState = new AnimationState();
    public AnimationState rapidSwingAnimationState = new AnimationState();
    public AnimationState chargeAnimationState = new AnimationState();
    public AnimationState spell1AnimationState = new AnimationState();
    public AnimationState spell2AnimationState = new AnimationState();
    public AnimationState spell3AnimationState = new AnimationState();
    public AnimationState lifeStealAnimationState = new AnimationState();
    public AnimationState groundPoundAnimationState = new AnimationState();
    public AnimationState groundPoundSpinAnimationState = new AnimationState();
    public AnimationState backAwayAnimationState = new AnimationState();
    public AnimationState slice1AnimationState = new AnimationState();
    public AnimationState slice2AnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();

    public final List<Pair<Vec3, ModelSnapshot>> trailSnapshots = new ArrayList<>(50);
    public float lastTrailTick = 0;

    public boolean shouldAddTrailSnapshot() {
        if (!MobsConfig.EnderKeeperAfterImage.get()) {
            return false;
        }
        return Mth.degreesDifferenceAbs(getYRot(), yBodyRot) < 45
                && Mth.degreesDifferenceAbs(getYRot(), yBodyRotO) < 45
                && Mth.degreesDifferenceAbs(yBodyRot, yBodyRotO) < 45
                && !isHiding()
                && (rapidSwingAnimationState.isStarted()
                || chargeAnimationState.isStarted()
                || groundPoundAnimationState.isStarted()
                || groundPoundSpinAnimationState.isStarted()
                || backAwayAnimationState.isStarted()
                || slice1AnimationState.isStarted()
                || slice2AnimationState.isStarted());
    }

    public EnderKeeper(EntityType<? extends AbstractEnderling> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
        this.setHostile(true);
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.PURPLE, true, false);
        this.setMaxUpStep(2.0F);
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.noCulling = true;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.attackGoals();
        this.goalSelector.addGoal(5, new WanderGoal<>(this, 1.0D, 80));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true){
            protected @NotNull AABB getTargetSearchArea(double area) {
                return this.mob.getBoundingBox().inflate(area, area, area);
            }
        });
    }

    public void attackGoals() {
        this.goalSelector.addGoal(1, new KeeperAttackGoal(this, IDLE, SWING, IDLE, 50, 10, 6.0F){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && !MobUtil.healthIsHalved(EnderKeeper.this)
                        && EnderKeeper.this.swingCool <= 0;
            }

            @Override
            public void stop() {
                super.stop();
                EnderKeeper.this.swingCool = 100;
            }
        });
        this.goalSelector.addGoal(1, new KeeperAttackGoal(this, IDLE, BACK_AWAY, IDLE, 20, 20, 6.0F){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && EnderKeeper.this.backAwayCool <= 0;
            }

            @Override
            public void stop() {
                super.stop();
                EnderKeeper.this.backAwayCool = 100;
            }
        });
        this.goalSelector.addGoal(1, new KeeperAttackGoal(this, IDLE, SWING_COMBO, IDLE, 70, 60, 6.0F){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && (EnderKeeper.this.getRandom().nextBoolean() && !MobUtil.healthIsHalved(EnderKeeper.this))
                        && EnderKeeper.this.swingComboCool <= 0;
            }

            @Override
            public void start() {
                super.start();
                if (EnderKeeper.this.getTarget() != null) {
                    MobUtil.instaLook(EnderKeeper.this, EnderKeeper.this.getTarget(), true);
                }
            }

            @Override
            public void stop() {
                super.stop();
                EnderKeeper.this.swingComboCool = 100;
            }
        });
        this.goalSelector.addGoal(1, new KeeperAttackGoal(this, IDLE, SWING_COMBO_TRIPLE, IDLE, 75, 65, 6.0F){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && (EnderKeeper.this.getRandom().nextFloat() <= 0.25F || MobUtil.healthIsHalved(EnderKeeper.this))
                        && EnderKeeper.this.swingComboCool <= 0;
            }

            @Override
            public void start() {
                super.start();
                if (EnderKeeper.this.getTarget() != null) {
                    MobUtil.instaLook(EnderKeeper.this, EnderKeeper.this.getTarget(), true);
                }
            }

            @Override
            public void stop() {
                super.stop();
                EnderKeeper.this.swingComboCool = 100;
            }
        });
        this.goalSelector.addGoal(2, new KeeperAttackGoal(this, IDLE, RAPID_SWING, IDLE, 62, 10, 6.0F, 40.0F){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && EnderKeeper.this.level.getRandom().nextFloat() <= 0.25F
                        && EnderKeeper.this.rapidSwingCool <= 0;
            }

            @Override
            public void start() {
                super.start();
                if (EnderKeeper.this.getTarget() != null) {
                    MobUtil.instaLook(EnderKeeper.this, EnderKeeper.this.getTarget(), true);
                }
            }

            @Override
            public void stop() {
                super.stop();
                EnderKeeper.this.rapidSwingCool = 200;
            }
        });
        this.goalSelector.addGoal(2, new KeeperAttackGoal(this, IDLE, CHARGE, IDLE, 50, 10, 6.0F, 40.0F){
            @Override
            public boolean canUse() {
                return super.canUse() && EnderKeeper.this.chargeCool <= 0;
            }

            @Override
            public void start() {
                super.start();
                if (EnderKeeper.this.getTarget() != null) {
                    MobUtil.instaLook(EnderKeeper.this, EnderKeeper.this.getTarget(), true);
                }
            }

            @Override
            public void stop() {
                super.stop();
                EnderKeeper.this.chargeCool = 200;
            }
        });

        this.goalSelector.addGoal(2, new KeeperAttackGoal(this, IDLE, GROUND_POUND, IDLE, 30, 10, 6.0F, 40.0F){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && EnderKeeper.this.groundPoundCool <= 0
                        && EnderKeeper.this.level.getRandom().nextBoolean();
            }

            @Override
            public void start() {
                super.start();
                EnderKeeper.this.groundPoundSize = MobUtil.healthIsHalved(EnderKeeper.this) ? EnderKeeper.this.level.getRandom().nextIntBetweenInclusive(8, 10) : 6;
                EnderKeeper.this.startHide();
                if (EnderKeeper.this.getTarget() != null) {
                    Vec3 vec3 = EnderKeeper.this.getTarget().position();
                    Vec3 vec31 = BlockFinder.SummonPosition(EnderKeeper.this.getTarget(), vec3);
                    EnderKeeper.this.ownedTeleport(vec31.x, vec31.y, vec31.z);
                    if (EnderKeeper.this.level instanceof ServerLevel serverLevel) {
                        ColorUtil colorUtil = ColorUtil.WHITE;
                        serverLevel.sendParticles(new AoEParticleOption(EnderKeeper.this.groundPoundSize, 20), EnderKeeper.this.getX(), EnderKeeper.this.getY() + 0.25F, EnderKeeper.this.getZ(), 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);
                    }
                }
            }

            @Override
            public void stop() {
                super.stop();
                EnderKeeper.this.stopHide();
                EnderKeeper.this.groundPoundCool = 200;
            }
        });
        this.goalSelector.addGoal(2, new KeeperAttackGoal(this, IDLE, GROUND_POUND_SPIN, IDLE, 65, 10, 6.0F, 40.0F){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && EnderKeeper.this.groundPoundCool <= 0
                        && (EnderKeeper.this.level.getRandom().nextFloat() <= 0.05F || (MobUtil.healthIsHalved(EnderKeeper.this) && EnderKeeper.this.level.getRandom().nextFloat() <= 0.75F));
            }

            @Override
            public void start() {
                super.start();
                EnderKeeper.this.groundPoundSize = MobUtil.healthIsHalved(EnderKeeper.this) ? EnderKeeper.this.level.getRandom().nextIntBetweenInclusive(8, 10) : 6;
                EnderKeeper.this.startHide();
                if (EnderKeeper.this.getTarget() != null) {
                    Vec3 vec3 = EnderKeeper.this.getTarget().position();
                    Vec3 vec31 = BlockFinder.SummonPosition(EnderKeeper.this.getTarget(), vec3);
                    EnderKeeper.this.ownedTeleport(vec31.x, vec31.y, vec31.z);
                    if (EnderKeeper.this.level instanceof ServerLevel serverLevel) {
                        ColorUtil colorUtil = ColorUtil.WHITE;
                        serverLevel.sendParticles(new AoEParticleOption(EnderKeeper.this.groundPoundSize, 20), EnderKeeper.this.getX(), EnderKeeper.this.getY() + 0.25F, EnderKeeper.this.getZ(), 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);
                    }
                }
            }

            @Override
            public void stop() {
                super.stop();
                EnderKeeper.this.stopHide();
                EnderKeeper.this.groundPoundCool = 200;
            }
        });
        this.goalSelector.addGoal(2, new KeeperAttackGoal(this, IDLE, LIFE_STEAL, IDLE, 140, 30, 20.0F){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && EnderKeeper.this.level.getRandom().nextBoolean()
                        && EnderKeeper.this.getHealth() <= (EnderKeeper.this.getMaxHealth() / 2.0F)
                        && EnderKeeper.this.lifeStealCool <= 0;
            }

            @Override
            public void start() {
                super.start();
                EnderKeeper.this.playSound(ModSounds.VOID_PREPARE_SPELL.get(), 2.0F, 0.5F);
            }

            @Override
            public void stop() {
                super.stop();
                EnderKeeper.this.lifeStealCool = 200;
            }
        });
        this.goalSelector.addGoal(2, new KeeperAttackGoal(this, IDLE, SPELL_1, IDLE, 50, 0, 40.0F){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && EnderKeeper.this.spell1Cool <= 0
                        && EnderKeeper.this.spell2Cool <= 0;
            }

            @Override
            public void stop() {
                super.stop();
                EnderKeeper.this.spell1Cool = 100;
            }
        });
        this.goalSelector.addGoal(3, new KeeperAttackGoal(this, IDLE, SPELL_2, IDLE, 65, 30, 20.0F){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && EnderKeeper.this.level.getRandom().nextBoolean()
                        && EnderKeeper.this.spell2Cool <= 0;
            }

            @Override
            public void stop() {
                super.stop();
                EnderKeeper.this.spell2Cool = 200;
            }
        });
        this.goalSelector.addGoal(3, new KeeperAttackGoal(this, IDLE, SPELL_3, IDLE, 80, 30, 6.0F, 20.0F){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && EnderKeeper.this.level.getRandom().nextBoolean()
                        && EnderKeeper.this.spell3Cool <= 0;
            }

            @Override
            public void start() {
                super.start();
                EnderKeeper.this.playSound(ModSounds.THUNDER_STRIKE_FAST.get(), 2.0F, 0.5F);
            }

            @Override
            public void stop() {
                super.stop();
                EnderKeeper.this.spell3Cool = 300;
            }
        });
        this.goalSelector.addGoal(3, new KeeperAttackGoal(this, IDLE, LIFE_STEAL, IDLE, 140, 30, 20.0F){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && EnderKeeper.this.level.getRandom().nextBoolean()
                        && EnderKeeper.this.getHealth() > (EnderKeeper.this.getMaxHealth() / 2.0F)
                        && EnderKeeper.this.getHealth() < EnderKeeper.this.getMaxHealth()
                        && EnderKeeper.this.lifeStealCool <= 0;
            }

            @Override
            public void start() {
                super.start();
                EnderKeeper.this.playSound(ModSounds.VOID_PREPARE_SPELL.get(), 2.0F, 0.5F);
            }

            @Override
            public void stop() {
                super.stop();
                EnderKeeper.this.lifeStealCool = 200;
            }
        });
        this.sliceGoals();
    }

    public void sliceGoals(){
        this.goalSelector.addGoal(3, new KeeperAttackGoal(this, IDLE, SLICE_START, SLICE_START, 20, 10, 6.0F, 40.0F){
            @Override
            public boolean canUse() {
                return super.canUse()
                        && EnderKeeper.this.slicingCool <= 0
                        && EnderKeeper.this.spell2Cool <= 100;
            }

            @Override
            public boolean canContinueToUse() {
                int hideTime = MobUtil.healthIsHalved(EnderKeeper.this) ? 20 : 40;
                return super.canContinueToUse() && EnderKeeper.this.hidingTime < hideTime;
            }

            @Override
            public void start() {
                super.start();
                EnderKeeper.this.startHide();
                if (EnderKeeper.this.getTarget() != null) {
                    EnderKeeper.this.teleportTowards(EnderKeeper.this.getTarget(), 4.0F);
                }
            }
        });
        this.goalSelector.addGoal(4, new KeeperAttackGoal(this, SLICE_START, SLICE_1, SLICE_1, 24, 14, 40.0F){

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && EnderKeeper.this.hidingTime < 10;
            }

            @Override
            public void start() {
                super.start();
                EnderKeeper.this.stopHide();
                if (EnderKeeper.this.getTarget() != null) {
                    MobUtil.instaLook(EnderKeeper.this, EnderKeeper.this.getTarget(), true);
                }
            }
        });
        this.goalSelector.addGoal(0, new KeeperAttackGoal(this, SLICE_1, SLICE_2, SLICE_2, 24, 14, 40.0F){

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && EnderKeeper.this.hidingTime < 10;
            }

            @Override
            public void start() {
                super.start();
                EnderKeeper.this.stopHide();
                if (EnderKeeper.this.getTarget() != null) {
                    MobUtil.instaLook(EnderKeeper.this, EnderKeeper.this.getTarget(), true);
                }
            }
        });
        this.goalSelector.addGoal(0, new KeeperAttackGoal(this, SLICE_2, SLICE_1, SLICE_1, 24, 14, 40.0F){
            @Override
            public boolean canUse() {
                return super.canUse() && EnderKeeper.this.sliceAmount <= 1;
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && EnderKeeper.this.hidingTime < 10;
            }

            @Override
            public void start() {
                super.start();
                EnderKeeper.this.stopHide();
                EnderKeeper.this.sliceAmount += 1;
                if (EnderKeeper.this.getTarget() != null) {
                    MobUtil.instaLook(EnderKeeper.this, EnderKeeper.this.getTarget(), true);
                }
            }
        });
        this.goalSelector.addGoal(0, new KeeperAttackGoal(this, SLICE_2, SLICE_1, IDLE, 20, 12, 40.0F){
            @Override
            public boolean canUse() {
                return super.canUse() && EnderKeeper.this.sliceAmount > 1;
            }

            @Override
            public void start() {
                super.start();
                EnderKeeper.this.stopHide();
                EnderKeeper.this.slicingCool = 250;
                EnderKeeper.this.sliceAmount = 0;
                if (EnderKeeper.this.getTarget() != null) {
                    MobUtil.instaLook(EnderKeeper.this, EnderKeeper.this.getTarget(), true);
                }
            }

            @Override
            public void stop() {
                super.stop();
                EnderKeeper.this.stopHide();
                EnderKeeper.this.slicingCool = 400;
                EnderKeeper.this.sliceAmount = 0;
                if (EnderKeeper.this.getTarget() != null) {
                    MobUtil.instaLook(EnderKeeper.this, EnderKeeper.this.getTarget(), true);
                }
            }
        });
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new ModGroundNavigation(this, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.EnderKeeperDamage.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.EnderKeeperHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.EnderKeeperArmor.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.EnderKeeperHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.EnderKeeperArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.EnderKeeperDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("IntroTick", this.introTick);
        compound.putInt("SwingCool", this.swingCool);
        compound.putInt("SwingComboCool", this.swingComboCool);
        compound.putInt("RapidSwingCool", this.rapidSwingCool);
        compound.putInt("ChargeCool", this.chargeCool);
        compound.putInt("Spell1Cool", this.spell1Cool);
        compound.putInt("Spell2Cool", this.spell2Cool);
        compound.putInt("Spell3Cool", this.spell3Cool);
        compound.putInt("LifeStealCool", this.lifeStealCool);
        compound.putInt("GroundPoundCool", this.groundPoundCool);
        compound.putInt("BackAwayCool", this.backAwayCool);
        compound.putInt("SlicingCool", this.slicingCool);
        compound.putInt("ModdedInvul", this.moddedInvul);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("IntroTick")) {
            this.introTick = compound.getInt("IntroTick");
        }
        if (compound.contains("SwingCool")) {
            this.swingCool = compound.getInt("SwingCool");
        }
        if (compound.contains("SwingComboCool")) {
            this.swingComboCool = compound.getInt("SwingComboCool");
        }
        if (compound.contains("RapidSwingCool")) {
            this.rapidSwingCool = compound.getInt("RapidSwingCool");
        }
        if (compound.contains("ChargeCool")) {
            this.chargeCool = compound.getInt("ChargeCool");
        }
        if (compound.contains("Spell1Cool")) {
            this.spell1Cool = compound.getInt("Spell1Cool");
        }
        if (compound.contains("Spell2Cool")) {
            this.spell2Cool = compound.getInt("Spell2Cool");
        }
        if (compound.contains("Spell3Cool")) {
            this.spell3Cool = compound.getInt("Spell3Cool");
        }
        if (compound.contains("LifeStealCool")) {
            this.lifeStealCool = compound.getInt("LifeStealCool");
        }
        if (compound.contains("GroundPoundCool")) {
            this.groundPoundCool = compound.getInt("GroundPoundCool");
        }
        if (compound.contains("BackAwayCool")) {
            this.backAwayCool = compound.getInt("BackAwayCool");
        }
        if (compound.contains("SlicingCool")) {
            this.slicingCool = compound.getInt("SlicingCool");
        }
        if (compound.contains("ModdedInvul")) {
            this.moddedInvul = compound.getInt("ModdedInvul");
        }
    }

    @Nullable
    @Override
    public LivingEntity getTrueOwner() {
        return null;
    }

    @Override
    public int xpReward() {
        return 1000;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (this.isIntro() && !source.is(DamageTypes.FELL_OUT_OF_WORLD)){
            return false;
        }
        double range = MobUtil.calculateRange(this, source);
        if (range > Mth.square(AttributesConfig.EnderKeeperHurtRange.get()) && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            if (source.getEntity() != null && !MobUtil.areAllies(this, source.getEntity())) {
                if (!this.isAttacking() && !this.isHiding()) {
                    this.teleportTowards(source.getEntity(), 4.0D);
                }
            }
            return false;
        }
        if (source.getEntity() instanceof OwnableEntity ownable) {
            if (ownable.getOwner() != null) {
                damage *= 0.5F;
            }
        }
        if (this.moddedInvul > 0){
            return false;
        }
        return super.hurt(source, damage);
    }

    protected void actuallyHurt(DamageSource source, float amount) {
        float initialAmount = amount;
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)){
            amount = Math.min(initialAmount, AttributesConfig.EnderKeeperDamageCap.get().floatValue());
        }
        if (this.moddedInvul <= 0){
            super.actuallyHurt(source, amount);
            if (source.getEntity() != null) {
                this.moddedInvul = MobsConfig.BossInvulnerabilityTime.get();
            }
        }
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, IDLE)){
            return 0;
        } else if (Objects.equals(animation, SWING)){
            return 1;
        } else if (Objects.equals(animation, SWING_COMBO)){
            return 2;
        } else if (Objects.equals(animation, SWING_COMBO_TRIPLE)){
            return 3;
        } else if (Objects.equals(animation, RAPID_SWING)){
            return 4;
        } else if (Objects.equals(animation, CHARGE)){
            return 5;
        } else if (Objects.equals(animation, SPELL_1)){
            return 6;
        } else if (Objects.equals(animation, SPELL_2)){
            return 7;
        } else if (Objects.equals(animation, SPELL_3)){
            return 8;
        } else if (Objects.equals(animation, GROUND_POUND)){
            return 9;
        } else if (Objects.equals(animation, GROUND_POUND_SPIN)){
            return 10;
        } else if (Objects.equals(animation, BACK_AWAY)){
            return 11;
        } else if (Objects.equals(animation, SLICE_1)){
            return 12;
        } else if (Objects.equals(animation, SLICE_2)){
            return 13;
        } else if (Objects.equals(animation, SLICE_START)){
            return 14;
        } else if (Objects.equals(animation, DEATH)){
            return 15;
        } else if (Objects.equals(animation, LIFE_STEAL)){
            return 16;
        } else if (Objects.equals(animation, INTRO)){
            return 17;
        }  else {
            return 0;
        }
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.introAnimationState);
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.swingAnimationState);
        animationStates.add(this.swingComboAnimationState);
        animationStates.add(this.swingComboTripleAnimationState);
        animationStates.add(this.rapidSwingAnimationState);
        animationStates.add(this.chargeAnimationState);
        animationStates.add(this.spell1AnimationState);
        animationStates.add(this.spell2AnimationState);
        animationStates.add(this.spell3AnimationState);
        animationStates.add(this.lifeStealAnimationState);
        animationStates.add(this.groundPoundAnimationState);
        animationStates.add(this.groundPoundSpinAnimationState);
        animationStates.add(this.backAwayAnimationState);
        animationStates.add(this.slice1AnimationState);
        animationStates.add(this.slice2AnimationState);
        animationStates.add(this.deathAnimationState);
        return animationStates;
    }

    public void stopAllAnimations() {
        for (AnimationState state : this.getAnimations()){
            state.stop();
        }
    }

    public void stopMostAnimation(AnimationState exception) {
        for (AnimationState state : this.getAnimations()) {
            if (state != exception){
                state.stop();
            }
        }
    }

    public int getCurrentAnimation(){
        return this.entityData.get(ANIM_STATE);
    }

    public boolean isCurrentAnimation(String animation) {
        return this.getCurrentAnimation() == this.getAnimationState(animation);
    }

    public boolean isAttacking() {
        return !this.isCurrentAnimation(IDLE) && !this.isHiding();
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_219422_) {
        if (ANIM_STATE.equals(p_219422_)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                    case 14:
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 17:
                        this.stopMostAnimation(this.introAnimationState);
                        this.introAnimationState.start(this.tickCount);
                        break;
                    case 1:
                        this.stopMostAnimation(this.swingAnimationState);
                        this.swingAnimationState.start(this.tickCount);
                        break;
                    case 2:
                        this.stopMostAnimation(this.swingComboAnimationState);
                        this.swingComboAnimationState.start(this.tickCount);
                        break;
                    case 3:
                        this.stopMostAnimation(this.swingComboTripleAnimationState);
                        this.swingComboTripleAnimationState.start(this.tickCount);
                        break;
                    case 4:
                        this.stopMostAnimation(this.rapidSwingAnimationState);
                        this.rapidSwingAnimationState.start(this.tickCount);
                        break;
                    case 5:
                        this.stopMostAnimation(this.chargeAnimationState);
                        this.chargeAnimationState.start(this.tickCount);
                        break;
                    case 6:
                        this.stopMostAnimation(this.spell1AnimationState);
                        this.spell1AnimationState.start(this.tickCount);
                        break;
                    case 7:
                        this.stopMostAnimation(this.spell2AnimationState);
                        this.spell2AnimationState.start(this.tickCount);
                        break;
                    case 8:
                        this.stopMostAnimation(this.spell3AnimationState);
                        this.spell3AnimationState.start(this.tickCount);
                        break;
                    case 9:
                        this.stopMostAnimation(this.groundPoundAnimationState);
                        this.groundPoundAnimationState.start(this.tickCount);
                        break;
                    case 10:
                        this.stopMostAnimation(this.groundPoundSpinAnimationState);
                        this.groundPoundSpinAnimationState.start(this.tickCount);
                        break;
                    case 11:
                        this.stopMostAnimation(this.backAwayAnimationState);
                        this.backAwayAnimationState.start(this.tickCount);
                        break;
                    case 12:
                        this.stopMostAnimation(this.slice1AnimationState);
                        this.slice1AnimationState.start(this.tickCount);
                        break;
                    case 13:
                        this.stopMostAnimation(this.slice2AnimationState);
                        this.slice2AnimationState.start(this.tickCount);
                        break;
                    case 15:
                        this.stopMostAnimation(this.deathAnimationState);
                        this.deathAnimationState.start(this.tickCount);
                        break;
                    case 16:
                        this.stopMostAnimation(this.lifeStealAnimationState);
                        this.lifeStealAnimationState.start(this.tickCount);
                        break;
                }
            }
        }

        super.onSyncedDataUpdated(p_219422_);
    }

    protected int decreaseAirSupply(int air) {
        return air;
    }

    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected boolean isAffectedByFluids() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
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

    public void makeStuckInBlock(BlockState p_33796_, Vec3 p_33797_) {
    }

    protected boolean canRide(Entity p_219462_) {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.ENDER_KEEPER_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ENDER_KEEPER_DEATH.get();
    }

    @Override
    public void stepSound() {
        this.playSound(ModSounds.TOWER_WRAITH_FLY.get(), 0.15F, 0.5F);
    }

    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        return pPotioneffect.getEffect().isBeneficial() && super.canBeAffected(pPotioneffect);
    }

    public boolean addEffect(MobEffectInstance pPotioneffect, @Nullable Entity entity) {
        if (entity == this) {
            return super.addEffect(pPotioneffect, entity);
        } else {
            return pPotioneffect.getEffect().isBeneficial();
        }
    }

    public VoidShrineBlockEntity getVoidShrine() {
        if (this.getBoundPos() != null) {
            if (this.level.getBlockEntity(this.getBoundPos()) instanceof VoidShrineBlockEntity blockEntity) {
                return blockEntity;
            }
        }
        return null;
    }

    private BlockPos getSafeGround() {
        BlockPos position = this.blockPosition();
        while (position.getY() < 256 && !this.level.getFluidState(position).isEmpty()) {
            position = position.above();
        }
        while (position.getY() > 1 && this.level.isEmptyBlock(position)) {
            position = position.below();
        }
        return position;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @org.jetbrains.annotations.Nullable SpawnGroupData pSpawnData, @org.jetbrains.annotations.Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED){
            this.setPose(Pose.EMERGING);
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.isIntro();
    }

    public boolean hasLineOfSight(Entity p_149755_) {
        return !this.isIntro() && super.hasLineOfSight(p_149755_);
    }

    public boolean isIntro() {
        return this.hasPose(Pose.EMERGING);
    }

    @Override
    public void die(DamageSource pCause) {
        this.deathRotation = this.getYRot();
        super.die(pCause);
        this.setAnimationState(DEATH);
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= MathHelper.secondsToTicks(2.5F) && this.deathTime < MathHelper.secondsToTicks(6)) {
            if (this.level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 8; ++i) {
                    serverLevel.sendParticles(new MagicSmokeParticle.Option(0, 0, this.level.getRandom().nextIntBetweenInclusive(40, 80), 0.25F), this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0, this.level.getRandom().nextBoolean() ? 0.01D : -0.01D, 0.1D, this.level.getRandom().nextBoolean() ? 0.01D : -0.01D, 0.5F);
                }
            }
        }
        if (this.deathTime == 32) {
            this.playSound(ModSounds.OBSIDIAN_CLAYMORE_SMASH.get(), 2.0F, 1.0F);
        }
        if (this.deathTime >= MathHelper.secondsToTicks(7.5F)) {
            this.remove(RemovalReason.KILLED);
        }
        this.setYRot(this.deathRotation);
        this.setYBodyRot(this.deathRotation);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            if (this.getVoidShrine() != null) {
                try {
                    this.getVoidShrine().getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                        ItemStack itemStack = handler.getStackInSlot(0);
                        if (itemStack.isEmpty()) {
                            handler.insertItem(0, new ItemStack(ModItems.SHROUDED_BLUEPRINT.get()), false);
                        }
                    });
                } catch (NullPointerException exception) {
                    ItemEntity itementity = this.spawnAtLocation(ModItems.SHROUDED_BLUEPRINT.get());
                    if (itementity != null) {
                        itementity.setGlowingTag(true);
                        itementity.setExtendedLifetime();
                    }
                }
            } else {
                ItemEntity itementity = this.spawnAtLocation(ModItems.SHROUDED_BLUEPRINT.get());
                if (itementity != null) {
                    itementity.setGlowingTag(true);
                    itementity.setExtendedLifetime();
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 5 == 0) {
            this.bossInfo.update();
        }
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        if (this.isDeadOrDying()){
            this.setYRot(this.deathRotation);
            this.setYBodyRot(this.deathRotation);
        }
        if (this.hasPose(Pose.EMERGING)){
            ++this.introTick;
            if (this.introTick == 1) {
                this.playSound(ModSounds.REAPER_FLY.get(), 2.0F, 0.75F);
            }
            if (this.introTick == 30) {
                this.playSound(ModSounds.TOWER_WRAITH_FLY.get(), 2.0F, 0.75F);
            }
            if (this.introTick == 60) {
                this.playSound(ModSounds.OBSIDIAN_CLAYMORE_SWING.get(), 2.0F, this.getVoicePitch() - 0.5F);
            }
            if (this.introTick == 85) {
                this.playSound(SoundEvents.PLAYER_ATTACK_CRIT, 2.0F, this.getVoicePitch());
                this.playSound(ModSounds.SWORD_SHING.get(), 3.0F, this.getVoicePitch());
            }
            if (this.introTick > MathHelper.secondsToTicks(4.5F)){
                this.setPose(Pose.STANDING);
                this.setAnimationState(IDLE);
            } else {
                this.setAnimationState(INTRO);
            }
        }
        if (!this.isIntro()) {
            MiscCapHelper.updateMobTarget(this);
            if (this.level.isClientSide) {
                this.idleAnimationState.animateWhen(!this.walkAnimation.isMoving() && this.isCurrentAnimation(IDLE) && !this.isDeadOrDying(), this.tickCount);
                if (this.shakeSword > 0) {
                    --this.shakeSword;
                }
                if (!this.isDeadOrDying() && !this.isHiding()) {
                    this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getRandomY(), this.getZ(), 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        Vec3 vector3d = this.getDeltaMovement();
        if (!this.onGround()&& vector3d.y < 0.0D && !this.isNoGravity()) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }
        if (this.getVoidShrine() != null) {
            try {
                this.getVoidShrine().setCoolTick(0);
            } catch (NullPointerException ignored) {
            }
        }
        if (this.swingCool > 0) {
            --this.swingCool;
        }
        if (this.swingComboCool > 0) {
            --this.swingComboCool;
        }
        if (this.rapidSwingCool > 0) {
            --this.rapidSwingCool;
        }
        if (this.chargeCool > 0) {
            --this.chargeCool;
        }
        if (this.spell1Cool > 0) {
            --this.spell1Cool;
        }
        if (this.spell2Cool > 0) {
            --this.spell2Cool;
        }
        if (this.spell3Cool > 0) {
            --this.spell3Cool;
        }
        if (this.lifeStealCool > 0) {
            --this.lifeStealCool;
        }
        if (this.groundPoundCool > 0) {
            --this.groundPoundCool;
        }
        if (this.backAwayCool > 0) {
            --this.backAwayCool;
        }
        if (this.slicingCool > 0) {
            --this.slicingCool;
        }
        if (this.moddedInvul > 0){
            --this.moddedInvul;
        }

        if (!this.level.isClientSide) {
            if (this.getBoundPos() == null) {
                if (this.tickCount % 100 == 0 || this.lastSafePosition == null) {
                    BlockPos blockPos = this.getSafeGround();
                    if (blockPos.getY() > 1) {
                        this.lastSafePosition = blockPos;
                    }
                }
            }
            if (this.getY() <= this.level.getMinBuildHeight()) {
                BlockPos blockPos = null;
                if (this.getBoundPos() != null) {
                    blockPos = this.getBoundPos();
                } else if (this.lastSafePosition != null) {
                    blockPos = this.lastSafePosition;
                }
                if (blockPos != null) {
                    this.ownedTeleport(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                }
            }
            float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            if (this.isAttacking()) {
                ++this.attackTick;
            } else {
                this.attackTick = 0;
            }
            if (this.isCurrentAnimation(SWING)) {
                if (this.attackTick == 13) {
                    this.playSound(ModSounds.OBSIDIAN_CLAYMORE_SWING.get(), 2.0F, this.getVoicePitch());
                }
                if (this.attackTick == 18) {
                    CameraShake.cameraShake(this.level, this.position(), 15.0F, 0.1F, 0, 10);
                    this.pushOn(1.35F, 1.35F);
                    this.areaAttack(6.5F, 6, 90, damage, 60, false);
                }
            }
            if (this.isCurrentAnimation(SWING_COMBO)) {
                if (this.attackTick == 13 || this.attackTick == 31) {
                    this.playSound(ModSounds.OBSIDIAN_CLAYMORE_SWING.get(), 2.0F, this.getVoicePitch());
                }
                if (this.attackTick == 18 || this.attackTick == 34) {
                    CameraShake.cameraShake(this.level, this.position(), 15.0F, 0.1F, 0, 10);
                    this.pushOn(1.35F, 1.35F);
                    this.areaAttack(6.5F, 6, 90, damage, 60, false);
                }
            }
            if (this.isCurrentAnimation(SWING_COMBO_TRIPLE)) {
                if (this.attackTick == 13 || this.attackTick == 31 || this.attackTick == 52) {
                    this.playSound(ModSounds.OBSIDIAN_CLAYMORE_SWING.get(), 2.0F, this.getVoicePitch());
                }
                if (this.attackTick == 18 || this.attackTick == 34) {
                    CameraShake.cameraShake(this.level, this.position(), 15.0F, 0.1F, 0, 10);
                    this.pushOn(1.35F, 1.35F);
                    this.areaAttack(6.5F, 6, 90, damage, 60, false);
                }
                if (this.attackTick == 56) {
                    CameraShake.cameraShake(this.level, this.position(), 15.0F, 0.1F, 0, 10);
                    this.areaAttack(6.5F, 8, 100, damage, 100, false);
                    this.playSound(ModSounds.OBSIDIAN_CLAYMORE_SMASH.get(), 3.0F, this.getVoicePitch() * 0.5F);
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 3.0F, this.getVoicePitch() * 0.5F);
                    this.playSound(SoundEvents.TOTEM_USE, 3.0F, this.getVoicePitch() * 0.5F);
                }
                for (int i = 56; i < 66; ++i) {
                    if (this.attackTick == i) {
                        int l = i - 56;
                        this.outwardTremor(l, 0.0F, damage);
                        this.outwardTremor(l, 0.5F, damage);
                        this.outwardTremor(l, -0.5F, damage);
                        if (l >= 1) {
                            this.outwardTremor(l, 1.5F, damage);
                            this.outwardTremor(l, -1.5F, damage);
                        }
                        if (l >= 2) {
                            this.outwardTremor(l, 2.5F, damage);
                            this.outwardTremor(l, -2.5F, damage);
                        }
                        if (l >= 3) {
                            this.outwardTremor(l, 3.5F, damage);
                            this.outwardTremor(l, -3.5F, damage);
                        }
                        if (l >= 4) {
                            this.outwardTremor(l, 4.5F, damage);
                            this.outwardTremor(l, -4.5F, damage);
                        }
                        if (l >= 5) {
                            this.outwardTremor(l, 5.5F, damage);
                            this.outwardTremor(l, -5.5F, damage);
                        }
                        if (l >= 6) {
                            this.outwardTremor(l, 6.5F, damage);
                            this.outwardTremor(l, -6.5F, damage);
                        }
                        if (l >= 7) {
                            this.outwardTremor(l, 7.5F, damage);
                            this.outwardTremor(l, -7.5F, damage);
                        }

                    }
                }
            }
            if (this.isCurrentAnimation(RAPID_SWING)) {
                if (this.attackTick == 10 || this.attackTick == 20 || this.attackTick == 28 || this.attackTick == 36 || this.attackTick == 44) {
                    this.playSound(ModSounds.OBSIDIAN_CLAYMORE_SWING.get(), 2.0F, this.getVoicePitch());
                }
                if (this.attackTick >= 13 && this.attackTick < 54) {
                    if (this.getTarget() != null) {
                        this.lookAt(this.getTarget(), 100.0F, 100.0F);
                        this.getLookControl().setLookAt(this.getTarget(), 100.0F, 100.0F);
                    }
                    if (this.attackTick == 16 || this.attackTick == 24 || this.attackTick == 32 || this.attackTick == 40 || this.attackTick == 48) {
                        this.areaAttack(6.5F, 6, 120, damage, 60, false);
                        VoidSlash voidSlash = new VoidSlash(this.level, this);
                        voidSlash.setPos(this.getEyePosition());
                        voidSlash.setVoidLevel(3);
                        voidSlash.slash(this.getLookAngle(), 1.0F);
                        voidSlash.setDamage(damage);
                        this.level.addFreshEntity(voidSlash);
                    }
                }
            }
            if (this.isCurrentAnimation(CHARGE)) {
                if (this.attackTick == 18) {
                    ModNetwork.sentToTrackingEntityAndPlayer(this, new SPlayFollowSoundPacket(this, ModSounds.VHOE_CHARGE.get(), 3.0F, this.getVoicePitch(), false));
                    float f1 = (float) Math.cos(Math.toRadians(this.getYRot() + 90));
                    float f2 = (float) Math.sin(Math.toRadians(this.getYRot() + 90));
                    if (this.getTarget() != null) {
                        float r = this.distanceTo(this.getTarget());
                        r = Mth.clamp(r, 0.0F, 10.0F);
                        this.push(f1 * 0.9F * r, 0, f2 * 0.9F * r);
                    } else {
                        this.push(f1 * 3.0F, 0, f2 * 3.0F);
                    }
                }
                if (this.attackTick >= 18 && this.attackTick < 27) {
                    if (this.level instanceof ServerLevel serverLevel) {
                        int width = serverLevel.getRandom().nextIntBetweenInclusive(1, 4);
                        float height = serverLevel.getRandom().nextFloat() * 0.5F;
                        Vec3 vec3 = this.getEyePosition().offsetRandom(serverLevel.getRandom(), 2.0F);
                        Vec3 angle = this.getLookAngle().multiply(-1.0D, 1.0D, -1.0D);
                        serverLevel.sendParticles(new WindBlowParticle.Option(new ColorUtil(ChatFormatting.LIGHT_PURPLE), width, height), vec3.x, vec3.y, vec3.z, 0, angle.x, angle.y, angle.z, 1.0F);
                    }
                    for (LivingEntity entityHit : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.2D))) {
                        if (!MobUtil.areAllies(this, entityHit)) {
                            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this), damage + (entityHit.getMaxHealth() * AttributesConfig.EnderKeeperHPPercentDamage.get().floatValue()));
                            if (entityHit.isDamageSourceBlocked(this.damageSources().mobAttack(this))) {
                                MobUtil.disableShield(entityHit, 100);
                            }
                            if (flag) {
                                this.applyVoidTouched(entityHit);
                                double d0 = entityHit.getX() - this.getX();
                                double d1 = entityHit.getZ() - this.getZ();
                                double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                                entityHit.push(d0 / d2 * 2.5D, 0.18D, d1 / d2 * 2.2D);
                            }
                        }
                    }
                }
            }
            if (this.isCurrentAnimation(SPELL_1)) {
                if (this.attackTick >= 10 && this.attackTick < 35) {
                    int tickRate = MobUtil.healthIsHalved(this) ? 2 : 5;
                    if (this.attackTick % tickRate == 0) {
                        VoidShock voidShock = new VoidShock(this, this.getTarget(), this.level);
                        voidShock.setPos(this.position().add(this.level.getRandom().nextInt(-3, 3), 4.0D, this.level.getRandom().nextInt(-3, 3)));
                        voidShock.setPower(Vec3.ZERO, 10);
                        voidShock.setBaseDamage(damage);
                        this.level.addFreshEntity(voidShock);
                        this.playSound(ModSounds.TELEPORT_ORB_THROW.get(), 2.0F, this.getVoicePitch());
                    }
                }
                if (this.attackTick % 10 == 0 && this.attackTick < 35) {
                    this.playSound(ModSounds.SWIRLINGS.get(), 1.5F, this.getVoicePitch() - 0.25F);
                }
            }
            if (this.isCurrentAnimation(SPELL_2)) {
                if (this.attackTick > 10 && this.attackTick <= 28) {
                    if (this.level instanceof ServerLevel serverLevel) {
                        ColorUtil colorUtil = new ColorUtil(ChatFormatting.DARK_PURPLE);
                        ServerParticleUtil.gatheringParticles(new GatherTrailParticle.Option(colorUtil, this.position().add(0, 8, 0)), this, serverLevel, 2);
                    }
                }
                if (this.attackTick == 43) {
                    VoidShockBomb voidShock = new VoidShockBomb(this, this.level);
                    voidShock.setPos(this.position().add(0, 6.0D, 0));
                    voidShock.setBaseDamage(damage);
                    voidShock.shootFromRotation(this, this.getXRot(), this.getYRot(), 0.0F, 1.0F, 1.0F);
                    this.level.addFreshEntity(voidShock);
                    this.playSound(ModSounds.HEAVY_WOOSH.get(), 3.0F, this.getVoicePitch());
                    this.playSound(ModSounds.TELEPORT_ORB_THROW.get(), 3.0F, this.getVoicePitch() - 0.5F);
                    if (MobUtil.healthIsHalved(this)) {
                        for (int i = 0; i < 2; ++i){
                            VoidShockBomb voidShock2 = new VoidShockBomb(this, this.level);
                            voidShock2.setPos(this.position().add(0, 6.0D, 0));
                            voidShock2.setBaseDamage(damage);
                            voidShock2.shootFromRotation(this, this.getXRot(), this.getYRot(), 0.0F, 0.5F + this.level.getRandom().nextFloat(), 8.0F);
                            this.level.addFreshEntity(voidShock2);
                        }
                    }
                }
            }
            if (this.isCurrentAnimation(SPELL_3)) {
                if (this.attackTick >= 20 && this.attackTick < 60) {
                    int amount = MobUtil.healthIsHalved(this) ? 2 : 1;
                    for (int i = 0; i < amount; ++i) {
                        BlockPos blockPos = this.blockPosition();
                        if (this.attackTick < 40 && this.getTarget() != null) {
                            blockPos = this.getTarget().blockPosition();
                        }
                        BlockPos blockPos1 = blockPos.offset(this.level.getRandom().nextInt(-16, 16), 0, this.level.getRandom().nextInt(-16, 16));
                        BlockPos blockPos2 = blockPos.offset(this.level.getRandom().nextInt(-16, 16), 0, this.level.getRandom().nextInt(-16, 16));
                        Vec3 vec3 = Vec3.atBottomCenterOf(blockPos1);
                        Vec3 vec32 = Vec3.atBottomCenterOf(blockPos2);
                        VoidLightningTrap trap = new VoidLightningTrap(this.level, vec3.x, vec3.y, vec3.z);
                        trap.setOwner(this);
                        trap.setDuration(40);
                        trap.setDamage(damage);
                        if (!this.level.getEntitiesOfClass(VoidLightningTrap.class, new AABB(blockPos1)).isEmpty()) {
                            trap.setPos(vec32.x(), vec32.y(), vec32.z());
                        }
                        MobUtil.moveDownToGround(trap);
                        this.level.addFreshEntity(trap);
                    }
                }
            }
            if (this.isCurrentAnimation(LIFE_STEAL)) {
                double radius = this.getBoundingBox().getSize() * 2.0F;
                if (this.attackTick < 40) {
                    if (this.level instanceof ServerLevel serverWorld) {
                        ServerParticleUtil.gatheringParticles(ParticleTypes.PORTAL, this, serverWorld, 20);
                    }
                }
                if (this.attackTick == 40) {
                    this.playSound(ModSounds.VHOE_PINWHEEL.get(), 2.0F, 0.75F);
                }
                if (this.attackTick == 60) {
                    if (this.level instanceof ServerLevel serverWorld) {
                        ColorUtil colorUtil = new ColorUtil(ChatFormatting.LIGHT_PURPLE);
                        serverWorld.sendParticles(new AoEParticleOption(0, 4.0F / 60.0F, 4.0F, 60), this.getX(), this.getY() + 0.1F, this.getZ(), 0, colorUtil.red, colorUtil.green, colorUtil.blue, 1.0F);
                    }
                }
                if (this.attackTick >= 40 && this.attackTick < 120) {
                    this.level.broadcastEntityEvent(this, (byte) 6);
                    if (this.level instanceof ServerLevel serverWorld) {
                        ColorUtil colorUtil = new ColorUtil(ChatFormatting.LIGHT_PURPLE);
                        ServerParticleUtil.gatheringParticles(new GatherTrailParticle.Option(colorUtil, this.position().add(0, 1, 0)), this, serverWorld, 4);
                        ServerParticleUtil.windParticle(serverWorld, colorUtil, (float) radius, 1.0F, this.getId(), this.position());
                        ServerParticleUtil.gatheringParticles(ParticleTypes.PORTAL, this, serverWorld, 20);
                        if (this.attackTick % 5 == 0) {
                            serverWorld.sendParticles(new ReverseShockwaveParticleOption(colorUtil, 20.0F, 1.0F, 0, true), this.getX(), this.getY() + 0.25D, this.getZ(), 1, 0, 0, 0, 0.5F);
                        }
                    }
                    if (MobUtil.healthIsHalved(this)) {
                        if (this.attackTick % 10 == 0) {
                            VoidShock voidShock = new VoidShock(this, this.getTarget(), this.level);
                            voidShock.setPos(this.position().add(0.0D, 4.0D, 0.0D));
                            voidShock.setPower(Vec3.ZERO, 10);
                            voidShock.setBaseDamage(damage);
                            this.level.addFreshEntity(voidShock);
                            this.playSound(ModSounds.TELEPORT_ORB_THROW.get(), 2.0F, this.getVoicePitch());
                        }
                    }
                    for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(20.0F))) {
                        if (!MobUtil.areAllies(this, livingEntity) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity) && livingEntity.isAlive()) {
                            Vec3 vec30 = this.position().subtract(livingEntity.position());
                            vec30 = vec30.normalize();
                            double scale = (this.attackTick - 40) / 80.0D;
                            vec30 = vec30.scale(scale);
                            vec30 = vec30.scale(0.2D);
                            Vec3 add = livingEntity.getDeltaMovement().add(vec30);
                            MobUtil.drag(livingEntity, add.x, add.y, add.z, MobUtil.healthIsHalved(this) ? 0.0D : 0.5D);
                            if (this.distanceTo(livingEntity) < radius) {
                                float lifeSteal = ((livingEntity.getMaxHealth() - livingEntity.getHealth()) * 0.25F) + 1.0F;
                                if (livingEntity.hurt(this.damageSources().indirectMagic(this, this), lifeSteal)) {
                                    if (this.level instanceof ServerLevel serverLevel) {
                                        ColorUtil colorUtil = new ColorUtil(ChatFormatting.DARK_PURPLE);
                                        Vec3 vec3 = new Vec3(livingEntity.getX(), livingEntity.getY() + (livingEntity.getBbHeight() / 2.0F), livingEntity.getZ());
                                        Vec3 vector3d1 = new Vec3(this.getRandomX(1.0F), this.getEyeY(), this.getRandomZ(1.0F));
                                        serverLevel.sendParticles(new GatherTrailParticle.Option(colorUtil, vector3d1), vec3.x, vec3.y, vec3.z, 0, 0.0F, 0.0F, 0.0F, 0.5F);
                                        for (int i = 0; i < 8; ++i){
                                            vec3 = new Vec3(livingEntity.getRandomX(1.0F), livingEntity.getRandomY(), livingEntity.getRandomZ(1.0F));
                                            serverLevel.sendParticles(new AbsorbTrailParticleOption(vector3d1, 11141290, 10), vec3.x, vec3.y, vec3.z, 1, 0.0, 0.0, 0.0, 0.0);
                                        }
                                    }
                                    this.heal(lifeSteal);
                                    this.playSound(ModSounds.SOUL_EAT.get(), 2.0F, 1.0F);
                                }
                                Vec3 vec3 = vec30.reverse();
                                vec3 = vec3.normalize();
                                vec3 = vec3.scale(2.0D);
                                MobUtil.drag(livingEntity, vec3.x, vec3.y, vec3.z, MobUtil.healthIsHalved(this) ? 0.0D : 0.5D);
                            }
                        }
                    }
                }
                if (this.attackTick == 114) {
                    this.playSound(ModSounds.OBSIDIAN_CLAYMORE_SWING.get(), 2.0F, this.getVoicePitch());
                }
                if (this.attackTick == 119) {
                    CameraShake.cameraShake(this.level, this.position(), 15.0F, 0.1F, 0, 10);
                    this.areaAttack(6.5F, 6, 90, damage, 60, false);
                }
                if (this.attackTick == 120) {
                    if (this.level instanceof ServerLevel serverLevel) {
                        new SpellExplosion(serverLevel, this, this.damageSources().indirectMagic(this, this), this.getX(), this.getY(), this.getZ(), 4.0F, damage) {
                            @Override
                            public void explodeHurt(Entity target, DamageSource damageSource, double x, double y, double z, double seen, float actualDamage) {
                                super.explodeHurt(target, damageSource, x, y, z, seen, actualDamage);
                                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
                                    if (target instanceof LivingEntity livingEntity && !livingEntity.hasEffect(GoetyEffects.VOID_TOUCHED.get())) {
                                        livingEntity.addEffect(new MobEffectInstance(GoetyEffects.VOID_TOUCHED.get(), MathHelper.secondsToTicks(3), 2, false, true));
                                    }
                                }
                            }
                        };
                        ColorUtil colorUtil = new ColorUtil(0xb103d8);
                        serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 4.0F, 1), this.getX(), this.getY() + 0.25D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                        serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 8.0F, 1), this.getX(), this.getY() + 0.25D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                        serverLevel.sendParticles(new VerticalCircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 4.0F, 1), this.getX(), this.getY() + 0.25D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                        serverLevel.sendParticles(new VerticalCircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 8.0F, 1), this.getX(), this.getY() + 0.25D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                        DustCloudParticleOption cloudParticleOptions = new DustCloudParticleOption(new Vector3f(Vec3.fromRGB24(16733695).toVector3f()), 1.0F);
                        for (int i = 0; i < 2; ++i) {
                            ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions, this.getX(), this.getY() + 0.25D, this.getZ(), 0, 0.14D, 0, 3.5F);
                        }
                        this.playSound(ModSounds.FUNGUS_EXPLOSION.get(), 3.0F, 0.6F + (serverLevel.getRandom().nextFloat() * 0.4F));
                    }
                }
            }
            if (this.isCurrentAnimation(GROUND_POUND) || this.isCurrentAnimation(GROUND_POUND_SPIN)) {
                if (this.hidingTime >= 10) {
                    this.stopHide();
                    this.playSound(ModSounds.OBSIDIAN_CLAYMORE_SMASH.get(), 2.0F, this.getVoicePitch());
                }
                if (this.attackTick == 5) {
                    if (this.level instanceof ServerLevel serverLevel) {
                        ColorUtil colorUtil = new ColorUtil(ChatFormatting.DARK_PURPLE);
                        serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, this.groundPoundSize, 1), this.getX(), BlockFinder.moveDownToGround(this), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    }
                    CameraShake.cameraShake(this.level, this.position(), 25.0F, 0.2F, 0, 20);
                }

                for (int i = 5; i < 5 + this.groundPoundSize; ++i) {
                    if (this.attackTick == i) {
                        this.surroundTremor(i - 5, 3, 0.0F, damage);
                    }
                }

                if (this.isCurrentAnimation(GROUND_POUND_SPIN)) {
                    if (this.attackTick == 33) {
                        this.pushOn(3.0F, 3.0F);
                    }
                    if (this.attackTick == 38) {
                        this.playSound(ModSounds.SWIRLINGS.get(), 2.0F, this.getVoicePitch() - 0.25F);
                    }
                    if (this.attackTick >= 38 && this.attackTick < 58) {
                        if (this.level instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(new WindParticleOption(new ColorUtil(ChatFormatting.DARK_PURPLE), 4.5F, 1.5F, this.getId()), this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 1.0F);
                            serverLevel.sendParticles(new WindParticleOption(new ColorUtil(ChatFormatting.DARK_PURPLE), 6.0F, 1.0F, this.getId()), this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 1.0F);
                            serverLevel.sendParticles(new WindParticleOption(new ColorUtil(ChatFormatting.DARK_PURPLE), 4.5F, 0.5F, this.getId()), this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 1.0F);
                        }
                        for (LivingEntity entityHit : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(6.0D, 0.0D, 6.0D))) {
                            if (!MobUtil.areAllies(this, entityHit)) {
                                boolean flag = entityHit.hurt(this.damageSources().mobAttack(this), damage + (entityHit.getMaxHealth() * AttributesConfig.EnderKeeperHPPercentDamage.get().floatValue()));
                                if (entityHit.isDamageSourceBlocked(this.damageSources().mobAttack(this))) {
                                    MobUtil.disableShield(entityHit, 100);
                                }
                                if (flag) {
                                    this.applyVoidTouched(entityHit);
                                    double d0 = entityHit.getX() - this.getX();
                                    double d1 = entityHit.getZ() - this.getZ();
                                    double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                                    entityHit.push(d0 / d2 * 2.5D, 0.18D, d1 / d2 * 2.2D);
                                }
                            }
                        }
                    }
                }
            }
            if (this.isCurrentAnimation(BACK_AWAY)) {
                if (this.attackTick == 2) {
                    float speed = -2.5F;
                    float dodgeYaw = (float) Math.toRadians(this.getYRot() + 90);
                    Vec3 vec3 = this.getDeltaMovement().add(speed * Math.cos(dodgeYaw), 0, speed * Math.sin(dodgeYaw));
                    this.playSound(SoundEvents.PLAYER_ATTACK_CRIT, 2.0F, 1.0F);
                    this.setDeltaMovement(vec3.x, 0.4F, vec3.z);
                }
            }
            if (this.isCurrentAnimation(SLICE_START)) {
                int hideTime = MobUtil.healthIsHalved(this) ? 20 : 40;
                if (this.getTarget() == null || this.getTarget().isDeadOrDying()) {
                    this.stopHide();
                    this.setAnimationState(IDLE);
                } else if (this.hidingTime >= hideTime) {
                    this.stopHide();
                    this.setAnimationState(SLICE_1);
                }
            }
            if (this.isCurrentAnimation(SLICE_1) || this.isCurrentAnimation(SLICE_2)) {
                if (this.attackTick == 1) {
                    this.playSound(ModSounds.OBSIDIAN_CLAYMORE_WINDUP.get(), 2.0F, this.getVoicePitch());
                }
                if (this.attackTick == 5) {
                    this.playSound(ModSounds.OBSIDIAN_CLAYMORE_SWING.get(), 2.0F, this.getVoicePitch());
                }
                if (this.attackTick == 10) {
                    float pushPower = MobUtil.healthIsHalved(this) ? 3.0F : 1.35F;
                    this.pushOn(pushPower, pushPower);
                    this.areaAttack(6.5F, 5, 90, damage, 60, false);
                }
                if (this.attackTick >= 24) {
                    this.startHide();
                    if (this.getTarget() != null) {
                        this.teleportTowards(this.getTarget(), 2.0F);
                    }
                }
                if (this.getTarget() == null || this.getTarget().isDeadOrDying()) {
                    this.stopHide();
                    this.setAnimationState(IDLE);
                }
            }
        }
        if (this.getTarget() != null) {
            this.getTarget().addEffect(new MobEffectInstance(GoetyEffects.PLUNGE.get(), 5, 0, false, false));
            if (this.getTarget() instanceof Player player) {
                player.getAbilities().flying &= player.isCreative();
            }
        }
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.overrideSetTarget(target);
    }

    @Override
    public void servantTick() {
        if (this.isGuardingArea()){
            if (this.distanceToSqr(this.vec3BoundPos()) > Mth.square(64.0F) && this.getTarget() == null){
                Vec3 vec3 = this.vec3BoundPos();
                this.teleportOut();
                if (this.ownedTeleport(vec3.x, vec3.y, vec3.z)){
                    this.teleportIn();
                }
            }
        }
    }

    public void areaAttack(float range, float height, float arc, float damage, int shieldBreak, boolean knockback) {
        MobUtil.areaAttack(this, range, height, arc, damage, AttributesConfig.EnderKeeperHPPercentDamage.get().floatValue(), shieldBreak, this.damageSources().mobAttack(this), knockback, this::applyVoidTouched);
    }

    public void applyVoidTouched(Entity entity) {
        if (entity instanceof LivingEntity livingEntity && !livingEntity.hasEffect(GoetyEffects.VOID_TOUCHED.get())) {
            livingEntity.addEffect(new MobEffectInstance(GoetyEffects.VOID_TOUCHED.get(), MathHelper.secondsToTicks(5), 1, false, true));
        }
    }

    public void surroundTremor(int distance, double topY, float side, float damage) {
        int hitY = Mth.floor(this.getBoundingBox().minY - 0.5D);
        double spread = Math.PI * (double)2.0F;
        int arcLen = Mth.ceil((double)distance * spread);
        double minY = this.getY() - 1.0D;
        double maxY = this.getY() + topY;

        for(int i = 0; i < arcLen; ++i) {
            double theta = ((double)i / ((double)arcLen - 1.0D) - 0.5D) * spread;
            double vx = Math.cos(theta);
            double vz = Math.sin(theta);
            double px = this.getX() + vx * (double)distance + (double)side * Math.cos((double)(this.yBodyRot + 90.0F) * Math.PI / 180.0D);
            double pz = this.getZ() + vz * (double)distance + (double)side * Math.sin((double)(this.yBodyRot + 90.0F) * Math.PI / 180.0D);
            int hitX = Mth.floor(px);
            int hitZ = Mth.floor(pz);
            BlockPos blockPos = new BlockPos(hitX, hitY, hitZ);

            BlockState blockState = level().getBlockState(blockPos);
            int maxDepth = 30;
            for (int depthCount = 0; depthCount < maxDepth; depthCount++) {
                if (blockState.getRenderShape() == RenderShape.MODEL) {
                    break;
                }
                blockPos = blockPos.below();
                blockState = level().getBlockState(blockPos);
            }

            if (blockState.getRenderShape() != RenderShape.MODEL) {
                blockState = Blocks.AIR.defaultBlockState();
            }
            BlockState blockAbove = this.level.getBlockState(blockPos.above());

            if (blockState != Blocks.AIR.defaultBlockState() && !blockState.hasBlockEntity() && !blockAbove.blocksMotion()) {
                ModFallingBlock fallingBlock = new ModFallingBlock(this.level, Vec3.atCenterOf(blockPos.above()), blockState, (float) (0.2D + this.getRandom().nextGaussian() * 0.15D));
                this.level.addFreshEntity(fallingBlock);
            }

            AABB selection = new AABB(px - 0.5D, minY, pz - 0.5D, px + 0.5D, maxY, pz + 0.5D);
            List<LivingEntity> entities = this.level.getEntitiesOfClass(LivingEntity.class, selection);
            for (LivingEntity target : entities) {
                if (!MobUtil.areAllies(target, this) && target != this) {
                    boolean flag = target.hurt(this.damageSources().mobAttack(this), damage + (target.getMaxHealth() * AttributesConfig.EnderKeeperHPPercentDamage.get().floatValue()));
                    if (flag) {
                        this.applyVoidTouched(target);
                        double d0 = target.getX() - this.getX();
                        double d1 = target.getZ() - this.getZ();
                        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                        target.push(d0 / d2 * 2.5D, 0.18D, d1 / d2 * 2.2D);
                    }
                }
            }
        }

    }

    private void outwardTremor(int distance, float math, float damage) {
        int hitY = Mth.floor(this.getBoundingBox().minY - 0.5);
        double minY = this.getY() - 2;
        double maxY = this.getY() + (float) 3;
        float angle = (0.01745329251F * this.yBodyRot);
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double extraX = distance * Mth.sin((float) (Math.PI + angle));
        double extraZ = distance * Mth.cos(angle);
        double px = this.getX() + extraX + f * math;
        double pz = this.getZ() + extraZ + f1 * math;
        int hitX = Mth.floor(px);
        int hitZ = Mth.floor(pz);
        BlockPos pos = new BlockPos(hitX, hitY, hitZ);
        BlockState block = level().getBlockState(pos);

        int maxDepth = 30;
        for (int depthCount = 0; depthCount < maxDepth; depthCount++) {
            if (block.getRenderShape() == RenderShape.MODEL) {
                break;
            }
            pos = pos.below();
            block = level().getBlockState(pos);
        }

        if (block.getRenderShape() != RenderShape.MODEL) {
            block = Blocks.AIR.defaultBlockState();
        }
        if (!this.level().isClientSide) {
            ModFallingBlock fallingBlockEntity = new ModFallingBlock(level(), hitX + 0.5D, hitY + 1.0D, hitZ + 0.5D, block, 10);
            fallingBlockEntity.push(0, 0.2D + getRandom().nextGaussian() * 0.15D, 0);
            level().addFreshEntity(fallingBlockEntity);
            AABB selection = new AABB(px - 0.5, minY, pz - 0.5, px + 0.5, maxY, pz + 0.5);
            List<LivingEntity> hit = level().getEntitiesOfClass(LivingEntity.class, selection);
            for (LivingEntity target : hit) {
                if (!MobUtil.areAllies(target, this) && target != this) {
                    boolean flag = target.hurt(this.damageSources().mobAttack(this), damage + (target.getMaxHealth() * AttributesConfig.EnderKeeperHPPercentDamage.get().floatValue()));
                    if (flag) {
                        this.applyVoidTouched(target);
                        double d0 = target.getX() - this.getX();
                        double d1 = target.getZ() - this.getZ();
                        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                        target.push(d0 / d2 * 2.5D, 0.18D, d1 / d2 * 2.2D);
                    }
                }
            }
        }

    }

    @Override
    public void hidingTick() {
        if (!this.isHiding()) {
            this.hidingTime = 0;
        } else {
            ++this.hidingTime;
            this.getNavigation().stop();
            this.getMoveControl().strafe(0.0F, 0.0F);
            if (this.getTarget() == null || this.getTarget().isDeadOrDying()) {
                this.stopHide();
            }
        }
    }

    public void pushOn(float powerX, float powerZ) {
        float f1 = (float) Math.cos(Math.toRadians(this.getYRot() + 90));
        float f2 = (float) Math.sin(Math.toRadians(this.getYRot() + 90));
        this.push(f1 * powerX, 0, f2 * powerZ);
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    @Override
    public double getMeleeAttackRangeSqr(LivingEntity livingEntity) {
        return Mth.square(6.0D);
    }

    public boolean isWithinMeleeAttackRange(LivingEntity livingentity) {
        double d0 = this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
        return d0 <= this.getMeleeAttackRangeSqr(livingentity);
    }

    public void stopHide() {
        if (this.isHiding()) {
            this.teleportIn();
        }
        super.stopHide();
        this.refreshDimensions();
    }

    public boolean ownedTeleport(double x, double y, double z) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, y, z);

        while(blockpos$mutableblockpos.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(blockpos$mutableblockpos).blocksMotion()) {
            blockpos$mutableblockpos.move(Direction.DOWN);
        }

        BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos);
        boolean flag = blockstate.blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
        if (flag && !flag1) {
            EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(this, x, y, z);
            if (event.isCanceled()) return false;
            Vec3 vec3 = this.position();
            boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), false);
            if (flag2) {
                if (!this.level.isClientSide) {
                    ModNetwork.sendToALL(new SRepositionPacket(this.getId(), this.getX(), this.getY(), this.getZ()));
                }
                this.level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
            }

            return flag2;
        } else {
            return false;
        }
    }

    @Override
    public void teleportTowards(Entity entity, double range) {
        if (!this.level.isClientSide() && this.isAlive()) {
            if (entity == null) {
                return;
            }
            try {
                for (int i = 0; i < 128; ++i) {
                    int range2 = Mth.floor(range);
                    double d1 = entity.getX() + this.level.getRandom().nextIntBetweenInclusive(-range2, range2);
                    double d2 = entity.getY();
                    double d3 = entity.getZ() + this.level.getRandom().nextIntBetweenInclusive(-range2, range2);
                    EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(this, d1, d2, d3);
                    if (event.isCanceled()) {
                        break;
                    }
                    Vec3 vec3 = new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                    if (this.level.noCollision(this.getBoundingBox().move(vec3)) && !this.level.containsAnyLiquid(this.getBoundingBox().move(vec3))) {
                        this.teleportTo(vec3.x, vec3.y, vec3.z);
                        MobUtil.instaLook(this, entity, true);
                        break;
                    } else if (i == 127) {
                        MobUtil.instaLook(this, entity, true);
                        break;
                    }
                }
            } catch (NullPointerException ignored) {

            }
        }
    }

    public void teleportIn() {
        super.teleportIn();
        if (!this.level.isClientSide) {
            ModNetwork.sendToALL(new SRepositionPacket(this.getId(), this.getX(), this.getY(), this.getZ()));
        }
    }

    @Override
    public void handleEntityEvent(byte pByte) {
        if (pByte == 6) {
            this.shakeSword = 5;
        } else {
            super.handleEntityEvent(pByte);
        }
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.hasPose(Pose.EMERGING) ? 1 : 0);
    }

    public void recreateFromPacket(ClientboundAddEntityPacket p_219420_) {
        super.recreateFromPacket(p_219420_);
        if (p_219420_.getData() == 1) {
            this.setPose(Pose.EMERGING);
        }
    }

    public static class KeeperAttackGoal extends Goal {
        protected final EnderKeeper entity;
        protected final int currentAttackType;
        protected final int attackType;
        protected final int attackEndType;
        protected final int attackMaxTick;
        protected final int attackSeeTick;
        protected final float attackOuterRange;
        protected final float attackRange;

        public KeeperAttackGoal(EnderKeeper entity, int currentAttackType, int attackType, int attackEndType, int attackMaxTick, int attackSeeTick, float attackOuterRange, float attackRange) {
            this.entity = entity;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
            this.currentAttackType = currentAttackType;
            this.attackType = attackType;
            this.attackEndType = attackEndType;
            this.attackMaxTick = attackMaxTick;
            this.attackSeeTick = attackSeeTick;
            this.attackOuterRange = attackOuterRange;
            this.attackRange = attackRange;
        }

        public KeeperAttackGoal(EnderKeeper entity, String currentAttackType, String attackType, String attackEndType, int attackMaxTick, int attackSeeTick, float attackOuterRange, float attackRange) {
            this(entity, entity.getAnimationState(currentAttackType), entity.getAnimationState(attackType), entity.getAnimationState(attackEndType), attackMaxTick, attackSeeTick, attackOuterRange, attackRange);
        }

        public KeeperAttackGoal(EnderKeeper entity, String currentAttackType, String attackType, String attackEndType, int attackMaxTick, int attackSeeTick, float attackRange) {
            this(entity, entity.getAnimationState(currentAttackType), entity.getAnimationState(attackType), entity.getAnimationState(attackEndType), attackMaxTick, attackSeeTick, 0.0F, attackRange);
        }

        @Override
        public boolean canUse() {
            LivingEntity target = entity.getTarget();
            return target != null
                    && target.isAlive()
                    && this.entity.distanceTo(target) < this.attackRange
                    && this.entity.distanceTo(target) >= this.attackOuterRange
                    && this.entity.getCurrentAnimation() == this.currentAttackType;
        }

        @Override
        public void start() {
            this.entity.setAnimationState(this.attackType);
            this.entity.getNavigation().stop();
        }

        @Override
        public void stop() {
            this.entity.setAnimationState(this.attackEndType);
            LivingEntity target = entity.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
                this.entity.setTarget(null);
            }
            this.entity.getNavigation().stop();
            if (this.entity.getTarget() == null) {
                this.entity.setAggressive(false);
            }
            this.entity.attackTick = 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.entity.getCurrentAnimation() == this.attackType
                    && this.entity.attackTick <= this.attackMaxTick;
        }

        public void tick() {
            LivingEntity target = this.entity.getTarget();
            if (this.entity.attackTick < this.attackSeeTick && target != null) {
                MobUtil.instaLook(this.entity, target);
            } else {
                this.entity.setYRot(this.entity.yRotO);
            }
            this.entity.getNavigation().stop();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
