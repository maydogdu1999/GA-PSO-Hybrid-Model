import java.util.ArrayList;
import java.util.Random;

/**
 * Swarm
 *
 * Representation of a swarm and implementation of PSO on a swarm in order to
 * optimize a given function
 * 
 * @author Mustafa Aydogdu
 * @author Griffin Ott
 * @author Evan Phillips
 */
public class Swarm {

    public static int dim;
    public static String function;
    public static String topology;
    private static int swarm_size;
    private static double RA_PROB = 0.20;
    private static int ROWS;
    private static int COLUMNS;
    public double min;
    public double[] min_pos;
    public static Particle[] swarm;

    /**
     * Initializes an instance of a Swarm object
     *
     * @param top  the neighborhood topology of the swarm
     * @param size number particles in the swarm
     * @param func mathematical function we are trying to optimize
     * @param d    dimensions that the function is being evaluated in
     */

    public Swarm(String top, int size, String func, int d) {
        this.topology = top;
        this.swarm_size = size;
        this.function = func;
        this.dim = d;
        min_pos = new double[this.dim];

        // sets rows and columns if topology is vn
        if (size == 16) {
            ROWS = 4;
            COLUMNS = 4;
        } else if (size == 30) {
            ROWS = 5;
            COLUMNS = 6;
        } else {
            ROWS = 7;
            COLUMNS = 7;
        }
    }

    /**
     * After a swarm is generated, PSO algorithm is run over a user-defined number
     * of iterations where position and velocity vectors of each particle are
     * updated.
     * 
     * @param iter number of iterations
     * @return the best solution found, the smallest solution found
     */
    public ArrayList<Individual> execute(int iter) {
        double min = Double.MAX_VALUE;
        double[] min_pos = new double[dim];
        double[] bests = new double[iter / 1000]; // best value each 1000 iterations
        int best_ctr = 0;

        for (int i = 0; i < swarm.length; i++)
            swarm[i].set_gbest();

        for (int i = 0; i < iter; i++) {
            for (int k = 0; k < swarm.length; k++) {
                swarm[k].update_velocity_position();
                swarm[k].set_pbest();
            }

            if (this.topology.equals("gl")) { // we set the gbest for global seperately
                double curGbest = swarm[3].getBestVal(); // can be bestVal of any particle
                double[] curGbestPos = swarm[3].position;
                for (int k = 0; k < swarm.length; k++) {
                    if (swarm[k].getBestVal() < curGbest) {
                        curGbest = swarm[k].getBestVal();
                        curGbestPos = swarm[k].position;
                    }
                }
                for (int k = 0; k < swarm.length; k++) {
                    swarm[k].set_gbest_global(curGbestPos);
                }

            } else {
                for (int j = 0; j < swarm.length; j++) {
                    swarm[j].set_gbest();
                }
            }

            if (this.topology.equals("ra")) {
                Random r = new Random();
                for (int v = 0; v < swarm.length; v++) { // particle by particle basis
                    if (r.nextDouble() < RA_PROB)
                        // recreate particle's neighborhood
                        swarm[v].set_neighbors(generateRaNeighbors(swarm[v], swarm));
                }
            }

            for (int c = 0; c < swarm.length; c++) {
                if (swarm[c].getBestVal() < min) {
                    min = swarm[c].getBestVal();
                    min_pos = swarm[c].getPbest();
                }
            }
            if (i % 1000 == 0 && i != 0) {
                bests[best_ctr] = min;
                best_ctr++;
            }

        }
        // after iterations set min to best val in swarm
        for (int i = 0; i < swarm.length; i++) {
            if (swarm[i].getBestVal() < min) {
                min = swarm[i].getBestVal();
                min_pos = swarm[i].getPbest();
            }
        }
        this.min = min;
        this.min_pos = min_pos;
        // bests[best_ctr] = min;

        ArrayList<Individual> pop = new ArrayList<Individual>();
        for (int i = 0; i < Main.size; i++) {
            Individual new_ind = new Individual(swarm[i].position, function);
            pop.add(new_ind);
        }
        return pop;
    }

    /**
     * returns a random double in a specified range
     * 
     * @param min smallest possible value for random number
     * @param max greatest possible value for random number
     */
    public static double getRandomDouble(double min, double max) {
        Random rand = new Random();
        return ((rand.nextDouble() * (max - min) + min));
    }

    public static Particle getParticle(int i) {
        return swarm[i];
    }

    /**
     * generates a randomized position vector given a range
     * 
     * @param min smallest possible value for random number
     * @param max greatest possible value for random number
     */
    public static double[] createPosition(double min, double max) {
        double initial_position[];
        initial_position = new double[dim];
        for (int i = 0; i < dim; i++) {
            initial_position[i] = getRandomDouble(min, max);
        }
        return initial_position;
    }

    /**
     * generates a randomized velocity vector given a range
     * 
     * @param min smallest possible value for random number
     * @param max greatest possible value for random number
     */
    public static double[] createVelocity(double min, double max) {
        double initial_velocity[];
        initial_velocity = new double[dim];
        for (int i = 0; i < dim; i++) {
            initial_velocity[i] = getRandomDouble(min, max);
        }
        return initial_velocity;
    }

