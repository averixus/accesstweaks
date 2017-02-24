package com.jayavery.accesstweaks.modules;

import java.util.Collection;
import java.util.Set;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraftforge.common.config.Configuration;

/** Module to control display of particles by type. */
public class Particles extends RenderGlobal {
    
    /** Set to store all particles currently allowed to display. */
    private static Set<Integer> allowed = Sets.newHashSet();
    
    /** Config key for this module. */
    public static final String CONFIG_PARTICLES = "particles";
    
    public Particles() {
        
        super(Minecraft.getMinecraft());
    }
    
    /** Updates the settings of this module from the config. */
    public static void syncConfig(Configuration config) {
        
        allowed.clear();
        
        for (ParticleGroup group : ParticleGroup.values()) {
            
            if (config.get(CONFIG_PARTICLES, group.getName(), true)
                    .getBoolean()) {
                
                allowed.addAll(group.getValues());
            }
        }
    }
    
    /** Spawns a particle only if it is in the allowed set. */
    @Override
    public void spawnParticle(int particleID, boolean ignoreRange,
            double xCoord, double yCoord, double zCoord, double xSpeed,
            double ySpeed, double zSpeed, int... parameters) {
        
        if (allowed.contains(particleID)) {
        
            super.spawnParticle(particleID, ignoreRange, xCoord, yCoord,
                    zCoord, xSpeed, ySpeed, zSpeed, parameters);
        }
    }
    
    /** Enum defining groups of particles for config settings. */
    public static enum ParticleGroup {
        
        EXPLOSIONS("explosions", 0, 1, 2, 3), WATER("water", 4, 5, 6, 7, 8, 39),
        MAGIC("magic", 10, 13, 14, 15, 16, 17, 42, 47),
        COMBAT("combat", 9, 44, 45, 48), DRIPS("drips", 18, 19),
        VILLAGERS("villagers", 20, 21), MYCELIUM("mycelium", 22),
        NOTES("notes", 23), PORTALS("portals", 24), GLYPHS("glyphs", 25),
        FIRE("fire", 11, 12, 26, 27, 43), REDSTONE("redstone", 30),
        SLIME("slime", 33), HEARTS("hearts", 34),
        BLOCKS("blocks", 37, 38), ITEMS("items", 36);
        
        private final Collection<Integer> values;
        private final String name;
        
        private ParticleGroup(String name, Integer... values) {
            
            this.values = ImmutableSet.<Integer>copyOf(values);
            this.name = name;
        }
        
        public String getName() {
            
            return this.name;
        }
        
        public Collection<Integer> getValues() {
            
            return this.values;
        }
    }
}
