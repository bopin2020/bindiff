package com.google.security.zynamics.bindiff.gui.dialogs;

import com.google.security.zynamics.bindiff.config.BinDiffConfig;
import com.google.security.zynamics.bindiff.utils.GuiUtils;
import com.google.security.zynamics.zylib.gui.CFilenameFormatter;
import com.google.security.zynamics.zylib.gui.CMessageBox;
import com.google.security.zynamics.zylib.gui.GuiHelper;
import com.google.security.zynamics.zylib.gui.FileChooser.FileChooserPanel;
import com.google.security.zynamics.zylib.io.DirectoryChooser;
import com.google.security.zynamics.zylib.system.SystemHelpers;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatterFactory;

public class NewWorkspaceDialog extends BaseDialog {
  private static final int LABEL_WIDTH = 100;
  private static final int ROW_HEIGHT = 25;

  private static final int DIALOG_WIDTH = 445;
  private static final int DIALOG_HEIGHT = 190;

  private final JFormattedTextField workspaceName;
  private final FileChooserPanel workspaceDirectoryPanel;

  private final JCheckBox createDirectory = new JCheckBox("Create directory for workspace");
  private final JCheckBox defaultWorkspace = new JCheckBox("Use this as default workspace");

  private final JButton ok = new JButton("Ok");
  private final JButton cancel = new JButton("Cancel");

  private final InternalActionListener actionListener = new InternalActionListener();

  private boolean okPressed;

  public NewWorkspaceDialog(final Window parent, final String title) {
    super(parent, title, DIALOG_WIDTH, DIALOG_HEIGHT);

    final CFilenameFormatter filenameFormatter =
        new CFilenameFormatter(new File(SystemHelpers.getApplicationDataDirectory()));
    workspaceName = new JFormattedTextField(new DefaultFormatterFactory(filenameFormatter));
    workspaceName.setText("BinDiff Workspace");

    String workspaceDir = BinDiffConfig.getInstance().getMainSettings().getWorkspaceDirectory();
    if ("".equals(workspaceDir) || workspaceDir == null) {
      workspaceDir = SystemHelpers.getUserDirectory();
    }
    workspaceDirectoryPanel =
        new FileChooserPanel(workspaceDir, actionListener, "...", 0, ROW_HEIGHT, 0);
    createDirectory.setSelected(true);

    ok.addActionListener(actionListener);
    cancel.addActionListener(actionListener);

    init();

    pack();

    GuiHelper.centerChildToParent(parent, this, true);
  }

  private boolean validateValues() {
    final String workspaceName = getWorkspaceName();
    if ("".equals(workspaceName) || workspaceName == null) {
      CMessageBox.showInformation(this, "A workspace need a name.");

      return false;
    }

    final File workspaceDir = new File(getWorkspacePath());
    if (workspaceDir.exists()) {
      if (!workspaceDir.isDirectory()) {
        CMessageBox.showInformation(this, "The specified workspace is not a directory.");

        return false;
      }

      if (workspaceDir.list().length != 0) {
        CMessageBox.showInformation(this, "The specified workspace directory is not empty.");

        return false;
      }
    } else {
      boolean created = false;
      try {
        created = workspaceDir.createNewFile();
      } catch (final IOException e) {
        CMessageBox.showError(
            this,
            String.format(
                "Could create workspace directory. '%s'", workspaceDir.getAbsolutePath()));

        return false;
      } finally {
        if (created) {
          workspaceDir.delete();
        }
      }

      return created;
    }

    return true;
  }

  private void init() {
    final JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(new EmptyBorder(5, 5, 5, 5));

    final JPanel inputPanel = new JPanel(new GridLayout(3, 1, 5, 5));
    inputPanel.setBorder(new TitledBorder(""));
    inputPanel.add(
        GuiUtils.createHorizontalNamedComponentPanel(
            "Name:", LABEL_WIDTH, workspaceName, ROW_HEIGHT));
    inputPanel.add(
        GuiUtils.createHorizontalNamedComponentPanel(
            "Location:", LABEL_WIDTH, workspaceDirectoryPanel, ROW_HEIGHT));
    inputPanel.add(
        GuiUtils.createHorizontalNamedComponentPanel(
            "", LABEL_WIDTH - 4, createDirectory, ROW_HEIGHT));

    panel.add(inputPanel, BorderLayout.NORTH);

    final JPanel buttonPanel = new JPanel(new BorderLayout());
    buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    final JPanel innerButtonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
    innerButtonPanel.add(ok);
    innerButtonPanel.add(cancel);
    buttonPanel.add(defaultWorkspace, BorderLayout.WEST);
    buttonPanel.add(innerButtonPanel, BorderLayout.EAST);

    panel.add(buttonPanel, BorderLayout.SOUTH);

    add(panel, BorderLayout.CENTER);
  }

  public String getWorkspaceName() {
    return workspaceName.getText();
  }

  public String getWorkspacePath() {
    if (createDirectory.isSelected()) {
      String path = workspaceDirectoryPanel.getText();
      if (!path.endsWith(File.separator)) {
        path += File.separator;
      }

      return path + workspaceName.getText();
    }

    return workspaceDirectoryPanel.getText();
  }

  public boolean isDefaultWorkspace() {
    return defaultWorkspace.isSelected();
  }

  public boolean isOkPressed() {
    return okPressed;
  }

  private class InternalActionListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      if (event.getSource() == workspaceDirectoryPanel.getButton()) {
        final DirectoryChooser selecter = new DirectoryChooser("Select workspace location");
        selecter.setSelectedFile(new File(workspaceDirectoryPanel.getText()));

        if (selecter.showOpenDialog(NewWorkspaceDialog.this) == DirectoryChooser.APPROVE_OPTION) {
          final File workspaceDir = selecter.getSelectedFile();

          if (workspaceDir.exists()) {
            workspaceDirectoryPanel.setText(selecter.getSelectedFile().getAbsolutePath());
          } else {
            CMessageBox.showError(
                selecter, "Workspace directory does not exist. Please choose a valid one.");
          }
        }

        return;
      } else if (event.getSource() == ok) {
        if (!validateValues()) {
          return;
        }

        okPressed = true;
      }

      dispose();
    }
  }
}