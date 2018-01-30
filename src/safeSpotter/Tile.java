package safeSpotter;

import com.runemate.game.api.hybrid.region.Region.CollisionFlags;

public class Tile {
    public int x, y;
    public int n = 0, ne = 0, e = 0, se = 0, s = 0, sw = 0, w = 0, nw = 0; /* side/boundary properties
        0 doesn't exist and can be walked across (tiles with nothing on them)
        1 can not be walked through but can be shot through (a fence)
        2 can not be walked through or shot through (a thin wall) */
    public int solidity = 0; /*
        0 can be walked on (tiles with nothing on them)
        1 can not be walked but can be shot over (a table)
        2 can not be walked or shot over (a thick castle wall) */

    // create a new tile object from Region collision flags
    public Tile(int x, int y, int collisionFlags){
        this.x = x;
        this.y = y;

        if((collisionFlags & CollisionFlags.BLOCKING_FLOOR_OBJECT) == CollisionFlags.BLOCKING_FLOOR_OBJECT ||
                (collisionFlags & CollisionFlags.RANGEABLE_OBJECT) == CollisionFlags.RANGEABLE_OBJECT ||
                (collisionFlags & CollisionFlags.OBJECT_TILE) == CollisionFlags.OBJECT_TILE){
            this.solidity = 1;
        }
        if((collisionFlags & CollisionFlags.BLOCKED_TILE) == CollisionFlags.BLOCKED_TILE ||
                (collisionFlags & CollisionFlags.UNSTEPPABLE_OBJECT) == CollisionFlags.UNSTEPPABLE_OBJECT){
            this.solidity = 2;
        }

        //note: the sides are initialised to 0, I'm just going to assume RANGE_BLOCKING boundaries will override the normal boundaries

        if((collisionFlags & CollisionFlags.NORTH_BOUNDARY_OBJECT) == CollisionFlags.NORTH_BOUNDARY_OBJECT) this.n = 1;
        if((collisionFlags & CollisionFlags.NORTH_EAST_BOUNDARY_OBJECT) == CollisionFlags.NORTH_EAST_BOUNDARY_OBJECT) this.ne= 1;
        if((collisionFlags & CollisionFlags.EAST_BOUNDARY_OBJECT) == CollisionFlags.EAST_BOUNDARY_OBJECT) this.e = 1;
        if((collisionFlags & CollisionFlags.SOUTH_EAST_BOUNDARY_OBJECT) == CollisionFlags.SOUTH_EAST_BOUNDARY_OBJECT) this.se = 1;
        if((collisionFlags & CollisionFlags.SOUTH_BOUNDARY_OBJECT) == CollisionFlags.SOUTH_BOUNDARY_OBJECT) this.s = 1;
        if((collisionFlags & CollisionFlags.SOUTH_WEST_BOUNDARY_OBJECT) == CollisionFlags.SOUTH_WEST_BOUNDARY_OBJECT) this.sw = 1;
        if((collisionFlags & CollisionFlags.WEST_BOUNDARY_OBJECT) == CollisionFlags.WEST_BOUNDARY_OBJECT) this.w = 1;
        if((collisionFlags & CollisionFlags.NORTH_WEST_BOUNDARY_OBJECT) == CollisionFlags.NORTH_WEST_BOUNDARY_OBJECT) this.nw = 1;

        if((collisionFlags & CollisionFlags.RANGE_BLOCKING_NORTH_BOUNDARY_OBJECT) == CollisionFlags.RANGE_BLOCKING_NORTH_BOUNDARY_OBJECT) this.n = 2;
        if((collisionFlags & CollisionFlags.RANGE_BLOCKING_NORTH_EAST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_BLOCKING_NORTH_EAST_BOUNDARY_OBJECT) this.ne= 2;
        if((collisionFlags & CollisionFlags.RANGE_BLOCKING_EAST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_BLOCKING_EAST_BOUNDARY_OBJECT) this.e = 2;
        if((collisionFlags & CollisionFlags.RANGE_BLOCKING_SOUTH_EAST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_BLOCKING_SOUTH_EAST_BOUNDARY_OBJECT) this.se = 2;
        if((collisionFlags & CollisionFlags.RANGE_BLOCKING_SOUTH_BOUNDARY_OBJECT) == CollisionFlags.RANGE_BLOCKING_SOUTH_BOUNDARY_OBJECT) this.s = 2;
        if((collisionFlags & CollisionFlags.RANGE_BLOCKING_SOUTH_WEST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_BLOCKING_SOUTH_WEST_BOUNDARY_OBJECT) this.sw = 2;
        if((collisionFlags & CollisionFlags.RANGE_BLOCKING_WEST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_BLOCKING_WEST_BOUNDARY_OBJECT) this.w = 2;
        if((collisionFlags & CollisionFlags.RANGE_BLOCKING_NORTH_WEST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_BLOCKING_NORTH_WEST_BOUNDARY_OBJECT) this.nw = 2;

        if((collisionFlags & CollisionFlags.PADDING) == CollisionFlags.PADDING ||
                (collisionFlags & CollisionFlags.RANGE_ALLOWING_NORTH_BOUNDARY_OBJECT) == CollisionFlags.RANGE_ALLOWING_NORTH_BOUNDARY_OBJECT ||
                (collisionFlags & CollisionFlags.RANGE_ALLOWING_NORTH_EAST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_ALLOWING_NORTH_EAST_BOUNDARY_OBJECT ||
                (collisionFlags & CollisionFlags.RANGE_ALLOWING_EAST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_ALLOWING_EAST_BOUNDARY_OBJECT ||
                (collisionFlags & CollisionFlags.RANGE_ALLOWING_SOUTH_EAST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_ALLOWING_SOUTH_EAST_BOUNDARY_OBJECT ||
                (collisionFlags & CollisionFlags.RANGE_ALLOWING_SOUTH_BOUNDARY_OBJECT) == CollisionFlags.RANGE_ALLOWING_SOUTH_BOUNDARY_OBJECT ||
                (collisionFlags & CollisionFlags.RANGE_ALLOWING_SOUTH_WEST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_ALLOWING_SOUTH_WEST_BOUNDARY_OBJECT ||
                (collisionFlags & CollisionFlags.RANGE_ALLOWING_WEST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_ALLOWING_WEST_BOUNDARY_OBJECT ||
                (collisionFlags & CollisionFlags.RANGE_ALLOWING_NORTH_WEST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_ALLOWING_NORTH_WEST_BOUNDARY_OBJECT
                ) {
            //I don't know what these do
        }
    }
}
