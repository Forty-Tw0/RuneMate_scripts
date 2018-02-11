package safeSpotter;

import com.runemate.game.api.hybrid.entities.Npc;

import java.util.ArrayList;
import java.util.List;

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
        return new int[]{npc.getPosition().getX() - npc.getDefinition().getAreaEdgeLength() + 1,
                npc.getPosition().getY() - npc.getDefinition().getAreaEdgeLength() + 1};
    }
}
