package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.OminousIdolBlock;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.items.block.OminousIdolBlockItem;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.ModTicketTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OminousIdolBlockEntity extends OwnedBlockEntity {
    public static String ILLAGER_LIST = OminousIdolBlockItem.ILLAGER_LIST;
    public static String CLIENT_ID_LIST = "client_id_list";
    public List<RaiderServant> illagers = new ArrayList<>();
    public List<UUID> uuids = new ArrayList<>();
    public List<Integer> ids = new ArrayList<>();
    public int clientCount = 0;
    private CursedCageBlockEntity cursedCageTile;
    public long ticketTime = 0;
    public boolean isEmpty = false;

    public OminousIdolBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.OMINOUS_IDOL.get(), p_155229_, p_155230_);
    }

    public void tick(){
        if (this.level != null) {
            if (this.level instanceof ServerLevel world) {
                if (!this.uuids.isEmpty()) {
                    ChunkPos chunkPos = world.getChunkAt(this.worldPosition).getPos();
                    if (--this.ticketTime <= 0L) {
                        world.getChunkSource().addRegionTicket(ModTicketTypes.BLOCK, chunkPos, 9, this.worldPosition);
                        this.ticketTime = ModTicketTypes.BLOCK.timeout() - 1L;
                    }
                    this.uuids.removeIf(uuid -> {
                        Entity entity = EntityFinder.getLivingEntityByUuiD(uuid);
                        if (!(entity instanceof RaiderServant illagerServant)){
                            this.markUpdated();
                            return true;
                        } else if (illagerServant.isRemoved() || !illagerServant.isAlive() || (illagerServant.getIdol() != null && illagerServant.getIdol() != this)) {
                            this.illagers.remove(illagerServant);
                            this.markUpdated();
                            return true;
                        }
                        return false;
                    });
                    for (UUID uuid : this.uuids) {
                        if (uuid != null) {
                            Entity entity = EntityFinder.getLivingEntityByUuiD(uuid);
                            if (entity instanceof RaiderServant illagerServant) {
                                if (!this.illagers.contains(illagerServant)) {
                                    this.illagers.add(illagerServant);
                                }
                            }
                        }
                    }
                    if (!this.getIllagers().isEmpty()) {
                        for (RaiderServant raider : this.getIllagers()) {
                            if (raider.isAlive() && raider.getIdol() == this) {
                                if (!this.ids.contains(raider.getId())) {
                                    this.ids.add(raider.getId());
                                    this.markUpdated();
                                }
                            }
                            if (!BlockFinder.samePos(raider.getRevivePos(), this.getBlockPos())) {
                                raider.setRevivePos(this.getBlockPos());
                                raider.setReviveDim(this.level.dimension());
                            }
                        }
                    }
                }
                if (!this.ids.isEmpty()) {
                    this.isEmpty = false;
                    if (this.ids.removeIf(integer -> {
                        Entity entity = this.level.getEntity(integer);
                        if (entity instanceof RaiderServant raider) {
                            return !raider.isAlive() || raider.isRemoved();
                        } else {
                            return true;
                        }
                    })) {
                        this.markUpdated();
                    }
                } else if (!this.isEmpty) {
                    this.markUpdated();
                    this.isEmpty = true;
                }
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(OminousIdolBlock.POWERED, this.checkCage() && !this.getIllagers().isEmpty()), 3);
            }
            this.clientCount = this.ids.size();
        }
    }

    public List<RaiderServant> getIllagers() {
        return this.illagers;
    }

    public void addIllager(RaiderServant raider){
        if (!this.uuids.contains(raider.getUUID())) {
            this.uuids.add(raider.getUUID());
        }
    }

    public void removeIllager(RaiderServant raider){
        this.uuids.remove(raider.getUUID());
    }

    public boolean hasSpace() {
        return this.getIllagers().size() < MainConfig.OminousIdolLimit.get();
    }

    public List<UUID> getUuids(){
        return this.uuids;
    }

    public List<Integer> getIds(){
        return this.ids;
    }

    public int getClientCount() {
        return this.clientCount;
    }

    private boolean checkCage() {
        if (this.level != null) {
            BlockPos pos = this.getBlockPos().below();
            BlockState blockState = this.level.getBlockState(pos);
            if (blockState.is(ModBlocks.CURSED_CAGE_BLOCK.get())) {
                BlockEntity tileentity = this.level.getBlockEntity(pos);
                if (tileentity instanceof CursedCageBlockEntity) {
                    this.cursedCageTile = (CursedCageBlockEntity) tileentity;
                    return !cursedCageTile.getItem().isEmpty();
                }
            }
        }
        return false;
    }

    public int getSoulEnergy() {
        if (this.level != null) {
            if (this.cursedCageTile != null) {
                return this.cursedCageTile.getSouls();
            }
        }
        return 0;
    }

    public void siphonSoulEnergy(int souls) {
        if (this.level != null) {
            if (this.cursedCageTile != null) {
                this.cursedCageTile.decreaseSouls(souls);
            }
        }
    }

    @Override
    public void readNetwork(CompoundTag tag) {
        super.readNetwork(tag);
        if (tag.contains(ILLAGER_LIST)){
            ListTag list = tag.getList(ILLAGER_LIST, Tag.TAG_STRING);
            for(int i = 0; i < list.size(); ++i) {
                UUID uuid = UUID.fromString(list.getString(i));
                if (!this.uuids.contains(uuid)) {
                    this.uuids.add(uuid);
                }
            }
        }
        if (!this.ids.isEmpty()) {
            this.ids.clear();
        }
        if (tag.contains(CLIENT_ID_LIST)){
            ListTag list = tag.getList(CLIENT_ID_LIST, Tag.TAG_INT);
            for(int i = 0; i < list.size(); ++i) {
                if (this.ids.isEmpty() || !this.ids.contains(list.getInt(i))) {
                    this.ids.add(list.getInt(i));
                }
            }
        }
    }

    @Override
    public CompoundTag writeNetwork(CompoundTag tag) {
        super.writeNetwork(tag);
        List<String> list = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        if (tag.contains(ILLAGER_LIST)) {
            for (int i = 0; i < tag.getList(ILLAGER_LIST, Tag.TAG_STRING).size(); ++i) {
                list.add(tag.getList(ILLAGER_LIST, Tag.TAG_STRING).getString(i));
            }
        }
        if (!this.uuids.isEmpty()) {
            for (UUID uuid : this.uuids) {
                if (!list.contains(uuid.toString())) {
                    ListTag nbttaglist = new ListTag();
                    if (tag.contains(ILLAGER_LIST)) {
                        nbttaglist = tag.getList(ILLAGER_LIST, Tag.TAG_STRING);
                    }

                    nbttaglist.add(StringTag.valueOf(uuid.toString()));
                    tag.put(ILLAGER_LIST, nbttaglist);
                }
            }
        }
        if (tag.contains(CLIENT_ID_LIST)) {
            for (int i = 0; i < tag.getList(CLIENT_ID_LIST, Tag.TAG_INT).size(); ++i) {
                list2.add(tag.getList(CLIENT_ID_LIST, Tag.TAG_INT).getInt(i));
            }
        }
        if (!this.ids.isEmpty()) {
            for (Integer integer : this.ids) {
                if (!list2.contains(integer)) {
                    ListTag nbttaglist = new ListTag();
                    if (tag.contains(CLIENT_ID_LIST)) {
                        nbttaglist = tag.getList(CLIENT_ID_LIST, Tag.TAG_INT);
                    }

                    nbttaglist.add(IntTag.valueOf(integer));
                    tag.put(CLIENT_ID_LIST, nbttaglist);
                }
            }
        }
        return tag;
    }

    public void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
        }
    }
}
