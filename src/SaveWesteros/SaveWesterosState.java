package SaveWesteros;

import Search.State;

import java.util.List;

public class SaveWesterosState implements State {
    private List<Cell> whiteWalkers;
    private Cell agentCell;
    private int dragonGlass;

    public SaveWesterosState(List<Cell> whiteWalkers, Cell agentCell, int dragonGlass) {
        this.whiteWalkers = whiteWalkers;
        this.agentCell = agentCell;
        this.dragonGlass = dragonGlass;
    }

    public List<Cell> getWhiteWalkers() {
        return whiteWalkers;
    }

    public Cell getAgentCell() {
        return agentCell;
    }

    public int getDragonGlass() {
        return dragonGlass;
    }

    @Override
    public boolean isSame(State state) {
        SaveWesterosState westerosState = (SaveWesterosState) state;
        return this.dragonGlass == westerosState.dragonGlass && this.agentCell == westerosState.agentCell && this.whiteWalkers.size() == westerosState.whiteWalkers.size();
    }
}
