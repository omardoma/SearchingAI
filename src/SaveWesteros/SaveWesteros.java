package SaveWesteros;

import Search.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SaveWesteros extends Problem {
    private Grid grid;
    private List<Cell> whiteWalkers;
    private List<Cell> obstacles;
    private Cell dragonStone;
    private int agentCapacity;
    public static final double PICKUP_COST = 6;
    public static final double MOVE_COST = 5;
    public static final double KILL_COST = 4;

    public SaveWesteros() {
        super();
        initOperators();
        whiteWalkers = new ArrayList<>();
        obstacles = new ArrayList<>();
    }

    public Grid getGrid() {
        return grid;
    }

    public List<Cell> getWhiteWalkers() {
        return whiteWalkers;
    }

    public List<Cell> getObstacles() {
        return obstacles;
    }

    public Cell getDragonStone() {
        return dragonStone;
    }

    public int getAgentCapacity() {
        return agentCapacity;
    }

    private void initOperators() {
        super.setOperators(Arrays.asList("UP", "DOWN", "LEFT", "RIGHT", "PICKUP", "KILL"));
    }

    private List<Cell> cloneCells(List<Cell> cells) {
        return new ArrayList<>(cells);
    }

    private void prepareSearch() {
        whiteWalkers = grid.getWhiteWalkers();
        obstacles = grid.getObstacles();
        agentCapacity = whiteWalkers.size();
        dragonStone = grid.getDragonStone();
        super.setInitialState(new SaveWesterosState(cloneCells(whiteWalkers), grid.getAgentCell(), 0));
    }

    private String getSequenceOfMoves(List<Node> expandedNodes) {
        return String.join(" -> ", expandedNodes.stream().skip(1).map(node -> node.getOperator().getName()).collect(Collectors.toList()));
    }

    private void visualizeGrid(List<Node> expandedNodes) {
        Cell[][] cells = grid.getCells();
        List<Cell> stateWhiteWalkers;
        Cell stateAgentCell;
        Cell current;
        for (SaveWesterosState state : expandedNodes.stream().map(node -> (SaveWesterosState) node.getState()).collect(Collectors.toList())) {
            stateWhiteWalkers = state.getWhiteWalkers();
            stateAgentCell = state.getAgentCell();
            System.out.println();
            System.out.println();
            for (int i = 0; i < grid.getM(); i++) {
                for (int j = 0; j < grid.getN(); j++) {
                    current = cells[i][j];
                    System.out.print("[" + (obstacles.contains(current) ? "O" : stateWhiteWalkers.contains(current) ? "W" : current == dragonStone ? "D" : current == stateAgentCell ? "A" : "E") + "]");
                }
                System.out.println();
            }
            System.out.println();
            System.out.println();
        }
    }

    private boolean canVisitCell(Cell cell) {
        return !(whiteWalkers.contains(cell) || obstacles.contains(cell));
    }

    private SaveWesterosState killWhiteWalkers(List<Cell> whiteWalkers, Cell currentAgentCell, int dragonGlass) {
        List<Cell> clonedWhiteWalkers = cloneCells(whiteWalkers);
        int row = currentAgentCell.getRow();
        int col = currentAgentCell.getCol();
        Predicate<Cell> predicate = cell -> {
            int adjacentRow = cell.getRow();
            int adjacentCol = cell.getCol();
            return (adjacentRow == row - 1 && adjacentCol == col) || (adjacentRow == row + 1 && adjacentCol == col) || (adjacentRow == row && adjacentCol == col - 1) || (adjacentRow == row && adjacentCol == col + 1);
        };
        if (clonedWhiteWalkers.removeIf(predicate)) {
            dragonGlass--;
        }
        return new SaveWesterosState(clonedWhiteWalkers, currentAgentCell, dragonGlass);
    }

    public Grid genGrid() {
        return new Grid();
    }

    public Grid genGrid(int n) throws Exception {
        return new Grid(n);
    }

    public Grid genGrid(int m, int n) throws Exception {
        return new Grid(m, n);
    }

    @Override
    public boolean isGoal(State state) {
        return ((SaveWesterosState) state).getWhiteWalkers().isEmpty();
    }

    @Override
    public List<Node> expand(Node node, List<String> operators) {
        List<Node> expansion = new ArrayList<>();

        // Return empty list in case of Iterative Deepening Search with exceeded limit for the generalSearch to stop
        if (node.getDepth() > super.getDepthLimit()) {
            return expansion;
        }

        SaveWesterosState state = (SaveWesterosState) node.getState();
        Cell stateAgentCell = state.getAgentCell();
        int row = stateAgentCell.getRow();
        int col = stateAgentCell.getCol();
        Cell[][] cells = grid.getCells();
        List<Cell> stateWhiteWalkers;
        Cell nextAgentCell;

        for (String operator : operators) {
            switch (operator) {
                case "UP":
                    if (row > 0) {
                        nextAgentCell = cells[row - 1][col];
                        if (canVisitCell(nextAgentCell)) {
                            expansion.add(new Node(new SaveWesterosState(cloneCells(state.getWhiteWalkers()), nextAgentCell, state.getDragonGlass()), node, node.getDepth() + 1, new Operator(operator, MOVE_COST)));
                        }
                    }
                    break;
                case "DOWN":
                    if (row < grid.getM() - 1) {
                        nextAgentCell = cells[row + 1][col];
                        if (canVisitCell(nextAgentCell)) {
                            expansion.add(new Node(new SaveWesterosState(cloneCells(state.getWhiteWalkers()), nextAgentCell, state.getDragonGlass()), node, node.getDepth() + 1, new Operator(operator, MOVE_COST)));
                        }
                    }
                    break;
                case "LEFT":
                    if (col > 0) {
                        nextAgentCell = cells[row][col - 1];
                        if (canVisitCell(nextAgentCell)) {
                            expansion.add(new Node(new SaveWesterosState(cloneCells(state.getWhiteWalkers()), nextAgentCell, state.getDragonGlass()), node, node.getDepth() + 1, new Operator(operator, MOVE_COST)));
                        }
                    }
                    break;
                case "RIGHT":
                    if (col < grid.getN() - 1) {
                        nextAgentCell = cells[row][col + 1];
                        if (canVisitCell(nextAgentCell)) {
                            expansion.add(new Node(new SaveWesterosState(cloneCells(state.getWhiteWalkers()), nextAgentCell, state.getDragonGlass()), node, node.getDepth() + 1, new Operator(operator, MOVE_COST)));
                        }
                    }
                    break;
                case "PICKUP":
                    if (stateAgentCell == dragonStone) {
                        expansion.add(new Node(new SaveWesterosState(cloneCells(state.getWhiteWalkers()), stateAgentCell, agentCapacity), node, node.getDepth() + 1, new Operator(operator, PICKUP_COST)));
                    }
                    break;
                case "KILL":
                    stateWhiteWalkers = state.getWhiteWalkers();
                    if (!stateWhiteWalkers.isEmpty() && state.getDragonGlass() > 0) {
                        SaveWesterosState newState = killWhiteWalkers(stateWhiteWalkers, stateAgentCell, state.getDragonGlass());
                        expansion.add(new Node(newState, node, node.getDepth() + 1, new Operator("KILL", KILL_COST - (stateWhiteWalkers.size() - newState.getWhiteWalkers().size()))));
                    }
                    break;
            }
        }
        return expansion;
    }

    public List search(Grid grid, Strategy strategy, boolean visualize) {
        this.grid = grid;
        prepareSearch();
        List result = new ArrayList();
        Node goalNode;
        if (strategy == Strategy.ID) {
            goalNode = iterativeDeepeningSearch(this);
        } else {
            goalNode = generalSearch(this, strategy);
        }
        if (goalNode != null) {
            List<Node> chosenExpandedNodes = getChosenExpandedNodes(goalNode);
            String sequenceOfMoves = getSequenceOfMoves(chosenExpandedNodes);
            double solutionCost = 0;
            if (!(strategy == Strategy.BF || strategy == Strategy.DF || strategy == Strategy.ID)) {
                solutionCost = chosenExpandedNodes.stream().skip(1).mapToDouble(node -> node.getOperator().getCost()).sum();
            }
            int chosenExpandedNodesCount = chosenExpandedNodes.size() - 1;
            if (visualize) {
                visualizeGrid(chosenExpandedNodes);
            }
            result.add(sequenceOfMoves);
            result.add(solutionCost);
            result.add(chosenExpandedNodesCount);
        }
        return result;
    }

    public static void main(String[] args) {
        SaveWesteros saveWesteros = new SaveWesteros();
        Grid grid = saveWesteros.genGrid();

        System.out.println("\n\n-------------------Breadth First-----------------\n\n");

        List result = saveWesteros.search(grid, Strategy.BF, false);
        if (!result.isEmpty()) {
            grid.printGridInfo();
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }

        System.out.println("\n\n-------------------Breadth First-----------------\n\n");


        System.out.println("\n\n-------------------Depth First-----------------\n\n");

        result = saveWesteros.search(grid, Strategy.DF, false);
        if (!result.isEmpty()) {
            grid.printGridInfo();
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }

        System.out.println("\n\n-------------------Depth First-----------------\n\n");


        System.out.println("\n\n-------------------Uniform Cost-----------------\n\n");

        result = saveWesteros.search(grid, Strategy.UC, false);
        if (!result.isEmpty()) {
            grid.printGridInfo();
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("Solution Cost: " + result.get(1));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }

        System.out.println("\n\n-------------------Uniform Cost-----------------\n\n");


        System.out.println("\n\n-------------------Iterative Deepening-----------------\n\n");

        result = saveWesteros.search(grid, Strategy.ID, false);
        if (!result.isEmpty()) {
            grid.printGridInfo();
            System.out.println("Depth Limit: " + saveWesteros.getDepthLimit());
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }
        System.out.println("\n\n-------------------Iterative Deepening-----------------\n\n");
    }
}
