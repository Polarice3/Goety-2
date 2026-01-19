package com.Polarice3.Goety.common.entities.neutral.ender;

import com.Polarice3.Goety.api.entities.IHiding;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SRepositionPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public abstract class AbstractEnderling extends Summoned implements IHiding {
    private static final EntityDataAccessor<Boolean> DATA_HIDE = SynchedEntityData.defineId(AbstractEnderling.class, EntityDataSerializers.BOOLEAN);
    public int preHidingTime = 0;
    public int hidingTime = 0;
    public int teleportCool;
    public int postTeleportTick;
    public int mobHurtTime;

    public AbstractEnderling(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.addBehaviourGoals();
    }

    public void targetRetaliateGoal() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, AbstractEnderling.class){
            protected void alertOther(Mob other, LivingEntity target) {
                if (this.mob.isAlliedTo(other)) {
                    other.setTarget(target);
                }
            }
        }.setAlertOthers());
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new FollowOwnerGoal<>(this, 1.0D, 10.0F, 2.0F){
            @Override
            protected boolean canTeleport() {
                return this.summonedEntity.teleportCool <= 0;
            }

            protected boolean tryToTeleportToLocation(int x, int y, int z) {
                if (Math.abs((double)x - this.owner.getX()) < 2.0D && Math.abs((double)z - this.owner.getZ()) < 2.0D) {
                    return false;
                } else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
                    return false;
                } else {
                    this.summonedEntity.teleportHits();
                    this.summonedEntity.moveTo((double)x + 0.5D, (double)y, (double)z + 0.5D, this.summonedEntity.getYRot(), this.summonedEntity.getXRot());
                    this.summonedEntity.teleportCool = MathHelper.secondsToTicks(10);
                    this.navigation.stop();
                    return true;
                }
            }
        });
    }

    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_HIDE, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("HidingTime")) {
            this.hidingTime = compound.getInt("HidingTime");
        }
        if (compound.contains("TeleportCool")) {
            this.teleportCool = compound.getInt("TeleportCool");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("HidingTime", this.hidingTime);
        compound.putInt("TeleportCool", this.teleportCool);
    }

    @Override
    public boolean isAlliedTo(Entity entityIn) {
        if (this.getTrueOwner() == null) {
            if (entityIn instanceof EnderMan){
                return this.getTeam() == null && entityIn.getTeam() == null;
            } else if (entityIn instanceof AbstractEnderling enderling){
                if (this.getTeam() == null && entityIn.getTeam() == null) {
                    return (this.isHostile() && enderling.isHostile()) || (!this.isHostile() && !enderling.isHostile());
                }
            }
        }
        return super.isAlliedTo(entityIn);
    }

    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public void die(DamageSource pCause) {
        if (this.isHiding()) {
            this.stopHide();
        }
        super.die(pCause);
    }

    protected float getStandingEyeHeight(Pose p_32517_, EntityDimensions p_32518_) {
        if (this.isHiding()){
            return 0.1F;
        } else {
            return super.getStandingEyeHeight(p_32517_, p_32518_);
        }
    }

    @Override
    public boolean isInvisible() {
        return super.isInvisible() || this.isHiding();
    }

    public boolean displayFireAnimation() {
        return !this.isHiding() && super.displayFireAnimation();
    }

    @Override
    public boolean isAttackable() {
        return super.isAttackable() && !this.isHiding();
    }

    @Override
    public boolean isSpectator() {
        return super.isSpectator() || this.isHiding();
    }

    @Override
    public boolean isPickable() {
        return super.isPickable() && !this.isHiding();
    }

    @Override
    public boolean isInvisibleTo(Player p_20178_) {
        if (this.isHiding()){
            return true;
        } else {
            return super.isInvisibleTo(p_20178_);
        }
    }

    @Override
    public boolean isInvulnerable() {
        return super.isInvulnerable() || this.isHiding();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource p_20122_) {
        return super.isInvulnerableTo(p_20122_) || this.isHiding();
    }

    public boolean canBeSeenByAnyone() {
        return super.canBeSeenByAnyone() && !this.isHiding();
    }

    public boolean isHiding() {
        return this.entityData.get(DATA_HIDE);
    }

    public void setHide(boolean hide) {
        this.entityData.set(DATA_HIDE, hide);
    }

    @Override
    public void playAmbientSound() {
        if (!this.isHiding()) {
            super.playAmbientSound();
        }
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        if (!this.isHiding()) {
            this.stepSound();
        }
    }

    public void stepSound() {
    }

    @Override
    public void tick() {
        super.tick();
        if (this.teleportCool > 0) {
            --this.teleportCool;
        }
        if (this.postTeleportTick > 0) {
            --this.postTeleportTick;
            this.getNavigation().stop();
        }
        if (this.mobHurtTime > 0) {
            --this.mobHurtTime;
        }
        if (!this.level.isClientSide) {
            this.hidingTick();
        }
    }

    public void hidingTick() {
        if (!this.isHiding()) {
            this.hidingTime = 0;
            if (this.level instanceof ServerLevel serverLevel) {
                if (this.isGuardingArea()) {
                    if (this.distanceToSqr(this.vec3BoundPos()) > Mth.square(GUARDING_RANGE)) {
                        BlockPos blockPos = BlockFinder.SummonRadius(this.getBoundPos(), this, serverLevel);
                        Optional<Vec3> optional = RespawnAnchorBlock.findStandUpPosition(this.getType(), serverLevel, blockPos);
                        optional.ifPresent(this::ownedTeleport);
                        this.refreshDimensions();
                    }
                }
            }
        } else {
            ++this.hidingTime;
            this.getNavigation().stop();
            this.getMoveControl().strafe(0.0F, 0.0F);
            if (this.hidingTime >= this.getHidingDuration() || this.shouldStopHiding()) {
                this.stopHide();
                this.teleportAfterHiding();
                this.refreshDimensions();
            }
        }
    }

    public void startHide() {
        if (!this.isHiding()) {
            this.teleportOut();
        }
        this.setHide(true);
        this.level.broadcastEntityEvent(this, (byte) 4);
        this.refreshDimensions();
    }

    public void stopHide() {
        this.setHide(false);
        this.level.broadcastEntityEvent(this, (byte) 5);
        this.refreshDimensions();
    }

    public int getHidingDuration() {
        return 0;
    }

    public boolean shouldStopHiding(){
        return false;
    }

    public void teleportAfterHiding() {
    }

    @Override
    public EntityDimensions getDimensions(Pose p_21047_) {
        if (this.isHiding()){
            return EntityDimensions.scalable(0.1F, 0.1F);
        } else {
            return super.getDimensions(p_21047_);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (source.getEntity() instanceof LivingEntity
                    || source.is(DamageTypeTags.IS_PROJECTILE)) {
                this.mobHurtTime = 10;
            }
            boolean flag = source.getDirectEntity() instanceof ThrownPotion;
            boolean flag1 = flag && this.hurtWithCleanWater(source, (ThrownPotion)source.getDirectEntity(), amount);

            if (!this.isHiding()) {
                if (flag1
                        || source.is(DamageTypeTags.IS_DROWNING)
                        || source.is(DamageTypes.IN_WALL)) {
                    if (this.teleportHurt()) {
                        return true;
                    }
                }
            }
        }
        return super.hurt(source, amount);
    }

    public boolean teleportHurt() {
        if (!this.isStaying()) {
            if (this.teleportCool <= 0) {
                for (int i = 0; i < 64; ++i) {
                    if (this.teleport()) {
                        this.teleportCool = MathHelper.secondsToTicks(10);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hurtWithCleanWater(DamageSource p_186273_, ThrownPotion p_186274_, float p_186275_) {
        ItemStack itemstack = p_186274_.getItem();
        Potion potion = PotionUtils.getPotion(itemstack);
        List<MobEffectInstance> list = PotionUtils.getMobEffects(itemstack);
        boolean flag = potion == Potions.WATER && list.isEmpty();
        return flag ? super.hurt(p_186273_, p_186275_) : false;
    }

    protected boolean teleport() {
        return this.teleport(64.0D);
    }

    protected boolean teleport(double range) {
        if (!this.level.isClientSide() && this.isAlive()) {
            double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * range;
            double d1 = this.getY() + (RandomUtil.nextInt(this.random, Mth.floor(range)) - (range / 2.0D));
            double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * range;
            return this.ownedTeleport(d0, d1, d2);
        } else {
            return false;
        }
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
            net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(this, x, y, z);
            if (event.isCanceled()) return false;
            Vec3 vec3 = this.position();
            boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), false);
            if (flag2) {
                if (!this.level.isClientSide) {
                    ModNetwork.sendToALL(new SRepositionPacket(this.getId(), this.getX(), this.getY(), this.getZ()));
                }
                this.level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
                if (this.getHidingDuration() > 0) {
                    this.teleportIn();
                } else {
                    this.teleportHits();
                }
            }

            return flag2;
        } else {
            return false;
        }
    }

    public void teleportTowards(Entity entity, double range) {
        if (!this.level.isClientSide() && this.isAlive()) {
            if (entity == null) {
                this.teleportIn();
                return;
            }
            try {
                for (int i = 0; i < 128; ++i) {
                    Vec3 vector3d = new Vec3(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(), this.getZ() - entity.getZ());
                    vector3d = vector3d.normalize();
                    double d1 = this.getX() + (this.getRandom().nextDouble() - 0.5D) * (range / 2.0D) - vector3d.x * range;
                    double d2 = this.getY() + (RandomUtil.nextInt(this.getRandom(), Mth.floor(range)) - (range / 2.0D)) - vector3d.y * range;
                    double d3 = this.getZ() + (this.getRandom().nextDouble() - 0.5D) * (range / 2.0D) - vector3d.z * range;
                    net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(this, d1, d2, d3);
                    if (event.isCanceled()) {
                        if (this.getHidingDuration() > 0) {
                            this.teleportIn();
                        } else {
                            this.teleportHits();
                        }
                        break;
                    }
                    boolean flag = true;
                    boolean teleport = false;
                    if (this.getTarget() != null) {
                        if (!BlockFinder.canSeeBlock(this.getTarget(), new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ()))) {
                            flag = false;
                        }
                    }
                    if (this.isGuardingArea()) {
                        if (this.getSpawnType() == MobSpawnType.SPAWNER) {
                            if (!BlockFinder.canSeeBlock(this, this.vec3BoundPos())) {
                                flag = false;
                            }
                        }
                    }
                    if (flag) {
                        teleport = this.ownedTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                    }
                    if (teleport) {
                        MobUtil.instaLook(this, entity);
                        break;
                    } else if (i == 127) {
                        MobUtil.instaLook(this, entity);
                        if (this.getHidingDuration() > 0) {
                            this.teleportIn();
                        } else {
                            this.teleportHits();
                        }
                        break;
                    }
                }
            } catch (NullPointerException exception) {
                this.teleportIn();
            }
        }
    }

    protected boolean teleportAway(Entity entity, double range) {
        if (!this.level.isClientSide() && this.isAlive()) {
            if (entity == null) {
                this.teleportIn();
                return false;
            }
            boolean spawner = false;
            if (this.isGuardingArea()) {
                if (this.getSpawnType() == MobSpawnType.SPAWNER) {
                    spawner = true;
                    range /= 2.0F;
                }
            }
            try {
                for (int i = 0; i < 128; ++i) {
                    double d0 = entity.getX() + (this.random.nextDouble() - 0.5D) * range;
                    double d1 = entity.getY() + (RandomUtil.nextInt(this.random, Mth.floor(range)) - (range / 2.0D));
                    double d2 = entity.getZ() + (this.random.nextDouble() - 0.5D) * range;
                    Vec3 vec3 = new Vec3(d0, d1, d2);
                    boolean flag = vec3.distanceTo(entity.position()) >= range;
                    if (this.getTarget() != null) {
                        if (!BlockFinder.canSeeBlock(this.getTarget(), vec3)) {
                            flag = false;
                        }
                    }
                    if (spawner) {
                        if (!BlockFinder.canSeeBlock(this, this.vec3BoundPos())) {
                            flag = false;
                            if (i >= 120) {
                                if (this.level instanceof ServerLevel serverLevel) {
                                    Optional<Vec3> optional = RespawnAnchorBlock.findStandUpPosition(this.getType(), serverLevel, this.getBoundPos());
                                    if (optional.isPresent()) {
                                        return this.ownedTeleport(optional.get());
                                    }
                                }
                            }
                        }
                    }
                    if ((flag) || i == 127) {
                        return this.ownedTeleport(vec3.x, vec3.y, vec3.z);
                    }
                }
            } catch (NullPointerException exception) {
                this.teleportIn();
                return false;
            }
        }
        return false;
    }

    @Override
    public void teleportHits() {
        this.level.broadcastEntityEvent(this, (byte) 46);
        if (!this.isSilent()) {
            this.level.playSound(null, this.xo, this.yo, this.zo, ModSounds.ENDERLING_TELEPORT_OUT.get(), this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(ModSounds.ENDERLING_TELEPORT_IN.get(), 1.0F, 1.0F);
        }
    }

    public void teleportOut() {
        if (!this.isSilent()) {
            this.playSound(ModSounds.ENDERLING_TELEPORT_OUT.get(), 1.0F, 1.0F);
        }
        this.serverTeleportParticles();
    }

    public void teleportIn() {
        if (!this.isSilent()) {
            this.playSound(ModSounds.ENDERLING_TELEPORT_IN.get(), 1.0F, 1.0F);
        }
        this.serverTeleportParticles();
    }

    public void serverTeleportParticles() {
        if (this.level instanceof ServerLevel serverLevel) {
            int i = 16;

            for(int j = 0; j < i; ++j) {
                ColorUtil colorUtil = new ColorUtil(0xf6d1f0);
                double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() * 2.0D;
                double d2 = this.getY() + this.random.nextDouble() * (double)this.getBbHeight();
                double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() * 2.0D;
                serverLevel.sendParticles(ModParticleTypes.SMALL_SPELL_SQUARE.get(), d1, d2, d3, 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte pByte) {
        if (pByte == 4){
            this.setHide(true);
        } else if (pByte == 5){
            this.setHide(false);
        } else if (pByte == 46) {
            int i = 16;

            for(int j = 0; j < i; ++j) {
                double d0 = (double)j / (i - 1);
                ColorUtil colorUtil = new ColorUtil(0xf6d1f0);
                double d1 = Mth.lerp(d0, this.xo, this.getX()) + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() * 2.0D;
                double d2 = Mth.lerp(d0, this.yo, this.getY()) + this.random.nextDouble() * (double)this.getBbHeight();
                double d3 = Mth.lerp(d0, this.zo, this.getZ()) + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() * 2.0D;
                this.level.addParticle(ModParticleTypes.SMALL_SPELL_SQUARE.get(), d1, d2, d3, colorUtil.red(), colorUtil.green(), colorUtil.blue());
            }
        } else {
            super.handleEntityEvent(pByte);
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand p_230254_2_) {
        if (!this.level.isClientSide){
            ItemStack itemstack = pPlayer.getItemInHand(p_230254_2_);
            Item item = itemstack.getItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if (item == Items.CHORUS_FRUIT && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.playSound(this.getAmbientSound() != null ? this.getAmbientSound() : SoundEvents.GENERIC_EAT, 1.0F, 1.25F);
                    this.heal(5.0F);
                    if (this.level instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    pPlayer.swing(p_230254_2_);
                    return InteractionResult.CONSUME;
                }
            }
        }
        return super.mobInteract(pPlayer, p_230254_2_);
    }
}
