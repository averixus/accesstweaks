package com.jayavery.accesstweaks.modules;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;
import com.google.common.collect.Maps;
import com.jayavery.accesstweaks.main.Accesstweaks;
import com.jayavery.accesstweaks.utilities.GuiSubtitles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Module to control sounds and subtitles. */
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
    
    private static final int WHITE = 16777215;
    
    /** Map to store the results of each sound type. */
    private static final EnumMap<SoundCategory, SoundResult> categoryResults =
            new EnumMap<SoundCategory, SoundResult>(SoundCategory.class);
    
    /** Config key for this module. */
    public static final String CONFIG_SOUNDS = "sounds";
    
    /** Gui to render custom subtitles. */
    private GuiSubtitles guiSubtitles = new GuiSubtitles();
    
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
    public void playEntitySound(PlaySoundAtEntityEvent event) {
        
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
            
            this.guiSubtitles.addSubtitle(sound, Minecraft.getMinecraft()
                    .getSoundHandler().getAccessor(event.getSound()
                    .getSoundName()), result.getColour());
        }
    }
    
    /** Play or prevent sound and subtitle according to settings.
     * This event catches some entity & all other sounds */
    @SubscribeEvent
    public void playSound(PlaySoundEvent event) {

        ISound sound = event.getSound();
        SoundCategory category = SoundCategory.categorise(sound);
        SoundResult result = categoryResults.get(category);
        
        if (!result.playSound()) {
            
            event.setResult(null);
        }
        
        if (result.showSubtitle()) {
            
            this.guiSubtitles.addSubtitle(sound, Minecraft.getMinecraft()
                    .getSoundHandler().getAccessor(event.getSound()
                    .getSoundLocation()), result.getColour());
        }
    }
    
    /** Override vanilla subtitles with this module's subtitles. */
    @SubscribeEvent
    public void guiOverlay(RenderGameOverlayEvent event) {

        if (event.getType() == ElementType.SUBTITLES) {

            event.setCanceled(true);
            this.guiSubtitles.renderSubtitles(event.getResolution());
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
}
