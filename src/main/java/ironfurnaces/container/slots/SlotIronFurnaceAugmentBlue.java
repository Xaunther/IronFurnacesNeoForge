package ironfurnaces.container.slots;

import ironfurnaces.items.augments.ItemAugment;
import ironfurnaces.items.augments.ItemAugmentBlasting;
import ironfurnaces.items.augments.ItemAugmentBlue;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotIronFurnaceAugmentBlue extends Slot {

    private BlockIronFurnaceTileBase te;

    public SlotIronFurnaceAugmentBlue(BlockIronFurnaceTileBase te, int slotIndex, int xPosition, int yPosition) {
        super(te, slotIndex, xPosition, yPosition);
        this.te = te;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof ItemAugmentBlue;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void setChanged() {
        te.onUpdateSent();
    }

    @Override
    public boolean isActive() {
        return te.getAugmentGUI() == 1;
    }

}
