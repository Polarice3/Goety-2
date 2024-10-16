package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.BreathingSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class FireBreathSpell extends BreathingSpell {
    public float damage = SpellConfig.FireBreathDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();

    @Override
    public int defaultSoulCost() {
        return SpellConfig.FireBreathCost.get();
    }

    @Override
    public int defaultCastUp() {
        return SpellConfig.FireBreathChargeUp.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.FIRE_BREATH_START.get();
    }

    public SoundEvent loopSound(LivingEntity livingEntity){
        return ModSounds.FIRE_BREATH.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.BURNING.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        float enchantment = 0;
        int burning = 1;
        int range = 0;
        if (WandUtil.enchantedFocus(entityLiving)) {
            enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
            burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), entityLiving);
            range = WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
        }
        float damage = this.damage + enchantment;
        if (!worldIn.isClientSide) {
            if (CuriosFinder.hasCurio(entityLiving, ModItems.RING_OF_THE_DRAGON.get())) {
                damage *= 2.0F;
                if (SpellConfig.DragonFireGriefing.get()) {
                    float flameRange = 15.0F * ((float) Math.PI / 180.0F);
                    for (int i = 0; i < 3; i++) {
                        Vec3 cast = entityLiving.getLookAngle().normalize().xRot(worldIn.random.nextFloat() * flameRange * 2 - flameRange).yRot(worldIn.random.nextFloat() * flameRange * 2 - flameRange);
                        HitResult hitResult = worldIn.clip(new ClipContext(entityLiving.getEyePosition(), entityLiving.getEyePosition().add(cast.scale(10)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entityLiving));
                        if (hitResult.getType() == HitResult.Type.BLOCK) {
                            Vec3 pos = hitResult.getLocation().subtract(cast.scale(0.5D));
                            BlockPos blockPos = BlockPos.containing(pos.x, pos.y, pos.z);
                            if (worldIn.getBlockState(blockPos).isAir() ||
                                    (BlockFinder.canBeReplaced(worldIn, blockPos)
                                            && worldIn.getFluidState(blockPos).isEmpty()
                                            && Blocks.FIRE.defaultBlockState().canSurvive(worldIn, blockPos))) {
                                worldIn.setBlockAndUpdate(blockPos, BaseFireBlock.getState(worldIn, blockPos));
                            }
                        }
                    }
                }
            }
            for (Entity target : getBreathTarget(entityLiving, range + 15)) {
                if (target != null) {
                    DamageSource damageSource = ModDamageSource.fireBreath(entityLiving, entityLiving);
                    if (CuriosFinder.hasNetherRobe(entityLiving)){
                        damageSource = ModDamageSource.magicFireBreath(entityLiving, entityLiving);
                    }
                    if (target.hurt(damageSource, damage)){
                        target.setSecondsOnFire(5 * burning);
                    }
                }
            }
        }
    }

    @Override
    public void showWandBreath(LivingEntity entityLiving) {
        int range = 0;
        if (WandUtil.enchantedFocus(entityLiving)){
            range = WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
        }

        if (!CuriosFinder.hasCurio(entityLiving, ModItems.RING_OF_THE_DRAGON.get())) {
            this.breathAttack(ParticleTypes.SOUL_FIRE_FLAME, entityLiving, 0.3F + ((double) range / 10), 5);
        } else {
            this.dragonBreathAttack(ModParticleTypes.DRAGON_FLAME.get(), entityLiving, 0.3F + ((double) range / 10));
        }
    }
}
