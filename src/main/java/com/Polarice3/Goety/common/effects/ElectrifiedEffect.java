package com.Polarice3.Goety.common.effects;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SThunderBoltPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Iterator;
import java.util.List;

public class ElectrifiedEffect extends GoetyBaseEffect {
    public ElectrifiedEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
    }

    public void applyEffectTick(LivingEntity living, int amplify) {
        if (living.isAlive()) {
            if (living.level instanceof ServerLevel worldIn) {
                boolean flag;
                if (living.isInvisible()) {
                    flag = living.getRandom().nextInt(15) == 0;
                } else {
                    flag = living.getRandom().nextBoolean();
                }
                if (flag) {
                    worldIn.sendParticles(ModParticleTypes.SPELL_ELECTRIC.get(), living.getRandomX(0.5D), living.getRandomY(), living.getRandomZ(0.5D), 1, 0.0D, 0.5D, 0.0D, 0);
                }
                if (living.tickCount % 80 == 0) {
                    List<Entity> list = living.level.getEntities(living, living.getBoundingBox().inflate(4.0D), selected -> selected instanceof LivingEntity selected2 && living.hasLineOfSight(selected2) && MobUtil.isOwnedTargetable(living, selected2));
                    Vec3 vec3 = living.getEyePosition();
                    Iterator<Entity> iterator = list.iterator();
                    int i = 0;
                    while (iterator.hasNext() && i < 2) {
                        Entity entity = iterator.next();
                        if (entity instanceof LivingEntity target) {
                            Vec3 vec31 = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ());
                            ModNetwork.sendToALL(new SThunderBoltPacket(vec3, vec31, 10));
                            if (target.hurt(ModDamageSource.directShock(living), 4.0F * (amplify + 1))) {
                                float chance = 0.05F;
                                if (worldIn.isThundering() && worldIn.isRainingAt(target.blockPosition())) {
                                    chance += 0.25F;
                                }
                                if (worldIn.random.nextFloat() <= chance) {
                                    target.addEffect(new MobEffectInstance(GoetyEffects.SPASMS.get(), MathHelper.secondsToTicks(5)));
                                }
                                target.knockback(2.0F, living.getX() - target.getX(), living.getZ() - target.getZ());
                            }
                            worldIn.playSound(null, living.getX(), living.getY(), living.getZ(), ModSounds.ZAP.get(), living.getSoundSource(), 1.0F, 1.0F);
                            ++i;
                        }
                    }
                }
            }
        }
    }
}
