package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.api.blocks.IEnchantedBlock;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.SculkGrowerBlock;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class SculkGrowerBlockEntity extends ModBlockEntity implements IEnchantedBlock {
    private CursedCageBlockEntity cursedCageTile;
    private int growCharges = -1;
    private int decayTimer = 100;
    private final List<BlockPos> growablePlantPos = Lists.newArrayList();
    private final List<BlockPos> totalPlantPos = Lists.newArrayList();
    protected final Object2IntMap<Enchantment> enchantments = new Object2IntOpenHashMap<>();

    public SculkGrowerBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.SCULK_GROWER.get(), p_155229_, p_155230_);
    }

    @Override
    public Object2IntMap<Enchantment> getEnchantments(){
        return this.enchantments;
    }

    public void tick(){
        if (this.level != null && !this.level.isClientSide){
            if (this.checkCage()){
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(SculkGrowerBlock.LIT, true), 3);
            } else {
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(SculkGrowerBlock.LIT, false), 3);
            }
            this.scanTotalPlants();
            if (this.level.getGameTime() % 5 == 0){
                this.findPlants();
            }
            if (this.growCharges <= 0) {
                if (!this.growablePlantPos.isEmpty() && this.checkCage()) {
                    if (this.takeSouls()) {
                        this.growCharges = MainConfig.SculkGrowerCharge.get();
                    }
                }
            } else {
                this.growPlants();
            }
            if (this.totalPlantPos.isEmpty() || this.decayTimer <= 0){
                this.decayCharges();
            }
        }
    }

    public void commonTick(){
        if (this.level != null && !this.level.isClientSide){
            if (this.growCharges <= 0){
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(SculkGrowerBlock.CHARGED, false), 3);
            } else {
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(SculkGrowerBlock.CHARGED, true), 3);
            }
        }
    }

    public void decayCharges(){
        if (this.level != null && this.level instanceof ServerLevel serverLevel) {
            if (this.growCharges > 0 && this.level.getGameTime() % 10 == 0){
                --this.growCharges;
                double d0 = 0.5625D;
                float f5 = 0.15F + 0.02F * serverLevel.random.nextFloat();
                float f = 0.4F + 0.3F * serverLevel.random.nextFloat();
                ModNetwork.INSTANCE.send(PacketDistributor.ALL.noArg(), new SPlayWorldSoundPacket(this.getBlockPos(), SoundEvents.SCULK_BLOCK_CHARGE, f5, f));
                for(Direction direction : Direction.values()) {
                    BlockPos blockpos = this.getBlockPos().relative(direction);
                    if (!serverLevel.getBlockState(blockpos).isSolidRender(serverLevel, blockpos)) {
                        Direction.Axis direction$axis = direction.getAxis();
                        double d1 = direction$axis == Direction.Axis.X ? 0.5D + d0 * (double)direction.getStepX() : (double)serverLevel.random.nextFloat();
                        double d2 = direction$axis == Direction.Axis.Y ? 0.5D + d0 * (double)direction.getStepY() : (double)serverLevel.random.nextFloat();
                        double d3 = direction$axis == Direction.Axis.Z ? 0.5D + d0 * (double)direction.getStepZ() : (double)serverLevel.random.nextFloat();
                        serverLevel.sendParticles(ParticleTypes.SCULK_CHARGE_POP, (double) this.getBlockPos().getX() + d1, (double) this.getBlockPos().getY() + d2, (double) this.getBlockPos().getZ() + d3, 2, 0.2D, 0.0D, 0.2D, 0.0D);
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void scanTotalPlants(){
        if (this.level != null) {
            this.totalPlantPos.clear();
            int distance = 4 + this.enchantments.getOrDefault(ModEnchantments.RADIUS.get(), 0);
            int y = distance / 2;
            for (int i = -distance; i <= distance; ++i) {
                for (int j = -y; j <= y; ++j) {
                    for (int k = -distance; k <= distance; ++k) {
                        BlockPos blockPos = this.getBlockPos().offset(i, j, k);
                        if (this.level.hasChunkAt(blockPos)) {
                            BlockState blockState = this.level.getBlockState(blockPos);
                            if (!blockState.isAir()) {
                                if (isCrops(blockPos)) {
                                    if (!this.totalPlantPos.contains(blockPos)) {
                                        this.totalPlantPos.add(blockPos);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void findPlants(){
        if (this.level != null && this.level instanceof ServerLevel serverLevel) {
            this.growablePlantPos.clear();
            int distance = 5 + this.enchantments.getOrDefault(ModEnchantments.RADIUS.get(), 0);
            int x = serverLevel.random.nextInt(distance) * (serverLevel.random.nextBoolean() ? -1 : 1);
            int y = distance / 2;
            int z = serverLevel.random.nextInt(distance) * (serverLevel.random.nextBoolean() ? -1 : 1);
            for (int i = -y; i < y; ++i) {
                BlockPos blockPos = this.getBlockPos().offset(x, i, z);
                if (this.level.hasChunkAt(blockPos)) {
                    if (isCrops(blockPos)) {
                        this.growablePlantPos.add(blockPos);
                    }
                }
            }
        }
    }

    public void growPlants(){
        if (this.level != null && this.level instanceof ServerLevel serverLevel) {
            if (!this.growablePlantPos.isEmpty()) {
                int potency = 1 + this.enchantments.getOrDefault(ModEnchantments.POTENCY.get(), 0);
                if (!MainConfig.SculkGrowerPotency.get()){
                    potency = 1;
                }
                int random = serverLevel.random.nextInt(this.growablePlantPos.size());
                BlockPos blockPos = this.growablePlantPos.get(random);
                BlockState blockState = serverLevel.getBlockState(blockPos);
                if (!blockState.isAir()) {
                    --this.growCharges;
                    serverLevel.sendParticles(new SculkChargeParticleOptions(0.0F), (double) blockPos.getX() + 0.5D, (double) blockPos.getY(), (double) blockPos.getZ() + 0.5D, 2, 0.2D, 0.0D, 0.2D, 0.0D);
                    float f5 = 0.15F + 0.02F * serverLevel.random.nextFloat();
                    float f = 0.4F + 0.3F * serverLevel.random.nextFloat();
                    ModNetwork.INSTANCE.send(PacketDistributor.ALL.noArg(), new SPlayWorldSoundPacket(blockPos, SoundEvents.SCULK_BLOCK_CHARGE, f5, f));
                    for (int i = 0; i < potency; ++i){
                        blockState.randomTick(serverLevel, blockPos, serverLevel.random);
                    }
                    this.decayTimer = 100;
                }
            } else {
                if (this.decayTimer > 0) {
                    --this.decayTimer;
                }
            }
        }
    }

    private boolean isCrops(BlockPos blockPos) {
        assert this.level != null;
        BlockState blockState = this.level.getBlockState(blockPos);
        Block cropBlock = blockState.getBlock();
        return cropBlock != Blocks.GRASS_BLOCK && !(cropBlock instanceof DoublePlantBlock) && cropBlock instanceof BonemealableBlock && ((BonemealableBlock) cropBlock).isValidBonemealTarget(this.level, blockPos, blockState, this.level.isClientSide);
    }

    private boolean takeSouls(){
        assert this.level != null;
        int potency = 1 + this.enchantments.getOrDefault(ModEnchantments.POTENCY.get(), 0);
        if (!MainConfig.SculkGrowerPotency.get()){
            potency = 1;
        }
        int cost = MainConfig.SculkGrowerCost.get() * potency;
        if (this.getCursedCageTile().getSouls() > cost){
            this.getCursedCageTile().decreaseSouls(cost);
            this.getCursedCageTile().generateManyParticles();
            return true;
        } else {
            return false;
        }
    }

    private boolean checkCage() {
        assert this.level != null;
        BlockPos pos = this.getBlockPos().above();
        BlockState blockState = this.level.getBlockState(pos);
        if (blockState.is(ModBlocks.CURSED_CAGE_BLOCK.get())){
            BlockEntity tileentity = this.level.getBlockEntity(pos);
            if (tileentity instanceof CursedCageBlockEntity){
                this.cursedCageTile = (CursedCageBlockEntity) tileentity;
                return !cursedCageTile.getItem().isEmpty();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public int getGrowCharges(){
        return this.growCharges;
    }

    @Override
    public void readNetwork(CompoundTag compoundNBT) {
        this.growCharges = compoundNBT.getInt("GrowCharges");
        this.decayTimer = compoundNBT.getInt("DecayTimer");
        this.loadEnchants(compoundNBT);
    }

    @Override
    public CompoundTag writeNetwork(CompoundTag pCompound) {
        pCompound.putInt("GrowCharges", this.growCharges);
        pCompound.putInt("DecayTimer", this.decayTimer);
        this.saveEnchants(pCompound, ModBlocks.SCULK_GROWER.get().asItem());
        return pCompound;
    }

    public CursedCageBlockEntity getCursedCageTile(){
        return this.cursedCageTile;
    }

}
