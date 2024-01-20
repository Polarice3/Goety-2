package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class Harpoon extends Arrow {
    public Harpoon(EntityType<? extends Arrow> p_36858_, Level p_36859_) {
        super(p_36858_, p_36859_);
    }

    public Harpoon(Level p_36866_, LivingEntity p_36867_) {
        super(p_36866_, p_36867_);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntityType.HARPOON.get();
    }

    protected float getWaterInertia() {
        return 0.99F;
    }

    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return ModSounds.HARPOON_HIT.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isInWater()){
            this.setSoundEvent(ModSounds.HARPOON_HIT_WATER.get());
        } else {
            this.setSoundEvent(ModSounds.HARPOON_HIT.get());
        }
    }

    protected void onHitBlock(BlockHitResult p_36755_) {
        super.onHitBlock(p_36755_);
        if (this.isInWater()){
            this.setSoundEvent(ModSounds.HARPOON_HIT_WATER.get());
        } else {
            this.setSoundEvent(ModSounds.HARPOON_HIT.get());
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
