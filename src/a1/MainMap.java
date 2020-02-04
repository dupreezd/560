package a1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainMap {

    static boolean flag = false;
    public static void main(String[] args) throws IOException {

        List<Color> colors = new ArrayList<>(); //color index
        List<State> states = new ArrayList<>(); //state index
        Map<String, State> hashed_states = new HashMap<>(); //using a map here to simplify the process of recording neighboring states, since states are read in as strings multiple times, don't want to traverse array to match string to state every time

        InputStreamReader in = new InputStreamReader(System.in); //input stream object is needed to exit the last while loop below since inner does not close automatically over System.in

        /// READING INPUT ///

        //input format is the following:
        //one string representing color per line
        //newline
        //one string representing state per line
        //newline
        //two strings representing neighboring states per line

        while (!in.ready()) {}
        while (true) { //reading colors
            String next = next(in);
            if (next.equals("")) { break; }
            else {
                colors.add(new Color(next));
            }
        }
        while (true) { //reading states
            String next = next(in);
            if (next.equals("")) { break; }
            else {
                states.add(new State(next));
                hashed_states.put(next, states.get(states.size()-1));
            }
        }
        int check = System.in.available();
        while (true) { //adding neighboring states to one another's neighbor lists
            String next = next(in);
            String next_2 = next(in);
            State next_state = hashed_states.get(next);
            State next_neighbor = hashed_states.get(next_2);

            next_state.addNeighbor(next_neighbor);
            next_neighbor.addNeighbor(next_state);
            if (flag) { break; }
        }
        in.close();

        //------------------------------------------

        /// GENERAL TESTING ///

//        for (Color color: colors){
//            System.out.println(color.getName());
//        }
//        for (State state: states){
//            System.out.println(state.getName());
//            for (State neighbor: state.getNeighbors()) {
//                System.out.println("  " + neighbor.getName());
//            }
//        }
    }

    public static String next(InputStreamReader in) throws IOException {
        StringBuilder s = new StringBuilder();
        char c; int i;
        while (true) {
            i = in.read();
            c = (char)i;
            if (c == ' ' || c == '\n') { break; }
            s.append(c);
            if (!in.ready()) { flag = true; break; }
        }
        return s.toString();
    }

}
