package net.arathain.bookofdragons.common.menu;

import net.arathain.bookofdragons.BookOfDragons;
import net.arathain.bookofdragons.common.entity.util.AbstractRideableDragonEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class DragonScreenHandler extends ScreenHandler {
    private static AbstractRideableDragonEntity dragon = null;
    private final SimpleInventory dragonContainer;


    public DragonScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory,
                playerInventory.player.getWorld().getEntityById(buf.readVarInt()) instanceof AbstractRideableDragonEntity deez ? deez : null
        );
    }
    public DragonScreenHandler(int syncId, PlayerInventory playerInventory, AbstractRideableDragonEntity dragon) {
        this(syncId, playerInventory, new SimpleInventory(dragon.getInventorySize()), dragon);
    }

    public static DragonScreenHandler dragonMenu(int containerId, PlayerInventory inventory, AbstractRideableDragonEntity actualDragon) {
        dragon = actualDragon;
        return new DragonScreenHandler(containerId, inventory, dragon.inventory, dragon);
    }

    public DragonScreenHandler(int syncId, PlayerInventory playerInventory, SimpleInventory inventory, AbstractRideableDragonEntity entity) {
        super(BookOfDragons.DRAGON_SCREEN_HANDLER_TYPE, syncId);
        this.dragonContainer = dragon.inventory;
        dragon = entity;
        //checkSize(inventory, entity.getInventorySize());

        dragonContainer.onOpen(playerInventory.player);

        // Dragon Inventory
        this.addSlot(new Slot(inventory, 0, 8, 18) {
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.SADDLE) && !this.hasStack() && dragon.canBeSaddled();
            }

            public boolean isEnabled() {
                return dragon.canBeSaddled();
            }
        });
        this.addSlot(new Slot(inventory, 1, 8, 36) {
            public boolean canInsert(ItemStack p_39690_) {
                return false;
                //return dragon.isArmor(p_39690_);
            }

            public boolean isEnabled() {
                return false;
                //return dragon.canWearArmor();
            }

            public int getMaxStackSize() {
                return 1;
            }
        });
        if (this.hasChest(dragon)) {
            for(int k = 0; k < 3; ++k) {
                for(int l = 0; l < dragon.getInventoryColumns(); ++l) {
                    this.addSlot(new Slot(inventory, 3 + l + k * dragon.getInventoryColumns(), 80 + l * 18, 18 + k * 18));
                }
            }
        }

        // Player Inventory
        for(int i1 = 0; i1 < 3; ++i1) {
            for(int k1 = 0; k1 < 9; ++k1) {
                this.addSlot(new Slot(playerInventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 102 + i1 * 18 + -18));
            }
        }

        // Player Hotbar
        for(int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(playerInventory, j1, 8 + j1 * 18, 142));
        }
    }

    private boolean hasChest(AbstractRideableDragonEntity p_150578_) {
        return p_150578_ != null && p_150578_.hasChest();
    }

    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            int i = this.dragonContainer.size();
            if (index < i) {
                if (!this.insertItem(itemStack2, i, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(1).canInsert(itemStack2) && !this.getSlot(1).hasStack()) {
                if (!this.insertItem(itemStack2, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).canInsert(itemStack2)) {
                if (!this.insertItem(itemStack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i <= 2 || !this.insertItem(itemStack2, 2, i, false)) {
                int k;
                int j = i;
                int l = k = j + 27;
                int m = l + 9;
                if (index >= l && index < m ? !this.insertItem(itemStack2, j, k, false) : (index >= j && index < k ? !this.insertItem(itemStack2, l, m, false) : !this.insertItem(itemStack2, l, k, false))) {
                    return ItemStack.EMPTY;
                }
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemStack;
    }


    public void close(PlayerEntity player) {
        super.close(player);
        this.dragonContainer.onClose(player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
        //return dragon != null && !dragon.hasInventoryChanged(this.dragonContainer) && this.dragonContainer.stillValid(player) && dragon.isAlive() && dragon.distanceTo(player) < 8.0F;
    }

    public AbstractRideableDragonEntity getDragon() {
        return dragon;
    }
}
