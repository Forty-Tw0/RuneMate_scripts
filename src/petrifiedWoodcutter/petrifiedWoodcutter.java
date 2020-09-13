package petrifiedWoodcutter;

import com.runemate.game.api.hybrid.GameEvents;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.framework.LoopingBot;

public class petrifiedWoodcutter extends LoopingBot {
    int dropState = 0;
    int dropPos = 0;

    @Override
    public void onStart(String... args){
        setLoopDelay(142, 842);
        GameEvents.OSRS.NPC_DISMISSER.disable();
    }

    @Override
    public void onLoop() {
        if (dropState == 0 && Inventory.isFull()) {
            Keyboard.pressKey(0x10);
            dropState = 1;
        } else if (dropState == 1) {
            SpriteItem item = Inventory.getItemIn(dropPos);
            if (item != null && item.getDefinition().getName().equals("Teak logs")) item.interact("Drop");
            if (dropPos == 27) {
                Keyboard.releaseKey(0x10);
                dropPos = 0;
                dropState = 0;
                return;
            }
            dropPos += 4;
            if (dropPos > 27) dropPos -= 27;
        }
        else {
            if (Inventory.getSelectedItem() != null) Inventory.getSelectedItem().click();
            if (!Players.getLocal().isMoving() && Players.getLocal().getAnimationId() == -1 && Mouse.getCrosshairState() == Mouse.CrosshairState.NONE) {
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
