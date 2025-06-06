package com.Polarice3.Goety.common.magic.spells.necromancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.GatherTrailParticle;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SThunderBoltPacket;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KillingSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.KillingCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.KillingDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.KillingCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NECROMANCY;
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity caster) {
        return this.getTarget(caster) != null;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int castTime, SpellStat spellStat) {
        LivingEntity target = this.getTarget(caster);
        if (target != null){
            ColorUtil colorUtil = new ColorUtil(0xd91516);
            ServerParticleUtil.windParticle(worldIn, colorUtil, 1.0F, 0.0F, target.getId(), target.position());
        }
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        Vec3 vec3 = caster.getEyePosition();
        LivingEntity target = this.getTarget(caster);
        if (target != null){
            ColorUtil colorUtil = new ColorUtil(0xd91516);
            Vec3 vec31 = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ());
            DamageSource damageSource = ModDamageSource.deathCurse(caster);
            float damage = target.getHealth();
            float casterDamage = damage * SpellConfig.KillingFeedback.get().floatValue();
            if (caster.getHealth() - MobUtil.hurtCalculation(caster, damageSource, casterDamage) <= 0.0F && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(caster)){
                damage = caster.getHealth() - 1;
                casterDamage = caster.getHealth() - 1;
            }
            if (caster.hurt(damageSource, casterDamage) || !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(caster)) {
                if (target.hurt(damageSource, damage)) {
                    for (int i = 0; i < 8; ++i) {
                        Vec3 vector3d = new Vec3(target.getX(), target.getEyeY(), target.getZ());
                        Vec3 vector3d1 = vector3d.offsetRandom(target.getRandom(), 8.0F);
                        worldIn.sendParticles(new GatherTrailParticle.Option(colorUtil, vector3d1), vector3d.x, vector3d.y, vector3d.z, 0, 0.0F, 0.0F, 0.0F, 0.5F);
                        ServerParticleUtil.windParticle(worldIn, colorUtil, 1.0F, 0.0F, target.getId(), target.position());
                    }
                    ModNetwork.sendToALL(new SThunderBoltPacket(vec3, vec31, colorUtil, 10));
                    this.playSound(worldIn, caster, ModSounds.THUNDERBOLT.get(), 3.0F, 0.75F);
                    this.playSound(worldIn, caster, SoundEvents.LIGHTNING_BOLT_IMPACT, 3.0F, 0.75F);
                }
            }
        }
    }
}
