// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.guing.common.components.dialogs;

import java.awt.Component;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;

/**
 * A dialog that has a close button.
 */
public class ClosableDialog
    extends
      JDialog {

  /**
   * Creates new instance.
   *
   * @param parent The dialog's parent.
   * @param modal Whether the dialog is modal or not.
   * @param content The dialog's actual content.
   * @param title The dialog's title.
   */
  @SuppressWarnings("this-escape")
  public ClosableDialog(Component parent, boolean modal, JComponent content, String title) {
    super(JOptionPane.getFrameForComponent(parent), title, modal);
    initComponents();
    getContentPane().add(content, java.awt.BorderLayout.CENTER);
    content.setBorder(new EmptyBorder(new Insets(4, 4, 4, 4)));
    getRootPane().setDefaultButton(buttonClose);
    pack();
  }

  /**
   * Closes the dialog.
   */
  private void doClose() {
    setVisible(false);
    dispose();
  }

  // FORMATTER:OFF
  // CHECKSTYLE:OFF
  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelButton = new javax.swing.JPanel();
        buttonClose = new CancelButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        buttonClose.setFont(buttonClose.getFont().deriveFont(buttonClose.getFont().getStyle() | java.awt.Font.BOLD));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("i18n/org/opentcs/plantoverview/system"); // NOI18N
        buttonClose.setText(bundle.getString("closableDialog.button_close.text")); // NOI18N
        buttonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCloseActionPerformed(evt);
            }
        });
        panelButton.add(buttonClose);

        getContentPane().add(panelButton, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents
  // CHECKSTYLE:ON
  // FORMATTER:ON

  private void buttonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCloseActionPerformed
    doClose();
  }//GEN-LAST:event_buttonCloseActionPerformed

  /**
   * Closes the dialog
   */
  private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
    doClose();
  }//GEN-LAST:event_closeDialog

  // FORMATTER:OFF
  // CHECKSTYLE:OFF
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClose;
    private javax.swing.JPanel panelButton;
    // End of variables declaration//GEN-END:variables
  // CHECKSTYLE:ON
  // FORMATTER:ON
}
