package agileElf;

import com.runemate.game.api.hybrid.GameEvents;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.Screen;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.Varbits;
import com.runemate.game.api.hybrid.local.hud.InteractableRectangle;
import com.runemate.game.api.hybrid.local.hud.interfaces.*;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.osrs.local.hud.interfaces.Magic;
import com.runemate.game.api.script.framework.LoopingBot;

import java.util.regex.Pattern;

public class agileElf extends LoopingBot {
    @Override
    public void onStart(String... args) {
        setLoopDelay(142, 842);
        GameEvents.OSRS.NPC_DISMISSER.disable();
        Camera.concurrentlyTurnTo(1.0);
        Camera.setZoom(0.0, 0.042); // why does this hang for seconds when window height is > 991
    }

    @Override
    public void onLoop() {
        Coordinate pos = Players.getLocal().getPosition();
        System.out.println(pos);
        GameObject portal = GameObjects.getLoaded("Portal").nearest();
        int randTileOffset = Random.nextInt(0, 4);
        if (Bank.isOpen()) {
            Bank.close(true);
        } else if (pos.getY() >= 3500 && pos.getY() <= 6115 && pos.getPlane() == 0) {
            GameObject ladder = GameObjects.getLoadedOn(new Coordinate(3254, 6109, 0), "Ladder").nearest();
            if (ladder != null) ladder.interact("Climb");
        } else if (portal != null && portal.getPosition().isReachable()) {
            portal.interact("Travel");
        } else if (pos.getX() >= 3255 && pos.getX() <= 3257 && pos.getY() >= 6102 && pos.getY() <= 6112 && pos.getPlane() == 2) {
            GameObject tightrope = GameObjects.getLoadedOn(new Coordinate(3257, 6105, 2), "Tightrope").nearest();
            if (tightrope != null) tightrope.interact("Cross");
        } else if (pos.getX() >= 3272 - randTileOffset && pos.getX() <= 3275 && pos.getY() == 6105 && pos.getPlane() == 2) {
            GameObject chimney = GameObjects.getLoadedOn(new Coordinate(3273, 6107, 2), "Chimney").nearest();
            if (chimney != null) chimney.interact("Jump");
        } else if (pos.getX() == 3269 && pos.getY() >= 3269 && pos.getY() <= 6115 && pos.getPlane() == 2) {
            GameObject roof = GameObjects.getLoadedOn(new Coordinate(3269, 6116, 2), "Roof edge").nearest();
            if (roof != null) roof.interact("Jump");
        } else if (pos.getX() >= 3268 && pos.getX() <= 3270 && (pos.getY() == 6116 || pos.getY() == 6117) && pos.getPlane() == 0) {
            GameObject hole = GameObjects.getLoadedOn(new Coordinate(3269, 6118, 0), "Dark hole").nearest();
            if (hole != null) hole.interact("Enter");
        } else if (pos.getX() >= 2269 && pos.getX() <= 2272 && pos.getY() >= 3389 && pos.getY() <= 3394 && pos.getPlane() == 0) {
            GameObject ladder = GameObjects.getLoadedOn(new Coordinate(2270, 3393, 0), "Ladder").nearest();
            if (ladder != null) ladder.interact("Climb");
        } else if (pos.getX() >= 2265 && pos.getX() <= 2269 && pos.getY() >= 3389 && pos.getY() <= 3393 && pos.getPlane() == 2) {
            GameObject bridge = GameObjects.getLoadedOn(new Coordinate(2264, 3390, 2), "Rope bridge").nearest();
            if (bridge != null) bridge.interact("Cross");
        } else if (pos.getX() >= 2254 && pos.getX() <= 2257 + randTileOffset && pos.getY() >= 3386 && pos.getY() <= 3390 && pos.getPlane() == 2) {
            GameObject tightrope = GameObjects.getLoadedOn(new Coordinate(2253, 3390, 2), "Tightrope").nearest();
            if (tightrope != null) tightrope.interact("Cross");
        } else if (pos.getX() >= 2243 && pos.getX() <= 2247 + randTileOffset && pos.getY() >= 3394 && pos.getY() <= 3398 && pos.getPlane() == 2) {
            GameObject bridge = GameObjects.getLoadedOn(new Coordinate(2246, 3399, 2), "Rope bridge").nearest();
            if (bridge != null) bridge.interact("Cross");
        } else if (pos.getX() >= 3267 && pos.getX() <= 3276 + randTileOffset && pos.getY() >= 6140 && pos.getY() <= 6147 && pos.getPlane() == 0) {
            GameObject ladder = GameObjects.getLoaded("Ladder").nearest();
            if (ladder != null) ladder.interact("Climb");
        } else if (pos.getX() >= 2244 && pos.getX() <= 2247 && pos.getY() >= 3406 - randTileOffset && pos.getY() <= 3409 && pos.getPlane() == 2) {
            GameObject tightrope = GameObjects.getLoadedOn(new Coordinate(2243, 3409, 2), "Tightrope").nearest();
            if (tightrope != null) tightrope.interact("Cross");
        } else if (pos.getX() >= 2250 - randTileOffset && pos.getX() <= 2253 && pos.getY() >= 3415 && pos.getY() <= 3419 && pos.getPlane() == 2) {
            GameObject tightrope = GameObjects.getLoadedOn(new Coordinate(2253, 3418, 2), "Tightrope").nearest();
            if (tightrope != null) tightrope.interact("Cross");
        } else if (pos.getY() > 3421 && pos.getY() < 3500 && pos.getPlane() == 0) {
            GameObject hole = GameObjects.getLoadedOn(new Coordinate(2258, 3432, 0), "Dark hole").nearest();
            if (hole != null) hole.interact("Enter");
        } else {
            System.out.println("noop");
        }
    }
}