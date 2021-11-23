package coda.bookofdragons.common.menu;

import coda.bookofdragons.common.entities.util.AbstractRideableDragonEntity;
import coda.bookofdragons.init.BODContainers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DragonInventoryMenu extends AbstractContainerMenu {
   private final Container dragonContainer;
   private final AbstractRideableDragonEntity dragon;

   public DragonInventoryMenu(int windowId, Inventory inventory, FriendlyByteBuf buf) {
      super(BODContainers.DRAGON_INV.get(), windowId);
      this.dragonContainer = inventory;
      this.dragon = getDragon();
      inventory.startOpen(inventory.player);
      this.addSlot(new Slot(inventory, 0, 8, 18) {
         public boolean mayPlace(ItemStack stack) {
            return stack.is(Items.SADDLE) && !this.hasItem() && dragon.isSaddleable();
         }

         public boolean isActive() {
            return dragon.isSaddleable();
         }
      });
      this.addSlot(new Slot(inventory, 1, 8, 36) {
         public boolean mayPlace(ItemStack stack) {
            return false;
         }

         public boolean isActive() {
            return false;
         }

         public int getMaxStackSize() {
            return 1;
         }
      });
      if (this.hasChest(dragon)) {
         for(int k = 0; k < 3; ++k) {
            for(int l = 0; l < dragon.getInventoryColumns(); ++l) {
               this.addSlot(new Slot(inventory, 2 + l + k * dragon.getInventoryColumns(), 80 + l * 18, 18 + k * 18));
            }
         }
      }

      for(int i1 = 0; i1 < 3; ++i1) {
         for(int k1 = 0; k1 < 9; ++k1) {
            this.addSlot(new Slot(inventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 102 + i1 * 18 + -18));
         }
      }

      for(int j1 = 0; j1 < 9; ++j1) {
         this.addSlot(new Slot(inventory, j1, 8 + j1 * 18, 142));
      }

   }

   public boolean stillValid(Player player) {
      return !this.dragon.hasInventoryChanged(this.dragonContainer) && this.dragonContainer.stillValid(player) && this.dragon.isAlive() && this.dragon.distanceTo(player) < 8.0F;
   }

   private boolean hasChest(AbstractRideableDragonEntity p_150578_) {
      return p_150578_ != null && p_150578_.hasChest();
   }

   public ItemStack quickMoveStack(Player player, int p_39666_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_39666_);
      if (slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         int i = this.dragonContainer.getContainerSize();
         if (p_39666_ < i) {
            if (!this.moveItemStackTo(itemstack1, i, this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (this.getSlot(1).mayPlace(itemstack1) && !this.getSlot(1).hasItem()) {
            if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.getSlot(0).mayPlace(itemstack1)) {
            if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (i <= 2 || !this.moveItemStackTo(itemstack1, 2, i, false)) {
            int j = i + 27;
            int k = j + 9;
            if (p_39666_ >= j && p_39666_ < k) {
               if (!this.moveItemStackTo(itemstack1, i, j, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_39666_ >= i && p_39666_ < j) {
               if (!this.moveItemStackTo(itemstack1, j, k, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo(itemstack1, j, j, false)) {
               return ItemStack.EMPTY;
            }

            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }
      }

      return itemstack;
   }

   public void removed(Player player) {
      super.removed(player);
      this.dragonContainer.stopOpen(player);
   }

   public AbstractRideableDragonEntity getDragon() {
      return dragon;
   }
}