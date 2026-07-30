package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.Tags;

public class OssuaryBlockEntity extends TrainingBlockEntity {

    public OssuaryBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.SHADE_OSSUARY.get(), p_155229_, p_155230_);
    }

    @Override
    public void tick(Level level, BlockPos blockPos, BlockState blockState, TrainingBlockEntity blockEntity) {
        super.tick(level, blockPos, blockState, blockEntity);
        if (blockEntity.isTraining()){
            if (blockEntity.trainTime != blockEntity.getMaxTrainTime()){
                if (blockEntity.trainTime % 20 == 0){
                    level.playSound(null, blockPos, SoundEvents.SOUL_ESCAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
            if (level instanceof ServerLevel serverLevel) {
                double d0 = (double)blockPos.getX() + level.getRandom().nextDouble();
                double d1 = (double)blockPos.getY() + 0.75D + (level.getRandom().nextDouble() * 0.5D);
                double d2 = (double)blockPos.getZ() + level.getRandom().nextDouble();
                serverLevel.sendParticles(ModParticleTypes.NECRO_FIRE.get(), d0, d1, d2, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                if (level.getRandom().nextFloat() < 0.3F) {
                    if (level.getRandom().nextFloat() < 0.17F) {
                        ModNetwork.sendToALL(new SPlayWorldSoundPacket(blockPos, SoundEvents.FURNACE_FIRE_CRACKLE, 0.5F + level.random.nextFloat(), level.random.nextFloat() * 0.7F + 0.3F));
                    }
                }
            }
        }
    }

    @Override
    public void setVariant(ItemStack itemStack, Level level, BlockPos blockPos) {
        if (level instanceof ServerLevel serverLevel) {
            BlockState blockState0 = level.getBlockState(blockPos);
            if (blockState0.hasProperty(BlockStateProperties.WATERLOGGED) && blockState0.getValue(BlockStateProperties.WATERLOGGED)){
                if (this.getTrainMob() != ModEntityType.SUNKEN_SKELETON_SERVANT.get()) {
                    this.setEntityType(ModEntityType.SUNKEN_SKELETON_SERVANT.get());
                    this.markUpdated();
                }
            } else if (serverLevel.getBiome(blockPos.above()).get().coldEnoughToSnow(blockPos.above()) || (getBlocks(blockState -> blockState.is(BlockTags.SNOW), 20) && getBlocks(blockState -> blockState.is(BlockTags.ICE), 8))) {
                if (this.getTrainMob() != ModEntityType.STRAY_SERVANT.get()) {
                    this.setEntityType(ModEntityType.STRAY_SERVANT.get());
                    this.markUpdated();
                }
            } else if (serverLevel.getBiome(blockPos.above()).is(BiomeTags.IS_JUNGLE) || (getBlocks(blockState -> blockState.getBlock() instanceof VineBlock, 20) && getBlocks(blockState -> blockState.is(BlockTags.LEAVES), 8))) {
                if (this.getTrainMob() != ModEntityType.MOSSY_SKELETON_SERVANT.get()) {
                    this.setEntityType(ModEntityType.MOSSY_SKELETON_SERVANT.get());
                    this.markUpdated();
                }
            } else if ((serverLevel.isThundering() && serverLevel.canSeeSky(blockPos.above()))
                    || (getBlocks(blockState -> blockState.getBlock() instanceof LightningRodBlock, 1) && getBlocks(blockState -> blockState.is(Tags.Blocks.STORAGE_BLOCKS_COPPER), 8))) {
                if (this.getTrainMob() != ModEntityType.RATTLED_SERVANT.get()) {
                    this.setEntityType(ModEntityType.RATTLED_SERVANT.get());
                    this.markUpdated();
                }
            } else if (BlockFinder.findStructure(serverLevel, blockPos.above(), ModTags.Structures.PILLAGER_OUTPOST) || (getBlocks(blockState -> blockState.is(Blocks.DARK_OAK_LOG), 15) && getBlocks(blockState -> blockState.getBlock() instanceof CarvedPumpkinBlock || blockState.getBlock() instanceof TargetBlock, 4))) {
                if (this.getTrainMob() != ModEntityType.SKELETON_PILLAGER_SERVANT.get()) {
                    this.setEntityType(ModEntityType.SKELETON_PILLAGER_SERVANT.get());
                    this.markUpdated();
                }
            } else if (this.getTrueOwner() instanceof Player player
                    && SEHelper.hasResearch(player, ResearchList.BYGONE)
                    && (BlockFinder.findStructure(serverLevel, blockPos, ModTags.Structures.CAN_SUMMON_WITHER_SKELETONS) || (getBlocks(blockState -> blockState.getBlock().getDescriptionId().contains("nether_brick"), 32) && getBlocks(blockState -> blockState.is(Blocks.NETHER_WART), 8)))) {
                if (this.getTrainMob() != ModEntityType.WITHER_SKELETON_SERVANT.get()) {
                    this.setEntityType(ModEntityType.WITHER_SKELETON_SERVANT.get());
                    this.markUpdated();
                }
            } else {
                if (this.getTrainMob() != ModEntityType.SKELETON_SERVANT.get()) {
                    this.setEntityType(ModEntityType.SKELETON_SERVANT.get());
                    this.markUpdated();
                }
            }
        }
    }

    public void startTraining(int amount, ItemStack itemStack){
        super.startTraining(amount, itemStack);
        if (this.level != null) {
            this.level.playSound(null, this.getBlockPos(), ModSounds.GRAVESTONE_START.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public void playSpawnSound() {
        if (this.level != null) {
            this.level.playSound(null, this.getBlockPos(), ModSounds.NECROMANCER_SUMMON.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public int maxTrainAmount() {
        return 5;
    }

    @Override
    public boolean summonLimit() {
        int count = 0;
        if (this.level instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof AbstractSkeletonServant servant) {
                    if (this.getTrueOwner() != null && servant.getTrueOwner() == this.getTrueOwner() && servant.isAlive()) {
                        ++count;
                    }
                }
            }
        }
        return count >= SpellConfig.SkeletonLimit.get();
    }

    @Override
    public boolean isFuel(ItemStack itemStack) {
        return itemStack.is(Items.BONE_MEAL);
    }
}
