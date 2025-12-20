package com.Polarice3.Goety.common.magic.spells.abyss;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.GulfTentacle;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GulfTentacleSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRange(0);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.WaterWhipCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.WaterWhipDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.WaterWhipCoolDown.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.GENERIC_SPLASH;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        int range = spellStat.getRange();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
            range += WandUtil.getRangeLevel(caster);
        }
        int i;
        if (WandUtil.getSpellOnHand(caster, InteractionHand.MAIN_HAND) == this){
            if (caster.getMainArm() == HumanoidArm.RIGHT){
                i = 1;
            } else {
                i = -1;
            }
        } else {
            if (caster.getMainArm() == HumanoidArm.RIGHT){
                i = -1;
            } else {
                i = 1;
            }
        }
        float f2 = caster.yBodyRot * (float) (Math.PI / 180.0);
        double d0 = Mth.sin(f2);
        double d1 = Mth.cos(f2);
        double d2 = (double)i * 0.5D;
        double d3 = 1.0D;
        float f3 = caster.isCrouching() ? -0.1875F : 0.0F;
        Vec3 vec3 = caster.getEyePosition().add(-d1 * d2 - d0 * d3, (double)f3 - 0.45F, -d0 * d2 + d1 * d3);
        GulfTentacle gulfTentacle = new GulfTentacle(ModEntityType.GULF_TENTACLE.get(), worldIn);
        gulfTentacle.setPos(vec3);
        gulfTentacle.setOffset(i);
        gulfTentacle.setRot(caster.getYRot(), caster.getXRot());
        gulfTentacle.setTrueOwner(caster);
        gulfTentacle.setRange(gulfTentacle.getRange() + range);
        gulfTentacle.setStaff(this.rightStaff(staff));
        if (potency > 0){
            int boost = Mth.clamp(potency - 1, 0, 10);
            gulfTentacle.addEffect(new MobEffectInstance(GoetyEffects.BUFF.get(), EffectsUtil.infiniteEffect(), boost, false, false));
        }
        worldIn.addFreshEntity(gulfTentacle);
        this.playSound(worldIn, caster, ModSounds.WHIP_SWING.get());
    }
}
