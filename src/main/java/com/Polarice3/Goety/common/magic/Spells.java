package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

public abstract class Spells {

    public Spells(){
    }

    public abstract int SoulCost();

    public abstract int CastDuration();

    public abstract SoundEvent CastingSound();

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

    protected HitResult rayTrace(Level worldIn, LivingEntity livingEntity, int range, double radius) {
        if (this.entityResult(worldIn, livingEntity, range, radius) == null){
            return this.blockResult(worldIn, livingEntity, range);
        } else {
            return this.entityResult(worldIn, livingEntity, range, radius);
        }
    }

    protected HitResult rayTraceCollide(Level worldIn, LivingEntity livingEntity, int range, double radius) {
        if (this.entityCollideResult(worldIn, livingEntity, range, radius) == null){
            return this.blockResult(worldIn, livingEntity, range);
        } else {
            return this.entityCollideResult(worldIn, livingEntity, range, radius);
        }
    }

    protected BlockHitResult blockResult(Level worldIn, LivingEntity livingEntity, double range) {
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

    protected EntityHitResult entityResult(Level worldIn, LivingEntity livingEntity, int range, double radius){
        Vec3 srcVec = livingEntity.getEyePosition(1.0F);
        Vec3 lookVec = livingEntity.getViewVector(1.0F);
        Vec3 destVec = srcVec.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        AABB axisalignedbb = livingEntity.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(radius, radius, radius);
        return ProjectileUtil.getEntityHitResult(worldIn, livingEntity, srcVec, destVec, axisalignedbb, entity -> entity instanceof LivingEntity && !entity.isSpectator() && entity.isPickable());
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

    public enum SpellType{
        NONE("none", null),
        NECROMANCY("necromancy", ModItems.NECRO_STAFF.get()),
        NETHER("nether", null),
        ILL("ill", ModItems.OMINOUS_STAFF.get()),
        FROST("frost", null),
        GEOMANCY("geomancy", null),
        WIND("wind", ModItems.WIND_STAFF.get()),
        VOID("void", null);

        private final Item staff;
        private final Component name;

        SpellType(String name, Item staff){
            this.name = Component.translatable("spell.goety." + name);
            this.staff = staff;
        }

        public Component getName(){
            return name;
        }

        public Item getStaff() {
            return staff;
        }
    }

}
