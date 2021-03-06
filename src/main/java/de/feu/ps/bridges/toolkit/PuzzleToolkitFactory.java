package de.feu.ps.bridges.toolkit;

import de.feu.ps.bridges.generator.PuzzleGeneratorFactory;
import de.feu.ps.bridges.model.Puzzle;
import de.feu.ps.bridges.serialization.Deserializer;

import java.io.File;

/**
 * Factory class that creates a new {@link PuzzleToolkit}.
 * @author Tim Gremplewski
 */
public final class PuzzleToolkitFactory {

    private PuzzleToolkitFactory() {
    }

    /**
     * Create a new instance for the given {@link Puzzle}.
     * @param puzzle puzzle to use.
     * @return a new instance.
     */
    private static PuzzleToolkit createFor(final Puzzle puzzle) {
        return new DefaultPuzzleToolkit(puzzle);
    }

    /**
     * Load a {@link Puzzle} from the given file and create a new {@link DefaultPuzzleToolkit} instance for it.
     * @param sourceFile Source file of the puzzle.
     * @return a new {@link DefaultPuzzleToolkit} instance.
     */
    public static PuzzleToolkit createForLoadedPuzzle(final File sourceFile) {
        final Puzzle puzzle = Deserializer.loadPuzzle(sourceFile);
        return createFor(puzzle);
    }

    /**
     * Generate a random puzzle and create a new instance for it.
     * @return a new instance.
     */
    public static PuzzleToolkit createForGeneratedPuzzle() {
        final Puzzle puzzle = PuzzleGeneratorFactory.createPuzzleGenerator().generate();
        return createFor(puzzle);
    }

    /**
     * Generate a new puzzle with a random number of islands and create a new instance for it.
     * @param columns Number of columns of the generated puzzle.
     * @param rows Number of columns of the generated puzzle.
     * @return a new instance.
     */
    public static PuzzleToolkit createForGeneratedPuzzle(final int columns, final int rows) {
        final Puzzle puzzle = PuzzleGeneratorFactory.createPuzzleGenerator(columns, rows).generate();
        return createFor(puzzle);
    }

    /**
     * Generate a new puzzle and create a new instance for it.
     * @param columns Number of columns of the generated puzzle.
     * @param rows Number of rows of the generated puzzle.
     * @param islands Number of islands of the generated puzzle.
     * @return a new instance.
     */
    public static PuzzleToolkit createForGeneratedPuzzle(final int columns, final int rows, final int islands) {
        Puzzle puzzle = PuzzleGeneratorFactory.createPuzzleGenerator(columns, rows, islands).generate();
        return createFor(puzzle);
    }

    public static PuzzleToolkit createForComposedPuzzle(final File sourceFile1, final File sourceFile2) {
        final Puzzle puzzle1 = Deserializer.loadPuzzle(sourceFile1);
        final Puzzle puzzle2 = Deserializer.loadPuzzle(sourceFile2);

        if (puzzle1.getColumnsCount() + puzzle2.getColumnsCount() > 25 || puzzle1.getRowsCount() + puzzle2.getRowsCount() > 25) {
            throw new IllegalSizeException("Puzzles are too big");
        }

        return createFor(PuzzleGeneratorFactory.composePuzzles(puzzle1, puzzle2));
    }
}
