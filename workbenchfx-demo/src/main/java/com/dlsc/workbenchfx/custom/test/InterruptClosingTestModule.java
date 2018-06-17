package com.dlsc.workbenchfx.custom.test;

import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.custom.overlay.CustomDialog;
import com.dlsc.workbenchfx.module.Module;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class InterruptClosingTestModule extends Module {

  boolean closePossible;

  public InterruptClosingTestModule() {
    super("Interrupt Close", FontAwesomeIcon.QUESTION);
  }

  @Override
  public void init(Workbench workbench) {
    super.init(workbench);
    closePossible = false;
  }

  @Override
  public Node activate() {
    return new Label("This module will open up a dialog when trying to close it.");
  }

  @Override
  public boolean destroy() {
    if (!closePossible) {
      workbench.openModule(this);
      workbench.showOverlay(new CustomDialog(workbench, this), true);
      return false;
    } else {
      return true;
    }

  }

  public boolean isClosePossible() {
    return closePossible;
  }

  public void setClosePossible(boolean closePossible) {
    this.closePossible = closePossible;
  }
}
