// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.servicewebapi.v1.converter;

import static java.util.Objects.requireNonNull;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.opentcs.access.to.model.BlockCreationTO;
import org.opentcs.data.model.Block;
import org.opentcs.data.model.TCSResourceReference;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.plantmodel.BlockTO;
import org.opentcs.util.Colors;

/**
 * Includes the conversion methods for all Block classes.
 */
public class BlockConverter {

  private final PropertyConverter pConverter;

  @Inject
  public BlockConverter(PropertyConverter pConverter) {
    this.pConverter = requireNonNull(pConverter, "pConverter");
  }

  public List<BlockCreationTO> toBlockCreationTOs(List<BlockTO> blocks) {
    return blocks.stream()
        .map(
            block -> new BlockCreationTO(block.getName())
                .withProperties(pConverter.toPropertyMap(block.getProperties()))
                .withMemberNames(block.getMemberNames())
                .withType(Block.Type.valueOf(block.getType()))
                .withLayout(
                    new BlockCreationTO.Layout(
                        Colors.decodeFromHexRGB(block.getLayout().getColor())
                    )
                )
        )
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public List<BlockTO> toBlockTOs(Set<Block> blocks) {
    return blocks.stream()
        .map(
            block -> new BlockTO(block.getName())
                .setType(block.getType().name())
                .setMemberNames(convertMemberNames(block.getMembers()))
                .setLayout(
                    new BlockTO.Layout()
                        .setColor(Colors.encodeToHexRGB(block.getLayout().getColor()))
                )
                .setProperties(pConverter.toPropertyTOs(block.getProperties()))
        )
        .sorted(Comparator.comparing(BlockTO::getName))
        .collect(Collectors.toList());
  }

  private Set<String> convertMemberNames(Set<TCSResourceReference<?>> members) {
    return members.stream()
        .map(TCSResourceReference::getName)
        .collect(Collectors.toSet());
  }
}
