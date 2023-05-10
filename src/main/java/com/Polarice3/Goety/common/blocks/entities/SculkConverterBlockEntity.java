package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.SculkConverterBlock;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SculkConverterBlockEntity extends ModBlockEntity implements IEnchantedBlock{
    private final SculkSpreader sculkSpreader = SculkSpreader.createLevelSpreader();
    private CursedCageBlockEntity cursedCageTile;
    protected final Object2IntMap<Enchantment> enchantments = new Object2IntOpenHashMap<>();

    public SculkConverterBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.SCULK_CONVERTER.get(), p_155229_, p_155230_);
    }

    public Object2IntMap<Enchantment> getEnchantments(){
        return this.enchantments;
    }

    public void activate(){
        if (this.level != null && this.level instanceof ServerLevel serverLevel) {
            if (this.checkCage()) {
                int spread = this.enchantments.getOrDefault(ModEnchantments.POTENCY.get(), 0) + 1;
                int cost = 5 * spread;
                if (this.getCursedCageTile().getSouls() > cost) {
                    this.getCursedCageTile().decreaseSouls(cost);
                    int x = serverLevel.getRandom().nextInt(-1, 1);
                    int z = serverLevel.getRandom().nextInt(-1, 1);
                    BlockPos blockPos = new BlockPos(this.getBlockPos().getX() + x, this.getBlockPos().above().getY(), this.getBlockPos().getZ() + z);
                    this.sculkSpreader.addCursors(blockPos, spread);
                    SculkConverterBlock.bloom(serverLevel, this.getBlockPos(), this.getBlockState(), serverLevel.random);
                }
            }
        }
    }

    public void tick(){
        if (this.level != null) {
            if (!this.level.isClientSide) {
                if (!this.checkCage()) {
                    this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(SculkConverterBlock.LIT, false), 3);
                } else {
                    this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(SculkConverterBlock.LIT, true), 3);
                }
                if (!this.sculkSpreader.getCursors().isEmpty()) {
                    this.sculkSpreader.updateCursors(this.level, this.getBlockPos(), this.level.getRandom(), true);
                }
            }
        }
    }

    private boolean checkCage() {
        assert this.level != null;
        BlockPos pos = new BlockPos(this.getBlockPos().getX(), this.getBlockPos().getY() + 1, this.getBlockPos().getZ());
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

    public void readNetwork(CompoundTag tag) {
        this.sculkSpreader.load(tag);
        this.loadEnchants(tag);
    }

    public CompoundTag writeNetwork(CompoundTag tag) {
        this.sculkSpreader.save(tag);
        this.saveEnchants(tag, ModBlocks.SCULK_CONVERTER_ITEM.get());
        return tag;
    }

    public CursedCageBlockEntity getCursedCageTile(){
        return this.cursedCageTile;
    }
}
