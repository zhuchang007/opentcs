/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uwant.vehicle.route.edit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;

/** @author zhuchang */
public class StandardDialog extends javax.swing.JDialog {

  /** A return status code - returned if Cancel button has been pressed */
  public static final int RET_CANCEL = 0;
  /** A return status code - returned if OK button has been pressed */
  public static final int RET_OK = 1;
  /** Der Inhalt des Dialogs. */
  protected JComponent fContent;

  private int returnStatus = RET_CANCEL;
  /** Creates new form StandardDialog */
  public StandardDialog(Component parent, boolean modal, JComponent content, String title) {
    super(JOptionPane.getFrameForComponent(parent), modal);
    initComponents();
    initSize(content);
    setTitle(title);
    setLocationRelativeTo(parent);
  }

  /**
   * Passt die Größe des Dialogs nach dem Hinzufügen des Panels an.
   *
   * @param content
   */
  protected final void initSize(JComponent content) {
    fContent = content;
    getContentPane().add(content, BorderLayout.CENTER);
    content.setBorder(new EmptyBorder(new Insets(3, 3, 3, 3)));
    getRootPane().setDefaultButton(okButton);
    pack();
  }
  /**
   * Liefert den Inhalt des Dialogs.
   *
   * @return
   */
  public JComponent getContent() {
    return fContent;
  }

  /**
   * return the return status of this dialog - one of RET_OK or RET_CANCEL
   *
   * @return
   */
  public int getReturnStatus() {
    return returnStatus;
  }

  /** Schließt den Dialog. */
  private void doClose(int retStatus) {
    returnStatus = retStatus;
    setVisible(false);
    dispose();
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(300, 300));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        buttonPanel.setName("buttonPanel"); // NOI18N
        buttonPanel.setPreferredSize(new java.awt.Dimension(400, 40));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("i18n/uwant/vehicle/commadapter/Bundle"); // NOI18N
        okButton.setText(bundle.getString("standardDialog.okButton.text")); // NOI18N
        okButton.setName("okButton"); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(okButton);

        cancelButton.setText(bundle.getString("standardDialog.cancelButton.text")); // NOI18N
        cancelButton.setName("cancelButton"); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void okButtonActionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_okButtonActionPerformed
    // TODO add your handling code here:
    doClose(RET_OK);
  } // GEN-LAST:event_okButtonActionPerformed

  private void cancelButtonActionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_cancelButtonActionPerformed
    // TODO add your handling code here:
    doClose(RET_CANCEL);
  } // GEN-LAST:event_cancelButtonActionPerformed

  private void closeDialog(java.awt.event.WindowEvent evt) { // GEN-FIRST:event_closeDialog
    // TODO add your handling code here:
    doClose(RET_CANCEL);
  } // GEN-LAST:event_closeDialog

  /** @param args the command line arguments */
  //  public static void main(String args[]) {
  //    /* Set the Nimbus look and feel */
  //    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
  //    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and
  // feel.
  //         * For details see
  // http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
  //     */
  //    try {
  //      for (javax.swing.UIManager.LookAndFeelInfo info
  //           : javax.swing.UIManager.getInstalledLookAndFeels()) {
  //        if ("Nimbus".equals(info.getName())) {
  //          javax.swing.UIManager.setLookAndFeel(info.getClassName());
  //          break;
  //        }
  //      }
  //    }
  //    catch (ClassNotFoundException ex) {
  //
  // java.util.logging.Logger.getLogger(StandardDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
  //    }
  //    catch (InstantiationException ex) {
  //
  // java.util.logging.Logger.getLogger(StandardDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
  //    }
  //    catch (IllegalAccessException ex) {
  //
  // java.util.logging.Logger.getLogger(StandardDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
  //    }
  //    catch (javax.swing.UnsupportedLookAndFeelException ex) {
  //
  // java.util.logging.Logger.getLogger(StandardDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
  //    }
  //    //</editor-fold>
  //
  //    /* Create and display the dialog */
  //    java.awt.EventQueue.invokeLater(new Runnable() {
  //      public void run() {
  //        RouteAddPanel routeAddPanel = new RouteAddPanel();
  //        StandardDialog dialog = new StandardDialog(new javax.swing.JFrame(),
  // true,routeAddPanel,"nihao");
  //        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
  //          @Override
  //          public void windowClosing(java.awt.event.WindowEvent e) {
  //            System.exit(0);
  //          }
  //        });
  //        dialog.setVisible(true);
  //      }
  //    });
  //  }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
}
