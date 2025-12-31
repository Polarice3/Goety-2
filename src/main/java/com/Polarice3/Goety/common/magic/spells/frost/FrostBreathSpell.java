package com.Polarice3.Goety.common.magic.spells.frost;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.BreathingSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.ArrayList;
import java.util.List;

public class FrostBreathSpell extends BreathingSpell {
    public float damage = SpellConfig.FrostBreathDamage.get().floatValue() * WandUtil.damageMultiply();

    @Override
    public SpellStat defaultStats() {
        return new SpellStat(0, 1, 8, 0.0D, 0, 0.0F);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.FrostBreathCost.get();
    }

    @Override
    public int defaultCastUp() {
        return SpellConfig.FrostBreathChargeUp.get();
    }

    @Override
    public int shotsNumber() {
        return SpellConfig.FrostBreathDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.FrostBreathCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return null;
    }

    @Override
    public SpellType getSpellType(){
        return SpellType.FROST;
    }

    @Override
    public void useParticle(Level worldIn, LivingEntity caster, ItemStack stack) {
        if (worldIn instanceof ServerLevel serverLevel){
            ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ParticleTypes.CLOUD, caster);
        }
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity caster, SpellStat spellStat) {
        if (caster instanceof Mob mob){
            if (mob.getTarget() != null){
                int range = spellStat.getRange();
                if (WandUtil.enchantedFocus(caster)){
                    range += WandUtil.getRangeLevel(caster);
                }
                return mob.hasLineOfSight(mob.getTarget()) && mob.distanceTo(mob.getTarget()) <= range + 4.0D;
            }
        }
        return super.conditionsMet(worldIn, caster, spellStat);
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        float potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        int range = spellStat.getRange();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
            range += WandUtil.getRangeLevel(caster);
        }
        float damage = this.damage + potency;
        if (!worldIn.isClientSide) {
            if (CuriosFinder.hasCurio(caster, ModItems.RING_OF_THE_DRAGON.get())) {
                damage *= 2.0F;
                if (SpellConfig.DragonFrostGriefing.get()) {
                    float flameRange = range * ((float) Math.PI / 180.0F);
                    for (int i = 0; i < 3; i++) {
                        Vec3 cast = caster.getLookAngle().normalize().xRot(worldIn.random.nextFloat() * flameRange * 2 - flameRange).yRot(worldIn.random.nextFloat() * flameRange * 2 - flameRange);
                        HitResult hitResult = worldIn.clip(new ClipContext(caster.getEyePosition(), caster.getEyePosition().add(cast.scale(10)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
                        if (hitResult.getType() == HitResult.Type.BLOCK) {
                            Vec3 pos = hitResult.getLocation().subtract(cast.scale(0.5D));
                            BlockPos blockPos = BlockPos.containing(pos.x, pos.y, pos.z);
                            if ((worldIn.getBlockState(blockPos).isAir() ||
                                    (BlockFinder.canBeReplaced(worldIn, blockPos)
                                            && worldIn.getFluidState(blockPos).isEmpty()))
                                    && worldIn.getBlockState(blockPos.below()).isSolidRender(worldIn, blockPos.below())) {
                                worldIn.setBlockAndUpdate(blockPos, Blocks.SNOW.defaultBlockState());
                            }
                            BlockState blockstate = Blocks.FROSTED_ICE.defaultBlockState();
                            if (worldIn.getBlockState(blockPos) == FrostedIceBlock.meltsInto() && blockstate.canSurvive(worldIn, blockPos) && worldIn.isUnobstructed(blockstate, blockPos, CollisionContext.empty()) && !net.minecraftforge.event.ForgeEventFactory.onBlockPlace(caster, net.minecraftforge.common.util.BlockSnapshot.create(worldIn.dimension(), worldIn, blockPos), net.minecraft.core.Direction.UP)) {
                                worldIn.setBlockAndUpdate(blockPos, blockstate);
                                worldIn.scheduleTick(blockPos, Blocks.FROSTED_ICE, Mth.nextInt(caster.getRandom(), 60, 120));
                            }
                        }
                    }
                }
            }
            for (Entity target : getBreathTarget(caster, range)) {
                LivingEntity livingEntity = MobUtil.getLivingTarget(target);
                if (livingEntity != null) {
                    if (livingEntity.hurt(ModDamageSource.frostBreath(caster, caster), damage)) {
                        livingEntity.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks(1) * duration));
                    }
                }
            }
        }
        worldIn.playSound(null, caster.getX(), caster.getY(), caster.getZ(), ModSounds.FROST_BREATH.get(), this.getSoundSource(), worldIn.random.nextFloat() * 0.5F, worldIn.random.nextFloat() * 0.5F);
    }

    @Override
    public void showWandBreath(LivingEntity entityLiving, ItemStack staff, SpellStat spellStat) {
        int range = 0;
        if (WandUtil.enchantedFocus(entityLiving)){
            range = WandUtil.getRangeLevel(entityLiving);
        }
        if (!CuriosFinder.hasCurio(entityLiving, ModItems.RING_OF_THE_DRAGON.get())) {
            this.breathAttack(ParticleTypes.POOF, entityLiving, 0.3F + ((double) range / 10), 5);
        } else {
            this.dragonBreathAttack(ModParticleTypes.FROST.get(), entityLiving, 0.3F + ((double) range / 10));
        }
    }
}
