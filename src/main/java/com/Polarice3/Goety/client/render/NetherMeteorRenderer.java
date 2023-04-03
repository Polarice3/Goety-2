package com.Polarice3.Goety.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.NetherMeteor;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NetherMeteorRenderer extends ExplosiveProjectileRenderer<NetherMeteor> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Goety.MOD_ID,"textures/entity/projectiles/nether_meteor.png");

    public NetherMeteorRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    public ResourceLocation getTextureLocation(NetherMeteor entity) {
        return TEXTURE;
    }
}
