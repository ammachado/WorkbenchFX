package com.dlsc.workbenchfx.view;

import static com.dlsc.workbenchfx.WorkbenchFx.STYLE_CLASS_ACTIVE_TAB;

import com.dlsc.workbenchfx.WorkbenchFx;
import com.dlsc.workbenchfx.module.Module;
import java.util.Objects;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents the presenter of the corresponding {@link ToolbarView}.
 *
 * @author François Martin
 * @author Marco Sanfratello
 */
public class ToolbarPresenter implements Presenter {
  private static final Logger LOGGER =
      LogManager.getLogger(ToolbarPresenter.class.getName());
  private final WorkbenchFx model;
  private final ToolbarView view;

  // Strong reference to prevent garbage collection
  private final ObservableList<Module> openModules;
  private final ObservableList<MenuItem> navigationDrawerItems;
  private final ObservableList<Node> toolbarControlsLeft;
  private final ObservableList<Node> toolbarControlsRight;

  /**
   * Creates a new {@link ToolbarPresenter} object for a corresponding {@link ToolbarView}.
   */
  public ToolbarPresenter(WorkbenchFx model, ToolbarView view) {
    this.model = model;
    this.view = view;
    openModules = model.getOpenModules();
    navigationDrawerItems = model.getNavigationDrawerItems();
    toolbarControlsLeft = model.getToolbarControlsLeft();
    toolbarControlsRight = model.getToolbarControlsRight();
    init();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void initializeViewParts() {
    model.getToolbarControlsRight().forEach(view::addToolbarControlRight);
    model.getToolbarControlsLeft().forEach(view::addToolbarControlLeft);

    // only add the menu button, if there is at least one navigation drawer item
    if (model.getNavigationDrawerItems().size() > 0) {
      view.addMenuButton();
    }

    view.homeBtn.requestFocus();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setupEventHandlers() {
    // When the home button is clicked, the view changes
    view.homeBtn.setOnAction(event -> model.openHomeScreen());
    // When the menu button is clicked, the navigation drawer gets shown
    view.menuBtn.setOnAction(event -> model.showOverlay(model.getNavigationDrawer(), true));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setupValueChangedListeners() {
    // When the List of the currently open toolbarControlsLeft is changed, the view is updated.
    toolbarControlsLeft.addListener((ListChangeListener<? super Node>) c -> {
      while (c.next()) {
        if (c.wasRemoved()) {
          for (Node node : c.getRemoved()) {
            LOGGER.debug("Dropdown " + node + " removed");
            view.removeToolbarControlLeft(c.getFrom());
          }
        }
        if (c.wasAdded()) {
          for (Node node : c.getAddedSubList()) {
            LOGGER.debug("Dropdown " + node + " added");
            view.addToolbarControlLeft(node);
          }
        }
      }
    });

    // When the List of the currently open toolbarControlsRight is changed, the view is updated.
    toolbarControlsRight.addListener((ListChangeListener<? super Node>) c -> {
      while (c.next()) {
        if (c.wasRemoved()) {
          for (Node node : c.getRemoved()) {
            LOGGER.debug("Dropdown " + node + " removed");
            view.removeToolbarControlRight(c.getFrom());
          }
        }
        if (c.wasAdded()) {
          for (Node node : c.getAddedSubList()) {
            LOGGER.debug("Dropdown " + node + " added");
            view.addToolbarControlRight(node);
          }
        }
      }
    });

    // When the List of the currently open modules is changed, the view is updated.
    openModules.addListener((ListChangeListener<? super Module>) c -> {
      while (c.next()) {
        if (c.wasRemoved()) {
          for (Module module : c.getRemoved()) {
            LOGGER.debug("Module " + module + " closed");
            view.removeTab(c.getFrom());
          }
        }
        if (c.wasAdded()) {
          for (Module module : c.getAddedSubList()) {
            LOGGER.debug("Module " + module + " opened");
            Node tabControl = model.getTab(module);
            view.addTab(tabControl);
            tabControl.requestFocus();
          }
        }
      }
    });

    model.activeModuleProperty().addListener((observable, oldModule, newModule) -> {
      if (Objects.isNull(oldModule)) {
        // Home is the old value
        view.homeBtn.getStyleClass().remove(STYLE_CLASS_ACTIVE_TAB);
      }
      if (Objects.isNull(newModule)) {
        // Home is the new value
        view.homeBtn.getStyleClass().add(STYLE_CLASS_ACTIVE_TAB);
      }
    });

    // makes sure the menu button is only being displayed if there are navigation drawer items
    navigationDrawerItems.addListener((InvalidationListener) observable -> {
      if (navigationDrawerItems.size() == 0) {
        view.removeMenuButton();
      } else {
        view.addMenuButton();
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setupBindings() {
  }

}
