package com.Polarice3.Goety.common.magic.spells.nether;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.HellBlast;
import com.Polarice3.Goety.common.entities.projectiles.Lavaball;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.CuriosFinder;
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

public class LavaballSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.LavaballCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.LavaballDuration.get();
    }

    @Override
    public SoundEvent CastingSound(LivingEntity caster) {
        if (CuriosFinder.hasUnholySet(caster)){
            return ModSounds.APOSTLE_PREPARE_SPELL.get();
        }
        return SoundEvents.EVOKER_PREPARE_ATTACK;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.LavaballCoolDown.get();
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
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    @Override
    public ColorUtil particleColors(LivingEntity caster) {
        return new ColorUtil(1.0F, 0.0F, 0.0F);
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        int burning = spellStat.getBurning();
        float radius = (float) spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
            burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), caster);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster) / 2.0F;
        }
        Vec3 vector3d = caster.getViewVector( 1.0F);
        AbstractHurtingProjectile fireballEntity = new Lavaball(worldIn,
                caster.getX() + vector3d.x / 2,
                caster.getEyeY() - 0.2,
                caster.getZ() + vector3d.z / 2,
                vector3d.x,
                vector3d.y,
                vector3d.z);
        if (CuriosFinder.hasUnholySet(caster)){
            fireballEntity = new HellBlast(caster.getX() + vector3d.x / 2,
                    caster.getEyeY() - 0.2,
                    caster.getZ() + vector3d.z / 2,
                    vector3d.x,
                    vector3d.y,
                    vector3d.z,
                    worldIn);
        }
        fireballEntity.setOwner(caster);
        if (fireballEntity instanceof Lavaball lavaball) {
            if (rightStaff(staff)) {
                lavaball.setUpgraded(true);
            }
            if (isShifting(caster)) {
                lavaball.setDangerous(false);
            }
            lavaball.setExtraDamage(potency);
            lavaball.setFiery(burning);
            lavaball.setExplosionPower(lavaball.getExplosionPower() + radius);
        } else if (fireballEntity instanceof HellBlast hellBlast){
            hellBlast.setExtraDamage(potency);
            hellBlast.setRadius(hellBlast.getRadius() + radius);
            hellBlast.setFiery(burning);
        }
        worldIn.addFreshEntity(fireballEntity);
        if (rightStaff(staff)) {
            for (int i = 0; i < 2; ++i) {
                AbstractHurtingProjectile fireballEntity1 = new Lavaball(worldIn,
                        caster.getX() + vector3d.x / 2 + worldIn.random.nextGaussian(),
                        caster.getEyeY() - 0.2,
                        caster.getZ() + vector3d.z / 2 + worldIn.random.nextGaussian(),
                        vector3d.x,
                        vector3d.y,
                        vector3d.z);
                if (CuriosFinder.hasUnholySet(caster)){
                    fireballEntity1 = new HellBlast(caster.getX() + vector3d.x / 2 + worldIn.random.nextGaussian(),
                            caster.getEyeY() - 0.2,
                            caster.getZ() + vector3d.z / 2 + worldIn.random.nextGaussian(),
                            vector3d.x,
                            vector3d.y,
                            vector3d.z,
                            worldIn);
                }
                fireballEntity1.setOwner(caster);
                if (fireballEntity1 instanceof Lavaball lavaball) {
                    lavaball.setUpgraded(true);
                    if (isShifting(caster)) {
                        lavaball.setDangerous(false);
                    }
                    lavaball.setExtraDamage(potency);
                    lavaball.setFiery(burning);
                    lavaball.setExplosionPower(lavaball.getExplosionPower() + radius);
                } else if (fireballEntity1 instanceof HellBlast hellBlast){
                    hellBlast.setExtraDamage(potency);
                    hellBlast.setRadius(hellBlast.getRadius() + radius);
                    hellBlast.setFiery(burning);
                }
                worldIn.addFreshEntity(fireballEntity1);
            }
        }
        this.playSound(worldIn, caster, SoundEvents.GHAST_SHOOT, 2.0F, this.projPitch(worldIn.getRandom()));
    }
}
