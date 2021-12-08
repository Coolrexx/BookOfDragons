package net.arathain.bookofdragons.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.arathain.bookofdragons.BookOfDragons;
import net.arathain.bookofdragons.common.entity.util.AbstractRideableDragonEntity;
import net.arathain.bookofdragons.common.menu.DragonScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DragonInventoryScreen extends HandledScreen<DragonScreenHandler> {
    private static final Identifier INVENTORY_LOCATION = new Identifier(BookOfDragons.MOD_ID, "textures/gui/container/dragon_inv.png");
    private final AbstractRideableDragonEntity dragon;
    private float xMouse;
    private float yMouse;

    public DragonInventoryScreen(DragonScreenHandler menu, PlayerInventory inv, Text title) {
        super(menu, inv, title);
        this.dragon = menu.getDragon();
        this.passEvents = false;
    }

    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, INVENTORY_LOCATION);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if (this.dragon != null) {
            AbstractRideableDragonEntity dragon = this.dragon;
            if (dragon.hasChest()) {
                this.drawTexture(matrices, i + 79, j + 17, 0, this.backgroundHeight, dragon.getInventoryColumns() * 18, 54);
            }
        }

        assert this.dragon != null;
        if (this.dragon.canBeSaddled()) {
            this.drawTexture(matrices, i + 7, j + 35 - 18, 18, this.backgroundHeight + 54, 18, 18);
        }

/*      if (this.dragon.canWearArmor()) {
         if (this.dragon instanceof Llama) {
            this.blit(matrices, i + 7, j + 35, 36, this.imageHeight + 54, 18, 18);
         } else {
            this.blit(matrices, i + 7, j + 35, 0, this.imageHeight + 54, 18, 18);
         }
      }*/
        this.drawTexture(matrices, i + 7, j + 35, 36, this.backgroundHeight + 54, 18, 18);

        InventoryScreen.drawEntity(i + 51, j + 60, 17, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.dragon);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.xMouse = mouseX;
        this.yMouse = mouseY;
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
