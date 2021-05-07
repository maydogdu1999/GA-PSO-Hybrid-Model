import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Comparator;

public class Individual {
    double[] variableAssign; //filled with 0s and 1s
    double fitnessScore; //number of clauses satisfied
    

     /**
     * Initializes an individual given the cnf caluses and an assignment of boolean(0/1) values to each variable
     */
    Individual(double[] variableAssign, String function) {
        this.variableAssign = variableAssign;
        calAndSetFitness(function);
    }

    /**
     * For variable v, gets the value of v, which is stored at v-1 in the variable assignment array
     */
    public double getValueOfVar(int variable) {
        return variableAssign[variable]; //minus 1 used because min variable is 1, but we want
                                             //array to be zero-indexed
    }

     /**
     * Takes in a list of clauses in cnf form and determines the number of clauses that are satisfied, given the 
     * array of variable assignments stored as an instance variable
     */
    public void calAndSetFitness(String function) {
    	if (function.equals("ros")) {
            this.fitnessScore = Main.eval_ros(variableAssign);

        }
        
    }

    public void setFitness(int fitnessScore) {
        this.fitnessScore = fitnessScore;
    }

    public Double getFitnessScore() {
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
