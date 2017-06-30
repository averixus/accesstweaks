/*******************************************************************************
 * Copyright (C) 2016 Jay Avery
 * 
 * This file is part of AccessTweaks. AccessTweaks is free software: distributed
 * under the GNU Affero General Public License (<http://www.gnu.org/licenses/>).
 ******************************************************************************/
package jayavery.accesstweaks;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Module to control sounds and subtitles. */
@EventBusSubscriber
public class Sounds {
    
    /** Regex for player sounds. */
    private static final String REG_PLAYER = "entity.player.*";
    /** Regex for hostile entity sounds. */
    private static final String REG_HOSTILE = "entity.(blaze|creeper|elder_guardian|enderdragon|endermen|endermite|evocation_illager|ghast|guardian|hostile|husk|magmacube|shulker|silverfish|skeleton|slime|small_magmacube|small_slime|spider|stray|vex|vindication_illager|witch|wither|wither_skeleton|zombie|zombie_villager).*";
    /** Regex for passive entity sounds. */
    private static final String REG_PASSIVE = "entity.(bat|cat|chicken|cow|donkey|generic|horse|irongolem|llama|mooshroom|mule|pig|polar_bear|rabbit|sheep|snowman|squid|villager|wolf).*";
    /** Regex for machinery sounds. */
    private static final String REG_MACHINE = "(block.dispenser|block.piston|block.redstone_torch|entity.minecart).*";
    /** Regex for ambient sounds. */
    private static final String REG_AMBIENT = "(weather|block.water|block.portal.ambient|block.lava|block.fire).*";
    
    /** White hex colour. */
    private static final int WHITE = 16777215;
    
    /** Map to store the results of each sound type. */
    private static final EnumMap<SoundCategory, SoundResult> categoryResults =
            new EnumMap<SoundCategory, SoundResult>(SoundCategory.class);
    
    /** Config key for this module. */
    public static final String CONFIG_SOUNDS = "sounds";
    
    /** Gui to render custom subtitles. */
    private static GuiSubtitles guiSubtitles = new GuiSubtitles();
    
    /** Updates the settings for this module from the config. */
    public static void syncConfig(Configuration config) {
        
        for (SoundCategory category : SoundCategory.values()) {
            
            String catName = category.getName();
            
            String configResult = config.get(CONFIG_SOUNDS,
                    catName + "+result", "Sound Only", "",
                    ResultType.nameArray).getString();
            String configColour = config.get(CONFIG_SOUNDS,
                    catName + "_colour", "FFFFFF").getString();
            int colour;
            
            try {
                
                colour = Integer.parseInt(configColour, 16);
        
            } catch (NumberFormatException ex) {
                
                System.err.println("\"" + configColour +
                        "\" is not a valid Hex colour, resetting to default.");
                colour = WHITE;
                config.get(CONFIG_SOUNDS, catName + "_colour","FFFFFF")
                        .setToDefault();
            }
            
            categoryResults.put(category, new SoundResult(configResult,
                    colour));
        }
    }
    
    /** Play or prevent sound and subtitle according to settings.
     * This event catches player & some other entity sounds */
    @SubscribeEvent
    public static void playEntitySound(PlaySoundAtEntityEvent event) {
        
        Entity entity = event.getEntity();
        ISound sound = null;
        
        if (entity != null) {
        
            sound = new PositionedSoundRecord(event.getSound(),
                    event.getCategory(), event.getVolume(), event.getPitch(),
                    (float) entity.posX, (float) entity.posY,
                    (float) entity.posZ);
        }
        
        SoundCategory category;
        
        if (Minecraft.getMinecraft().player != null &&
                entity == Minecraft.getMinecraft().player) {
            
            category = SoundCategory.ME;
            
        } else {
            
            category = SoundCategory.categorise(sound);
        }

        SoundResult result = categoryResults.get(category);
        
        if (!result.playSound()) {
            
            event.setCanceled(true);
        }
        
        if (result.showSubtitle()) {
            
            guiSubtitles.addSubtitle(sound, Minecraft.getMinecraft()
                    .getSoundHandler().getAccessor(event.getSound()
                    .getSoundName()), result.getColour());
        }
    }
    
    /** Play or prevent sound and subtitle according to settings.
     * This event catches some entity & all other sounds */
    @SubscribeEvent
    public static void playSound(PlaySoundEvent event) {

        ISound sound = event.getSound();
        SoundCategory category = SoundCategory.categorise(sound);
        SoundResult result = categoryResults.get(category);
        
        if (!result.playSound()) {
            
            event.setResultSound(null);
        }
        
        if (result.showSubtitle()) {
            
            guiSubtitles.addSubtitle(sound, Minecraft.getMinecraft()
                    .getSoundHandler().getAccessor(event.getSound()
                    .getSoundLocation()), result.getColour());
        }
    }
    
