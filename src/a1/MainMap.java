package a1;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class MainMap {

    public static void main(String[] args) throws IOException {

        List<Color> colors = new ArrayList<Color>(); //color index
        List<State> states = new ArrayList<State>(); //state index
        Map<String, State> hashed_states = new HashMap<String, State>(); //using a map here to simplify the process of recording neighboring states, since states are read in as strings multiple times, don't want to traverse array to match string to state every time

        Scanner scan = new Scanner(System.in);
        InputStream in_stream = System.in; //input stream object is needed to exit the last while loop below since scanner does not close automatically over System.in

        /// READING INPUT ///

        //input format is the following:
        //one string representing color per line
        //newline
        //one string representing state per line
        //newline
        //two strings representing neighboring states per line

        while (true) { //reading colors
            String next = scan.nextLine();
            if (next.equals("")) { break; }
            else {
                colors.add(new Color(next));
            }
        }
        while (true) { //reading states
            String next = scan.nextLine();
            if (next.equals("")) { break; }
            else {
                states.add(new State(next));
                hashed_states.put(next, states.get(states.size()-1));
            }
        }
        while (scan.nextLine().equals("")) { //adding neighboring states to one another's neighbor lists
            String next = scan.next();
            String next_2 = scan.next();
            State next_state = hashed_states.get(next);
            State next_neighbor = hashed_states.get(next_2);

            next_state.addNeighbor(next_neighbor);
            next_neighbor.addNeighbor(next_state);
        }
        scan.close();

        //------------------------------------------

        /// GENERAL TESTING ///

//        for (Color color: colors){
//            System.out.println(color.getName());
//        }
        for (State state: states){
            //System.out.println(state.getName());
            for (State neighbor: state.getNeighbors()) {
                System.out.print("  " + neighbor.getName());
            }
        }
    }
}
