// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.components.kernel.services;

import java.util.List;
import java.util.function.Predicate;
import org.opentcs.access.KernelRuntimeException;
import org.opentcs.data.notification.UserNotification;

/**
 * Provides methods concerning {@link UserNotification}s.
 */
public interface NotificationService {

  /**
   * Returns a list of user notifications.
   *
   * @param predicate A filter predicate that accepts the user notifications to be returned. May be
   * {@code null} to return all existing user notifications.
   * @return A list of user notifications, in the order in which they were published.
   * @throws KernelRuntimeException In case there is an exception executing this method.
   */
  List<UserNotification> fetchUserNotifications(Predicate<UserNotification> predicate)
      throws KernelRuntimeException;

  /**
   * Publishes a user notification.
   *
   * @param notification The notification to be published.
   * @throws KernelRuntimeException In case there is an exception executing this method.
   */
  void publishUserNotification(UserNotification notification)
      throws KernelRuntimeException;
}
