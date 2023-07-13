package com.Polarice3.Goety.common.research;

import com.Polarice3.Goety.Goety;
import net.minecraft.resources.ResourceLocation;

public class Research {
    public String id;

    public Research(String id){
        this.id = id;
    }

    public ResourceLocation getLocation(){
        return Goety.location(this.id);
    }

    public String getId(){
        return this.id;
    }
}
