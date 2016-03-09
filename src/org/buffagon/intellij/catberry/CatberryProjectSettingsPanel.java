package org.buffagon.intellij.catberry;

import com.intellij.openapi.util.Comparing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Catberry Plugin settings form.
 *
 * @author Prokofiev Alex
 */
public class CatberryProjectSettingsPanel {
  private JPanel rootPanel;
  private JCheckBox enableCatberrySupportField;
  private JComboBox templateEngineField;
  private JTextField componentsRootField;
  private JTextField storesRootField;
  private JPanel settingsPanel;

  private CatberryReadWriteSettings settings;


  public CatberryProjectSettingsPanel(CatberryReadWriteSettings settings) {
    this.settings = settings;
    enableCatberrySupportField.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateUI();
      }
    });
  }

  private void updateUI() {
    settingsPanel.setEnabled(enableCatberrySupportField.isSelected());
  }

  public JPanel getRootPanel() {
    return rootPanel;
  }

  public JPanel getSettingsPanel() {
    return settingsPanel;
  }

  public boolean isModified() {
    return !(Comparing.equal(settings.isCatberryEnabled(), enableCatberrySupportField.isSelected()) &&
        Comparing.equal(settings.getTemplateEngineName(), templateEngineField.getSelectedItem().toString()) &&
        Comparing.equal(settings.getComponentsRoot(), componentsRootField.getText()) &&
        Comparing.equal(settings.getStoresRoot(), storesRootField.getText()));
  }

  public void apply() {
    settings.setCatberryEnabled(enableCatberrySupportField.isSelected());
    settings.setTemplateEngineName(templateEngineField.getSelectedItem().toString());
    settings.setComponentsRoot(componentsRootField.getText());
    settings.setStoresRoot(storesRootField.getText());
  }

  public void reset() {
    enableCatberrySupportField.setSelected(settings.isCatberryEnabled());
    templateEngineField.setSelectedItem(settings.getTemplateEngineName());
    componentsRootField.setText(settings.getComponentsRoot());
    storesRootField.setText(settings.getStoresRoot());
    updateUI();
  }
}
