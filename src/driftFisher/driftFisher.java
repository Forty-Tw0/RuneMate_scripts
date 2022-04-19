package driftFisher;

import com.runemate.game.api.hybrid.GameEvents;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.interfaces.*;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.StopWatch;
import com.runemate.game.api.hybrid.util.calculations.CommonMath;
import com.runemate.game.api.script.framework.LoopingBot;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class driftFisher extends LoopingBot {
    final StopWatch stopWatch = new StopWatch();

    private int fishing_xp_start;
    private int hunter_xp_start;

    @Override
    public void onStart(String... args) {
        setLoopDelay(142, 842);
        GameEvents.OSRS.NPC_DISMISSER.disable();
        Camera.concurrentlyTurnTo(1.0);
        Camera.setZoom(0.0, 0.042); // why does this hang for seconds when window height is > 991
        stopWatch.start();
        fishing_xp_start = Skill.FISHING.getExperience();
        hunter_xp_start = Skill.HUNTER.getExperience();
    }

    int clickedFishTTLms = 60 * 600; // RuneLite defaults to 60 game ticks of 600ms
    Map<Npc, Long> clickedFish = new HashMap<>();
    Pattern tridentName = Pattern.compile(".*trident.*", Pattern.CASE_INSENSITIVE);
    boolean hasTrident = false;
    final int annettesNetsContainerID = 309;
    final int driftNetContainerID = 607;

    @Override
    public void onLoop() {
        long start = System.currentTimeMillis();
        System.out.println("Checking for expired fish tags on: " + clickedFish.size());
        LocatableEntityQueryResults<GameObject> openNets = GameObjects.newQuery().names("Drift net anchors").actions("Harvest").results();
        clickedFish.entrySet().removeIf(entry ->
                // is timed out
                entry.getValue() + clickedFishTTLms < System.currentTimeMillis() ||
                        // is despawned (caught)
                        entry.getKey() == null || !entry.getKey().isValid() ||
                        // is next to the net (possibly failed to catch)
                        openNets.stream().anyMatch(net -> net.getArea() != null && net.getArea().getSurroundingCoordinates().contains(entry.getKey().getPosition()))
        );
        System.out.println("Fish still tagged: " + clickedFish.size());

        SpriteItem numulite = Inventory.newQuery().names("Numulites").results().first();
        if (numulite != null && numulite.getQuantity() < 200) stop("No more numulite.");

        if (!Traversal.isRunEnabled() && Traversal.getRunEnergy() > 10) {
            System.out.println("Turn on run");
            Traversal.toggleRun();
            return;
        } else {
            if (Players.getLocal() == null) return;

            GameObject tunnel = GameObjects.newQuery().names("Tunnel").actions("Pay").results().first();
            if (tunnel != null && tunnel.getPosition() != null && Players.getLocal().getPosition() != null &&
                    tunnel.getPosition().getY() > Players.getLocal().getPosition().getY() &&
                    tunnel.getPosition().getX() <= Players.getLocal().getPosition().getX() &&
                    tunnel.getPosition().getX() + 6 >= Players.getLocal().getPosition().getX()
            ) {
                System.out.println("Quick-Enter Tunnel");
                tunnel.interact("Pay");
            } else {
                GameObject plantDoor = GameObjects.newQuery().names("Plant door").results().first();
                if (plantDoor != null && plantDoor.getPosition() != null && Players.getLocal().getPosition() != null &&
                        plantDoor.getPosition().getX() >= Players.getLocal().getPosition().getX()
                ) {
                    SpriteItem weapon = Equipment.getItemIn(Equipment.Slot.WEAPON);
                    if (weapon != null) {
                        System.out.println("Remove weapon+shield before going through the door");
                        weapon.interact("Remove");
                    } else {
                        SpriteItem trident = Inventory.getItems(tridentName).first();
                        if (trident != null) {
                            hasTrident = true; // At this step, any existing trident would be in the inventory as required by the door. Remember it.
                        }
                        System.out.println("Enter plant door");
                        plantDoor.interact("Navigate");
                    }
                } else {
                    // This is a very efficient if statement with simultaneous variable assignment and null checks
                    SpriteItem tempItem;
                    ItemDefinition tempItemDef;
                    if (hasTrident && (((tempItem = Equipment.getItemIn(Equipment.Slot.WEAPON)) == null) || ( // set tempItem and check if null
                            ((tempItemDef = tempItem.getDefinition()) != null) && // set tempItemDef and check if null
                                    !tempItemDef.getName().toLowerCase().contains("trident") // check if this non-null weapon is a trident
                    ))) {
                        ChatDialog.Continue continueOption = ChatDialog.getContinue();
                        if (continueOption != null) {
                            System.out.println("Continue dialog");
                            continueOption.select(true);
                        } else {
                            ChatDialog.Option wieldTrident = ChatDialog.getOption("I'll steer clear of deep water but wield this anyway.");
                            if (wieldTrident != null) {
                                System.out.println("Approve trident wield dialog");
                                wieldTrident.select(true);
                            } else {
                                SpriteItem trident = Inventory.getItems(tridentName).first();
                                if (trident != null) {
                                    System.out.println("Wield trident");
                                    trident.interact("Wield");
                                } else {
                                    System.out.println("Missing trident?");
                                    hasTrident = false;
                                }
                            }
                        }
                    } else {
                        if (ChatDialog.hasTitle("There are fish in the net.")) {
                            System.out.println("Harvest fish dialog");
                            ChatDialog.Option harvest = ChatDialog.getOption("Harvest the fish and destroy the net.");
                            if (harvest != null) harvest.select(true);
                        } else {
                            InterfaceComponent driftNetContents = Interfaces.newQuery().containers(driftNetContainerID).ids(39780356).results().first();
                            if (driftNetContents != null && driftNetContents.isValid() && driftNetContents.isVisible()) {
                                if (driftNetContents.getChildren(item -> (item.getName() != null && !item.getName().equals("Pufferfish"))).isEmpty()) {
                                    System.out.println("Close bank");
                                    Keyboard.typeKey(27); // close with escape
                                } else {
                                    System.out.println("Bank all");
                                    InterfaceComponent bankAll = Interfaces.newQuery().containers(driftNetContainerID).actions("Bank all").results().first();
                                    if (bankAll != null) bankAll.interact("Bank all");
                                }
                            } else {
                                if (Inventory.newQuery().names("Drift net").unnoted().results().isEmpty()) {
                                    InterfaceComponent annettesNetsNet = Interfaces.newQuery().containers(annettesNetsContainerID).names("Drift net").actions("Withdraw-All").results().first();
                                    if (annettesNetsNet != null) {
                                        if (annettesNetsNet.getContainedItemQuantity() == 0) {
                                            stop("Ran out of nets.");
                                        } else {
                                            System.out.println("Withdraw nets");
                                            annettesNetsNet.interact("Withdraw-All");
                                        }
                                    } else {
                                        System.out.println("Open net storage");
                                        GameObject annette = GameObjects.newQuery().names("Annette").results().first();
                                        if (annette != null) annette.interact("Nets");
                                    }
                                } else {
                                    boolean annettesNetsOpen = !Interfaces.newQuery().containers(annettesNetsContainerID).visible().results().isEmpty();
                                    if (annettesNetsOpen) {
                                        System.out.println("Close net storage");
                                        Keyboard.typeKey(27); // close with escape
                                    } else {
                                        GameObject netAnchor = GameObjects.newQuery().names("Drift net anchors").actions("Set up").results().nearest();
                                        if (netAnchor != null) {
                                            System.out.println("Setup net");
                                            netAnchor.interact("Set up");
                                        } else {
                                            GameObject fullNet = GameObjects.newQuery().names("Drift net (full)").results().nearest();
                                            if (fullNet != null) {
                                                System.out.println("Harvest net");
                                                fullNet.interact("Harvest");
                                            } else {
                                                GroundItem spores = GroundItems.newQuery().names("Seaweed spore").results().nearest();
                                                if (spores != null && !Inventory.isFull()) {
                                                    System.out.println("Pickup spores");
                                                    spores.interact("Take");
                                                } else {
                                                    System.out.println("Chase fish");
                                                    Optional<Npc> fish = Npcs.newQuery().names("Fish shoal").results().sortByDistance().stream().filter(shoal -> !clickedFish.containsKey(shoal)).findFirst();
                                                    if (fish.isPresent() && fish.get().interact("Chase")) {
                                                        System.out.println("Remember clicked fish");
                                                        clickedFish.put(fish.get(), System.currentTimeMillis());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Loop runtime: " + (System.currentTimeMillis() - start));
        System.out.println(stopWatch.getRuntimeAsString() +
                " Fishing: " + (Skill.FISHING.getExperience() - fishing_xp_start) + " " + (int) CommonMath.rate(TimeUnit.HOURS, stopWatch.getRuntime(), (Skill.FISHING.getExperience() - fishing_xp_start)) + "/h" +
                " Hunter: " + (Skill.HUNTER.getExperience() - hunter_xp_start) + " " + (int) CommonMath.rate(TimeUnit.HOURS, stopWatch.getRuntime(), (Skill.HUNTER.getExperience() - hunter_xp_start)) + "/h");
    }
}