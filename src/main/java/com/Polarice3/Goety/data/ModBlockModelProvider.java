package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockModelProvider extends BlockModelProvider {

    public ModBlockModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Goety.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

    }
}
