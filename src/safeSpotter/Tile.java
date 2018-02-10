package safeSpotter;

import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.region.Region.CollisionFlags;

public class Tile{
    Coordinate coordinate;
    private int collisionFlags;

    public Tile(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.collisionFlags = coordinate.getCollisionFlag();
    }

    /*
        0 can be walked on (tiles with nothing on them)
        1 can not be walked but can be shot over (a table)
        2 can not be walked or shot over (a thick castle wall)
    */
    public int getSolidity(){
        int solidity = 0;
        if ((collisionFlags& CollisionFlags.BLOCKING_FLOOR_OBJECT) == CollisionFlags.BLOCKING_FLOOR_OBJECT ||
                (collisionFlags& CollisionFlags.RANGEABLE_OBJECT) == CollisionFlags.RANGEABLE_OBJECT ||
                (collisionFlags& CollisionFlags.OBJECT_TILE) == CollisionFlags.OBJECT_TILE) {
            solidity = 1;
        }
        if ((collisionFlags& CollisionFlags.BLOCKED_TILE) == CollisionFlags.BLOCKED_TILE ||
                (collisionFlags& CollisionFlags.UNSTEPPABLE_OBJECT) == CollisionFlags.UNSTEPPABLE_OBJECT) {
            solidity = 2;
        }
        return solidity;
    }

