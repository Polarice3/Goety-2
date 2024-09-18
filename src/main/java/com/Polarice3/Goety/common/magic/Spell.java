package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.FoggyCloudParticleOption;
import com.Polarice3.Goety.client.particles.GatherTrailParticle;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class Spell implements ISpell {

    public Spell(){
    }

    public abstract int defaultSoulCost();

    public abstract int defaultCastDuration();

    @Nullable
    public abstract SoundEvent CastingSound();

    public abstract int defaultSpellCooldown();

    public void SpellResult(LivingEntity caster, ItemStack staff){
        SpellResult(caster.level, caster, staff);
    }

    public void SpellResult(Level level, LivingEntity caster, ItemStack staff){
        if (level instanceof ServerLevel serverLevel){
            SpellResult(serverLevel, caster, staff);
        }
    }

    public abstract void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff);

    public SpellType getSpellType(){
        return SpellType.NONE;
    }

    public boolean GeoPower(LivingEntity entityLiving){
        return CuriosFinder.hasCurio(entityLiving, ModItems.AMETHYST_NECKLACE.get());
    }

    public boolean isShifting(LivingEntity entityLiving){
        return (entityLiving.isCrouching() || entityLiving.isShiftKeyDown()) && !WandUtil.findWand(entityLiving).isEmpty();
    }

    public boolean conditionsMet(ServerLevel worldIn, LivingEntity entityLiving){
        return true;
    }

    @Nullable
    public LivingEntity getTarget(LivingEntity caster){
        return this.getTarget(caster, 16);
    }

    @Nullable
    public LivingEntity getTarget(LivingEntity caster, int range){
        if (caster instanceof Mob mob){
            return mob.getTarget();
        } else {
            HitResult hitResult = this.rayTrace(caster.level, caster, range, 3);
            if (hitResult instanceof EntityHitResult entityHitResult){
                if (entityHitResult.getEntity() instanceof PartEntity<?> partEntity &&
                    partEntity.getParent() instanceof LivingEntity living){
                    return living;
                } else if (entityHitResult.getEntity() instanceof LivingEntity living){
                    return living;
                }
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
    public void useParticle(Level worldIn, LivingEntity livingEntity, ItemStack stack) {
        if (this.getSpellType() == SpellType.WILD) {
            if (worldIn instanceof ServerLevel serverLevel) {
                ColorUtil colorUtil = new ColorUtil(0xfcd9f7);
                serverLevel.sendParticles(ModParticleTypes.SPELL_SQUARE.get(), livingEntity.getX(), livingEntity.getY() + 2.0D, livingEntity.getZ(), 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 0.5F);
                serverLevel.sendParticles(new FoggyCloudParticleOption(new ColorUtil(0xcf75af), 0.25F, 6), livingEntity.getX(), livingEntity.getY() + 1.5D, livingEntity.getZ(), 1, 0, 0, 0, 0);
            }
        } else if (this.getSpellType() == SpellType.NECROMANCY){
            if (worldIn instanceof ServerLevel serverLevel){
                int range = 1;
                ColorUtil colorUtil = new ColorUtil(0xffffff);
                if (stack.is(ModItems.NAMELESS_STAFF.get())){
                    range = 3;
                    colorUtil = new ColorUtil(0xa7fc3e);
                }
                ServerParticleUtil.gatheringParticles(new GatherTrailParticle.Option(colorUtil, livingEntity.position().add(0, 2, 0)), livingEntity, serverLevel, range);
            }
        } else {
            ISpell.super.useParticle(worldIn, livingEntity, stack);
        }
    }

    public List<Enchantment> acceptedEnchantments(){
        return new ArrayList<>();
    }

    protected HitResult rayTraceCollide(Level worldIn, LivingEntity livingEntity, int range, double radius) {
        if (this.entityCollideResult(worldIn, livingEntity, range, radius) == null){
            return this.blockResult(worldIn, livingEntity, range);
        } else {
            return this.entityCollideResult(worldIn, livingEntity, range, radius);
        }
    }

    protected EntityHitResult entityCollideResult(Level worldIn, LivingEntity livingEntity, int range, double radius){
        Vec3 srcVec = livingEntity.getEyePosition(1.0F);
        Vec3 lookVec = livingEntity.getViewVector(1.0F);
        Vec3 destVec = srcVec.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        AABB axisalignedbb = livingEntity.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(radius, radius, radius);
        return ProjectileUtil.getEntityHitResult(worldIn, livingEntity, srcVec, destVec, axisalignedbb, entity -> entity instanceof LivingEntity && livingEntity.hasLineOfSight(entity) && !entity.isSpectator() && entity.isPickable());
    }

    public SoundSource getSoundSource(){
        return SoundSource.PLAYERS;
    }

    public void playSound(ServerLevel serverLevel, LivingEntity caster, SoundEvent soundEvent){
        this.playSound(serverLevel, caster, soundEvent, 1.0F, 1.0F);
    }

    public void playSound(ServerLevel serverLevel, LivingEntity caster, SoundEvent soundEvent, float volume, float pitch){
        serverLevel.playSound((Player) null, caster.getX(), caster.getY(), caster.getZ(), soundEvent, this.getSoundSource(), volume, pitch);
    }

}
