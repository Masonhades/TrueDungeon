//package me.Masonhades.truedungeon.commands;
//
//
//import me.Masonhades.truedungeon.Truedungeon;
//import me.Masonhades.truedungeon.gui.CustomMenu;
//
//import net.minecraft.commands.Commands;
//import net.minecraft.network.chat.Component;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.SimpleMenuProvider;
//import net.minecraftforge.event.RegisterCommandsEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
//@Mod.EventBusSubscriber(modid = Truedungeon.MODID)
//public class CommandDispatcher {
//
//    @SubscribeEvent
//    public static void register(RegisterCommandsEvent event) {
//        event.getDispatcher().register(
//                Commands.literal("openmenu")
//                        .requires(src -> src.hasPermission(0))
//                        .executes(ctx -> {
//                            ServerPlayer player = ctx.getSource().getPlayerOrException();
//                            player.openMenu(new SimpleMenuProvider(
//                                    (id, inv, p) -> new CustomMenu(id, inv),
//                                    Component.literal("Custom Menu")
//                            ));
//                            return 1;
//                        })
//        );
//    }
//}
//
//
