package me.Masonhades.truedungeon.gui;

import me.Masonhades.truedungeon.Truedungeon;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Truedungeon.MODID);

    public static final RegistryObject<MenuType<CustomMenu>> CUSTOM_MENU =
            registerMenu("custom_menu", CustomMenu::new);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenu(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }
    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }

}
