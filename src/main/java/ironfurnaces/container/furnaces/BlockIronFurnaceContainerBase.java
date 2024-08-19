package ironfurnaces.container.furnaces;

import ironfurnaces.IronFurnaces;
import ironfurnaces.container.slots.*;
import ironfurnaces.items.ItemHeater;
import ironfurnaces.items.augments.ItemAugmentBlasting;
import ironfurnaces.items.augments.ItemAugmentRed;
import ironfurnaces.items.augments.ItemAugmentGreen;
import ironfurnaces.items.augments.ItemAugmentBlue;
import ironfurnaces.items.augments.ItemAugmentSmoking;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import ironfurnaces.util.container.FactoryDataSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;


public abstract class BlockIronFurnaceContainerBase extends AbstractContainerMenu {

    private BlockIronFurnaceTileBase te;
    private Player playerEntity;
    private IItemHandler playerInventory;
    private final Level world;

    private static final int FURNACE_SLOT_X = 56;
    private static final int FURNACE_INPUT_SLOT_Y = 17;
    private static final int FURNACE_FUEL_SLOT_Y = 53;
    private static final int FURNACE_OUTPUT_SLOT_X = 116;
    private static final int FURNACE_OUTPUT_SLOT_Y = (FURNACE_INPUT_SLOT_Y + FURNACE_FUEL_SLOT_Y) / 2;
    private static final int FURNACE_AUGMENT_RED_SLOT_X = 26;
    private static final int FURNACE_AUGMENT_GREEN_SLOT_X = 80;
    private static final int FURNACE_AUGMENT_BLUE_SLOT_X = 134;
    private static final int GENERATOR_FUEL_SLOT_Y = 40;
    private static final int FACTORY_INPUT_SLOT_Y = 6;
    private static final int FACTORY_OUTPUT_SLOT_Y = 55;
    private static final int FACTORY_SLOT_X0 = 28;
    private static final int FACTORY_SLOT_DX = 21;
    private static final int PLAYER_INVENTORY_SLOT_X0 = 8;
    private static final int PLAYER_INVENTORY_SLOT_Y0 = 84;


    public BlockIronFurnaceContainerBase(MenuType<?> containerType, int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(containerType, windowId);
        this.te = (BlockIronFurnaceTileBase) world.getBlockEntity(pos);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.world = playerInventory.player.level();

        //FURNACE
        this.addSlot(new SlotIronFurnaceInput(te, te.INPUT, FURNACE_SLOT_X, FURNACE_INPUT_SLOT_Y));
        this.addSlot(new SlotIronFurnaceFuel(te, te.FUEL, FURNACE_SLOT_X, FURNACE_FUEL_SLOT_Y));
        this.addSlot(new SlotIronFurnace(playerEntity, te, te.OUTPUT, FURNACE_OUTPUT_SLOT_X, FURNACE_OUTPUT_SLOT_Y));
        this.addSlot(new SlotIronFurnaceAugmentRed(te, te.AUGMENT_RED, FURNACE_AUGMENT_RED_SLOT_X, FURNACE_OUTPUT_SLOT_Y));
        this.addSlot(new SlotIronFurnaceAugmentGreen(te, te.AUGMENT_GREEN, FURNACE_AUGMENT_GREEN_SLOT_X, FURNACE_OUTPUT_SLOT_Y));
        this.addSlot(new SlotIronFurnaceAugmentBlue(te, te.AUGMENT_BLUE, FURNACE_AUGMENT_BLUE_SLOT_X, FURNACE_OUTPUT_SLOT_Y));

        //GENERATOR
        this.addSlot(new SlotIronFurnaceInputGenerator(te, te.GENERATOR_FUEL, FURNACE_SLOT_X, GENERATOR_FUEL_SLOT_Y));

        //FACTORY
        for( int factoryIndex = 0; factoryIndex < te.FACTORY_INPUT.length; ++factoryIndex ) {
            this.addSlot(new SlotIronFurnaceInputFactory(factoryIndex, te,
                    te.FACTORY_INPUT[factoryIndex], FACTORY_SLOT_X0+FACTORY_SLOT_DX*factoryIndex, FACTORY_INPUT_SLOT_Y));
        }
        for( int factoryIndex = 0; factoryIndex < te.FACTORY_INPUT.length; ++factoryIndex ) {
            this.addSlot(new SlotIronFurnaceOutputFactory(factoryIndex, playerEntity, te,
                    te.FACTORY_INPUT[factoryIndex] + te.FACTORY_INPUT.length, FACTORY_SLOT_X0+FACTORY_SLOT_DX*factoryIndex, FACTORY_OUTPUT_SLOT_Y));
        }
        layoutPlayerInventorySlots(PLAYER_INVENTORY_SLOT_X0, PLAYER_INVENTORY_SLOT_Y0);
        checkContainerSize(this.te, 19);
        addDataSlots();
    }