    /* side/boundary properties
        0 doesn't exist and can be walked across (tiles with nothing on them)
        1 can not be walked through but can be shot through (a fence)
        2 can not be walked through or shot through (a thin wall)
    */
    public int getN(){
        int n = 0;
        if ((collisionFlags& CollisionFlags.NORTH_BOUNDARY_OBJECT) == CollisionFlags.NORTH_BOUNDARY_OBJECT)
            n = 1;
        if ((collisionFlags& CollisionFlags.RANGE_BLOCKING_NORTH_BOUNDARY_OBJECT) == CollisionFlags.RANGE_BLOCKING_NORTH_BOUNDARY_OBJECT)
            n = 2;
        return n;
    }
    public int getNE(){
        int ne = 0;
        if ((collisionFlags& CollisionFlags.NORTH_EAST_BOUNDARY_OBJECT) == CollisionFlags.NORTH_EAST_BOUNDARY_OBJECT)
            ne = 1;
        if ((collisionFlags& CollisionFlags.RANGE_BLOCKING_NORTH_EAST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_BLOCKING_NORTH_EAST_BOUNDARY_OBJECT)
            ne = 2;
        return ne;
    }
    public int getE(){
        int e = 0;
        if ((collisionFlags& CollisionFlags.EAST_BOUNDARY_OBJECT) == CollisionFlags.EAST_BOUNDARY_OBJECT)
            e = 1;
        if ((collisionFlags& CollisionFlags.RANGE_BLOCKING_EAST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_BLOCKING_EAST_BOUNDARY_OBJECT)
            e = 2;
        return e;
    }
    public int getSE(){
        int se = 0;
        if ((collisionFlags& CollisionFlags.SOUTH_EAST_BOUNDARY_OBJECT) == CollisionFlags.SOUTH_EAST_BOUNDARY_OBJECT)
            se = 1;
        if ((collisionFlags& CollisionFlags.RANGE_BLOCKING_SOUTH_EAST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_BLOCKING_SOUTH_EAST_BOUNDARY_OBJECT)
            se = 2;
        return se;
    }
    public int getS(){
        int s = 0;
        if ((collisionFlags& CollisionFlags.SOUTH_BOUNDARY_OBJECT) == CollisionFlags.SOUTH_BOUNDARY_OBJECT)
            s = 1;
        if ((collisionFlags& CollisionFlags.RANGE_BLOCKING_SOUTH_BOUNDARY_OBJECT) == CollisionFlags.RANGE_BLOCKING_SOUTH_BOUNDARY_OBJECT)
            s = 2;
        return s;
    }
    public int getSW(){
        int sw = 0;
        if ((collisionFlags& CollisionFlags.SOUTH_WEST_BOUNDARY_OBJECT) == CollisionFlags.SOUTH_WEST_BOUNDARY_OBJECT)
            sw = 1;
        if ((collisionFlags& CollisionFlags.RANGE_BLOCKING_SOUTH_WEST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_BLOCKING_SOUTH_WEST_BOUNDARY_OBJECT)
            sw = 2;
        return sw;
    }
    public int getW(){
        int w = 0;
        if ((collisionFlags& CollisionFlags.WEST_BOUNDARY_OBJECT) == CollisionFlags.WEST_BOUNDARY_OBJECT)
            w = 1;
        if ((collisionFlags& CollisionFlags.RANGE_BLOCKING_WEST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_BLOCKING_WEST_BOUNDARY_OBJECT)
            w = 2;
        return w;
    }
    public int getNW(){
        int nw = 0;
        if ((collisionFlags& CollisionFlags.NORTH_WEST_BOUNDARY_OBJECT) == CollisionFlags.NORTH_WEST_BOUNDARY_OBJECT)
            nw = 1;
        if ((collisionFlags& CollisionFlags.RANGE_BLOCKING_NORTH_WEST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_BLOCKING_NORTH_WEST_BOUNDARY_OBJECT)
            nw = 2;
        return nw;
    }

    //I think these are out of bounds
    public int getUnused() {
        if ((collisionFlags& CollisionFlags.PADDING) == CollisionFlags.PADDING ||
                (collisionFlags& CollisionFlags.RANGE_ALLOWING_NORTH_BOUNDARY_OBJECT) == CollisionFlags.RANGE_ALLOWING_NORTH_BOUNDARY_OBJECT ||
                (collisionFlags& CollisionFlags.RANGE_ALLOWING_NORTH_EAST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_ALLOWING_NORTH_EAST_BOUNDARY_OBJECT ||
                (collisionFlags& CollisionFlags.RANGE_ALLOWING_EAST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_ALLOWING_EAST_BOUNDARY_OBJECT ||
                (collisionFlags& CollisionFlags.RANGE_ALLOWING_SOUTH_EAST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_ALLOWING_SOUTH_EAST_BOUNDARY_OBJECT ||
                (collisionFlags& CollisionFlags.RANGE_ALLOWING_SOUTH_BOUNDARY_OBJECT) == CollisionFlags.RANGE_ALLOWING_SOUTH_BOUNDARY_OBJECT ||
                (collisionFlags& CollisionFlags.RANGE_ALLOWING_SOUTH_WEST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_ALLOWING_SOUTH_WEST_BOUNDARY_OBJECT ||
                (collisionFlags& CollisionFlags.RANGE_ALLOWING_WEST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_ALLOWING_WEST_BOUNDARY_OBJECT ||
                (collisionFlags& CollisionFlags.RANGE_ALLOWING_NORTH_WEST_BOUNDARY_OBJECT) == CollisionFlags.RANGE_ALLOWING_NORTH_WEST_BOUNDARY_OBJECT
                ) {
            return 1;
        }else{
            return 0;
        }
    }

    //an npc's position is tied to the root tile, think of it as though it's legs are here
    public int containsNPC(){
        int type = 0;
        if (!Npcs.newQuery().on(coordinate).targeting(Players.getLocal()).actions("Attack")
                .filter(npc -> npc.getHealthGauge() == null ||
                        (npc.getHealthGauge() != null && npc.getHealthGauge().getPercent() != 0)).results().isEmpty()){
            type = 1; //this is the root tile, this is where the npc's position technically is
        } else if (!Npcs.newQuery().targeting(Players.getLocal()).actions("Attack")
                .filter(npc -> npc.getArea().getCoordinates().contains(coordinate) && npc.getHealthGauge() == null ||
                        (npc.getHealthGauge() != null && npc.getHealthGauge().getPercent() != 0)).results().isEmpty()){
            //type = 2; //this is part of the npc body which extends from the root
        }
        return type;
    }
}
