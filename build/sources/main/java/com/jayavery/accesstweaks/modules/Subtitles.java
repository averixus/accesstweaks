package com.jayavery.accesstweaks.modules;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import com.jayavery.accesstweaks.main.Main;
import com.jayavery.accesstweaks.utilities.Regex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.gui.GuiSubtitleOverlay;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Subtitles extends GuiSubtitleOverlay {
    
    public static final String CONFIG_SUBTITLES = "captions";
    
    public static boolean showEnvironment;
    public static boolean showMachines;
    public static boolean showWater;
    public static boolean showFire;
    public static boolean showEntityCombat;
    public static boolean showEntityAmbient;
    public static boolean showBlocks;
    public static boolean showActions;
    public static boolean showMovement; 
    
    public static String[] configShowAlways;
    public static String[] configShowNever;
    
    public Subtitles(Minecraft clientIn) {
        
        super(clientIn);
    }

    @SubscribeEvent
    public void guiOverlay(RenderGameOverlayEvent event) {

        if (event.getType() == ElementType.SUBTITLES) {
            
            event.setCanceled(true);
            this.renderSubtitles(event.getResolution());
        }        
    }    
    @Override
    public void soundPlay(ISound sound, SoundEventAccessor accessor) {
        System.out.println("adding subtitle");
        if (accessor.getSubtitle() == null) {
            
            return;
        }
        
        String subtitle = accessor.getSubtitle().getUnformattedText();
        String name = ((TextComponentTranslation) accessor
                .getSubtitle()).getKey();
        
        boolean showSubtitle = true;
        
        if (!showEnvironment && Pattern.matches(Regex.ENVIRONMENT, name)) {
            
            showSubtitle = false;
            
        } else if (!showMachines && Pattern.matches(Regex.MACHINES, name)) {
            
            showSubtitle = false;
            
        } else if (!showWater && Pattern.matches(Regex.WATER, name)) {
            
            showSubtitle = false;
            
        } else if (!showFire && Pattern.matches(Regex.FIRE, name)) {
            
            showSubtitle = false;
            
        } else if (!showEntityCombat &&
                Pattern.matches(Regex.ENTITY_COMBAT, name)) {
            
            showSubtitle = false;
            
        } else if (!showEntityAmbient &&
                Pattern.matches(Regex.ENTITY_AMBIENT, name)) {
            
            showSubtitle = false;
            
        } else if (!showBlocks && Pattern.matches(Regex.BLOCKS, name)) {
            
            showSubtitle = false;
            
        } else if (!showActions && Pattern.matches(Regex.ACTIONS, name)) {
            
            showSubtitle = false;
            
        } else if (!showMovement && Pattern.matches(Regex.MOVEMENT, name)) {
            
            showSubtitle = false;
        }
        
        for (String sub : configShowNever) {

            try {
                
                if (Pattern.matches(sub, name) ||
                        Pattern.matches(sub, subtitle)) {

                    showSubtitle = false;
                }
                
            } catch (PatternSyntaxException e) {

                System.err.println("Regular expression syntax error in: \"" +
                        e.getPattern() + "\"");
                System.err.println(e.getDescription() + " around index " +
                        e.getIndex());
            }
        }
        
        for (String sub : configShowAlways) {

            try {
                
                if (Pattern.matches(sub, name) ||
                        Pattern.matches(sub, subtitle)) {

                    showSubtitle = true;
                }
                
            } catch (PatternSyntaxException e) {

                System.err.println("Regular expression syntax error in: \"" +
                        e.getPattern() + "\"");
                System.err.println(e.getDescription() + " around index " +
                        e.getIndex());
            }
        }
        
        if (showSubtitle) {
            
            super.soundPlay(sound, accessor);
        }
    }

    public static void syncConfig() {

        showEnvironment = Main.config.get(CONFIG_SUBTITLES,
                "showEnvironment", true).getBoolean();
        showMachines = Main.config.get(CONFIG_SUBTITLES,
                "showMachines", true).getBoolean();
        showWater = Main.config.get(CONFIG_SUBTITLES,
                "showWater", true).getBoolean();
        showFire = Main.config.get(CONFIG_SUBTITLES,
                "showFire", true).getBoolean();
        showEntityCombat = Main.config.get(CONFIG_SUBTITLES,
                "showEntityCombat", true).getBoolean();
        showEntityAmbient = Main.config.get(CONFIG_SUBTITLES,
                "showEntityAmbient", true).getBoolean();
        showBlocks = Main.config.get(CONFIG_SUBTITLES,
                "showBlocks", true).getBoolean();
        showActions = Main.config.get(CONFIG_SUBTITLES,
                "showActions", true).getBoolean();
        showMovement = Main.config.get(CONFIG_SUBTITLES,
                "showMovement", true).getBoolean();
        
        configShowAlways = Main.config.get(CONFIG_SUBTITLES,
                "configShowAlways", new String[] {}).getStringList();
        configShowNever = Main.config.get(CONFIG_SUBTITLES,
                "configShowNever", new String[] {}).getStringList();
   }
}
