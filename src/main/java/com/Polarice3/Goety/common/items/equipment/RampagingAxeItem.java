package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RampagingAxeItem extends AxeItem {
    public RampagingAxeItem() {
        super(ModTiers.SPECIAL, 5.0F, -3.0F, (new Item.Properties()).rarity(Rarity.UNCOMMON).tab(Goety.TAB));
    }

    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (pState.is(BlockTags.MINEABLE_WITH_AXE)){
            MobEffectInstance effectinstance1 = pEntityLiving.getEffect(GoetyEffects.RAMPAGE.get());
            if (!pEntityLiving.hasEffect(GoetyEffects.RAMPAGE.get())){
                pEntityLiving.addEffect(new MobEffectInstance(GoetyEffects.RAMPAGE.get(), MathHelper.secondsToTicks(10)));
            } else if (effectinstance1 != null){
                if (effectinstance1.getAmplifier() < 4 && pLevel.random.nextFloat() <= 0.25F) {
                    EffectsUtil.amplifyEffect(pEntityLiving, GoetyEffects.RAMPAGE.get(), MathHelper.secondsToTicks(10));
                } else {
                    EffectsUtil.resetDuration(pEntityLiving, GoetyEffects.RAMPAGE.get(), MathHelper.secondsToTicks(10));
                }
            }
        }
        return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
    }

    @SubscribeEvent
    public static void AxeDeath(LivingDeathEvent event){
        LivingEntity killed = event.getEntity();
        Entity killer = event.getSource().getEntity();
        Level world = killed.getCommandSenderWorld();
        if (killer instanceof LivingEntity livingEntity) {
            if (ModDamageSource.physicalAttacks(event.getSource()) && livingEntity.getMainHandItem().getItem() instanceof RampagingAxeItem) {
                MobEffectInstance effectinstance1 = livingEntity.getEffect(GoetyEffects.RAMPAGE.get());
                if (!livingEntity.hasEffect(GoetyEffects.RAMPAGE.get())){
                    livingEntity.addEffect(new MobEffectInstance(GoetyEffects.RAMPAGE.get(), MathHelper.secondsToTicks(10)));
                } else if (effectinstance1 != null){
                    int random = killed.getMaxHealth() > 20 ? 0 : world.random.nextInt(4);
                    if (effectinstance1.getAmplifier() < 4) {
                        if (random == 0) {
                            EffectsUtil.amplifyEffect(livingEntity, GoetyEffects.RAMPAGE.get(), MathHelper.secondsToTicks(10));
                        }
                    } else {
                        livingEntity.removeEffect(GoetyEffects.RAMPAGE.get());
                        if (world instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(new ShockwaveParticleOption(), livingEntity.getX(), livingEntity.getY() + 0.5F, livingEntity.getZ(), 0, 0.0D, 0.0D, 0.0D, 0);
                            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 0, 1.0D, 0.0D, 0.0D, 0.5F);
                        }
                        world.explode(livingEntity, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 3.0F, Explosion.BlockInteraction.NONE);
                    }
                }
            }
        }
    }
}
