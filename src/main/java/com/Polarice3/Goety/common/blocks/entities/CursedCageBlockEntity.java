package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.magic.TotemOfSouls;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class CursedCageBlockEntity extends BlockEntity implements Clearable {
    private ItemStack item = ItemStack.EMPTY;
    private int spinning;

    public CursedCageBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.CURSED_CAGE.get(), blockPos, blockState);
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

    public ItemStack getItem() {
        return this.item;
    }

    public void setItem(ItemStack stack) {
        this.item = stack;
        this.setChanged();
    }

    public Player getOwner(){
        if (this.item.getItem() == ModItems.SOUL_TRANSFER.get() && this.item.getTag() != null) {
            if (this.item.getTag().contains("owner")) {
                UUID owner = this.item.getTag().getUUID("owner");
                return this.level.getPlayerByUUID(owner);
            }
        }
        return null;
    }

    public int getSouls(){
        if (this.level != null) {
            if (this.item.getItem() == ModItems.SOUL_TRANSFER.get() && this.item.getTag() != null) {
                if (this.item.getTag().contains("owner")) {
                    UUID owner = this.item.getTag().getUUID("owner");
                    Player player = this.level.getPlayerByUUID(owner);
                    if (player != null) {
                        if (SEHelper.getSEActive(player)) {
                            return SEHelper.getSESouls(player);
                        }
                    }
                }
            }
            if (this.item.getItem() == ModItems.TOTEM_OF_SOULS.get()) {
                assert this.item.getTag() != null;
                return this.item.getTag().getInt(TotemOfSouls.SOULS_AMOUNT);
            }
        }
        return 0;
    }

    public void decreaseSouls(int souls) {
        if (this.item.getItem() == ModItems.TOTEM_OF_SOULS.get()) {
            assert this.item.getTag() != null;
            int Soulcount = this.item.getTag().getInt(TotemOfSouls.SOULS_AMOUNT);
            if (!this.item.isEmpty()) {
                if (Soulcount > 0){
                    Soulcount -= souls;
                    this.item.getTag().putInt(TotemOfSouls.SOULS_AMOUNT, Soulcount);
                    this.generateParticles();
                }
            }
        }
        if (this.level != null) {
            if (this.item.getItem() == ModItems.SOUL_TRANSFER.get() && this.item.getTag() != null) {
                if (this.item.getTag().contains("owner")) {
                    UUID owner = this.item.getTag().getUUID("owner");
                    Player player = this.level.getPlayerByUUID(owner);
                    if (player != null) {
                        if (SEHelper.getSEActive(player)) {
                            int Soulcount = SEHelper.getSESouls(player);
                            if (Soulcount > 0) {
                                SEHelper.decreaseSESouls(player, souls);
                                SEHelper.sendSEUpdatePacket(player);
                                ArcaBlockEntity arcaTile = (ArcaBlockEntity) this.level.getBlockEntity(SEHelper.getArcaBlock(player));
                                if (arcaTile != null) {
                                    arcaTile.generateParticles();
                                    this.generateParticles();
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public int getSpinning(){
        return this.spinning;
    }

    public void generateParticles() {
        if (this.getSouls() <= 0){
            return;
        }
        BlockPos blockpos = this.getBlockPos();

        if (this.level != null) {
            if (!this.level.isClientSide) {
                ServerLevel serverWorld = (ServerLevel) this.level;
                double d0 = (double) blockpos.getX() + this.level.random.nextDouble();
                double d1 = (double) blockpos.getY() + this.level.random.nextDouble();
                double d2 = (double) blockpos.getZ() + this.level.random.nextDouble();
                for (int p = 0; p < 4; ++p) {
                    serverWorld.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, d0, d1, d2, 1, 0, 0, 0, 0);
                    serverWorld.sendParticles(ParticleTypes.SMOKE, d0, d1, d2, 1, 0.0D, 5.0E-4D, 0.0D, 5.0E-4D);
                }
            }
            this.spinning = 20;
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
                    serverWorld.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, d9, d13, d19, 1, 0.0D, 0.0D, 0.0D, 0);
                }
            }
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
        item = ItemStack.of(tag.getCompound("item"));
    }

    public CompoundTag writeNetwork(CompoundTag tag) {
        tag.put("item", item.save(new CompoundTag()));
        return tag;
    }

    public void clearContent() {
        this.setItem(ItemStack.EMPTY);
    }

    public void tick() {
        if (this.spinning > 0){
            --this.spinning;
        }
    }
}
