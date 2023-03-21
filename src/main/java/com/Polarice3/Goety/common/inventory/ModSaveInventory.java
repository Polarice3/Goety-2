package com.Polarice3.Goety.common.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Code based from @cnlimiter's Portable-Craft mod
 */
public class ModSaveInventory extends SavedData {
    private static final String NAME = "GoetySavedData";
    private static final String BREWING = "WITCH_ROBE_BREW";
    public static List<Container> witchRobeInventory = new ArrayList<>();
    private static int witchRobeListNum = 0;

    private static ModSaveInventory INSTANCE;

    public static ModSaveInventory load(CompoundTag compoundTag) {
        ModSaveInventory.read(compoundTag);
        return new ModSaveInventory();
    }

    public static void resetInstance() {
        witchRobeInventory = new ArrayList<>();
        witchRobeListNum = 0;
        INSTANCE = null;
    }

    public static ModSaveInventory getInstance() {
        return INSTANCE;
    }

    public static void setInstance(ServerLevel world) {
        if (world == null) return;
        DimensionDataStorage manager = world.getDataStorage();
        INSTANCE = manager.computeIfAbsent(ModSaveInventory::load, ModSaveInventory::new, NAME);
    }

    public static void read(CompoundTag nbt) {
        ListTag listBrewingStand = nbt.getList(BREWING, 10);

        for (int i = 0; i < listBrewingStand.size(); ++i) {
            witchRobeInventory.add(new WitchRobeInventory(listBrewingStand.getCompound(i)));
        }

        int BrewingStand;
        if (witchRobeInventory.size() != 0) {
            for (Container inventoryBrewingStand : witchRobeInventory) {
                WitchRobeInventory robeInventory = (WitchRobeInventory) inventoryBrewingStand;
                BrewingStand = robeInventory.getInventoryNum();
                if (witchRobeListNum == 0) {
                    witchRobeListNum = BrewingStand;
                } else if (witchRobeListNum < BrewingStand) {
                    witchRobeListNum = BrewingStand;
                }
            }
        }

    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag listBrewingStand = new ListTag();
        for (Container inventoryBrewingStand : witchRobeInventory) {
            WitchRobeInventory BrewingStand = (WitchRobeInventory) inventoryBrewingStand;
            CompoundTag nbt2 = new CompoundTag();
            BrewingStand.save(nbt2);
            listBrewingStand.add(nbt2);
        }

        nbt.put(BREWING, listBrewingStand);
        return nbt;
    }

    public int addAndCreateWitchRobe() {
        ++witchRobeListNum;
        WitchRobeInventory robeInventory = new WitchRobeInventory();
        robeInventory.setInventoryNum(witchRobeListNum);
        witchRobeInventory.add(robeInventory);
        this.setDirty();
        return witchRobeListNum;
    }

    public WitchRobeInventory getWitchRobeInventory(int inv, LivingEntity player) {
        Iterator<Container> var2 = witchRobeInventory.iterator();

        WitchRobeInventory witchRobeInventory;
        do {
            if (!var2.hasNext()) {
                WitchRobeInventory witchRobeInventory1 = new WitchRobeInventory();
                witchRobeInventory1.setInventoryNum(inv);
                ModSaveInventory.witchRobeInventory.add(witchRobeInventory1);
                this.setDirty();
                return witchRobeInventory1;
            }

            witchRobeInventory = (WitchRobeInventory) var2.next();
        } while (witchRobeInventory.getInventoryNum() != inv);

        witchRobeInventory.setLivingEntity(player);

        return witchRobeInventory;
    }

}
