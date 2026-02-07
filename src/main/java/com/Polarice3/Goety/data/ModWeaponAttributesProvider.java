package com.Polarice3.Goety.data;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.equipment.BladeOfEnderItem;
import com.Polarice3.Goety.common.items.magic.DarkStaff;
import com.Polarice3.Goety.common.items.magic.DarkWand;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.compress.utils.Lists;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

//Stolen from @0999312:https://github.com/0999312/umapyoi/blob/7c60655c6b3ba5f51795b6adaa99ccfad965bc70/src/main/java/net/tracen/umapyoi/data/compat/BetterCombatProvider.java
public class ModWeaponAttributesProvider implements DataProvider {
    protected final PackOutput output;
    protected final ExistingFileHelper existingFileHelper;
    private final ExistingFileHelper.IResourceType resourceType;
    protected final Map<ResourceLocation, String> datas = Maps.newLinkedHashMap();

    public ModWeaponAttributesProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        this.output = output;
        this.existingFileHelper = existingFileHelper;
        this.resourceType = new ExistingFileHelper.ResourceType(PackType.SERVER_DATA, ".json", "weapon_attributes");
    }

    public void addData(Item item, String attribute) {
        this.datas.computeIfAbsent(ForgeRegistries.ITEMS.getKey(item), loc->{
            if (loc != null) {
                existingFileHelper.trackGenerated(loc, resourceType);
            }
            return attribute;
        });
    }

    private void addNewData() {
        ModItems.ITEMS.getEntries().stream().map(RegistryObject::get).forEach(item ->
        {
            if (item instanceof DarkStaff) {
                this.addData(item, "bettercombat:trident");
            } else if (item instanceof DarkWand) {
                this.addData(item, "bettercombat:wand");
            } else if (item instanceof BladeOfEnderItem) {
                this.addData(item, "bettercombat:claymore");
            }
        });
    }

    @Override
    public String getName() {
        return "Goety Better Combat Weapon Attributes Provider";
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        this.datas.clear();
        this.addNewData();
        final Path outputFolder = output.getOutputFolder();
        List<CompletableFuture<?>> futureList = Lists.newArrayList();

        this.datas.forEach( (loc, data) -> {
            String pathString = String.join("/", PackType.SERVER_DATA.getDirectory(), loc.getNamespace(), "weapon_attributes", loc.getPath()+".json");
            Path path = outputFolder.resolve(pathString);

            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("parent", data);

            futureList.add(DataProvider.saveStable(cache, jsonObj, path));
        });
        return CompletableFuture.allOf(futureList.stream().toArray(CompletableFuture<?>[]::new));
    }

}
