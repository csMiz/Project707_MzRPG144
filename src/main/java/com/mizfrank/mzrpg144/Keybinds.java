package com.mizfrank.mzrpg144;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class Keybinds {

    public static KeyBinding KEY_RELOAD;
    public static KeyBinding KEY_SWITCH_AMMO;

    public static void register()
    {
        KEY_RELOAD = new KeyBinding("mz.keyname.reload", GLFW.GLFW_KEY_R, "mz.keycat");
        KEY_SWITCH_AMMO = new KeyBinding("mz.keyname.swammo", GLFW.GLFW_KEY_C, "mz.keycat");

        ClientRegistry.registerKeyBinding(KEY_RELOAD);
        ClientRegistry.registerKeyBinding(KEY_SWITCH_AMMO);

    }

}
