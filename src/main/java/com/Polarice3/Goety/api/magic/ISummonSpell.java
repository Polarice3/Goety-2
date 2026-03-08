package com.Polarice3.Goety.api.magic;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ISummonSpell extends ISpell{
    int SummonDownDuration();

    void commonResult(ServerLevel worldIn, LivingEntity entityLiving);

    default boolean hasSummonDown(LivingEntity caster){
        return caster.hasEffect(GoetyEffects.SUMMON_DOWN.get());
    }

    default void SummonSap(LivingEntity owner, LivingEntity summonedEntity){
        if (owner != null && summonedEntity != null) {
            if (this.hasSummonDown(owner)) {
                MobEffectInstance effectinstance = owner.getEffect(GoetyEffects.SUMMON_DOWN.get());
                if (effectinstance != null) {
                    summonedEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, EffectsUtil.infiniteEffect(), effectinstance.getAmplifier()));
                    summonedEntity.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), EffectsUtil.infiniteEffect(), effectinstance.getAmplifier() + 4));
                }
                for (ItemStack itemStack : summonedEntity.getAllSlots()){
                    if (itemStack.isDamageableItem()){
                        itemStack.setDamageValue(itemStack.getMaxDamage() - summonedEntity.getRandom().nextInt(1 + summonedEntity.getRandom().nextInt(Math.max(itemStack.getMaxDamage() - 3, 1))));
                    }
                }
                summonedEntity.setHealth(summonedEntity.getMaxHealth() / 2.0F);
            }
        }
    }

    default void SummonDown(LivingEntity entityLiving){
        MobEffectInstance effectinstance1 = entityLiving.getEffect(GoetyEffects.SUMMON_DOWN.get());
        int i = 1;
        if (effectinstance1 != null) {
            i += effectinstance1.getAmplifier();
            entityLiving.removeEffectNoUpdate(GoetyEffects.SUMMON_DOWN.get());
        } else {
            --i;
        }

        i = Mth.clamp(i, 0, 4);
        int s = SummonDownDuration();
        if (entityLiving instanceof Player player){
            if (WandUtil.enchantedFocus(player)){
                s = (int) (SummonDownDuration() * 1.5);
            }
        }
        MobEffectInstance effectinstance = new MobEffectInstance(GoetyEffects.SUMMON_DOWN.get(), s, i, false, false, true);
        entityLiving.addEffect(effectinstance);
    }

    default void setTarget(LivingEntity source, Mob summoned){
        LivingEntity target = this.getTarget(summoned);
        if (target != null){
            if (!MobUtil.areAllies(source, target)) {
                summoned.setTarget(target);
            }
        }
    }

    default void uponSummon(ServerLevel worldIn, LivingEntity caster, ItemStack staff, LivingEntity summoned) {
        this.summonParticles(worldIn, caster, staff, summoned);
    }

    default void summonParticles(ServerLevel worldIn, LivingEntity caster, ItemStack staff, LivingEntity summoned) {
        ColorUtil colorUtil = new ColorUtil(0x8FE6DF);
        int colorFrom = 0x17b0e0;
        int colorTo = 0xffffff;
        if (summoned.getMobType() != MobType.UNDEAD) {
            if (this.getSpellType() == SpellType.VOID) {
                colorUtil = new ColorUtil(0xcc00fa);
                colorFrom = 0xcc00fa;
                colorTo = 0xe079fa;
            } else if (this.getSpellType() == SpellType.WILD) {
                colorUtil = new ColorUtil(0x403b14);
                colorFrom = 0x403b14;
                colorTo = 0x5b4e1d;
            } else if (this.getSpellType() == SpellType.NETHER) {
                colorUtil = new ColorUtil(0xffa300);
                colorFrom = 0xffa300;
                colorTo = 0xffff6e;
            } else if (this.getSpellType() == SpellType.GEOMANCY) {
                colorUtil = new ColorUtil(0xffca00);
                colorFrom = 0xffca00;
                colorTo = 0xffff00;
            } else {
                colorUtil = new ColorUtil(0xffffff);
                colorFrom = 0xffffff;
            }
        } else {
            if (staff.is(ModItems.NAMELESS_STAFF.get())) {
                colorUtil = new ColorUtil(0xa7fc3e);
                colorFrom = 0xa7fc3e;
                colorTo = 0xcffc97;
            } else if (this.typeStaff(staff, SpellType.WILD)) {
                colorUtil = new ColorUtil(0x403b14);
                colorFrom = 0x403b14;
                colorTo = 0x5b4e1d;
            } else if (this.typeStaff(staff, SpellType.NETHER)) {
                colorUtil = new ColorUtil(0xffa300);
                colorFrom = 0xffa300;
                colorTo = 0xffff6e;
            }
        }
        ServerParticleUtil.summonUndeadParticles(worldIn, summoned, colorUtil, colorFrom, colorTo);
    }
}
