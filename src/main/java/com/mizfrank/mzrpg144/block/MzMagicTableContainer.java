package com.mizfrank.mzrpg144.block;

import com.google.common.collect.Lists;
import com.mizfrank.mzrpg144.item.ItemCollection;
import com.mizfrank.mzrpg144.item.MzItemEnchant.MzSwordEnchant;
import net.minecraft.block.AnvilBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MzMagicTableContainer extends Container {

    private PlayerEntity playerEntity;
    private IItemHandler playerInventory;
    private World world;
    private BlockPos blockPos;

    private final IInventory outputSlot;
    private final IInventory inputSlot;
    private final IntReferenceHolder breakRateDesc;


    protected MzMagicTableContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(ContainerCollection.MZ_MAGIC_TABLE_CONTAINER.get(), windowId);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.world = world;
        this.blockPos = pos;
        outputSlot = new CraftResultInventory();
        inputSlot = new Inventory(2) {
            public void markDirty() {
                super.markDirty();
                onCraftMatrixChanged(this);
            }
        };
        breakRateDesc = IntReferenceHolder.single();
        this.addSlot(new Slot(inputSlot, 0, 27, 47));
        this.addSlot(new Slot(inputSlot, 1, 76, 47));
        this.addSlot(new Slot(outputSlot, 2, 134, 47) {
            public boolean isItemValid(ItemStack itemStack) {
                return false;
            }

            public boolean canTakeStack(PlayerEntity p_82869_1_) {
                return this.getHasStack();
            }

            public ItemStack onTake(PlayerEntity playerEntity1, ItemStack itemStack) {
                return itemStack;
            }
        });
        layoutPlayerInventorySlots(10, 70);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerEntity) {
        return isWithinUsableDistance(IWorldPosCallable.of(world, blockPos), playerEntity, BlockCollection.MZ_MAGIC_TABLE.get());
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        super.onCraftMatrixChanged(inventory);
        if (inventory == inputSlot) {
            updateEnchantOutput();
        }
    }

    public void updateEnchantOutput() {
        // update result slot
        ItemStack codeItem = inputSlot.getStackInSlot(0);
        ItemStack exeItem = inputSlot.getStackInSlot(1);
        if (codeItem.isEmpty() || exeItem.isEmpty()){
            outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
        }
        else {
            ItemStack tmpResult = getCompileResult(codeItem, exeItem);
            if (tmpResult == null){
                // TODO: 提示存在语法错误
                outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
            }
            else{
                outputSlot.setInventorySlotContents(0, tmpResult);
            }
            detectAndSendChanges();
        }

    }

    public ItemStack getCompileResult(ItemStack codeIn, ItemStack exeIn){
        if (!((codeIn.getItem() instanceof WritableBookItem || codeIn.getItem() instanceof WrittenBookItem ||
                codeIn.getItem() instanceof BookItem || codeIn.getItem() instanceof EnchantedBookItem) &&
                exeIn.getItem() == ItemCollection.MZ_MAGIC_BOOK.get())){
            return null;
        }
        CompoundNBT exeNbt = exeIn.getOrCreateTag();
        int hasCompiled = exeNbt.getInt("compiled");
        if (hasCompiled > 0) { return null; }

        CompoundNBT codeNbt = codeIn.getOrCreateTag();
        ListNBT codePages = codeNbt.getList("pages", 8);
        StringBuilder sb = new StringBuilder("");
        for(int i = 0; i < codePages.size(); ++i) {
            String tmpStr = codePages.getString(i);
            sb.append(tmpStr);
        }
        String[] lines = sb.toString().split("\n");

        List<String> processedLineList = new ArrayList<>();
        for(String line : lines){
            String tmpStr = line;
            // 空格，空行，大小写处理
            tmpStr = tmpStr.toLowerCase().trim();
            tmpStr = tmpStr.replace("("," ( ")
                    .replace(")"," ) ")
                    .replace(",", " , ");
            tmpStr = tmpStr.replaceAll("\\s+"," ");
            if (tmpStr.contains("'")){
                tmpStr = tmpStr.split("'")[0];
            }
            if (tmpStr.length() > 0){
                processedLineList.add(tmpStr);
            }
        }

        Stack<Integer> indentFlag = new Stack<>();
        for (String line : processedLineList){
            // 括号层级校验，函数名校验
            String[] segs = line.split(" ");
            if (segs.length > 0){
                String cmd = segs[0];
                if (cmd.equals("sub")){
                    indentFlag.push(1);
                }
                else if (cmd.equals("function")){
                    indentFlag.push(2);
                }
                else if (cmd.equals("if")){
                    indentFlag.push(3);
                }
                else if (cmd.equals("do")){
                    indentFlag.push(4);
                }
                else if (cmd.equals("loop")){
                    if (!indentFlag.empty()){
                        if (indentFlag.peek() == 4){
                            indentFlag.pop();
                        }
                    }
                }
                else if (cmd.equals("for")){
                    indentFlag.push(5);
                }
                else if (cmd.equals("next")){
                    if (!indentFlag.empty()){
                        if (indentFlag.peek() == 5){
                            indentFlag.pop();
                        }
                    }
                }
                else if (cmd.equals("structure")){
                    indentFlag.push(6);
                }
                else if (cmd.equals("end")){
                    if (segs.length > 1){
                        String cmd2 = segs[1];
                        if (!indentFlag.empty()){
                            if (cmd2.equals("sub")){
                                if (indentFlag.peek() == 1){
                                    indentFlag.pop();
                                }
                            }
                            else if (cmd2.equals("function")){
                                if (indentFlag.peek() == 2){
                                    indentFlag.pop();
                                }
                            }
                            else if (cmd2.equals("if")){
                                if (indentFlag.peek() == 3){
                                    indentFlag.pop();
                                }
                            }
                            else if (cmd2.equals("structure")){
                                if (indentFlag.peek() == 6){
                                    indentFlag.pop();
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!indentFlag.empty()){  // 校验不通过
            return null;
        }

        ItemStack result = new ItemStack(ItemCollection.MZ_MAGIC_BOOK_COMPILED.get());
        ListNBT linesNBT = new ListNBT();
        for (String line : processedLineList){
            linesNBT.add(new StringNBT(line));
        }
        result.setTagInfo("lines", linesNBT);
        result.getOrCreateTag().putInt("compiled", 1);
        return exeIn;
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
            this.clearContainer(playerIn, p_lambda$onContainerClosed$1_2_, inputSlot);
        });
    }


}

