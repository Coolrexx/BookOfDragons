package net.arathain.bookofdragons.common.entity.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.arathain.bookofdragons.BODComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class DragonRiderComponent implements AutoSyncedComponent {
    private final PlayerEntity entity;
    private boolean pressingUp = false;
    private boolean pressingDown = false;

    public DragonRiderComponent(PlayerEntity entity) {
        this.entity = entity;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        setPressingUp(tag.getBoolean("PressingUp"));
        setPressingDown(tag.getBoolean("PressingDown"));
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("PressingUp", isPressingUp());
        tag.putBoolean("PressingDown", isPressingDown());
    }

    public boolean isPressingUp() {
        return pressingUp;
    }
    public boolean isPressingDown() {
        return pressingDown;
    }

    public void setPressingUp(boolean pressingUp) {
        this.pressingUp = pressingUp;
        BODComponents.DRAGON_RIDER_COMPONENT.sync(entity);
    }

    public void setPressingDown(boolean pressingDown) {
        this.pressingDown = pressingDown;
        BODComponents.DRAGON_RIDER_COMPONENT.sync(entity);
    }
}
