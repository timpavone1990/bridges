package de.feu.ps.bridges.gui.controller;

import de.feu.ps.bridges.gui.gamestate.GameState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller for the new puzzle dialog.
 * @author Tim Gremplewski
 */
public class NewPuzzleController implements Initializable {

    @FXML
    private Spinner<Integer> columnsSpinner;

    @FXML
    private Spinner<Integer> rowsSpinner;

    @FXML
    private Spinner<Integer> islandsSpinner;

    @FXML
    private RadioButton autoGenerateRadioButton;

    @FXML
    private GridPane manualSettingsGridPane;

    @FXML
    private CheckBox manualIslandsCountCheckBox;

    private final GameState gameState;
    private final Stage stage;

    /**
     * Creates a new instance.
     * @param gameState the {@link GameState} to use.
     * @param stage the {@link Stage} to use.
     * @throws NullPointerException if gameState or stage is null.
     */
    public NewPuzzleController(final GameState gameState, final Stage stage) {
        this.gameState = Objects.requireNonNull(gameState, "Parameter 'gameState' must not be null.");
        this.stage = Objects.requireNonNull(stage, "Parameter 'stage' must not be null.");
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        // TODO Can this be done in the fxml file?
        columnsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 25));
        columnsSpinner.valueProperty().addListener((a, b, c) -> updateValidIslandsRange());

        rowsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 25));
        rowsSpinner.valueProperty().addListener((a, b, c) -> updateValidIslandsRange());

        updateValidIslandsRange();

        autoGenerateRadioButton.setSelected(true);
        autoModeSelected(null);

        manualIslandsCountCheckBox.setSelected(false);
        manualIslandsCountClicked(null);
    }

    private void updateValidIslandsRange() {
        final int max = (int) (0.2 * columnsSpinner.getValue() * rowsSpinner.getValue());
        islandsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, max));
    }

    /**
     * Invoked when the user selected the auto generation mode.
     * @param actionEvent the event.
     */
    public void autoModeSelected(final ActionEvent actionEvent) {
        manualSettingsGridPane.setDisable(true);
    }

    /**
     * Invoked when the user selected the manual generation mode.
     * @param actionEvent the event.
     */
    public void manualModeSelected(final ActionEvent actionEvent) {
        manualSettingsGridPane.setDisable(false);
    }

    /**
     * Invoked when the user clicked the manual islands count checkbox.
     * @param actionEvent the event.
     */
    public void manualIslandsCountClicked(final ActionEvent actionEvent) {
        boolean manualIslandsCount = manualIslandsCountCheckBox.isSelected();
        islandsSpinner.setDisable(!manualIslandsCount);
    }

    /**
     * Invoked when the user clicked the cancel button.
     * @param actionEvent the event.
     */
    public void cancel(final ActionEvent actionEvent) {
        stage.close();
    }

    /**
     * Invoked when the user clicked the ok button.
     * @param actionEvent the event.
     */
    public void ok(final ActionEvent actionEvent) {
        if (autoGenerateRadioButton.isSelected()) {
            gameState.newPuzzle();
        } else {
            int columns = columnsSpinner.getValue();
            int rows = rowsSpinner.getValue();

            if (manualIslandsCountCheckBox.isSelected()) {
                int islands = islandsSpinner.getValue();
                gameState.newPuzzle(columns, rows, islands);
            } else {
                gameState.newPuzzle(columns, rows);
            }
        }
        stage.close();
    }
}