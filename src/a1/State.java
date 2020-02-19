package a1;

import java.util.ArrayList;
import java.util.List;

public class State {

    private Color color;
    private String name;
    private List<State> fwdNeighbors; //forward arcs
    private List<State> bwdNeighbors; //backward arcs
    private int position;

    public static final Color blank = new Color("#####"); //states will be initialized w/ dummy color to avoid null pointer exception ambiguity

    public State(String s, int p) {
        name = s;
        color = blank;
        position = p;
        fwdNeighbors = new ArrayList<State>();
        bwdNeighbors = new ArrayList<State>();
    }

    //adds neighbors if not already in neighbor array
    public void addFwdNeighbor(State s) {
        for (State neighbor : fwdNeighbors) {
            if (neighbor == s) { // == here because they should literally be identical state objects
                return;
            }
        }
        fwdNeighbors.add(s);
    }
    public void addBwdNeighbor(State s) {
        for (State neighbor : bwdNeighbors) {
            if (neighbor == s) { // == here because they should literally be identical state objects
                return;
            }
        }
        bwdNeighbors.add(s);
    }

    public void setColor(Color c) {color = c;}
    public List<State> getFwdNeighbors() {return fwdNeighbors;}
    public List<State> getBwdNeighbors() {return bwdNeighbors;}
    public String getName() {return name;}
    public int getPosition() {return position;}

}
