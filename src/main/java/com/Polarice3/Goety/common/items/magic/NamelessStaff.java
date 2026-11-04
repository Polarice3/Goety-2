package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.items.INamelessDyeable;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.render.item.CustomItemsRenderer;
import com.Polarice3.Goety.config.ItemConfig;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class NamelessStaff extends DarkStaff implements INamelessDyeable {

    public NamelessStaff() {
        super(ItemConfig.NamelessStaffDamage.get(), SpellType.NECROMANCY);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new DarkWandClient() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new CustomItemsRenderer();
            }
        });
    }
}
