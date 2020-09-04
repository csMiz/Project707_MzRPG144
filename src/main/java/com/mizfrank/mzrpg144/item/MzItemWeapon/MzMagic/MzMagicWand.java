package com.mizfrank.mzrpg144.item.MzItemWeapon.MzMagic;

import com.mizfrank.mzrpg144.MzRPG;
import com.mizfrank.mzrpg144.item.ItemCollection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class MzMagicWand extends TieredItem {

    protected MzMagicRuntime currentRt = null;

    public MzMagicWand(IItemTier material, Properties properties) {
        super(material, properties.group(MzRPG.MZ_ITEMGROUP).maxStackSize(1));
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int currentUseDur) {
        if (currentRt != null){
            if (!currentRt.isNormalEnd()){
                float penalty = currentRt.getAbortPenalty();
                // TODO: 减少使用者HP
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack itemStack) {
        return UseAction.NONE;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        ItemStack exeTarget = playerFindMagicBook(player, itemStack);

        currentRt = null;
        if (exeTarget != null){
            currentRt = new MzMagicRuntime();
            int result = currentRt.loadExe(exeTarget);
            if (result != 0){
                float penalty = currentRt.getAbortPenalty();
                // TODO: 减少使用者HP

                currentRt = null;
                return new ActionResult(ActionResultType.PASS, itemStack);
            }
            player.setActiveHand(hand);    // 这里导致移动速度变慢
            return new ActionResult(ActionResultType.SUCCESS, itemStack);
        }

        return new ActionResult(ActionResultType.PASS, itemStack);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (currentRt != null){
            int tick = stack.getUseDuration() - player.getItemInUseCount();
            if (tick % 4 == 0){  // 每 4 tick 执行一次
                int result = currentRt.runLine();
                if (result != 0){
                    float penalty = currentRt.getAbortPenalty();
                    // TODO: 减少使用者HP

                    currentRt = null;
                }
            }
        }
    }

    public ItemStack playerFindMagicBook(PlayerEntity player, ItemStack itemStack){
        for(int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack tmp = player.inventory.getStackInSlot(i);
            if (tmp.getItem() == ItemCollection.MZ_MAGIC_BOOK_COMPILED.get()) {
                return tmp;
            }
        }
        return null;
    }

}
