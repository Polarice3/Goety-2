package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.NecroticCandlestick;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.MiscCapHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class NecroticCandlestickBlockEntity extends BlockEntity {

    public NecroticCandlestickBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.NECROTIC_CANDLESTICK.get(), p_155229_, p_155230_);
    }

    public void tick(){
        if (this.level != null && !this.level.isClientSide){
            if (this.getBlockState().hasProperty(NecroticCandlestick.LIT)) {
                if (this.getBlockState().getValue(NecroticCandlestick.LIT)) {
                    AABB aabb = new AABB(this.worldPosition).inflate(8.0D);
                    for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, aabb, undead -> undead.getMobType() == MobType.UNDEAD || undead instanceof Player)) {
                        MiscCapHelper.setSunscreen(livingEntity, 20);
                        if (livingEntity.getMobType() == MobType.UNDEAD) {
                            if ((!this.level.canSeeSky(this.worldPosition) && !this.level.canSeeSky(livingEntity.blockPosition())) || this.level.isNight()) {
                                if (livingEntity.getHealth() < livingEntity.getMaxHealth()) {
                                    if (livingEntity.tickCount % 100 == 0) {
                                        livingEntity.addEffect(new MobEffectInstance(GoetyEffects.NECROSIS.get(), 55, 0, false, false));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
