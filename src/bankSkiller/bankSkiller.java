package bankSkiller;

import com.runemate.game.api.hybrid.GameEvents;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.osrs.local.hud.interfaces.Magic;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.LoopingBot;

import java.util.regex.Pattern;

public class bankSkiller extends LoopingBot {
    @Override
    public void onStart(String... args){
        setLoopDelay(142, 242);
        GameEvents.OSRS.NPC_DISMISSER.disable();
    }

    int state = 0;

    @Override
    public void onLoop() {
        superGlass();
    }

    void stringing() {
        if (ChatDialog.getContinue() != null || !Inventory.contains(Pattern.compile(".*bow \\(u\\)")) || !Inventory.contains(Pattern.compile("Bow string"))) {
            if (!Bank.isOpen()) {
                Bank.open();
            }
            else {
                if (!Inventory.isEmpty() && Inventory.containsAnyExcept(Pattern.compile(".*bow \\(u\\)|Bow String"))) {
                    Bank.depositInventory();
                }
                else if (!Inventory.contains(Pattern.compile(".*bow \\(u\\)"))) {
                    Bank.withdraw(Pattern.compile(".*bow \\(u\\)"), 14);
                }
                else if (!Inventory.contains(Pattern.compile("Bow string"))) {
                    int rand = Random.nextInt(0, 10000);
                    if (rand < 6000) {
                        Bank.withdraw("Bow string", 0);
                    } else if (rand > 7000) {
                        Bank.withdraw("Bow string", -1);
                    } else {
                        Bank.withdraw("Bow string", 14);
                    }
                }
                else {
                    Keyboard.typeKey(27);
                }
            }
        }
        else if (Bank.isOpen()) {
            Keyboard.typeKey(27);
        }
        else {
            if (Interfaces.newQuery().actions("String").results().first() != null) {
                Keyboard.typeKey(32);
                Execution.delayUntil(() -> Inventory.getEmptySlots() > 1, 4200);
            }
            else if (!Inventory.contains(Pattern.compile(".*bow$")) && Players.getLocal().getAnimationId() == -1) {
                if (Inventory.getSelectedItem() == null) {
                    if (Inventory.getItemIn(12) != null) Inventory.getItemIn(12).click();
                }
                else {
                    if (Inventory.getItemIn(16) != null) Inventory.getItemIn(16).click();
                    Execution.delayUntil(() -> Interfaces.newQuery().actions("String").results().first() != null, 4200);
                }
            }
        }
    }

    void superGlass() {
        switch(state) {
            case 0:
                if (!Bank.isOpen()) {
                    Bank.open();
                } else {
                    state++;
                }
                break;
            case 1:
                if (Inventory.containsAnyExcept("Astral rune")) {
                    if (Inventory.getQuantity("Astral rune") < 1) pause();
                    Bank.depositInventory();
                } else {
                    state++;
                }
                break;
            case 2:
                if (Inventory.getQuantity("Bucket of sand") != 18) {
                    if (Bank.getQuantity("Bucket of sand") < 18) pause();
                    Bank.withdraw("Bucket of sand", 18);
                } else {
                    state++;
                }
                break;
            case 3:
                if (Inventory.getQuantity("Giant seaweed") < 3) {
                    if (Bank.getQuantity("Giant seaweed") < 3) pause();
                    Bank.withdraw("Giant seaweed", 3);
                } else {
                    state++;
                }
                break;
            case 4:
                if (Bank.isOpen()) {
                    Bank.close(true);
                } else {
                    state++;
                }
                break;
            case 5:
                Magic.Lunar.SUPERGLASS_MAKE.activate();
                GameObjects.newQuery().actions("Bank").results().nearest().hover();
                state = 0;
                break;
        }
    }

    void tanHide() {
        int castCount = 0;
        switch(state) {
            case 0:
                if (!Bank.isOpen()) {
                    Bank.open();
                } else {
                    state++;
                }
                break;
            case 1:
                if (Inventory.containsAnyExcept("Nature rune", "Astral rune", "Coins")) {
                    Bank.depositInventory();
                } else {
                    state++;
                }
                break;
            case 2:
                if (Inventory.getQuantity(Pattern.compile(".* dragonhide$")) == 0) {
                    int rand = Random.nextInt(0, 10000);
                    if (rand < 6000) {
                        Bank.withdraw(Pattern.compile(".* dragonhide$"), 0);
                    } else if (rand > 7000) {
                        Bank.withdraw(Pattern.compile(".* dragonhide$"), -1);
                    } else {
                        Bank.withdraw(Pattern.compile(".* dragonhide$"), 25);
                    }
                } else {
                    state++;
                }
                break;
            case 3:
                if (Bank.isOpen()) {
                    Bank.close(true);
                } else {
                    state++;
                }
                break;
            case 4:
                if (Inventory.containsAnyOf(Pattern.compile(".* dragonhide$")) && castCount < 5) {
                    if (Magic.Lunar.TAN_LEATHER.activate()) castCount++;
                } else {
                    castCount = 0;
                    state = 0;
                }
                break;
        }
    }
}
