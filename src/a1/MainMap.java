package a1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainMap {

    static boolean flag = false; //flag that updates when the input is no longer available
    public static void main(String[] args) throws IOException {

        List<Color> colors = new ArrayList<>(); //color index
        List<State> states = new ArrayList<>(); //state index
        Map<String, State> hashed_states = new HashMap<>(); //using a map here to simplify the process of recording neighboring states, since states are read in as strings multiple times, don't want to traverse array to match string to state every time

        InputStreamReader in = new InputStreamReader(System.in); //input stream object is needed to exit the last while loop below since inner does not close automatically over System.in

        /// READING INPUT ///

        /*
        input format is the following:
        one string representing color per line
        newline
        one string representing state per line
        newline
        two strings representing neighboring states per line
         */

        while (!in.ready()) {} //stalls until an input it available
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
        do { //adding neighboring states to one another's neighbor lists, do-while makes the loop break once we reach the end of the input
            String next = next(in);
            String next_2 = next(in);
            State next_state = hashed_states.get(next);
            State next_neighbor = hashed_states.get(next_2);
            next_state.addNeighbor(next_neighbor); //add the states to one another's neighbor list, now we have a graph
            next_neighbor.addNeighbor(next_state);
        } while (!flag);
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
        StringBuilder s = new StringBuilder(); //string builder is suggested to be used when dynamically concatenating chars
        char c;
        while (true) {
            c = (char)in.read(); //in.read() gets the integer form of the next char on the buffer, and returns -1 when buffer is empty
            if (c == ' ' || c == '\n') { break; }
            s.append(c);
            if (!in.ready()) { flag = true; break; } //in.ready() is false when the buffer is empty, so we set the flag to break the while loop in main
        }
        return s.toString();
    }

}
