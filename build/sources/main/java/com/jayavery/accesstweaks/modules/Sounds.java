package com.jayavery.accesstweaks.modules;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import com.jayavery.accesstweaks.main.Main;
import com.jayavery.accesstweaks.utilities.Regex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sounds {
    
    public static final String CONFIG_SOUNDS = "sounds";
    
    public static boolean playEnvironment;
    public static boolean playMachines;
    public static boolean playWater;
    public static boolean playFire;
    public static boolean playEntityCombat;
    public static boolean playEntityAmbient;
    public static boolean playBlocks;
    public static boolean playActions;
    public static boolean playMovement;
    
    public static String[] configPlayAlways;
    public static String[] configPlayNever;
    
    @SubscribeEvent
    public void playSound(PlaySoundEvent event) {

        ISound sound = event.getSound();
        String name = event.getSound().getSoundLocation().toString();
        boolean playSound = true;
        
        if (!playEnvironment && Pattern.matches(Regex.ENVIRONMENT, name)) {
            
            playSound = false;
            
        } else if (!playMachines && Pattern.matches(Regex.MACHINES, name)) {
            
            playSound = false;
            
        } else if (!playWater && Pattern.matches(Regex.WATER, name)) {
            
            playSound = false;
            
        } else if (!playFire && Pattern.matches(Regex.FIRE, name)) {
            
            playSound = false;
            
        } else if (!playEntityCombat &&
                Pattern.matches(Regex.ENTITY_COMBAT, name)) {
            
            playSound = false;
            
        } else if (!playEntityAmbient &&
                Pattern.matches(Regex.ENTITY_AMBIENT, name)) {
            
            playSound = false;
            
        } else if (!playBlocks && Pattern.matches(Regex.BLOCKS, name)) {
            
            playSound = false;
            
        } else if (!playActions && Pattern.matches(Regex.ACTIONS, name)) {
            
            playSound = false;
            
        } else if (!playMovement && Pattern.matches(Regex.MOVEMENT, name)) {
            
            playSound = false;
        }
        
        for (String sub : configPlayNever) {

            try {
                
                if (Pattern.matches(sub, name)) {

                    playSound = false;
                }
                
            } catch (PatternSyntaxException e) {

                System.err.println("Regular expression syntax error in: \"" +
                        e.getPattern() + "\"");
                System.err.println(e.getDescription() + " around index " +
                        e.getIndex());
            }
        }
        
        for (String sub : configPlayAlways) {

            try {
                
                if (Pattern.matches(sub, name)) {

                    playSound = true;
                }
                
            } catch (PatternSyntaxException e) {
                
                System.err.println("Regular expression syntax error in: \"" +
                        e.getPattern() + "\"");
                System.err.println(e.getDescription() + " around index " +
                        e.getIndex());
            }
        }

        if (!playSound) {
            
            event.setResultSound(null);
            Main.SUBTITLES.soundPlay(sound, Minecraft.getMinecraft()
                    .getSoundHandler().getAccessor(event.getSound()
                    .getSoundLocation()));
        }
    }
    
    public static void syncConfig() {
        
        playEnvironment = Main.config.get(CONFIG_SOUNDS,
                "playEnvironment", true).getBoolean();
        playMachines = Main.config.get(CONFIG_SOUNDS,
                "playMachines", true).getBoolean();
        playWater = Main.config.get(CONFIG_SOUNDS,
                "playWater", true).getBoolean();
        playFire = Main.config.get(CONFIG_SOUNDS,
                "playFire", true).getBoolean();
        playEntityCombat = Main.config.get(CONFIG_SOUNDS,
                "playEntityCombat", true).getBoolean();
        playEntityAmbient = Main.config.get(CONFIG_SOUNDS,
                "playEntityAmbient", true).getBoolean();
        playBlocks = Main.config.get(CONFIG_SOUNDS,
                "playBlocks", true).getBoolean();
        playActions = Main.config.get(CONFIG_SOUNDS,
                "playActions", true).getBoolean();
        playMovement = Main.config.get(CONFIG_SOUNDS,
                "playMovement", true).getBoolean();
        
        configPlayAlways = Main.config.get(CONFIG_SOUNDS,
                "configPlayAlways", new String[] {}).getStringList();
        configPlayNever = Main.config.get(CONFIG_SOUNDS,
                "configPlayNever", new String[] {}).getStringList();
    }
}
