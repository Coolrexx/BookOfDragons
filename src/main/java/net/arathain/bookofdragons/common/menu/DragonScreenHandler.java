package net.arathain.bookofdragons.common.menu;

import net.arathain.bookofdragons.BookOfDragons;
import net.arathain.bookofdragons.common.entity.util.AbstractRideableDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class DragonScreenHandler extends ScreenHandler {
    private static AbstractRideableDragonEntity dragon = null;
    private final SimpleInventory dragonContainer;


    public DragonScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory,
                playerInventory.player.getWorld().getEntityById(buf.readVarInt()) instanceof AbstractRideableDragonEntity deez ? deez.inventory : null,
                buf.readByte()
        );
    }

    public static DragonScreenHandler dragonMenu(int containerId, PlayerInventory inventory) {
        return new DragonScreenHandler(containerId, inventory, dragon.inventory, dragon.getId());
    }

    public DragonScreenHandler(int windowId, PlayerInventory inventory, SimpleInventory container, int id) {
        super(BookOfDragons.DRAGON_SCREEN_HANDLER_TYPE, windowId);
        this.dragonContainer = container;
        dragon = (AbstractRideableDragonEntity) inventory.player.world.getEntityById(id);

        container.onOpen(inventory.player);

        // Dragon Inventory
        this.addSlot(new Slot(container, 0, 8, 18) {
            public boolean mayPlace(ItemStack stack) {
                return stack.isOf(Items.SADDLE) && !this.hasStack() && dragon.canBeSaddled();
            }

            public boolean isActive() {
                return dragon.canBeSaddled();
            }
        });
        this.addSlot(new Slot(container, 1, 8, 36) {
            public boolean mayPlace(ItemStack p_39690_) {
                return false;
                //return dragon.isArmor(p_39690_);
            }

            public boolean isActive() {
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
                    this.addSlot(new Slot(container, 3 + l + k * dragon.getInventoryColumns(), 80 + l * 18, 18 + k * 18));
                }
            }
        }

        // Player Inventory
        for(int i1 = 0; i1 < 3; ++i1) {
            for(int k1 = 0; k1 < 9; ++k1) {
                this.addSlot(new Slot(inventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 102 + i1 * 18 + -18));
            }
        }

        // Player Hotbar
        for(int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(inventory, j1, 8 + j1 * 18, 142));
        }
    }

    private boolean hasChest(AbstractRideableDragonEntity p_150578_) {
        return p_150578_ != null && p_150578_.hasChest();
    }

    public ItemStack transferSlot(PlayerEntity player, int p_39666_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(p_39666_);
        if (slot.hasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            int i = this.dragonContainer.size();
            if (p_39666_ < i) {
                if (!this.insertItem(itemstack1, i, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.getSlot(1).canInsert(itemstack1) && !this.getSlot(1).hasStack()) {
                if (!this.insertItem(itemstack1, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.getSlot(0).canInsert(itemstack1)) {
                if (!this.insertItem(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
                dragon.setSaddled(getSlot(0).getStack().isOf(Items.SADDLE));
            }
            else if (i <= 2 || !this.insertItem(itemstack1, 2, i, false)) {
                int j = i + 27;
                int k = j + 9;
                if (p_39666_ >= j && p_39666_ < k) {
                    if (!this.insertItem(itemstack1, i, j, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (p_39666_ < j) {
                    if (!this.insertItem(itemstack1, j, k, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (!this.insertItem(itemstack1, j, j, false)) {
                    return ItemStack.EMPTY;
                }

                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return itemstack;
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
