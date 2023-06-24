package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.BrewCauldronBlockEntity;
import com.Polarice3.Goety.common.blocks.properties.ModStateProperties;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.BrewUtils;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Based and modified from @MoriyaShiine's Witch Cauldron codes.
 */
public class BrewCauldronBlock extends BaseEntityBlock{
    private static final VoxelShape INSIDE = box(2.0D, 3.0D, 2.0D,
            14.0D, 13.0D, 14.0D);
    private static final VoxelShape LEGS = Shapes.or(box(1.0D, 0.0D, 1.0D,
            2.0D, 3.0D, 2.0D),
            box(14.0D, 0.0D, 1.0D,
                    15.0D, 3.0D, 2.0D),
            box(14.0D, 0.0D, 14.0D,
                    15.0D, 3.0D, 15.0D),
            box(1.0D, 0.0D, 14.0D,
                    2.0D, 3.0D, 15.0D));
    private static final VoxelShape BOTTOM = Shapes.or(box(2.0D, 1.0D, 2.0D,
            14.0D, 3.0D, 14.0D), LEGS);
    private static final VoxelShape BODY = Shapes.or(box(0.0D, 3.0D, 0.0D,
                    16.0D, 13.0D, 2.0D),
            box(14.0D, 3.0D, 2.0D,
                    16.0D, 13.0D, 14.0D),
            box(0.0D, 3.0D, 14.0D,
                    16.0D, 13.0D, 16.0D),
            box(0.0D, 3.0D, 2.0D,
                    2.0D, 13.0D, 14.0D));
    private static final VoxelShape TOP = Shapes.or(
            box(1.0D, 13.0D, 1.0D,
                    15.0D, 15.0D, 2.0D),
            box(14.0D, 13.0D, 2.0D,
                    15.0D, 15.0D, 14.0D),
            box(1.0D, 13.0D, 14.0D,
                    15.0D, 15.0D, 15.0D),
            box(1.0D, 13.0D, 2.0D,
                    2.0D, 15.0D, 14.0D));
    protected static final VoxelShape SHAPE = Shapes.or(BODY, TOP, BOTTOM);
    public static final IntegerProperty LEVEL = ModStateProperties.LEVEL_BREW;
    public static final BooleanProperty FAILED = ModStateProperties.FAILED;

