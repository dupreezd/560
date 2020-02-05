package a1;

import java.lang.reflect.Array;
import java.util.*;

public class Country {

    private List<Color> colors = new ArrayList<>(); //color index
    private List<State> states = new ArrayList<>(); //state index
    private Color[] colorTest;

    private Map<String, State> hashed_states = new HashMap<>(); //using a map here to simplify the process of recording neighboring states, since states are read in as strings multiple times, don't want to traverse array to match string to state every time
    private Map<State, Integer> adjIndex = new HashMap<>();

    private boolean[][] borders;

    public Country() {}

    public void addColor(Color c) {colors.add(c);}
    public void addState(State s) {states.add(s);}
    public Map<String, State> getHashMap() {return hashed_states;}
    public List<Color> getColors() {return colors;}
    public List<State> getStates() {return states;}

    public void buildAdjIndex() {
        for (int i = 0; i < states.size(); i++) {
            adjIndex.put(states.get(i), i);
        }
    }

    public void setBorders() {
        buildAdjIndex();
        borders = new boolean[states.size()][states.size()];
        for (State state: states) {
            borders[adjIndex.get(state)][adjIndex.get(state)] = true;
            for (State neighbor: state.getNeighbors()) {
                borders[adjIndex.get(state)][adjIndex.get(neighbor)] = true;
                borders[adjIndex.get(neighbor)][adjIndex.get(state)] = true;
            }
        }
        colorTest = new Color[states.size()];
    }

    public boolean isValid(State s, Color c) {
        for (int i = 0; i < states.size(); i++) {
            if (borders[adjIndex.get(s)][i] && c.equals(colorTest[i])) { return false; }
        }
        return true;
    }

    public boolean paint(State state) {
        int colorCount = colors.size();

        for (Color color: colors) {
            if (isValid(state, color)) {
                colorTest[adjIndex.get(state)] = color;
                if (adjIndex.get(state)+1 == states.size()) { return true; }
                if (paint(states.get(adjIndex.get(state)+1))) { return true; }
                colorTest[adjIndex.get(state)] = null;
            }
        }
        return false;
    }

    public void construct() {
        setBorders();
        if (paint(states.get(0))) {
            for (State s : states) {
                s.setColor(colorTest[adjIndex.get(s)]);
                System.out.println(s.getName() + " " + s.getColor());
            }
        } else {
            System.out.println("No solution");
        }
    }


}
