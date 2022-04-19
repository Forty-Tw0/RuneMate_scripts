package constructor;

import com.runemate.game.api.hybrid.GameEvents;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.InteractablePoint;
import com.runemate.game.api.hybrid.local.hud.Menu;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.osrs.local.hud.interfaces.Magic;
import com.runemate.game.api.script.framework.LoopingBot;

public class constructor extends LoopingBot {

    void mouseWheelTurnTo(Coordinate Tc){
        if ( Tc == null ) return;
        Player player = Players.getLocal();
        Coordinate Pc = player.getPosition();

        int Dx = Tc.getX() - Pc.getX();
        int Dy = Tc.getY() - Pc.getY();
        int Yaw = Camera.getYaw();

        int Beta = (int)( Math.atan2( - Dx, Dy ) * 180 / Math.PI ); //atan2 is in radians so 180/PI wil transform it to degrees, like the game.
        if ( Beta < 0 ) Beta = 360 + Beta;

        int deltaYaw = Beta - Yaw;

        if ( deltaYaw > 180 ) {
            deltaYaw = deltaYaw - 360;
        } else if ( deltaYaw < - 180 ) {
            deltaYaw = deltaYaw + 360;
        }

        int deltaMouseMoveX = (int) (-deltaYaw*2.5);

        Area hoverArea = new Area.Circular(Players.getLocal().getPosition(), 3);
        hoverArea.getRandomCoordinate().hover();

        Mouse.press(Mouse.Button.WHEEL);
        Mouse.move(new InteractablePoint((int) (Mouse.getPosition().getX() + deltaMouseMoveX), (int) (Mouse.getPosition().getY() + Random.nextInt(-10,10))));
        Mouse.release(Mouse.Button.WHEEL);
    }

    @Override
    public void onStart(String... args) {
        setLoopDelay(142, 842);
        GameEvents.OSRS.NPC_DISMISSER.disable();
    }

    @Override
    public void onLoop() {
        if (Players.getLocal().getAnimationId() == -1 && !Players.getLocal().isMoving() && Mouse.getCrosshairState() == Mouse.CrosshairState.NONE) {
            if (!GameObjects.newQuery().names("Mythic Statue").results().isEmpty()) {
                Magic.TELEPORT_TO_HOUSE.activate("Outside");
            }
            else if (!GameObjects.newQuery().names("Portal").actions("Build mode").results().isEmpty()) {
                if (Inventory.getQuantity(22114) == 0 || Inventory.getQuantity(8780) == 21) {
                    if (Inventory.getSelectedItem() != null) {
                        Inventory.getSelectedItem().click();
                    }
                    else {
                        GameObject portal = GameObjects.newQuery().names("Portal").actions("Build mode").results().nearest();
                        if (portal != null) {
                            if (!portal.isVisible()) {
                                mouseWheelTurnTo(portal.getPosition());
                                //Camera.turnTo(portal);
                            }
                            else {
                                if (!portal.interact("Build mode") && Menu.isOpen()) Menu.close();
                            }
                        }
                    }
                }
                else {
                    if (!ChatDialog.getOptions().isEmpty()) {
                        Keyboard.typeKey("3");
                    }
                    else {
                        if (Inventory.getSelectedItem() == null) {
                            Inventory.getItems(8781).first().interact("Use");
                        }
                        else {
                            Npc phials = Npcs.newQuery().names("Phials").results().nearest();
                            if (phials != null) {
                                if (!phials.isVisible()) {
                                    mouseWheelTurnTo(phials.getPosition());
                                    //Camera.turnTo(phials);
                                }
                                else {
                                    if (!phials.interact("Use") && Menu.isOpen()) Menu.close();
                                }
                            }
                        }
                    }
                }
            }
            else {
                if (Inventory.getQuantity(8780) == 0 && Inventory.getQuantity(22114) == 1) {
                    GameObject portal = GameObjects.newQuery().names("Portal").actions("Lock").results().nearest();
                    if (portal != null) {
                        if (!portal.isVisible()) {
                            mouseWheelTurnTo(portal.getPosition());
                            //Camera.turnTo(portal);
                        }
                        else {
                            if (!portal.interact("Enter") && Menu.isOpen()) Menu.close();
                        }
                    }
                }
                else {
                    if (!GameObjects.newQuery().names("Guild trophy space").actions("Build").results().isEmpty()) {
                        if (!Interfaces.newQuery().actions("Build").results().isEmpty()) {
                            Keyboard.typeKey("4");
                        } else {
                            GameObject buildSpace = GameObjects.newQuery().names("Guild trophy space").actions("Build").results().nearest();
                            if (buildSpace != null) {
                                if (!buildSpace.isVisible()) {
                                    mouseWheelTurnTo(buildSpace.getPosition());
                                    //Camera.turnTo(buildSpace);
                                }
                                else {
                                    if (!buildSpace.interact("Build") && Menu.isOpen()) Menu.close();
                                }
                            }
                        }
                    } else if (!GameObjects.newQuery().names("Mythical cape").actions("Remove").results().isEmpty()) {
                        if (!ChatDialog.getOptions().isEmpty()) {
                            Keyboard.typeKey("1");
                        }
                        else {
                            GameObject removeSpace = GameObjects.newQuery().names("Mythical cape").actions("Remove").results().nearest();
                            if (removeSpace != null) {
                                if (!removeSpace.isVisible()) {
                                    mouseWheelTurnTo(removeSpace.getPosition());
                                    //Camera.turnTo(removeSpace);
                                }
                                else {
                                    if (!removeSpace.interact("Remove") && Menu.isOpen()) Menu.close();
                                }
                            }
                        }
                    }
                }
            }
        }
        else {
            if (!GameObjects.newQuery().names("Guild trophy space", "Mythical cape").results().isEmpty() &&
                    Mouse.getTarget() != GameObjects.newQuery().names("Guild trophy space", "Mythical cape").results().nearest()) {
                GameObjects.newQuery().names("Guild trophy space", "Mythical cape").results().nearest().hover();
            }
        }
    }
}