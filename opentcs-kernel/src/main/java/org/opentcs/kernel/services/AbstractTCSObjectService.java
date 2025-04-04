// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.services;

import static java.util.Objects.requireNonNull;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Set;
import java.util.function.Predicate;
import org.opentcs.access.CredentialsException;
import org.opentcs.access.KernelRuntimeException;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.data.ObjectHistory;
import org.opentcs.data.ObjectUnknownException;
import org.opentcs.data.TCSObject;
import org.opentcs.data.TCSObjectReference;

/**
 * Delegate method calls to the {@link TCSObjectService} implementation.
 */
public abstract class AbstractTCSObjectService
    implements
      TCSObjectService {

  /**
   * The tcs object service to delegate method calls to.
   */
  private final TCSObjectService objectService;

  /**
   * Creates a new instance.
   *
   * @param objectService The service to delegate method calls to.
   */
  public AbstractTCSObjectService(TCSObjectService objectService) {
    this.objectService = requireNonNull(objectService, "objectService");
  }

  @Override
  public <T extends TCSObject<T>> T fetchObject(Class<T> clazz, TCSObjectReference<T> ref)
      throws CredentialsException {
    requireNonNull(clazz, "clazz");
    requireNonNull(ref, "ref");

    return getObjectService().fetchObject(clazz, ref);
  }

  @Override
  public <T extends TCSObject<T>> T fetchObject(Class<T> clazz, String name)
      throws CredentialsException {
    requireNonNull(clazz, "clazz");

    return getObjectService().fetchObject(clazz, name);
  }

  @Override
  public <T extends TCSObject<T>> Set<T> fetchObjects(Class<T> clazz)
      throws CredentialsException {
    requireNonNull(clazz, "clazz");

    return getObjectService().fetchObjects(clazz);
  }

  @Override
  public <T extends TCSObject<T>> Set<T> fetchObjects(
      @Nonnull
      Class<T> clazz,
      @Nonnull
      Predicate<? super T> predicate
  )
      throws CredentialsException {
    requireNonNull(clazz, "clazz");
    requireNonNull(predicate, "predicate");

    return getObjectService().fetchObjects(clazz, predicate);
  }

  @Override
  public void updateObjectProperty(
      TCSObjectReference<?> ref,
      String key,
      @Nullable
      String value
  )
      throws ObjectUnknownException,
        CredentialsException {
    requireNonNull(ref, "ref");
    requireNonNull(key, "key");

    getObjectService().updateObjectProperty(ref, key, value);
  }

  @Override
  public void appendObjectHistoryEntry(TCSObjectReference<?> ref, ObjectHistory.Entry entry)
      throws ObjectUnknownException,
        KernelRuntimeException {
    requireNonNull(ref, "ref");
    requireNonNull(entry, "entry");

    getObjectService().appendObjectHistoryEntry(ref, entry);
  }

  /**
   * Retruns the {@link TCSObjectService} implementation being used.
   *
   * @return The {@link TCSObjectService} implementation being used.
   */
  public TCSObjectService getObjectService() {
    return objectService;
  }
}