    /** Override vanilla subtitles with this module's subtitles. */
    @SubscribeEvent
    public static void guiOverlay(RenderGameOverlayEvent event) {

        if (event.getType() == ElementType.SUBTITLES) {

            event.setCanceled(true);
            guiSubtitles.renderSubtitles(event.getResolution());
        }
    }
    
    /** Enum defining types of sound result. */
    private enum ResultType {
        
        SOUND(true, false), SUBTITLE(false, true),
        NEITHER(false, false), BOTH(true, true);
        
        /** Map config string to result type. */
        public static final Map<String, ResultType> nameMap =
                Maps.newHashMap();
        /** Array of config strings. */
        public static final String[] nameArray;
        
        static {
            nameMap.put("Sound Only", SOUND);
            nameMap.put("Subtitle Only", SUBTITLE);
            nameMap.put("No Sound Or Subtitle", NEITHER);
            nameMap.put("Sound and Subtitle", BOTH);
            nameArray = nameMap.keySet().toArray(new String[4]);
        }
        
        public final boolean playSound;
        public final boolean showSubtitle;
        
        private ResultType(boolean playSound, boolean showSubtitle) {
            
            this.playSound = playSound;
            this.showSubtitle = showSubtitle;
        }
    }
    
    /** Object storing the result of a sound event. */
    private static class SoundResult {
        
        /** The result type. */
        private ResultType type;
        /** The subtitle colour. */
        private int colour;
        
        public SoundResult(String type, int colour) {
            
            this.type = ResultType.nameMap.get(type);
            this.colour = colour;
        }
        
        /** @return Whether the sound should play. */
        public boolean playSound() {
            
            return this.type.playSound;
        }
        
        /** @return Whether the subtitle should display. */
        public boolean showSubtitle() {
            
            return this.type.showSubtitle;
        }
        
        /** @return The colour of the subtitle. */
        public int getColour() {
            
            return this.colour;
        }
    }
    
    /** Enum defining categories of sound for config. */
    private enum SoundCategory {
        
        ME("me"), PLAYER("player"), HOSTILE("hostile"), PASSIVE("passive"),
        MACHINE("machine"), AMBIENT("ambient"), ACTION("action");
        
        private final String name;
        
        private SoundCategory(String name) {
            
            this.name = name;
        }
        
        public String getName() {
            
            return this.name;
        }
        
        /** @return The category the given sound belongs to. */
        public static SoundCategory categorise(ISound sound) {
            
            if (sound == null) {
                
                return AMBIENT;
            }
            
            String name = sound.getSoundLocation().getResourcePath();
            
            if (Pattern.matches(REG_PLAYER, name)) {
                
                return PLAYER;
                
            } else if (Pattern.matches(REG_HOSTILE, name)) {
                
                return HOSTILE;
                
            } else if (Pattern.matches(REG_PASSIVE, name)) {
                
                return PASSIVE;
                
            } else if (Pattern.matches(REG_MACHINE, name)) {
                
                return MACHINE;
                
            } else if (Pattern.matches(REG_AMBIENT, name)) {
                
                return AMBIENT;
                
            } else {
            
                return ACTION;
            }
        }
    }
    
    /** Replacement for vanilla subtitle gui. */
    @SideOnly(Side.CLIENT)
    public static class GuiSubtitles extends Gui {
        
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
                        
                        j = Math.max(j, this.mc.fontRenderer
                                .getStringWidth(subtitle.getString()));
                    }
                }

                j = j + this.mc.fontRenderer.getStringWidth("<") +
                        this.mc.fontRenderer.getStringWidth(" ") +
                        this.mc.fontRenderer.getStringWidth(">") +
                        this.mc.fontRenderer.getStringWidth(" ");

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
                    int i1 = this.mc.fontRenderer.FONT_HEIGHT;
                    int j1 = i1 / 2;
                    int k1 = this.mc.fontRenderer.getStringWidth(text);

                    // Alpha for age
                    int l1 = MathHelper.floor(MathHelper.clampedLerp(255.0D,
                            75.0D, (Minecraft.getSystemTime() -
                            subtitle.getStartTime()) / 3000.0F));
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
                            
                            this.mc.fontRenderer.drawString(">",
                            l - this.mc.fontRenderer
                            .getStringWidth(">"), -j1, alpha | colour);
                            
                        } else if (d0 < 0.0D) {
                            
                            this.mc.fontRenderer.drawString("<",
                                    -l, -j1, alpha | colour);
                        }
                    }

                    // Draw text
                    this.mc.fontRenderer.drawString(text, -k1 / 2,
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
}
