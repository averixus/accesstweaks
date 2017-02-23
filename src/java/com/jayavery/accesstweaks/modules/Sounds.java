package com.jayavery.accesstweaks.modules;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;
import com.google.common.collect.Maps;
import com.jayavery.accesstweaks.main.Main;
import com.jayavery.accesstweaks.utilities.GuiSubtitles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sounds {
    
    private GuiSubtitles guiSubtitles = new GuiSubtitles();
    
    // Player & some other entity sounds
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
    
    // Some entity & all other sounds
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
    
    // Render subtitles
    @SubscribeEvent
    public void guiOverlay(RenderGameOverlayEvent event) {

        if (event.getType() == ElementType.SUBTITLES) {

            event.setCanceled(true);
            this.guiSubtitles.renderSubtitles(event.getResolution());
        }
    }
    
    public static enum ResultType {
        
        SOUND(true, false), SUBTITLE(false, true),
        NEITHER(false, false), BOTH(true, true);
        
        public static final Map<String, ResultType> stringMap =
                Maps.newHashMap();
        
        static {
            stringMap.put("Sound Only", SOUND);
            stringMap.put("Subtitle Only", SUBTITLE);
            stringMap.put("No Sound Or Subtitle", NEITHER);
            stringMap.put("Sound and Subtitle", BOTH);
        }
        
        public final boolean playSound;
        public final boolean showSubtitle;
        
        private ResultType(boolean playSound, boolean showSubtitle) {
            
            this.playSound = playSound;
            this.showSubtitle = showSubtitle;
        }
    }
    
    public static class SoundResult {
        
        private ResultType type;
        private int colour;
        
        public SoundResult(String type, int colour) {
            
            this.type = ResultType.stringMap.get(type);
            this.colour = colour;
        }
        
        public boolean playSound() {
            
            return this.type.playSound;
        }
        
        public boolean showSubtitle() {
            
            return this.type.showSubtitle;
        }
        
        public int getColour() {
            
            return this.colour;
        }
    }
    
    public static enum SoundCategory {
        
        ME("me"), PLAYER("player"), HOSTILE("hostile"), PASSIVE("passive"),
        MACHINE("machine"), AMBIENT("ambient"), ACTION("action");
        
        private String name;
        
        private SoundCategory(String name) {
            
            this.name = name;
        }
        
        @Override
        public String toString() {
            
            return this.name;
        }
        
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
    
    private static final EnumMap<SoundCategory, SoundResult> categoryResults =
            new EnumMap<SoundCategory, SoundResult>(SoundCategory.class);
    
    public static final String CONFIG_SOUNDS = "sounds";
    
    
    public static void syncConfig() {
        
        for (SoundCategory category : SoundCategory.values()) {
            
            try {
                
                categoryResults.put(category, new SoundResult(Main.config
                        .get(CONFIG_SOUNDS, category + "+result", "Sound Only",
                        "", ResultType.stringMap.keySet().toArray(new
                        String[ResultType.stringMap.size()])).getString(),
                        Integer.parseInt(Main.config.get(CONFIG_SOUNDS,
                        category + "_colour", "FFFFFF").getString(), 16)));
        
            } catch (NumberFormatException ex) {
                
                System.err.println("\"" + Main.config.get(CONFIG_SOUNDS,
                        category + "_colour", "FFFFFF").getString() +
                        "\" is not a valid Hex colour, resetting to default.");
                Main.config.get(CONFIG_SOUNDS, category + "_colour", "FFFFFF")
                        .setToDefault();
            }
        }
    }
    
    // Ref
    public static final String REG_PLAYER = "entity.player.*";
    public static final String REG_HOSTILE = "entity.(blaze|creeper|elder_guardian|enderdragon|endermen|endermite|evocation_illager|ghast|guardian|hostile|husk|magmacube|shulker|silverfish|skeleton|slime|small_magmacube|small_slime|spider|stray|vex|vindication_illager|witch|wither|wither_skeleton|zombie|zombie_villager).*";
    public static final String REG_PASSIVE = "entity.(bat|cat|chicken|cow|donkey|generic|horse|irongolem|llama|mooshroom|mule|pig|polar_bear|rabbit|sheep|snowman|squid|villager|wolf).*";
    public static final String REG_MACHINE = "(block.dispenser|block.piston|block.redstone_torch|entity.minecart).*";
    public static final String REG_AMBIENT = "(weather|block.water|block.portal.ambient|block.lava|block.fire).*";
}
