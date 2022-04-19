package bankSkiller;

import com.runemate.game.api.hybrid.GameEvents;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.details.Interactable;
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

import java.util.Objects;
import java.util.regex.Pattern;

public class bankSkiller extends LoopingBot {
    @Override
    public void onStart(String... args){
        setLoopDelay(142, 242);
        GameEvents.OSRS.NPC_DISMISSER.disable();
    }

    int state = 0;
    final Pattern bow_u = Pattern.compile(".*bow \\(u\\)");
    final Pattern bowOrString = Pattern.compile(".*bow \\(u\\)|Bow String");
    final Pattern bow = Pattern.compile(".*bow$");
    final Pattern dragonhide = Pattern.compile(".* dragonhide$");
    final Pattern bankPattern = Pattern.compile(".*[Bb]ank.*");

    @Override
    public void onLoop() {
        stringing();
    }

    void stringing() {
        if (ChatDialog.getContinue() != null || !Inventory.contains(bow_u) || !Inventory.contains("Bow string")) {
            if (!Bank.isOpen()) {
                Bank.open();
            }
            else {
                if (!Inventory.isEmpty() && Inventory.containsAnyExcept(bowOrString) || Inventory.getQuantity(bow_u) > 14 || Inventory.getQuantity("Bow String") > 14) {
                    Bank.depositInventory();
                }
                else if (!Inventory.contains(bow_u)) {
                    Bank.withdraw(bow_u, 14);
                }
                else if (!Inventory.contains("Bow string")) {
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
            if (Interfaces.newQuery().containers(270).actions("String").results().first() != null) {
                Keyboard.typeKey(32);
                Interactable bank = GameObjects.newQuery().names(bankPattern).results().nearest();
                if (bank != null) bank.hover();
                Execution.delayUntil(() -> Inventory.getEmptySlots() > 1, 4200);
            }
            else if (!Inventory.contains(bow) && Objects.requireNonNull(Players.getLocal()).getAnimationId() == -1) {
                if (Inventory.getSelectedItem() == null) {
                    if (Inventory.getItemIn(12) != null) Objects.requireNonNull(Inventory.getItemIn(12)).click();
                    Execution.delayUntil(() -> Inventory.getSelectedItem() != null, 420);
                }
                else {
                    if (Inventory.getItemIn(16) != null) Objects.requireNonNull(Inventory.getItemIn(16)).click();
                    Execution.delayUntil(() -> Interfaces.newQuery().containers(270).actions("String").results().first() != null, 4200);
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
                Objects.requireNonNull(GameObjects.newQuery().actions("Bank").results().nearest()).hover();
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
                if (Inventory.getQuantity() == 0) {
                    int rand = Random.nextInt(0, 10000);
                    if (rand < 6000) {
                        Bank.withdraw(dragonhide, 0);
                    } else if (rand > 7000) {
                        Bank.withdraw(dragonhide, -1);
                    } else {
                        Bank.withdraw(dragonhide, 25);
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
                if (Inventory.containsAnyOf(dragonhide) && castCount < 5) {
                    if (Magic.Lunar.TAN_LEATHER.activate()) castCount++;
                } else {
                    castCount = 0;
                    state = 0;
                }
                break;
        }
    }
}
