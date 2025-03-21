// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.guing.common.components.properties.panel;

import static java.util.Objects.requireNonNull;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.opentcs.guing.base.components.properties.type.AbstractProperty;
import org.opentcs.guing.base.components.properties.type.ModelAttribute;
import org.opentcs.guing.base.components.properties.type.Property;
import org.opentcs.guing.base.components.properties.type.Selectable;
import org.opentcs.guing.common.components.dialogs.DetailsDialogContent;
import org.opentcs.guing.common.util.I18nPlantOverview;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;

/**
 * Panel for selecting a property from a combo box.
 */
public class SelectionPropertyEditorPanel
    extends
      JPanel
    implements
      DetailsDialogContent {

  private final ListCellRenderer<Object> listCellRenderer;
  /**
   * Das Attribut.
   */
  private AbstractProperty fProperty;

  /**
   * Creates new form SelectionPropertyEditorPanel
   */
  @SuppressWarnings({"unchecked", "this-escape"})
  public SelectionPropertyEditorPanel(ListCellRenderer<?> listCellRenderer) {
    requireNonNull(listCellRenderer, "listCellRenderer");

    this.listCellRenderer = (ListCellRenderer<Object>) listCellRenderer;
    initComponents();
  }

  @Override // DetailsDialogContent
  public void setProperty(Property property) {
    fProperty = (AbstractProperty) property;

    @SuppressWarnings("unchecked")
    ComboBoxModel<Object> model = new DefaultComboBoxModel<>(
        ((Selectable<Object>) fProperty).getPossibleValues().toArray()
    );
    valueComboBox.setModel(model);

    Object value = fProperty.getValue();
    valueComboBox.setSelectedItem(value);
    valueComboBox.setRenderer(listCellRenderer);
  }

  @Override // DetailsDialogContent
  public void updateValues() {
    Object selectedItem = valueComboBox.getSelectedItem();
    fProperty.setValue(selectedItem);
    fProperty.setChangeState(ModelAttribute.ChangeState.DETAIL_CHANGED);
  }

  @Override // DetailsDialogContent
  public String getTitle() {
    return ResourceBundleUtil.getBundle(I18nPlantOverview.PROPERTIES_PATH)
        .getString("selectionPropertyEditorPanel.title");
  }

  @Override // DetailsDialogContent
  public Property getProperty() {
    return fProperty;
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
    java.awt.GridBagConstraints gridBagConstraints;

    valueLabel = new javax.swing.JLabel();
    valueComboBox = new javax.swing.JComboBox<>();

    setLayout(new java.awt.GridBagLayout());

    valueLabel.setFont(valueLabel.getFont());
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("i18n/org/opentcs/plantoverview/panels/propertyEditing"); // NOI18N
    valueLabel.setText(bundle.getString("selectionPropertyEditorPanel.label_value.text")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
    add(valueLabel, gridBagConstraints);

    valueComboBox.setFont(valueComboBox.getFont());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
    add(valueComboBox, gridBagConstraints);
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JComboBox<Object> valueComboBox;
  private javax.swing.JLabel valueLabel;
  // End of variables declaration//GEN-END:variables
  // CHECKSTYLE:ON
  // FORMATTER:ON
}
