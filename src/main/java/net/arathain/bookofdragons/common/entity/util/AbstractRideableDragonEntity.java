package net.arathain.bookofdragons.common.entity.util;

import net.arathain.bookofdragons.BookOfDragonsClient;
import net.arathain.bookofdragons.common.entity.goal.FollowDriverGoal;
import net.arathain.bookofdragons.common.menu.DragonScreenHandler;
import net.arathain.bookofdragons.mixin.LivingEntityAccessor;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class AbstractRideableDragonEntity extends AbstractFlyingDragonEntity implements Saddleable, InventoryChangedListener {
    private static final TrackedData<Boolean> CHESTED = DataTracker.registerData(AbstractRideableDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SADDLED = DataTracker.registerData(AbstractRideableDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public Entity previousDriver = null;
    public SimpleInventory inventory;

    public AbstractRideableDragonEntity(EntityType<? extends AbstractRideableDragonEntity> type, World world) {
        super(type, world);
        this.moveControl = new FlightMoveControl(this, 20, false);
        this.stepHeight = 1.0F;
        this.createInventory();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SADDLED, false);
        this.dataTracker.startTracking(CHESTED, false);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(3, new FollowDriverGoal(this));
    }

    @Override
    public Ingredient getIngredient() {
        return null;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }


    protected void removePassenger(Entity passenger) {
        if (getControllingPassenger() == passenger) {
            this.previousDriver = passenger;
        }
        super.removePassenger(passenger);
    }

    @Nullable
    public Entity getControllingPassenger() {
        return this.getFirstPassenger();
    }

    @Override
    public boolean canBeControlledByRider() {
        return this.getControllingPassenger() instanceof LivingEntity;
    }

    @Override
    public void travel(Vec3d travelVector) {
        boolean flying = this.isInAir();
        float speed = (float) this.getAttributeValue(flying ? EntityAttributes.GENERIC_FLYING_SPEED : EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (!this.hasPassengers() && !this.canBeControlledByRider() && !this.isSaddled()) {
            this.airStrafingSpeed = 0.02f;
            super.travel(travelVector);
            return;
        }
            LivingEntity passenger = (LivingEntity) this.getControllingPassenger();
            if (passenger != null) {
                this.headYaw = passenger.headYaw;
                this.serverHeadYaw = this.headYaw;
                this.serverYaw = this.headYaw;
                this.serverPitch = passenger.getPitch() * 0.5F;
                this.setPitch((float) this.serverPitch);
                this.setYaw((float) this.headYaw);
                this.setRotation(this.getYaw(), this.getPitch());
                this.bodyYaw = this.headYaw;

                //if (!flying && passenger.jumping) this.jump();

                if (this.getControllingPassenger() != null) {
                    travelVector = new Vec3d(passenger.sidewaysSpeed * 0.5, BookOfDragonsClient.getFlightDelta(), passenger.forwardSpeed * 0.5);
                    this.setMovementSpeed(speed);
                    this.stepBobbingAmount = 0;
                } else if (passenger instanceof PlayerEntity) {
                    this.updateLimbs(this, false);
                    this.setVelocity(Vec3d.ZERO);
                    return;
                }
            }
        if (flying) {
            //this.moveRelative(speed, travelVector);
            this.move(MovementType.SELF, getVelocity());
            this.setVelocity(getVelocity().multiply(0.91f));
            this.updateLimbs(this, false);
        }
        else {
            super.travel(travelVector);
        }
    }

    @Override
    public boolean canBeSaddled() {
        return true;
    }
    @Override
    public void saddle(@Nullable SoundCategory sound) {

        this.inventory.addStack(new ItemStack(Items.SADDLE));
        if (sound != null) {
            this.world.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_HORSE_SADDLE, sound, 0.5F, 1.0F);
        }
    }
    public boolean hasChest() {
        return this.dataTracker.get(CHESTED);
    }

    public void setChest(boolean chested) {
        this.dataTracker.set(CHESTED, chested);
    }

    @Override
    public boolean isSaddled() {
        return this.dataTracker.get(SADDLED);
    }

    public void setSaddled(boolean saddled) {
        this.dataTracker.set(SADDLED, saddled);
    }

    protected void dropInventory() {
        super.dropInventory();
        if (this.hasChest()) {
            if (!this.world.isClient) {
                this.dropStack(Blocks.CHEST.asItem().getDefaultStack());
            }

            this.setChest(false);
        }

        if (this.isSaddled()) {
            if (!this.world.isClient) {
                this.dropStack(Items.SADDLE.getDefaultStack());
            }

            this.setSaddled(false);
        }

    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putBoolean("ChestedDragon", this.hasChest());
        if (this.hasChest()) {
            NbtList listtag = new NbtList();

            for(int i = 2; i < this.inventory.size(); ++i) {
                ItemStack itemstack = this.inventory.getStack(i);
                if (!itemstack.isEmpty()) {
                    NbtCompound compoundtag = new NbtCompound();
                    compoundtag.putByte("Slot", (byte)i);
                    itemstack.writeNbt(compoundtag);
                    listtag.add(compoundtag);
                }
            }

            nbt.put("Items", listtag);
        }
        if (!this.inventory.getStack(0).isEmpty()) {
            nbt.put("SaddleItem", this.inventory.getStack(0).writeNbt(new NbtCompound()));
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.createInventory();

        this.setChest(nbt.getBoolean("ChestedDragon"));
        this.createInventory();
        if (this.hasChest()) {
            NbtList listtag = nbt.getList("Items", 10);

            for(int i = 0; i < listtag.size(); ++i) {
                NbtCompound compoundtag = listtag.getCompound(i);
                int j = compoundtag.getByte("Slot") & 255;
                if (j >= 2 && j < this.inventory.size()) {
                    this.inventory.setStack(j, ItemStack.fromNbt(compoundtag));
                }
            }
        }

        if (nbt.contains("SaddleItem", 10)) {
            ItemStack itemstack = ItemStack.fromNbt(nbt.getCompound("SaddleItem"));
            if (itemstack.isOf(Items.SADDLE)) {
                this.inventory.setStack(0, itemstack);
            }
        }

        this.updateContainerEquipment();
    }

    public StackReference getStackReference(int mappedIndex) {
        return mappedIndex == 499 ? new StackReference() {
            public ItemStack get() {
                return AbstractRideableDragonEntity.this.hasChest() ? new ItemStack(Items.CHEST) : ItemStack.EMPTY;
            }

            public boolean set(ItemStack stack) {
                if (stack.isEmpty()) {
                    if (AbstractRideableDragonEntity.this.hasChest()) {
                        AbstractRideableDragonEntity.this.setChest(false);
                        AbstractRideableDragonEntity.this.createInventory();
                    }

                    return true;
                } else if (stack.isOf(Items.CHEST)) {
                    if (!AbstractRideableDragonEntity.this.hasChest()) {
                        AbstractRideableDragonEntity.this.setChest(true);
                        AbstractRideableDragonEntity.this.createInventory();
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } : super.getStackReference(mappedIndex);
    }

    protected void createInventory() {
        SimpleInventory simplecontainer = this.inventory;
        this.inventory = new SimpleInventory(this.getInventorySize());
        if (simplecontainer != null) {
            simplecontainer.removeListener(this);
            int i = Math.min(simplecontainer.size(), this.inventory.size());

            for(int j = 0; j < i; ++j) {
                ItemStack itemstack = simplecontainer.getStack(j);
                if (!itemstack.isEmpty()) {
                    this.inventory.setStack(j, itemstack.copy());
                }
            }
        }

        this.inventory.addListener(this);
        this.updateContainerEquipment();
    }

    public int getInventorySize() {
        return this.hasChest() ? 17 : 2;
    }

    protected void updateContainerEquipment() {
        if (!this.world.isClient()) {
            this.setSaddled(this.inventory.getStack(0).isOf(Items.SADDLE));
        }
    }


    public int getInventoryColumns() {
        return 5;
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);
        if (!this.isBaby()) {
            if (this.isTamed() && player.shouldCancelInteraction()) {
                this.openInventory(player);
                return ActionResult.success(this.world.isClient());
            }
            if (this.hasPassengers()) {
                return super.interactMob(player, hand);
            }
        }

        if (!itemstack.isEmpty()) {
            if (!this.hasChest() && this.isSaddled() && itemstack.isOf(Blocks.CHEST.asItem())) {
                this.setChest(true);
                this.playAddChestSound();
                if (!player.getAbilities().creativeMode) {
                    itemstack.decrement(1);
                }

                return ActionResult.success(this.world.isClient());
            }
            if (!this.isSaddled() && itemstack.isOf(Items.SADDLE)) {
                this.inventory.setStack(0, new ItemStack(Items.SADDLE));
                this.setSaddled(true);
            }
        }
        else if (this.isSaddled() && !isBaby() && itemstack.isEmpty()) {
            player.startRiding(this);
            this.navigation.stop();
        }

        return super.interactMob(player, hand);
    }

    public static Vec3d getYawVec(float yaw, double xOffset, double zOffset) {
        return new Vec3d(xOffset, 0, zOffset).rotateY(-yaw * ((float) Math.PI / 180f));
    }

    // TODO - coda - split this into individual dragon classes (maybe use a helper methods?)
    @Override
    public void updatePassengerPosition(Entity passenger) {
        Vec3d pos = getYawVec(bodyYaw, 0.0F, -0.35F).add(getX(), getY() + 1.25F, getZ());
        passenger.setPos(pos.x, pos.y, pos.z);
    }

    protected void playAddChestSound() {
        this.playSound(SoundEvents.ENTITY_DONKEY_CHEST, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        boolean flag = this.isSaddled();
        this.updateContainerEquipment();
        if (this.getRandom().nextInt(40) > 38 && !flag && this.isSaddled()) {
            this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 0.5F, 1.0F);
        }
    }

    public void openInventory(PlayerEntity player) {
        if (!this.world.isClient() && (!this.hasPassengers() || this.hasPassenger(player)) && this.isTamed()) {
            player.openHandledScreen(new DragonScreenHandlerFactory());
        }
    }

    private class DragonScreenHandlerFactory implements ExtendedScreenHandlerFactory {
        private AbstractRideableDragonEntity dragon() {
            return AbstractRideableDragonEntity.this;
        }

        @Override
        public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
            buf.writeVarInt(this.dragon().getId());
        }

        @Override
        public Text getDisplayName() {
            return this.dragon().getDisplayName();
        }

        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
            var dragonInv = this.dragon().inventory;
            return new DragonScreenHandler(syncId, inv, dragonInv, this.dragon().getId());
        }
    }
}
