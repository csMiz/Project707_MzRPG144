package com.mizfrank.mzrpg144;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class Keybinds {

    public static KeyBinding KEY_RELOAD;

    public static void register()
    {
        KEY_RELOAD = new KeyBinding("mz.keyname.reload", GLFW.GLFW_KEY_R, "mz.keycat");

        ClientRegistry.registerKeyBinding(KEY_RELOAD);

    }

}
