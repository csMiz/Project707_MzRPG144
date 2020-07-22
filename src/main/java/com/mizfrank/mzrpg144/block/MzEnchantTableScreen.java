package com.mizfrank.mzrpg144.block;

import com.mizfrank.mzrpg144.MzRPG;
import com.mizfrank.mzrpg144.Networking;
import com.mizfrank.mzrpg144.StringCutter;
import com.mizfrank.mzrpg144.entity.IMzSpecialty;
import com.mizfrank.mzrpg144.entity.MzSpecialtyProvider;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MzEnchantTableScreen extends ContainerScreen<MzEnchantTableContainer> implements IContainerListener {

    private int pos_left;
    private int pos_top;
    private int win_width;
    private int win_height;

    private ResourceLocation GUI = new ResourceLocation(MzRPG.MOD_ID, "textures/gui/enchant_gui.png");
    private FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
    private String winTitle;
    private String enchantDesc;
    private String successRateDesc;

    private String txtsrc_winTitle;

    private PlayerEntity currentPlayer;
    private IMzSpecialty playerSpec;
    private ItemStack targetEquip;
    private Item targetMatType;

    public MzEnchantTableScreen(MzEnchantTableContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        loadTextResources();
        loadPlayerSpec(inv.player);

    }

    private void loadTextResources() {
        txtsrc_winTitle = new TranslationTextComponent("screen.mzrpg144.mz_enchant_table_text1").getUnformattedComponentText();

    }

    private void loadPlayerSpec(PlayerEntity playerEntity) {
        currentPlayer = playerEntity;
        playerSpec = currentPlayer.getCapability(MzSpecialtyProvider.MZ_SPEC_CAP, null).orElse(null);

    }


    /**
     * Resize will also call init()
     */
    @Override
    protected void init() {
        super.init();
        win_width = this.xSize;  // this.xSize = 176
        win_height = this.ySize;  // this.ySize = 166
        pos_left = (this.width - win_width) / 2;
        pos_top = (this.height - win_height) / 2;

    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawStringWithShadow(winTitle, 5, 5, 0xffffff);
        // show predicted result

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        TextureManager textureManager = this.minecraft.getTextureManager();
        textureManager.bindTexture(GUI);
        this.blit(pos_left, pos_top, 0, 0, win_width, win_height);
    }

    @Override
    public void sendAllContents(Container container, NonNullList<ItemStack> nonNullList) {
        targetEquip = container.getSlot(0).getStack();
        targetMatType = container.getSlot(1).getStack().getItem();
        // calculate result
        // enchantDesc =
    }

    @Override
    public void sendSlotContents(Container container, int i, ItemStack itemStack) {
    }

    @Override
    public void sendWindowProperty(Container container, int i, int i1) {
    }
}
