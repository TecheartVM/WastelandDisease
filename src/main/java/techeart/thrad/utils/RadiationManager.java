package techeart.thrad.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import techeart.thrad.capabilities.radcap.IRadiation;
import techeart.thrad.capabilities.radcap.RadiationCapability;
import techeart.thrad.config.Configuration;
import techeart.thrad.network.PacketHandler;
import techeart.thrad.network.packets.PacketSyncRadCap;

import java.util.Map;
import java.util.Random;

public class RadiationManager
{
    private static Random random = new Random();

    public static boolean tickRadiation(Player player)
    {
        BlockPos headPos = player.blockPosition().above();
        int exposure = getExposureAtPos(player.level, headPos);

        //System.out.println("Exposure: " + exposure);

        MobEffectInstance curEffect = player.getEffect(RegistryHandler.EXPOSURE.get());
        if(curEffect != null && curEffect.getAmplifier() >= exposure) return false;
        if(exposure < 0)
        {
            int radLevel = getRadLevel(player);
            if (radLevel > Configuration.defaultRadLevel.get() && radLevel > Configuration.minRadLevel.get())
                if (random.nextInt(10000) < Configuration.radLevelDecreaseChance.get())
                    setRadLevel(player, radLevel - 1);
        }
        else
        {
            player.addEffect(new MobEffectInstance(
                    RegistryHandler.EXPOSURE.get(),
                    600, //TODO: config option
                    exposure,
                    false,
                    false,
                    false
            ));
        }
        return true;
    }

    /**Gets the pollution exposure level at the specified position in the specified world
     * considering the dimension, biome and surrounding objects.
     * @return the actual exposure level in world location subtracted by a number
     * of layers of solid blocks detected by raycast in some random direction.
     * */
    public static int getExposureAtPos(Level world, BlockPos pos)
    {
        if(pos.getY() < Configuration.minYLevel.get()
        || pos.getY() > Configuration.maxYLevel.get())
            return -1;

        /*--------------check for dimension--------------*/
        String dimId = world.dimension().location().toString();
        //System.out.println(dimId);
        boolean dimensionListed = Configuration.dimensionsList.get().contains(dimId);
        if(dimensionListed) { if(Configuration.dimensionsBlacklist.get()) return -1; }
        else if(!Configuration.dimensionsBlacklist.get()) return -1;

        int curExposure = Configuration.maxExposure.get();

        /*----------------check for biome----------------*/
        ResourceLocation biomeRL = world.getBiome(pos).getRegistryName();
        if(biomeRL != null)
        {
            String biomeId = biomeRL.toString();
            //System.out.println(biomeId);
            boolean biomeListed = false;
            for (Map.Entry<String, Integer> e : Configuration.biomesList.entrySet())
            {
                if(e.getKey().equals(biomeId))
                {
                    biomeListed = true;
                    curExposure = Math.min(e.getValue(), Configuration.maxExposure.get());
                    break;
                }
            }
            if(biomeListed) { if(Configuration.biomesBlacklist.get()) return -1; }
            else if(!Configuration.biomesBlacklist.get()) return -1;
        }

        if(world.canSeeSky(pos)) return curExposure;

        int coverLayers = getProtectiveLayers(world, pos, false);
        return curExposure - coverLayers;
    }

    /**Gets the number of solid blocks found by a raycast in a random direction.*/
    private static int getProtectiveLayers(Level world, BlockPos castOrigin, boolean allowCastDown)
    {
        int blocksFound = 0;

        Random rand = new Random();
        Vec3 dir = new Vec3(
                rand.nextDouble() * 2.0d - 1.0d,
                 allowCastDown ? rand.nextDouble() * 2.0d - 1.0d : rand.nextDouble(),
                rand.nextDouble() * 2.0d - 1.0d
        );
        if(dir.length() == 0) dir = new Vec3(1,1,1);

        HitResult hit = Utils.raycastSearchForBlock(world, castOrigin, dir, Configuration.coverSearchRadius.get());
        if(hit.getType() == HitResult.Type.MISS) return 0;

        BlockState hitState = Utils.getHitBlockState(world, hit, dir);
        if(!canBlockPassRad(hitState)) blocksFound++;

        BlockPos newOrigin = Utils.blockPosFromVec3(hit.getLocation().add(dir.normalize()));

        int castsLeft = Configuration.maxLayerChecks.get() - 1;
        while (blocksFound < Configuration.maxCoverThickness.get() && castsLeft > 0)
        {
            HitResult hit1 = Utils.raycastSearchForBlock(world, newOrigin, dir, Configuration.maxDistToNextLayer.get() + 1);
            if(hit1.getType() == HitResult.Type.MISS) break;

            BlockState state = Utils.getHitBlockState(world, hit1, dir);
            if(!canBlockPassRad(state)) blocksFound++;

            newOrigin = Utils.blockPosFromVec3(hit1.getLocation().add(dir.normalize()));
            if(world.canSeeSky(newOrigin)) break;

            castsLeft--;
        }

        //System.out.println("Blocks found on the way: " + blocksFound);

        return blocksFound;
    }

    /*TODO: this method requires testing*/
    /**Determines if the radiation can freely pass the given block state.*/
    public static boolean canBlockPassRad(BlockState block)
    {
        Material material = block.getMaterial();
        if(material.isLiquid()) return false;
        if(material.isSolidBlocking()) return false;
        return !material.isSolid() || !material.blocksMotion();
    }

    /**@return the rad level from the player capability, or 0 if is absent.*/
    public static int getRadLevel(Player player)
    {
        LazyOptional<IRadiation> lo = getRadCap(player);
        if(lo.isPresent()) return lo.orElse(null).getRadLevel();
        return 0;
    }

    /**Fills the player capability with the specified rad value.*/
    public static void setRadLevel(Player player, int value)
    {
        LazyOptional<IRadiation> lo = getRadCap(player);
        if(lo.isPresent())
        {
            lo.orElse(null).setRadLevel(value);
            if(!player.level.isClientSide())
                PacketHandler.sendToClient(new PacketSyncRadCap(value), (ServerPlayer)player);
        }
    }

    public static LazyOptional<IRadiation> getRadCap(Player player)
    {
        return player.getCapability(RadiationCapability.RADIATION_CAPABILITY);
    }

    /**Determines if the player should see the Radiation Bar on his HUD.*/
    public static boolean shouldRadBarBeRendered(Player player)
    {
        return Configuration.barMode.get().getShouldDispayCheck().apply(player);
    }
}