    public void addDataSlots()
    {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getAugmentGUI() ? 1 : 0;
            }

            @Override
            public void set(int value) {
                te.furnaceSettings.set(10, value);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getIsFurnace() ? 1 : 0;
            }

            @Override
            public void set(int value) {
                if (value == 1)
                {
                    te.currentAugment[2] = 0;
                }
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getIsGenerator() ? 1 : 0;
            }

            @Override
            public void set(int value) {
                if (value == 1)
                {
                    te.currentAugment[2] = 2;
                }
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getIsFactory() ? 1 : 0;
            }

            @Override
            public void set(int value) {
                if (value == 1)
                {
                    te.currentAugment[2] = 1;
                }
            }
        });
        addEnergyData();
        addFurnaceData();
        addGeneratorData();
        addFactoryData();

    }

    public void addFurnaceData()
    {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return te.furnaceBurnTime;
            }

            @Override
            public void set(int value) {
                te.furnaceBurnTime = value;
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return te.recipesUsed;
            }

            @Override
            public void set(int value) {
                int add = te.recipesUsed;
                te.recipesUsed = value;
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return te.cookTime;
            }

            @Override
            public void set(int value) {
                te.cookTime = value;
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return te.totalCookTime;
            }

            @Override
            public void set(int value) {
                te.totalCookTime = value;
            }
        });
    }

    public void addGeneratorData()
    {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (int)te.generatorBurn;
            }

            @Override
            public void set(int value) {
                te.generatorBurn = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return te.generatorRecentRecipeRF;
            }

            @Override
            public void set(int value) {
                te.generatorRecentRecipeRF = value;
            }
        });
    }

    public void addFactoryData()
    {
        for (int i = 0; i < te.factoryCookTime.length; i++)
        {
            addDataSlot(new FactoryDataSlot(i) {
                @Override
                public int get() {
                    return te.factoryCookTime[index];
                }

                @Override
                public void set(int value) {
                    te.factoryCookTime[index] = value;
                }
            });
        }

        for (int i = 0; i < te.factoryTotalCookTime.length; i++)
        {
            addDataSlot(new FactoryDataSlot(i) {
                @Override
                public int get() {
                    return te.factoryTotalCookTime[index];
                }

                @Override
                public void set(int value) {
                    te.factoryTotalCookTime[index] = value;
                }
            });
        }
    }

    public int getEnergy() {
        return te.getEnergy();
    }

    public int getMaxEnergy() {
        return te.energyStorage.getMaxEnergyStored();
    }


    private void addEnergyData() {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getMaxEnergy();
            }

            @Override
            public void set(int value) {
                te.setMaxEnergy(value);
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getEnergy();
            }

            @Override
            public void set(int value) {
                te.setEnergy(value);
            }
        });
    }

    public boolean stillValid(Player player) {
        return this.te.stillValid(player);
    }

    public int getTier()
    {
        return te.getTier();

    }


    public boolean isAutoSplit()
    {
        return te.isAutoSplit();
    }



    public int getRedstoneMode() {
        return this.te.getRedstoneSetting();
    }


    public int getComSub() {
        return this.te.getRedstoneComSub();
    }


    public boolean getAutoInput() {
        return this.te.getAutoInput() == 1;
    }


    public boolean getAugmentGUI() {
        return this.te.getAugmentGUI() == 1;
    }


    public boolean getIsFactory() {
        return this.te.isFactory();
    }


    public boolean getIsFurnace() {
        return this.te.isFurnace();
    }
    
    public boolean getIsGenerator() {
        return this.te.isGenerator();
    }
    
    public boolean getAutoOutput() {
        return this.te.getAutoOutput() == 1;
    }
    
    public Component getTooltip(int index) {
        switch (te.furnaceSettings.get(index))
        {
            case 1:
                return Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_input");
            case 2:
                return Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_output");
            case 3:
                return Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_input_output");
            case 4:
                return Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_fuel");
            default:
                return Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".gui_none");
        }
    }


    public int getSettingTop()
    {
        return this.te.getSettingTop();
    }


    public int getSettingBottom()
    {
        return this.te.getSettingBottom();
    }


    public int getSettingFront()
    {
        return this.te.getSettingFront();
    }


    public int getSettingBack()
    {
        return this.te.getSettingBack();
    }


    public int getSettingLeft()
    {
        return this.te.getSettingLeft();
    }


    public int getSettingRight()
    {
        return this.te.getSettingRight();
    }


    public int getIndexFront()
    {
        return this.te.getIndexFront();
    }


    public int getIndexBack()
    {
        return this.te.getIndexBack();
    }


    public int getIndexLeft()
    {
        return this.te.getIndexLeft();
    }


    public int getIndexRight()
    {
        return this.te.getIndexRight();
    }


    public BlockPos getPos() {
        return this.te.getBlockPos();
    }


    public boolean isBurning() {
        return this.te.isBurning();
    }

    public boolean isRainbowFurnace() {
        return this.te.isRainbowFurnace();
    }


    public int getCookScaled(int pixels) {
        int i = this.te.cookTime;
        int j = this.te.totalCookTime;
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }


    public int getFactoryCookScaled(int index, int pixels) {
        int i = this.te.factoryCookTime[index];
        int j = this.te.factoryTotalCookTime[index];
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }


    public int getFactoryCooktimeSize()
    {
        return this.te.factoryCookTime.length;
    }


    public int getBurnLeftScaled(int pixels) {
        int i = this.te.recipesUsed;
        if (i == 0) {
            i = 200;
        }

        return this.te.furnaceBurnTime * pixels / i;
    }


    public int getGeneratorBurnScaled(int pixels) {
        int i = this.te.generatorRecentRecipeRF;
        if (i == 0) {
            i = 200;
        }
        return (int)this.te.generatorBurn * pixels / i;
    }


    public boolean isGeneratorBurning()
    {
        return te.generatorBurn > 0;
    }


    public int getEnergyScaled(int pixels)
    {
        int i = this.te.getEnergy();
        int j = this.te.getCapacity();
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (te.isGenerator())
            {
                if (index != te.GENERATOR_FUEL && index != te.AUGMENT_RED && index != te.AUGMENT_GREEN && index != te.AUGMENT_BLUE)
                {
                    if (te.getItem(te.AUGMENT_RED).getItem() instanceof ItemAugmentSmoking)
                    {
                        if (te.getSmokingBurn(itemstack1) > 0) {
                            if (!this.moveItemStackTo(itemstack1, te.GENERATOR_FUEL, te.GENERATOR_FUEL+1, false)) {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                    else if (te.getItem(te.AUGMENT_RED).getItem() instanceof ItemAugmentBlasting)
                    {
                        if (te.hasGeneratorBlastingRecipe(itemstack1)) {
                            if (!this.moveItemStackTo(itemstack1, te.GENERATOR_FUEL, te.GENERATOR_FUEL+1, false)) {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                    else
                    {
                        if (BlockIronFurnaceTileBase.isItemFuel(itemstack1, RecipeType.SMELTING) &&
                                !(itemstack1.getItem() instanceof ItemHeater)) {
                            if (!this.moveItemStackTo(itemstack1, te.GENERATOR_FUEL, te.GENERATOR_FUEL+1, false)) {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                    if (!this.moveItemStackToAugmentSlot(itemstack1)) {
                        return ItemStack.EMPTY;
                    } else if (!this.moveItemStackWithinInventory(itemstack1, index)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 19, 19 + 9*4, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (te.isFactory()) {
                if (index >= te.FACTORY_INPUT[0] + te.FACTORY_INPUT.length &&
                        index <= te.FACTORY_INPUT[te.FACTORY_INPUT.length-1] + te.FACTORY_INPUT.length) {
                    if (!this.moveItemStackTo(itemstack1, 19, 19 + 9*4, true)) {
                        return ItemStack.EMPTY;
                    }

                    slot.onQuickCraft(itemstack1, itemstack);
                } else if (index >= 19) {
                     if (this.te.hasRecipe(itemstack1)) {
                         if (!this.moveItemStackTo(itemstack1, te.FACTORY_INPUT[te.FACTORY_INPUT.length/2-1-getTier()],
                                te.FACTORY_INPUT[te.FACTORY_INPUT.length/2+getTier()]+1, false)) {
                            return ItemStack.EMPTY;
                         }
                    } else if (!this.moveItemStackToAugmentSlot(itemstack1)) {
                        return ItemStack.EMPTY;
                    } else if (!this.moveItemStackWithinInventory(itemstack1, index)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 19, 19 + 9*4, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (te.isFurnace())
            {

                if (index == te.OUTPUT) {
                    if (!this.moveItemStackTo(itemstack1, 19, 19 + 9*4, true)) {
                        return ItemStack.EMPTY;
                    }

                    slot.onQuickCraft(itemstack1, itemstack);
                } else if (index != te.FUEL && index != te.INPUT && index != te.AUGMENT_RED && index != te.AUGMENT_GREEN && index != te.AUGMENT_BLUE) {
                    if (this.te.hasRecipe(itemstack1)) {
                        if (!this.moveItemStackTo(itemstack1, te.INPUT, te.INPUT+1, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (BlockIronFurnaceTileBase.isItemFuel(itemstack1, RecipeType.SMELTING)) {
                        if (!this.moveItemStackTo(itemstack1, te.FUEL, te.FUEL+1, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackToAugmentSlot(itemstack1)) {
                        return ItemStack.EMPTY;
                    } else if (!this.moveItemStackWithinInventory(itemstack1, index)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 19, 19 + 9*4, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);

        }


        return itemstack;
    }



    private boolean moveItemStackToAugmentSlot( ItemStack itemStack ) {
        if (itemStack.getItem() instanceof ItemAugmentRed) {
            return this.moveItemStackTo(itemStack, te.AUGMENT_RED, te.AUGMENT_RED+1, false);
        }
        else if (itemStack.getItem() instanceof ItemAugmentGreen) {
            return this.moveItemStackTo(itemStack, te.AUGMENT_GREEN, te.AUGMENT_GREEN+1, false);
        }
        else if (itemStack.getItem() instanceof ItemAugmentBlue) {
            return this.moveItemStackTo(itemStack, te.AUGMENT_BLUE, te.AUGMENT_BLUE+1, false);
        }
        return true;
    }



    private boolean moveItemStackWithinInventory( ItemStack itemStack, final int index ) {
        if (index >= 19 && index < 19 + 9*3) {
            return this.moveItemStackTo(itemStack, 19 + 9*3, 19 + 9*4, false);
        } else if (index >= 19 + 9*3 && index < 19 + 9*4) {
            return this.moveItemStackTo(itemStack, 19, 19 + 9*3, false);
        }
        return true;
    }



    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

}
