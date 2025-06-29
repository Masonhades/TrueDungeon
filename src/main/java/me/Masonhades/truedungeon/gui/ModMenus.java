package me.Masonhades.truedungeon.gui;

import me.Masonhades.truedungeon.Truedungeon;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Truedungeon.MODID);

    public static final RegistryObject<MenuType<CustomMenu>> CUSTOM_MENU =
            MENUS.register("custom_menu", () -> new MenuType<>(CustomMenu::new, FeatureFlags.VANILLA_SET));
}
