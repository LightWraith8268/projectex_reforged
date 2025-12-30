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

package dev.latvian.mods.projectex.block;

import dev.latvian.mods.projectex.Matter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * Matter Block - solid glowing decorative block for crafting tiered Energy Links.
 *
 * Features:
 * - Full-bright glowing texture (light level 15)
 * - Color-coded per Matter tier
 * - Used in Energy Link upgrade recipes (4 blocks in + pattern)
 */
public class MatterBlock extends Block {
	public final Matter matter;

	public MatterBlock(Matter matter) {
		super(BlockBehaviour.Properties.of()
				.mapColor(MapColor.NONE)
				.strength(3.0F, 3.0F)
				.sound(SoundType.METAL)
				.lightLevel(state -> 15)  // Full-bright glow
				.requiresCorrectToolForDrops());
		this.matter = matter;
	}
}
