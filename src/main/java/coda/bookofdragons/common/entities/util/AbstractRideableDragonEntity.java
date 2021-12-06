package coda.bookofdragons.common.entities.util;

import coda.bookofdragons.client.ClientEvents;
import coda.bookofdragons.common.entities.util.goal.FollowDriverGoal;
import coda.bookofdragons.common.menu.DragonInventoryMenu;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public abstract class AbstractRideableDragonEntity extends AbstractFlyingDragonEntity implements Saddleable, ContainerListener, MenuProvider {
    private static final EntityDataAccessor<Boolean> CHESTED = SynchedEntityData.defineId(AbstractRideableDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(AbstractRideableDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public Entity previousDriver = null;
    public SimpleContainer inventory;

    public AbstractRideableDragonEntity(EntityType<? extends AbstractRideableDragonEntity> type, Level world) {
        super(type, world);
        this.moveControl = new FlyingMoveControl(this, 20, false);
        this.maxUpStep = 1.0F;
        this.createInventory();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new FollowDriverGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHESTED, false);
        this.entityData.define(SADDLED  , false);
    }

    protected void removePassenger(Entity passenger) {
        if (getControllingPassenger() == passenger) {
            this.previousDriver = passenger;
        }
        super.removePassenger(passenger);
    }

    @Nullable
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    @Override
    public boolean canBeControlledByRider() {
        return this.getControllingPassenger() instanceof LivingEntity;
    }

    @Override
    public void travel(Vec3 travelVector) {
        boolean flying = this.isFlying();
        float speed = (float) this.getAttributeValue(flying ? Attributes.FLYING_SPEED : Attributes.MOVEMENT_SPEED);

        if (this.canBeControlledByRider()) {
            LivingEntity passenger = (LivingEntity) this.getControllingPassenger();
            if (passenger != null) {

                this.yRot = passenger.yRot;
                this.yRotO = this.yRot;
                this.xRot = passenger.xRot * 0.5F;
                this.setRot(this.yRot, this.xRot);
                this.yBodyRot = this.yRot;
                this.yHeadRot = this.yRot;

                yRot = Mth.rotateIfNecessary(yHeadRot, yRot, isFlying() ? 5 : 7);

                if (!flying && passenger.jumping) jumpFromGround();

                if (this.isControlledByLocalInstance()) {
                    travelVector = new Vec3(passenger.xxa * 0.25, ClientEvents.getFlightDelta(), passenger.zza * 0.25);
                    this.setSpeed(speed);
                    this.lerpSteps = 0;
                } else if (passenger instanceof Player) {
                    this.calculateEntityAnimation(this, flying);
                    this.setDeltaMovement(Vec3.ZERO);
                    if (!level.isClientSide && !isOnGround())
                        ((ServerPlayer) passenger).connection.aboveGroundVehicleTickCount = 0;
                    return;
                }
            }
        }
        if (flying) {
            this.moveRelative(speed, travelVector);
            this.move(MoverType.SELF, getDeltaMovement());
            this.setDeltaMovement(getDeltaMovement().scale(0.91f));
            this.calculateEntityAnimation(this, true);
        }
        else {
            super.travel(travelVector);
        }
    }

    public boolean hasChest() {
        return this.entityData.get(CHESTED);
    }

    public void setChest(boolean p_30505_) {
        this.entityData.set(CHESTED, p_30505_);
    }

    @Override
    public boolean isSaddleable() {
        return true;
    }

    @Override
    public void equipSaddle(@Nullable SoundSource p_21748_) {
        this.inventory.setItem(0, new ItemStack(Items.SADDLE));
        if (p_21748_ != null) {
            this.level.playSound(null, this, SoundEvents.HORSE_SADDLE, p_21748_, 0.5F, 1.0F);
        }
    }

    @Override
    public boolean isSaddled() {
        return this.entityData.get(SADDLED);
    }

    public void setSaddled(boolean p_30505_) {
        this.entityData.set(SADDLED, p_30505_);
    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (this.hasChest()) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(Blocks.CHEST);
            }

            this.setChest(false);
        }

        if (this.isSaddled()) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(Items.SADDLE);
            }

            this.setSaddled(false);
        }

    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        nbt.putBoolean("ChestedDrgaon", this.hasChest());
        if (this.hasChest()) {
            ListTag listtag = new ListTag();

            for(int i = 2; i < this.inventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);
                if (!itemstack.isEmpty()) {
                    CompoundTag compoundtag = new CompoundTag();
                    compoundtag.putByte("Slot", (byte)i);
                    itemstack.save(compoundtag);
                    listtag.add(compoundtag);
                }
            }

            nbt.put("Items", listtag);
        }

        if (!this.inventory.getItem(0).isEmpty()) {
            nbt.put("SaddleItem", this.inventory.getItem(0).save(new CompoundTag()));
        }
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.createInventory();

        this.setChest(nbt.getBoolean("ChestedHorse"));
        this.createInventory();
        if (this.hasChest()) {
            ListTag listtag = nbt.getList("Items", 10);

            for(int i = 0; i < listtag.size(); ++i) {
                CompoundTag compoundtag = listtag.getCompound(i);
                int j = compoundtag.getByte("Slot") & 255;
                if (j >= 2 && j < this.inventory.getContainerSize()) {
                    this.inventory.setItem(j, ItemStack.of(compoundtag));
                }
            }
        }

        if (nbt.contains("SaddleItem", 10)) {
            ItemStack itemstack = ItemStack.of(nbt.getCompound("SaddleItem"));
            if (itemstack.is(Items.SADDLE)) {
                this.inventory.setItem(0, itemstack);
            }
        }

        this.updateContainerEquipment();
    }

    public SlotAccess getSlot(int p_149479_) {
        return p_149479_ == 499 ? new SlotAccess() {
            public ItemStack get() {
                return AbstractRideableDragonEntity.this.hasChest() ? new ItemStack(Items.CHEST) : ItemStack.EMPTY;
            }

            public boolean set(ItemStack p_149485_) {
                if (p_149485_.isEmpty()) {
                    if (AbstractRideableDragonEntity.this.hasChest()) {
                        AbstractRideableDragonEntity.this.setChest(false);
                        AbstractRideableDragonEntity.this.createInventory();
                    }

                    return true;
                } else if (p_149485_.is(Items.CHEST)) {
                    if (!AbstractRideableDragonEntity.this.hasChest()) {
                        AbstractRideableDragonEntity.this.setChest(true);
                        AbstractRideableDragonEntity.this.createInventory();
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } : super.getSlot(p_149479_);
    }

    protected void createInventory() {
        SimpleContainer simplecontainer = this.inventory;
        this.inventory = new SimpleContainer(this.getInventorySize());
        if (simplecontainer != null) {
            simplecontainer.removeListener(this);
            int i = Math.min(simplecontainer.getContainerSize(), this.inventory.getContainerSize());

            for(int j = 0; j < i; ++j) {
                ItemStack itemstack = simplecontainer.getItem(j);
                if (!itemstack.isEmpty()) {
                    this.inventory.setItem(j, itemstack.copy());
                }
            }
        }

        this.inventory.addListener(this);
        this.updateContainerEquipment();
        this.itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this.inventory));
    }

    protected int getInventorySize() {
        return this.hasChest() ? 17 : 2;
    }

    protected void updateContainerEquipment() {
        if (!this.level.isClientSide) {
            this.setSaddled(this.inventory.getItem(0).is(Items.SADDLE));
        }
    }

    private LazyOptional<?> itemHandler = null;

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (this.isAlive() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && itemHandler != null)
            return itemHandler.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (itemHandler != null) {
            LazyOptional<?> oldHandler = itemHandler;
            itemHandler = null;
            oldHandler.invalidate();
        }
    }

    public int getInventoryColumns() {
        return 5;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!this.isBaby()) {
            if (/*this.isTamed() && */player.isSecondaryUseActive() && inventory != null) {
                player.openMenu(this);
                inventory = new SimpleContainer(3);
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }

            if (this.isVehicle()) {
                return super.mobInteract(player, hand);
            }
        }

        if (!itemstack.isEmpty()) {
            if (!this.hasChest() && this.isSaddled() && itemstack.is(Blocks.CHEST.asItem())) {
                this.setChest(true);
                this.playChestEquipsSound();
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
            if (!this.isSaddled() && itemstack.is(Items.SADDLE)) {
                this.inventory.setItem(0, new ItemStack(Items.SADDLE));
                this.setSaddled(true);
            }
        }
        else if (this.isSaddled() && !isBaby() && itemstack.isEmpty()) {
            player.startRiding(this);
            this.navigation.stop();
        }

        return super.mobInteract(player, hand);
    }

    public static Vec3 getYawVec(float yaw, double xOffset, double zOffset) {
        return new Vec3(xOffset, 0, zOffset).yRot(-yaw * ((float) Math.PI / 180f));
    }

    protected void playChestEquipsSound() {
        this.playSound(SoundEvents.DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
        if (inventory == null) {
            return null;
        }
        return new DragonInventoryMenu(p_createMenu_1_, p_createMenu_2_, this.inventory, getId());
    }

    @Override
    public void containerChanged(Container p_18983_) {
        boolean flag = this.isSaddled();
        this.updateContainerEquipment();
        if (this.tickCount > 20 && !flag && this.isSaddled()) {
            this.playSound(SoundEvents.HORSE_SADDLE, 0.5F, 1.0F);
        }
    }
}
