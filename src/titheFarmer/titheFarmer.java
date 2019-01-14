package titheFarmer;

import com.runemate.game.api.hybrid.GameEvents;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.*;
import com.runemate.game.api.hybrid.local.hud.interfaces.*;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.osrs.local.hud.interfaces.Magic;
import com.runemate.game.api.script.framework.LoopingBot;

import java.util.regex.Pattern;

public class titheFarmer extends LoopingBot {
    Coordinate anchor;
    Coordinate tiles[] = new Coordinate[20];
    int seedType = -1;

    private void updateTiles() {
        GameObject ankor = GameObjects.newQuery().names("Fruit cart").results().first();
        if (ankor != null && ankor.getPosition() != anchor) {
            anchor = ankor.getPosition();

            tiles[0] = new Coordinate(anchor.getX()-11, anchor.getY()-12, 0);
            tiles[1] = new Coordinate(anchor.getX()-16, anchor.getY()-12, 0);
            tiles[2] = new Coordinate(anchor.getX()-16, anchor.getY()-9, 0);
            tiles[3] = new Coordinate(anchor.getX()-11, anchor.getY()-9, 0);
            tiles[4] = new Coordinate(anchor.getX()-11, anchor.getY()-6, 0);
            tiles[5] = new Coordinate(anchor.getX()-16, anchor.getY()-6, 0);
            tiles[6] = new Coordinate(anchor.getX()-16, anchor.getY()-3, 0);
            tiles[7] = new Coordinate(anchor.getX()-11, anchor.getY()-3, 0);

            tiles[8] = new Coordinate(anchor.getX()-11, anchor.getY()+5, 0);
            tiles[9] = new Coordinate(anchor.getX()-16, anchor.getY()+5, 0);
            tiles[10] = new Coordinate(anchor.getX()-16, anchor.getY()+8, 0);
            tiles[11] = new Coordinate(anchor.getX()-11, anchor.getY()+8, 0);
            tiles[12] = new Coordinate(anchor.getX()-11, anchor.getY()+11, 0);
            tiles[13] = new Coordinate(anchor.getX()-16, anchor.getY()+11, 0);
            tiles[14] = new Coordinate(anchor.getX()-16, anchor.getY()+14, 0);
            tiles[15] = new Coordinate(anchor.getX()-11, anchor.getY()+14, 0);

            tiles[16] = new Coordinate(anchor.getX()-21, anchor.getY()+14, 0);
            tiles[17] = new Coordinate(anchor.getX()-21, anchor.getY()+11, 0);
            tiles[18] = new Coordinate(anchor.getX()-21, anchor.getY()+8, 0);
            tiles[19] = new Coordinate(anchor.getX()-21, anchor.getY()+5, 0);
        }
    }

    private int getState(GameObject go) {
        int seedlingID;
        switch (seedType) {
            case 1: seedlingID = 27384; break;
            case 2: seedlingID = 27395; break;
            case 3: seedlingID = 27406; break;
            default: return -1;
        }

        int ready = 27383;
        int seedling = seedlingID;
        int seedlingWatered = seedlingID+1;
        int seedlingDead = seedlingID+2;
        int stage1 = seedlingID+3;
        int stage1Watered = seedlingID+4;
        int stage1Dead = seedlingID+5;
        int stage2 = seedlingID+6;
        int stage2Watered = seedlingID+7;
        int stage2Dead = seedlingID+8;
        int grown = seedlingID+9;

        if (go != null) {
            //higher states are favoured by getNextObject()
            if (go.getId() == ready) return 5;
            if (go.getId() == seedling) return 6;
            if (go.getId() == seedlingWatered) return 0;
            if (go.getId() == seedlingDead) return 1;
            if (go.getId() == stage1) return 4;
            if (go.getId() == stage1Watered) return 0;
            if (go.getId() == stage1Dead) return 1;
            if (go.getId() == stage2) return 3;
            if (go.getId() == stage2Watered) return 0;
            if (go.getId() == stage2Dead) return 1;
            if (go.getId() == grown) return 2;
        }
        return -1;
    }

