package Search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class Problem {
    private List<String> operators;
    private State initialState;
    private int depthLimit;

    public Problem() {
        depthLimit = Integer.MAX_VALUE;
    }

    public List<String> getOperators() {
        return operators;
    }

    protected void setOperators(List<String> operators) {
        this.operators = operators;
    }

    public State getInitialState() {
        return initialState;
    }

    protected void setInitialState(State initialState) {
        this.initialState = initialState;
    }

    public int getDepthLimit() {
        return depthLimit;
    }

    public abstract boolean isGoal(State state);

    public abstract List<Node> expand(Node node, List<String> operator);

    public abstract double calculatePathCost(Node node);

    protected static List<Node> getChosenExpandedNodes(Node node) {
        LinkedList<Node> nodes = new LinkedList<>();
        while (node != null) {
            nodes.addFirst(node);
            node = node.getParent();
        }
        return nodes;
    }

    private static boolean isRepeatedState(State testState, List<State> repeatedStates) {
        return repeatedStates.stream().anyMatch(state -> state.isSame(testState));
    }

    protected static Node iterativeDeepeningSearch(Problem problem) {
        problem.depthLimit = 0;
        Node goalNode = null;
        while (problem.depthLimit < Integer.MAX_VALUE) {
            goalNode = generalSearch(problem, Strategy.ID);
            if (goalNode != null) {
                break;
            }
            problem.depthLimit++;
        }
        return goalNode;
    }

    protected static Node generalSearch(Problem problem, Strategy strategy) {
        ArrayList<State> repeatedStates = new ArrayList<>();
        LinkedList<Node> nodes = new LinkedList<>();
        nodes.add(new Node(problem.initialState, null, 0, null));
        Node node;
        while (!nodes.isEmpty()) {
            node = nodes.removeFirst();
            if (problem.isGoal(node.getState())) {
                return node;
            }
            for (Node successorNode : problem.expand(node, problem.operators)) {
                if (!isRepeatedState(successorNode.getState(), repeatedStates)) {
                    switch (strategy) {
                        case BF:
                            nodes.addLast(successorNode);
                            break;
                        case DF:
                            nodes.addFirst(successorNode);
                            break;
                        case ID:
                            nodes.addFirst(successorNode);
                            break;
                        case UC:
                            if (problem.calculatePathCost(node) < problem.calculatePathCost(successorNode)) {
                                nodes.addLast(successorNode);
                            } else {
                                nodes.addFirst(successorNode);
                            }
                            break;
                    }
                    repeatedStates.add(successorNode.getState());
                }
            }
        }
        return null;
    }

}
