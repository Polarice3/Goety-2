package com.Polarice3.Goety.common.entities.vehicle;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.IntFunction;

public class ModBoat extends Boat {
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(ModBoat.class, EntityDataSerializers.INT);

    public ModBoat(EntityType<? extends Boat> p_i50129_1_, Level p_i50129_2_) {
        super(p_i50129_1_, p_i50129_2_);
        this.blocksBuilding = true;
    }

    public ModBoat(Level p_i1705_1_, double p_i1705_2_, double p_i1705_4_, double p_i1705_6_) {
        this(ModEntityType.MOD_BOAT.get(), p_i1705_1_);
        this.setPos(p_i1705_2_, p_i1705_4_, p_i1705_6_);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = p_i1705_2_;
        this.yo = p_i1705_4_;
        this.zo = p_i1705_6_;
    }

    public void setVariant(ModBoat.Type p_38333_) {
        this.entityData.set(DATA_ID_TYPE, p_38333_.ordinal());
    }

    public ModBoat.Type getModVariant() {
        return ModBoat.Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    protected void addAdditionalSaveData(CompoundTag p_38359_) {
        p_38359_.putString("Type", this.getModVariant().getSerializedName());
    }

    protected void readAdditionalSaveData(CompoundTag p_38338_) {
        if (p_38338_.contains("Type", 8)) {
            this.setVariant(ModBoat.Type.byName(p_38338_.getString("Type")));
        }

    }

    public Item getDropItem() {
        return switch (this.getModBoatType()) {
            case HAUNTED -> ModItems.HAUNTED_BOAT.get();
            case ROTTEN -> ModItems.ROTTEN_BOAT.get();
            case WINDSWEPT -> ModItems.WINDSWEPT_BOAT.get();
            case PINE -> ModItems.PINE_BOAT.get();
            case CHORUS -> ModItems.CHORUS_BOAT.get();
            case CORRUPT_CHORUS -> ModItems.CORRUPT_CHORUS_BOAT.get();
        };
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE, Type.HAUNTED.ordinal());
    }

    public void setType(Type pBoatType) {
        this.entityData.set(DATA_ID_TYPE, pBoatType.ordinal());
    }

    public Type getModBoatType() {
        return Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    public enum Type implements StringRepresentable {
        HAUNTED(ModBlocks.HAUNTED_PLANKS.get(), "haunted"),
        ROTTEN(ModBlocks.ROTTEN_PLANKS.get(), "rotten"),
        WINDSWEPT(ModBlocks.WINDSWEPT_PLANKS.get(), "windswept"),
        PINE(ModBlocks.PINE_PLANKS.get(), "pine"),
        CHORUS(ModBlocks.CHORUS_PLANKS.get(), "chorus"),
        CORRUPT_CHORUS(ModBlocks.CORRUPT_CHORUS_PLANKS.get(), "corrupt_chorus");

        private final String name;
        private final Block planks;
        public static final StringRepresentable.EnumCodec<ModBoat.Type> CODEC = StringRepresentable.fromEnum(ModBoat.Type::values);
        private static final IntFunction<ModBoat.Type> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

        private Type(Block p_i48146_3_, String p_i48146_4_) {
            this.name = p_i48146_4_;
            this.planks = p_i48146_3_;
        }

        public String getSerializedName() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public Block getPlanks() {
            return this.planks;
        }

        public String toString() {
            return this.name;
        }

        public static ModBoat.Type byId(int p_38431_) {
            return BY_ID.apply(p_38431_);
        }

        public static ModBoat.Type byName(String p_38433_) {
            return CODEC.byName(p_38433_, HAUNTED);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
