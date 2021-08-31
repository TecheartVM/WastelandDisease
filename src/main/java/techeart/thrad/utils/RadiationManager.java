package techeart.thrad.utils;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
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

import java.util.Random;

public class RadiationManager
{
    public static boolean tickRadiation(Player player)
    {
        BlockPos headPos = player.blockPosition().above();
        int exposure = getExposureAtPos(player.level, headPos);

        if(exposure < 0)
        {
            //TODO: decrease player rad level by 1 with <Configuration.RAD_LEVEL_DECREASE_CHANCE> chance
        }
        else
        {
            //TODO: apply Exposure effect with <exposure> amplifier value to a player
        }

        return true;
    }

    public static int getExposureAtPos(Level world, BlockPos pos)
    {
        if(pos.getY() < Configuration.MIN_Y_LEVEL_AFFECTED
        || pos.getY() > Configuration.MAX_Y_LEVEL_AFFECTED)
            return Configuration.MAX_EXPOSURE_LEVEL;

        /*TODO: check for dimension and biome here*/

      String dimId = world.dimension().location().toString();
      System.out.println(dimId);
      boolean flag = Configuration.dimensionsList.get().contains(dimId);
      System.out.println(flag);

      System.out.println(Configuration.dimensionsList.get());

        for (String s : Configuration.dimensionsList.get())
        {
            if(s.equals(dimId))
            {
                flag = true;
                break;
            }
        }
      if(flag) { if(Configuration.dimensionsBlacklist.get()) return -1; }
      else if(!Configuration.dimensionsBlacklist.get()) return -1;

        if(world.canSeeSky(pos)) return Configuration.MAX_EXPOSURE_LEVEL;

        int coverLayers = getProtectiveLayers(world, pos, false);
        return Configuration.MAX_EXPOSURE_LEVEL - coverLayers;


    }

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

        System.out.println("Casting along vector " + dir);

        HitResult hit = Utils.raycastSearchForBlock(world, castOrigin, dir, Configuration.COVER_SEARCH_RADIUS);
        if(hit.getType() == HitResult.Type.MISS) return 0;

        BlockState hitState = Utils.getHitBlockState(world, hit, dir);
        if(!canBlockPassRad(hitState)) blocksFound++;

        BlockPos newOrigin = Utils.blockPosFromVec3(hit.getLocation().add(dir.normalize()));

        int castsLeft = Configuration.MAX_LAYER_CHECKS - 1;
        while (blocksFound < Configuration.MAX_COVER_THICKNESS && castsLeft > 0)
        {
            HitResult hit1 = Utils.raycastSearchForBlock(world, newOrigin, dir, Configuration.MAX_DIST_TO_NEXT_LAYER + 1);
            if(hit1.getType() == HitResult.Type.MISS) break;

            BlockState state = Utils.getHitBlockState(world, hit1, dir);
            if(!canBlockPassRad(state)) blocksFound++;

            newOrigin = Utils.blockPosFromVec3(hit1.getLocation().add(dir.normalize()));
            if(world.canSeeSky(newOrigin)) break;

            castsLeft--;
        }

        System.out.println("Total casts: " + (Configuration.MAX_LAYER_CHECKS - castsLeft));
        System.out.println("Blocks found on the way: " + blocksFound);

        return blocksFound;
    }

    /*TODO: this method requires testing*/
    public static boolean canBlockPassRad(BlockState block)
    {
        Material material = block.getMaterial();
        if(material.isLiquid()) return false;
        if(material.isSolidBlocking()) return false;
        return !material.isSolid() || !material.blocksMotion();
    }

    public static int getRadLevel(Player player)
    {
        LazyOptional<IRadiation> lo = getRadCap(player);
        if(lo.isPresent()) return lo.orElse(null).getRadLevel();
        return 0;
    }

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

    public static boolean shouldRadBarBeRendered(AbstractClientPlayer player)
    {
        for (InteractionHand hand : InteractionHand.values())
            if(player.getItemInHand(hand).getItem() == RegistryHandler.RAD_METER.get()) return true;
        return false;
    }
}
