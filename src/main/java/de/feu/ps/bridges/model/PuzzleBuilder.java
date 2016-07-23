package de.feu.ps.bridges.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Helper class to construct a new puzzle.
 * @author Tim Gremplewski
 */
public class PuzzleBuilder {

    private int islandsCount;
    private final int columns;
    private final int rows;
    private List<ModifiableIsland> islands;
    private ModifiablePuzzle puzzle;

    private PuzzleBuilder(final int columns, final int rows, final int islandsCount) {
        if (columns < 1) {
            throw new IllegalArgumentException("Parameter 'columns' must not be less than 1.");
        }

        if (rows < 1) {
            throw new IllegalArgumentException("Parameter 'rows' must not be less than 1.");
        }

        this.columns = columns;
        this.rows = rows;
        this.islandsCount = islandsCount;
        islands = new ArrayList<>(islandsCount);
        puzzle = new DefaultPuzzle(columns, rows);
    }

    /**
     * Creates a new instance.
     * @param columns Amount of columns of the puzzle to be build.
     * @param rows Amount of rows of the puzzle to be build.
     * @param islands Amount of islands of the puzzel to be build.
     * @return a new PuzzleBuilder instance.
     * @throws IllegalArgumentException if columns, rows or islands is less than 1.
     */
    public static PuzzleBuilder createBuilder(final int columns, final int rows, final int islands) {
        return new PuzzleBuilder(columns, rows, islands);
    }

    /**
     * Add a new bridge between the given islands.
     * @param island1 Island to be bridged.
     * @param island2 Another island to be bridged.
     * @param doubleBridge indicates whether the new bridge should be a double bridge.
     */
    public void addBridge(final Island island1, final Island island2, final boolean doubleBridge) {
        puzzle.buildBridge(island1, island2, doubleBridge);
    }

    /**
     * Add a new island to the puzzle.
     * @param position Position where the island should be created.
     * @param requiredBridges Amount of required bridged of the new island.
     * @return the newly created Island
     * @throws IllegalStateException if the puzzle already has enough islands.
     */
    public Island addIsland(final Position position, final int requiredBridges) {
        if (islands.size() == islandsCount) {
            throw new IllegalStateException("The puzzle already has enough islands.");
        }
        validatePosition(position);

        // TODO This cast is ugly
        final ModifiableIsland island = ((ModifiableIsland) puzzle.buildIsland(position, requiredBridges));
        islands.add(island);
        return island;
    }

    private void validatePosition(Position position) {
        Objects.requireNonNull(position, "Parameter 'position' must not be null.");

        if (position.getColumn() >= columns) {
            throw new IllegalArgumentException("The puzzle does not have this column: " + position.getColumn());
        }

        if (position.getRow() >= rows) {
            throw new IllegalArgumentException("The puzzle does not have this row: " + position.getRow());
        }
    }

    /**
     * Get the amount of islands of the puzzle.
     * @return the amount of islands of the puzzle.
     */
    public int getIslandsCount() {
        return islandsCount;
    }

    /**
     * Get the created puzzle.
     * @return the created puzzle.
     */
    public Puzzle getResult() {
        return puzzle;
    }

    /**
     * For each island, set the amount of required bridges to the current amount of existing bridges.
     */
    public void setRequiredBridgesToCurrentCountOfBridges() {
        islands.forEach(island -> island.setRequiredBridges(island.getActualBridgesCount()));
    }
}