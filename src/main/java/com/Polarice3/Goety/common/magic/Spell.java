package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.FoggyCloudParticleOption;
import com.Polarice3.Goety.client.particles.GatherTrailParticle;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class Spell implements ISpell {

    public Spell(){
    }

    public abstract int defaultSoulCost();

    public abstract int defaultCastDuration();

    public abstract int defaultSpellCooldown();

    public void mobSpellResult(LivingEntity caster, ItemStack staff){
        mobSpellResult(caster, staff, this.defaultStats());
    }

    public void mobSpellResult(LivingEntity caster, ItemStack staff, SpellStat spellStat){
        serverCheckSpellResult(caster.level, caster, staff, spellStat);
    }

    public void serverCheckSpellResult(Level level, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        if (level instanceof ServerLevel serverLevel){
            SpellResult(serverLevel, caster, staff, spellStat);
        }
    }

    public SpellType getSpellType(){
        return SpellType.NONE;
    }

    public boolean GeoPower(LivingEntity caster){
        return CuriosFinder.hasCurio(caster, ModItems.AMETHYST_NECKLACE.get());
    }

    public boolean isShifting(LivingEntity caster){
        return (caster.isCrouching() || caster.isShiftKeyDown()) && !WandUtil.findWand(caster).isEmpty();
    }

    public boolean conditionsMet(Level worldIn, LivingEntity caster){
        if (worldIn instanceof ServerLevel serverLevel){
            return this.conditionsMet(serverLevel, caster);
        } else {
            return false;
        }
    }

    @Nullable
    public LivingEntity getTarget(LivingEntity caster){
        int range = this.defaultStats().getRange();
        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), caster);
        }
        return this.getTarget(caster, range);
    }

    @Nullable
    public LivingEntity getTarget(LivingEntity caster, int range){
        if (caster instanceof Mob mob){
            return mob.getTarget();
        } else {
            HitResult hitResult = this.rayTrace(caster.level, caster, range, 3);
            if (hitResult instanceof EntityHitResult entityHitResult){
                return MobUtil.getLivingTarget(entityHitResult.getEntity());
            }
            return null;
        }
    }

    public boolean rightStaff(ItemStack staff){
        return staff.getItem() instanceof IWand darkWand && darkWand.getSpellType() == getSpellType();
    }

    public boolean typeStaff(ItemStack staff, SpellType spellType){
        return staff.getItem() instanceof IWand darkWand && darkWand.getSpellType() == spellType;
    }

    @Override
    public void useParticle(Level worldIn, LivingEntity caster, ItemStack stack) {
        if (worldIn instanceof ServerLevel serverLevel){
            if (this.getSpellType() == SpellType.FROST) {
                if (caster.tickCount % 5 == 0) {
                    ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ParticleTypes.SNOWFLAKE, caster);
                }
            } else if (this.getSpellType() == SpellType.GEOMANCY) {
                BlockPos blockPos = BlockPos.containing(caster.getX(), caster.getY() - 1.0F, caster.getZ());
                BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPos));
                for (int i = 0; i < 8; ++i) {
                    ServerParticleUtil.circularParticles(serverLevel, option, caster.getX(), caster.getY() + 0.25D, caster.getZ(), 1.0F);
                }
            } else if (this.getSpellType() == SpellType.WIND) {
                if (caster.tickCount % 5 == 0) {
                    ColorUtil colorUtil = new ColorUtil(0x458c88);
                    ServerParticleUtil.gatheringParticles(new GatherTrailParticle.Option(colorUtil, caster.position().add(0, 1, 0)), caster, serverLevel, 1);
                }
            } else if (this.getSpellType() == SpellType.STORM) {
                if (caster.tickCount % 5 == 0) {
                    ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ModParticleTypes.SPELL_ELECTRIC.get(), caster);
                }
            } else if (this.getSpellType() == SpellType.WILD) {
                ColorUtil colorUtil = new ColorUtil(0xfcd9f7);
                serverLevel.sendParticles(ModParticleTypes.SPELL_SQUARE.get(), caster.getX(), caster.getY() + 2.0D, caster.getZ(), 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 0.5F);
                serverLevel.sendParticles(new FoggyCloudParticleOption(new ColorUtil(0xcf75af), 0.25F, 6), caster.getX(), caster.getY() + 1.5D, caster.getZ(), 1, 0, 0, 0, 0);
            } else if (this.getSpellType() == SpellType.ABYSS){
                int range = 1;
                ColorUtil colorUtil = new ColorUtil(ChatFormatting.AQUA);
                ServerParticleUtil.gatheringParticles(new GatherTrailParticle.Option(colorUtil, caster.position().add(0, 2, 0)), caster, serverLevel, range);
            } else if (this.getSpellType() == SpellType.VOID){
                int range = 1;
                ColorUtil colorUtil = new ColorUtil(ChatFormatting.DARK_PURPLE);
                ServerParticleUtil.gatheringParticles(new GatherTrailParticle.Option(colorUtil, caster.position().add(0, 2, 0)), caster, serverLevel, range);
                for(int i = 0; i < 2; ++i) {
                    serverLevel.sendParticles(ParticleTypes.PORTAL, caster.getRandomX(0.5D), caster.getRandomY() - 0.25D, caster.getRandomZ(0.5D), 0, (worldIn.getRandom().nextDouble() - 0.5D) * 2.0D, -worldIn.getRandom().nextDouble(), (worldIn.getRandom().nextDouble() - 0.5D) * 2.0D, 1.0D);
                }
            } else if (this.getSpellType() == SpellType.NETHER){
                int range = 1;
                ColorUtil colorUtil = new ColorUtil(ChatFormatting.GOLD);
                ServerParticleUtil.gatheringParticles(new GatherTrailParticle.Option(colorUtil, caster.position().add(0, 2, 0)), caster, serverLevel, range);
            } else if (this.getSpellType() == SpellType.NECROMANCY){
                int range = 1;
                ColorUtil colorUtil = new ColorUtil(0xffffff);
                if (stack.is(ModItems.NAMELESS_STAFF.get())){
                    range = 3;
                    colorUtil = new ColorUtil(0xa7fc3e);
                }
                ServerParticleUtil.gatheringParticles(new GatherTrailParticle.Option(colorUtil, caster.position().add(0, 2, 0)), caster, serverLevel, range);
            } else {
                ISpell.super.useParticle(worldIn, caster, stack);
            }
        }
    }

    public List<Enchantment> acceptedEnchantments(){
        return new ArrayList<>();
    }

    protected HitResult rayTraceCollide(Level worldIn, LivingEntity caster, int range, double radius) {
        if (this.entityCollideResult(worldIn, caster, range, radius) == null){
            return this.blockResult(worldIn, caster, range);
        } else {
            return this.entityCollideResult(worldIn, caster, range, radius);
        }
    }

    protected EntityHitResult entityCollideResult(Level worldIn, LivingEntity caster, int range, double radius){
        Vec3 srcVec = caster.getEyePosition(1.0F);
        Vec3 lookVec = caster.getViewVector(1.0F);
        Vec3 destVec = srcVec.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        AABB axisalignedbb = caster.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(radius, radius, radius);
        return ProjectileUtil.getEntityHitResult(worldIn, caster, srcVec, destVec, axisalignedbb, entity -> entity instanceof LivingEntity && caster.hasLineOfSight(entity) && !entity.isSpectator() && entity.isPickable());
    }

    public SoundSource getSoundSource(){
        return SoundSource.PLAYERS;
    }

    public float projPitch(RandomSource source){
        return (source.nextFloat() - source.nextFloat()) * 0.2F + 1.0F;
    }

    public void playSound(ServerLevel serverLevel, Entity entity, SoundEvent soundEvent){
        this.playSound(serverLevel, entity, soundEvent, 1.0F, 1.0F);
    }

    public void playSound(ServerLevel serverLevel, Entity entity, SoundEvent soundEvent, float volume, float pitch){
        serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(), soundEvent, this.getSoundSource(), volume, pitch);
    }

    public void playSound(ServerLevel serverLevel, LivingEntity caster, float volume, float pitch){
        SoundEvent soundEvent = this.CastingSound(caster);
        if (soundEvent != null){
            this.playSound(serverLevel, caster, soundEvent, volume, pitch);
        }
    }
}
