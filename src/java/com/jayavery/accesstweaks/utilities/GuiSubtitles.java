package com.jayavery.accesstweaks.utilities;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import com.google.common.collect.Lists;
import com.jayavery.accesstweaks.main.Accesstweaks;
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

/** Replacement for vanilla subtitle gui. */
@SideOnly(Side.CLIENT)
public class GuiSubtitles extends Gui {
    
    /** Minecraft instance. */
    private Minecraft mc = Minecraft.getMinecraft();
    /** List of subtitles currently displaying. */
    private final List<Subtitle> subtitles = Lists.newArrayList();

    /** Adds a subtitle to the list in the given colour. */
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
    
    /** Draws all current subtitles on the screen. */
    public void renderSubtitles(ScaledResolution resolution) {

        if (!this.subtitles.isEmpty()) {
            
            // Setup
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO);
            Vec3d vec3d = new Vec3d(this.mc.player.posX,
                    this.mc.player.posY + this.mc.player
                    .getEyeHeight(), this.mc.player.posZ);
            Vec3d vec3d1 = (new Vec3d(0.0D, 0.0D, -1.0D)).rotatePitch(
                    -this.mc.player.rotationPitch * 0.017453292F)
                    .rotateYaw(-this.mc.player.rotationYaw *
                    0.017453292F);
            Vec3d vec3d2 = (new Vec3d(0.0D, 1.0D, 0.0D)).rotatePitch(
                    -this.mc.player.rotationPitch * 0.017453292F)
                    .rotateYaw(-this.mc.player.rotationYaw *
                    0.017453292F);
            Vec3d vec3d3 = vec3d1.crossProduct(vec3d2);
            int i = 0;
            int j = 0;
            
            Iterator<Subtitle> iterator = this.subtitles.iterator();
            
            // Get width for display
            while (iterator.hasNext()) {
                
                Subtitle subtitle = iterator.next();

                if (subtitle.getStartTime() + 3000L <=
                        Minecraft.getSystemTime()) {
                    
                    iterator.remove();
                    
                } else {
                    
                    j = Math.max(j, this.mc.fontRendererObj
                            .getStringWidth(subtitle.getString()));
                }
            }

            j = j + this.mc.fontRendererObj.getStringWidth("<") +
                    this.mc.fontRendererObj.getStringWidth(" ") +
                    this.mc.fontRendererObj.getStringWidth(">") +
                    this.mc.fontRendererObj.getStringWidth(" ");

            // Draw each
            for (Subtitle subtitle : this.subtitles) {
                
                String text = subtitle.getString();
                int colour =  subtitle.getColour();
                
                // Setup
                Vec3d vec3d4 = subtitle.getLocation()
                        .subtract(vec3d).normalize();
                double d0 = -vec3d3.dotProduct(vec3d4);
                double d1 = -vec3d1.dotProduct(vec3d4);
                boolean inView = d1 > 0.5D;
                int l = j / 2;
                int i1 = this.mc.fontRendererObj.FONT_HEIGHT;
                int j1 = i1 / 2;
                int k1 = this.mc.fontRendererObj.getStringWidth(text);

                // Alpha for age
                int l1 = MathHelper.floor(MathHelper.clampedLerp(255.0D, 75.0D,
                        (Minecraft.getSystemTime() - subtitle.getStartTime()) /
                        3000.0F));
                int alpha = l1 << 24;

                // Draw background
                GlStateManager.pushMatrix();
                GlStateManager.translate(resolution.getScaledWidth() - l *
                        1.0F - 2.0F, resolution.getScaledHeight() - 30 - i *
                        (i1 + 1) * 1.0F, 0.0F);
                GlStateManager.scale(1.0F, 1.0F, 1.0F);
                drawRect(-l - 1, -j1 - 1, l + 1, j1 + 1, -872415232);
                GlStateManager.enableBlend();

                // Add direction arrow
                if (!inView) {
                    
                    if (d0 > 0.0D) {
                        
                        this.mc.fontRendererObj.drawString(">",
                        l - this.mc.fontRendererObj
                        .getStringWidth(">"), -j1, alpha | colour);
                        
                    } else if (d0 < 0.0D) {
                        
                        this.mc.fontRendererObj.drawString("<",
                                -l, -j1, alpha | colour);
                    }
                }

                // Draw text
                this.mc.fontRendererObj.drawString(text, -k1 / 2,
                        -j1, alpha | colour);

                GlStateManager.popMatrix();
                i++;
            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    /** Object for storing information about a single subtitle. */
    @SideOnly(Side.CLIENT)
    private class Subtitle {
        
        /** Text of the subtitle. */
        private final String text;
        /** World time of start. */
        private long startTime;
        /** Location of soudn origin. */
        private Vec3d location;
        /** Text colour. */
        private final int colour;

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

        /** Update start time and location of this subtitle. */
        public void refresh(Vec3d newLocation) {
            
            this.location = newLocation;
            this.startTime = Minecraft.getSystemTime();
        }
    }
}
