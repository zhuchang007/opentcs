// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.guing.common.components.tree.elements;

import jakarta.annotation.Nullable;
import java.util.Set;
import javax.swing.JPopupMenu;

/**
 * A context indicating if an user object is contained in
 * the components, blocks or groups tree view. Currently it only
 * offers a tree dependant popup menu.
 */
public interface UserObjectContext {

  /**
   * Returns a popup menu with actions for this context.
   *
   * @param selectedUserObjects The user objects that are currently selected
   * in the tree view.
   * @return A popup menu.
   */
  JPopupMenu getPopupMenu(
      @Nullable
      Set<UserObject> selectedUserObjects
  );

  /**
   * Called after a specific item was removed from the tree (via the <code>
   * DeleteAction</code>.
   *
   * @param userObject The UserObject affected.
   * @return <code>true</code>, if it was successfully removed.
   */
  boolean removed(UserObject userObject);

  /**
   * Returns the type of this context.
   *
   * @return One of CONTEXT_TYPE.
   */
  ContextType getType();

  /**
   * Supported context types.
   */
  enum ContextType {

    /**
     * Component.
     */
    COMPONENT,
    /**
     * Block.
     */
    BLOCK,
    /**
     * Null context.
     */
    NULL;
  }
}
