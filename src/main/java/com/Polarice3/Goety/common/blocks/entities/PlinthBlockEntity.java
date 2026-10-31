package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.api.blocks.IEnchantedBlock;
import com.Polarice3.Goety.common.blocks.PlinthBlock;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

//Backported from Goety 3, made by Vivideru: https://github.com/Vivideru/Goety-3/blob/main/src/main/java/com/Vivideru/Goety/common/blocks/entities/PlinthBlockEntity.java
public class PlinthBlockEntity extends BlockEntity implements IEnchantedBlock {
    public static final int BASE_RADIUS = 4;
    public static final int RADIUS_PER_LEVEL = 6;
    private final Object2IntMap<Enchantment> enchantments = new Object2IntOpenHashMap<>();
    @Nullable
    private UUID occupant;
    private int tickCount;

    public PlinthBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLINTH.get(), pos, state);
    }

    @Override
    public Object2IntMap<Enchantment> getEnchantments() {
        return this.enchantments;
    }

    public int getRadius() {
        return BASE_RADIUS + RADIUS_PER_LEVEL * this.getRadiusLevel();
    }

    public int getRadiusLevel() {
        if (this.level == null || this.enchantments.isEmpty()) {
            return 0;
        }
        Registry<Enchantment> registry = this.level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        for (Object2IntMap.Entry<Enchantment> entry : this.enchantments.object2IntEntrySet()) {
            if (registry.getResourceKey(entry.getKey()).filter(key -> key == ModEnchantments.RADIUS.getKey()).isPresent()) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    @Nullable
    public UUID getOccupant() {
        return this.occupant;
    }

    public void setOccupant(@Nullable UUID occupant) {
        this.occupant = occupant;
        this.setChanged();
        if (occupant == null) {
            this.setCharged(false);
        }
    }

    public void setCharged(boolean charged) {
        this.updateCharged(charged);
    }

    @Nullable
    public Mob getOccupantMob() {
        if (this.level instanceof ServerLevel serverLevel) {
            if (this.occupant != null) {
                Entity entity = serverLevel.getEntity(this.occupant);
                return entity instanceof Mob mob && mob.isAlive() ? mob : null;
            }
        }
        return null;
    }

    public Vec3 restPosition() {
        return new Vec3(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.125D, this.worldPosition.getZ() + 0.5D);
    }

    private void updateCharged(boolean charged) {
        if (this.level != null) {
            BlockState state = this.getBlockState();
            if (state.hasProperty(PlinthBlock.CHARGED) && state.getValue(PlinthBlock.CHARGED) != charged) {
                this.level.setBlock(this.worldPosition, state.setValue(PlinthBlock.CHARGED, charged), 3);
            }
        }
    }

    public void tick() {
        if (this.level instanceof ServerLevel serverLevel) {
            ++this.tickCount;
            if (this.tickCount % 10 == 0) {
                PlinthHooks.tickPlinth(serverLevel, this, this.tickCount % 20 == 0);
            }
        }
    }

    public void release() {
        if (this.level instanceof ServerLevel) {
            Mob mob = this.getOccupantMob();
            if (mob != null) {
                PlinthHooks.wake(mob, null);
                PlinthHooks.clearPlinth(mob);
            }
        }
        this.occupant = null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        this.saveEnchants(tag, this.getBlockState().getBlock().asItem());
        if (this.occupant != null) {
            tag.put("Occupant", NbtUtils.createUUID(this.occupant));
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.loadEnchants(tag);
        this.occupant = tag.hasUUID("Occupant") ? tag.getUUID("Occupant") : null;
    }
}
