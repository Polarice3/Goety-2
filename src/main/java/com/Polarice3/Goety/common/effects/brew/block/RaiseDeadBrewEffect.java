package com.Polarice3.Goety.common.effects.brew.block;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.Polarice3.Goety.common.magic.spells.necromancy.SkeletonSpell;
import com.Polarice3.Goety.common.magic.spells.necromancy.ZombieSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RaiseDeadBrewEffect extends BrewEffect {
    public RaiseDeadBrewEffect(int soulCost, int capacityExtra) {
        super("raise_dead", soulCost, capacityExtra, MobEffectCategory.BENEFICIAL, 0x1f1421);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public boolean canLinger() {
        return true;
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        raiseDead(pLevel, pPos, pSource, pAmplifier, pAreaOfEffect, MobUtil.getSummonLifespan(pLevel) * (pAmplifier + 1));
    }

    public static void raiseDead(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect, int lifetime) {
        raiseUndead(pLevel, pPos, pSource, lifetime);
        pLevel.playSound(null, pPos.getX(), pPos.getY(), pPos.getZ(), ModSounds.NECROMANCER_SUMMON.get(), pSource.getSoundSource(), 1.4F, 0.7F);
        int j = 0;

        for (int h = 0; h <= pAmplifier; ++h){
            if (h == 1){
                if (pLevel.random.nextDouble() < (pAmplifier * 0.5D)){
                    ++j;
                }
            } else if (h > 1){
                if (pLevel.random.nextDouble() < (pAmplifier * 0.25D)){
                    ++j;
                }
            }
        }

        for (int i = 0; i < j; ++i) {
            int radius = 4 + pAreaOfEffect;
            int x = pPos.getX() - (radius / 2) + pLevel.random.nextInt(radius) + 1;
            int y = pPos.getY() + radius;
            int z = pPos.getZ() - (radius / 2) + pLevel.random.nextInt(radius) + 1;
            raiseUndead(pLevel, BlockFinder.SummonPosition(pSource, x, y, z), pSource, lifetime);
        }
    }

    private static void raiseUndead(Level pLevel, BlockPos pPos, LivingEntity pSource, int lifetime) {
        if(!pLevel.isClientSide) {
            Summoned summoned = ModEntityType.ZOMBIE_SERVANT.get().create(pLevel);
            if (pLevel.random.nextBoolean()){
                summoned = ModEntityType.SKELETON_SERVANT.get().create(pLevel);
            }
            if (summoned != null) {
                EntityType<?> entityType = summoned.getVariant(pLevel, pPos);
                if (entityType != null) {
                    summoned = (Summoned) entityType.create(pLevel);
                }
                if (summoned != null) {
                    if (pLevel instanceof ServerLevel serverLevel) {
                        if (summoned instanceof ZombieServant) {
                            if (!new ZombieSpell().conditionsMet(serverLevel, pSource)){
                                return;
                            }
                        } else if (summoned instanceof AbstractSkeletonServant){
                            if (!new SkeletonSpell().conditionsMet(serverLevel, pSource)){
                                return;
                            }
                        }
                    }
                    summoned.setPos(Vec3.upFromBottomCenterOf(pPos, 1.0F));
                    if (pSource != null){
                        summoned.setTrueOwner(pSource);
                    }
                    if (pLevel instanceof ServerLevel serverLevel) {
                        summoned.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pPos), MobSpawnType.MOB_SUMMONED, null, null);
                    }
                    summoned.setPersistenceRequired();
                    summoned.setLimitedLife(lifetime);
                    pLevel.addFreshEntity(summoned);
                }
            }
        }

    }
}
