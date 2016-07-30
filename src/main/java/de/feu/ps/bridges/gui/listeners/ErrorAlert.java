package de.feu.ps.bridges.gui.listeners;

import de.feu.ps.bridges.gui.events.ErrorEvent;
import javafx.scene.control.Alert;

import java.util.ResourceBundle;

/**
 * @author Tim Gremplewski
 */
public class ErrorAlert implements ErrorEventListener {
    private final ResourceBundle resourceBundle;

    public ErrorAlert(final ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Override
    public void handleEvent(final ErrorEvent event) {
        getErrorAlert(event)
            .showAndWait();
    }

    private Alert getErrorAlert(final ErrorEvent errorEventType) {
        switch (errorEventType) {
            case PUZZLE_GENERATION_FAILED:
                return getErrorAlert(resourceBundle.getString("generation.failed"));
            case LOADING_PUZZLE_FAILED:
                return getErrorAlert(resourceBundle.getString("loading.failed"));
            case SAVING_PUZZLE_FAILED:
                return getErrorAlert(resourceBundle.getString("saving.failed"));
            case RESTARTING_PUZZLE_FAILED:
                return getErrorAlert(resourceBundle.getString("restarting.failed"));
            case NEXT_MOVE_FAILED:
                return getErrorAlert(resourceBundle.getString("nextMove.failed"));
            case SOLVING_FAILED:
                return getErrorAlert(resourceBundle.getString("solving.failed"));
            case BUILD_BRIDGE_FAILED:
                return getErrorAlert(resourceBundle.getString("buildBridge.failed"));
            case TEAR_DOWN_BRIDGE_FAILED:
                return getErrorAlert(resourceBundle.getString("tearDownBridge.failed"));
            default:
                return getErrorAlert("");
        }
    }

    private Alert getErrorAlert(final String headerText) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(resourceBundle.getString("error.title"));
        alert.setHeaderText(headerText);
        alert.setContentText(resourceBundle.getString("error.content.text"));
        return alert;
    }
}