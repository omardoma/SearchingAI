package Search;

public class Node {
    private State state;
    private Node parent;
    private Operator operator;
    private int depth;

    public Node(State state, Node parent, int depth, Operator operator) {
        this.state = state;
        this.parent = parent;
        this.depth = depth;
        this.operator = operator;
    }

    public State getState() {
        return state;
    }

    public Node getParent() {
        return parent;
    }

    public Operator getOperator() {
        return operator;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isRootNode() {
        return parent == null;
    }
}
