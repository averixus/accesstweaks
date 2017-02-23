package com.jayavery.accesstweaks.utilities;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import com.google.common.collect.Lists;
import com.jayavery.accesstweaks.main.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSubtitleOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSubtitles extends Gui {
    
    private Minecraft minecraft = Minecraft.getMinecraft();
    private final List<Subtitle> subtitles = Lists.newArrayList();

    public void addSubtitle(ISound sound, SoundEventAccessor accessor,
            int colour) {

        if (sound != null && accessor.getSubtitle() != null) {
            
            String s = accessor.getSubtitle().getFormattedText();

            if (!this.subtitles.isEmpty()) {
                
                for (Subtitle subtitle : this.subtitles) {
                    
                    if (subtitle.getString().equals(s)) {
                        
                        subtitle.refresh(new Vec3d(sound.getXPosF(),
                                sound.getYPosF(), sound.getZPosF()));
                        return;
                    }
                }
            }

            this.subtitles.add(new Subtitle(s, new Vec3d(sound.getXPosF(),
                    sound.getYPosF(), sound.getZPosF()), colour));
        }
    }
    
    public void renderSubtitles(ScaledResolution resolution) {

        if (!this.subtitles.isEmpty()) {
            
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO);
            Vec3d vec3d = new Vec3d(this.minecraft.player.posX,
                    this.minecraft.player.posY + this.minecraft.player
                    .getEyeHeight(), this.minecraft.player.posZ);
            Vec3d vec3d1 = (new Vec3d(0.0D, 0.0D, -1.0D)).rotatePitch(
                    -this.minecraft.player.rotationPitch * 0.017453292F)
                    .rotateYaw(-this.minecraft.player.rotationYaw *
                    0.017453292F);
            Vec3d vec3d2 = (new Vec3d(0.0D, 1.0D, 0.0D)).rotatePitch(
                    -this.minecraft.player.rotationPitch * 0.017453292F)
                    .rotateYaw(-this.minecraft.player.rotationYaw *
                    0.017453292F);
            Vec3d vec3d3 = vec3d1.crossProduct(vec3d2);
            int i = 0;
            int j = 0;
            
            Iterator<Subtitle> iterator = this.subtitles.iterator();

            while (iterator.hasNext()) {
                
                Subtitle subtitle = iterator.next();

                if (subtitle.getStartTime() + 3000L <=
                        Minecraft.getSystemTime()) {
                    
                    iterator.remove();
                    
                } else {
                    
                    j = Math.max(j, this.minecraft.fontRendererObj
                            .getStringWidth(subtitle.getString()));
                }
            }

            j = j + this.minecraft.fontRendererObj.getStringWidth("<") +
                    this.minecraft.fontRendererObj.getStringWidth(" ") +
                    this.minecraft.fontRendererObj.getStringWidth(">") +
                    this.minecraft.fontRendererObj.getStringWidth(" ");

            for (Subtitle subtitle : this.subtitles) {
                
                String text = subtitle.getString();
                int colour =  subtitle.getColour();
                
                Vec3d vec3d4 = subtitle.getLocation()
                        .subtract(vec3d).normalize();
                double d0 = -vec3d3.dotProduct(vec3d4);
                double d1 = -vec3d1.dotProduct(vec3d4);
                boolean inView = d1 > 0.5D;
                int l = j / 2;
                int i1 = this.minecraft.fontRendererObj.FONT_HEIGHT;
                int j1 = i1 / 2;
                int k1 = this.minecraft.fontRendererObj.getStringWidth(text);

                // Set alpha
                int l1 = MathHelper.floor(MathHelper.clampedLerp(255.0D, 75.0D,
                        (Minecraft.getSystemTime() - subtitle.getStartTime()) /
                        3000.0F));
                int i2 = l1 << 24;

                GlStateManager.pushMatrix();
                GlStateManager.translate(resolution.getScaledWidth() - l *
                        1.0F - 2.0F, resolution.getScaledHeight() - 30 - i *
                        (i1 + 1) * 1.0F, 0.0F);
                GlStateManager.scale(1.0F, 1.0F, 1.0F);
                drawRect(-l - 1, -j1 - 1, l + 1, j1 + 1, -872415232);
                GlStateManager.enableBlend();

                if (!inView) {
                    
                    if (d0 > 0.0D) {
                        
                        this.minecraft.fontRendererObj.drawString(">",
                        l - this.minecraft.fontRendererObj
                        .getStringWidth(">"), -j1, i2 | colour);
                        
                    } else if (d0 < 0.0D) {
                        
                        this.minecraft.fontRendererObj.drawString("<",
                                -l, -j1, i2 | colour);
                    }
                }

                this.minecraft.fontRendererObj.drawString(text, -k1 / 2,
                        -j1, i2 | colour);

                GlStateManager.popMatrix();
                ++i;
            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    
    
    @SideOnly(Side.CLIENT)
    public class Subtitle {
        
        private final String text;
        private long startTime;
        private Vec3d location;
        private int colour;

        public Subtitle(String subtitle, Vec3d location, int colour) {
            
            this.text = subtitle;
            this.location = location;
            this.colour = colour;
            this.startTime = Minecraft.getSystemTime();
        }

        public String getString() {
            
            return this.text;
        }

        public long getStartTime() {
            
            return this.startTime;
        }

        public Vec3d getLocation() {
            
            return this.location;
        }
        
        public int getColour() {
            
            return this.colour;
        }

        public void refresh(Vec3d newLocation) {
            
            this.location = newLocation;
            this.startTime = Minecraft.getSystemTime();
        }
    }
}
