package agileElf;

import com.runemate.game.api.hybrid.GameEvents;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.interfaces.*;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.CommonMath;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.LoopingBot;

import java.util.concurrent.TimeUnit;

public class agileElf extends LoopingBot {
    final StopWatch stopWatch = new StopWatch();

    private int agility_xp_start;

    @Override
    public void onStart(String... args) {
        setLoopDelay(142, 842);
        GameEvents.OSRS.NPC_DISMISSER.disable();
        Camera.concurrentlyTurnTo(1.0);
        Camera.setZoom(0.0, 0.042); // why does this hang for seconds when window height is > 991
        stopWatch.start();
        agility_xp_start = Skill.AGILITY.getExperience();
    }

    @Override
    public void onLoop() {
        Player player = Players.getLocal();
        if (player == null) return;
        Coordinate pos = player.getPosition();
        if (pos == null) return;
        GameObject portal = GameObjects.getLoaded("Portal").nearest();
        int randTileOffset = Random.nextInt(0, 4);
        if (Bank.isOpen()) {
            Bank.close(true);
        } else if (pos.getY() >= 3500 && pos.getY() <= 6115 && pos.getPlane() == 0 && !player.isMoving()) {
            GameObject ladder = GameObjects.getLoadedOn(new Coordinate(3254, 6109, 0), "Ladder").nearest();
            if (ladder != null) ladder.click();//.interact("Climb");
        } else if (portal != null && portal.getPosition() != null && portal.getPosition().isReachable()) {
            portal.click();//.interact("Travel");
        } else if (pos.getX() >= 3255 && pos.getX() <= 3257 && pos.getY() >= 6102 && pos.getY() <= 6112 && pos.getPlane() == 2) {
            GameObject tightrope = GameObjects.getLoadedOn(new Coordinate(3257, 6105, 2), "Tightrope").nearest();
            if (tightrope != null) tightrope.click();//.interact("Cross");
        } else if (pos.getX() >= 3272 - randTileOffset && pos.getX() <= 3275 && pos.getY() == 6105 && pos.getPlane() == 2) {
            GameObject chimney = GameObjects.getLoadedOn(new Coordinate(3273, 6107, 2), "Chimney").nearest();
            if (chimney != null) chimney.click();//.interact("Jump");
        } else if (pos.getX() == 3269 && pos.getY() >= 3269 && pos.getY() <= 6115 && pos.getPlane() == 2) {
            GameObject roof = GameObjects.getLoadedOn(new Coordinate(3269, 6116, 2), "Roof edge").nearest();
            if (roof != null) roof.click();//.interact("Jump");
        } else if (pos.getX() >= 3268 && pos.getX() <= 3270 && (pos.getY() == 6116 || pos.getY() == 6117) && pos.getPlane() == 0) {
            GameObject hole = GameObjects.getLoadedOn(new Coordinate(3269, 6118, 0), "Dark hole").nearest();
            if (hole != null) hole.click();//.interact("Enter");
        } else if (pos.getX() >= 2269 && pos.getX() <= 2272 && pos.getY() >= 3389 && pos.getY() <= 3394 && pos.getPlane() == 0) {
            GameObject ladder = GameObjects.getLoadedOn(new Coordinate(2270, 3393, 0), "Ladder").nearest();
            if (ladder != null) ladder.click();//.interact("Climb");
        } else if (pos.getX() >= 2265 && pos.getX() <= 2269 && pos.getY() >= 3389 && pos.getY() <= 3393 && pos.getPlane() == 2) {
            GameObject bridge = GameObjects.getLoadedOn(new Coordinate(2264, 3390, 2), "Rope bridge").nearest();
            if (bridge != null) bridge.click();//.interact("Cross");
        } else if (pos.getX() >= 2254 && pos.getX() <= 2257 + randTileOffset && pos.getY() >= 3386 && pos.getY() <= 3390 && pos.getPlane() == 2) {
            GameObject tightrope = GameObjects.getLoadedOn(new Coordinate(2253, 3390, 2), "Tightrope").nearest();
            if (tightrope != null) tightrope.click();//.interact("Cross");
        } else if (pos.getX() >= 2243 && pos.getX() <= 2247 + randTileOffset && pos.getY() >= 3394 && pos.getY() <= 3398 && pos.getPlane() == 2) {
            GameObject bridge = GameObjects.getLoadedOn(new Coordinate(2246, 3399, 2), "Rope bridge").nearest();
            if (bridge != null) bridge.click();//.interact("Cross");
        } else if (pos.getX() >= 3267 && pos.getX() <= 3276 + randTileOffset && pos.getY() >= 6140 && pos.getY() <= 6147 && pos.getPlane() == 0) {
            GameObject ladder = GameObjects.getLoaded("Ladder").nearest();
            if (ladder != null) ladder.click();//.interact("Climb");
        } else if (pos.getX() >= 2244 && pos.getX() <= 2247 && pos.getY() >= 3406 - randTileOffset && pos.getY() <= 3409 && pos.getPlane() == 2) {
            GameObject tightrope = GameObjects.getLoadedOn(new Coordinate(2243, 3409, 2), "Tightrope").nearest();
            if (tightrope != null) tightrope.click();//.interact("Cross");
        } else if (pos.getX() >= 2250 - randTileOffset && pos.getX() <= 2253 && pos.getY() >= 3415 && pos.getY() <= 3419 && pos.getPlane() == 2) {
            GameObject tightrope = GameObjects.getLoadedOn(new Coordinate(2253, 3418, 2), "Tightrope").nearest();
            if (tightrope != null) tightrope.click();//.interact("Cross");
        } else if (pos.getY() > 3421 && pos.getY() < 3500 && pos.getPlane() == 0) {
            GameObject hole = GameObjects.getLoadedOn(new Coordinate(2258, 3432, 0), "Dark hole").nearest();
            if (hole != null) hole.click();//.interact("Enter");
        } else {
            //System.out.println("noop");
        }
        System.out.println(stopWatch.getRuntimeAsString() +
                " Agility: " + (Skill.AGILITY.getExperience() - agility_xp_start) + " " + (int) CommonMath.rate(TimeUnit.HOURS, stopWatch.getRuntime(), (Skill.AGILITY.getExperience() - agility_xp_start)) + "/h");
    }
}