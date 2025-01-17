package a1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class MainMap {

    static boolean flag = false; //flag that updates when the input is no longer available
    public static void main(String[] args) throws IOException {

        Country country = new Country();
        //InputStreamReader in = new InputStreamReader(System.in); //for testing (reading from console)
        Scanner scan = new Scanner(System.in);

        //region *READING INPUT*
        /*
        input format is the following:
        one string representing color per line
        newline
        one string representing state per line
        newline
        two strings representing neighboring states per line
        newline?
         */
        /*
            while (!in.ready()) {} //stalls until an input it available
            while (true) { //reading colors
                String next = next(in);
                if (next.equals("")) {
                    break;
                } else {
                    country.addColor(new Color(next));
                }
            }
            while (true) { //reading states
                String next = next(in);
                if (next.equals("")) {
                    break;
                } else {
                    country.addState(new State(next, country.getStates().size()));
                    country.getHashMap().put(next, country.getStates().get(country.getStates().size() - 1));
                }
            }
            do { //adding neighboring states to one another's neighbor lists, do-while makes the loop break once we reach the end of the input
                String next = next(in);
                String next_2 = next(in);
                State next_state = country.getHashMap().get(next);
                State next_neighbor = country.getHashMap().get(next_2);
                next_state.addFwdNeighbor(next_neighbor); //add the states to one another's neighbor list, now we have a graph
                next_neighbor.addBwdNeighbor(next_state);
            } while (!flag);
            in.close();
        */

        while (true) { //reading colors
            String next = scan.nextLine();
            if (next.equals("")) {
                break;
            } else {
                country.addColor(new Color(next));
            }
        }
        while (true) { //reading states
            String next = scan.nextLine();
            if (next.equals("")) {
                break;
            } else {
                country.addState(new State(next, country.getStates().size()));
                country.getHashMap().put(next, country.getStates().get(country.getStates().size() - 1));
            }
        }
        do { //adding neighboring states to one another's neighbor lists, do-while makes the loop break once we reach the end of the input
            String next = scan.next();
            String next_2 = scan.next();
            State next_state = country.getHashMap().get(next);
            State next_neighbor = country.getHashMap().get(next_2);
            next_state.addFwdNeighbor(next_neighbor); //add the states to one another's neighbor list, now we have a graph
            next_neighbor.addBwdNeighbor(next_state);
        } while (scan.hasNext());
        //endregion

        country.backtrackingSearch();
        country.localSearch();

    }

    /*
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
    */

}