    private GameObject getNextObject() {
        return GameObjects.newQuery().on(tiles[recurse(0, 0)]).types(GameObject.Type.PRIMARY).results().first();
    }
    private int recurse(int ret, int i) {
        if (i > 3) { return ret; }//if (i > tiles.length-1) { return ret; }
        else if (getState(GameObjects.newQuery().on(tiles[i]).types(GameObject.Type.PRIMARY).results().first()) >
                getState(GameObjects.newQuery().on(tiles[ret]).types(GameObject.Type.PRIMARY).results().first())) {
            return recurse(i, ++i); }
        else { return recurse(ret, ++i); }
    }

    @Override
    public void onStart(String... args){
        setLoopDelay(142, 842);
        GameEvents.OSRS.NPC_DISMISSER.disable();
        if (Skill.FARMING.getBaseLevel() >= 34 && Skill.FARMING.getBaseLevel() < 54) {
            seedType = 1;
        } else if (Skill.FARMING.getBaseLevel() >= 54 && Skill.FARMING.getBaseLevel() < 74) {
            seedType = 2;
        } else if (Skill.FARMING.getBaseLevel() >= 74) {
            seedType = 3;
        }
        updateTiles();
    }

    @Override
    public void onLoop() {
        Camera.turnTo(0, 1, 0.042);
        GameObject farmDoor = GameObjects.newQuery().names("Farm door").results().first();
        GameObject seedTable = GameObjects.newQuery().names("Seed table").results().first();
        SpriteItem fertiliser = Inventory.getItems("Gricoller's fertiliser").first();
        if (fertiliser != null) fertiliser.interact("Drop");
        if (!Inventory.contains(Pattern.compile(".*ano seed")) && !Inventory.contains(Pattern.compile(".*ano fruit"))) {
            if (farmDoor != null && farmDoor.getPosition().getX() != 1778) {
                farmDoor.interact("Open");
            }
            else {
                if (!ChatDialog.getOptions().isEmpty()) {
                    if (Skill.FARMING.getBaseLevel() >= 34 && Skill.FARMING.getBaseLevel() < 54) {
                        Keyboard.typeKey("1");
                        seedType = 1;
                    } else if (Skill.FARMING.getBaseLevel() >= 54 && Skill.FARMING.getBaseLevel() < 74) {
                        Keyboard.typeKey("2");
                        seedType = 2;
                    } else if (Skill.FARMING.getBaseLevel() >= 74) {
                        Keyboard.typeKey("3");
                        seedType = 3;
                    }
                }
                else if (ChatDialog.getText() != null && !ChatDialog.getText().matches(".*You grab some seeds.*")) {
                    if (seedTable != null && Mouse.getCrosshairState() == Mouse.CrosshairState.NONE)
                        seedTable.interact("Search");
                }
            }
        }
        else {
            if (farmDoor != null && farmDoor.getPosition().getX() == 1778) {
                farmDoor.interact("Open");
            }
            else {
                if (!Inventory.contains(Pattern.compile("Watering can\\(.*")) && Inventory.contains("Watering can")) {
                    Magic.Lunar.HUMIDIFY.activate();
                }
                else {
                    updateTiles();
                    GameObject go = getNextObject();
                    if (go != null && !Players.getLocal().isMoving() && Players.getLocal().getAnimationId() == -1) {
                        //System.out.println(go.getPosition()+" "+getState(go));
                        if (getState(go) == 2) {
                            go.interact("Harvest");
                        }
                        else if (getState(go) == 5) {
                            if (Inventory.getSelectedItem() == null) {
                                Inventory.getItems(Pattern.compile(".*ano seed")).random().interact("Use");
                            }
                            else { go.interact("Use"); }
                        }
                        else if (getState(go) == 3 || getState(go) == 4  || getState(go) == 6) {
                            if (Inventory.getSelectedItem() == null) {
                                Inventory.getItems(Pattern.compile("Watering can\\(.*")).first().interact("Use");
                            }
                            else { go.interact("Use"); }
                        }
                        else if (getState(go) == 1) {
                            go.interact("Clear");
                        }
                    }
                    else {
                        if (Inventory.getQuantity(Pattern.compile(".*ano seed")) == 0) {
                            if (Inventory.getQuantity(Pattern.compile(".*ano fruit")) > 0) {
                                GameObjects.newQuery().names("Sack").results().first().interact("Deposit");
                            }
                            else {
                                farmDoor.interact("Open");
                            }
                        }
                    }
                }
            }
        }
    }
}
