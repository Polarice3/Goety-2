package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.mixin.ClientModLoaderAccessor;
import net.minecraftforge.fml.LoadingFailedException;

public class ClientUtils {
    public static boolean noLoadingExceptions() {
        System.out.println();
        LoadingFailedException error = ClientModLoaderAccessor.getError();
        return error == null || error.getErrors().isEmpty();
    }
}
