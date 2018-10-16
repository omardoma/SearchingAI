package Search;

import java.util.*;

public abstract class Problem {
    private List<Operator> operators;
    private State initialState;

    public List<Operator> getOperators() {
        return operators;
    }

    public void setOperators(List<Operator> operators) {
        this.operators = operators;
    }

    public State getInitialState() {
        return initialState;
    }

    public void setInitialState(State initialState) {
        this.initialState = initialState;
    }

    public abstract boolean isGoal(State state);

    public abstract List<Node> expand(Node node, List<Operator> operator);

    public static List<Node> getChosenExpandedNodes(Node node) {
        LinkedList<Node> nodes = new LinkedList<>();
        while (node != null) {
            nodes.addFirst(node);
            node = node.getParent();
        }
        return nodes;
    }

    public static double calculatePathCost(Node node) {
        if (node.isRootNode()) {
            return 0;
        }
        return getChosenExpandedNodes(node).stream().skip(1).mapToDouble(currentNode -> node.getOperator().getCost()).sum();
    }

    public static Node generalSearch(Problem problem, Strategy strategy, int limit) {
        List<State> repeatedStates = new ArrayList();
        LinkedList<Node> nodes = new LinkedList<>();
        nodes.add(new Node(problem.initialState, null, 0, null));
        Node node;
        while (!nodes.isEmpty()) {
            node = nodes.removeFirst();
            if (problem.isGoal(node.getState())) {
                return node;
            }
            for (Node successorNode : problem.expand(node, problem.operators)) {
                if (!repeatedStates.stream().anyMatch(state -> state.isSame(successorNode.getState()))) {
                    switch (strategy) {
                        case BF:
                            nodes.addLast(successorNode);
                            break;
                        case DF:
                            nodes.addFirst(successorNode);
                            break;
                        case ID:
                            if (successorNode.getDepth() <= limit) {
                                nodes.addFirst(successorNode);
                                break;
                            } else {
                                return null;
                            }
                        case UC:
                            if (calculatePathCost(node) < calculatePathCost(successorNode)) {
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
