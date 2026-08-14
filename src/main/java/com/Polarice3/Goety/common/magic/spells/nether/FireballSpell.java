package com.Polarice3.Goety.common.magic.spells.nether;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.HellBolt;
import com.Polarice3.Goety.common.entities.projectiles.ModFireball;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Learned you could use this method for better projectile accuracy from codes by @Yunus1903
 */
public class FireballSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.FireballCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.FireballDuration.get();
    }

    @Override
    public SoundEvent CastingSound(LivingEntity caster) {
        return SoundEvents.BLAZE_BURN;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.FireballCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.BURNING.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        float damage = SpellConfig.FireballDamage.get().floatValue() * WandUtil.damageMultiply();
        int potency = spellStat.getPotency();
        int burning = spellStat.getBurning();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
            burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), caster);
        }
        Vec3 vector3d = caster.getViewVector(1.0F);
        double x = caster.getX() + vector3d.x / 2;
        double y = caster.getEyeY() - 0.2D;
        double z = caster.getZ() + vector3d.z / 2;
        Vec3 origin = new Vec3(x, y, z);
        if (ItemHelper.hasMaleficHelm(caster) && this.rightStaff(staff)) {
            origin = MobUtil.vecFromCenterToFrontOfFace(caster, 0.5D, 0.0F);
            vector3d = MobUtil.vecCenterFaceVector(caster, 2.0D, 0.0F);
        }
        AbstractHurtingProjectile smallFireballEntity = new ModFireball(worldIn,
                origin.x,
                origin.y,
                origin.z,
                vector3d.x,
                vector3d.y,
                vector3d.z);
        if (CuriosFinder.hasUnholySet(caster)){
            smallFireballEntity = new HellBolt(
                    origin.x,
                    origin.y,
                    origin.z,
                    vector3d.x,
                    vector3d.y,
                    vector3d.z, worldIn);
        }
        smallFireballEntity.setOwner(caster);
        if (smallFireballEntity instanceof ModFireball fireball) {
            if (isShifting(caster)) {
                fireball.setDangerous(false);
            }
            fireball.setExtraDamage(potency);
            fireball.setFiery(burning);
        } else if (smallFireballEntity instanceof HellBolt hellBolt){
            hellBolt.setDamage(damage + potency);
            hellBolt.setFiery(burning);
        }
        worldIn.addFreshEntity(smallFireballEntity);
        if (rightStaff(staff)) {
            for (int i = 0; i < 2; ++i) {
                double x1 = caster.getX() + vector3d.x / 2 + worldIn.getRandom().nextGaussian();
                double y1 = caster.getEyeY() - 0.2D;
                double z1 = caster.getZ() + vector3d.z / 2 + worldIn.getRandom().nextGaussian();
                Vec3 origin2 = new Vec3(x1, y1, z1);
                if (ItemHelper.hasMaleficHelm(caster)) {
                    if (i == 0) {
                        origin2 = MobUtil.vecFromCenterToFrontOfFace(caster, 0.5D, -10.0F);
                        vector3d = MobUtil.vecCenterFaceVector(caster, 2.0D, -10.0F);
                    } else {
                        origin2 = MobUtil.vecFromCenterToFrontOfFace(caster, 0.5D, 10.0F);
                        vector3d = MobUtil.vecCenterFaceVector(caster, 2.0D, 10.0F);
                    }
                }
                AbstractHurtingProjectile smallFireballEntity2 = new ModFireball(worldIn,
                        origin2.x,
                        origin2.y,
                        origin2.z,
                        vector3d.x,
                        vector3d.y,
                        vector3d.z);
                if (CuriosFinder.hasUnholySet(caster)){
                    smallFireballEntity2 = new HellBolt(
                            origin2.x,
                            origin2.y,
                            origin2.z,
                            vector3d.x,
                            vector3d.y,
                            vector3d.z, worldIn);
                }
                smallFireballEntity2.setOwner(caster);
                if (smallFireballEntity2 instanceof ModFireball fireball) {
                    if (isShifting(caster)) {
                        fireball.setDangerous(false);
                    }
                    fireball.setExtraDamage(potency);
                    fireball.setFiery(burning);
                } else if (smallFireballEntity2 instanceof HellBolt hellBolt){
                    hellBolt.setDamage(damage + potency);
                    hellBolt.setFiery(burning);
                }
                worldIn.addFreshEntity(smallFireballEntity2);
            }
        }
        SoundEvent soundEvent = SoundEvents.BLAZE_SHOOT;
        if (CuriosFinder.hasUnholySet(caster)){
            soundEvent = ModSounds.HELL_BOLT_SHOOT.get();
        }
        this.playSound(worldIn, caster, soundEvent, 2.0F, this.projPitch(worldIn.getRandom()));
    }
}
