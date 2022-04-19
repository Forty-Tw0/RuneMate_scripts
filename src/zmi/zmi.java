package zmi;

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

public class zmi extends LoopingBot {
    @Override
    public void onStart(String... args){
        setLoopDelay(142, 842);
        GameEvents.OSRS.NPC_DISMISSER.disable();
    }

    @Override
    public void onLoop() {
        //still need to figure out how to close the world map if opened
        Camera.turnTo(221, 1, 0.042);
        GameObject ladder = GameObjects.newQuery().on(new Coordinate(2452, 3231, 0)).results().nearest();
        GameObject altar = GameObjects.newQuery().names("Runecrafting altar").results().nearest();
        Npc banker = Npcs.newQuery().names("Eniola").results().nearest();
        if (ladder != null) {
            ChatDialog.Continue continueOption = ChatDialog.getContinue();
            if (continueOption != null) {
                continueOption.select(true);
            } else if (Inventory.getQuantity(564) > 0 &&
                    (Inventory.contains(5511) || Inventory.contains(5513) || Inventory.contains(5515))
            ) {
                if (Players.getLocal().getAnimationId() == -1) Magic.Lunar.NPC_CONTACT.activate("Dark Mage");
            }
            else if (ladder.isVisible()) {
                    ladder.interact("Climb");
            }
            else {
                if (!InterfaceWindows.getInventory().isOpen()) Keyboard.typeKey(27);
                WebPath path = Traversal.getDefaultWeb().getPathBuilder().buildTo(new Coordinate(2453, 3232, 0));
                if (path != null) {
                    if (path.getNext() == null) path.reset();
                    path.step(Path.TraversalOption.MANAGE_RUN);
                }
            }
        }
        else if (altar != null) {
            if (!altar.isVisible()) {
                if (
                    (Inventory.contains(5509) && Varbits.load(603).getValue() != 3 && !Inventory.contains(7936)) ||
                    (Inventory.contains(5510) && Varbits.load(604).getValue() != 6 && !Inventory.contains(7936)) ||
                    (Inventory.contains(5511) && Varbits.load(604).getValue() != 3 && !Inventory.contains(7936)) ||
                    (Inventory.contains(5512) && Varbits.load(605).getValue() != 9 && !Inventory.contains(7936)) ||
                    (Inventory.contains(5513) && Varbits.load(605).getValue() != 7 && !Inventory.contains(7936)) ||
                    (Inventory.contains(5514) && Varbits.load(606).getValue() != 12 && !Inventory.contains(7936)) ||
                    (Inventory.contains(5515) && Varbits.load(606).getValue() != 9 && !Inventory.contains(7936)) ||
                    (
                        (!Inventory.contains(5509) || Varbits.load(603).getValue() == 3) &&
                        (!Inventory.contains(5510) || Varbits.load(604).getValue() == 6) &&
                        (!Inventory.contains(5511) || Varbits.load(604).getValue() == 3) &&
                        (!Inventory.contains(5512) || Varbits.load(605).getValue() == 9) &&
                        (!Inventory.contains(5513) || Varbits.load(605).getValue() == 7) &&
                        (!Inventory.contains(5514) || Varbits.load(606).getValue() == 12) &&
                        (!Inventory.contains(5515) || Varbits.load(606).getValue() == 9) &&
                        Inventory.getEmptySlots() > 2
                    )
                ) {
                    if (!Bank.isOpen()) {
                        if (banker != null && Mouse.getCrosshairState() == Mouse.CrosshairState.NONE) {
                            if (Inventory.getSelectedItem() != null) banker.click();
                            banker.interact("Bank");
                        }
                    }
                    else if (Inventory.contains(Pattern.compile(".* rune$"))) {
                        Bank.depositInventory();
                    }
                    else if (Traversal.getRunEnergy() < 70 && !Inventory.contains(Pattern.compile("Stamina potion.*"))) {
                        Bank.withdraw(Pattern.compile("Stamina potion.*"), 1);
                    }
                    else if (Skill.CONSTITUTION.getCurrentLevel() < 70 && !Inventory.contains("Lobster")){
                        Bank.withdraw(379, 1);
                    }
                    else {
                        int rand = Random.nextInt(0, 10000);
                        if (rand < 6000) {
                            Bank.withdraw(7936, 0);
                        } else if (rand > 7000) {
                            Bank.withdraw(7936, -1);
                        } else {
                            Bank.withdraw(7936, 42);
                        }
                    }
                }
                else if (Bank.isOpen()) {
                    Bank.close(true);
                    if (!Traversal.isRunEnabled()) Traversal.toggleRun();
                }
                else if (Inventory.contains(379) || (Inventory.contains(7936) &&
                        (Inventory.contains(5509) && Varbits.load(603).getValue() != 3) ||
                        (Inventory.contains(5510) && Varbits.load(604).getValue() != 6) ||
                        (Inventory.contains(5511) && Varbits.load(604).getValue() != 3) ||
                        (Inventory.contains(5512) && Varbits.load(605).getValue() != 9) ||
                        (Inventory.contains(5513) && Varbits.load(605).getValue() != 7) ||
                        (Inventory.contains(5514) && Varbits.load(606).getValue() != 12) ||
                        (Inventory.contains(5515) && Varbits.load(606).getValue() != 9))
                ) {
                    if (Inventory.contains(5509) && Varbits.load(603).getValue() != 3)
                        Inventory.getItems(5509).first().click();
                    if (Inventory.contains(5510) && Varbits.load(604).getValue() != 6)
                        Inventory.getItems(5510).first().click();
                    if (Inventory.contains(5511) && Varbits.load(604).getValue() != 3)
                        Inventory.getItems(5511).first().click();
                    if (Inventory.contains(5512) && Varbits.load(605).getValue() != 9)
                        Inventory.getItems(5512).first().click();
                    if (Inventory.contains(5513) && Varbits.load(605).getValue() != 7)
                        Inventory.getItems(5513).first().click();
                    if (Inventory.contains(5514) && Varbits.load(606).getValue() != 12)
                        Inventory.getItems(5514).first().click();
                    if (Inventory.contains(5515) && Varbits.load(606).getValue() != 9)
                        Inventory.getItems(5514).first().click();
                    if (Inventory.contains(379))
                        Inventory.getItems(379).first().interact("Eat");
                }
                else if (Players.getLocal().getTarget() == null && (Players.getLocal().getPosition().getX() > 3018 ||
                        Players.getLocal().distanceTo(altar) < Players.getLocal().distanceTo(banker))
                ) {
                    WebPath path = Traversal.getDefaultWeb().getPathBuilder().buildTo(altar.getPosition());
                    if (path != null) {
                        if (path.getNext() == null) path.reset();
                        path.step(Path.TraversalOption.MANAGE_RUN);
                    }
                }
                else if (Traversal.getRunEnergy() < 70 && !Traversal.isStaminaEnhanced() &&
                        Inventory.contains(Pattern.compile("Stamina potion.*"))
                ) {
                    if (Inventory.getSelectedItem() != null)
                        Inventory.getItems(Pattern.compile("Stamina potion.*")).first().click();
                    Inventory.getItems(Pattern.compile("Stamina potion.*")).first().interact("Drink");
                }
                else if (Players.getLocal().getTarget() == null) {
                    if (Inventory.getSelectedItem() == null) {
                        Inventory.getItems(7936).random().interact("Use");
                    }
                    else {
                        Player p = Players.newQuery().filter(player -> player.isMoving() &&
                                (player.getOrientationAsAngle() >= 270 || player.getOrientationAsAngle() == 0) &&
                                player.getPosition().getY() < Players.getLocal().getPosition().getY() - 2 &&
                                player.getInteractionPoint().isVisible()).results().random();
                        if (p != null && p.getInteractionPoint() != null) p.getInteractionPoint().click();
                    }
                }
                else {
                    Mouse.move(new InteractableRectangle(Screen.getBounds().width,
                            Random.nextInt(0, Screen.getBounds().height), 1, 1));
                }
            }
            else {
                if (Inventory.getSelectedItem() != null) altar.click();
                if ((Inventory.contains(5509) && Varbits.load(603).getValue() != 0) ||
                    (Inventory.contains(5510) && Varbits.load(604).getValue() != 0) ||
                    (Inventory.contains(5511) && Varbits.load(604).getValue() != 0) ||
                    (Inventory.contains(5512) && Varbits.load(605).getValue() != 0) ||
                    (Inventory.contains(5513) && Varbits.load(605).getValue() != 0) ||
                    (Inventory.contains(5514) && Varbits.load(606).getValue() != 0) ||
                    (Inventory.contains(5515) && Varbits.load(606).getValue() != 0)
                ) {
                    if (!Inventory.isFull()) {
                        if (!Keyboard.isPressed(16)) Keyboard.pressKey(16);
                        if (Inventory.contains(5509) && !Inventory.isFull() && Varbits.load(603).getValue() != 0)
                            Inventory.getItems(5509).first().click();
                        if (Inventory.contains(5510) && !Inventory.isFull() && Varbits.load(604).getValue() != 0)
                            Inventory.getItems(5510).first().click();
                        if (Inventory.contains(5511) && !Inventory.isFull() && Varbits.load(604).getValue() != 0)
                            Inventory.getItems(5511).first().click();
                        if (Inventory.contains(5512) && !Inventory.isFull() && Varbits.load(605).getValue() != 0)
                            Inventory.getItems(5512).first().click();
                        if (Inventory.contains(5513) && !Inventory.isFull() && Varbits.load(605).getValue() != 0)
                            Inventory.getItems(5513).first().click();
                        if (Inventory.contains(5514) && !Inventory.isFull() && Varbits.load(606).getValue() != 0)
                            Inventory.getItems(5514).first().click();
                        if (Inventory.contains(5515) && !Inventory.isFull() && Varbits.load(606).getValue() != 0)
                            Inventory.getItems(5515).first().click();
                    }
                    if (Inventory.contains(7936)) altar.interact("Craft-rune");
                }
                else {
                    if (Keyboard.isPressed(16)) Keyboard.releaseKey(16);
                    if (Inventory.contains(7936)) {
                        altar.interact("Craft-rune");
                    }
                    else if (Players.getLocal().getAnimationId() == -1) Magic.Lunar.OURANIA_TELEPORT.activate();
                }
            }
        }
    }
}
