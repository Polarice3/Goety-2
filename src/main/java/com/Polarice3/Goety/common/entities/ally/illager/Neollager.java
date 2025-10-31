package com.Polarice3.Goety.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class Neollager extends AbstractIllagerServant {
    protected static final EntityDataAccessor<Boolean> DATA_BABY_ID = SynchedEntityData.defineId(Neollager.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> IS_MAGIC = SynchedEntityData.defineId(Neollager.class, EntityDataSerializers.BOOLEAN);
    protected int age;
    protected int forcedAge;
    protected int forcedAgeTimer;

    public Neollager(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new LoomGoal(this));
        this.goalSelector.addGoal(2, new AvoidTargetGoal<>(this, LivingEntity.class, 8.0F, 0.6D, 1.0D));
    }

    public void miscGoal() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new RaiderWanderGoal<>(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.NeollagerHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.NeollagerArmor.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.NeollagerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.NeollagerArmor.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_BABY_ID, false);
        this.entityData.define(IS_MAGIC, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Magic")){
            this.setMagic(compound.getBoolean("Magic"));
        }
        if (compound.contains("Age")){
            this.setAge(compound.getInt("Age"));
        }
        if (compound.contains("ForcedAge")){
            this.forcedAge = compound.getInt("ForcedAge");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Magic", this.isMagic());
        compound.putInt("Age", this.getAge());
        compound.putInt("ForcedAge", this.forcedAge);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_146754_) {
        if (DATA_BABY_ID.equals(p_146754_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_146754_);
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PILLAGER_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_35498_) {
        return SoundEvents.PILLAGER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PILLAGER_DEATH;
    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    public boolean canJoinPatrol() {
        return false;
    }

    public int getAge() {
        if (this.level().isClientSide) {
            return this.entityData.get(DATA_BABY_ID) ? -1 : 1;
        } else {
            return this.age;
        }
    }

    public void ageUp(int seconds, boolean particles) {
        int i = this.getAge();
        i += seconds * 20;
        if (i > 0) {
            i = 0;
        }

        int j = i - i;
        this.setAge(i);
        if (particles) {
            this.forcedAge += j;
            if (this.forcedAgeTimer == 0) {
                this.forcedAgeTimer = 40;
            }
        }

        if (this.getAge() == 0) {
            this.setAge(this.forcedAge);
        }

    }

    public void ageUp(int seconds) {
        this.ageUp(seconds, false);
    }

    public void setAge(int age) {
        int i = this.getAge();
        this.age = age;
        if (i < 0 && age >= 0 || i >= 0 && age < 0) {
            this.entityData.set(DATA_BABY_ID, age < 0);
            this.ageBoundaryReached();
        }

    }

    public boolean isBaby() {
        return this.getAge() < 0;
    }

    public void setBaby(boolean p_146756_) {
        this.setAge(p_146756_ ? -24000 : 0);
    }

    public void setMagic(boolean magic){
        this.entityData.set(IS_MAGIC, magic);
    }

    public boolean isMagic(){
        return this.entityData.get(IS_MAGIC);
    }

    @Override
    public boolean canTrain(Level level, BlockPos blockPos, EntityType<? extends Mob> entityType) {
        Mob mob = entityType.create(this.level);
        if (super.canTrain(level, blockPos, entityType)) {
            if (mob instanceof SpellcasterIllagerServant) {
                return this.isMagic();
            } else {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData data = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (pLevel.getLevel().getRandom().nextFloat() <= 0.15F){
            this.setMagic(true);
        }
        return data;
    }

    @Override
    public void convertNewEquipment(Entity entity){
        if (entity instanceof Villager villager) {
            VillagerProfession profession = villager.getVillagerData().getProfession();
            if (profession == VillagerProfession.CLERIC || profession == VillagerProfession.LIBRARIAN){
                this.setMagic(true);
            }
        }
    }

    public void tick(){
        super.tick();
        if (this.level.isClientSide) {
            if (this.forcedAgeTimer > 0) {
                if (this.forcedAgeTimer % 4 == 0) {
                    this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
                }

                --this.forcedAgeTimer;
            }
        } else if (this.isAlive()) {
            int i = this.getAge();
            if (i < 0) {
                ++i;
                this.setAge(i);
            } else if (i > 0) {
                --i;
                this.setAge(i);
            }
        }
    }

    protected void ageBoundaryReached() {
        if (!this.isBaby() && this.isPassenger()) {
            Entity entity = this.getVehicle();
            if (entity instanceof Boat boat) {
                if (!boat.hasEnoughSpaceFor(this)) {
                    this.stopRiding();
                }
            }
        }
    }

    @Nullable
    @Override
    public <T extends Mob> T convertTo(EntityType<T> p_21407_, boolean p_21408_) {
        T convert = super.convertTo(p_21407_, p_21408_);
        if (convert instanceof AbstractIllagerServant servant) {
            if (MobsConfig.IllagerServantTrainArmor.get() && MobsConfig.RaiderServantWearArmor.get()) {
                if (this.getTrueOwner() != null && CuriosFinder.hasCurio(this.getTrueOwner(), ModItems.RING_OF_THE_FORGE.get())) {
                    servant.spawnArmor(this.getTrueOwner().getRandom());
                }
            }
        }
        return convert;
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getMainHandItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (!this.isBaby() && itemstack.is(ItemTags.BANNERS) && this.getMainHandItem().isEmpty()){
                if (this.getLeaderBannerInstance().isEmpty()){
                    this.playSound(SoundEvents.PILLAGER_AMBIENT, 1.0F, 0.75F);
                    this.level.broadcastEntityEvent(this, (byte) 9);
                    return InteractionResult.FAIL;
                } else if (!ItemHelper.sameBanner(itemstack, this.getBannerPatternInstance())){
                    if (!this.level.isClientSide) {
                        this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy());
                        if (!pPlayer.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                        this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            } else if (pPlayer.getMainHandItem().isEmpty() && pPlayer.isShiftKeyDown() && this.getMainHandItem().is(ItemTags.BANNERS)){
                ItemStack helmet = this.getItemBySlot(EquipmentSlot.MAINHAND);
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                this.dropEquipment(EquipmentSlot.MAINHAND, helmet);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public static class LoomGoal extends Goal {
        public Neollager illager;
        @Nullable
        public BlockPos loom;
        @Nullable
        public Path path;
        public int workTick;

        public LoomGoal(Neollager illager){
            this.illager = illager;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.illager.getMainHandItem().is(ItemTags.BANNERS) && !this.illager.getLeaderBannerInstance().isEmpty()) {
                this.loom = this.findLoom();
                if (this.loom != null) {
                    Path path = this.illager.getNavigation().createPath(this.loom, 1);
                    if (path != null && path.canReach()) {
                        this.path = path;
                        return this.illager.getTarget() == null && this.path != null && this.path.canReach();
                    }
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.loom != null) {
                if (this.path != null && this.path.canReach()) {
                    return this.illager.level.getBlockState(this.loom).is(Blocks.LOOM) && super.canContinueToUse();
                }
            }
            return false;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void stop() {
            this.loom = null;
            this.path = null;
            this.workTick = 0;
        }

        public void tick(){
            if (this.loom == null) {
                this.stop();
                return;
            }
            if (this.illager.distanceToSqr(Vec3.atBottomCenterOf(this.loom)) > Mth.square(2)){
                this.illager.getNavigation().moveTo(this.loom.getX(), this.loom.getY(), this.loom.getZ(), 0.5F);
            } else {
                this.illager.getNavigation().stop();
                this.illager.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atBottomCenterOf(this.loom));
                ++this.workTick;
                if (this.workTick > 100){
                    this.illager.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    ItemEntity itementity = new ItemEntity(this.illager.level, this.loom.getX() + 0.5F, this.loom.getY() + 1.0F, this.loom.getZ() + 0.5F, this.illager.getLeaderBannerInstance());
                    itementity.setDefaultPickUpDelay();
                    this.illager.playSound(SoundEvents.UI_LOOM_TAKE_RESULT);
                    this.illager.level.addFreshEntity(itementity);
                    if (this.illager.level instanceof ServerLevel serverLevel) {
                        ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ParticleTypes.HAPPY_VILLAGER, this.illager);
                    }
                }
            }

        }

        public BlockPos findLoom(){
            List<BlockPos> blockPosList = new ArrayList<>();
            for (int i = -8; i <= 8; ++i){
                for (int j = -8; j <= 8; ++j){
                    for (int k = -8; k <= 8; ++k){
                        BlockPos blockPos1 = this.illager.blockPosition().offset(i, j, k);
                        BlockState blockState = this.illager.level.getBlockState(blockPos1);
                        if (blockState.is(Blocks.LOOM)){
                            blockPosList.add(blockPos1);
                        }
                    }
                }
            }
            if (!blockPosList.isEmpty()){
                blockPosList.sort(Comparator.comparingDouble(blockPos1 -> this.illager.distanceToSqr(Vec3.atCenterOf(blockPos1))));
                if (blockPosList.stream().findFirst().isPresent()){
                    return blockPosList.stream().findFirst().get();
                }
            }
            return null;
        }
    }
}
