package com.Polarice3.Goety.common.magic.spells.void_spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.TouchSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.List;

public class BanishSpell extends TouchSpell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.BanishCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.CHORUS_FRUIT_TELEPORT;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.BanishCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.VOID;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity entityLiving) {
        HitResult hitResult = entityLiving.pick(entityLiving.getAttributeValue(ForgeMod.REACH_DISTANCE.get()), 1.0F, false);
        if (hitResult instanceof EntityHitResult result){
            if (result.getEntity() instanceof LivingEntity living){
                if (living.getMaxHealth() < SpellConfig.BanishMaxHealth.get()
                        && !MobUtil.hasEntityTypesConfig(SpellConfig.BanishBlackList.get(), living.getType())){
                    worldIn.playSound((Player)null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.SPELL_FAIL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    return false;
                }
                net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(living, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ());
                if (event.isCanceled()) {
                    return false;
                }
            }
        }
        return super.conditionsMet(worldIn, entityLiving);
    }

    @Override
    public void touchResult(ServerLevel worldIn, LivingEntity caster, LivingEntity target) {
        double d0 = target.getX();
        double d1 = target.getY();
        double d2 = target.getZ();
        int range = 64;
        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), caster);
        }

        for(int i = 0; i < range; ++i) {
            double d3 = target.getX() + (target.getRandom().nextDouble() - 0.5D) * range;
            double d4 = Mth.clamp(target.getY() + (double)(target.getRandom().nextInt(range) - (range / 2)), (double)worldIn.getMinBuildHeight(), (double)(worldIn.getMinBuildHeight() + worldIn.getLogicalHeight() - 1));
            double d5 = target.getZ() + (target.getRandom().nextDouble() - 0.5D) * range;
            if (target.isPassenger()) {
                target.stopRiding();
            }

            Vec3 vec3 = target.position();
            worldIn.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(target));
            if (target.randomTeleport(d3, d4, d5, true)) {
                SoundEvent soundevent = target instanceof Fox ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
                worldIn.playSound((Player)null, d0, d1, d2, soundevent, SoundSource.PLAYERS, 1.0F, 1.0F);
                target.playSound(soundevent, 1.0F, 1.0F);
                break;
            }
        }
    }
}
