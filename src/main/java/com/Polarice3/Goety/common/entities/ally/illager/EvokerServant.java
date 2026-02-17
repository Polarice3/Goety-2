package com.Polarice3.Goety.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.ally.illager.raider.AllyVex;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.entities.ally.undead.bound.BoundEvoker;
import com.Polarice3.Goety.common.entities.projectiles.Fangs;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EvokerServant extends SpellcasterIllagerServant{
    @Nullable
    private Sheep wololoTarget;
    @Nullable
    private Mob ravageTarget;
    private int ravageCool;

    public EvokerServant(EntityType<? extends EvokerServant> p_32627_, Level p_32628_) {
        super(p_32627_, p_32628_);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new EvokerCastingSpellGoal());
        this.goalSelector.addGoal(2, new AvoidTargetGoal<>(this, LivingEntity.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(4, new EvokerSummonSpellGoal());
        this.goalSelector.addGoal(5, new EvokerAttackSpellGoal());
        this.goalSelector.addGoal(6, new EvokerWololoSpellGoal());
        this.goalSelector.addGoal(6, new EvokerRavagingSpellGoal());
    }

    @Override
    public void miscGoal() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new RaiderWanderGoal<>(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.EvokerServantFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.EvokerServantArmor.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.EvokerServantHealth.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.EvokerServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.EvokerServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.EvokerServantFollowRange.get());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.ravageCool = compound.getInt("RavageCool");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("RavageCool", this.ravageCool);
    }

    @Override
    public int xpReward() {
        return 10;
    }

    @Override
    public void die(DamageSource pCause) {
        if (!this.level.isClientSide) {
            if (this.getIdol() == null) {
                if (this.getTrueOwner() != null) {
                    if (CuriosFinder.hasNamelessSet(this.getTrueOwner())){
                        BoundEvoker servant = this.convertTo(ModEntityType.BOUND_EVOKER.get(), true);
                        if (servant != null) {
                            servant.setTrueOwner(this.getTrueOwner());
                            net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, servant);
                            if (!this.isSilent()) {
                                this.level.levelEvent((Player)null, 1026, this.blockPosition(), 0);
                            }
                        }
                    }
                }
            }
        }
        super.die(pCause);
    }

    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource p_32654_) {
        return SoundEvents.EVOKER_HURT;
    }

    void setWololoTarget(@Nullable Sheep p_32635_) {
        this.wololoTarget = p_32635_;
    }

    @Nullable
    Sheep getWololoTarget() {
        return this.wololoTarget;
    }

    void setRavageTarget(@Nullable Mob p_32635_) {
        this.ravageTarget = p_32635_;
    }

    @Nullable
    Mob getRavageTarget() {
        return this.ravageTarget;
    }

    @Override
    public boolean validLootToStore(ItemStack itemStack) {
        return super.validLootToStore(itemStack) && !itemStack.is(ModItems.OMINOUS_SADDLE.get());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.ravageCool > 0) {
            --this.ravageCool;
        }
    }

    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    class EvokerAttackSpellGoal extends SpellcasterUseSpellGoal {

        @Override
        public boolean canUse() {
            LivingEntity livingentity = EvokerServant.this.getTarget();
            if (super.canUse()) {
                if (livingentity != null) {
                    if (livingentity.distanceTo(EvokerServant.this) <= 12.5F) {
                        return true;
                    } else {
                        EvokerServant.this.getNavigation().moveTo(livingentity, 0.75D);
                    }
                }
            }
            return false;
        }

        protected int getCastingTime() {
            return 40;
        }

        protected int getCastingInterval() {
            return 100;
        }

        protected void performSpellCasting() {
            LivingEntity livingentity = EvokerServant.this.getTarget();
            if (livingentity == null){
                return;
            }
            double d0 = Math.min(livingentity.getY(), EvokerServant.this.getY());
            double d1 = Math.max(livingentity.getY(), EvokerServant.this.getY()) + 1.0D;
            float f = (float) Mth.atan2(livingentity.getZ() - EvokerServant.this.getZ(), livingentity.getX() - EvokerServant.this.getX());
            if (EvokerServant.this.distanceToSqr(livingentity) < 9.0D) {
                for(int i = 0; i < 5; ++i) {
                    float f1 = f + (float)i * (float)Math.PI * 0.4F;
                    this.createSpellEntity(EvokerServant.this.getX() + (double)Mth.cos(f1) * 1.5D, EvokerServant.this.getZ() + (double)Mth.sin(f1) * 1.5D, d0, d1, f1, 0);
                }

                for(int k = 0; k < 8; ++k) {
                    float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                    this.createSpellEntity(EvokerServant.this.getX() + (double)Mth.cos(f2) * 2.5D, EvokerServant.this.getZ() + (double)Mth.sin(f2) * 2.5D, d0, d1, f2, 3);
                }
            } else {
                for(int l = 0; l < 16; ++l) {
                    double d2 = 1.25D * (double)(l + 1);
                    this.createSpellEntity(EvokerServant.this.getX() + (double)Mth.cos(f) * d2, EvokerServant.this.getZ() + (double)Mth.sin(f) * d2, d0, d1, f, l);
                }
            }

        }

        private void createSpellEntity(double p_32673_, double p_32674_, double p_32675_, double p_32676_, float p_32677_, int p_32678_) {
            BlockPos blockpos = BlockPos.containing(p_32673_, p_32676_, p_32674_);
            boolean flag = false;
            double d0 = 0.0D;

            do {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = EvokerServant.this.level.getBlockState(blockpos1);
                if (blockstate.isFaceSturdy(EvokerServant.this.level, blockpos1, Direction.UP)) {
                    if (!EvokerServant.this.level.isEmptyBlock(blockpos)) {
                        BlockState blockstate1 = EvokerServant.this.level.getBlockState(blockpos);
                        VoxelShape voxelshape = blockstate1.getCollisionShape(EvokerServant.this.level, blockpos);
                        if (!voxelshape.isEmpty()) {
                            d0 = voxelshape.max(Direction.Axis.Y);
                        }
                    }

                    flag = true;
                    break;
                }

                blockpos = blockpos.below();
            } while(blockpos.getY() >= Mth.floor(p_32675_) - 1);

            if (flag) {
                EvokerServant.this.level.addFreshEntity(new Fangs(EvokerServant.this.level, p_32673_, (double)blockpos.getY() + d0, p_32674_, p_32677_, p_32678_, EvokerServant.this));
            }

        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.FANGS;
        }
    }

    class EvokerCastingSpellGoal extends SpellcasterCastingSpellGoal {
        public void tick() {
            if (EvokerServant.this.getTarget() != null) {
                EvokerServant.this.getLookControl().setLookAt(EvokerServant.this.getTarget(), (float)EvokerServant.this.getMaxHeadYRot(), (float)EvokerServant.this.getMaxHeadXRot());
            } else if (EvokerServant.this.getWololoTarget() != null) {
                EvokerServant.this.getLookControl().setLookAt(EvokerServant.this.getWololoTarget(), (float)EvokerServant.this.getMaxHeadYRot(), (float)EvokerServant.this.getMaxHeadXRot());
            }

        }
    }

    class EvokerSummonSpellGoal extends SpellcasterUseSpellGoal {
        private final TargetingConditions vexCountTargeting = TargetingConditions.forNonCombat().range(16.0D).ignoreLineOfSight().ignoreInvisibilityTesting();

        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else {
                int i = EvokerServant.this.level.getNearbyEntities(AllyVex.class, this.vexCountTargeting, EvokerServant.this, EvokerServant.this.getBoundingBox().inflate(16.0D)).size();
                return EvokerServant.this.random.nextInt(8) + 1 > i;
            }
        }

        protected int getCastingTime() {
            return 100;
        }

        protected int getCastingInterval() {
            return 340;
        }

        protected void performSpellCasting() {
            ServerLevel serverlevel = (ServerLevel)EvokerServant.this.level;

            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos = EvokerServant.this.blockPosition().offset(-2 + EvokerServant.this.random.nextInt(5), 1, -2 + EvokerServant.this.random.nextInt(5));
                AllyVex vex = ModEntityType.VEX_SERVANT.get().create(EvokerServant.this.level());
                if (vex != null) {
                    vex.moveTo(blockpos, 0.0F, 0.0F);
                    vex.finalizeSpawn(serverlevel, EvokerServant.this.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);
                    vex.setTrueOwner(EvokerServant.this);
                    vex.setBoundOrigin(blockpos);
                    vex.setLimitedLife(20 * (30 + EvokerServant.this.random.nextInt(90)));
                    serverlevel.addFreshEntityWithPassengers(vex);
                }
            }

        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.SUMMON_VEX;
        }
    }

    public class EvokerWololoSpellGoal extends SpellcasterUseSpellGoal {
        private final TargetingConditions wololoTargeting = TargetingConditions.forNonCombat().range(16.0D).selector((p_32710_) -> {
            return ((Sheep)p_32710_).getColor() == DyeColor.BLUE;
        });

        public boolean canUse() {
            if (EvokerServant.this.getTarget() != null) {
                return false;
            } else if (EvokerServant.this.isCastingSpell()) {
                return false;
            } else if (EvokerServant.this.tickCount < this.nextAttackTickCount) {
                return false;
            } else if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(EvokerServant.this.level, EvokerServant.this)) {
                return false;
            } else {
                List<Sheep> list = EvokerServant.this.level.getNearbyEntities(Sheep.class, this.wololoTargeting, EvokerServant.this, EvokerServant.this.getBoundingBox().inflate(16.0D, 4.0D, 16.0D));
                if (list.isEmpty()) {
                    return false;
                } else {
                    EvokerServant.this.setWololoTarget(list.get(EvokerServant.this.random.nextInt(list.size())));
                    return true;
                }
            }
        }

        public boolean canContinueToUse() {
            return EvokerServant.this.getWololoTarget() != null && this.attackWarmupDelay > 0;
        }

        public void stop() {
            super.stop();
            EvokerServant.this.setWololoTarget((Sheep)null);
        }

        protected void performSpellCasting() {
            Sheep sheep = EvokerServant.this.getWololoTarget();
            if (sheep != null && sheep.isAlive()) {
                sheep.setColor(DyeColor.RED);
            }

        }

        protected int getCastWarmupTime() {
            return 40;
        }

        protected int getCastingTime() {
            return 60;
        }

        protected int getCastingInterval() {
            return 140;
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_WOLOLO;
        }

        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.WOLOLO;
        }
    }

    public class EvokerRavagingSpellGoal extends SpellcasterUseSpellGoal {
        private final TargetingConditions ravageTargeting = TargetingConditions.forNonCombat().range(16.0D).selector((p_32710_) -> {
            return p_32710_.getType().is(ModTags.EntityTypes.VILLAGERS);
        });

        public boolean canUse() {
            if (!EvokerServant.this.isLeader()) {
                return false;
            } else if (EvokerServant.this.getTarget() != null) {
                return false;
            } else if (EvokerServant.this.isCastingSpell()) {
                return false;
            } else if (EvokerServant.this.tickCount < this.nextAttackTickCount) {
                return false;
            } else if (EvokerServant.this.ravageCool > 0) {
                return false;
            } else if (EvokerServant.this.getNearbyCompanions().isEmpty()) {
                return false;
            } else if (this.otherEvokers().size() < 2) {
                return false;
            } else {
                if (EvokerServant.this.getTrueOwner() instanceof Player player) {
                    if (!SEHelper.hasResearch(player, ResearchList.RAVAGING)){
                        return false;
                    } else if (EvokerServant.this.isGrudgedTowardsType(EntityType.VILLAGER)) {
                        List<Mob> list = EvokerServant.this.level.getNearbyEntities(Mob.class, this.ravageTargeting, EvokerServant.this, EvokerServant.this.getBoundingBox().inflate(16.0D, 4.0D, 16.0D));
                        if (list.isEmpty()) {
                            return false;
                        } else {
                            EvokerServant.this.setRavageTarget(list.get(EvokerServant.this.random.nextInt(list.size())));
                            return true;
                        }
                    } else if (EvokerServant.this.getCommandPosEntity() instanceof Villager villager && villager.distanceTo(EvokerServant.this) <= 16.0D) {
                        EvokerServant.this.setRavageTarget(villager);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        public boolean canContinueToUse() {
            return EvokerServant.this.getRavageTarget() != null
                    && EvokerServant.this.getRavageTarget().isAlive()
                    && this.otherEvokers().size() >= 2
                    && EvokerServant.this.getTarget() == null
                    && this.attackWarmupDelay > 0;
        }

        @Override
        public void tick() {
            super.tick();
            Mob victim = EvokerServant.this.getRavageTarget();
            if (victim != null && victim.isAlive()) {
                MobUtil.instaLook(EvokerServant.this, victim);
                MiscCapHelper.setShakeTime(victim, 20);
                victim.setLastHurtByMob(EvokerServant.this);
                victim.getNavigation().stop();
                victim.getMoveControl().strafe(0.0F, 0.0F);
                if (victim.tickCount % 20 == 0) {
                    if (victim.level instanceof ServerLevel serverLevel){
                        ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.ENCHANT, victim);
                    }
                }
                Vec3 offset = new Vec3(2, 0, 0);
                Vec3 at = this.groundOf(victim.position().add(offset));
                EvokerServant.this.getNavigation().moveTo(at.x, at.y, at.z, 0.75F);
                for (int i = 0; i < this.otherEvokers().size(); ++i) {
                    EvokerServant evokerServant = this.otherEvokers().get(i);
                    float f = (float) (i + 1) / (this.otherEvokers().size() + 1);
                    Vec3 offset2 = new Vec3(2, 0, 0).yRot(f * ((float) Math.PI * 2F));
                    Vec3 at2 = this.groundOf(victim.position().add(offset2));
                    evokerServant.getNavigation().moveTo(at2.x, at2.y, at2.z, 0.75F);
                    MobUtil.instaLook(evokerServant, victim);
                    evokerServant.setIsCastingSpell(IllagerServantSpell.RAVAGING);
                    evokerServant.spellCastingTickCount = 20;
                }
            }
        }

        public void stop() {
            super.stop();
            this.attackWarmupDelay = 0;
            EvokerServant.this.spellCastingTickCount = 0;
            EvokerServant.this.setIsCastingSpell(IllagerServantSpell.NONE);
            EvokerServant.this.setRavageTarget(null);
        }

        private Vec3 groundOf(Vec3 in) {
            BlockPos origin = BlockPos.containing(in);
            BlockPos.MutableBlockPos blockPos = origin.mutable();
            while (!EvokerServant.this.level.isEmptyBlock(blockPos) && blockPos.getY() < EvokerServant.this.level.getMaxBuildHeight()) {
                blockPos.move(0, 1, 0);
            }
            while (EvokerServant.this.level.isEmptyBlock(blockPos.below()) && blockPos.getY() > EvokerServant.this.level.getMinBuildHeight()) {
                blockPos.move(0, -1, 0);
            }
            return new Vec3(in.x, blockPos.getY(), in.z);
        }

        protected void performSpellCasting() {
            Mob victim = EvokerServant.this.getRavageTarget();
            if (victim != null && victim.isAlive()) {
                Player player = null;
                if (EvokerServant.this.getTrueOwner() instanceof Player player1) {
                    player = player1;
                }
                Entity entity = MobUtil.convertTo(victim, ModEntityType.RAVAGED.get(), true, player);
                if (entity instanceof Mob mob){
                    mob.setYHeadRot(victim.getYHeadRot());
                    mob.setYRot(victim.getYRot());
                    mob.spawnAnim();
                }
                EvokerServant.this.ravageCool = MathHelper.secondsToTicks(MobsConfig.EvokerServantRavagedCooldown.get());
                for (EvokerServant evokerServant : this.otherEvokers()) {
                    evokerServant.ravageCool = MathHelper.secondsToTicks(MobsConfig.EvokerServantRavagedCooldown.get());
                }
            }

        }

        public List<EvokerServant> otherEvokers() {
            List<EvokerServant> servants = new ArrayList<>();
            for (RaiderServant raider : EvokerServant.this.getNearbyCompanions()) {
                if (raider instanceof EvokerServant evokerServant) {
                    if (evokerServant.ravageCool <= 0
                            && evokerServant.getTarget() == null
                            && (!evokerServant.isCastingSpell()
                            || evokerServant.getCurrentSpell() == IllagerServantSpell.RAVAGING)) {
                        servants.add(evokerServant);
                    }
                }
            }
            return servants;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        protected int getCastWarmupTime() {
            return MathHelper.secondsToTicks(30);
        }

        protected int getCastingTime() {
            return MathHelper.secondsToTicks(31);
        }

        protected int getCastingInterval() {
            return 140;
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_WOLOLO;
        }

        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.RAVAGING;
        }
    }
}
