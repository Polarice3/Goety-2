package com.Polarice3.Goety.api.magic;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.curios.MagicHatItem;
import com.Polarice3.Goety.common.items.curios.MagicRobeItem;
import com.Polarice3.Goety.common.items.curios.NecroGarbs;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.compat.serene_seasons.SSeasonsIntegration;
import com.Polarice3.Goety.compat.serene_seasons.SSeasonsLoaded;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.*;

import javax.annotation.Nullable;
import java.util.List;

public interface ISpell {

    default SpellStat defaultStats(){
        return new SpellStat(0, 0, 16, 2.0D, 0, 0.0F);
    }

    int defaultSoulCost();

    @Deprecated(forRemoval = true)
    default int soulCost(LivingEntity caster){
        return ISpell.this.soulCost(caster, ItemStack.EMPTY);
    }

    default int soulCost(LivingEntity caster, ItemStack staff){
        return SoulCalculation(caster);
    }

    default int SoulCalculation(LivingEntity caster){
        int cost = defaultSoulCost() * SoulCostUp(caster);
        BlockPos blockPos = caster.blockPosition();
        Level level = caster.level;
        Holder<Biome> biomeHolder = level.getBiome(blockPos);
        boolean enable = SpellConfig.EnvironmentalCost.get();
        if (SoulDiscount(caster)){
            cost /= 1.15F;
        }
        if (this.getSpellType() == SpellType.FROST){
            if (FrostSoulDiscount(caster)){
                cost /= 2;
            }
            if (enable) {
                if (biomeHolder.get().coldEnoughToSnow(blockPos) || biomeHolder.is(ModTags.Biomes.FROST_DISCOUNT) || (level.isRainingAt(blockPos) && biomeHolder.get().coldEnoughToSnow(blockPos))) {
                    cost /= 1.5F;
                } else if (biomeHolder.is(ModTags.Biomes.FROST_MARKUP)) {
                    cost *= 1.5F;
                } else if (SSeasonsLoaded.SERENE_SEASONS.isLoaded()){
                    if (SSeasonsIntegration.summonSnowVariant(level, blockPos)){
                        cost /= 1.5F;
                    }
                }
            }
        }
        if (this.getSpellType() == SpellType.WIND){
            if (WindSoulDiscount(caster)){
                cost /= 2;
            }
            if (enable) {
                if ((blockPos.getY() >= 128 && level.canSeeSky(blockPos)) || (biomeHolder.is(ModTags.Biomes.WIND_DISCOUNT))) {
                    cost /= 1.5F;
                } else if ((blockPos.getY() <= 32 && !level.canSeeSky(blockPos)) || (biomeHolder.is(ModTags.Biomes.WIND_MARKUP))) {
                    cost *= 1.5F;
                }
            }
        }
        if (this.getSpellType() == SpellType.STORM){
            if (StormSoulDiscount(caster)){
                cost /= 2;
            }
            if (enable) {
                if ((level.canSeeSky(blockPos) && level.isThundering()) || biomeHolder.is(ModTags.Biomes.STORM_DISCOUNT)) {
                    cost /= 1.5F;
                } else if (biomeHolder.is(ModTags.Biomes.STORM_MARKUP)) {
                    cost *= 1.5F;
                }
            }
        }
        if (this.getSpellType() == SpellType.GEOMANCY){
            if (GeoSoulDiscount(caster)){
                cost /= 2;
            }
            if (enable) {
                if ((blockPos.getY() <= 32 || biomeHolder.is(ModTags.Biomes.GEOMANCY_DISCOUNT))) {
                    cost /= 1.5F;
                } else if (blockPos.getY() >= 128 || biomeHolder.is(ModTags.Biomes.GEOMANCY_MARKUP)) {
                    cost *= 1.5F;
                }
            }
        }
        if (this.getSpellType() == SpellType.NETHER){
            if (NetherSoulDiscount(caster)){
                cost /= 2;
            }
            if (enable) {
                if (level.dimension() == Level.NETHER || biomeHolder.is(ModTags.Biomes.NETHER_DISCOUNT)) {
                    cost /= 1.5F;
                } else if (biomeHolder.is(ModTags.Biomes.NETHER_MARKUP)) {
                    cost *= 1.5F;
                }
            }
        }
        if (this.getSpellType() == SpellType.NECROMANCY){
            if (NecroSoulDiscount(caster)){
                cost /= 2;
            }
            if (enable) {
                if (level.getMoonBrightness() > 0.9F || biomeHolder.is(ModTags.Biomes.NECROMANCY_DISCOUNT)
                        || BlockFinder.findStructure(level, blockPos, ModTags.Structures.NECROMANCER_POWER)) {
                    cost /= 1.5F;
                } else if (biomeHolder.is(ModTags.Biomes.NECROMANCY_MARKUP)) {
                    cost *= 1.5F;
                }
            }
        }
        if (this.getSpellType() == SpellType.WILD){
            if (WildSoulDiscount(caster)){
                cost /= 2;
            }
            if (enable) {
                if (biomeHolder.is(ModTags.Biomes.WILD_DISCOUNT)) {
                    cost /= 1.5F;
                } else if (biomeHolder.is(ModTags.Biomes.WILD_MARKUP)) {
                    cost *= 1.5F;
                } else if (SSeasonsLoaded.SERENE_SEASONS.isLoaded()){
                    if (SSeasonsIntegration.summonSnowVariant(level, blockPos)){
                        cost *= 1.5F;
                    }
                }
            }
        }
        if (this.getSpellType() == SpellType.ABYSS){
            if (AbyssSoulDiscount(caster)){
                cost /= 2;
            }
            if (enable) {
                if (biomeHolder.is(ModTags.Biomes.ABYSS_DISCOUNT)) {
                    cost /= 1.5F;
                } else if (biomeHolder.is(ModTags.Biomes.ABYSS_MARKUP)) {
                    cost *= 1.5F;
                }
            }
        }
        if (this.getSpellType() == SpellType.VOID){
            if (VoidSoulDiscount(caster)){
                cost /= 2;
            }
            if (enable) {
                if (biomeHolder.is(ModTags.Biomes.VOID_DISCOUNT)
                        || level.dimension() == Level.END) {
                    cost /= 1.5F;
                } else if (biomeHolder.is(ModTags.Biomes.VOID_MARKUP)) {
                    cost *= 1.5F;
                }
            }
        }
        return cost;
    }

