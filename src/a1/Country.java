package a1;

import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class Country {

    protected List<Color> colors = new ArrayList<>();
    private List<State> states = new ArrayList<>();
    private Map<String, State> hashed_states = new HashMap<>(); //using a map here to simplify the process of recording neighboring states, since states are read in as strings multiple times, don't want to traverse array to match string to state every time
    private Map<State, Integer> index = new HashMap<>(); //map to find the index of a state in the array, for efficiency

    private boolean[][] borders; //adjacency matrix tracking which nodes border one another
    private Color[] temp; //temporary color array for local search
    private int[] problems;
    private int nodesSearched; //counter used to report # of steps in the backtracking output
    private int steps; //counter to report # of steps in local search
    private int con = 0;

    private List<Set<Color>> domain; //set of possible domains for backtracking (arc consistency)
    private PriorityQueue<State> pq; //priority queue to sort by most constraining variable

    public Country() {} //default constructor

    public void addColor(Color c) {colors.add(c);}
    public void addState(State s) {states.add(s);}
    public Map<String, State> getHashMap() {return hashed_states;}
    public List<State> getStates() {return states;}

    public void buildIndex() { //a helper function to make the index map
        for (int i = 0; i < states.size(); i++) {
            index.put(states.get(i), i);
        }
    }
    public void setBorders() { //constructs the adjacency matrix "borders" for local search and the initial pq + set of domains for backtracking
        buildIndex();
        borders = new boolean[states.size()][states.size()]; //adjacency matrix
        pq = new PriorityQueue<>(states.size(), new StateComparator()); //priority queue with most constraining states on top
        for (State state: states) { //filling the matrix and pq
            borders[index.get(state)][index.get(state)] = true;
            for (State neighbor: state.getFwdNeighbors()) {
                borders[index.get(state)][index.get(neighbor)] = true;
                borders[index.get(neighbor)][index.get(state)] = true;
            }
            pq.add(state);
        }
        temp = new Color[states.size()]; //initialize the temp color array for local search
        problems = new int[states.size()]; //initialize constraint count array for local search
        domain = new ArrayList<>(states.size()); //initialize domains
        Set<Color> tempSet = new HashSet<>(new ArrayList<Color>(colors)); //make a set of all the colors
        for (int i = 0; i < states.size(); i++) {
            domain.add(new HashSet<Color>(tempSet)); //adding a copy of the set of all the colors for each state
        }
    }

    public boolean arcIsValid(State s, Color c, List<Set<Color>> tempDomain, PriorityQueue<State> tempPq) { //true if arc consistency holds

        tempDomain.get(s.getPosition()).clear();
        tempDomain.get(s.getPosition()).add(c); //set the color for current state

        List<State> checkerQ = new ArrayList<>(); //queue used to check & recheck arc consistency each time domains are altered
        checkerQ.add(s);
        checkerQ.addAll(s.getFwdNeighbors());

        while (checkerQ.size() > 0) {
            Set<Color> tempSet = tempDomain.get(checkerQ.get(0).getPosition());
            if (tempSet.size() == 0) {return false;} //when any domain is empty, we need to backtrack
            if (tempSet.size() == 1) { //when a domain has only one color, we can check that it's neighbors don't have that colors in their domain
                Color c2 = a1.State.blank;
                for (Color color: tempSet) {c2 = color;} //easiest way to get the 1 color out of the set, lol
                for(State neighbor: checkerQ.get(0).getFwdNeighbors()) { //check the fwd arc and update neighbor's domains
                    if(tempDomain.get(neighbor.getPosition()).remove(c2)) {checkerQ.addAll(neighbor.getFwdNeighbors());} //if domain was altered, neighbors need to be re-checked
                    if (tempDomain.get(neighbor.getPosition()).size() == 0) {return false;} //if domain becomes empty, it's a fail
                }
                tempPq.remove(checkerQ.get(0)); //we don't need to check this state later in this branch because it's domain is already fixed
            }
            checkerQ.remove(0); //dequeue
        }
        return true; //if no arc consistency fails happened, the color assignment is valid
    }
    public boolean arcPaint(List<Set<Color>> domain, PriorityQueue<State> pq) { //main backtracking function, recursive
        State state = pq.poll();
        nodesSearched++;
        for(Color color: domain.get(state.getPosition())) { //for color in this state's domain

            List<Set<Color>> tempDomain = new ArrayList<Set<Color>>(domain); //shallow copy domain
            for (int i = 0; i < tempDomain.size(); i++) { //turn domain into a one-level-deep copy
                tempDomain.add(i, new HashSet<Color>(tempDomain.get(i)));
                tempDomain.remove(i+1);
            }
            PriorityQueue<State> tempPq = new PriorityQueue<>(pq); //copy the pq

            if (arcIsValid(state, color, tempDomain, tempPq)) {
                if (tempPq.isEmpty()) { //when we find a complete solution we update the main domain
                    this.pq = tempPq;
                    this.domain = tempDomain;
                    return true;
                }
                if (arcPaint(tempDomain, tempPq)) { //if the recursive call works, we give take it's temp domain and pq
                    pq = tempPq;
                    domain = tempDomain;
                    return true;
                }
            }
        }
        return false;
    }
    public void backtrackingSearch() { //runs the arcPaint function, actually changes the state's colors, and prints the output as requested
        setBorders();
        System.out.println("\nBacktracking search result: ");
        if (arcPaint(domain, pq)) {
            for (int i = 0; i < domain.size(); i++) {
                for (Color color: domain.get(i)) {
                    states.get(i).setColor(color);
                    System.out.println(states.get(i).getName() + " " + color.getName());
                }
            }
        } else {System.out.println("No solution");}
        System.out.println("Nodes searched: " + nodesSearched);
    }

    public State checkConstraints(State name, Color checking){ //objective function that counts number of conflicts with neighboring states & returns state that historically causes most issues
        con = 0;
        int mostProblems = 0; //for counting the number of problems that most problematic state has
        State problemChild = name; //node with highest accumulated conflicts

        //check all neighbors to see if they cause constraints
            //we only check neighbors instead of all the states to save time--over time a state will build up problem count anyway
        for (State s : name.getFwdNeighbors()) {
            if (checking.equals(temp[index.get(s)])) { //if state being checked is same color as neighbor, increment counter
                con++;
                if (++problems[index.get(s)] > mostProblems) {
                    mostProblems = problems[index.get(s)];
                    problemChild = s;
                }
            }
        }
        for (State s : name.getBwdNeighbors()) {
            if (checking.equals(temp[index.get(s)])) {
                con++;
                if (++problems[index.get(s)] > mostProblems) {
                    mostProblems = problems[index.get(s)];
                    problemChild = s;
                }
            }
        }

        return problemChild;
    }
    public boolean isComplete(){ //checks if every state is valid to see if we have solved the problem
        for (State s: states){
            checkConstraints(s, temp[index.get(s)]);
            if (con > 0) {return false;}
        }
        return true;
    }

    public void localSearch(){
        setBorders();
        boolean complete = false;
        Random r = new Random(); //will use for random assignment
        System.out.println("\nLocal Search Result: ");
        for (State s: states){temp[index.get(s)] = colors.get(r.nextInt(colors.size()));} //start by randomly assigning colors
        int localSteps = 0; //step counter for random resents, maxes at 1,000,000 steps

        long startTime = System.currentTimeMillis(); //setting up timeout condition
        long maxTime = 60 * 1000 + startTime;

        while (!complete) { //run loop until all states are valid (see helper method)
            int start = r.nextInt(states.size()); //picks a random state to start at, or on reset
            State current = states.get(start);
            for (int i = 0; i < states.size()*states.size(); i++) { //randomly reset the searched node after n^2 runs, where n = number of states
                steps++; //total steps
                localSteps++; //steps since last reset

                //check conflicts for existing color, and next color. keep color with lowest constraints
                State problemChild = checkConstraints(current, temp[index.get(current)]); //update constrains from current assignment
                State newProblemChild = problemChild;
                int curConstraints = con;

                if (curConstraints > 0){ //don't enter this loop if 0 conflicts
                    for (Color c : colors) { //if there are less constraints with a different color, sets that color in temp
                        //int next = checkConstraints(current, c);
                        newProblemChild = checkConstraints(problemChild, c); //which one is worst if we try another color?
                        int next = con; //how many overall constraints did the new color cause?
                        if (next < curConstraints) {
                            temp[index.get(problemChild)] = c;
                            curConstraints = next; //new lowest number to beat
                        }
                    }
                    if (newProblemChild.getName().equals(problemChild.getName())) { //if we still have the same node, reset it's problem value to break out of a cycle
                        problems = new int[states.size()];
                        break;
                    }
                }
                current = newProblemChild; //adjust from the new most problematic state
                if (isComplete()) { //break condition
                    complete = true;
                    for (State s: states){
                        System.out.println(s.getName() + " " + temp[index.get(s)].getName());
                    }
                    break;
                }
                if (localSteps > 1000000) { //if the steps exceed 1 mil we are probably stuck at a minima, need to reset
                    for (State s: states){temp[index.get(s)] = colors.get(r.nextInt(colors.size()));}
                    localSteps = 0;
                }
            }
            if ((System.currentTimeMillis() > maxTime)) { //timeout after 1 min
                System.out.println("Failed. Search time exceeded one minute.");
                break;
            }
        }
        System.out.println("Number of steps: " + steps);
    }

    class StateComparator implements Comparator<State>{ //indicates which state is most constraining
        public int compare(State s1, State s2) {
            if (s1.getFwdNeighbors().size() < s2.getFwdNeighbors().size()) {return 1;}
            else if (s1.getFwdNeighbors().size() > s2.getFwdNeighbors().size()) {return -1;}
            else {return 0;}
        }
    }

}
