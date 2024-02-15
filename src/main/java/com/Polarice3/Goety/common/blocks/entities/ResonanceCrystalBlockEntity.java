package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.api.blocks.entities.IWindPowered;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.ResonanceCrystalBlock;
import com.Polarice3.Goety.common.entities.ally.SquallGolem;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResonanceCrystalBlockEntity extends ModBlockEntity implements IWindPowered {
    public List<SquallGolem> squallGolems = new ArrayList<>();
    public List<UUID> uuids = new ArrayList<>();
    public int active;
    private boolean isOn;

    public ResonanceCrystalBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.RESONANCE_CRYSTAL.get(), p_155229_, p_155230_);
    }

    public void tick(){
        if (this.level != null) {
            if (this.active > 0) {
                --this.active;
            }
            if (this.getBlockState().getValue(ResonanceCrystalBlock.POWERED)) {
                if (!this.isOn){
                    this.level.playSound(null, this.getBlockPos(), ModSounds.RESONANCE_CRYSTAL_ON.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    this.isOn = true;
                } else {
                    if (this.level.getGameTime() % MathHelper.secondsToTicks(6) == 0){
                        this.level.playSound(null, this.getBlockPos(), ModSounds.RESONANCE_CRYSTAL_LOOP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
                if (this.level instanceof ServerLevel world) {
                    ChunkPos chunkPos = this.level.getChunkAt(this.worldPosition).getPos();
                    if (!world.getForcedChunks().contains(chunkPos.toLong())) {
                        world.setChunkForced(chunkPos.x, chunkPos.z, true);
                        if (!world.isLoaded(this.worldPosition)) {
                            world.getChunkAt(this.worldPosition).setLoaded(true);
                        }
                    }
                    BlockPos blockPos = this.getBlockPos();
                    ServerParticleUtil.gatheringBlockParticles(ModParticleTypes.RESONANCE_GATHER.get(), blockPos, world);
                    ServerParticleUtil.addAuraParticles(world, ParticleTypes.CLOUD, blockPos.getX() + 0.5F, blockPos.getY(), blockPos.getZ() + 0.5F, 1.0F);
                    for (UUID uuid : this.uuids){
                        Entity entity = EntityFinder.getEntityByUuiD(uuid);
                        if (entity instanceof SquallGolem squallGolem){
                            if (!this.squallGolems.contains(squallGolem)){
                                this.squallGolems.add(squallGolem);
                            } else if (!squallGolem.isAlive()){
                                this.squallGolems.remove(squallGolem);
                                this.uuids.remove(uuid);
                            }
                        }
                    }
                    this.activateGolems();
                }
            } else {
                if (this.isOn){
                    this.level.playSound(null, this.getBlockPos(), ModSounds.RESONANCE_CRYSTAL_OFF.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    this.isOn = false;
                }
            }
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(ResonanceCrystalBlock.POWERED, this.active > 0), 3);
        }
    }

    public void activateGolems(){
        if (!this.squallGolems.isEmpty()){
            for (SquallGolem squallGolem : this.squallGolems){
                if (squallGolem != null) {
                    squallGolem.activate(20);
                }
            }
        }
    }

    public List<SquallGolem> getSquallGolems() {
        return this.squallGolems;
    }

    public void addSquallGolem(SquallGolem squallGolem){
        this.uuids.add(squallGolem.getUUID());
    }

    public List<UUID> getUuids(){
        return this.uuids;
    }

    @Override
    public int activeTicks() {
        return this.active;
    }

    @Override
    public void activate(int tick) {
        this.active = tick;
    }

    @Override
    public void readNetwork(CompoundTag tag) {
        if (tag.contains("active")){
            this.active = tag.getInt("active");
        }
        if (tag.contains("golemList")){
            ListTag list = tag.getList("golemList", 8);
            for(int i = 0; i < list.size(); ++i) {
                this.uuids.add(UUID.fromString(list.getString(i)));
            }
        }
        if (tag.contains("isOn")){
            this.isOn = tag.getBoolean("isOn");
        }
    }

    @Override
    public CompoundTag writeNetwork(CompoundTag tag) {
        tag.putInt("active", this.active);
        List<String> list = new ArrayList<>();
        if (tag.contains("golemList")) {
            for (int i = 0; i < tag.getList("golemList", 8).size(); ++i) {
                list.add(tag.getList("golemList", 8).getString(i));
            }
        }
        for (UUID uuid : this.uuids) {
            if (!list.contains(uuid.toString())) {
                ListTag nbttaglist = new ListTag();
                if (tag.contains("golemList")) {
                    nbttaglist = tag.getList("golemList", 8);
                }

                nbttaglist.add(StringTag.valueOf(uuid.toString()));
                tag.put("golemList", nbttaglist);
            }
        }
        tag.putBoolean("isOn", this.isOn);
        return tag;
    }
}
