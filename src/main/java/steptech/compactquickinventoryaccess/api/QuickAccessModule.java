package steptech.compactquickinventoryaccess.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import steptech.compactquickinventoryaccess.api.wrapper.ModuleInstructionWrapper;

import java.util.ArrayList;
import java.util.List;

public interface QuickAccessModule {
    static @Nullable ItemStack takeOneFromItemStack(@NotNull InventoryView inventoryView, int rawSlot) {
        final ItemStack itemStack = inventoryView.getItem(rawSlot);
        if (itemStack != null) {
            final int amount = itemStack.getAmount();
            if (amount > 1) {
                itemStack.setAmount(amount - 1);
                final ItemStack copy = itemStack.clone();
                copy.setAmount(1);
                return copy;
            } else {
                inventoryView.setItem(rawSlot, null);
                return itemStack;
            }
        }
        return null;
    }

    /**
     * Gets all contents of the current {@link InventoryView} as obtained by {@link Player#getOpenInventory()} with the
     * index of their raw slot
     * @param player The {@link Player} to get the {@link InventoryView}s contents from
     * @return A {@link List} with all contents of the current {@link InventoryView} on the index of the raw slot.
     * There for some {@link ItemStack}s may be {@code null}.
     */
    static @NotNull List<@Nullable ItemStack> getInventoryContents(@NotNull Player player) {
        final List<@Nullable ItemStack> contents = new ArrayList<>();
        final InventoryView inventoryView = player.getOpenInventory();
        for (int rawSlot = 0, bound = inventoryView.countSlots(); rawSlot < bound; rawSlot++) {
            contents.add(inventoryView.getItem(rawSlot));
        }
        return contents;
    }

    boolean matchesItem(@NotNull ItemStack clickedItem);
    boolean matchesOtherRequirements(@NotNull Player player);
    @NotNull ModuleInstructionWrapper modifyInventory(@NotNull Player player, int rawSlot);
}
