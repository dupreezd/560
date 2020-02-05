package a1;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class State {

    private Color color;
    private String name;
    private List<State> neighbors;

    public static final Color blank = new Color("#####"); //states will be initialized w/ dummy color to avoid null pointer exception ambiguity

    public State(String s) {
        name = s;
        color = blank;
        neighbors = new ArrayList<State>();
    }

    public boolean equals(@NotNull String s) { return getName().equals(s); } //lets us quickly check state equality by name only, probably for testing purposes only

    //adds neighbors if not already in neighbor array
    public void addNeighbor(State s) {
        for (State neighbor : neighbors) {
            if (neighbor == s) { // == here because they should literally be identical state objects
                return;
            }
        }
        neighbors.add(s);
    }

    public void setColor(Color c) {
        color = c;
    }

    public void resetColor() {
        color = blank;
    }

    public List<State> getNeighbors() {
        return neighbors;
    }

    public String getColor() {
        return color.getName();
    }

    public String getName() {
        return name;
    }

}
