package de.feu.ps.bridges.solver;

import de.feu.ps.bridges.model.Bridge;
import de.feu.ps.bridges.model.BridgeImpl;
import de.feu.ps.bridges.model.Island;
import de.feu.ps.bridges.model.Puzzle;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Tim Gremplewski
 */
public class Solver {

    private static final Logger LOGGER = Logger.getLogger(Solver.class.getName());

    private Map<Island, Set<Bridge>> saveMoves;

    public Solver() {
        saveMoves = new HashMap<>();
    }

    public void solve(Puzzle puzzle) {
        Bridge addedBridge;
        do {
            addedBridge = getSafeMove(puzzle);

            if (addedBridge == null && puzzle.getColumnsCount() == 25) {
                addedBridge = tryAndError(puzzle);
            }

            if (addedBridge != null) {
                puzzle.addBridge(addedBridge);
            }
        } while (/*puzzle.getStatus() != PuzzleStatus.SOLVED &&*/ addedBridge != null);
    }

    public Bridge tryAndError(Puzzle puzzle) {
        Set<Island> islands = puzzle.getIslands();

        Bridge nextMove = null;

        for (Island island : islands) {
            if (island.getRemainingBridges() == 0) {
                continue;
            }

            Set<Island> reachableUnfinishedNeighbours = getReachableUnfinishedNeighbours(puzzle, island);
            Set<Island> destinations = reachableUnfinishedNeighbours.parallelStream().filter(island1 -> causesNoIsolation(puzzle, island, island1)).collect(Collectors.toSet());

            for (Island destination : destinations) {
                Bridge tryBridge = new BridgeImpl(island, destination, false);
                puzzle.addBridge(tryBridge);

                boolean causesConflict = false;

                Set<Island> islands1 = puzzle.getIslands();
                for (Island island1 : islands1) {
                    if (island1.getRemainingBridges() == 0) {
                        continue;
                    }

                    Set<Island> reachableTest = getReachableUnfinishedNeighbours(puzzle, island1);
                    Set<Island> destinationsTest = reachableTest.parallelStream().filter(island_1 -> causesNoIsolation(puzzle, island1, island_1)).collect(Collectors.toSet());
                    if (destinationsTest.isEmpty()) {
                        causesConflict = true;
                        break;
                    }
                }

                puzzle.removeBridge(tryBridge);
                if (!causesConflict) {
                    if (nextMove == null) {
                        nextMove = tryBridge;
                    } else {
                        // Island has more than one destination that do not lead to a direct conflict
                        nextMove = null;
                        break;
                    }
                }
            }

            if (nextMove != null) {
                break;
            }
        }

        return nextMove;
    }

    public Bridge getSafeMove(Puzzle puzzle) {
        // TODO check if already solved

        // TODO: Same sort is done in Serializer
        //List<Island> islands = puzzle.getIslands().stream().sorted(comparingInt(Island::getColumn).thenComparing(Island::getRow)).collect(Collectors.toList());

        Bridge safeMove = null;

        for (Island island : puzzle.getIslands()) {
            //LOGGER.log(Level.INFO, island.toString());
            if (island.getRemainingBridges() == 0) {
                continue;
            }

            final Set<Island> reachableUnfinishedNeighbours = getReachableUnfinishedNeighbours(puzzle, island);
//            final Set<Island> islandsThatNeedToConnectTo = islandsThatNeedToConnectTo(puzzle, island, reachableUnfinishedNeighbours);
            final Set<Island> islandsThatNeedToConnectTo = new HashSet<>();

            // Some islands may have no other option than bridge to the current island
            // These islands need to be looked at first, otherwise all reachable neighbours are possible destinations
            Set<Island> possibleDestinations;
            if (islandsThatNeedToConnectTo.isEmpty()) {
                possibleDestinations = reachableUnfinishedNeighbours;
            } else {
                possibleDestinations = islandsThatNeedToConnectTo;
            }

            possibleDestinations = possibleDestinations.parallelStream().filter(island1 -> causesNoIsolation(puzzle, island, island1)).collect(Collectors.toSet());

            if (!possibleDestinations.isEmpty()) {
                int remainingBridges = island.getRemainingBridges();
                int existingBridgesToPossibleDestinations = 0;

                for (Island destination : possibleDestinations) {
                    if (island.isBridgedTo(destination)) {
                        existingBridgesToPossibleDestinations++;
                    }
                }

                //List<Island> sortedPossibleDestinations = possibleDestinations.stream().sorted(comparingInt(Island::getColumn).thenComparing(Island::getRow)).collect(Collectors.toList());

                for (Island destination : possibleDestinations) {
                    int existingToDestination = 0;
                    if (island.isBridgedTo(destination)) {
                        existingToDestination = 1;
                    }

                    if (isSave(remainingBridges + existingBridgesToPossibleDestinations, possibleDestinations.size(), existingToDestination)) {
                        safeMove = new BridgeImpl(island, destination, false);
                        break;
                    }
                }

                if (safeMove != null) {
                    break;
                }

                /*int saveBridgesToEveryPossibleDestination = 0;

                if (remainingBridges == 2 * possibleDestinations.size()) {
                    saveBridgesToEveryPossibleDestination = 2;
                } else if (remainingBridges == 2 * possibleDestinations.size() - 1) {
                    saveBridgesToEveryPossibleDestination = 1;
                }

                if (saveBridgesToEveryPossibleDestination > 0) {

                    Island finalDestination = null;
                    boolean createDoubleBridge = false;

                    Optional<Island> optionalDestination =
                        possibleDestinations.stream()
                            .filter(destination -> !island.isBridgedTo(destination))
                            .findFirst();

                    if (optionalDestination.isPresent()) {
                        finalDestination = optionalDestination.get();
                        createDoubleBridge = saveBridgesToEveryPossibleDestination == 2;
                    } else if (saveBridgesToEveryPossibleDestination == 2) {
                        optionalDestination =
                            possibleDestinations.stream()
                            .filter(destination -> !island.getBridgeTo(destination).isDoubleBridge())
                            .findFirst();

                        if (optionalDestination.isPresent()) {
                            finalDestination = optionalDestination.get();
                        }
                    }

                    if (finalDestination != null) {
                        safeMove = new BridgeImpl(island, finalDestination, createDoubleBridge);
                        break;
                    }
                }*/
            }
        }

        return safeMove;
    }

