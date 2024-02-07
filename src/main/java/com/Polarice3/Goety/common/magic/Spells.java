package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
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

public abstract class Spells implements ISpell {

    public Spells(){
    }

    public abstract int defaultSoulCost();

    public abstract int defaultCastDuration();

    @Nullable
    public abstract SoundEvent CastingSound();

    public abstract int defaultSpellCooldown();

    public abstract void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff);

    public SpellType getSpellType(){
        return SpellType.NONE;
    }

    public boolean GeoPower(LivingEntity entityLiving){
        return CuriosFinder.hasCurio(entityLiving, ModItems.AMETHYST_NECKLACE.get());
    }

    public boolean isShifting(LivingEntity entityLiving){
        return entityLiving.isCrouching() || entityLiving.isShiftKeyDown();
    }

    public boolean conditionsMet(ServerLevel worldIn, LivingEntity entityLiving){
        return true;
    }

    public boolean rightStaff(ItemStack staff){
        return getSpellType().getStaff() != null && staff.is(getSpellType().getStaff());
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

}
