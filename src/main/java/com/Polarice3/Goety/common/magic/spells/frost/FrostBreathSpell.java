package com.Polarice3.Goety.common.magic.spells.frost;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.BreathingSpells;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.ArrayList;
import java.util.List;

public class FrostBreathSpell extends BreathingSpells {
    public float damage = SpellConfig.FrostBreathDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();

    @Override
    public int defaultSoulCost() {
        return SpellConfig.FrostBreathCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.SNOW_GOLEM_AMBIENT;
    }

    @Override
    public SpellType getSpellType(){
        return SpellType.FROST;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        float enchantment = 0;
        int duration = 1;
        int range = 0;
        if (entityLiving instanceof Player player){
            if (WandUtil.enchantedFocus(player)){
                enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), player);
                duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), player);
                range = WandUtil.getLevels(ModEnchantments.RANGE.get(), player);
            }
        }
        float damage = this.damage + enchantment;
        if (!worldIn.isClientSide) {
            if (CuriosFinder.hasCurio(entityLiving, ModItems.RING_OF_THE_DRAGON.get())) {
                damage *= 2.0F;
                if (SpellConfig.DragonFrostGriefing.get()) {
                    float flameRange = 15.0F * ((float) Math.PI / 180.0F);
                    for (int i = 0; i < 3; i++) {
                        Vec3 cast = entityLiving.getLookAngle().normalize().xRot(worldIn.random.nextFloat() * flameRange * 2 - flameRange).yRot(worldIn.random.nextFloat() * flameRange * 2 - flameRange);
                        HitResult hitResult = worldIn.clip(new ClipContext(entityLiving.getEyePosition(), entityLiving.getEyePosition().add(cast.scale(10)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entityLiving));
                        if (hitResult.getType() == HitResult.Type.BLOCK) {
                            Vec3 pos = hitResult.getLocation().subtract(cast.scale(0.5D));
                            BlockPos blockPos = new BlockPos(pos.x, pos.y, pos.z);
                            if ((worldIn.getBlockState(blockPos).isAir() ||
                                    (BlockFinder.canBeReplaced(worldIn, blockPos)
                                            && worldIn.getFluidState(blockPos).isEmpty()))
                                    && worldIn.getBlockState(blockPos.below()).isSolidRender(worldIn, blockPos.below())) {
                                worldIn.setBlockAndUpdate(blockPos, Blocks.SNOW.defaultBlockState());
                            }
                            BlockState blockstate = Blocks.FROSTED_ICE.defaultBlockState();
                            boolean isFull = worldIn.getBlockState(blockPos).getBlock() == Blocks.WATER && worldIn.getBlockState(blockPos).getValue(LiquidBlock.LEVEL) == 0;
                            if (worldIn.getBlockState(blockPos).getMaterial() == Material.WATER && isFull && blockstate.canSurvive(worldIn, blockPos) && worldIn.isUnobstructed(blockstate, blockPos, CollisionContext.empty()) && !net.minecraftforge.event.ForgeEventFactory.onBlockPlace(entityLiving, net.minecraftforge.common.util.BlockSnapshot.create(worldIn.dimension(), worldIn, blockPos), net.minecraft.core.Direction.UP)) {
                                worldIn.setBlockAndUpdate(blockPos, blockstate);
                                worldIn.scheduleTick(blockPos, Blocks.FROSTED_ICE, Mth.nextInt(worldIn.getRandom(), 60, 120));
                            }
                        }
                    }
                }
            }
            for (Entity target : getTarget(entityLiving, range + 15)) {
                if (target != null) {
                    if (target instanceof LivingEntity livingTarget) {
                        if (target.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)){
                            damage *= 2.0F;
                        }
                        if (livingTarget.hurt(ModDamageSource.frostBreath(entityLiving, entityLiving), damage)) {
                            livingTarget.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks(1) * duration));
                        }
                    }
                }
            }
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.PLAYER_BREATH, this.getSoundSource(), worldIn.random.nextFloat() * 0.5F, worldIn.random.nextFloat() * 0.5F);
    }

    @Override
    public ParticleOptions getParticle() {
        return ParticleTypes.POOF;
    }

    @Override
    public void showWandBreath(LivingEntity entityLiving) {
        int range = 0;
        if (entityLiving instanceof Player player){
            if (WandUtil.enchantedFocus(player)){
                range = WandUtil.getLevels(ModEnchantments.RANGE.get(), player);
            }
        }
        if (!CuriosFinder.hasCurio(entityLiving, ModItems.RING_OF_THE_DRAGON.get())) {
            this.breathAttack(entityLiving, 0.3F + ((double) range / 10), 5);
        } else {
            this.dragonBreathAttack(entityLiving, 0.3F + ((double) range / 10));
        }
    }
}
