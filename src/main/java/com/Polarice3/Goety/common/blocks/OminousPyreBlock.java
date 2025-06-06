package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.ModBlockEntities;
import com.Polarice3.Goety.common.blocks.entities.OminousPyreBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class OminousPyreBlock extends BarracksBlock {
    protected static final VoxelShape SHAPE_BASE = Block.box(1.0D, 0.0D, 1.0D,
            15.0D, 2.0D, 15.0D);
    protected static final VoxelShape SHAPE_PILLAR = Block.box(2.0D, 2.0D, 2.0D,
            14.0D, 8.0D, 14.0D);
    protected static final VoxelShape SHAPE_TOP = Block.box(1.0D, 8.0D, 1.0D,
            15.0D, 12.0D, 15.0D);
    public static final VoxelShape SHAPE = Shapes.or(SHAPE_BASE, SHAPE_PILLAR, SHAPE_TOP);

    public OminousPyreBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .strength(2.0F, 6.0F)
                .lightLevel((state) -> state.getValue(BlockStateProperties.POWERED) ? 14 : 0)
                .noOcclusion()
                .dynamicShape()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.FALSE));
    }

    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (pState.getValue(POWERED)) {
            if (!pEntity.fireImmune() && pEntity instanceof LivingEntity livingEntity && !EnchantmentHelper.hasFrostWalker(livingEntity)
                    && livingEntity.getY() >= pPos.getY() + 0.5F && livingEntity.getMobType() != MobType.ILLAGER) {
                float damage = 1.0F;
                pEntity.hurt(pEntity.damageSources().inFire(), damage);
            }
        }
        super.entityInside(pState, pLevel, pPos, pEntity);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POWERED);
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRand) {
        double d0 = (double)pPos.getX() + pRand.nextDouble();
        double d1 = (double)pPos.getY() + 0.75D;
        double d2 = (double)pPos.getZ() + pRand.nextDouble();
        if (pState.getValue(POWERED)) {
            if (pLevel.getFluidState(pPos.above()).isEmpty() && pLevel.getBlockState(pPos.above()).isAir()) {
                for (int p = 0; p < 4; ++p) {
                    pLevel.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 5.0E-4D, 0.0D);
                }
            }
            if (pRand.nextInt(24) == 0) {
                pLevel.playLocalSound(pPos.getX(), pPos.getY(), pPos.getZ(), SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 0.5F + pRand.nextFloat(), pRand.nextFloat() * 0.7F + 0.3F, false);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new OminousPyreBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152755_, BlockState p_152756_, BlockEntityType<T> p_152757_) {
        return createTickerHelper(p_152757_, ModBlockEntities.OMINOUS_PYRE.get(), p_152755_.isClientSide ? OminousPyreBlockEntity::clientTick : OminousPyreBlockEntity::serverTick);
    }
}
