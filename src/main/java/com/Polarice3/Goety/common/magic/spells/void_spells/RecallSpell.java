package com.Polarice3.Goety.common.magic.spells.void_spells;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.magic.ITouchSpell;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.items.magic.RecallFocus;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModAttributes;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RecallSpell extends Spell implements ITouchSpell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.RecallCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.RecallDuration.get();
    }

    @Override
    public boolean hasCustomCooldown(LivingEntity caster, ItemStack staff, ItemStack focus, int initialCooldown) {
        return true;
    }

    @Override
    public int defaultSpellCooldown() {
        return 0;
    }

    @Override
    public SoundEvent CastingSound() {
        return null;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.VOID;
    }

    @Override
    public SoundEvent loopSound(LivingEntity caster) {
        return SoundEvents.PORTAL_TRIGGER;
    }

    public boolean conditionsMet(ServerLevel worldIn, LivingEntity caster){
        if (caster instanceof ServerPlayer player) {
            if (RecallFocus.isValid(worldIn, WandUtil.findFocus(player))) {
                return true;
            } else if (!RecallFocus.hasRecall(WandUtil.findFocus(player))){
                player.displayClientMessage(Component.translatable("info.goety.focus.noPos"), true);
            } else {
                player.displayClientMessage(Component.translatable("info.goety.focus.PosInvalid"), true);
            }
        }
        return false;
    }

    @Override
    public void useParticle(Level worldIn, LivingEntity caster, ItemStack stack) {
        for(int i = 0; i < 2; ++i) {
            worldIn.addParticle(ParticleTypes.PORTAL, caster.getRandomX(0.5D), caster.getRandomY() - 0.25D, caster.getRandomZ(0.5D), (worldIn.random.nextDouble() - 0.5D) * 2.0D, -worldIn.random.nextDouble(), (worldIn.random.nextDouble() - 0.5D) * 2.0D);
        }
    }

    @Override
    public void touchResult(ServerLevel worldIn, LivingEntity caster, LivingEntity target, ItemStack staff, SpellStat spellStat) {
        if (caster instanceof ServerPlayer player) {
            if (RecallFocus.isValid(worldIn, WandUtil.findFocus(player))) {
                if (MobUtil.getOwner(target) != null){
                    if (MobUtil.getOwner(target) == caster){
                        if (target instanceof IServant servant) {
                            BlockPos blockPos = RecallFocus.getRecallBlockPos(WandUtil.findFocus(player));
                            servant.setWandering(false);
                            if (blockPos != null) {
                                servant.setStaying(false);
                                servant.setBoundPos(blockPos);
                            } else {
                                servant.setStaying(true);
                                servant.setBoundPos(null);
                            }
                        }
                        if (RecallFocus.recall(target, WandUtil.findFocus(player))) {
                            SEHelper.addCooldown(player, WandUtil.findFocus(player).getItem(), (int) (SpellConfig.RecallCoolDown.get() * ModAttributes.getCooldownDiscount(caster)));
                        }
                    }
                }
            }
        }
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        if (caster instanceof ServerPlayer player) {
            if (RecallFocus.isValid(worldIn, WandUtil.findFocus(player))) {
                if (RecallFocus.recall(player, WandUtil.findFocus(player))) {
                    SEHelper.addCooldown(player, WandUtil.findFocus(player).getItem(), (int) (SpellConfig.RecallCoolDown.get() * ModAttributes.getCooldownDiscount(caster)));
                }
            }
        }
    }
}
