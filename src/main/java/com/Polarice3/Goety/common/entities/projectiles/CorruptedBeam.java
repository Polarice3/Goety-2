package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class CorruptedBeam extends AbstractBeam {

    public CorruptedBeam(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
    }

    public CorruptedBeam(EntityType<?> p_i48580_1_, Level p_i48580_2_, LivingEntity owner) {
        super(p_i48580_1_, p_i48580_2_);
        this.setOwner(owner);
    }

    @Override
    public void damageEntities(Set<LivingEntity> entities) {
        for (LivingEntity entity : entities) {
            entity.invulnerableTime = 0;
            Vec3 deltaMovement = entity.getDeltaMovement();
            float damage = SpellConfig.CorruptedBeamDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
            if (this.getOwner() != null){
                if (WandUtil.enchantedFocus(this.getOwner())){
                    damage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), this.getOwner()) / 2.0F;
                }
            }
            entity.hurt(DamageSource.indirectMagic(owner, owner), damage);
            entity.setDeltaMovement(deltaMovement);
        }
    }
}
