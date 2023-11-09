package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.CorruptedBeam;
import com.Polarice3.Goety.common.magic.EverChargeSpells;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CorruptedBeamSpell extends EverChargeSpells {
    @Override
    public int SoulCost() {
        return SpellConfig.CorruptionCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.CORRUPT_BEAM_START.get();
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        List<CorruptedBeam> entities = worldIn.getEntitiesOfClass(CorruptedBeam.class, entityLiving.getBoundingBox().inflate(2.0F),
                corruptedBeam -> corruptedBeam.getOwner() == entityLiving);
        Vec3 vector3d = entityLiving.getViewVector( 1.0F);
        if (entities.isEmpty()) {
            CorruptedBeam corruptedBeam = new CorruptedBeam(ModEntityType.CORRUPTED_BEAM.get(), worldIn, entityLiving);
            corruptedBeam.moveTo(
                    entityLiving.getX() + vector3d.x / 2,
                    entityLiving.getEyeY() - 0.2,
                    entityLiving.getZ() + vector3d.z / 2, entityLiving.getYRot(), entityLiving.getXRot());
            corruptedBeam.setOwner(entityLiving);
            corruptedBeam.setItemBase(true);
            worldIn.addFreshEntity(corruptedBeam);
        }
    }
}
