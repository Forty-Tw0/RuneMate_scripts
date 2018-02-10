package safeSpotter;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.framework.LoopingBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class safeSpotter extends LoopingBot{
    @Override
    public void onStart(String... args){
        setLoopDelay(200);
    }

    //When a NPC is chasing you it will first move diagonally until it aligns with your character horizontally or vertically.
    //If the NPC is 2-squared or even more the NPC will try to align the south-west corner.
    //http://i.imgur.com/A8SyuPS.png

    @Override
    public void onLoop(){
        Tile[] map = collisionMap(5);
        List<int[]> npcTiles = getAllNpcTiles(Npcs.newQuery().targeting(Players.getLocal()).actions("Attack")
                .filter(npc -> npc.getHealthGauge() == null ||
                (npc.getHealthGauge() != null && npc.getHealthGauge().getPercent() != 0)).results());

        drawMap(map, npcTiles);

        /**/
        if (Players.getLocal() != null) {
            if (Players.getLocal().getTarget() == null) {
                if(!Npcs.newQuery().targeting(Players.getLocal()).filter(npc -> npc.getHealthGauge() == null || (npc.getHealthGauge() != null && npc.getHealthGauge().getPercent() != 0)).results().isEmpty()){
                    System.out.println("Attacking nearest aggro'd target.");
                    Npcs.newQuery().targeting(Players.getLocal()).filter(npc -> npc.getHealthGauge() == null || (npc.getHealthGauge() != null && npc.getHealthGauge().getPercent() != 0)).results().nearest().interact("Attack");
                } else {
                    System.out.println("Attacking nearest new target");
                    //area.absolute(safeTiles)
                    LocatableEntityQueryResults<Npc> targets = Npcs.newQuery().names("Cave horror").actions("Attack").filter(
                            npc -> npc.getHealthGauge() == null ||
                                    (npc.getHealthGauge() != null && npc.getHealthGauge().getPercent() != 0)
                    ).results().sortByDistance();
                    Npc target = targets.nearest();
                    if (target != null) target.interact("Attack");
                }
            } else {
                System.out.println("In combat.");
                //ensure we are still safe, if not move back to a safe spot - loop this since npcs can drag us out
                Tile[] safeTiles = findSafeSpots(map, Players.getLocal().getPosition(),
                        Npcs.newQuery().names("Cave horror").actions("Attack").filter(
                                npc -> npc.getHealthGauge() == null ||
                                        (npc.getHealthGauge() != null && npc.getHealthGauge().getPercent() != 0)
                        ).results());
                if(!tilesContain(safeTiles, Players.getLocal().getPosition())){
                    //safeTiles.nearestTo().click();
                }

            }
        }
        /**/
    }

    public List<int[]> getNpcArea(Npc npc){
        int size = npc.getDefinition().getAreaEdgeLength();
        List<int[]> tiles = new ArrayList<>();
        for(int y = 0; y < size; y++){
            for(int x = 0; x < size; x++){
                tiles.add(new int[]{npc.getPosition().getX() + x - size + 1, npc.getPosition().getY() - y});
            }
        }
        return tiles;
    }

    public List<int[]> getAllNpcTiles(LocatableEntityQueryResults<Npc> npcs){
        List<int[]> tiles = new ArrayList<>();
        npcs.forEach((npc) -> tiles.addAll(getNpcArea(npc)));
        return tiles;
    }

    public Tile[] collisionMap(int radius) {
        Tile[] map = new Tile[(int)Math.pow(radius*2+1, 2)];

        int i = 0;
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                int xx = Players.getLocal().getPosition().getX() + x;
                int yy = Players.getLocal().getPosition().getY() - y;
                Coordinate c = com.runemate.game.api.hybrid.region.Region.getArea().getCoordinates().stream().filter(
                    coordinate -> coordinate.getX() == xx && coordinate.getY() == yy).collect(Collectors.toList()).get(0);
                map[i++] = new Tile(c);
            }
        }
        return map;
    }

    public void drawMap(Tile[] tiles, List<int[]> npcTiles) {
        int size = (int) Math.sqrt(tiles.length);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Tile tile = tiles[y * size + x];
                if (y == 0 || y == size - 1) {
                    if (tile.getNW() == 0) System.out.print("\u001B[0m+");
                    if (tile.getNW() == 1) System.out.print("\u001B[32m+");
                    if (tile.getNW() == 2) System.out.print("\u001B[31m+");
                    if (tile.getN() == 0) System.out.print("\u001B[0m---");
                    if (tile.getN() == 1) System.out.print("\u001B[32m---");
                    if (tile.getN() == 2) System.out.print("\u001B[31m---");
                    if (tile.getNE() == 0) System.out.print("\u001B[0m+");
                    if (tile.getNE() == 1) System.out.print("\u001B[32m+");
                    if (tile.getNE() == 2) System.out.print("\u001B[31m+");
                } else {
                    Tile tileNear = tiles[y * size + x - size];
                    if ((tile.getNW() > tileNear.getSW() ? tile.getNW() : tileNear.getSW()) == 0) System.out.print("\u001B[0m+");
                    if ((tile.getNW() > tileNear.getSW() ? tile.getNW() : tileNear.getSW()) == 1) System.out.print("\u001B[32m+");
                    if ((tile.getNW() > tileNear.getSW() ? tile.getNW() : tileNear.getSW()) == 2) System.out.print("\u001B[31m+");
                    if ((tile.getN() > tileNear.getS() ? tile.getN() : tileNear.getS()) == 0) System.out.print("\u001B[0m---");
                    if ((tile.getN() > tileNear.getS() ? tile.getN() : tileNear.getS()) == 1) System.out.print("\u001B[32m---");
                    if ((tile.getN() > tileNear.getS() ? tile.getN() : tileNear.getS()) == 2) System.out.print("\u001B[31m---");
                    if ((tile.getNE() > tileNear.getSE() ? tile.getNE() : tileNear.getSE()) == 0) System.out.print("\u001B[0m+");
                    if ((tile.getNE() > tileNear.getSE() ? tile.getNE() : tileNear.getSE()) == 1) System.out.print("\u001B[32m+");
                    if ((tile.getNE() > tileNear.getSE() ? tile.getNE() : tileNear.getSE()) == 2) System.out.print("\u001B[31m+");
                }
            }
            System.out.print("\u001B[0m\n");
            for (int x = 0; x < size; x++) {
                Tile tile = tiles[y * size + x];
                if (tile.getW() == 0) System.out.print("\u001B[0m| ");
                if (tile.getW() == 1) System.out.print("\u001B[32m| ");
                if (tile.getW() == 2) System.out.print("\u001B[31m| ");
                if(tile.getUnused() == 1 || tile.getSolidity() != 0) {
                    if (tile.getUnused() == 1) System.out.print("\u001B[31m!");
                    if (tile.getSolidity() == 1) System.out.print("\u001B[32m#");
                    if (tile.getSolidity() == 2) System.out.print("\u001B[31m#");
                } else if (Players.getLocal().getPosition().getX() == tile.coordinate.getX() &&
                        Players.getLocal().getPosition().getY() == tile.coordinate.getY()) {
                    System.out.print("\u001B[36mO");
                } else if(isInList(npcTiles, new int[]{tile.coordinate.getX(), tile.coordinate.getY()})) {
                    System.out.print("\u001B[33m#");
                /*} else if (tile.containsNPC() == 1) {
                    System.out.print("\u001B[93m#");
                } else if (tile.containsNPC() == 2) {
                    System.out.print("\u001B[33m#");*/
                } else {
                    System.out.print("\u001B[0m ");
                }
                if (tile.getE() == 0) System.out.print("\u001B[0m |");
                if (tile.getE() == 1) System.out.print("\u001B[32m |");
                if (tile.getE() == 2) System.out.print("\u001B[31m |");
            }
            System.out.print("\u001B[0m\n");
            if (y == size - 1) {
                for (int x = 0; x < size; x++) {
                    Tile tile = tiles[y * size + x];
                    if (tile.getSW() == 0) System.out.print("\u001B[0m+");
                    if (tile.getSW() == 1) System.out.print("\u001B[32m+");
                    if (tile.getSW() == 2) System.out.print("\u001B[31m+");
                    if (tile.getS() == 0) System.out.print("\u001B[0m---");
                    if (tile.getS() == 1) System.out.print("\u001B[32m---");
                    if (tile.getS() == 2) System.out.print("\u001B[31m---");
                    if (tile.getSE() == 0) System.out.print("\u001B[0m+");
                    if (tile.getSE() == 1) System.out.print("\u001B[32m+");
                    if (tile.getSE() == 2) System.out.print("\u001B[31m+");
                }
                System.out.print("\u001B[0m\n");
            }
        }
    }

    public static boolean isInList(List<int[]> list, int[] candidate){
        for(int[] item : list){
            if(Arrays.equals(item, candidate)){
                return true;
            }
        }
        return false;
    }

    public boolean tilesContain(Tile[] tiles, Coordinate c){
        for (Tile tile: tiles) {
            if(tile.coordinate.getY() == c.getX() && tile.coordinate.getY() == c.getY()){
                return true;
            }
        }
        return false;
    }

    public Tile[] findSafeSpots(Tile[] map, Coordinate player, LocatableEntityQueryResults<Npc> targets){
        Tile[] tiles = map;
        return tiles;
    }
}
