package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.entities.BrewCauldronBlockEntity;
import com.Polarice3.Goety.common.blocks.properties.ModStateProperties;
import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.magic.TaglockKit;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BrewUtils;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Based and modified from @MoriyaShiine's Witch Cauldron codes.
 */
@SuppressWarnings("deprecation")
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
            14.0D, 4.0D, 14.0D), LEGS);
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
        super(Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .requiresCorrectToolForDrops()
                .strength(2.0F)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0).setValue(FAILED, Boolean.FALSE));
    }

    public void tick(BlockState p_220702_, ServerLevel p_220703_, BlockPos p_220704_, RandomSource p_220705_) {
        BlockPos blockpos = PointedDripstoneBlock.findStalactiteTipAboveCauldron(p_220703_, p_220704_);
        if (blockpos != null) {
            Fluid fluid = PointedDripstoneBlock.getCauldronFillFluidType(p_220703_, blockpos);
            if (fluid == Fluids.WATER) {
                this.receiveStalactiteDrip(p_220702_, p_220703_, p_220704_, fluid);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.getBlockEntity(pPos) instanceof BrewCauldronBlockEntity cauldron) {
            ItemStack stack = pPlayer.getItemInHand(pHand);
            boolean bucket = ItemHelper.isValidFluidContainerToFill(stack, Fluids.WATER), waterBucket = ItemHelper.isValidFluidContainerToDrain(stack, Fluids.WATER), glassBottle = stack.getItem() == Items.GLASS_BOTTLE, waterBottle = (stack.getItem() == Items.POTION || stack.getItem() == ModItems.BREW.get()) && PotionUtils.getPotion(stack) == Potions.WATER, apple = BrewUtils.brewableFood(stack), ladle = stack.getItem() == ModItems.CAULDRON_LADLE.get();
            boolean taglock = stack.getItem() instanceof TaglockKit && TaglockKit.hasEntity(stack);
            boolean playSound = false;
            if (!pLevel.isClientSide) {
                if (bucket || waterBucket || apple || taglock || glassBottle || waterBottle || ladle) {
                    int targetLevel = cauldron.getTargetLevel(stack, pPlayer);
                    if (targetLevel > -1) {
                        if (bucket) {
                            ItemHelper.addAndConsumeItem(pPlayer, pHand, ItemHelper.fill(Fluids.WATER, stack), false);
                            playSound = true;
                        } else if (waterBucket) {
                            ItemHelper.addAndConsumeItem(pPlayer, pHand, ItemHelper.drain(Fluids.WATER, stack), false);
                            playSound = true;
                        } else if (apple){
                            if (cauldron.mode == BrewCauldronBlockEntity.Mode.COMPLETED) {
                                ItemStack itemStack = BrewUtils.setCustomEffects(stack.copy(), PotionUtils.getCustomEffects(cauldron.getBrew()), BrewUtils.getBrewEffects(cauldron.getBrew()));
                                ItemHelper.addAndConsumeItem(pPlayer, pHand, itemStack);
                                SEHelper.increaseBottling(pPlayer);
                                playSound = true;
                            }
                        } else if (taglock && pState.getValue(LEVEL) >= 3){
                            if (cauldron.mode == BrewCauldronBlockEntity.Mode.COMPLETED) {
                                LivingEntity target = TaglockKit.getEntity(stack);
                                if (target != null){
                                    if (TaglockKit.isSameDimension(pPlayer, stack)
                                            && TaglockKit.isInRange(Vec3.atCenterOf(pPos), stack, BrewCauldronBlockEntity.getWitchPoles(cauldron))) {
                                        List<MobEffectInstance> list = PotionUtils.getMobEffects(cauldron.getBrew());
                                        List<BrewEffectInstance> list1 = BrewUtils.getBrewEffects(cauldron.getBrew());
                                        if (!list.isEmpty()) {
                                            for (MobEffectInstance mobeffectinstance : list) {
                                                MobEffect mobeffect = mobeffectinstance.getEffect();
                                                if (mobeffect.isInstantenous()) {
                                                    mobeffect.applyInstantenousEffect(pPlayer, pPlayer, target, mobeffectinstance.getAmplifier(), 1.0F);
                                                } else {
                                                    int i = (int) (1.0F * (double) mobeffectinstance.getDuration() + 0.5D);
                                                    if (i > 20) {
                                                        target.addEffect(new MobEffectInstance(mobeffect, i, mobeffectinstance.getAmplifier(), mobeffectinstance.isAmbient(), mobeffectinstance.isVisible()), pPlayer);
                                                    }
                                                }
                                            }
                                        }
                                        if (!list1.isEmpty()) {
                                            for (BrewEffectInstance brewEffectInstance : list1) {
                                                BrewEffect brewEffect = brewEffectInstance.getEffect();
                                                if (brewEffect.isInstantenous()) {
                                                    brewEffect.applyInstantenousEffect(pPlayer, pPlayer, target, brewEffectInstance.getAmplifier(), 1.0F);
                                                }
                                            }
                                        }
                                        pLevel.playSound(null, pPos, ModSounds.CAST_SPELL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                                        ItemHelper.addAndConsumeItem(pPlayer, pHand, new ItemStack(Items.GLASS_BOTTLE));
                                        SEHelper.increaseBottling(pPlayer, 5);
                                        playSound = true;
                                    } else {
                                        pLevel.playSound(null, pPos, ModSounds.SPELL_FAIL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                                        pPlayer.displayClientMessage(Component.translatable("info.goety.taglock.difDimension"), true);
                                    }
                                }
                            }
                        } else if (glassBottle) {
                            ItemStack bottle = null;
                            if (cauldron.mode == BrewCauldronBlockEntity.Mode.IDLE) {
                                bottle = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
                            } else if (cauldron.mode == BrewCauldronBlockEntity.Mode.COMPLETED) {
                                SEHelper.increaseBottling(pPlayer);
                                bottle = cauldron.getBrew();
                            } else if (cauldron.mode == BrewCauldronBlockEntity.Mode.FAILED){
                                bottle = new ItemStack(ModItems.REFUSE_BOTTLE.get());
                            }
                            if (bottle != null) {
                                ItemHelper.addAndConsumeItem(pPlayer, pHand, bottle);
                                playSound = true;
                            }
                        } else if (waterBottle) {
                            ItemHelper.addAndConsumeItem(pPlayer, pHand, new ItemStack(Items.GLASS_BOTTLE));
                            playSound = true;
                        } else if (ladle) {
                            cauldron.brew();
                        }
                        if (targetLevel == 0) {
                            cauldron.mode = cauldron.reset();
                        }
                        pLevel.setBlockAndUpdate(pPos, pState.setValue(ModStateProperties.LEVEL_BREW, targetLevel));
                        if (playSound) {
                            pLevel.playSound(null, pPos, bucket ? SoundEvents.BUCKET_FILL : waterBucket ? SoundEvents.BUCKET_EMPTY : glassBottle ? SoundEvents.BOTTLE_FILL : SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
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
                        boolean flag = pEntity.hurt(ModDamageSource.getDamageSource(pLevel, ModDamageSource.BOILING), 1.0F);
                        if (flag && pLevel.getBlockState(pPos).getValue(LEVEL) == 3 && !pEntity.isAlive()){
                            blockEntity.mode = blockEntity.addSacrifice(pEntity);
                            blockEntity.markUpdated();
                        }
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

    @Override
    public void handlePrecipitation(BlockState blockState, Level level, BlockPos blockPos, Biome.Precipitation precipitation) {
        if (precipitation == Biome.Precipitation.RAIN) {
            if (level.getRandom().nextInt(20) == 1) {
                if (blockState.getValue(LEVEL) < 3) {
                    level.setBlockAndUpdate(blockPos, blockState.setValue(LEVEL, blockState.getValue(LEVEL) + 1));
                }
            }
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LEVEL, FAILED);
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    protected void receiveStalactiteDrip(BlockState p_152940_, Level p_152941_, BlockPos p_152942_, Fluid p_152943_) {
        if (p_152943_ == Fluids.WATER) {
            if (p_152940_.getValue(LEVEL) < 3) {
                p_152941_.setBlockAndUpdate(p_152942_, p_152940_.setValue(LEVEL, p_152940_.getValue(LEVEL) + 1));
            }
            p_152941_.gameEvent(GameEvent.BLOCK_CHANGE, p_152942_, GameEvent.Context.of(p_152940_));
            p_152941_.levelEvent(1047, p_152942_, 0);
        }

    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return state.getValue(LEVEL) == 3 ? 15 : state.getValue(LEVEL) == 2 ? 10 : state.getValue(LEVEL) == 1 ? 5 : 0;
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
