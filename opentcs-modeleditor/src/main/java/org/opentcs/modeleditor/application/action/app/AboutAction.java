// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.modeleditor.application.action.app;

import static java.util.Objects.requireNonNull;
import static javax.swing.Action.LARGE_ICON_KEY;
import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.SMALL_ICON;
import static org.opentcs.modeleditor.util.I18nPlantOverviewModeling.MENU_PATH;

import jakarta.inject.Inject;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.opentcs.access.SharedKernelServicePortalProvider;
import org.opentcs.customizations.plantoverview.ApplicationFrame;
import org.opentcs.guing.common.application.ApplicationState;
import org.opentcs.guing.common.util.ImageDirectory;
import org.opentcs.modeleditor.application.OpenTCSView;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;
import org.opentcs.util.Environment;

/**
 * Displays a dialog showing information about the application.
 */
public class AboutAction
    extends
      AbstractAction {

  /**
   * This action's ID.
   */
  public static final String ID = "application.about";

  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(MENU_PATH);
  /**
   * Stores the application's current state.
   */
  private final ApplicationState appState;
  /**
   * Provides access to a portal.
   */
  private final SharedKernelServicePortalProvider portalProvider;
  /**
   * The parent component for dialogs shown by this action.
   */
  private final Component dialogParent;

  /**
   * Creates a new instance.
   *
   * @param appState Stores the application's current state.
   * @param portalProvider Provides access to a portal.
   * @param dialogParent The parent component for dialogs shown by this action.
   */
  @Inject
  @SuppressWarnings("this-escape")
  public AboutAction(
      ApplicationState appState,
      SharedKernelServicePortalProvider portalProvider,
      @ApplicationFrame
      Component dialogParent
  ) {
    this.appState = requireNonNull(appState, "appState");
    this.portalProvider = requireNonNull(portalProvider, "portalProvider");
    this.dialogParent = requireNonNull(dialogParent, "dialogParent");

    putValue(NAME, BUNDLE.getString("aboutAction.name"));
    putValue(MNEMONIC_KEY, Integer.valueOf('A'));

    ImageIcon icon = ImageDirectory.getImageIcon("/menu/help-contents.png");
    putValue(SMALL_ICON, icon);
    putValue(LARGE_ICON_KEY, icon);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    JOptionPane.showMessageDialog(
        dialogParent,
        "<html><p><b>" + OpenTCSView.NAME + "</b><br> "
            + BUNDLE.getFormatted(
                "aboutAction.optionPane_applicationInformation.message.baselineVersion",
                Environment.getBaselineVersion()
            )
            + "<br>"
            + BUNDLE.getFormatted(
                "aboutAction.optionPane_applicationInformation.message.customization",
                Environment.getCustomizationName(),
                Environment.getCustomizationVersion()
            )
            + "<br>"
            + BUNDLE.getString(
                "aboutAction.optionPane_applicationInformation.message.copyright"
            )
            + "<br>"
            + BUNDLE.getString(
                "aboutAction.optionPane_applicationInformation.message.runningOn"
            )
            + "<br>"
            + "Java: "
            + System.getProperty("java.version")
            + ", "
            + System.getProperty("java.vendor")
            + "<br>"
            + "JVM: "
            + System.getProperty("java.vm.version")
            + ", "
            + System.getProperty("java.vm.vendor")
            + "<br>"
            + "OS: "
            + System.getProperty("os.name")
            + " "
            + System.getProperty("os.version")
            + ", "
            + System.getProperty("os.arch")
            + "<br>"
            + "<b>Kernel</b><br>"
            + portalProvider.getPortalDescription()
            + "<br>"
            + BUNDLE.getFormatted(
                "aboutAction.optionPane_applicationInformation.message.mode",
                appState.getOperationMode()
            )
            + "</p></html>",
        BUNDLE.getString(
            "aboutAction.optionPane_applicationInformation.title"
        ),
        JOptionPane.PLAIN_MESSAGE,
        new ImageIcon(
            getClass().getResource("/org/opentcs/guing/res/symbols/openTCS/openTCS.300x132.gif")
        )
    );
  }
}
