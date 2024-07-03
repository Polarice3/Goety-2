package com.Polarice3.Goety.api.magic;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.curios.MagicHatItem;
import com.Polarice3.Goety.common.items.curios.MagicRobeItem;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
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
    int defaultSoulCost();

    default int soulCost(LivingEntity entityLiving){
        return SoulCalculation(entityLiving);
    }

    default int SoulCalculation(LivingEntity entityLiving){
        int cost = defaultSoulCost() * SoulCostUp(entityLiving);
        BlockPos blockPos = entityLiving.blockPosition();
        Level level = entityLiving.level;
        Holder<Biome> biomeHolder = level.getBiome(blockPos);
        boolean enable = SpellConfig.EnvironmentalCost.get();
        if (SoulDiscount(entityLiving)){
            cost /= 1.15F;
        }
        if (this.getSpellType() == SpellType.FROST){
            if (FrostSoulDiscount(entityLiving)){
                cost /= 2;
            }
            if (enable) {
                if (biomeHolder.get().coldEnoughToSnow(blockPos) || biomeHolder.is(ModTags.Biomes.FROST_DISCOUNT) || (level.isRainingAt(blockPos) && biomeHolder.get().coldEnoughToSnow(blockPos))) {
                    cost /= 1.5F;
                } else if (biomeHolder.get().shouldSnowGolemBurn(blockPos) || biomeHolder.is(ModTags.Biomes.FROST_MARKUP)) {
                    cost *= 1.5F;
                }
            }
        }
        if (this.getSpellType() == SpellType.WIND){
            if (WindSoulDiscount(entityLiving)){
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
            if (StormSoulDiscount(entityLiving)){
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
            if (GeoSoulDiscount(entityLiving)){
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
            if (NetherSoulDiscount(entityLiving)){
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
            if (NecroSoulDiscount(entityLiving)){
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
            if (WildSoulDiscount(entityLiving)){
                cost /= 2;
            }
            if (enable) {
                if (biomeHolder.is(ModTags.Biomes.WILD_DISCOUNT)) {
                    cost /= 1.5F;
                } else if (biomeHolder.is(ModTags.Biomes.WILD_MARKUP)) {
                    cost *= 1.5F;
                }
            }
        }
        if (this.getSpellType() == SpellType.ABYSS){
            if (AbyssSoulDiscount(entityLiving)){
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
            if (VoidSoulDiscount(entityLiving)){
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

    default int castDuration(LivingEntity entityLiving){
        if (ReduceCastTime(entityLiving)){
            return defaultCastDuration() / 2;
        } else {
            return defaultCastDuration();
        }
    }

    @Nullable
    SoundEvent CastingSound();

    default float castingVolume(){
        return 0.5F;
    }

    int defaultSpellCooldown();

    default int spellCooldown(){
        return defaultSpellCooldown();
    }

    default void startSpell(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
    }

    void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff);

    SpellType getSpellType();

    boolean conditionsMet(ServerLevel worldIn, LivingEntity entityLiving);

    List<Enchantment> acceptedEnchantments();

    default SoundEvent loopSound(LivingEntity entityLiving){
        return null;
    }

    default ColorUtil particleColors(LivingEntity entityLiving){
        return new ColorUtil(0.2F, 0.2F, 0.2F);
    }

    @Nullable
    default LivingEntity getTarget(LivingEntity caster){
        return null;
    }

    default HitResult rayTrace(Level worldIn, LivingEntity livingEntity, int range, double radius) {
        if (this.entityResult(worldIn, livingEntity, range, radius) == null){
            return this.blockResult(worldIn, livingEntity, range);
        } else {
            return this.entityResult(worldIn, livingEntity, range, radius);
        }
    }

    default BlockHitResult blockResult(Level worldIn, LivingEntity livingEntity, double range) {
        float f = livingEntity.getXRot();
        float f1 = livingEntity.getYRot();
        Vec3 vector3d = livingEntity.getEyePosition(1.0F);
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 vector3d1 = vector3d.add((double)f6 * range, (double)f5 * range, (double)f7 * range);
        return worldIn.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, livingEntity));
    }

    default EntityHitResult entityResult(Level worldIn, LivingEntity livingEntity, int range, double radius){
        Vec3 srcVec = livingEntity.getEyePosition(1.0F);
        Vec3 lookVec = livingEntity.getViewVector(1.0F);
        Vec3 destVec = srcVec.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        AABB axisalignedbb = livingEntity.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(radius, radius, radius);
        return ProjectileUtil.getEntityHitResult(worldIn, livingEntity, srcVec, destVec, axisalignedbb, entity -> entity instanceof LivingEntity && !entity.isSpectator() && entity.isPickable());
    }

    default boolean ReduceCastTime(LivingEntity entityLiving){
        if (this.getSpellType() == SpellType.NECROMANCY){
            return CuriosFinder.hasCurio(entityLiving, ModItems.NECRO_CROWN.get()) || CuriosFinder.hasCurio(entityLiving, itemStack -> itemStack.getItem() instanceof MagicHatItem);
        } else {
            return CuriosFinder.hasCurio(entityLiving, itemStack -> itemStack.getItem() instanceof MagicHatItem);
        }
    }

    @Nullable
    default MobEffectInstance summonDownEffect(LivingEntity livingEntity){
        return livingEntity.getEffect(GoetyEffects.SUMMON_DOWN.get());
    }

    default int SoulCostUp(LivingEntity entityLiving){
        MobEffectInstance mobEffectInstance = summonDownEffect(entityLiving);
        if (mobEffectInstance != null){
            return mobEffectInstance.getAmplifier() + 2;
        }
        return 1;
    }

    default boolean SoulDiscount(LivingEntity entityLiving){
        return CuriosFinder.hasCurio(entityLiving, itemStack -> itemStack.getItem() instanceof MagicRobeItem);
    }

    default boolean FrostSoulDiscount(LivingEntity entityLiving){
        return CuriosFinder.hasFrostRobes(entityLiving);
    }

    default boolean WindSoulDiscount(LivingEntity entityLiving){
        return CuriosFinder.hasCurio(entityLiving, ModItems.WIND_ROBE.get());
    }

    default boolean GeoSoulDiscount(LivingEntity entityLiving){
        return CuriosFinder.hasCurio(entityLiving, ModItems.AMETHYST_NECKLACE.get());
    }

    default boolean StormSoulDiscount(LivingEntity entityLiving){
        return CuriosFinder.hasCurio(entityLiving, ModItems.STORM_ROBE.get());
    }

    default boolean WildSoulDiscount(LivingEntity entityLiving){
        return CuriosFinder.hasWildRobe(entityLiving);
    }

    default boolean NetherSoulDiscount(LivingEntity entityLiving){
        return CuriosFinder.hasNetherRobe(entityLiving);
    }

    default boolean AbyssSoulDiscount(LivingEntity entityLiving){
        return false;
    }

    default boolean NecroSoulDiscount(LivingEntity entityLiving){
        return false;
    }

    default boolean VoidSoulDiscount(LivingEntity entityLiving){
        return false;
    }
}
