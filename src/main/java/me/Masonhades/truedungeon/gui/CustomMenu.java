package me.Masonhades.truedungeon.gui;

import me.Masonhades.truedungeon.entity.custom.QuestDealerEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CustomMenu extends AbstractContainerMenu {

    private final Level level;
    private final QuestDealerEntity dealer;


    //Client
    public CustomMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, (QuestDealerEntity) playerInv.player.level().getEntity(buf.readVarInt()));
    }

    public CustomMenu(int id, Inventory playerInv, QuestDealerEntity dealer) {
        super(ModMenus.CUSTOM_MENU.get(), id);
        this.dealer = dealer;
        this.level = playerInv.player.level();

        SimpleContainer container = dealer.getQuestInventory();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                int index = i * 9 + j;
                this.addSlot(new Slot(container, index, 8 + j * 18, 18 + i * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }
                });
            }
        }
        addPlayerInventory(playerInv);
        addPlayerHotbar(playerInv);
    }


    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }




    @Override
    public boolean stillValid(Player player) {
        return dealer.isAlive() && player.distanceToSqr(dealer) < 64.0D;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    public QuestDealerEntity getDealer() {
        return dealer;
    }
}
