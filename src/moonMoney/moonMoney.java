package moonMoney;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.osrs.local.hud.interfaces.Magic;
import com.runemate.game.api.script.framework.LoopingBot;

import java.util.regex.Pattern;

public class moonMoney extends LoopingBot {
    @Override
    public void onStart(String... args){
        setLoopDelay(142, 642);
    }

    int state = 0;

    @Override
    public void onLoop() {
        tanHide();
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
                    Bank.depositInventory();
                } else {
                    state++;
                }
                break;
            case 2:
                if (Inventory.getQuantity("Bucket of sand") != 13) {
                    Bank.withdraw("Bucket of sand", 13);
                } else {
                    state++;
                }
                break;
            case 3:
                if (Inventory.getQuantity("Seaweed") < 13) {
                    int rand = Random.nextInt(0, 10000);
                    if (rand < 6000) {
                        Bank.withdraw("Seaweed", 0);
                    } else if (rand > 7000) {
                        Bank.withdraw("Seaweed", -1);
                    } else {
                        Bank.withdraw("Seaweed", 13);
                    }
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
                //GameObjects.getLoaded("Chest").nearest().hover();
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
