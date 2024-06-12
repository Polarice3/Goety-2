package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.SnapWartsBlock;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WarlockRobeItem extends WarlockGarmentItem{

    @SubscribeEvent
    public static void LivingEffects(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (CuriosFinder.hasWarlockRobe(livingEntity)){
            if (livingEntity.getRandom().nextFloat() < 7.5E-4F){
                for(int i = 0; i < livingEntity.getRandom().nextInt(35) + 10; ++i) {
                    livingEntity.level.addParticle(ModParticleTypes.WARLOCK.get(), livingEntity.getX() + livingEntity.getRandom().nextGaussian() * (double)0.13F, livingEntity.getBoundingBox().maxY + 0.5D + livingEntity.getRandom().nextGaussian() * (double)0.13F, livingEntity.getZ() + livingEntity.getRandom().nextGaussian() * (double)0.13F, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @SubscribeEvent
    public static void TargetEvents(LivingChangeTargetEvent event){
        if (event.getEntity().getMobType() == MobType.ARTHROPOD){
            if (CuriosFinder.hasWarlockRobe(event.getOriginalTarget())) {
                event.setNewTarget(null);
            }
        }
    }

    @SubscribeEvent
    public static void HurtEvent(LivingHurtEvent event){
        LivingEntity victim = event.getEntity();
        if (CuriosFinder.hasWarlockRobe(victim)){
            if (event.getSource().isMagic()){
                float resistance = 1.0F - (ItemConfig.WarlockRobeResistance.get() / 100.0F);
                event.setAmount(event.getAmount() * resistance);
            }
        }
    }

    @SubscribeEvent
    public static void onBreakingBlock(BlockEvent.BreakEvent event){
        Player player = event.getPlayer();
        if (CuriosFinder.hasWarlockRobe(player)){
            if (event.getState().is(ModBlocks.SNAP_WARTS.get()) && event.getState().getValue(SnapWartsBlock.AGE) >= 2){
                if (!player.level.isClientSide) {
                    if (player.getRandom().nextFloat() <= 0.25F){
                        Block.dropResources(event.getState(), player.level, event.getPos(), null, player, player.getUseItem());
                    }
                }
            }
        }
    }
}
