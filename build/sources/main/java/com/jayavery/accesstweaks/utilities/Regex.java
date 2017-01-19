package com.jayavery.accesstweaks.utilities;


public class Regex {

    public static final String ENVIRONMENT = ".*(ambient\\.cave|portal\\.ambient|lightning|weather).*";
    public static final String MACHINES = ".*(comparator|dispenser|piston|minecart|tnt\\.primed).*";
    public static final String WATER = ".*(water|swim|splash^_).*";
    public static final String FIRE = ".*(fire(\\.|\\z)|(lava|burn).*)";
    public static final String ENTITY_COMBAT = ".*entity.*(death|hurt|shoot|attack|hit|angry|warn|warning|shield|arrow|potion|explode|primed|scream|throw|burn|break).*";
    public static final String ENTITY_AMBIENT = ".*entity.*(ambient|growl|flap|squish|close|open|purr|purreow|egg|flop|stare|breathe|growl|shake|pant|whine|splash)";
    public static final String BLOCKS = ".*block.*(break|place|destroy|hit)";
    public static final String ACTIONS = ".*(use|close|open|locked|click|attach|detach|brew|yes|no|shear|trading|drink|eat|bobber|elytra).*";
    public static final String MOVEMENT = ".*(step|fall|jump|takeoff|teleport|gallop|land|swim)";
    
}
