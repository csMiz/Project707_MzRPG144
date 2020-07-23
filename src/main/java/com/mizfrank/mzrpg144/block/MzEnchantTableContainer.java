package com.mizfrank.mzrpg144.block;


import com.mizfrank.mzrpg144.item.MzItemEnchant.MzSwordEnchant;
import com.mizfrank.mzrpg144.item.MzSword;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class MzEnchantTableContainer extends Container {

    private PlayerEntity playerEntity;
    private IItemHandler playerInventory;
    private World world;
    private BlockPos blockPos;

    private float breakRate;

    private final IInventory outputSlot;
    private final IInventory inputSlotEquip;
    private final IInventory inputSlotMat;
    private final IntReferenceHolder breakRateDesc;


    protected MzEnchantTableContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(ContainerCollection.MZ_MEDAL_BOX_CONTAINER.get(), windowId);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.world = world;
        this.blockPos = pos;
        outputSlot = new CraftResultInventory();
        inputSlotEquip = new Inventory(1) {
            public void markDirty() {
                super.markDirty();
                onCraftMatrixChanged(this);
            }
        };
        inputSlotMat = new Inventory(1) {
            public void markDirty() {
                super.markDirty();
                onCraftMatrixChanged(this);
            }
        };
        breakRateDesc = IntReferenceHolder.single();
        this.addSlot(new Slot(inputSlotEquip, 0, 27, 47));
        this.addSlot(new Slot(inputSlotMat, 1, 76, 47));
        this.addSlot(new Slot(this.outputSlot, 2, 134, 47) {
            public boolean isItemValid(ItemStack itemStack) {
                return false;
            }

            public boolean canTakeStack(PlayerEntity p_82869_1_) {
                return this.getHasStack();
            }

            public ItemStack onTake(PlayerEntity playerEntity1, ItemStack itemStack) {
                float breakRnd = playerEntity1.getRNG().nextFloat() * 100.0f;
                if (breakRnd < breakRate){
                    float decDur = itemStack.getOrCreateTag().getFloat("mz_dec_dur");  // default = 1.0
                    float newDecDur = decDur + (2.0f * playerEntity1.getRNG().nextFloat());  // -> +0.0 to +2.0
                    itemStack.setTagInfo("mz_dec_dur", new FloatNBT(newDecDur));
                }
                // clear slot 1
                inputSlotEquip.setInventorySlotContents(0, ItemStack.EMPTY);
                // reduce slot 2
                ItemStack tmpMat = inputSlotMat.getStackInSlot(0);
                if (!tmpMat.isEmpty() && tmpMat.getCount() > 1) {
                    tmpMat.shrink(1);
                    inputSlotMat.setInventorySlotContents(0, tmpMat);
                } else {
                    inputSlotMat.setInventorySlotContents(0, ItemStack.EMPTY);
                }
                // get slot 3
                breakRate = 0.0f;
                IWorldPosCallable.of(world, blockPos).consume((p_lambda$onTake$0_2_, p_lambda$onTake$0_3_) -> {
                    // call no extra events
                });
                return itemStack;
            }
        });
        layoutPlayerInventorySlots(10, 70);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerEntity) {
        return isWithinUsableDistance(IWorldPosCallable.of(world, blockPos), playerEntity, BlockCollection.MZ_MEDAL_BOX.get());
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        super.onCraftMatrixChanged(inventory);
        if (inventory == inputSlotEquip || inventory == inputSlotMat) {
            updateEnchantOutput();
        }
    }

    public void updateEnchantOutput() {
        // update result slot
        ItemStack equip = inputSlotEquip.getStackInSlot(0);
        ItemStack mat = inputSlotMat.getStackInSlot(0);
        if (equip.isEmpty() || mat.isEmpty()){
            outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
        }
        else{
            ItemStack tmpResult = getEnchantResultSlot(equip, mat);
            outputSlot.setInventorySlotContents(0, tmpResult);
        }

        detectAndSendChanges();
    }

    public ItemStack getEnchantResultSlot(ItemStack equip, ItemStack mat){
        CompoundNBT nbt = equip.getTag();
        int equipType = -1;
        if (nbt != null){
            if (nbt.contains("mz_type")){
                equipType = nbt.getInt("mz_type");
            }
        }
        ItemStack result = ItemStack.EMPTY;
        if (equipType == 1){
            MzSwordEnchant srcEnchant = new MzSwordEnchant(nbt.getIntArray("mz_enchant"));
            srcEnchant.prepare(mat.getItem());
            boolean passElement = srcEnchant.passElement;
            if (passElement){
                // set fail rate
                breakRate = srcEnchant.breakRate;
                // and
                result = equip.copy();
                result.setTagInfo("mz_enchant", srcEnchant.toNBTIntArray());
            }
        }
        return result;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);
        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }
    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }
    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }


    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 2) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 0 && index != 1) {
                if (index >= 3 && index < 39 && !this.mergeItemStack(itemstack1, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        IWorldPosCallable.of(world, blockPos).consume((p_lambda$onContainerClosed$1_2_, p_lambda$onContainerClosed$1_3_) -> {
            this.clearContainer(playerIn, p_lambda$onContainerClosed$1_2_, inputSlotEquip);
            this.clearContainer(playerIn, p_lambda$onContainerClosed$1_2_, inputSlotMat);
        });
    }


}
