// Copyright 2011-2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.security.zynamics.bindiff.gui.dialogs.graphsettings.panels;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.security.zynamics.bindiff.config.BinDiffConfig;
import com.google.security.zynamics.bindiff.enums.ELayoutOrientation;
import com.google.security.zynamics.bindiff.graph.settings.GraphSettings;
import com.google.security.zynamics.bindiff.gui.components.TextComponentUtils;
import com.google.security.zynamics.bindiff.gui.dialogs.graphsettings.ESettingsDialogType;
import com.google.security.zynamics.bindiff.utils.GuiUtils;
import com.google.security.zynamics.zylib.gui.CDecFormatter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatterFactory;

/** Settings panel for hierarchical layouts. */
public class HierarchicalLayoutPanel extends JPanel {
  private static final int LABEL_WIDTH = 275;
  private static final int ROW_HEIGHT = 25;
  private static final int NUMBER_OF_ROWS = 4;

  private final JComboBox<String> orientation = new JComboBox<>();
  private final JComboBox<String> layoutStyle = new JComboBox<>();
  private final JFormattedTextField minimumLayerDistance =
      TextComponentUtils.addDefaultEditorActions(
          new JFormattedTextField(new DefaultFormatterFactory(new CDecFormatter(3))));
  private final JFormattedTextField minimumNodeDistance =
      TextComponentUtils.addDefaultEditorActions(
          new JFormattedTextField(new DefaultFormatterFactory(new CDecFormatter(3))));

  private final ESettingsDialogType dialogType;

  private final GraphSettings settings;

  public HierarchicalLayoutPanel(final String borderTitle, final ESettingsDialogType type) {
    this(borderTitle, type, null);
  }

  public HierarchicalLayoutPanel(
      final String borderTitle, final ESettingsDialogType type, final GraphSettings settings) {
    super(new BorderLayout());

    checkNotNull(borderTitle);
    checkArgument(settings == null ^ type == ESettingsDialogType.GRAPH_VIEW_SETTINGS);

    dialogType = type;

    this.settings = settings;

    init(borderTitle);
  }

  private ELayoutOrientation getLayoutOrientation(final BinDiffConfig config) {
    switch (dialogType) {
      case INITIAL_CALL_GRAPH_SETTING:
        return config.getInitialCallGraphSettings().getHierarchicalOrientation();
      case INITIAL_FLOW_GRAPH_SETTINGS:
        return config.getInitialFlowGraphSettings().getHierarchicalOrientation();
      default:
    }

    return settings.getLayoutSettings().getHierarchicalOrientation();
  }

  private int getMinimumLayerDistance(final BinDiffConfig config) {
    switch (dialogType) {
      case INITIAL_CALL_GRAPH_SETTING:
        return config.getInitialCallGraphSettings().getHierarchicalMinimumNodeDistance();
      case INITIAL_FLOW_GRAPH_SETTINGS:
        return config.getInitialFlowGraphSettings().getHierarchicalMinimumNodeDistance();
      default:
    }

    return (int) settings.getLayoutSettings().getMinimumHierarchicLayerDistance();
  }

  private int getMinimumNodeDistance(final BinDiffConfig config) {
    switch (dialogType) {
      case INITIAL_CALL_GRAPH_SETTING:
        return config.getInitialCallGraphSettings().getHierarchicalMinimumLayerDistance();
      case INITIAL_FLOW_GRAPH_SETTINGS:
        return config.getInitialFlowGraphSettings().getHierarchicalMinimumLayerDistance();
      default:
    }

    return (int) settings.getLayoutSettings().getMinimumHierarchicNodeDistance();
  }

  private boolean getOrthogonalEdgeRouting(final BinDiffConfig config) {
    switch (dialogType) {
      case INITIAL_CALL_GRAPH_SETTING:
        return config.getInitialCallGraphSettings().getHierarchicalOrthogonalEdgeRouting();
      case INITIAL_FLOW_GRAPH_SETTINGS:
        return config.getInitialFlowGraphSettings().getHierarchicalOrthogonalEdgeRouting();
      default:
    }

    return settings.getLayoutSettings().getHierarchicalOrthogonalEdgeRouting();
  }

  private void init(final String borderTitle) {
    setBorder(new LineBorder(Color.GRAY));

    layoutStyle.addItem("On");
    layoutStyle.addItem("Off");
    orientation.addItem("Vertical");
    orientation.addItem("Horizontal");

    setCurrentValues();

    final JPanel panel = new JPanel(new GridLayout(NUMBER_OF_ROWS, 1, 5, 5));
    panel.setBorder(new TitledBorder(borderTitle));

    panel.add(
        GuiUtils.createHorizontalNamedComponentPanel(
            "Orthogonal Edge Routing", LABEL_WIDTH, layoutStyle, ROW_HEIGHT));
    panel.add(
        GuiUtils.createHorizontalNamedComponentPanel(
            "Layout orientation", LABEL_WIDTH, orientation, ROW_HEIGHT));
    panel.add(
        GuiUtils.createHorizontalNamedComponentPanel(
            "Minimum layer distance", LABEL_WIDTH, minimumLayerDistance, ROW_HEIGHT));
    panel.add(
        GuiUtils.createHorizontalNamedComponentPanel(
            "Minimum node distance", LABEL_WIDTH, minimumNodeDistance, ROW_HEIGHT));

    add(panel, BorderLayout.NORTH);
  }

  public ELayoutOrientation getLayoutOrientation() {
    return ELayoutOrientation.getEnum(orientation.getSelectedIndex());
  }

  public int getMinimumLayerDistance() {
    return Integer.parseInt(minimumLayerDistance.getText());
  }

  public int getMinimumNodeDistance() {
    return Integer.parseInt(minimumNodeDistance.getText());
  }

  public boolean getOrthogonalEdgeRouting() {
    return layoutStyle.getSelectedIndex() == 0;
  }

  public void setCurrentValues() {
    final BinDiffConfig config = BinDiffConfig.getInstance();

    layoutStyle.setSelectedIndex(getOrthogonalEdgeRouting(config) ? 0 : 1);
    orientation.setSelectedIndex(ELayoutOrientation.getOrdinal(getLayoutOrientation(config)));
    minimumLayerDistance.setText(Integer.toString(getMinimumLayerDistance(config)));
    minimumNodeDistance.setText(Integer.toString(getMinimumNodeDistance(config)));
  }
}
