package coda.bookofdragons.client.screen;

import coda.bookofdragons.BookOfDragons;
import coda.bookofdragons.common.entities.util.AbstractRideableDragonEntity;
import coda.bookofdragons.common.menu.DragonInventoryMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DragonInventoryScreen extends AbstractContainerScreen<DragonInventoryMenu> {
   private static final ResourceLocation INVENTORY_LOCATION = new ResourceLocation(BookOfDragons.MOD_ID, "textures/gui/container/dragon_inv.png");
   private final AbstractRideableDragonEntity dragon;
   private float xMouse;
   private float yMouse;

   public DragonInventoryScreen(DragonInventoryMenu menu, Inventory inv, Component component) {
      super(menu, inv, component);
      this.dragon = menu.getDragon();
      this.passEvents = false;
   }

   protected void renderBg(PoseStack p_98821_, float p_98822_, int p_98823_, int p_98824_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, INVENTORY_LOCATION);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(p_98821_, i, j, 0, 0, this.imageWidth, this.imageHeight);
      if (this.dragon != null) {
         AbstractRideableDragonEntity dragon = this.dragon;
         if (dragon.hasChest()) {
            this.blit(p_98821_, i + 79, j + 17, 0, this.imageHeight, dragon.getInventoryColumns() * 18, 54);
         }
      }

      if (this.dragon.isSaddleable()) {
         this.blit(p_98821_, i + 7, j + 35 - 18, 18, this.imageHeight + 54, 18, 18);
      }

/*      if (this.dragon.canWearArmor()) {
         if (this.dragon instanceof Llama) {
            this.blit(p_98821_, i + 7, j + 35, 36, this.imageHeight + 54, 18, 18);
         } else {
            this.blit(p_98821_, i + 7, j + 35, 0, this.imageHeight + 54, 18, 18);
         }
      }*/
      this.blit(p_98821_, i + 7, j + 35, 36, this.imageHeight + 54, 18, 18);

      InventoryScreen.renderEntityInInventory(i + 51, j + 60, 17, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.dragon);
   }

   public void render(PoseStack p_98826_, int p_98827_, int p_98828_, float p_98829_) {
      this.renderBackground(p_98826_);
      this.xMouse = (float)p_98827_;
      this.yMouse = (float)p_98828_;
      super.render(p_98826_, p_98827_, p_98828_, p_98829_);
      this.renderTooltip(p_98826_, p_98827_, p_98828_);
   }
}