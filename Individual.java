import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Comparator;

public class Individual {
    int[] variableAssign; //filled with 0s and 1s
    int fitnessScore; //number of clauses satisfied
    

     /**
     * Initializes an individual given the cnf caluses and an assignment of boolean(0/1) values to each variable
     */
    Individual(int[] variableAssign, ArrayList<Integer> cnf) {
        this.variableAssign = variableAssign;
        calAndSetFitness(cnf);
    }

    /**
     * For variable v, gets the value of v, which is stored at v-1 in the variable assignment array
     */
    public int getValueOfVar(int variable) {
        return variableAssign[variable - 1]; //minus 1 used because min variable is 1, but we want
                                             //array to be zero-indexed
    }

     /**
     * Takes in a list of clauses in cnf form and determines the number of clauses that are satisfied, given the 
     * array of variable assignments stored as an instance variable
     */
    public void calAndSetFitness(ArrayList<Integer> cnf) {
        int totalScore = 0;
        boolean increaseScore = false;

        for (int i = 0; i < cnf.size(); i++) {
            Integer cur_literal = cnf.get(i);

            if (cur_literal == 0) { // if literal is 0
                if (increaseScore == true) {
                    totalScore++;
                }
                increaseScore = false; // set back to false for the next clause
            }

            if (cur_literal > 0) { // if literal is positive
                if (variableAssign[cur_literal - 1] == 1) {
                    increaseScore = true;
                }
            }
            if (cur_literal < 0) { // if literal is negative
                if (variableAssign[-(cur_literal + 1)] == 0) {
                    increaseScore = true;
                }
            }
        }
        this.fitnessScore = totalScore;
    }

    public void setFitness(int fitnessScore) {
        this.fitnessScore = fitnessScore;
    }

    public Integer getFitnessScore() {
        return fitnessScore;
    }

    /**
     * Formats the printing of an individual's variable assignment
     */
    public String toString() {
        String finalString = "";
        for (int i = 0; i < variableAssign.length; i++) {
            finalString += variableAssign[i] + " ";

        }
        return finalString;
    }

}
