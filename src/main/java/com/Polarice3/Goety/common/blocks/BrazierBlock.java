package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BrazierBlock extends Block implements SimpleWaterloggedBlock {
    protected static final VoxelShape SHAPE_BASE_1 = Block.box(1.0D, 0.0D, 1.0D,
            15.0D, 1.0D, 15.0D);
    protected static final VoxelShape SHAPE_BASE_2 = Block.box(2.0D, 1.0D, 2.0D,
            14.0D, 2.0D, 14.0D);
    protected static final VoxelShape SHAPE_HOLD_1 = Block.box(7.0D, 2.0D, 2.0D,
            9.0D, 7.0D, 3.0D);
    protected static final VoxelShape SHAPE_HOLD_2 = Block.box(13.0D, 2.0D, 7.0D,
            14.0D, 7.0D, 9.0D);
    protected static final VoxelShape SHAPE_HOLD_3 = Block.box(7.0D, 2.0D, 13.0D,
            9.0D, 7.0D, 14.0D);
    protected static final VoxelShape SHAPE_HOLD_4 = Block.box(2.0D, 2.0D, 7.0D,
            3.0D, 7.0D, 9.0D);
    protected static final VoxelShape SHAPE_TOP_BASE = Block.box(3.0D, 5.0D, 3.0D,
            13.0D, 7.0D, 13.0D);
    protected static final VoxelShape SHAPE_BARS_1 = Block.box(3.0D, 7.0D, 3.0D,
            13.0D, 11.0D, 3.0D);
    protected static final VoxelShape SHAPE_BARS_2 = Block.box(13.0D, 7.0D, 3.0D,
            13.0D, 11.0D, 13.0D);
    protected static final VoxelShape SHAPE_BARS_3 = Block.box(3.0D, 7.0D, 13.0D,
            13.0D, 11.0D, 13.0D);
    protected static final VoxelShape SHAPE_BARS_4 = Block.box(3.0D, 7.0D, 3.0D,
            3.0D, 11.0D, 13.0D);
    protected static final VoxelShape SHAPE_BURNER = Block.box(4.0D, 7.0D, 4.0D,
            12.0D, 8.0D, 12.0D);
    public static final VoxelShape SHAPE_HOLDER = Shapes.or(SHAPE_HOLD_1, SHAPE_HOLD_2, SHAPE_HOLD_3, SHAPE_HOLD_4);
    public static final VoxelShape SHAPE_BASE = Shapes.or(SHAPE_BASE_1, SHAPE_BASE_2);
    public static final VoxelShape SHAPE_BARS = Shapes.or(SHAPE_BARS_1, SHAPE_BARS_2, SHAPE_BARS_3, SHAPE_BARS_4);
    public static final VoxelShape SHAPE_TOP = Shapes.or(SHAPE_TOP_BASE, SHAPE_BARS, SHAPE_BURNER);
    public static final VoxelShape SHAPE = Shapes.or(SHAPE_BASE, SHAPE_HOLDER, SHAPE_TOP);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public boolean soulFire;

    public BrazierBlock() {
        this(false);
    }

    public BrazierBlock(boolean soulFire) {
        super(Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.5F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.CHAIN)
                .lightLevel((state) -> {
                    int light = 14;
                    if (soulFire){
                        light = 10;
                    }
                    return state.getValue(BlockStateProperties.LIT) ? light : 0;
                })
                .noOcclusion()
        );
        this.soulFire = soulFire;
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE).setValue(LIT, Boolean.TRUE));
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        RandomSource randomsource = pLevel.getRandom();
        if (canLight(pState)){
            pLevel.playSound((Player)null, pPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F);
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)));
        } else {
            pLevel.playSound((Player)null, pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, Boolean.valueOf(false)));
        }
        return InteractionResult.SUCCESS;
    }

    public static boolean canLight(BlockState p_51322_) {
        return !p_51322_.getValue(LIT);
    }

    public RenderShape getRenderShape(BlockState p_222219_) {
        return RenderShape.MODEL;
    }

    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (pState.getValue(LIT)) {
            if (!pEntity.fireImmune() && pEntity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) pEntity)
                    && pEntity.getY() >= pPos.getY() + 0.5F) {
                float damage = 1.0F;
                if (this.soulFire){
                    damage = 2.0F;
                }
                pEntity.hurt(pEntity.damageSources().inFire(), damage);
            }
        }
        super.entityInside(pState, pLevel, pPos, pEntity);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        LevelAccessor iworld = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        boolean flag = iworld.getFluidState(blockpos).getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, flag);
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState) {
        if (!pState.getValue(BlockStateProperties.WATERLOGGED) && pFluidState.getType() == Fluids.WATER) {
            pLevel.setBlock(pPos, pState.setValue(WATERLOGGED, Boolean.TRUE), 3);
            pLevel.scheduleTick(pPos, pFluidState.getType(), pFluidState.getType().getTickDelay(pLevel));
            return true;
        } else {
            return false;
        }
    }

    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED, LIT);
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    public BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return state.getValue(LIT) ? BlockPathTypes.DAMAGE_FIRE : super.getBlockPathType(state, level, pos, mob);
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRand) {
        double d0 = (double)pPos.getX() + pRand.nextDouble();
        double d1 = (double)pPos.getY() + pRand.nextDouble();
        double d2 = (double)pPos.getZ() + pRand.nextDouble();
        ParticleOptions particleOptions = ModParticleTypes.BIG_FIRE.get();
        ParticleOptions particleOptions2 = ModParticleTypes.BIG_FIRE_DROP.get();
        if (this.soulFire){
            particleOptions = ModParticleTypes.BIG_SOUL_FIRE.get();
            particleOptions2 = ModParticleTypes.BIG_SOUL_FIRE_DROP.get();
        }
        if (pState.getValue(LIT)) {
            if (pState.getValue(WATERLOGGED)) {
                if (pLevel.getFluidState(pPos.above()).isEmpty() && pLevel.getBlockState(pPos.above()).isAir()) {
                    pLevel.addParticle(particleOptions, pPos.getX() + 0.5F, pPos.getY() + 1.0F, pPos.getZ() + 0.5F, 0, 0, 0);
                }
            } else {
                if (pLevel.getFluidState(pPos.above()).isEmpty() && pLevel.getBlockState(pPos.above()).isAir()) {
                    for (int p = 0; p < 4; ++p) {
                        pLevel.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 5.0E-4D, 0.0D);
                    }
                    pLevel.addParticle(particleOptions, pPos.getX() + 0.5F, pPos.getY() + 1.0F, pPos.getZ() + 0.5F, 0, 0, 0);
                    pLevel.addParticle(particleOptions2, pPos.getX() + 0.5F, pPos.getY() + 1.0F, pPos.getZ() + 0.5F, 0, 0, 0);
                }
            }
            pLevel.playSound(null, pPos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + pLevel.random.nextFloat(), pLevel.random.nextFloat() * 0.7F + 0.3F);
        }
    }
}
