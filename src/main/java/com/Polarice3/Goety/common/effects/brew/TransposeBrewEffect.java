package com.Polarice3.Goety.common.effects.brew;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public class TransposeBrewEffect extends BrewEffect{
    public TransposeBrewEffect() {
        super("transpose", MobEffectCategory.BENEFICIAL, 0x2b0178);
    }

    @Override
    public void applyBlockEffect(Level pLevel, BlockPos pPos, LivingEntity pSource, int pAmplifier, int pAreaOfEffect) {
        for (BlockPos blockPos : this.getSpherePos(pPos, pAreaOfEffect + 4)){
            for (Entity entity : pLevel.getEntitiesOfClass(Entity.class, new AABB(blockPos))){
                if (entity instanceof ItemEntity || entity instanceof LivingEntity) {
                    entity.teleportTo(pPos.getX(), pPos.getY() + 1, pPos.getZ());
                    pLevel.gameEvent(GameEvent.TELEPORT, entity.position(), GameEvent.Context.of(entity));
                    if (!entity.isSilent()) {
                        pLevel.playSound((Player) null, entity.xo, entity.yo, entity.zo, SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 1.0F, 1.0F);
                        entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                    }
                    pLevel.broadcastEntityEvent(entity, (byte) 46);
                }
            }
        }
    }
}
