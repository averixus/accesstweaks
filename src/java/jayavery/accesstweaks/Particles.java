/*******************************************************************************
 * Copyright (C) 2016 Jay Avery
 * 
 * This file is part of AccessTweaks. AccessTweaks is free software: distributed
 * under the GNU Affero General Public License (<http://www.gnu.org/licenses/>).
 ******************************************************************************/
package jayavery.accesstweaks;

import java.util.Collection;
import java.util.Set;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

/** Module to control display of particles by type. */
public class Particles extends ParticleManager {
    
    /** Set to store all particles currently allowed to display. */
    private static Set<Integer> allowed = Sets.newHashSet();
    
    /** Config key for this module. */
    public static final String CONFIG_PARTICLES = "particles";
    
    public Particles(World world, TextureManager manager) {
        
        super(world, manager);
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
    public Particle spawnEffectParticle(int particleID, double xCoord,
            double yCoord, double zCoord, double xSpeed, double ySpeed,
            double zSpeed, int... parameters) {
        
        if (allowed.contains(particleID)) {
        
            return super.spawnEffectParticle(particleID, xCoord, yCoord,
                    zCoord, xSpeed, ySpeed, zSpeed, parameters);
            
        }
        
        return null;
    }
    
    /** Spawns crit particles only if allowed. */
    @Override
    public void emitParticleAtEntity(Entity entity, EnumParticleTypes type) {
        
        if (allowed.contains(type.getParticleID())) {
            
            super.emitParticleAtEntity(entity, type);
        }
    }
    
    /** Spawns crit particles only if allowed. */
    @Override
    public void emitParticleAtEntity(Entity entity, EnumParticleTypes type,
            int lifetime) {
        
        if (allowed.contains(type.getParticleID())) {
            
            super.emitParticleAtEntity(entity, type, lifetime);
        }
    }
    
    /** Spawn block breaking particles only if allowed. */
    @Override
    public void addBlockDestroyEffects(BlockPos pos, IBlockState state) {
        
        if (allowed.contains(37)) {
            
            super.addBlockDestroyEffects(pos, state);
        }
    }
    
    /** Spawn block hitting particles only if allowed. */
    @Override
    public void addBlockHitEffects(BlockPos pos, EnumFacing side) {
        
        if (allowed.contains(38)) {
            
            super.addBlockHitEffects(pos, side);
        }
    }
    
    /** Enum defining groups of particles for config settings. */
    public static enum ParticleGroup {
        
        EXPLOSIONS("explosions", 0, 1, 2, 3), WATER("water", 4, 5, 6, 7, 8, 39),
        MAGIC("magic", 13, 14, 15, 16, 17, 42, 47),
        COMBAT("combat", 9, 10, 44, 45, 48), DRIPS("drips", 18, 19),
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
