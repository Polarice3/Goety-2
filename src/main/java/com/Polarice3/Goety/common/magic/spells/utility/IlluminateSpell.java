package com.Polarice3.Goety.common.magic.spells.utility;

import com.Polarice3.Goety.client.particles.GatherTrailParticle;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.List;
import java.util.stream.Stream;

//Based on @P3pp3rF1y's codes:https://github.com/P3pp3rF1y/Reliquary/blob/1.20.x/src/main/java/reliquary/items/LanternOfParanoiaItem.java
public class IlluminateSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRadius(16.0D);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.IlluminateCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return 72000;
    }

    @Override
    public int defaultSpellCooldown() {
        return 20;
    }

    @Override
    public void useParticle(Level worldIn, LivingEntity caster, ItemStack stack) {
        if (worldIn instanceof ServerLevel serverLevel){
            int range = 1;
            ColorUtil colorUtil = new ColorUtil(ChatFormatting.YELLOW);
            ServerParticleUtil.gatheringParticles(new GatherTrailParticle.Option(colorUtil, caster.position().add(0, 2, 0)), caster, serverLevel, range);
        }
    }

    @Override
    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int castTime, SpellStat spellStat) {
        if (castTime >= SpellConfig.IlluminateChargeUp.get()) {
            double radius = spellStat.getRadius();
            if (WandUtil.enchantedFocus(caster)) {
                radius *= WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster) / 2.0D;
            }

            int radiusInt = Mth.floor(radius);

            Stream<BlockPos> stream = BlockPos.betweenClosedStream(caster.blockPosition().offset(-radiusInt, -radiusInt / 2, -radiusInt), caster.blockPosition().offset(radiusInt, radiusInt / 2, radiusInt));

            if (castTime % SpellConfig.IlluminateDuration.get() == 0) {
                if (stream.anyMatch(pos -> tryToPlaceAtPos(worldIn, caster, pos) || replaceSoulLight(pos, worldIn))) {
                    ServerParticleUtil.addParticlesAroundMiddleSelf(worldIn, ModParticleTypes.GLOW_EFFECT.get(), caster);
                }
            }
        }
    }

    @Override
    public void stopSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, ItemStack focus, int castTime, SpellStat spellStat) {
        if (caster instanceof Player player && !focus.isEmpty()) {
            SEHelper.addCooldown(player, focus.getItem(), this.spellCooldown(caster));
            SEHelper.sendSEUpdatePacket(player);
        }
    }

    public boolean tryToPlaceAtPos(ServerLevel world, LivingEntity caster, BlockPos pos) {
        int lightLevel = caster.level.getBrightness(LightLayer.BLOCK, pos);
        if (lightLevel > SpellConfig.IlluminateMinLightLevel.get()) {
            return false;
        }

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        BlockPlaceContext context = new BlockPlaceContext(world, null, InteractionHand.MAIN_HAND, ItemStack.EMPTY, new BlockHitResult(Vec3.atBottomCenterOf(pos), Direction.UP, pos, false));
        if (this.isBadPlacementToTry(world, pos, state, block, context)) {
            return false;
        }
        return tryToPlaceGlowLightAround(pos, caster, world);
    }

    public boolean isBadPlacementToTry(ServerLevel world, BlockPos pos, BlockState state, Block block, BlockPlaceContext context) {
        return block instanceof LiquidBlock
                || world.getBlockState(pos.below()).getBlock().hasDynamicShape()
                || !state.getFluidState().isEmpty()
                || (!state.isAir() && !state.canBeReplaced(BlockPlaceContext.at(context, pos, Direction.DOWN)));
    }

    public boolean isBlockBlockingView(ServerLevel world, LivingEntity caster, BlockPos pos) {
        double playerEyeHeight = caster.getY() + caster.getEyeHeight();
        for (float xOff = -0.2F; xOff <= 0.2F; xOff += 0.4F) {
            for (float yOff = -0.2F; yOff <= 0.2F; yOff += 0.4F) {
                for (float zOff = -0.2F; zOff <= 0.2F; zOff += 0.4F) {

                    Vec3 playerVec = new Vec3(caster.getX() + xOff, playerEyeHeight + yOff, caster.getZ() + zOff);
                    Vec3 rayTraceVector = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(0.5D + xOff, 0.5D + yOff, 0.5D + zOff);

                    HitResult rayTraceResult = world.clip(new ClipContext(playerVec, rayTraceVector, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));

                    if (rayTraceResult.getType() == HitResult.Type.MISS) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean tryToPlaceGlowLightAround(BlockPos pos, LivingEntity caster, ServerLevel world) {
        if (isBlockBlockingView(world, caster, pos)) {
            return false;
        }
        return tryToPlaceGlowLight(pos, world);
    }

    public boolean replaceSoulLight(BlockPos pos, ServerLevel world) {
        if (world.getBlockState(pos).is(ModBlocks.SOUL_LIGHT_BLOCK.get())) {
            if (world.setBlock(pos, ModBlocks.GLOW_LIGHT_BLOCK.get().defaultBlockState(), 3)) {
                world.sendParticles(ModParticleTypes.GLOW_EFFECT.get(), pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 1, 0.0F, 0.0F, 0.0F, 0.0F);
                world.playSound(null, pos, SoundEvents.CANDLE_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F);
                return true;
            }
        }
        return false;
    }

    public boolean tryToPlaceGlowLight(BlockPos pos, ServerLevel world) {
        if (ModBlocks.GLOW_LIGHT_BLOCK.get().defaultBlockState().canSurvive(world, pos)) {
            List<Direction> trySides = Lists.newArrayList(Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);
            for (Direction side : trySides) {
                BlockState blockState = world.getBlockState(pos.relative(side));
                if (blockState.isAir() || !world.isUnobstructed(blockState, pos, CollisionContext.empty())) {
                    continue;
                }

                if (world.setBlock(pos, ModBlocks.GLOW_LIGHT_BLOCK.get().defaultBlockState(), 3)) {
                    world.sendParticles(ModParticleTypes.GLOW_EFFECT.get(), pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 1, 0.0F, 0.0F, 0.0F, 0.0F);
                    world.playSound(null, pos, SoundEvents.CANDLE_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F);
                    return true;
                }
            }
        }
        return false;
    }

}
