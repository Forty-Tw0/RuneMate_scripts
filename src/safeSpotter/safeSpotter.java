package safeSpotter;

import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.framework.LoopingBot;

public class safeSpotter extends LoopingBot{


    @Override
    public void onStart(String... args){
        setLoopDelay(2000);
    }

    @Override
    public void onLoop(){
        System.out.println(com.runemate.game.api.hybrid.region.Region.getBase());
        System.out.println(Players.getLocal().getPosition());
        drawMap(collisionMap(5));
    }

    public Tile[] collisionMap(int radius) {
        Tile[] map = new Tile[(int)Math.pow(radius*2+1, 2)];
        Coordinate center = Players.getLocal().getPosition();
        int[][] flags = com.runemate.game.api.hybrid.region.Region.getCollisionFlags()[center.getPlane()];

        int i = 0;
        for (int y = -radius; y <= radius; y++) {
            for(int x = -radius; x <= radius; x++) {
                int xx = center.getX() + x - com.runemate.game.api.hybrid.region.Region.getBase().getX();
                int yy = center.getY() - y - com.runemate.game.api.hybrid.region.Region.getBase().getY();
                if(xx < flags.length && yy < flags.length && xx >= 0 && yy >= 0) {
                    map[i++] = new Tile(center.getX() + x, center.getY() - y, flags[xx][yy]);
                }
            }
        }
        return map;
    }

    public void drawMap(Tile[] tiles){
        int size = (int)Math.sqrt(tiles.length);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (tiles[y*size+x].nw == 0) System.out.print("\u001B[0m+");
                if (tiles[y*size+x].nw == 1) System.out.print("\u001B[32m+");
                if (tiles[y*size+x].nw == 2) System.out.print("\u001B[31m+");
                if (tiles[y*size+x].n == 0) System.out.print("\u001B[0m---");
                if (tiles[y*size+x].n == 1) System.out.print("\u001B[32m---");
                if (tiles[y*size+x].n == 2) System.out.print("\u001B[31m---");
                if (tiles[y*size+x].ne == 0) System.out.print("\u001B[0m+");
                if (tiles[y*size+x].ne == 1) System.out.print("\u001B[32m+");
                if (tiles[y*size+x].ne == 2) System.out.print("\u001B[31m+");
            }
            System.out.print('\n');
            for (int x = 0; x < size; x++) {
                if (tiles[y*size+x].w == 0) System.out.print("\u001B[0m| ");
                if (tiles[y*size+x].w == 1) System.out.print("\u001B[32m| ");
                if (tiles[y*size+x].w == 2) System.out.print("\u001B[31m| ");
                if (Players.getLocal().getPosition().getX() == tiles[y*size+x].x &&
                        Players.getLocal().getPosition().getY() == tiles[y*size+x].y){
                    if (tiles[y*size+x].solidity == 0) System.out.print("\u001B[36mO");
                    if (tiles[y*size+x].solidity == 1) System.out.print("\u001B[36mO");
                    if (tiles[y*size+x].solidity == 2) System.out.print("\u001B[36mO");
                }else{
                    if (tiles[y*size+x].solidity == 0) System.out.print("\u001B[0m ");
                    if (tiles[y*size+x].solidity == 1) System.out.print("\u001B[32m#");
                    if (tiles[y*size+x].solidity == 2) System.out.print("\u001B[31m#");
                }
                if (tiles[y*size+x].e == 0) System.out.print("\u001B[0m |");
                if (tiles[y*size+x].e == 1) System.out.print("\u001B[32m |");
                if (tiles[y*size+x].e == 2) System.out.print("\u001B[31m |");
            }
            System.out.print('\n');
            for (int x = 0; x < size; x++) {
                if (tiles[y*size+x].sw == 0) System.out.print("\u001B[0m+");
                if (tiles[y*size+x].sw == 1) System.out.print("\u001B[32m+");
                if (tiles[y*size+x].sw == 2) System.out.print("\u001B[31m+");
                if (tiles[y*size+x].s == 0) System.out.print("\u001B[0m---");
                if (tiles[y*size+x].s == 1) System.out.print("\u001B[32m---");
                if (tiles[y*size+x].s == 2) System.out.print("\u001B[31m---");
                if (tiles[y*size+x].se == 0) System.out.print("\u001B[0m+");
                if (tiles[y*size+x].se == 1) System.out.print("\u001B[32m+");
                if (tiles[y*size+x].se == 2) System.out.print("\u001B[31m+");
            }
            System.out.print("\u001B[0m\n");
        }
    }
}