    /**
     * converts a 1D array into a 2D array for the purposes of defining neighbors in
     * von Neumann
     */
    public static Particle[][] vonNeumann_convert(Particle[] swarm) {
        Particle swarm_2d[][] = new Particle[ROWS][COLUMNS];
        int counter = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                swarm_2d[i][j] = swarm[counter];
                counter++;
            }
        }
        return swarm_2d;
    }

    /**
     * given a particle in a swarm, assigns the particle k-1 random neighbors
     * without repetition. Note: value of k = 5 being used.
     * 
     * @return the randomly generated neighbors of a given particle
     */
    public static Particle[] generateRaNeighbors(Particle particle, Particle[] swarm) {
        // k = 5
        ArrayList<Integer> indices = new ArrayList<Integer>();
        int counter = 0;
        int particle_index = 100; // out of bounds
        for (int k = 0; k < swarm.length; k++) { // create arrayList of swarm indices
            indices.add(counter);
            counter++;
            if (swarm[k].equals(particle)) { // find index of particle
                particle_index = k;
            }
        }
        Random rand = new Random();
        Particle neighbors[];
        neighbors = new Particle[5];

        neighbors[0] = swarm[indices.remove(particle_index)]; // don't duplicate itself
        neighbors[1] = swarm[indices.remove(rand.nextInt(indices.size()))];
        neighbors[2] = swarm[indices.remove(rand.nextInt(indices.size()))];
        neighbors[3] = swarm[indices.remove(rand.nextInt(indices.size()))];
        neighbors[4] = swarm[indices.remove(rand.nextInt(indices.size()))];

        return neighbors;
    }

    /**
     * generates a swarm
     * 
     * @return the initial swarm with particles randomly dispersed across the
     *         solution space according to specified ranges of each function
     */
    public static Particle[] generate_swarm(String function, String topology) {

        Particle swarm[];
        swarm = new Particle[swarm_size];
        if (function.equals("ros")) {
            for (int i = 0; i < swarm_size; i++) {
                swarm[i] = new Particle(createPosition(15.0, 30.0), createVelocity(-2.0, 2.0));
            }

        } else if (function.equals("ack")) {
            for (int i = 0; i < swarm_size; i++) {
                swarm[i] = new Particle(createPosition(16.0, 32.0), createVelocity(-2.0, 4.0));
            }

        } else if (function.equals("ras")) {
            for (int i = 0; i < swarm_size; i++) {
                swarm[i] = new Particle(createPosition(2.56, 5.12), createVelocity(-2.0, 4.0));
            }
        } else if (function.equals("zakh")) {
            for (int i = 0; i < swarm_size; i++) {
                swarm[i] = new Particle(createPosition(5.0, 10.0), createVelocity(-2.0, 4.0));
            }
        }

        else { // Styb
            for (int i = 0; i < swarm_size; i++) {
                swarm[i] = new Particle(createPosition(0.0, 5.0), createVelocity(-2.0, 4.0));
            }
        }

        create_neighborhoods(topology, swarm); // initialize neighbors of each particle
        return swarm;
    }

    /**
     * Given a topology, sets neighborhood for each particle in a swarm
     */
    private static void create_neighborhoods(String topology, Particle[] swarm) {

        if (topology.equals("gl")) {
            for (int i = 0; i < swarm.length; i++) {
                swarm[i].set_neighbors(swarm); // every particle’s neighborhood is the entire swarm
            }

        } else if (topology.equals("ri")) {
            for (int i = 0; i < swarm.length; i++) {
                Particle neighbors[];
                neighbors = new Particle[3];
                // edge case 1: particle with no neighbor to the left
                if (i == 0) {
                    neighbors[0] = swarm[swarm.length - 1];
                    neighbors[1] = swarm[i];
                    neighbors[2] = swarm[i + 1];
                }
                // edge case 2: particle with no neighbor to the right
                else if (i == swarm.length - 1) {
                    neighbors[0] = swarm[i - 1];
                    neighbors[1] = swarm[i];
                    neighbors[2] = swarm[0];
                } else { // particle has a neighbor to the left and right
                    neighbors[0] = swarm[i - 1];
                    neighbors[1] = swarm[i];
                    neighbors[2] = swarm[i + 1];
                }
                swarm[i].set_neighbors(neighbors);
            }

        } else if (topology.equals("ra")) {
            for (int i = 0; i < swarm.length; i++) {
                swarm[i].set_neighbors(generateRaNeighbors(swarm[i], swarm));
            }
        }
        // von Neumann
        else {
            Particle[][] swarm_2d = vonNeumann_convert(swarm);
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLUMNS; j++) {

                    Particle neighbors[];
                    neighbors = new Particle[5];
                    neighbors[0] = swarm_2d[i][j]; // a particle is its own neighbor

                    if (i == 0) { // particle is in row 0
                        neighbors[1] = swarm_2d[ROWS - 1][j];
                    } else {
                        // particle directly above
                        neighbors[1] = swarm_2d[i - 1][j];
                    }
                    if (i == ROWS - 1) { // particle is in the last row
                        neighbors[2] = swarm_2d[0][j];
                    } else {
                        // particle directly below
                        neighbors[2] = swarm_2d[i + 1][j];
                    }
                    if (j == 0) { // particle is in column 0
                        neighbors[3] = swarm_2d[i][COLUMNS - 1];
                    } else {
                        // particle directly to the left
                        neighbors[3] = swarm_2d[i][j - 1];
                    }
                    if (j == COLUMNS - 1) { // particle is in last column
                        neighbors[4] = swarm_2d[i][0];
                    } else {
                        // particle directly to the right
                        neighbors[4] = swarm_2d[i][j + 1];

                    }
                    swarm_2d[i][j].set_neighbors(neighbors);
                }
            }

        }
    }

}
