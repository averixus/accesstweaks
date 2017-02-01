package com.jayavery.accesstweaks.modules;

import java.util.HashSet;
import java.util.Set;
import com.google.common.collect.Sets;
import com.jayavery.accesstweaks.main.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;

public class Particles extends RenderGlobal {
    
    public Particles() {
        
        super(Minecraft.getMinecraft());
    }
    
    @Override
    public void drawBlockDamageTexture(Tessellator tessellator,
            VertexBuffer worldRenderer, Entity entity, float ticks) {
        
        super.drawBlockDamageTexture(tessellator, worldRenderer, entity, ticks);
    }
    
    @Override
    public void spawnParticle(int particleID, boolean ignoreRange,
            double xCoord, double yCoord, double zCoord, double xSpeed,
            double ySpeed, double zSpeed, int... parameters) {
        
        if (allowed.contains(particleID)) {
        
            super.spawnParticle(particleID, ignoreRange, xCoord, yCoord,
                    zCoord, xSpeed, ySpeed, zSpeed, parameters);
        }
    }
    
    public static enum ParticleSet {
        
        EXPLOSIONS("explosions", 0, 1, 2, 3), WATER("water", 4, 5, 6, 7, 8, 39),
        MAGIC("magic", 10, 13, 14, 15, 16, 17, 42, 47),
        COMBAT("combat", 9, 44, 45, 48), DRIPS("drips", 18, 19),
        VILLAGERS("villagers", 20, 21), MYCELIUM("mycelium", 22),
        NOTES("notes", 23), PORTALS("portals", 24), GLYPHS("glyphs", 25),
        FIRE("fire", 11, 12, 26, 27, 43), REDSTONE("redstone", 30),
        SLIME("slime", 33), HEARTS("hearts", 34),
        BLOCKS("blocks", 37, 38), ITEMS("items", 36);
        
        private int[] values;
        private String name;
        
        private ParticleSet(String name, int... values) {
            
            this.values = values;
            this.name = name;
        }
        
        @Override
        public String toString() {
            
            return this.name;
        }
        
        public int[] getValues() {
            
            return this.values;
        }
    }
    
    private static Set<Integer> allowed = Sets.newHashSet();
    
    public static final String CONFIG_PARTICLES = "particles";
    
    public static void syncConfig() {
        
        for (ParticleSet set : ParticleSet.values()) {
            
            if (Main.config.get(CONFIG_PARTICLES, set.toString(), true)
                    .getBoolean()) {
                
                for (int id : set.getValues()) {
                    
                    allowed.add(id);
                }
            }
        }
    }
}