    private boolean causesNoIsolation(Puzzle puzzle, Island island1, Island island2) {

        Set<Island> visitedIslands = new HashSet<>();
        Queue<Island> islandsToVisit = new LinkedList<>();

        if (island1.getRemainingBridges() > 1) {
            // If remainingBridges == 1, island1 will only be able to reach island2
            Set<Island> reachable = getReachableUnfinishedNeighbours(puzzle, island1);
            islandsToVisit.addAll(reachable);
        }

        islandsToVisit.addAll(island1.getBridgedNeighbours());
        visitedIslands.add(island1);

        if (island2.getRemainingBridges() > 1) {
            // If remainingBridges == 1, island2 will only be able to reach island1
            Set<Island> reachable = getReachableUnfinishedNeighbours(puzzle, island2);
            islandsToVisit.addAll(reachable);
        }

        islandsToVisit.addAll(island2.getBridgedNeighbours());
        visitedIslands.add(island2);

        while (!islandsToVisit.isEmpty()) {
            Island island = islandsToVisit.remove();
            if (!visitedIslands.contains(island)) {
                if (island.getRemainingBridges() > 0) {
                    islandsToVisit.addAll(getReachableUnfinishedNeighbours(puzzle, island));
                }
                islandsToVisit.addAll(island.getBridgedNeighbours());
            }
            visitedIslands.add(island);
        }

        return visitedIslands.containsAll(puzzle.getIslands());
    }

    private boolean isSave(int requiredBridges, int possibleDestinations, int existingInDirection) {
        int saveBridgesInEveryDirection = 0;
        if (requiredBridges == 2 * possibleDestinations) {
            saveBridgesInEveryDirection = 2;
        } else if (requiredBridges == 2 * possibleDestinations - 1) {
            saveBridgesInEveryDirection = 1;
        }

        return existingInDirection < saveBridgesInEveryDirection;
    }

    private Set<Island> getReachableUnfinishedNeighbours(Puzzle puzzle, final Island island) {
        return island
            .getNeighbours().stream()
            .filter(this::islandNeedsMoreBridges)
            .filter(neighbour -> !island.isBridgedTo(neighbour) || !island.getBridgeTo(neighbour).isDoubleBridge())
            .filter(neighbour -> noIntersectingBridge(puzzle, island, neighbour))
            .collect(Collectors.toSet());
    }

    private Set<Island> islandsThatNeedToConnectTo(Puzzle puzzle, Island island, Set<Island> neighbours) {
        return neighbours.stream()
            .filter(neighbour -> {
                Set<Island> reachableUnfinishedNeighbours = getReachableUnfinishedNeighbours(puzzle, neighbour);
                return reachableUnfinishedNeighbours.size() == 1 && reachableUnfinishedNeighbours.contains(island);
            })
            .collect(Collectors.toSet());
    }

    private boolean noIntersectingBridge(Puzzle puzzle, Island island1, Island island2) {
        return !puzzle.isAnyBridgeCrossing(island1.getPosition(), island2.getPosition());
    }

    private boolean islandNeedsMoreBridges(Island island) {
        // TODO Use getStatus?
        return island.getRemainingBridges() > 0;
    }
}
