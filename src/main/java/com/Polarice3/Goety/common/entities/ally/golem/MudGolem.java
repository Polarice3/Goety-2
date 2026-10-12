package com.Polarice3.Goety.common.entities.ally.golem;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class MudGolem extends AbstractGolemServant implements InventoryCarrier {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(MudGolem.class, EntityDataSerializers.BYTE);
    private final SimpleContainer inventory = new SimpleContainer(8);

    public MudGolem(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new GolemHarvestGoal(this));
        this.goalSelector.addGoal(1, new GolemBonemealGoal(this));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    public void addAdditionalSaveData(CompoundTag p_34397_) {
        super.addAdditionalSaveData(p_34397_);
        this.writeInventoryToTag(p_34397_);
    }

    public void readAdditionalSaveData(CompoundTag p_34387_) {
        super.readAdditionalSaveData(p_34387_);
        this.readInventoryFromTag(p_34387_);
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public SlotAccess getSlot(int p_149995_) {
        int i = p_149995_ - 300;
        return i >= 0 && i < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, i) : super.getSlot(p_149995_);
    }

    protected void pickUpItem(ItemEntity p_35467_) {
        InventoryCarrier.pickUpItem(this, this, p_35467_);
    }

    @Override
    public boolean canPickUpLoot() {
        return true;
    }

    public boolean wantsToPickUp(ItemStack p_35543_) {
        return p_35543_.is(ItemTags.VILLAGER_PLANTABLE_SEEDS) && this.getInventory().canAddItem(p_35543_);
    }

    public boolean hasFarmSeeds() {
        return this.getInventory().hasAnyMatching((p_281096_) -> {
            return p_281096_.is(ItemTags.VILLAGER_PLANTABLE_SEEDS);
        });
    }

    public static class GolemHarvestGoal extends Goal {
        public MudGolem servant;
        private static final int HARVEST_DURATION = 200;
        public static final float SPEED_MODIFIER = 0.5F;
        @Nullable
        private BlockPos aboveFarmlandPos;
        private long nextOkStartTime;
        private int timeWorkedSoFar;
        private final List<BlockPos> validFarmlandAroundGolem = Lists.newArrayList();

        public GolemHarvestGoal(MudGolem servant){
            this.servant = servant;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.servant.getTarget() != null){
                return false;
            } else if (this.servant.getLastHurtByMob() != null){
                return false;
            } else {
                BlockPos.MutableBlockPos blockpos$mutableblockpos = this.servant.blockPosition().mutable();
                this.validFarmlandAroundGolem.clear();

                for(int i = -1; i <= 1; ++i) {
                    for(int j = -1; j <= 1; ++j) {
                        for(int k = -1; k <= 1; ++k) {
                            blockpos$mutableblockpos.set(this.servant.getX() + (double)i, this.servant.getY() + (double)j, this.servant.getZ() + (double)k);
                            if (this.validPos(blockpos$mutableblockpos, this.servant.level)) {
                                this.validFarmlandAroundGolem.add(new BlockPos(blockpos$mutableblockpos));
                            }
                        }
                    }
                }

                this.aboveFarmlandPos = this.getValidFarmland(this.servant.level);
                return this.aboveFarmlandPos != null;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.timeWorkedSoFar < 200;
        }

        @Override
        public void start() {
            super.start();
            if (this.aboveFarmlandPos != null){
                this.servant.getLookControl().setLookAt(this.aboveFarmlandPos.getCenter());
                this.servant.getNavigation().moveTo(this.aboveFarmlandPos.getCenter().x(), this.aboveFarmlandPos.getCenter().y(), this.aboveFarmlandPos.getCenter().z(), 1.0F);
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.servant.getNavigation().stop();
            this.timeWorkedSoFar = 0;
            this.nextOkStartTime = this.servant.level.getGameTime() + 40L;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.aboveFarmlandPos != null){
                if (this.aboveFarmlandPos.closerToCenterThan(this.servant.position(), 2.0D)) {
                    if (this.servant.level.getGameTime() > this.nextOkStartTime) {
                        BlockState blockstate = this.servant.level.getBlockState(this.aboveFarmlandPos);
                        Block block = blockstate.getBlock();
                        Block block1 = this.servant.level.getBlockState(this.aboveFarmlandPos.below()).getBlock();
                        if (block instanceof CropBlock && ((CropBlock)block).isMaxAge(blockstate)) {
                            this.servant.level.destroyBlock(this.aboveFarmlandPos, true, this.servant);
                        }

                        if (blockstate.isAir() && block1 instanceof FarmBlock && this.servant.hasFarmSeeds()) {
                            SimpleContainer simplecontainer = this.servant.getInventory();

                            for(int i = 0; i < simplecontainer.getContainerSize(); ++i) {
                                ItemStack itemstack = simplecontainer.getItem(i);
                                boolean flag = false;
                                if (!itemstack.isEmpty() && itemstack.is(ItemTags.VILLAGER_PLANTABLE_SEEDS)) {
                                    Item $$11 = itemstack.getItem();
                                    if ($$11 instanceof BlockItem blockitem) {
                                        BlockState blockstate1 = blockitem.getBlock().defaultBlockState();
                                        this.servant.level.setBlockAndUpdate(this.aboveFarmlandPos, blockstate1);
                                        this.servant.level.gameEvent(GameEvent.BLOCK_PLACE, this.aboveFarmlandPos, GameEvent.Context.of(this.servant, blockstate1));
                                        flag = true;
                                    } else if (itemstack.getItem() instanceof net.minecraftforge.common.IPlantable) {
                                        if (((net.minecraftforge.common.IPlantable)itemstack.getItem()).getPlantType(this.servant.level, aboveFarmlandPos) == net.minecraftforge.common.PlantType.CROP) {
                                            this.servant.level.setBlock(aboveFarmlandPos, ((net.minecraftforge.common.IPlantable)itemstack.getItem()).getPlant(this.servant.level, aboveFarmlandPos), 3);
                                            flag = true;
                                        }
                                    }
                                }

                                if (flag) {
                                    this.servant.level.playSound((Player)null, (double)this.aboveFarmlandPos.getX(), (double)this.aboveFarmlandPos.getY(), (double)this.aboveFarmlandPos.getZ(), SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
                                    itemstack.shrink(1);
                                    if (itemstack.isEmpty()) {
                                        simplecontainer.setItem(i, ItemStack.EMPTY);
                                    }
                                    break;
                                }
                            }
                        }

                        if (block instanceof CropBlock && !((CropBlock)block).isMaxAge(blockstate)) {
                            this.validFarmlandAroundGolem.remove(this.aboveFarmlandPos);
                            this.aboveFarmlandPos = this.getValidFarmland(this.servant.level);
                            if (this.aboveFarmlandPos != null) {
                                this.nextOkStartTime = this.servant.level.getGameTime() + 20L;
                                this.servant.getLookControl().setLookAt(this.aboveFarmlandPos.getCenter());
                                this.servant.getNavigation().moveTo(this.aboveFarmlandPos.getCenter().x(), this.aboveFarmlandPos.getCenter().y(), this.aboveFarmlandPos.getCenter().z(), 1.0F);
                            }
                        }
                    }
                } else {
                    this.servant.getLookControl().setLookAt(this.aboveFarmlandPos.getCenter());
                    this.servant.getNavigation().moveTo(this.aboveFarmlandPos.getCenter().x(), this.aboveFarmlandPos.getCenter().y(), this.aboveFarmlandPos.getCenter().z(), 1.0F);
                }
            }
            ++this.timeWorkedSoFar;
        }

        @Nullable
        private BlockPos getValidFarmland(Level p_23165_) {
            return this.validFarmlandAroundGolem.isEmpty() ? null : this.validFarmlandAroundGolem.get(p_23165_.getRandom().nextInt(this.validFarmlandAroundGolem.size()));
        }

        private boolean validPos(BlockPos p_23181_, Level p_23182_) {
            BlockState blockstate = p_23182_.getBlockState(p_23181_);
            Block block = blockstate.getBlock();
            Block block1 = p_23182_.getBlockState(p_23181_.below()).getBlock();
            return block instanceof CropBlock && ((CropBlock)block).isMaxAge(blockstate) || blockstate.isAir() && block1 instanceof FarmBlock;
        }
    }

    public class GolemBonemealGoal extends Goal {
        public MudGolem servant;
        private long nextWorkCycleTime;
        private long lastBonemealingSession;
        private int timeWorkedSoFar;
        private Optional<BlockPos> cropPos = Optional.empty();

        public GolemBonemealGoal(MudGolem servant){
            this.servant = servant;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.servant.getTarget() != null){
                return false;
            } else if (!(this.servant.getMainHandItem().isEmpty() || this.servant.getMainHandItem().is(Items.BONE_MEAL) || this.servant.getOffhandItem().isEmpty() || this.servant.getOffhandItem().is(Items.BONE_MEAL))){
                return false;
            } else if ((this.lastBonemealingSession == 0L || this.lastBonemealingSession + 160L <= this.servant.level.getGameTime())) {
                if (this.servant.getInventory().countItem(Items.BONE_MEAL) <= 0) {
                    return false;
                } else {
                    this.cropPos = this.pickNextTarget(this.servant.level, this.servant);
                    return this.cropPos.isPresent();
                }
            } else {
                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.timeWorkedSoFar < 80 && this.cropPos.isPresent();
        }

        public void start() {
            this.setCurrentCropAsTarget(this.servant);
            if (this.servant.getMainHandItem().isEmpty()) {
                this.servant.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BONE_MEAL));
            } else if (this.servant.getOffhandItem().isEmpty()){
                this.servant.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.BONE_MEAL));
            }
            this.nextWorkCycleTime = this.servant.level.getGameTime();
            this.timeWorkedSoFar = 0;
        }

        @Override
        public void stop() {
            super.stop();
            if (this.servant.getMainHandItem().is(Items.BONE_MEAL)) {
                this.servant.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            } else if (this.servant.getOffhandItem().is(Items.BONE_MEAL)){
                this.servant.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }
            this.lastBonemealingSession = this.servant.level.getGameTime();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.cropPos.isPresent()) {
                BlockPos blockpos = this.cropPos.get();
                if (blockpos.closerToCenterThan(this.servant.position(), 2.0D)){
                    if (this.servant.level.getGameTime() >= this.nextWorkCycleTime) {
                        ItemStack itemstack = ItemStack.EMPTY;
                        SimpleContainer simplecontainer = this.servant.getInventory();
                        int i = simplecontainer.getContainerSize();

                        for (int j = 0; j < i; ++j) {
                            ItemStack itemstack1 = simplecontainer.getItem(j);
                            if (itemstack1.is(Items.BONE_MEAL)) {
                                itemstack = itemstack1;
                                break;
                            }
                        }

                        if (!itemstack.isEmpty() && BoneMealItem.growCrop(itemstack, this.servant.level, blockpos)) {
                            this.servant.level.levelEvent(1505, blockpos, 0);
                            itemstack.shrink(1);
                            if (this.servant.getMainHandItem().is(Items.BONE_MEAL)) {
                                this.servant.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                            } else if (this.servant.getOffhandItem().is(Items.BONE_MEAL)){
                                this.servant.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                            }
                            this.cropPos = this.pickNextTarget(this.servant.level, this.servant);
                            this.setCurrentCropAsTarget(this.servant);
                            this.nextWorkCycleTime = this.servant.level.getGameTime() + 40L;
                        }

                        ++this.timeWorkedSoFar;
                    }
                } else {
                    this.servant.getLookControl().setLookAt(blockpos.getCenter());
                    this.servant.getNavigation().moveTo(blockpos.getCenter().x(), blockpos.getCenter().y(), blockpos.getCenter().z(), 1.0F);
                }
            }
        }

        private void setCurrentCropAsTarget(MudGolem p_24481_) {
            this.cropPos.ifPresent((p_24484_) -> {
                p_24481_.getLookControl().setLookAt(p_24484_.getCenter());
                p_24481_.getNavigation().moveTo(p_24484_.getCenter().x(), p_24484_.getCenter().y(), p_24484_.getCenter().z(), 1.0F);
            });
        }

        private Optional<BlockPos> pickNextTarget(Level p_24493_, MudGolem p_24494_) {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            Optional<BlockPos> optional = Optional.empty();
            int i = 0;

            for(int j = -1; j <= 1; ++j) {
                for(int k = -1; k <= 1; ++k) {
                    for(int l = -1; l <= 1; ++l) {
                        blockpos$mutableblockpos.setWithOffset(p_24494_.blockPosition(), j, k, l);
                        if (this.validPos(blockpos$mutableblockpos, p_24493_)) {
                            ++i;
                            if (p_24493_.random.nextInt(i) == 0) {
                                optional = Optional.of(blockpos$mutableblockpos.immutable());
                            }
                        }
                    }
                }
            }

            return optional;
        }

        private boolean validPos(BlockPos p_24486_, Level p_24487_) {
            BlockState blockstate = p_24487_.getBlockState(p_24486_);
            Block block = blockstate.getBlock();
            return block instanceof CropBlock && !((CropBlock)block).isMaxAge(blockstate);
        }
    }
}
