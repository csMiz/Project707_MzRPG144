package com.mizfrank.mzrpg144.block;

import com.mizfrank.mzrpg144.MzRPG;
import com.mizfrank.mzrpg144.Networking;
import com.mizfrank.mzrpg144.StringCutter;
import com.mizfrank.mzrpg144.entity.IMzSpecialty;
import com.mizfrank.mzrpg144.entity.MzSpecialtyProvider;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class MzMedalBoxScreen extends ContainerScreen<MzMedalBoxContainer> {

    private int pos_left;
    private int pos_top;
    private int win_width;
    private int win_height;

    private ResourceLocation GUI = new ResourceLocation(MzRPG.MOD_ID, "textures/gui/spec1_gui.png");
    private FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
    //The parameters of GuiButton are(x, y, width, height, text, onButtonClick(Button tmpBtn));
    private Button btn1 = null;
    private Button btn2 = null;
    private Button btn3 = null;
    private String winTitle;
    private List<String> specDesc;
    private Button btnOK = null;
    private int listSelectedIndex = 0;

    private String txtsrc_winTitle;
    private String txtsrc_btnWarrior;
    private String txtsrc_btnArcher;
    private String txtsrc_btnMage;
    private List<String> txtsrc_descWarrior;
    private List<String> txtsrc_descArcher;
    private List<String> txtsrc_descMage;

    private PlayerEntity currentPlayer;
    private IMzSpecialty playerSpec;
    private int playerSpecMajor;

//    public static void open() {
//        Minecraft.getInstance().displayGuiScreen(new MzMedalBoxScreen());
//    }

    public MzMedalBoxScreen(MzMedalBoxContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        loadTextResources();
        loadPlayerSpec(inv.player);

    }

    private void loadTextResources(){
        txtsrc_winTitle = new TranslationTextComponent("screen.mzrpg144.mz_medal_box_text1").getUnformattedComponentText();
        txtsrc_btnWarrior = new TranslationTextComponent("txt.spec_warrior").getUnformattedComponentText();
        txtsrc_btnArcher = new TranslationTextComponent("txt.spec_archer").getUnformattedComponentText();
        txtsrc_btnMage = new TranslationTextComponent("txt.spec_mage").getUnformattedComponentText();
        txtsrc_descWarrior = StringCutter.cut(
                new TranslationTextComponent("txt.spec_warrior_desc").getUnformattedComponentText(),
                25);
        txtsrc_descArcher = StringCutter.cut(
                new TranslationTextComponent("txt.spec_archer_desc").getUnformattedComponentText(),
                25);
        txtsrc_descMage = StringCutter.cut(
                new TranslationTextComponent("txt.spec_mage_desc").getUnformattedComponentText(),
                25);
    }

    private void loadPlayerSpec(PlayerEntity playerEntity){
        currentPlayer = playerEntity;
        playerSpec = currentPlayer.getCapability(MzSpecialtyProvider.MZ_SPEC_CAP, null).orElse(null);
        playerSpecMajor = playerSpec.getMajorType();
        listSelectedIndex = playerSpecMajor;
        if (playerSpecMajor < 0){
            listSelectedIndex = 0;
        }
    }

    private void onListSelectionChanged(){
        switch (listSelectedIndex){
            case 0:
                specDesc = txtsrc_descWarrior;
                winTitle = txtsrc_winTitle + ": " + txtsrc_btnWarrior;
                break;
            case 1:
                specDesc = txtsrc_descArcher;
                winTitle = txtsrc_winTitle + ": " + txtsrc_btnArcher;
                break;
            case 2:
                specDesc = txtsrc_descMage;
                winTitle = txtsrc_winTitle + ": " + txtsrc_btnMage;
                break;
        }
        btnOK.visible = playerSpec.canAddSpec();


    }

    /**
     * Resize will also call init()
     * */
    @Override
    protected void init() {
        super.init();
        win_width = 256;  // this.xSize = 176
        win_height = 166;  // this.ySize = 166
        pos_left = (this.width - win_width) / 2;
        pos_top = (this.height - win_height) / 2;

        btn1 = new Button(pos_left + 10,pos_top + 22,60,20,
                txtsrc_btnWarrior, (tmpBtn) -> {
            listSelectedIndex = 0;
            onListSelectionChanged();
        });
        setSpecColor(0);
        this.addButton(btn1);

        btn2 = new Button(pos_left + 10,pos_top + 42,60,20,
                txtsrc_btnArcher, (tmpBtn) -> {
            listSelectedIndex = 1;
            onListSelectionChanged();
        });
        setSpecColor(1);
        this.addButton(btn2);

        btn3 = new Button(pos_left + 10,pos_top + 62,60,20,
                txtsrc_btnMage, (tmpBtn) -> {
            listSelectedIndex = 2;
            onListSelectionChanged();
        });
        setSpecColor(2);
        this.addButton(btn3);

        btnOK = new Button(pos_left + 120,pos_top + 125,80,20,
                new TranslationTextComponent("txt.btnOK1").getUnformattedComponentText()
                , (tmpBtn) -> {
            playerSpec.learnSpec(listSelectedIndex);
            // Sync to server
            Networking.sendToServer(playerSpec.getAllSpecDataArray());

            this.onClose();
            Minecraft.getInstance().player.sendMessage(new TranslationTextComponent("txt.spec_update_ok"));
        });
        this.addButton(btnOK);

        onListSelectionChanged();  // refresh
    }

    private void setSpecColor(int specIdx){
        int color = 0xffffff;
        if (playerSpec.isSpecAllowed(specIdx)){
            int level = playerSpec.getSpecLevel(specIdx);
            if (level < 100){  // E
                color = 0x3322aa;
            }
            else if (level < 200){  // D
                color = 0x2255aa;
            }
            else if (level < 300){  // C
                color = 0x22aaaa;
            }
            else if (level < 400){  // B
                color = 0x22aa44;
            }
            else if (level < 500){  // A
                color = 0x99aa22;
            }
            else{  // S
                color = 0xeccb34;
            }
        }
        switch (specIdx){
            case 0:
                btn1.setFGColor(color);
                break;
            case 1:
                btn2.setFGColor(color);
                break;
            case 2:
                btn3.setFGColor(color);
                break;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        //this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {


        fontRenderer.drawStringWithShadow(winTitle,-35, 5, 0xffffff);

//        GlStateManager.pushMatrix();
//        GlStateManager.scalef(0.8f, 0.8f, 0.8f);
        int lineTop = 22;
        for (String line : specDesc){
            fontRenderer.drawString(line,45, lineTop, 0x303030);
            lineTop += 12;
        }
//        GlStateManager.popMatrix();

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        TextureManager textureManager = this.minecraft.getTextureManager();
        textureManager.bindTexture(GUI);
        this.blit(pos_left, pos_top, 0, 0, win_width, win_height);
    }


}
