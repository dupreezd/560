package a1;

import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class Country {

    protected List<Color> colors = new ArrayList<>(); //color index
    private List<State> states = new ArrayList<>(); //state index
    private Map<String, State> hashed_states = new HashMap<>(); //using a map here to simplify the process of recording neighboring states, since states are read in as strings multiple times, don't want to traverse array to match string to state every time
    private Map<State, Integer> index = new HashMap<>(); //map to find the index of a state in the array, for efficiency

    private boolean[][] borders; //adjacency matrix tracking which nodes border one another
    private Color[] tempColors; //parallel array to states list, used to assign colors temporarily while backtracking
    private Color[] temp; //same temporary color array for local search
    private int nodesSearched; //counter used to report # of steps in the backtracking output
    private int steps; //counter to report # of steps in local search

    private List<Set<Color>> domain;
    private PriorityQueue<State> pq;

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
        pq = new PriorityQueue<>(states.size(), new StateComparator());
        for (State state: states) {
            borders[index.get(state)][index.get(state)] = true;
            for (State neighbor: state.getFwdNeighbors()) {
                borders[index.get(state)][index.get(neighbor)] = true;
                borders[index.get(neighbor)][index.get(state)] = true;
            }
            pq.add(state);
        }
        tempColors = new Color[states.size()];
        temp = new Color[states.size()];

        domain = new ArrayList<>(states.size());
        Set<Color> tempSet = new HashSet<>(new ArrayList<Color>(colors));
        for (int i = 0; i < states.size(); i++) {
            domain.add(new HashSet<Color>(tempSet)); //just adding a set of all the colors for each state
        }
    }

    public boolean isValid(State s, Color c) { //check whether or not a state can be colored
        nodesSearched++;
        for (int i = 0; i < states.size(); i++) { //compare current state to all the other ones
            if (borders[index.get(s)][i] && c.equals(tempColors[i])) { return false; } //it's a neighbor and it's the same color, it's a no
        }
        return true;
    }

    //same as isValid method but uses temp[] for local search!
    public boolean isValidLS(State s, Color c) { //check whether or not a state can be colored

        for (int i = 0; i < states.size(); i++) { //compare current state to all the other ones
            if (borders[index.get(s)][i] && c.equals(temp[i])) { return false; } //it's a neighbor and it's the same color, it's a no
        }
        return true;
    }

    public boolean paint(State state) { //recursive function that tests colors and updates tempColors to something valid, or return false
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

    public boolean arcIsValid(State s, Color c, List<Set<Color>> tempDomain, PriorityQueue<State> tempPq) { //returns empty list if failed, returns new domain if valid

        tempDomain.get(s.getPosition()).clear();
        tempDomain.get(s.getPosition()).add(c);

        List<State> checkerQ = new ArrayList<>();
        checkerQ.add(s);
        checkerQ.addAll(s.getFwdNeighbors());

        while (checkerQ.size() > 0) {
            Set<Color> tempSet = tempDomain.get(checkerQ.get(0).getPosition());

            if (tempSet.size() == 0) {
                return false;
            }

            if (tempSet.size() == 1) {
                Color c2 = a1.State.blank;
                for (Color color: tempSet) {c2 = color;}

                for(State neighbor: checkerQ.get(0).getFwdNeighbors()) {
                    if(tempDomain.get(neighbor.getPosition()).remove(c2)) {
                        checkerQ.addAll(neighbor.getFwdNeighbors());
                    }
                    if (tempDomain.get(neighbor.getPosition()).size() == 0) {
                        return false;
                    }
                }

                tempPq.remove(checkerQ.get(0));
            }

            checkerQ.remove(0);
        }
        return true;
    }

    public boolean arcPaint(List<Set<Color>> domain, PriorityQueue<State> pq) {
        State state = pq.poll();
        nodesSearched++;

        for(Color color: domain.get(state.getPosition())) {

            List<Set<Color>> tempDomain = new ArrayList<Set<Color>>(domain);
            for (int i = 0; i < tempDomain.size(); i++) {
                tempDomain.add(i, new HashSet<Color>(tempDomain.get(i)));
                tempDomain.remove(i+1);
            }

            PriorityQueue<State> tempPq = new PriorityQueue<>(pq);

            if (arcIsValid(state, color, tempDomain, tempPq)) {

                if (tempPq.isEmpty()) {
                    this.pq = tempPq;
                    this.domain = tempDomain;
                    return true;
                }

                if (arcPaint(tempDomain, tempPq)) {
                    pq = tempPq;
                    domain = tempDomain;
                    return true;
                }
            }
        }
        return false;
    }

    public void backtrackingSearch() { //runs the paint function, actually changes the state's colors, and prints the output as requested
        setBorders();
        System.out.println("\nBacktracking search result: ");
//        if (paint(states.get(0))) {
//            for (State s : states) {
//                s.setColor(tempColors[index.get(s)]);
//                System.out.println(s.getName() + " " + s.getColor());
//            }
        if (arcPaint(domain, pq)) {
            for (int i = 0; i < domain.size(); i++) {
                for (Color color: domain.get(i)) {
                    states.get(i).setColor(color);
                    System.out.println(states.get(i).getName() + " " + color.getName());
                }
            }
        } else {
            System.out.println("No solution");
        }
        System.out.println("Nodes searched: " + nodesSearched);
    }

    public int checkConstraints(State name, Color checking){ //objective function that counts number of conflicts with neighboring states
        int con = 0;

        for (State s: name.getFwdNeighbors()) {
            if (checking.toString() == s.getColor()){ //if state being checked is same color as neighbor, increment counter
                con++;
            }
        }
        for (State s: name.getBwdNeighbors()) {
            if (checking.toString() == s.getColor()){
                con++;
            }
        }
        return con;
    }

    public boolean isComplete(){ //checks if every state is valid to see if we have solved the problem
        for (State s: states){
            if (isValidLS(s, temp[index.get(s)])){
                continue;
            } if (index.get(s) + 1 == states.size()){
                return true; //true only if we have reached the end of the list
            } else {
                return false;
            }
        }

        return true;
    }

    public void localSearch(){
        setBorders();
        Random r = new Random(); //will use for random assignment
        System.out.println("\nLocal Search Result: ");

        //start by assigning random colors
        for (State s: states){
            temp[index.get(s)] = colors.get(r.nextInt(colors.size()));
        }

        steps++; //increment steps

        while (!isComplete()) { //run loop until all states are valid (see helper method)

            for (int i = 0; i < 10; i++) { //10 random starting places before checking the whole list again

                //generate random starting place
                int start = r.nextInt(states.size());

                //check conflicts for existing color, and next color. keep color with lowest constraints
                State current = states.get(start);
                int curConstraints = checkConstraints(current, temp[index.get(current)]); //current number of constraints on that state

                for (Color c : colors) { //if there are less constraints with a different color, sets that color in temp
                    int next = checkConstraints(current, c);
                    if (next < curConstraints) {
                        temp[start] = c;
                        steps++; //increment counter
                        curConstraints = next; //new lowest number to beat
                    }
                }

            }
        }

        //print final values
        for (State s: states){
            System.out.println(s.getName() + " " + temp[index.get(s)].getName());
        }
        System.out.println("Number of steps: " + steps);
    }

    class StateComparator implements Comparator<State>{
        public int compare(State s1, State s2) {
            if (s1.getFwdNeighbors().size() < s2.getFwdNeighbors().size()) {return 1;}
            else if (s1.getFwdNeighbors().size() > s2.getFwdNeighbors().size()) {return -1;}
            else {return 0;}
        }
    }

}
