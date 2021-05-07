import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class GA {

    public static int num_of_variables;
    public static int num_of_individuals;
    public static ArrayList<Integer> cnf;
    private static double CROSSOVER_PROB;
    private static double MUTATION_PROB;

    /**
     * Initializes an instance of a GA object
     *
     * @param num_of_variables number of variables that appear in the cnf
     * @param num_of_individuals number of individuals to be generated to make up the population
     * @param cnf ArrayList representing the cnf clauses to be optimized 
     * @param CROSSOVER_PROB For each pairing from the breeding pool, the likelihood that a crossover happens
     * @param MUTATION_PROB For each variable assignment in each individual in each iteration, 
     * the likelihood of a mutation happening that flips the value between 0 and 1
     */
    public GA(int num_of_variables, int num_of_individuals, ArrayList<Integer> cnf, double CROSSOVER_PROB,
            double MUTATION_PROB) {
        this.num_of_variables = num_of_variables;
        this.num_of_individuals = num_of_individuals;
        this.cnf = cnf;
        this.CROSSOVER_PROB = CROSSOVER_PROB;
        this.MUTATION_PROB = MUTATION_PROB;

    }

    /**
     * Initializes an individual where each variable assignment is a random number in the set {0,1}
     */
    public static Individual createIndividual() {
        Random rn = new Random();
        int indiv[];
        indiv = new int[num_of_variables];
        for (int i = 0; i < num_of_variables; i++) {
            indiv[i] = rn.nextInt(2);
        }
        Individual individual = new Individual(indiv, cnf);
        return individual;
    }

    /**
     * Runs the GA algorithm given the number of iterations to execute and the type of crossover and selection algorithms
     *
     * Generates initial population and selects a breeding pool biased towards higher fitness, then implements a 
     * crossover, potentially perform mutations, and repeat the process with the newly generated population
     * Best-fitness indivudal generated is kept track throughout and the overall highest-fitness individual is 
     * returned as the optimized solution
     */
    public Individual execute(int numIter, String crossOver, String selection) {
        ArrayList<Individual> curGen = createPopulation(num_of_individuals);
        Individual max_fit = Collections.max(curGen, new IndivComparator());
        for (int i = 0; i < numIter; i++) {

            if (selection.equals("rs")) {
                curGen = rankSelection(curGen);
            } else if (selection.equals("es")) {
                curGen = expRankSelection(curGen);
            } else {
                curGen = boltzmannSelection(curGen);
            }
            if (crossOver.equals("uc")) {
                curGen = uniformCross(curGen);
            } else {
                curGen = onePointCross(curGen);
            }
            curGen = mutation(curGen, MUTATION_PROB);

            if (max_fit.getFitnessScore() < Collections.max(curGen, new IndivComparator()).getFitnessScore()) {
                max_fit = Collections.max(curGen, new IndivComparator());
                Main.setBestIteration(i);
            }
        }

        return max_fit;
   
    }
 
    /**
     * Population of given size created where each individual's variable assignments are randomly generated
     */
    public static ArrayList<Individual> createPopulation(int population_size) {
        ArrayList<Individual> population = new ArrayList<Individual>();
        for (int i = 0; i < population_size; i++) {
            population.add(createIndividual());
        }
        return population;
    }

    /**
     * Selects a breeding pool from a given population where each individual's probability of being chosen is 
     * their rank divided by the sum of all individual's ranks, where the highest value rank is the best individual
     * 
     * Chosen with replacement
     */
    public static ArrayList<Individual> rankSelection(ArrayList<Individual> population) {
        ArrayList<Individual> selectedPop = new ArrayList<Individual>();
        Collections.sort(population, new IndivComparator()); // sort fitness scores in order of least fit to most fit
        // create denominator of each individual's propbability
        double denominator = ((population.size() * (population.size() + 1)) / 2);
        // select individuals randomly with those of higher ranks having greater probs
        while (selectedPop.size() < population.size()) {
            double p = Math.random();
            double cumulativeProbability = 0.0;
            for (int i = 0; i < population.size(); i++) {
                cumulativeProbability += (i + 1) / denominator;
                if (p <= cumulativeProbability) {
                    selectedPop.add(population.get(i));
                    break;
                }
            }
        }
        return selectedPop;
    }
    
    /**
     * Selects a breeding pool based on a population where each individual's likelihood of being chosen is 
     * e raised to their rank divided by the sum of this value for all individuals, where the highest rank 
     * corresponds to the most fit individual
     * 
     * Chosen with replacement
     */
    public static ArrayList<Individual> expRankSelection(ArrayList<Individual> population) {
        ArrayList<Individual> selectedPop = new ArrayList<Individual>(); //will store selected pop.
        HashMap<Individual, Double> prob = new HashMap<Individual, Double>(); //will store probabilities

        Collections.sort(population, new IndivComparator()); //Sort the population
        double sum = 0.0; //sum of all exponentials
        for (int i = 0; i < population.size(); i++) {
            sum += Math.exp(i + 1);
        }

        for (int i = 0; i < population.size(); i++) { //map individuals to their probabilities
            double indProb = Math.exp(i + 1) / sum;
            prob.put(population.get(i), indProb);
        }

        Random rand = new Random();
        while (selectedPop.size() < population.size()) { //do the selection
            double r = rand.nextDouble();
            double cumulative = 0.0;
            Iterator<Individual> iter = population.iterator();
            while (true) {

                Individual ind = iter.next();
                cumulative += prob.get(ind);
                if (cumulative > r || !iter.hasNext()) {
                    selectedPop.add(ind);
                    break;
                }
            }
        }
        return selectedPop;

    }

    /**
     * Selects a breeding pool based on a current population, where each individual's likelihood of being chosen
     * is equal to e raised to the individual's fitness score divided by the sum of this value for all individuals
     * 
     * Chosen with replacement
     */
    public static ArrayList<Individual> boltzmannSelection(ArrayList<Individual> population) {
        ArrayList<Individual> bp = new ArrayList<Individual>(); //will store selected pop.
        HashMap<Individual, Double> prob = new HashMap<Individual, Double>(); //for stroing probabilities
        Iterator<Individual> it = population.iterator();
        double sum = 0.0;
        while (it.hasNext()) {
            sum += Math.exp(it.next().getFitnessScore());
        }
        Iterator<Individual> itr = population.iterator();
        while (itr.hasNext()) {
            Individual i = itr.next();
            double p = Math.exp(i.getFitnessScore()) / sum;
            prob.put(i, p); //map indiivduals to their probabilities
        }

        Random rand = new Random();
        while (bp.size() < population.size()) { //Do the selection
            double r = rand.nextDouble();
            double probSum = 0.0;
            Iterator<Individual> i = population.iterator();
            while (i.hasNext())
            {
                Individual ind = i.next();
                probSum += prob.get(ind);
                if (probSum > r || !i.hasNext()) {
                    bp.add(ind);
                    break;
                }
            }
        }
        return bp;
    }
    
    /**
     * Implements crossovers with pairs from the breeding pool with some probability, where a dividing line is 
     * made at some point in the variable assignments, and the values are swapped across that point
     */
    public static ArrayList<Individual> onePointCross(ArrayList<Individual> population) {
        ArrayList<Individual> nextPop = new ArrayList<Individual>();

        while (population.size() > 1) {

            Random rand = new Random();
            Individual firstParent = population.remove(rand.nextInt(population.size()));
            Individual secondParent = population.remove(rand.nextInt(population.size()));

            if (rand.nextDouble() < CROSSOVER_PROB) {

                Individual firstChild = new Individual(new int[num_of_variables], cnf);
                Individual secondChild = new Individual(new int[num_of_variables], cnf);
                int randomIndex = rand.nextInt((num_of_variables - 1)) + 1;
                // accounting for the valid range of indices, excluding 0 and size
                for (int i = 0; i < randomIndex; i++) {
                    firstChild.variableAssign[i] = firstParent.variableAssign[i];
                }

                for (int i = 0; i < randomIndex; i++) {
                    secondChild.variableAssign[i] = secondParent.variableAssign[i];
                }

                for (int i = randomIndex; i < num_of_variables; i++) {
                    firstChild.variableAssign[i] = secondParent.variableAssign[i];
                }

                for (int i = randomIndex; i < num_of_variables; i++) {
                    secondChild.variableAssign[i] = firstParent.variableAssign[i];
                }

                nextPop.add(firstChild);
                nextPop.add(secondChild);
            } else { // if no crossover takes place
                nextPop.add(firstParent);
                nextPop.add(secondParent);
            }
        }
        return nextPop;
    }

    /**
     * Implements crossovers with pairs of individuals from the breeding pool where for each slot in the variable
     * assignments, the variable assignment in the child is randomly picked from one of the two parents
     * 
     * When a crossover is performed, it is done twice so that the population size remains constant
     */
    public static ArrayList<Individual> uniformCross(ArrayList<Individual> population) {
        ArrayList<Individual> newgen = new ArrayList<Individual>();
        while (population.size() > 1) {
            Random r = new Random();
            Individual p1 = population.remove(r.nextInt(population.size()));
            Individual p2 = population.remove(r.nextInt(population.size()));
            if (r.nextDouble() < CROSSOVER_PROB) {
                for (int i = 0; i < 2; i++) {
                    int vars[] = new int[num_of_variables];
                    for (int k = 1; k < vars.length; k++) {
                        if (r.nextDouble() < 0.5)
                            vars[k] = p1.getValueOfVar(k);
                        else
                            vars[k] = p2.getValueOfVar(k);
                    }
                    newgen.add(new Individual(vars, cnf));
                }
            } else {
                newgen.add(p1);
                newgen.add(p2);
            }
        }
        return newgen;
    }

    static class IndivComparator implements Comparator<Individual> {

        /**
         * Comparator that compares two individuals based on fitness.  Returns a positive value if o1 has higher
         * score, negative value is o2 has higher score, and 0 if they are the same
         */
        @Override
        public int compare(Individual o1, Individual o2) {

            return o1.getFitnessScore().compareTo(o2.getFitnessScore());
        }
    }

    /**
     * Goes through population and, with some probability for each variable assignment in each individual, flips 
     * the variable assignment at that slot
     */
    public static ArrayList<Individual> mutation(ArrayList<Individual> children, double mutation_prob) {
        ArrayList<Individual> mutated_children = new ArrayList<Individual>();
        for (Individual child : children) {
            for (int i = 0; i < (child.variableAssign).length; i++) {
                double rand = Math.random();
                if (rand <= mutation_prob) {
                    if (child.variableAssign[i] == 0) {
                        child.variableAssign[i] = 1;
                    } else {
                        child.variableAssign[i] = 0;
                    }
                }
            }
            mutated_children.add(child);
        }
        return mutated_children;
    }
}
