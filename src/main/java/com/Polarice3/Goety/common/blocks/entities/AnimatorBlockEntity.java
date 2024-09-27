package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.api.magic.GolemType;
import com.Polarice3.Goety.common.blocks.AnimatorBlock;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.common.magic.construct.SpawnFromBlock;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class AnimatorBlockEntity extends BlockEntity implements Clearable {
    private ItemStack item = ItemStack.EMPTY;
    private CursedCageBlockEntity cursedCageTile;
    private int spinning;
    public boolean showBlock;

    public AnimatorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ANIMATOR.get(), blockPos, blockState);
    }

    public int getSoulCost(){
        if (this.getPosition() != null){
            double distance = this.getPosition().pos().distToCenterSqr(Vec3.atCenterOf(this.getBlockPos()));
            return (int) (MainConfig.AnimatorCost.get() * Mth.square(distance));
        }
        return 0;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return BlockEntity.INFINITE_EXTENT_AABB;
    }

    public void summonGolem(){
        if (this.level != null) {
            if (this.getPosition() != null) {
                if (this.level.dimension() == this.getPosition().dimension()) {
                    if (this.checkCage() && this.cursedCageTile.getSouls() >= this.getSoulCost()) {
                        ItemStack itemStack = ModItems.ANIMATION_CORE.get().getDefaultInstance();
                        BlockState blockState = this.level.getBlockState(this.getPosition().pos());
                        if (GolemType.getGolemList().containsKey(blockState)) {
                            if (GolemType.getGolemList().get(blockState).spawnServant(this.getOwner(), itemStack, this.level, this.getPosition().pos())){
                                this.level.playSound(null, this.getBlockPos(), ModSounds.SUMMON_SPELL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                                this.level.playSound(null, this.getPosition().pos(), ModSounds.SUMMON_SPELL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                                this.cursedCageTile.decreaseSouls(this.getSoulCost());
                                this.generateManyParticles();
                            }
                        } else {
                            if (SpawnFromBlock.spawnServant(this.getOwner(), itemStack, this.level, this.getPosition().pos())){
                                this.level.playSound(null, this.getBlockPos(), ModSounds.SUMMON_SPELL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                                this.level.playSound(null, this.getPosition().pos(), ModSounds.SUMMON_SPELL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                                this.cursedCageTile.decreaseSouls(this.getSoulCost());
                                this.generateManyParticles();
                            }
                        }
                    }
                }
            }
        }
    }

    public ItemStack getItem() {
        return this.item;
    }

    public void setItem(ItemStack stack) {
        this.item = stack;
        this.setChanged();
    }

    public Player getOwner(){
        if (this.level != null) {
            if (!this.getItem().isEmpty()) {
                if (this.getItem().getItem() instanceof WaystoneItem && this.getItem().getTag() != null) {
                    if (this.getItem().getTag().contains(WaystoneItem.TAG_OWNER)) {
                        UUID owner = this.getItem().getTag().getUUID(WaystoneItem.TAG_OWNER);
                        return this.level.getPlayerByUUID(owner);
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public GlobalPos getPosition(){
        if (!this.getItem().isEmpty()) {
            if (this.getItem().getTag() != null) {
                return WaystoneItem.getPosition(this.getItem().getTag());
            }
        }
        return null;
    }

    public int getSpinning(){
        return this.spinning;
    }

    public void tick() {
        if (this.spinning > 0){
            --this.spinning;
        }
        if (this.level != null) {
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(AnimatorBlock.POWERED, this.checkCage() && this.getPosition() != null), 3);
        }
    }

    public void generateManyParticles(){
        BlockPos blockpos = this.getBlockPos();
        if (this.level != null) {
            if (!this.level.isClientSide) {
                ServerLevel serverWorld = (ServerLevel) this.level;
                for(int k = 0; k < 20; ++k) {
                    double d9 = (double)blockpos.getX() + 0.5D + (this.level.random.nextDouble() - 0.5D) * 2.0D;
                    double d13 = (double)blockpos.getY() + 0.5D + (this.level.random.nextDouble() - 0.5D) * 2.0D;
                    double d19 = (double)blockpos.getZ() + 0.5D + (this.level.random.nextDouble() - 0.5D) * 2.0D;
                    serverWorld.sendParticles(ParticleTypes.SMOKE, d9, d13, d19, 1, 0.0D, 0.0D, 0.0D, 0);
                    serverWorld.sendParticles(ParticleTypes.FLAME, d9, d13, d19, 1, 0.0D, 0.0D, 0.0D, 0);
                }
            }
        }

    }

    private boolean checkCage() {
        if (this.level != null) {
            BlockPos pos = new BlockPos(this.getBlockPos().getX(), this.getBlockPos().getY() - 1, this.getBlockPos().getZ());
            BlockState blockState = this.level.getBlockState(pos);
            if (blockState.is(ModBlocks.CURSED_CAGE_BLOCK.get())) {
                BlockEntity tileentity = this.level.getBlockEntity(pos);
                if (tileentity instanceof CursedCageBlockEntity cageBlock) {
                    this.cursedCageTile = cageBlock;
                    return !cursedCageTile.getItem().isEmpty();
                }
            }
        }
        return false;
    }

    public boolean isShowBlock(){
        return this.showBlock;
    }

    public void setShowBlock(boolean showBlock){
        this.showBlock = showBlock;
        this.setChanged();
        this.markNetworkDirty();
    }

    public void markNetworkDirty() {
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.writeNetwork(super.getUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.readNetwork(pkt.getTag());
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.load(tag);
        this.readNetwork(tag);
    }

    public void readNetwork(CompoundTag tag) {
        this.item = ItemStack.of(tag.getCompound("item"));
        if (tag.contains("showBlock")) {
            this.showBlock = tag.getBoolean("showBlock");
        }
    }

    public CompoundTag writeNetwork(CompoundTag tag) {
        tag.put("item", item.save(new CompoundTag()));
        tag.putBoolean("showBlock", this.showBlock);
        return tag;
    }

    public void clearContent() {
        this.setItem(ItemStack.EMPTY);
    }

    public void markUpdated() {
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public void load(CompoundTag compound) {
        this.readNetwork(compound);
        super.load(compound);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        this.writeNetwork(compound);
        super.saveAdditional(compound);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
