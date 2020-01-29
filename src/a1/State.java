package a1;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class State {

    private Color color;
    private String name;
    private List<State> neighbors;

    public static final Color blank = new Color("#####");

    public State(String s) {
        name = s;
        color = blank;
        neighbors = new ArrayList<State>();
    }

    public boolean equals(@NotNull String s) {
        return getName().equals(s);
    }

    public void addNeighbor(State s) {
        for (int i = 0; i < neighbors.size(); i++) {
            if (neighbors.get(i).equals(s)) {
                return;
            }
        }
        neighbors.add(s);
    }

    public void setColor(Color c) {
        color = c;
    }

    public void setColor(String s) {
        color = new Color(s);
    }

    public String getColor() {
        return color.getName();
    }

    public String getName() {
        return name;
    }

}
