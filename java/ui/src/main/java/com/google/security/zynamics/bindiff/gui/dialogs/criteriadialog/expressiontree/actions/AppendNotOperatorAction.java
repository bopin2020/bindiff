package com.google.security.zynamics.bindiff.gui.dialogs.criteriadialog.expressiontree.actions;

import com.google.security.zynamics.bindiff.gui.dialogs.criteriadialog.expressiontree.ExpressionTreeActionProvider;
import com.google.security.zynamics.bindiff.gui.dialogs.criteriadialog.operators.NotCriterium;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class AppendNotOperatorAction extends AbstractAction {
  private final ExpressionTreeActionProvider actionProvider;

  public AppendNotOperatorAction(final ExpressionTreeActionProvider actionProvider) {
    super("Append NOT");

    this.actionProvider = actionProvider;
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    actionProvider.appendCriterium(new NotCriterium());
  }
}