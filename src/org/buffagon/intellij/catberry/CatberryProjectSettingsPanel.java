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

  private CatberryProjectSettingsProvider mySettingsProvider;


  public CatberryProjectSettingsPanel() {
    enableCatberrySupportField.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateUI();
      }
    });
  }

  private void updateUI() {
    templateEngineField.setEnabled(enableCatberrySupportField.isSelected());
  }

  public JComponent getPanel(CatberryProjectSettingsProvider provider) {
    mySettingsProvider = provider;
    return rootPanel;
  }

  public boolean isModified() {
    return !(Comparing.equal(mySettingsProvider.isCatberryEnabled(), enableCatberrySupportField.isSelected()) &&
           Comparing.equal(mySettingsProvider.getTemplateEngineName(), templateEngineField.getSelectedItem().toString()));
  }

  public void apply() {
    mySettingsProvider.setCatberryEnabled(enableCatberrySupportField.isSelected());
    mySettingsProvider.setTemplateEngineName(templateEngineField.getSelectedItem().toString());
  }

  public void reset() {
    enableCatberrySupportField.setSelected(mySettingsProvider.isCatberryEnabled());
    templateEngineField.setSelectedItem(mySettingsProvider.getTemplateEngineName());
    updateUI();
  }
}