    int defaultCastDuration();

    @Deprecated(forRemoval = true)
    default int castDuration(LivingEntity caster){
        return ISpell.this.castDuration(caster, ItemStack.EMPTY);
    }

    default int castDuration(LivingEntity caster, ItemStack staff){
        if (ReduceCastTime(caster)){
            return defaultCastDuration() / 2;
        } else {
            return defaultCastDuration();
        }
    }

    @Nullable
    default SoundEvent CastingSound(LivingEntity caster){
        return this.CastingSound();
    }

    @Nullable
    default SoundEvent CastingSound() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    default float castingVolume(){
        return 0.5F;
    }

    default float castingPitch(){
        return 1.0F;
    }

    int defaultSpellCooldown();

    default int spellCooldown(){
        return defaultSpellCooldown();
    }

    default void startSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
    }

    default void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int castTime, SpellStat spellStat) {
    }

    @Deprecated(forRemoval = true)
    default void stopSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int useTimeRemaining) {
        stopSpell(worldIn, caster, staff, ItemStack.EMPTY, this.castDuration(caster, staff) - useTimeRemaining, this.defaultStats());
    }

    default void stopSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, ItemStack focus, int castTime, SpellStat spellStat) {
    }

    default void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
    }

    SpellType getSpellType();

    default boolean conditionsMet(ServerLevel worldIn, LivingEntity caster){
        return conditionsMet(worldIn, caster, this.defaultStats());
    }

    default boolean conditionsMet(ServerLevel worldIn, LivingEntity caster, SpellStat spellStat){
        return true;
    }

    List<Enchantment> acceptedEnchantments();

    default SoundEvent loopSound(LivingEntity caster){
        return null;
    }

    default ColorUtil particleColors(LivingEntity caster){
        return new ColorUtil(0.2F, 0.2F, 0.2F);
    }

    @Nullable
    default ParticleOptions getParticle(LivingEntity caster){
        return ParticleTypes.ENTITY_EFFECT;
    }

    default void useParticle(Level worldIn, LivingEntity caster, ItemStack stack){
        double d0 = this.particleColors(caster).red();
        double d1 = this.particleColors(caster).green();
        double d2 = this.particleColors(caster).blue();
        ParticleOptions particle = this.getParticle(caster);
        if (particle != null){
            if (worldIn instanceof ServerLevel serverLevel){
                serverLevel.sendParticles(particle, caster.getX(), caster.getBoundingBox().maxY + 0.5D, caster.getZ(), 0, d0, d1, d2, 0.5F);
            } else {
                worldIn.addParticle(particle, caster.getX(), caster.getBoundingBox().maxY + 0.5D, caster.getZ(), d0, d1, d2);
            }
        }
    }

    @Nullable
    default LivingEntity getTarget(LivingEntity caster){
        return null;
    }

    default HitResult rayTrace(Level worldIn, LivingEntity caster, int range, double radius) {
        if (this.entityResult(worldIn, caster, range, radius) == null){
            return this.blockResult(worldIn, caster, range);
        } else {
            return this.entityResult(worldIn, caster, range, radius);
        }
    }

    default BlockHitResult blockResult(Level worldIn, LivingEntity caster, double range) {
        float f = caster.getXRot();
        float f1 = caster.getYRot();
        Vec3 vector3d = caster.getEyePosition(1.0F);
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 vector3d1 = vector3d.add((double)f6 * range, (double)f5 * range, (double)f7 * range);
        return worldIn.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, caster));
    }

    default EntityHitResult entityResult(Level worldIn, LivingEntity caster, int range, double radius){
        Vec3 srcVec = caster.getEyePosition(1.0F);
        Vec3 lookVec = caster.getViewVector(1.0F);
        Vec3 destVec = srcVec.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        AABB axisalignedbb = caster.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(radius, radius, radius);
        return ProjectileUtil.getEntityHitResult(worldIn, caster, srcVec, destVec, axisalignedbb, entity -> entity instanceof LivingEntity && !entity.isSpectator() && entity.isPickable());
    }

    default boolean ReduceCastTime(LivingEntity caster){
        if (this.getSpellType() == SpellType.FROST){
            return CuriosFinder.hasFrostCrown(caster) || CuriosFinder.hasMagicHat(caster);
        } else if (this.getSpellType() == SpellType.WILD){
            return CuriosFinder.hasWildCrown(caster) || CuriosFinder.hasMagicHat(caster);
        } else if (this.getSpellType() == SpellType.ABYSS){
            return CuriosFinder.hasAbyssCrown(caster) || CuriosFinder.hasMagicHat(caster);
        } else if (this.getSpellType() == SpellType.VOID){
            return CuriosFinder.hasVoidCrown(caster) || CuriosFinder.hasMagicHat(caster);
        } else if (this.getSpellType() == SpellType.NETHER){
            return CuriosFinder.hasNetherCrown(caster) || CuriosFinder.hasMagicHat(caster);
        } else if (this.getSpellType() == SpellType.NECROMANCY){
            return CuriosFinder.hasUndeadCrown(caster) || CuriosFinder.hasMagicHat(caster);
        } else {
            return CuriosFinder.hasCurio(caster, itemStack -> (itemStack.getItem() instanceof MagicHatItem) || (itemStack.getItem() instanceof NecroGarbs.NecroCrownItem crown && crown.isNameless));
        }
    }

    @Nullable
    default MobEffectInstance summonDownEffect(LivingEntity caster){
        return caster.getEffect(GoetyEffects.SUMMON_DOWN.get());
    }

    default int SoulCostUp(LivingEntity caster){
        MobEffectInstance mobEffectInstance = summonDownEffect(caster);
        if (mobEffectInstance != null){
            return mobEffectInstance.getAmplifier() + 2;
        }
        return 1;
    }

    default boolean SoulDiscount(LivingEntity caster){
        return CuriosFinder.hasCurio(caster, itemStack -> itemStack.getItem() instanceof MagicRobeItem);
    }

    default boolean FrostSoulDiscount(LivingEntity caster){
        return CuriosFinder.hasFrostRobes(caster);
    }

    default boolean WindSoulDiscount(LivingEntity caster){
        return CuriosFinder.hasCurio(caster, ModItems.WIND_ROBE.get());
    }

    default boolean GeoSoulDiscount(LivingEntity caster){
        return CuriosFinder.hasCurio(caster, ModItems.AMETHYST_NECKLACE.get());
    }

    default boolean StormSoulDiscount(LivingEntity caster){
        return CuriosFinder.hasCurio(caster, ModItems.STORM_ROBE.get());
    }

    default boolean WildSoulDiscount(LivingEntity caster){
        return CuriosFinder.hasWildRobe(caster);
    }

    default boolean NetherSoulDiscount(LivingEntity caster){
        return CuriosFinder.hasNetherRobe(caster);
    }

    default boolean AbyssSoulDiscount(LivingEntity caster){
        return CuriosFinder.hasAbyssRobes(caster);
    }

    default boolean NecroSoulDiscount(LivingEntity caster){
        return false;
    }

    default boolean VoidSoulDiscount(LivingEntity caster){
        return CuriosFinder.hasVoidRobe(caster);
    }
}
