package safeSpotter;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.location.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class npcWrapper {
    Npc npc;

    public npcWrapper(Npc npc){
        this.npc = npc;
    }

    public List<int[]> getNpcArea(){
        int size = npc.getDefinition().getAreaEdgeLength();
        List<int[]> tiles = new ArrayList<>();
        for(int y = 0; y < size; y++){
            for(int x = 0; x < size; x++){
                tiles.add(new int[]{npc.getPosition().getX() + x - size + 1, npc.getPosition().getY() - y});
            }
        }
        return tiles;
    }

    public int[] getRealPosition(){
        //an NPC will try to align with a player on it's SW corner, this is where it's loot drops as well
        return new int[]{npc.getPosition().getX() - npc.getDefinition().getAreaEdgeLength() + 1,
                npc.getPosition().getY() - npc.getDefinition().getAreaEdgeLength() + 1};
    }

    public boolean hasPathToTile(int[] tileXY, Tile[] map){
        int[] xy = getRealPosition();
        //When a NPC is chasing you it will first move diagonally until it aligns with your character horizontally or vertically.
        //http://i.imgur.com/A8SyuPS.png

        //npc will try to move EW before moving NS, if the npc is standing on the player it will be random
        //https://clips.twitch.tv/KitschyMuddyWerewolfDxCat

        //if the npc is on the player
        if (xy[0] == tileXY[0] && xy[1] == tileXY[1]) return false;

        //Extrapolate NPC location diagonally until it is aligned horizontally or vertically
        while(xy[0] != tileXY[0] && xy[1] != tileXY[1]){
            if (xy[0] > tileXY[0]) {
                if (xy[1] > tileXY[1]) {
                    //move WS

                } else {
                    //move WN
                }
            } else {
                if (xy[1] > tileXY[1]) {
                    //move ES
                } else {
                    //move WS
                }
            }
        }

        //check every tile in between the NPC and target tile for obstructions
        if (xy[0] == tileXY[0]){
            //NPC aligned vertically
            if(xy[1] < tileXY[1]){
                for (int y = npc.getPosition().getY(); y < tileXY[1]; y++){
                    for (int x = xy[0]; x < npc.getPosition().getX(); x++){
                        int finalX = x;
                        int finalY = y;
                        if (new Tile(com.runemate.game.api.hybrid.region.Region.getArea().getCoordinates().stream().filter(
                                coordinate -> coordinate.getX() == finalX && coordinate.getY() == finalY)
                                .collect(Collectors.toList()).get(0)).getSolidity() != 0) return false;
                    }
                }
                return true;
                //move north
            } else {
                //move south
            }
        } else if(xy[1] == tileXY[1]){
            //NPC aligned horizontally
            if(xy[0] < tileXY[0]){
                //move east
            } else {
                //move west
            }
        }
        return false;//temp##############
    }

    public boolean isInRange() {
        //check if the npc is in range of the current weapon
        //can we shoot through obstacles?
        return true;
    }
}
