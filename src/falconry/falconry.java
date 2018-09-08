package falconry;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.region.Projectiles;
import com.runemate.game.api.script.framework.LoopingBot;

import static com.runemate.game.api.hybrid.local.Skill.HUNTER;

public class falconry extends LoopingBot {
    int dropState = 0;
    int dropPos = 0;

    @Override
    public void onStart(String... args){
        setLoopDelay(142, 842);
    }

    @Override
    public void onLoop() {
        if (dropState == 0 && Inventory.getQuantity() > 26) {
            Keyboard.pressKey(0x10);
            dropState = 1;
        } else if (dropState == 1) {
            SpriteItem item = Inventory.getItemIn(dropPos);
            if (item != null &&
                (item.getDefinition().getName().equals("Bones") ||
                item.getDefinition().getName().equals("Spotted kebbit fur") ||
                item.getDefinition().getName().equals("Dark kebbit fur") ||
                item.getDefinition().getName().equals("Dashing kebbit fur"))
                ) item.interact("Drop");
            if (dropPos == 27) {
                Keyboard.releaseKey(0x10);
                dropPos = 0;
                dropState = 0;
                return;
            }
            dropPos += 4;
            if (dropPos > 27) dropPos -= 27;
        } else {
            if (Players.getLocal().getAnimationId() == -1) {
                if (!Npcs.newQuery().names("Gyr Falcon").results().isEmpty()){
                    Npc falcon = Npcs.newQuery().names("Gyr Falcon").results().sortByDistance().nearest();
                    if (!falcon.isVisible()) {
                        falcon.getPosition().minimap().click();
                    } else {
                        falcon.interact("Retrieve");
                    }
                } else {
                    if (Projectiles.newQuery().target(Players.getLocal()).results().isEmpty()) {
                        if (HUNTER.getCurrentLevel() >= 57) {
                            Npcs.newQuery().names("Dashing kebbit", "Dark kebbit", "Spotted kebbit").results().sortByDistance().nearest().interact("Catch");
                        } else if (HUNTER.getCurrentLevel() >= 57) {
                            Npcs.newQuery().names("Dark kebbit", "Spotted kebbit").results().sortByDistance().nearest().interact("Catch");
                        } else {
                            Npcs.newQuery().names("Spotted kebbit").results().sortByDistance().nearest().interact("Catch");
                        }
                    }
                }
            }
        }
    }
}
