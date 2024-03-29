package steptech.compactquickinventoryaccess.api;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class TempItemRemover {
    private final Supplier<@NotNull InventoryView> inventoryViewSupplier;
    private final int rawSlot;
    private final int slot;
    private final InventoryType topInventoryType;
    private final int topInventorySize;
    private ItemStack removedItem;
    private boolean hasBeenPutBack = false;

    public TempItemRemover(@NotNull Supplier<@NotNull InventoryView> inventoryViewSupplier, int rawSlot) {
        this.inventoryViewSupplier = inventoryViewSupplier;
        this.rawSlot = rawSlot;

        //save some data for later
        final InventoryView inventoryView = inventoryViewSupplier.get();
        this.slot = inventoryView.convertSlot(rawSlot);
        final Inventory topInventory = inventoryView.getTopInventory();
        this.topInventoryType = topInventory.getType();
        this.topInventorySize = topInventory.getSize();

        //remove item
        this.removedItem = removeOne(inventoryView, rawSlot);
    }

    private @Nullable ItemStack removeOne(@NotNull InventoryView inventoryView, int rawSlot) {
        final ItemStack item = inventoryView.getItem(rawSlot);
        if (item == null) return null;

        final int amount = item.getAmount();
        if (amount > 1) {
            item.setAmount(amount - 1);
            final ItemStack clone = item.clone();
            clone.setAmount(1);
            return clone;
        } else {
            inventoryView.setItem(rawSlot, null);
            return item;
        }
    }

    public void putItemBack() {
        putItemBack(false);
    }

    public void putItemBack(boolean force) {
        //check if already has been put back
        if (this.hasBeenPutBack && !force) return;
        this.hasBeenPutBack = true;

        final InventoryView inventoryView = this.inventoryViewSupplier.get();
        checkForSpaceOrDrop(inventoryView.getPlayer(),
                rawSlotRefersToBottomInventory() ?
                        inventoryView.getBottomInventory() :
                        hasSimilarProperties(inventoryView) ?
                                inventoryView.getTopInventory() :
                                null,
                this.slot);
    }

    private void checkForSpaceOrDrop(@NotNull HumanEntity humanEntity, @Nullable Inventory inventory, int slot) {
        if (inventory != null && isEmptySlot(inventory.getItem(slot))) {
            inventory.setItem(slot, this.removedItem);
        } else if (this.removedItem != null) {
            final Location location = humanEntity.getLocation();
            location.getWorld().dropItem(location, this.removedItem);
        }
    }

    private boolean hasSimilarProperties(@NotNull InventoryView inventoryView) {
        final Inventory topInventory = inventoryView.getTopInventory();
        return topInventory.getType() == this.topInventoryType && topInventory.getSize() == this.topInventorySize;
    }

    private boolean rawSlotRefersToBottomInventory() {
        return this.rawSlot >= this.topInventorySize;
    }

    private boolean isEmptySlot(@Nullable ItemStack currentItem) {
        return currentItem == null || currentItem.getType() == Material.AIR;
    }

    public @Nullable ItemStack getRemovedItem() {
        return removedItem;
    }

    public void setRemovedItem(@Nullable ItemStack removedItem) {
        this.removedItem = removedItem;
    }
}
