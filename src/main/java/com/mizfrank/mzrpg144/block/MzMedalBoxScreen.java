package com.mizfrank.mzrpg144.block;

import com.mizfrank.mzrpg144.MzRPG;
import com.mizfrank.mzrpg144.StringCutter;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class MzMedalBoxScreen extends ContainerScreen<MzMedalBoxContainer> {

    private int pos_left;
    private int pos_top;
    private int win_width;
    private int win_height;

    private ResourceLocation GUI = new ResourceLocation(MzRPG.MOD_ID, "textures/gui/spec1_gui.png");
    //The parameters of GuiButton are(x, y, width, height, text, onButtonClick(Button tmpBtn));
    private Button btn1 = null;
    private Button btn2 = null;
    private Button btn3 = null;
    private String winTitle;
    private List<String> specDesc;
    private Button btnOK = null;

    public MzMedalBoxScreen(MzMedalBoxContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        winTitle = new TranslationTextComponent("screen.mzrpg144.mz_medal_box_text1").getUnformattedComponentText();
        specDesc = StringCutter.cut(
                new TranslationTextComponent("txt.spec_warrior_desc").getUnformattedComponentText(),
                25);
    }

    @Override
    protected void init() {
        super.init();
        win_width = 256;  // this.xSize = 176
        win_height = 166;  // this.ySize = 166
        pos_left = (this.width - win_width) / 2;
        pos_top = (this.height - win_height) / 2;

        btn1 = new Button(pos_left + 10,pos_top + 22,60,20,
                new TranslationTextComponent("txt.spec_warrior").getUnformattedComponentText()
                , (tmpBtn) -> {});
        this.addButton(btn1);

        btn2 = new Button(pos_left + 10,pos_top + 42,60,20,
                new TranslationTextComponent("txt.spec_archer").getUnformattedComponentText()
                , (tmpBtn) -> {});
        this.addButton(btn2);

        btn3 = new Button(pos_left + 10,pos_top + 62,60,20,
                new TranslationTextComponent("txt.spec_mage").getUnformattedComponentText()
                , (tmpBtn) -> {});
        this.addButton(btn3);

        btnOK = new Button(pos_left + 120,pos_top + 125,80,20,
                new TranslationTextComponent("txt.btnOK1").getUnformattedComponentText()
                , (tmpBtn) -> {});
        this.addButton(btnOK);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        //this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

        drawString(Minecraft.getInstance().fontRenderer,
                winTitle,-35, 5, 0xffffff);


//        GlStateManager.pushMatrix();
//        GlStateManager.scalef(0.8f, 0.8f, 0.8f);
        int lineTop = 22;
        for (String line : specDesc){
            Minecraft.getInstance().fontRenderer.drawString(line,
                    45, lineTop, 0x303030);
            lineTop += 12;
        }
//        GlStateManager.popMatrix();

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        this.blit(pos_left, pos_top, 0, 0, win_width, win_height);
    }





}
