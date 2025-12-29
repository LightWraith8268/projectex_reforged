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

package dev.latvian.mods.projectex.data;

import dev.latvian.mods.projectex.Matter;
import dev.latvian.mods.projectex.ProjectEX;
import dev.latvian.mods.projectex.Star;
import dev.latvian.mods.projectex.block.ProjectEXBlocks;
import dev.latvian.mods.projectex.item.ProjectEXItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class ProjectEXRecipeProvider extends RecipeProvider {
	public ProjectEXRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
	}

	@Override
	protected void buildRecipes(RecipeOutput output) {
		// Collector tier upgrades
		Matter prev = null;
		for (Matter matter : Matter.VALUES) {
			if (prev != null) {
				ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ProjectEXBlocks.COLLECTOR.get(matter).get())
					.requires(ProjectEXBlocks.COLLECTOR.get(prev).get())
					.requires(matter.getItem().get())  // Use getItem() which handles both matter items and reference items
					.group("projectex:collector")
					.unlockedBy("has_matter", has(matter.getItem().get()))
					.save(output, ProjectEX.MOD_ID + ":collector/" + matter.name);
			}
			prev = matter;
		}

		// Power Flower tier upgrades
		prev = null;
		for (Matter matter : Matter.VALUES) {
			if (prev != null) {
				ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ProjectEXBlocks.POWER_FLOWER.get(matter).get())
					.requires(ProjectEXBlocks.POWER_FLOWER.get(prev).get())
					.requires(matter.getItem().get())  // Use getItem() which handles both matter items and reference items
					.group("projectex:power_flower")
					.unlockedBy("has_matter", has(matter.getItem().get()))
					.save(output, ProjectEX.MOD_ID + ":power_flower/" + matter.name);
			}
			prev = matter;
		}

		// Relay tier upgrades
		prev = null;
		for (Matter matter : Matter.VALUES) {
			if (prev != null) {
				ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ProjectEXBlocks.RELAY.get(matter).get())
					.requires(ProjectEXBlocks.RELAY.get(prev).get())
					.requires(matter.getItem().get())  // Use getItem() which handles both matter items and reference items
					.group("projectex:relay")
					.unlockedBy("has_matter", has(matter.getItem().get()))
					.save(output, ProjectEX.MOD_ID + ":relay/" + matter.name);
			}
			prev = matter;
		}

		// Magnum Star tier upgrades
		// Note: First tier (Ein) is crafted from 4x ProjectE Klein Star Omega in a shapeless recipe
		// This should be added via ProjectE integration later
		Star prevStar = null;
		for (Star star : Star.VALUES) {
			if (prevStar != null) {
				// 4x previous tier = 1x next tier
				ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ProjectEXItems.MAGNUM_STAR.get(star).get())
					.requires(ProjectEXItems.MAGNUM_STAR.get(prevStar).get())
					.requires(ProjectEXItems.MAGNUM_STAR.get(prevStar).get())
					.requires(ProjectEXItems.MAGNUM_STAR.get(prevStar).get())
					.requires(ProjectEXItems.MAGNUM_STAR.get(prevStar).get())
					.group("projectex:magnum_star")
					.unlockedBy("has_prev_star", has(ProjectEXItems.MAGNUM_STAR.get(prevStar).get()))
					.save(output, ProjectEX.MOD_ID + ":magnum_star/" + star.name);
			}
			prevStar = star;
		}

		// Colossal Star tier upgrades
		// Note: First tier (Ein) is crafted from 4x Magnum Star Omega in a shapeless recipe
		prevStar = null;
		for (Star star : Star.VALUES) {
			if (prevStar != null) {
				// 4x previous tier = 1x next tier
				ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ProjectEXItems.COLOSSAL_STAR.get(star).get())
					.requires(ProjectEXItems.COLOSSAL_STAR.get(prevStar).get())
					.requires(ProjectEXItems.COLOSSAL_STAR.get(prevStar).get())
					.requires(ProjectEXItems.COLOSSAL_STAR.get(prevStar).get())
					.requires(ProjectEXItems.COLOSSAL_STAR.get(prevStar).get())
					.group("projectex:colossal_star")
					.unlockedBy("has_prev_star", has(ProjectEXItems.COLOSSAL_STAR.get(prevStar).get()))
					.save(output, ProjectEX.MOD_ID + ":colossal_star/" + star.name);
			}
			prevStar = star;
		}

		// Alchemy Table recipe
		// [Dark Matter] [Alchemy Bag] [Dark Matter]
		// [Alchemy Bag] [Stone Table] [Alchemy Bag]
		// [Dark Matter] [Alchemy Bag] [Dark Matter]
		// Note: Using Obsidian as placeholder for Stone Table and Ender Chests for Alchemy Bags until ProjectE items are available
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ProjectEXBlocks.ALCHEMY_TABLE.get())
			.pattern("DMD")
			.pattern("ASA")
			.pattern("DMD")
			.define('D', Items.OBSIDIAN)  // Placeholder for Dark Matter
			.define('M', Items.ENDER_CHEST)  // Placeholder for Alchemy Bag
			.define('A', Items.ENDER_CHEST)  // Placeholder for Alchemy Bag
			.define('S', ProjectEXBlocks.STONE_TABLE.get())
			.group("projectex")
			.unlockedBy("has_stone_table", has(ProjectEXBlocks.STONE_TABLE.get()))
			.save(output, ProjectEX.MOD_ID + ":alchemy_table");

		// Arcane Tablet recipe
		// [Red Matter] [Alchemy Table] [Red Matter]
		// [Red Matter] [Transmutation Tablet] [Red Matter]
		// [Red Matter] [Red Matter] [Red Matter]
		// Note: Using Nether Star as placeholder for Red Matter and Ender Chest for Transmutation Tablet until ProjectE items are available
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ProjectEXItems.ARCANE_TABLET.get())
			.pattern("RAR")
			.pattern("RTR")
			.pattern("RRR")
			.define('R', Items.NETHER_STAR)  // Placeholder for Red Matter
			.define('A', ProjectEXBlocks.ALCHEMY_TABLE.get())
			.define('T', Items.ENDER_CHEST)  // Placeholder for Transmutation Tablet
			.group("projectex")
			.unlockedBy("has_alchemy_table", has(ProjectEXBlocks.ALCHEMY_TABLE.get()))
			.save(output, ProjectEX.MOD_ID + ":arcane_tablet");
	}
}
