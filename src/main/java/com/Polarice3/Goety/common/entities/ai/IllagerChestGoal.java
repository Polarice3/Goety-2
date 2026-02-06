package com.Polarice3.Goety.common.entities.ai;

import com.Polarice3.Goety.api.entities.ally.illager.ILooter;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class IllagerChestGoal<T extends RaiderServant & ILooter> extends MoveToBlockGoal {
    public T illager;
    public boolean hasOpenedChest = false;
    public int searchRange;
    public Predicate<ItemStack> predicate = itemStack -> true;
    public Predicate<ItemStack> chestPredicate = itemStack -> false;

    public IllagerChestGoal(T illager, int range) {
        super(illager, 0.75F, range);
        this.illager = illager;
        this.searchRange = range;
    }

    public IllagerChestGoal(T illager) {
        this(illager, MobsConfig.IllagerServantChestRange.get());
    }

    public ItemStack getItem(){
        Optional<ItemStack> optional = this.illager.itemsInInv(this.predicate).stream().findFirst();
        return optional.orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean canUse() {
        if (this.illager.isStaying()) {
            return false;
        }
        if (this.illager.getTarget() != null){
            return false;
        }
        if (this.illager.getMarked() != null){
            return false;
        }
        if (this.illager.isRaiding()){
            return false;
        }
        if (this.illager.isCelebrating()){
            return false;
        }
        if (this.illager.itemsInInv(this.predicate).isEmpty()){
            return false;
        }
        return this.findNearestBlock();
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.canUse();
    }

    protected boolean baseNearestBlock() {
        return super.findNearestBlock();
    }

    protected boolean findNearestBlock() {
        if (this.illager.getChestPos() != null) {
            this.blockPos = this.illager.getChestPos();
            if (this.blockPos != null){
                return this.illager.distanceToSqr(this.blockPos.getX() + 0.5F, this.blockPos.getY() + 0.5F, this.blockPos.getZ() + 0.5F) <= Mth.square(this.searchRange);
            }
        }
        return false;
    }

    public boolean isChestRaidable(LevelReader world, BlockPos pos) {
        Container container = this.getChest(world, pos);
        if (container != null) {
            return container.hasAnyMatching(this.chestPredicate);
        }
        return false;
    }

    @Nullable
    public Container getChest(LevelReader world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        if (block instanceof ChestBlock chestBlock && world instanceof Level level) {
            return ChestBlock.getContainer(chestBlock, blockState, level, pos, true);
        } else if (world.getBlockState(pos).getBlock() instanceof BaseEntityBlock) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof Container inventory) {
                return inventory;
            }
        }
        return null;
    }

    public List<ItemStack> getChestContent(LevelReader world, BlockPos pos) {
        List<ItemStack> list = new ArrayList<>();
        Container container = this.getChest(world, pos);
        if (container != null) {
            for (int i = 0; i < container.getContainerSize(); ++i) {
                ItemStack itemStack = container.getItem(i);
                list.add(itemStack);
            }
        }
        return list;
    }

    public boolean isFull(ItemStack addStack, LevelReader world, BlockPos pos) {
        Container chest = this.getChest(world, pos);
        if (chest == null) {
            return false;
        }
        List<ItemStack> list = this.getChestContent(world, pos);
        if (list.isEmpty()) {
            return false;
        }
        if (addStack == null || addStack.isEmpty()) {
            return false;
        }
        int i = 0;
        for (ItemStack containerStack : list) {
            if (containerStack.isEmpty()) {
                ++i;
            } else if (ItemStack.isSameItem(containerStack, addStack)) {
                final int j = Math.min(addStack.getMaxStackSize(), containerStack.getMaxStackSize());
                final int k = Math.min(addStack.getCount(), j - containerStack.getCount());
                if (k > 0) {
                    ++i;
                }
            }
        }
        return i == 0;
    }

    public boolean hasLineOfSightChest() {
        HitResult hitResult = this.illager.level.clip(new ClipContext(this.illager.getEyePosition(1.0F), new Vec3(this.blockPos.getX() + 0.5, this.blockPos.getY() + 0.5, this.blockPos.getZ() + 0.5), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.illager));
        if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockPos pos = blockHitResult.getBlockPos();
            return pos.equals(this.blockPos) || this.illager.level.isEmptyBlock(pos) || this.illager.level.getBlockEntity(pos) == this.illager.level.getBlockEntity(this.blockPos);
        }
        return true;
    }

    public List<ItemStack> getItems(Container inventory) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (this.chestPredicate.test(stack)) {
                items.add(stack);
            }
        }
        return items;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.blockPos == null){
            this.stop();
        } else {
            Container chest = this.getChest(this.illager.level, this.blockPos);
            if (chest != null) {
                double distance = this.illager.distanceToSqr(this.blockPos.getX() + 0.5F, this.blockPos.getY() + 0.5F, this.blockPos.getZ() + 0.5F);
                if (this.hasLineOfSightChest()) {
                    if (this.isReachedTarget() && distance <= 3.0D) {
                        this.toggleChest(chest, false);
                        this.chestInteract(chest);
                        this.stop();
                    } else {
                        if (distance < 5.0D && !this.hasOpenedChest) {
                            this.hasOpenedChest = true;
                            this.toggleChest(chest, true);
                        }
                    }
                }
            }
        }
    }

    public void chestInteract(Container container) {
    }

    public void stop() {
        super.stop();
        if (this.blockPos != null) {
            BlockEntity blockEntity = this.illager.level.getBlockEntity(this.blockPos);
            if (blockEntity instanceof Container container) {
                this.toggleChest(container, false);
            }
        }
        this.blockPos = BlockPos.ZERO;
        this.hasOpenedChest = false;
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        return pos != null && this.isChestRaidable(worldIn, pos);
    }

    public void toggleChest(Container container, boolean open) {
        if (container instanceof ChestBlockEntity chest) {
            if (open) {
                this.illager.level.blockEvent(this.blockPos, chest.getBlockState().getBlock(), 1, 1);
            } else {
                this.illager.level.blockEvent(this.blockPos, chest.getBlockState().getBlock(), 1, 0);
            }
            this.illager.level.updateNeighborsAt(this.blockPos, chest.getBlockState().getBlock());
            this.illager.level.updateNeighborsAt(this.blockPos.below(), chest.getBlockState().getBlock());
        }
    }
}
