package a1;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class State {

    private Color color;
    private String name;
    private List<State> fwdNeighbors;
    private List<State> bwdNeighbors;

    public static final Color blank = new Color("#####"); //states will be initialized w/ dummy color to avoid null pointer exception ambiguity

    public State(String s) {
        name = s;
        color = blank;
        fwdNeighbors = new ArrayList<State>();
        bwdNeighbors = new ArrayList<State>();
    }

    public boolean equals(@NotNull String s) {return getName().equals(s);} //lets us quickly check state equality by name only, probably for testing purposes only

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
    public void resetColor() {color = blank;}
    public List<State> getFwdNeighbors() {return fwdNeighbors;}
    public List<State> getBwdNeighbors() {return bwdNeighbors;}
    public String getColor() {return color.getName();}
    public String getName() {return name;}

}
