/*
 * ProjectEX
 *
 * Copyright (C) 2024 LatvianModder (original author)
 * Copyright (C) 2024 LightWraith8268 (1.21.1+ NeoForge port)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */

package dev.latvian.mods.projectex.container;

import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Optional;

/**
 * Alchemy Table Menu - Enhanced Transmutation Container with Crafting Grid
 *
 * Extends ProjectE's TransmutationContainer to add:
 * - 3x3 Crafting grid (slots 0-8)
 * - Crafting output slot (slot 9)
 * - Klein Star charging slot (slot 10)
 *
 * Implementation follows Phase 1 of ALCHEMY_TABLE_SPEC.md
 */
public class AlchemyTableMenu extends TransmutationContainer {

	// Crafting grid container (3x3 = 9 slots)
	private final TransientCraftingContainer craftingGrid = new TransientCraftingContainer(this, 3, 3);

	// Crafting result container (1 slot)
	private final ResultContainer craftResult = new ResultContainer();

	// Klein Star charging container (1 slot)
	private final SimpleContainer kleinStarSlot = new SimpleContainer(1);

	// Player reference for recipe lookup
	private final Player player;

	/**
	 * Network constructor - called on client when opening GUI from packet
	 */
	public static AlchemyTableMenu fromNetwork(int windowId, Inventory playerInv, FriendlyByteBuf data) {
		// Read hand from packet (main hand vs offhand)
		InteractionHand hand = data.readEnum(InteractionHand.class);
		return new AlchemyTableMenu(windowId, playerInv, hand);
	}

	/**
	 * Constructor for portable Arcane Tablet (held in hand)
	 */
	public AlchemyTableMenu(int windowId, Inventory playerInv, InteractionHand hand) {
		// Call parent TransmutationContainer constructor with hand
		// This initializes the transmutation inventory and slots
		super(windowId, playerInv, hand, 0); // 0 = no additional slots yet

		this.player = playerInv.player;

		// Add our custom slots for crafting and charging
		initCustomSlots();
	}

	/**
	 * Constructor for block-based Alchemy Table
	 */
	public AlchemyTableMenu(int windowId, Inventory playerInv) {
		// Call parent TransmutationContainer constructor without hand
		// This initializes the transmutation inventory and slots
		super(windowId, playerInv);

		this.player = playerInv.player;

		// Add our custom slots for crafting and charging
		initCustomSlots();
	}

	/**
	 * Initialize custom slots for crafting grid, result, and Klein Star charging
	 */
	private void initCustomSlots() {
		// Add 3x3 crafting grid (9 slots)
		// Position on right side of GUI, offset from transmutation grid
		int craftingStartX = 98;  // X position for crafting grid start
		int craftingStartY = 18;  // Y position for crafting grid start

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				int slotIndex = col + row * 3;
				addSlot(new Slot(craftingGrid, slotIndex,
						craftingStartX + col * 18,
						craftingStartY + row * 18));
			}
		}

		// Add crafting result slot (1 slot)
		// Position to the right of the crafting grid
		addSlot(new Slot(craftResult, 0, craftingStartX + 94, craftingStartY + 18) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false; // Result slot - can't place items directly
			}

			@Override
			public void onTake(Player player, ItemStack stack) {
				// Called when player takes crafted item
				onCraftedItem(stack);
				super.onTake(player, stack);
			}
		});

		// Add Klein Star charging slot (1 slot)
		// Position at bottom-left, similar to ProjectE transmutation table
		addSlot(new Slot(kleinStarSlot, 0, 12, 74) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				// Only accept Klein Stars (and our custom Magnum/Colossal/Final stars)
				return SlotPredicates.RELAY_INV.test(stack);
			}
		});
	}

	/**
	 * Called when an item is crafted - handles recipe consumption and auto-learning
	 * This is Phase 3 (Auto-Learn) preparation
	 */
	private void onCraftedItem(ItemStack craftedStack) {
		// TODO Phase 2: Implement ingredient consumption with inventory priority
		// TODO Phase 3: Implement auto-learn system for crafted items with EMC

		// For now, just clear the crafting grid (vanilla behavior)
		craftingGrid.clearContent();
		updateCraftingResult();
	}

	/**
	 * Update the crafting result slot based on current crafting grid contents
	 */
	private void updateCraftingResult() {
		if (player.level().isClientSide) {
			return; // Only process on server
		}

		// Create CraftingInput from current crafting grid contents
		CraftingInput craftingInput = craftingGrid.asCraftInput();

		// Look up recipe based on crafting grid contents
		Optional<RecipeHolder<CraftingRecipe>> optionalRecipe = player.level()
				.getRecipeManager()
				.getRecipeFor(RecipeType.CRAFTING, craftingInput, player.level());

		if (optionalRecipe.isPresent()) {
			RecipeHolder<CraftingRecipe> recipeHolder = optionalRecipe.get();
			CraftingRecipe recipe = recipeHolder.value();

			// Check if player can craft this recipe
			ItemStack result = recipe.assemble(craftingInput, player.level().registryAccess());
			craftResult.setItem(0, result);
		} else {
			// No valid recipe - clear result slot
			craftResult.setItem(0, ItemStack.EMPTY);
		}

		// Notify client of change
		broadcastChanges();
	}

	/**
	 * Called when container contents change - update crafting result
	 */
	@Override
	public void slotsChanged(Container container) {
		super.slotsChanged(container);

		if (container == craftingGrid) {
			updateCraftingResult();
		}
	}

	/**
	 * Called when player closes the GUI
	 */
	@Override
	public void removed(Player player) {
		super.removed(player);

		// Return crafting grid items to player inventory
		if (!player.level().isClientSide) {
			for (int i = 0; i < craftingGrid.getContainerSize(); i++) {
				ItemStack stack = craftingGrid.removeItemNoUpdate(i);
				if (!stack.isEmpty()) {
					player.drop(stack, false);
				}
			}

			// Return Klein Star to player inventory
			ItemStack kleinStar = kleinStarSlot.removeItemNoUpdate(0);
			if (!kleinStar.isEmpty()) {
				player.drop(kleinStar, false);
			}
		}
	}

	/**
	 * Shift-click handling - integrates crafting slots with transmutation inventory
	 */
	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		// TODO Phase 2: Implement smart shift-click behavior
		// For now, delegate to parent for transmutation slots
		return super.quickMoveStack(player, slotIndex);
	}

	/**
	 * Check if this menu is still valid (player hasn't moved too far away)
	 */
	@Override
	public boolean stillValid(Player player) {
		// For portable Arcane Tablet, always valid if player still holds the item
		// For block-based Alchemy Table, check distance to block
		// For now, always valid (portable behavior)
		return true;
	}
}
