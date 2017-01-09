package com.jayavery.accesstweaks.modules;

import java.util.HashSet;
import java.util.Set;
import com.jayavery.accesstweaks.main.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;

public class Particles extends RenderGlobal {
    
    public static final String CONFIG_PARTICLES = "particles";
    
    // Reference sets
    public static final int[] EXPLOSIONS = new int[] {0, 1, 2, 3};
    public static final int[] WATER = new int[] {4, 5, 6, 7, 8, 39};
    public static final int[] MAGIC = new int[]
            {10, 13, 14, 15, 16, 17, 42, 47};
    public static final int[] COMBAT = new int[] {9, 44, 45, 48};
    public static final int[] DRIPS = new int[] {18, 19};
    public static final int[] VILLAGERS = new int[] {20, 21};
    public static final int[] MYCELIUM = new int[] {22};
    public static final int[] NOTES = new int[] {23};
    public static final int[] PORTALS = new int[] {24};
    public static final int[] GLYPHS = new int[] {25};
    public static final int[] FIRE = new int[] {11, 12, 26, 27, 43};
    public static final int[] REDSTONE = new int[] {30};
    public static final int[] SLIME = new int[] {33};
    public static final int[] HEARTS = new int[] {34};
    public static final int[] BLOCKS = new int[] {37, 38};
    public static final int[] ITEMS = new int[] {36};
    
    public static Set<Integer> allowed;
    
    public Particles() {
        
        super(Minecraft.getMinecraft());
    }
    
    @Override
    public void drawBlockDamageTexture(Tessellator tessellator, VertexBuffer worldRenderer, Entity entity, float ticks) {
        
        super.drawBlockDamageTexture(tessellator, worldRenderer, entity, ticks);
    }
    
    @Override
    public void spawnParticle(int particleID, boolean ignoreRange,
            double xCoord, double yCoord, double zCoord, double xSpeed,
            double ySpeed, double zSpeed, int... parameters) {
        
        if (allowed.contains(particleID)) {
        
            this.func_190570_a(particleID, ignoreRange, false, xCoord, yCoord,
                    zCoord, xSpeed, ySpeed, zSpeed, parameters);
        }
    }
    
    public static void syncConfig() {
        
        allowed = new HashSet<Integer>();
        
        if (readConfig("explosions")) {
            
            for (int id : EXPLOSIONS) {
                
                allowed.add(id);
            }
        }
        
        if (readConfig("water")) {
            
            for (int id : WATER) {
                
                allowed.add(id);
            }
        }
        
        if (readConfig("magic")) {
            
            for (int id : MAGIC) {
                
                allowed.add(id);
            }
        }
        
        if (readConfig("combat")) {
            
            for (int id : COMBAT) {
                
                allowed.add(id);
            }
        }
        
        if (readConfig("drips")) {
            
            for (int id : DRIPS) {
                
                allowed.add(id);
            }
        }
        
        if (readConfig("villagers")) {
            
            for (int id : VILLAGERS) {
                
                allowed.add(id);
            }
        }
        
        if (readConfig("mycelium")) {
            
            for (int id : MYCELIUM) {
                
                allowed.add(id);
            }
        }
        
        if (readConfig("notes")) {
            
            for (int id : NOTES) {
                
                allowed.add(id);
            }
        }
        
        if (readConfig("portals")) {
            
            for (int id : PORTALS) {
                
                allowed.add(id);
            }
        }
        
        if (readConfig("glyphs")) {
            
            for (int id : GLYPHS) {
                
                allowed.add(id);
            }
        }
        
        if (readConfig("fire")) {
            
            for (int id : FIRE) {
                
                allowed.add(id);
            }
        }
        
        if (readConfig("redstone")) {
            
            for (int id : REDSTONE) {
                
                allowed.add(id);
            }
        }
        
        if (readConfig("slime")) {
            
            for (int id : SLIME) {
                
                allowed.add(id);
            }
        }
        
        if (readConfig("hearts")) {
            
            for (int id : HEARTS) {
                
                allowed.add(id);
            }
        }
        
        if (readConfig("blocks")) {
            
            for (int id : BLOCKS) {
                
                allowed.add(id);
            }
        }
        
        if (readConfig("items")) {
            
            for (int id : ITEMS) {
                
                allowed.add(id);
            }
        }
    }
    
    private static boolean readConfig(String name) {
        
        return Main.config.get(CONFIG_PARTICLES, name, true).getBoolean();
    }
}
