package techeart.thrad.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import techeart.thrad.compat.CompatCurios;
import techeart.thrad.config.Configuration;
import techeart.thrad.MainClass;
import techeart.thrad.utils.RadiationManager;

@OnlyIn(Dist.CLIENT)
public class RadiationBar extends Gui
{
    //private static final ResourceLocation TEXTURE = new ResourceLocation(MainClass.MODID, "textures/gui/radbar" + Configuration.RAD_BAR_SKIN_ID + ".png");
    private static final String TEXTURE_PATH = "textures/gui/radbar";
    private final ResourceLocation texture;

    private static final int BAR_WIDTH = 16;
    private static final int BAR_HEIGHT = 120;
    private static final int ACTIVE_AREA_HEIGHT = 100;
    private static final int ACTIVE_AREA_UP_OFFSET = 1;
    private static final int BAR_LEFT_OFFSET = 10; //from right border
    private static final int BAR_UP_OFFSET = 0; //from center

    public RadiationBar(Minecraft minecraft)
    {
        super(minecraft);
        texture = new ResourceLocation(MainClass.MODID, TEXTURE_PATH + Configuration.barSkin.get().getId() + ".png");
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event)
    {
        if(event.getType() == RenderGameOverlayEvent.ElementType.LAYER)
        {
            AbstractClientPlayer player = minecraft.player;
            if(player == null) return;
            if(!RadiationManager.shouldRadBarBeRendered(player)) return;

            int radLevel = RadiationManager.getRadLevel(player);
            int max = Configuration.maxRadLevel.get();
            int filledPixels = Math.round((float)radLevel/max * ACTIVE_AREA_HEIGHT);
            filledPixels = Math.min(filledPixels, ACTIVE_AREA_HEIGHT);
            filledPixels = Math.max(filledPixels, 0);

            int barX = event.getWindow().getGuiScaledWidth() - BAR_LEFT_OFFSET - BAR_WIDTH;
            int barY = event.getWindow().getGuiScaledHeight() / 2 - (BAR_HEIGHT / 2) - BAR_UP_OFFSET;

            renderBar(barX, barY, filledPixels, event.getMatrixStack());
        }
    }

    private void renderBar(int x, int y, int filledPixels, PoseStack matrix)
    {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, texture);

        //draws full bar
        this.blit(matrix, x, y, 0, 0, BAR_WIDTH, BAR_HEIGHT);
        //draws empty bar part over the base
        this.blit(matrix, x, y, BAR_WIDTH, 0, BAR_WIDTH, BAR_HEIGHT - filledPixels - ACTIVE_AREA_UP_OFFSET);
    }
}
