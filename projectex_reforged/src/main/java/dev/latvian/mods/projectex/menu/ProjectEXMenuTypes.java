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

package dev.latvian.mods.projectex.menu;

import dev.latvian.mods.projectex.ProjectEX;
import dev.latvian.mods.projectex.container.AlchemyTableMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Registry for custom menu types
 */
public class ProjectEXMenuTypes {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, ProjectEX.MOD_ID);

	/**
	 * Alchemy Table menu type - supports both block and portable item variants
	 */
	public static final Supplier<MenuType<AlchemyTableMenu>> ALCHEMY_TABLE = REGISTRY.register("alchemy_table",
			() -> IMenuTypeExtension.create(AlchemyTableMenu::fromNetwork));
}
