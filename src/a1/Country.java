package a1;

import java.lang.reflect.Array;
import java.util.*;

public class Country {

    private List<Color> colors = new ArrayList<>(); //color index
    private List<State> states = new ArrayList<>(); //state index
    private Map<String, State> hashed_states = new HashMap<>(); //using a map here to simplify the process of recording neighboring states, since states are read in as strings multiple times, don't want to traverse array to match string to state every time
    private Map<State, Integer> index = new HashMap<>(); //map to find the index of a state in the array, for efficiency

    private boolean[][] borders; //adjacency matrix tracking which nodes border one another
    private Color[] tempColors; //parallel array to states list, used to assign colors temporarily while backtracking
    private int nodesSearched; //counter used to report # of steps in the output

    public Country() {} //default constructor

    public void addColor(Color c) {colors.add(c);}
    public void addState(State s) {states.add(s);}
    public Map<String, State> getHashMap() {return hashed_states;}
    public List<Color> getColors() {return colors;}
    public List<State> getStates() {return states;}

    public void buildIndex() { //just a helper function to make the index map
        for (int i = 0; i < states.size(); i++) {
            index.put(states.get(i), i);
        }
    }

    public void setBorders() { //constructs the adjacency matrix "borders"
        buildIndex();
        borders = new boolean[states.size()][states.size()];
        for (State state: states) {
            borders[index.get(state)][index.get(state)] = true;
            for (State neighbor: state.getFwdNeighbors()) {
                borders[index.get(state)][index.get(neighbor)] = true;
                borders[index.get(neighbor)][index.get(state)] = true;
            }
        }
        tempColors = new Color[states.size()];
    }

    public boolean isValid(State s, Color c) { //check whether or not a state can be colored
        nodesSearched++;
        for (int i = 0; i < states.size(); i++) { //compare current state to all the other ones
            if (borders[index.get(s)][i] && c.equals(tempColors[i])) { return false; } //it's a neighbor and it's the same color, it's a no
        }
        return true;
    }

    public boolean paint(State state) { //recursive function that tests colors and updates tempColors to something valid, or return false
        int colorCount = colors.size();

        for (Color color: colors) {
            if (isValid(state, color)) { //if it's valid to paint it the current color...
                tempColors[index.get(state)] = color; //paint it this color
                if (index.get(state)+1 == states.size()) { return true; }  //if we're at the end, then we're done
                if (paint(states.get(index.get(state)+1))) { return true; } //otherwise, keep going
                tempColors[index.get(state)] = null; //if the above recursive run didn't work, we have to reset and try another color
            }
        }
        return false; //if it never returns true, then there's no solution
    }

    public void backtrackingSearch() { //runs the paint function, actually changes the state's colors, and prints the output as requested
        setBorders();
        System.out.println("\nBacktracking search result: ");
        if (paint(states.get(0))) {
            for (State s : states) {
                s.setColor(tempColors[index.get(s)]);
                System.out.println(s.getName() + " " + s.getColor());
            }
        } else {
            System.out.println("No solution");
        }
        System.out.println("Nodes searched: " + nodesSearched);
    }


}
