package minorMiner;

import com.runemate.game.api.hybrid.GameEvents;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.CommonMath;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.InventoryListener;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class minorMiner extends LoopingBot {
    public StopWatch stopWatch = new StopWatch();
    private int xp_start;

    @Override
    public void onStart(String... args){
        setLoopDelay(1, 1);
        GameEvents.OSRS.NPC_DISMISSER.disable();
        stopWatch.start();
        xp_start = Skill.MINING.getExperience();
    }

    @Override
    public void onLoop() {
        iron2();
    }

    Coordinate currentMiningTile;
    int dropClicks = 0;
    void iron2() {
        Player player = Players.getLocal();
        if (player == null) return;
        Coordinate playerPos = player.getPosition();
        if (playerPos == null) return;
        List<GameObject> ironRocks = GameObjects.getLoadedWithin(
                        new Area.Rectangular(
                                new Coordinate(playerPos.getX() - 1, playerPos.getY() - 1),
                                new Coordinate(playerPos.getX() + 1, playerPos.getY() + 1)
                        ),
                        11364, 11365, 11390, 11391).sortByDistanceFromMouse()
                .stream().filter(r ->
                        Objects.equals(r.getPosition(), new Coordinate(playerPos.getX(), playerPos.getY() + 1)) ||
                                Objects.equals(r.getPosition(), new Coordinate(playerPos.getX() + 1, playerPos.getY())) ||
                                Objects.equals(r.getPosition(), new Coordinate(playerPos.getX(), playerPos.getY() - 1)) ||
                                Objects.equals(r.getPosition(), new Coordinate(playerPos.getX() - 1, playerPos.getY()))
                ).collect(Collectors.toList());
        if (ironRocks.size() >= 2) {
            /* only drop ore if there are not enough empty slots or pending drop clicks
            1 0 true
            1 1
            1 2
            0 0 true
            0 1 true
            0 2
             */
            if (Inventory.getEmptySlots() + dropClicks <= 1) {
                if (!Keyboard.isPressed(0x10)) Keyboard.pressKey(0x10);
                SpriteItem slot0 = Inventory.getItemIn(0);
                if (dropClicks == 0 && slot0 != null && slot0.getId() == 440) {
                    if (slot0.click()) {
                        dropClicks++;
                        currentMiningTile = null; // mining cancelled
                    }
                }
                SpriteItem slot4 = Inventory.getItemIn(4);
                // This handles the edge case where slot1 is empty and slot5 is not, while dropClicks==0
                if ((dropClicks == 1 || slot0 == null) && slot4 != null && slot4.getId() == 440) {
                    if (slot4.click()) {
                        dropClicks++;
                        currentMiningTile = null; // mining cancelled
                    }
                }
                if (dropClicks < 2 && ((slot0 != null && slot0.getId() != 440) || (slot4 != null && slot4.getId() != 440))) {
                    System.out.println("Consider organising inventory so we can efficiently drop slots 1 and 5!");
                    SpriteItem slotX = Inventory.getItems(440).sortByDistanceFromMouse().first();
                    if (slotX != null) {
                        if (slotX.click()) {
                            dropClicks++;
                            currentMiningTile = null; // mining cancelled
                        }
                    } else if (Inventory.isFull()) {
                        stop("Inventory is full but I found no iron ore?");
                    }
                }
            } else {
                // getOrientationAngle sometimes reads the wrong value as if it gets stuck in the middle of a turn, always a multiple of 45 degrees
                if (player.getAnimationId() == -1 && player.getOrientationAsAngle() % 45 == 0 && Mouse.getCrosshairState() == Mouse.CrosshairState.NONE) {
                    // it is more efficient to not click the same rock twice in a row
                    GameObject ironRock = ironRocks.stream().filter(r -> (r.getId() == 11364 || r.getId() == 11365) && !Objects.equals(r.getPosition(), currentMiningTile)).findFirst().orElse(null);
                    if (ironRock != null && ironRock.click()) currentMiningTile = ironRock.getPosition();
                } else {
                    dropClicks = 0;
                    SpriteItem slot0 = Inventory.getItemIn(0);
                    if (slot0 == null || slot0.getId() == 440) {
                        Inventory.getBoundsOf(0).hover();
                    } else {
                        SpriteItem slot4 = Inventory.getItemIn(4);
                        if (slot4 == null || slot4.getId() == 440) {
                            Inventory.getBoundsOf(5).hover();
                        } else {
                            int[] slotNums = IntStream.range(0, 27).toArray();
                            int slotX = Arrays.stream(slotNums).filter(slot -> Inventory.getItemIn(slot) == null).findFirst().orElse(-1);
                            if (slotX != -1) Inventory.getBoundsOf(slotX).hover();
                        }
                    }
                }
            }
        } else {
            stop("Please start while standing next to 2+ iron rocks, I will not walk you there.");
        }
        System.out.println(stopWatch.getRuntimeAsString() + " Mining: " +
                (Skill.MINING.getExperience() - xp_start) + "xp " +
                (int) CommonMath.rate(TimeUnit.HOURS, stopWatch.getRuntime(), (Skill.MINING.getExperience() - xp_start)) + "/h " +
                currentMiningTile
        );
    }
}