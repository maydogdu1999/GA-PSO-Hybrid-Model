import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class GA {

    public static int num_of_positions;
    public static int num_of_individuals;
    public static ArrayList<Integer> cnf;
    private static double CROSSOVER_PROB;
    private static double MUTATION_PROB;
    private static String function;
    private static double MUTATION_SHIFT;

    /**
     * Initializes an instance of a GA object
     *
     * @param num_of_positions   number of positions that appear in an individual's
     *                           position vector
     * @param num_of_individuals number of individuals to be generated to make up
     *                           the population
     * @param function           a string for the function name
     * @param CROSSOVER_PROB     For each pairing from the breeding pool, the
     *                           likelihood that a crossover happens
     * @param MUTATION_PROB      For each variable assignment in each individual in
     *                           each iteration, the likelihood of a mutation
     *                           happening that flips the value between 0 and 1
     * @param shift              a double representing the percentage by which a
     *                           variable in an invidiudal will increased or
     *                           decreased
     */

    public GA(int num_of_positions, int num_of_individuals, String function, double CROSSOVER_PROB,
            double MUTATION_PROB, double shift) {
        this.num_of_positions = num_of_positions;
        this.num_of_individuals = num_of_individuals;
        this.function = function;
        this.CROSSOVER_PROB = CROSSOVER_PROB;
        this.MUTATION_PROB = MUTATION_PROB;
        this.MUTATION_SHIFT = shift;

    }

    /**
     * Runs the GA algorithm given the number of iterations to execute and the type
     * of crossover and selection algorithms
     *
     * Generates initial population and selects a breeding pool biased towards
     * higher fitness, then implements a crossover, potentially perform mutations,
     * and repeat the process with the newly generated population Best-fitness
     * indivudal generated is kept track throughout and the positions of the final
     * individuals are returned as double[][] to be handed in to the PSO.
     * 
     */
    public double[][] execute(int numIter, String crossOver, String selection, ArrayList<Individual> curGen) {

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
            curGen = mutation(curGen, MUTATION_PROB, MUTATION_SHIFT);

        }

        double[][] new_positions = new double[Main.size][Main.dimensionality];

        for (int i = 0; i < Main.size; i++) {
            for (int j = 0; j < Main.dimensionality; j++) {
                double num = curGen.get(5).getValueOfVar(j);
                new_positions[i][j] = num;
            }
        }

        return new_positions;

    }

    /**
     * Selects a breeding pool from a given population where each individual's
     * probability of being chosen is their rank divided by the sum of all
     * individual's ranks, where the highest value rank is the best individual
     * 
     * Chosen with replacement
     */
    public static ArrayList<Individual> rankSelection(ArrayList<Individual> population) {
        ArrayList<Individual> selectedPop = new ArrayList<Individual>();
        Collections.sort(population, new IndivComparator()); // sort fitness scores in order of least fit to most fit
        Collections.reverse(population);
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
     * Selects a breeding pool based on a population where each individual's
     * likelihood of being chosen is e raised to their rank divided by the sum of
     * this value for all individuals, where the highest rank corresponds to the
     * most fit individual
     * 
     * Chosen with replacement
     */
    public static ArrayList<Individual> expRankSelection(ArrayList<Individual> population) {
        ArrayList<Individual> selectedPop = new ArrayList<Individual>(); // will store selected pop.
        HashMap<Individual, Double> prob = new HashMap<Individual, Double>(); // will store probabilities
        Collections.sort(population, new IndivComparator()); // Sort the population
        Collections.reverse(population); // lower fitness represents more fit indiv
        double sum = 0.0; // sum of all exponentials
        for (int i = 0; i < population.size(); i++) {
            sum += Math.exp(i + 1);
        }

        for (int i = 0; i < population.size(); i++) { // map individuals to their probabilities
            double indProb = Math.exp(i + 1) / sum;
            prob.put(population.get(i), indProb);
        }

        Random rand = new Random();
        while (selectedPop.size() < population.size()) { // do the selection
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
     * Selects a breeding pool based on a current population, where each
     * individual's likelihood of being chosen is equal to e raised to the
     * individual's fitness score divided by the sum of this value for all
     * individuals
     * 
     * Chosen with replacement
     */
    public static ArrayList<Individual> boltzmannSelection(ArrayList<Individual> population) {
        ArrayList<Individual> bp = new ArrayList<Individual>(); // will store selected pop.
        HashMap<Individual, Double> prob = new HashMap<Individual, Double>(); // for storing probabilities
        Iterator<Individual> it = population.iterator();
        double sum = 0.0;
        while (it.hasNext()) {
            sum += Math.exp(1 / it.next().getFitnessScore());
        }
        Iterator<Individual> itr = population.iterator();
        while (itr.hasNext()) {
            Individual i = itr.next();
            double p = Math.exp(1 / i.getFitnessScore()) / sum; // favoring individuals with lower fitness scores
            prob.put(i, p); // map indiivduals to their probabilities
        }

        Random rand = new Random();
        while (bp.size() < population.size()) { // Do the selection
            double r = rand.nextDouble();
            double probSum = 0.0;
            Iterator<Individual> i = population.iterator();
            while (i.hasNext()) {
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
     * Implements crossovers with pairs from the breeding pool with some
     * probability, where a dividing line is made at some point in the variable
     * assignments, and the values are swapped across that point
     */
    public static ArrayList<Individual> onePointCross(ArrayList<Individual> population) {
        ArrayList<Individual> nextPop = new ArrayList<Individual>();
        Random random = new Random();
        if (population.size() % 2 != 0) { // checks to see if pop size is odd
            Individual single = population.remove(random.nextInt(population.size()));
            nextPop.add(single);
        }
        while (population.size() > 1) {

            Random rand = new Random();
            Individual firstParent = population.remove(rand.nextInt(population.size()));
            Individual secondParent = population.remove(rand.nextInt(population.size()));

            if (rand.nextDouble() < CROSSOVER_PROB) {

                Individual firstChild = new Individual(new double[num_of_positions], function);
                Individual secondChild = new Individual(new double[num_of_positions], function);
                int randomIndex = rand.nextInt((num_of_positions - 1)) + 1;
                // accounting for the valid range of indices, excluding 0 and size
                for (int i = 0; i < randomIndex; i++) {
                    firstChild.position[i] = firstParent.position[i];
                }

                for (int i = 0; i < randomIndex; i++) {
                    secondChild.position[i] = secondParent.position[i];
                }

                for (int i = randomIndex; i < num_of_positions; i++) {
                    firstChild.position[i] = secondParent.position[i];
                }

                for (int i = randomIndex; i < num_of_positions; i++) {
                    secondChild.position[i] = firstParent.position[i];
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
     * Implements crossovers with pairs of individuals from the breeding pool where
     * for each slot in the variable assignments, the variable assignment in the
     * child is randomly picked from one of the two parents
     * 
     * When a crossover is performed, it is done twice so that the population size
     * remains constant
     */
    public static ArrayList<Individual> uniformCross(ArrayList<Individual> population) {

        ArrayList<Individual> newgen = new ArrayList<Individual>();
        Random random = new Random();
        if (population.size() % 2 != 0) { // checks to see if pop size is odd
            Individual single = population.remove(random.nextInt(population.size()));
            newgen.add(single);
        }
        while (population.size() > 0) {
            Random r = new Random();
            Individual p1 = population.remove(r.nextInt(population.size()));

            Individual p2 = population.remove(r.nextInt(population.size()));
            if (r.nextDouble() < CROSSOVER_PROB) {
                for (int i = 0; i < 2; i++) {
                    double vars[] = new double[num_of_positions];
                    for (int k = 1; k < vars.length; k++) {
                        if (r.nextDouble() < 0.5)
                            vars[k] = p1.getValueOfVar(k);
                        else
                            vars[k] = p2.getValueOfVar(k);
                    }
                    newgen.add(new Individual(vars, function));
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
         * Comparator that compares two individuals based on fitness. Returns a positive
         * value if o1 has higher score, negative value is o2 has higher score, and 0 if
         * they are the same
         */
        @Override
        public int compare(Individual o1, Individual o2) {

            return o1.getFitnessScore().compareTo(o2.getFitnessScore());
        }
    }

    /**
     * Goes through population and, with some probability for each variable
     * assignment in each individual, flips the variable assignment at that slot
     */
    public static ArrayList<Individual> mutation(ArrayList<Individual> children, double mutation_prob, double shift) {
        ArrayList<Individual> mutated_children = new ArrayList<Individual>();
        for (Individual child : children) {
            for (int i = 0; i < (child.position).length; i++) {
                double rand = Math.random();
                if (rand <= mutation_prob) {
                    double random = Math.random();
                    if (random < 0.5)
                        child.position[i] *= (1 + shift);
                    else
                        child.position[i] *= (1 - shift);
                }
            }
            mutated_children.add(child);
        }
        return mutated_children;
    }

}
