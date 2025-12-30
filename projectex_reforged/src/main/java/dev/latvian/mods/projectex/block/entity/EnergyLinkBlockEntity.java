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

package dev.latvian.mods.projectex.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

/**
 * Energy Link block entity that converts stored EMC to Forge Energy (FE).
 *
 * Conversion rate: 1 EMC = 10 FE (configurable)
 *
 * This block accumulates EMC like other Link blocks, but instead of only
 * transferring to player knowledge, it also exposes an IEnergyStorage capability
 * to provide FE to adjacent energy consumers.
 */
public class EnergyLinkBlockEntity extends LinkBaseBlockEntity {
	// EMC to FE conversion rate (1 EMC = 10 FE)
	public static final int EMC_TO_FE_RATIO = 10;

	// Maximum FE that can be extracted per tick
	public static final int MAX_FE_EXTRACT_PER_TICK = 10000;

	public EnergyLinkBlockEntity(BlockPos pos, BlockState state) {
		super(ProjectEXBlockEntities.ENERGY_LINK.get(), pos, state);
	}

	/**
	 * Get the IEnergyStorage capability for the specified side.
	 * This allows adjacent blocks to extract FE from the Energy Link.
	 *
	 * @param side The side from which energy is being accessed (null for internal access)
	 * @return IEnergyStorage implementation for this block entity
	 */
	public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
		return new IEnergyStorage() {
			@Override
			public int receiveEnergy(int maxReceive, boolean simulate) {
				// Energy Link cannot receive FE, only provide it
				return 0;
			}

			@Override
			public int extractEnergy(int maxExtract, boolean simulate) {
				if (storedEMC.equals(BigInteger.ZERO)) {
					return 0;
				}

				// Limit extraction to configured maximum per tick
				int actualMaxExtract = Math.min(maxExtract, MAX_FE_EXTRACT_PER_TICK);

				// Calculate how much EMC this represents
				long emcNeeded = (actualMaxExtract + EMC_TO_FE_RATIO - 1) / EMC_TO_FE_RATIO; // Round up

				// Check how much EMC we actually have available
				BigInteger availableEmc = storedEMC;
				long emcToUse = Math.min(emcNeeded, availableEmc.min(BigInteger.valueOf(Long.MAX_VALUE)).longValue());

				if (emcToUse <= 0) {
					return 0;
				}

				// Calculate actual FE to provide
				int feToProvide = (int) (emcToUse * EMC_TO_FE_RATIO);
				feToProvide = Math.min(feToProvide, actualMaxExtract);

				if (!simulate && feToProvide > 0) {
					// Actually consume the EMC
					long emcConsumed = (feToProvide + EMC_TO_FE_RATIO - 1) / EMC_TO_FE_RATIO; // Round up
					storedEMC = storedEMC.subtract(BigInteger.valueOf(emcConsumed));
					setChanged();
				}

				return feToProvide;
			}

			@Override
			public int getEnergyStored() {
				// Return current FE equivalent (capped at Integer.MAX_VALUE)
				BigInteger feEquivalent = storedEMC.multiply(BigInteger.valueOf(EMC_TO_FE_RATIO));
				if (feEquivalent.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
					return Integer.MAX_VALUE;
				}
				return feEquivalent.intValue();
			}

			@Override
			public int getMaxEnergyStored() {
				// Effectively unlimited, but report a reasonable cap for UI purposes
				return Integer.MAX_VALUE;
			}

			@Override
			public boolean canExtract() {
				return true;
			}

			@Override
			public boolean canReceive() {
				return false;
			}
		};
	}
}
