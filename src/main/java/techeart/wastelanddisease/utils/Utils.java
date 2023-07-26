package techeart.wastelanddisease.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class Utils
{
    /**Performs a raycast from the given {@link BlockPos} in the given direction for a given distance.
     * @return the {@link HitResult} of detected block.
     * */
    public static HitResult raycastSearchForBlock(Level world, BlockPos from, Vec3 dir, int dist)
    {
        Vec3 p1 = new Vec3(from.getX() + 0.5d, from.getY() + 0.5d, from.getZ() + 0.5d);
        Vec3 p2 = p1.add(dir.normalize().scale(dist));
        return world.clip(
                new ClipContext(p1, p2, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, null)
        );
    }

    public static BlockState getHitBlockState(@Nonnull Level world, @Nonnull HitResult hit, @Nonnull Vec3 dir)
    {
        Vec3 hitPoint = hit.getLocation().add(dir.normalize().scale(0.1d));
        return world.getBlockState(blockPosFromVec3(hitPoint));
    }

    public static BlockPos blockPosFromVec3(Vec3 vec) { return new BlockPos(vec.x, vec.y, vec.z); }
}
