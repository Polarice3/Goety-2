package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.client.particles.TeleportInShockwaveParticleOption;
import com.Polarice3.Goety.client.particles.WindBlowParticle;
import com.Polarice3.Goety.common.blocks.VoidShrineBlock;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.boss.EnderKeeper;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningPacket;
import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import java.util.Optional;

public class VoidShrineBlockEntity extends PedestalBlockEntity {
    public int tick;
    public int coolTick;

    public VoidShrineBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.VOID_SHRINE.get(), p_155229_, p_155230_);
    }

    public void tick() {
        if (this.level instanceof ServerLevel serverLevel) {
            if (this.getBlockState().getValue(VoidShrineBlock.CHARGE) >= 4) {
                if (!this.getBlockState().getValue(VoidShrineBlock.TRIGGERED)) {
                    serverLevel.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(VoidShrineBlock.TRIGGERED, true));
                } else {
                    if (this.tick > 120) {
                        ++this.coolTick;
                    } else {
                        ++this.tick;
                    }
                    if (this.tick < 100) {
                        Vec3 vec3 = Vec3.atCenterOf(this.getBlockPos());
                        if (this.level.getRandom().nextInt(20) == 0) {
                            Vec3 endVec = vec3.add(0.0D, 2.5D, 0.0D);
                            ModNetwork.sendToALL(new SLightningPacket(vec3, endVec, new ColorUtil(ChatFormatting.LIGHT_PURPLE), 2));
                        }
                        if (this.tick % 5 == 0){
                            serverLevel.sendParticles(new TeleportInShockwaveParticleOption(4, 1), vec3.x, vec3.y + 0.25F, vec3.z, 0, 0, 0, 0, 0.5F);
                        }
                        int width = this.level.getRandom().nextIntBetweenInclusive(1, 4);
                        float height = this.level.getRandom().nextFloat() * 0.5F;
                        vec3 = vec3.offsetRandom(this.level.getRandom(), 3.0F);
                        serverLevel.sendParticles(new WindBlowParticle.Option(new ColorUtil(ChatFormatting.LIGHT_PURPLE), width, height), vec3.x, vec3.y, vec3.z, 0, 0.0F, 1.0F, 0.0F, 1.0F);
                    }
                    if (this.tick == 100) {
                        this.breakBlocksAround();
                        Vec3 vec3 = this.worldPosition.above().getCenter();
                        EnderKeeper keeper = new EnderKeeper(ModEntityType.ENDER_KEEPER.get(), serverLevel);
                        keeper.setPos(vec3);
                        keeper.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.worldPosition), MobSpawnType.MOB_SUMMONED, null, null);
                        keeper.setBoundPos(this.worldPosition);
                        keeper.setPersistenceRequired();
                        serverLevel.addFreshEntity(keeper);
                    }
                    if (this.coolTick < 20) {
                        for (Player player : serverLevel.getEntitiesOfClass(Player.class, new AABB(this.getBlockPos()).inflate(64.0F), EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
                            player.addEffect(new MobEffectInstance(GoetyEffects.IMPAIRED.get(), 5, 0, false, false));
                        }
                    }
                    ItemStack itemStack = ItemStack.EMPTY;
                    LazyOptional<IItemHandler> lazyOptional = this.getCapability(ForgeCapabilities.ITEM_HANDLER, null);
                    if (lazyOptional.isPresent()) {
                        if (lazyOptional.resolve().isPresent()) {
                            Optional<IItemHandler> optional = lazyOptional.resolve();
                            if (optional.isPresent()) {
                                itemStack = optional.get().getStackInSlot(0);
                            }
                        }
                    }
                    if (this.coolTick >= 20 && itemStack.isEmpty()) {
                        serverLevel.playSound(null, this.worldPosition, SoundEvents.RESPAWN_ANCHOR_DEPLETE.get(), SoundSource.BLOCKS, 1.0F, 0.8F);
                        serverLevel.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(VoidShrineBlock.CHARGE, 0).setValue(VoidShrineBlock.TRIGGERED, false));
                    }
                }
            } else {
                if (this.coolTick > 0) {
                    this.coolTick = 0;
                }
                if (this.tick > 0) {
                    this.tick = 0;
                }
            }
        }
    }

    public void breakBlocksAround() {
        if (this.level == null) {
            return;
        }
        int radius = 7;
        for (int i = -radius; i <= radius; ++i) {
            for (int j = 1; j <= radius; ++j) {
                for (int k = -radius; k <= radius; ++k) {
                    BlockPos blockpos = this.worldPosition.offset(i, j, k);
                    BlockState block = this.level.getBlockState(blockpos);
                    if (block != Blocks.AIR.defaultBlockState() && !block.is(BlockTags.WITHER_IMMUNE)) {
                        this.level.destroyBlock(blockpos, false);
                    }
                }
            }
        }
    }

    public void setCoolTick(int coolTick) {
        this.coolTick = coolTick;
    }

    public void readNetwork(CompoundTag compound) {
        super.readNetwork(compound);
        this.tick = compound.getInt("Tick");
        this.coolTick = compound.getInt("CoolTick");
    }

    public CompoundTag writeNetwork(CompoundTag compound) {
        CompoundTag compoundTag = super.writeNetwork(compound);
        compoundTag.putInt("Tick", this.tick);
        compoundTag.putInt("CoolTick", this.coolTick);
        return compoundTag;
    }
}
