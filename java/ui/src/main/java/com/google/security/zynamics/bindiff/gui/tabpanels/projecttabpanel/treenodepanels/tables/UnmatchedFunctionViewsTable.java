package com.google.security.zynamics.bindiff.gui.tabpanels.projecttabpanel.treenodepanels.tables;

import com.google.security.zynamics.bindiff.enums.ESide;
import com.google.security.zynamics.bindiff.gui.tabpanels.projecttabpanel.WorkspaceTabPanelFunctions;
import com.google.security.zynamics.bindiff.gui.tabpanels.projecttabpanel.treenodepanels.tables.popup.FlowGraphViewsTablePopupMenu;
import com.google.security.zynamics.bindiff.gui.tabpanels.projecttabpanel.treenodepanels.tables.renderers.BackgroundCellRenderer;
import com.google.security.zynamics.bindiff.gui.tabpanels.projecttabpanel.treenodepanels.tables.renderers.FunctionTypeCellRenderer;
import com.google.security.zynamics.bindiff.resources.Colors;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.disassembly.IAddress;
import com.google.security.zynamics.zylib.general.ListenerProvider;
import com.google.security.zynamics.zylib.gui.tables.CTableSorter;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

public class UnmatchedFunctionViewsTable extends AbstractTable {

  private final ListenerProvider<IUnmatchedFunctionsViewsTableListener> listeners =
      new ListenerProvider<>();

  private final InternalSelectionListener selectionListener = new InternalSelectionListener();

  private final ESide side;

  public UnmatchedFunctionViewsTable(
      final UnmatchedFunctionViewsTableModel unmatchedModel,
      final WorkspaceTabPanelFunctions controller) {
    super(unmatchedModel, controller);

    sortColumn(UnmatchedFunctionViewsTableModel.TYPE, CTableSorter.ASCENDING);
    sortColumn(UnmatchedFunctionViewsTableModel.ADDRESS, CTableSorter.ASCENDING);

    side = unmatchedModel.getSide();

    init();

    getSelectionModel().addListSelectionListener(selectionListener);
  }

  private void init() {
    final TableColumn address =
        getColumnModel().getColumn(UnmatchedFunctionViewsTableModel.ADDRESS);
    final TableColumn name =
        getColumnModel().getColumn(UnmatchedFunctionViewsTableModel.FUNCTION_NAME);
    final TableColumn type = getColumnModel().getColumn(UnmatchedFunctionViewsTableModel.TYPE);
    final TableColumn callers =
        getColumnModel().getColumn(UnmatchedFunctionViewsTableModel.CALLERS);
    final TableColumn calls = getColumnModel().getColumn(UnmatchedFunctionViewsTableModel.CALLEES);
    final TableColumn basicBlocks =
        getColumnModel().getColumn(UnmatchedFunctionViewsTableModel.BASICBLOCKS);
    final TableColumn jumps = getColumnModel().getColumn(UnmatchedFunctionViewsTableModel.JUMPS);
    final TableColumn instructions =
        getColumnModel().getColumn(UnmatchedFunctionViewsTableModel.INSTRUCTIONS);

    address.setMinWidth(60);
    name.setMinWidth(55);
    type.setMinWidth(35);
    callers.setMinWidth(40);
    calls.setMinWidth(75);
    basicBlocks.setMinWidth(40);
    jumps.setMinWidth(40);
    instructions.setMinWidth(40);

    address.setPreferredWidth(60);
    name.setPreferredWidth(200);
    type.setPreferredWidth(35);
    callers.setPreferredWidth(50);
    calls.setPreferredWidth(75);
    basicBlocks.setPreferredWidth(50);
    jumps.setPreferredWidth(50);
    instructions.setPreferredWidth(50);

    final BackgroundCellRenderer backgroundRenderer =
        new BackgroundCellRenderer(Colors.GRAY250, Colors.GRAY32, SwingConstants.LEFT);

    address.setCellRenderer(backgroundRenderer);
    name.setCellRenderer(backgroundRenderer);
    type.setCellRenderer(new FunctionTypeCellRenderer());
    callers.setCellRenderer(backgroundRenderer);
    basicBlocks.setCellRenderer(backgroundRenderer);
    jumps.setCellRenderer(backgroundRenderer);
    instructions.setCellRenderer(backgroundRenderer);
    calls.setCellRenderer(backgroundRenderer);
  }

  @Override
  protected JPopupMenu getPopupMenu(final int rowIndex, final int columnIndex) {
    return new FlowGraphViewsTablePopupMenu(this, rowIndex, columnIndex);
  }

  @Override
  protected void handleDoubleClick(final int row) {
    IAddress primaryAddr = null;
    IAddress secondaryAddr = null;

    if (side == ESide.PRIMARY) {
      primaryAddr =
          new CAddress(
              (String) getTableModel().getValueAt(row, UnmatchedFunctionViewsTableModel.ADDRESS),
              16);
    } else {
      secondaryAddr =
          new CAddress(
              (String) getTableModel().getValueAt(row, UnmatchedFunctionViewsTableModel.ADDRESS),
              16);
    }

    getController()
        .openFlowgraphView(getController().getMainWindow(), getDiff(), primaryAddr, secondaryAddr);
  }

  public void addListener(final IUnmatchedFunctionsViewsTableListener listener) {
    listeners.addListener(listener);
  }

  public ESide getSide() {
    return side;
  }

  public void removeListener(final IUnmatchedFunctionsViewsTableListener listener) {
    listeners.removeListener(listener);
  }

  private class InternalSelectionListener implements ListSelectionListener {
    @Override
    public void valueChanged(final ListSelectionEvent e) {
      for (final IUnmatchedFunctionsViewsTableListener listener : listeners) {
        listener.rowSelectionChanged(UnmatchedFunctionViewsTable.this);
      }
    }
  }
}