package petrifiedWoodcutter;

import com.runemate.game.api.hybrid.GameEvents;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.framework.LoopingBot;

import java.util.Objects;

public class petrifiedWoodcutter extends LoopingBot {
    boolean dropping = false;
    int dropPos = 0;

    @Override
    public void onStart(String... args){
        setLoopDelay(142, 842);
        GameEvents.OSRS.NPC_DISMISSER.disable();
    }

    @Override
    public void onLoop() {
        if (!dropping && Inventory.isFull()) {
            Keyboard.pressKey(0x10);
            dropping = true;
        } else if (dropping) {
            Keyboard.pressKey(0x10); // just to make sure it is pressed
            SpriteItem item = Inventory.getItemIn(dropPos);
            if (item != null && Objects.requireNonNull(item.getDefinition()).getName().equals("Teak logs")) item.click();
            if (dropPos == 27) {
                Keyboard.releaseKey(0x10);
                dropPos = 0;
                dropping = false;
                return;
            }
            dropPos += 4;
            if (dropPos > 27) dropPos -= 27; // -=27 retains the column number
        }
        else {
            if (Inventory.getSelectedItem() != null) Inventory.getSelectedItem().click();
            Player player = Players.getLocal();
            if (player != null && !player.isMoving() && player.getAnimationId() == -1 && Mouse.getCrosshairState() == Mouse.CrosshairState.NONE) {
                GameObject tree = GameObjects.newQuery().names("Teak Tree").results().nearest();
                if (tree != null) {
                    tree.interact("Chop down");
                }
            }
            else {
                LocatableEntityQueryResults<GameObject> trees = GameObjects.newQuery().names("Teak Tree").results().sortByDistance();
                if (trees != null && trees.size() > 1) {
                    trees.get(1).hover();
                }
            }
        }
    }
}
