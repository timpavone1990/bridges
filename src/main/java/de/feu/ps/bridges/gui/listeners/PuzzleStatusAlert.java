package de.feu.ps.bridges.gui.listeners;

import de.feu.ps.bridges.analyser.PuzzleStatus;
import de.feu.ps.bridges.gui.events.AutomatedSolvingEvent;
import de.feu.ps.bridges.gui.events.PuzzleEvent;
import de.feu.ps.bridges.gui.model.GameState;
import javafx.scene.control.Alert;

import java.util.ResourceBundle;

import static de.feu.ps.bridges.analyser.PuzzleStatus.UNSOLVED;
import static de.feu.ps.bridges.gui.events.AutomatedSolvingEvent.STARTED;
import static de.feu.ps.bridges.gui.events.PuzzleEvent.PUZZLE_STATUS_CHANGED;

/**
 * @author Tim Gremplewski
 */
public class PuzzleStatusAlert implements AutomatedSolvingEventListener, PuzzleEventListener {

    private final ResourceBundle resourceBundle;
    private final GameState gameState;
    private boolean automatedSolvingRunning;

    public PuzzleStatusAlert(final ResourceBundle resourceBundle, final GameState gameState) {
        this.resourceBundle = resourceBundle;
        this.gameState = gameState;
        automatedSolvingRunning = false;
    }

    @Override
    public void handleEvent(final AutomatedSolvingEvent event) {
        automatedSolvingRunning = event == STARTED;
        if (event != STARTED) {
            // Show an alert showing the puzzle status when the automated solving was finished or cancelled
            showPuzzleStatusAlert();
        }
    }

    @Override
    public void handleEvent(final PuzzleEvent event) {
        if (event == PUZZLE_STATUS_CHANGED
            && !automatedSolvingRunning
            && gameState.getPuzzleStatus() != UNSOLVED) {

            showPuzzleStatusAlert();
        }
    }

    private void showPuzzleStatusAlert() {
        showPuzzleStatusAlert(gameState.getPuzzleStatus());
    }

    private void showPuzzleStatusAlert(final PuzzleStatus puzzleStatus) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        switch (puzzleStatus) {
            case SOLVED:
                alert.setTitle(resourceBundle.getString("autoSolveDialog.solved.title"));
                alert.setHeaderText(resourceBundle.getString("autoSolveDialog.solved.title"));
                alert.setContentText(resourceBundle.getString("autoSolveDialog.solved.contentText"));
                break;
            case UNSOLVED:
                alert.setTitle(resourceBundle.getString("autoSolveDialog.unsolved.title"));
                alert.setHeaderText(resourceBundle.getString("autoSolveDialog.unsolved.title"));
                alert.setContentText(resourceBundle.getString("autoSolveDialog.unsolved.contentText"));
                break;
            case UNSOLVABLE:
                alert.setTitle(resourceBundle.getString("autoSolveDialog.unsolved.title"));
                alert.setHeaderText(resourceBundle.getString("autoSolveDialog.unsolved.title"));
                alert.setContentText(resourceBundle.getString("autoSolveDialog.unsolvable.contentText"));
                break;
        }
        alert.showAndWait();
    }
}
