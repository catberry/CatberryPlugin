package org.buffagon.intellij.catberry.settings;

import com.intellij.ui.CollectionComboBoxModel;
import org.buffagon.intellij.catberry.TemplateEngine;

import javax.swing.*;
import java.util.Arrays;

/**
 * Catberry Plugin settings form.
 *
 * @author Prokofiev Alex
 */
public class CatberryProjectSettingsPanel {
  private JPanel rootPanel;
  private JComboBox templateEngineField;

  private CatberryProjectSettings settings;


  public CatberryProjectSettingsPanel(CatberryProjectSettings settings) {
    this.settings = settings;
    //TODO: ???
    templateEngineField.setModel(new CollectionComboBoxModel(Arrays.asList(TemplateEngine.values())));
  }

  public JPanel getRootPanel() {
    return rootPanel;
  }

  public void apply() {
    settings.templateEngine = (TemplateEngine) templateEngineField.getSelectedItem();
  }

  public void reset() {
    templateEngineField.setSelectedItem(settings.templateEngine);
  }

  private void createUIComponents() {

  }
}
