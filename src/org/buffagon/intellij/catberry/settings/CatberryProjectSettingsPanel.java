package org.buffagon.intellij.catberry.settings;

import com.intellij.openapi.util.Comparing;
import com.intellij.ui.CollectionComboBoxModel;
import org.buffagon.intellij.catberry.TemplateEngine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

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
  private JPanel basePanel;
  private JPanel optionalPanel;

  private CatberryReadWriteSettings settings;


  public CatberryProjectSettingsPanel(CatberryReadWriteSettings settings) {
    this.settings = settings;
    enableCatberrySupportField.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateUI();
      }
    });
    //TODO: ???
    templateEngineField.setModel(new CollectionComboBoxModel(Arrays.asList(TemplateEngine.values())));
  }

  private void updateUI() {
    enableCatberrySupportField.setEnabled(enableCatberrySupportField.isSelected());
    templateEngineField.setEnabled(enableCatberrySupportField.isSelected());
    componentsRootField.setEnabled(enableCatberrySupportField.isSelected());
    storesRootField.setEnabled(enableCatberrySupportField.isSelected());
  }

  public JPanel getRootPanel() {
    return rootPanel;
  }

  public JPanel getBasePanel() {
    return basePanel;
  }

  public boolean isModified() {
    return !(Comparing.equal(settings.isCatberryEnabled(), enableCatberrySupportField.isSelected()) &&
        Comparing.equal(settings.getTemplateEngine(), templateEngineField.getSelectedItem().toString()) &&
        Comparing.equal(settings.getComponentsRoot(), componentsRootField.getText()) &&
        Comparing.equal(settings.getStoresRoot(), storesRootField.getText()));
  }

  public void apply() {
    settings.setCatberryEnabled(enableCatberrySupportField.isSelected());
    settings.setTemplateEngine((TemplateEngine) templateEngineField.getSelectedItem());
    settings.setComponentsRoot(componentsRootField.getText());
    settings.setStoresRoot(storesRootField.getText());
  }

  public void reset() {
    enableCatberrySupportField.setSelected(settings.isCatberryEnabled());
    templateEngineField.setSelectedItem(settings.getTemplateEngine());
    componentsRootField.setText(settings.getComponentsRoot());
    storesRootField.setText(settings.getStoresRoot());
    updateUI();
  }

  private void createUIComponents() {

  }
}
