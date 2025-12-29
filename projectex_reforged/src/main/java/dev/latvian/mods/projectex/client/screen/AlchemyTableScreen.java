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

package dev.latvian.mods.projectex.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.latvian.mods.projectex.ProjectEX;
import dev.latvian.mods.projectex.container.AlchemyTableMenu;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Client-side GUI for Alchemy Table
 *
 * Combines ProjectE transmutation interface with vanilla crafting grid
 * Layout:
 * - Left: Transmutation grid (inherited from TransmutationContainer)
 * - Right: 3x3 crafting grid + result slot
 * - Bottom-left: Klein Star charging slot
 */
public class AlchemyTableScreen extends AbstractContainerScreen<AlchemyTableMenu> {
	// GUI texture - 256x256 PNG
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
			ProjectEX.MOD_ID,
			"textures/gui/alchemy_table.png"
	);

	public AlchemyTableScreen(AlchemyTableMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);

		// GUI dimensions (width x height in pixels)
		// Wider than standard to accommodate both transmutation and crafting grids
		this.imageWidth = 276;  // Standard chest = 176, we need extra space for crafting
		this.imageHeight = 196; // Standard crafting table = 166, transmutation = 196

		// Title position
		this.titleLabelX = 6;
		this.titleLabelY = 8;

		// Inventory label position (player inventory text)
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		// Render the background texture
		int x = this.leftPos;
		int y = this.topPos;

		// Draw main GUI background
		graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		// Render the background (darkened screen behind GUI)
		super.render(graphics, mouseX, mouseY, partialTick);

		// Render custom overlays (EMC costs, shimmer effects, etc.)
		renderCustomOverlays(graphics, mouseX, mouseY, partialTick);

		// Render tooltips for items under mouse cursor
		this.renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		// Render the title text ("Alchemy Table")
		graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);

		// Render inventory label ("Inventory")
		graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
	}

	/**
	 * Phase 5: Custom rendering overlays
	 * - Purple/gold shimmer for transmuted items
	 * - EMC cost overlays on craftable items
	 * - Klein Star charge display
	 */
	private void renderCustomOverlays(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		// Get player from client Minecraft instance
		if (this.minecraft == null || this.minecraft.player == null) {
			return;
		}

		// Get player's knowledge provider for checking if items can be transmuted
		var knowledge = this.minecraft.player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
		if (knowledge == null) {
			return;
		}

		// Iterate through all slots and render custom overlays
		for (Slot slot : menu.slots) {
			ItemStack stack = slot.getItem();
			if (stack.isEmpty()) {
				continue;
			}

			// Check if this slot is in the crafting grid (not transmutation or inventory)
			// Crafting grid slots are indices 9-17 (3x3) + 18 (result)
			int slotIndex = slot.index;
			boolean isCraftingSlot = slotIndex >= 9 && slotIndex <= 18;

			if (isCraftingSlot) {
				long emcValue = IEMCProxy.INSTANCE.getValue(stack);

				// Render shimmer effect if item can be transmuted
				if (emcValue > 0 && !knowledge.hasKnowledge(stack)) {
					renderTransmutationShimmer(graphics, slot, partialTick);
				}

				// Render EMC cost overlay if item has EMC value
				if (emcValue > 0) {
					renderEMCOverlay(graphics, slot, emcValue);
				}
			}

			// Render Klein Star charge display (slot 8 is Klein Star charging slot)
			if (slotIndex == 8 && stack.getItem().toString().contains("klein_star")) {
				renderKleinStarCharge(graphics, slot, stack);
			}
		}
	}

	/**
	 * Render purple/gold shimmer effect for items that can be transmuted
	 */
	private void renderTransmutationShimmer(GuiGraphics graphics, Slot slot, float partialTick) {
		int x = this.leftPos + slot.x;
		int y = this.topPos + slot.y;

		// Animate shimmer using game time
		float time = (System.currentTimeMillis() % 3000) / 3000.0F;
		float alpha = (float) (Math.sin(time * Math.PI * 2) * 0.3 + 0.5);

		// Render purple/gold gradient shimmer
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		// Purple shimmer layer
		graphics.fill(x, y, x + 16, y + 16, (int) (alpha * 128) << 24 | 0x9932CC);

		// Gold accent shimmer
		graphics.fill(x, y, x + 16, y + 2, (int) (alpha * 200) << 24 | 0xFFD700);

		RenderSystem.disableBlend();
	}

	/**
	 * Render EMC cost overlay on slots
	 */
	private void renderEMCOverlay(GuiGraphics graphics, Slot slot, long emcValue) {
		int x = this.leftPos + slot.x;
		int y = this.topPos + slot.y;

		// Format EMC value for display
		String emcText = formatEMC(emcValue);

		// Render EMC text in bottom-right corner of slot
		graphics.pose().pushPose();
		graphics.pose().translate(0, 0, 200); // Render above items

		// Background shadow for readability
		graphics.drawString(this.font, emcText, x + 17 - this.font.width(emcText), y + 9, 0x000000, false);

		// Gold EMC text
		graphics.drawString(this.font, emcText, x + 16 - this.font.width(emcText), y + 8, 0xFFD700, false);

		graphics.pose().popPose();
	}

	/**
	 * Render Klein Star charge bar
	 */
	private void renderKleinStarCharge(GuiGraphics graphics, Slot slot, ItemStack stack) {
		int x = this.leftPos + slot.x;
		int y = this.topPos + slot.y;

		// Get Klein Star's stored EMC (would need to access star's NBT in real implementation)
		// For now, just render a placeholder charge bar
		int chargePercent = 50; // TODO: Get actual charge from star NBT

		// Render charge bar below slot
		int barWidth = 16;
		int barHeight = 2;
		int filledWidth = (barWidth * chargePercent) / 100;

		graphics.pose().pushPose();
		graphics.pose().translate(0, 0, 200);

		// Background bar (dark gray)
		graphics.fill(x, y + 17, x + barWidth, y + 17 + barHeight, 0xFF333333);

		// Filled bar (gold gradient)
		graphics.fill(x, y + 17, x + filledWidth, y + 17 + barHeight, 0xFFFFD700);

		graphics.pose().popPose();
	}

	/**
	 * Format EMC value for display (abbreviate large numbers)
	 */
	private String formatEMC(long emc) {
		if (emc >= 1_000_000_000) {
			return String.format("%.1fB", emc / 1_000_000_000.0);
		} else if (emc >= 1_000_000) {
			return String.format("%.1fM", emc / 1_000_000.0);
		} else if (emc >= 1_000) {
			return String.format("%.1fK", emc / 1_000.0);
		} else {
			return String.valueOf(emc);
		}
	}
}
