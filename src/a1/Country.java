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

    public boolean isValidLS(State s, Color c) { //check whether or not a state can be colored

        for (int i = 0; i < states.size(); i++) { //compare current state to all the other ones
            if (borders[index.get(s)][i] && c.equals(temp[i])) { return false; } //it's a neighbor and it's the same color, it's a no
        }
        return true;
    }
    public int checkConstraints(State name, Color checking){ //objective function that counts number of conflicts with neighboring states
        int con = 0;

        for (State s: name.getFwdNeighbors()) {
            if (checking == temp[index.get(s)]){ //if state being checked is same color as neighbor, increment counter
                con++;
            }
        }
        for (State s: name.getBwdNeighbors()) {
            if (checking == temp[index.get(s)]){
                con++;
            }
        }
        return con;
    }
    public boolean isComplete(){ //checks if every state is valid to see if we have solved the problem
        for (State s: states){

            if (checkConstraints(s, temp[index.get(s)]) == 0){
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
        boolean complete = false;
        Random r = new Random(); //will use for random assignment
        System.out.println("\nLocal Search Result: ");

        for (State s: states){
            temp[index.get(s)] = colors.get(0);
        }

        steps++; //increment steps
        long startTime = System.currentTimeMillis();
        long maxTime = 60 * 1000 + startTime;
        while (!complete) { //run loop until all states are valid (see helper method)
            for (int i = 0; i < 10; i++) { //10 random starting places before checking the whole list again
                //generate random starting place
                int start = r.nextInt(states.size());

                //check conflicts for existing color, and next color. keep color with lowest constraints
                State current = states.get(start);
                int curConstraints = checkConstraints(current, temp[index.get(current)]); //current number of constraints on that state

                if (curConstraints != 0){ //don't enter this loop if 0 conflicts
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
            if ((System.currentTimeMillis() > maxTime)) {
                System.out.println("Timed out.");
                break;
            }
            if (isComplete()) {complete = true;}
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