    public BrewCauldronBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_PURPLE)
                .requiresCorrectToolForDrops()
                .strength(2.0F)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0).setValue(FAILED, Boolean.FALSE));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.getBlockEntity(pPos) instanceof BrewCauldronBlockEntity cauldron) {
            ItemStack stack = pPlayer.getItemInHand(pHand);
            boolean bucket = stack.getItem() == Items.BUCKET, waterBucket = stack.getItem() == Items.WATER_BUCKET, glassBottle = stack.getItem() == Items.GLASS_BOTTLE, waterBottle = (stack.getItem() == Items.POTION || stack.getItem() == ModItems.BREW.get()) && PotionUtils.getPotion(stack) == Potions.WATER, apple = stack.getItem() == Items.APPLE, wand = stack.getItem() == ModItems.CAULDRON_LADLE.get();
            if (!pLevel.isClientSide) {
                if (bucket || waterBucket || apple || glassBottle || waterBottle || wand) {
                    int targetLevel = cauldron.getTargetLevel(stack, pPlayer);
                    if (targetLevel > -1) {
                        if (bucket) {
                            ItemHelper.addAndConsumeItem(pPlayer, pHand, new ItemStack(Items.WATER_BUCKET));
                        } else if (waterBucket) {
                            ItemHelper.addAndConsumeItem(pPlayer, pHand, new ItemStack(Items.BUCKET));
                        } else if (apple){
                            if (cauldron.mode == BrewCauldronBlockEntity.Mode.COMPLETED) {
                                ItemStack itemStack = BrewUtils.setCustomEffects(new ItemStack(ModItems.BREW_APPLE.get()), PotionUtils.getCustomEffects(cauldron.getBrew()), BrewUtils.getBrewEffects(cauldron.getBrew()));
                                ItemHelper.addAndConsumeItem(pPlayer, pHand, itemStack);
                            }
                        } else if (glassBottle) {
                            ItemStack bottle = null;
                            if (cauldron.mode == BrewCauldronBlockEntity.Mode.IDLE) {
                                bottle = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
                            } else if (cauldron.mode == BrewCauldronBlockEntity.Mode.COMPLETED) {
                                bottle = cauldron.getBrew();
                            } else if (cauldron.mode == BrewCauldronBlockEntity.Mode.FAILED){
                                bottle = new ItemStack(ModItems.REFUSE_BOTTLE.get());
                            }
                            if (bottle != null) {
                                ItemHelper.addAndConsumeItem(pPlayer, pHand, bottle);
                            }
                        } else if (waterBottle) {
                            ItemHelper.addAndConsumeItem(pPlayer, pHand, new ItemStack(Items.GLASS_BOTTLE));
                        } else if (wand) {
                            cauldron.brew();
                        }
                        if (targetLevel == 0) {
                            cauldron.mode = cauldron.reset();
                        }
                        pLevel.setBlockAndUpdate(pPos, pState.setValue(ModStateProperties.LEVEL_BREW, targetLevel));
                        pLevel.playSound(null, pPos, bucket ? SoundEvents.BUCKET_FILL : waterBucket ? SoundEvents.BUCKET_EMPTY : glassBottle ? SoundEvents.BOTTLE_FILL : SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
                cauldron.markUpdated();
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    public RenderShape getRenderShape(BlockState p_222219_) {
        return RenderShape.MODEL;
    }

    public VoxelShape getShape(BlockState p_151964_, BlockGetter p_151965_, BlockPos p_151966_, CollisionContext p_151967_) {
        return SHAPE;
    }

    public VoxelShape getInteractionShape(BlockState p_151955_, BlockGetter p_151956_, BlockPos p_151957_) {
        return INSIDE;
    }

    protected double getContentHeight(BlockState p_153528_) {
        return (6.0D + (double) p_153528_.getValue(LEVEL) * 3.0D) / 16.0D;
    }

    protected boolean isEntityInsideContent(BlockState p_151980_, BlockPos p_151981_, Entity p_151982_) {
        return p_151982_.getY() < (double)p_151981_.getY() + this.getContentHeight(p_151980_) && p_151982_.getBoundingBox().maxY > (double)p_151981_.getY() + 0.25D;
    }

    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        BrewCauldronBlockEntity blockEntity = (BrewCauldronBlockEntity) pLevel.getBlockEntity(pPos);
        if (!pLevel.isClientSide) {
            if (blockEntity != null) {
                if (pLevel.getBlockState(pPos).getValue(LEVEL) > 0 && pEntity instanceof LivingEntity && this.isEntityInsideContent(pState, pPos, pEntity)) {
                    if (blockEntity.isHeated() && !pEntity.fireImmune()) {
                        pEntity.hurt(ModDamageSource.BOILING, 1.0F);
                    }
                }
                if (pLevel.getBlockState(pPos).getValue(LEVEL) == 3 && blockEntity.mode != BrewCauldronBlockEntity.Mode.COMPLETED && pEntity instanceof ItemEntity itemEntity) {
                    pLevel.playSound(null, pPos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.33F, 1.0F);
                    ItemStack stack = itemEntity.getItem();
                    if (stack.getItem().hasCraftingRemainingItem(stack)) {
                        ItemEntity remainder = new ItemEntity(pLevel, pPos.getX() + 0.5, pPos.getY() + 1, pPos.getZ() + 0.5, new ItemStack(stack.getItem().getCraftingRemainingItem()));
                        remainder.setDeltaMovement(Vec3.ZERO);
                        remainder.setNoGravity(true);
                        pLevel.addFreshEntity(remainder);
                    }
                    blockEntity.mode = blockEntity.insertItem(stack.split(1));
                    blockEntity.markUpdated();
                }
            }
        }
        super.entityInside(pState, pLevel, pPos, pEntity);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LEVEL, FAILED);
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new BrewCauldronBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (world, pos, state, blockEntity) -> {
            if (!world.isClientSide) {
                if (blockEntity instanceof BrewCauldronBlockEntity blockEntity1) {
                    blockEntity1.tick();
                }
            }
        };
    }
}
