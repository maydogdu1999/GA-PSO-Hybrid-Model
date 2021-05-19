import java.util.*;
import java.io.*;

/**
 * Main
 *
 * Takes in user input and prints out best solution found by a particle
 * 
 * @author Mustafa Aydogdu
 * @author Griffin Ott
 * @author Evan Phillips
 */
public class Main {

    static int runGA = 60; 
    static int runPSO = 140;
    static String topology = "ra";
    static String function = "styb";
        static String selection = "rs"; //or bs

    static int size = 49;
    static int dimensionality = 30;
    static double ga_cross_prob = 0.7; 
    static double ga_mut_prob = 0.3; 
    static int iterations = 100;
    static double shift = 0.3; 

    public static void main(String[] args) {
        if(args.length != 1)
            System.out.println("Usage: java Main <function>");
        else
        {
        Main.function = args[0];
    
        Swarm s = new Swarm(topology, size, function, dimensionality); // intialize swarm
		Particle[] init_swarm = Swarm.generate_swarm(function, topology); //initialize random particles
		s.swarm = init_swarm; // make the random particles the particles of the initialized swarm
		ArrayList<Individual> curPop = s.execute(runPSO); //execute PSO for runPSO times
		
		GA population = new GA(dimensionality, size, function, ga_cross_prob, ga_mut_prob, shift);
		// initialize a GA population
		double[][] curPositions; // initialize position vectors to be used

		
		double min_Val = Double.MAX_VALUE; //keep track of min_val
		double[] min_pos; //keep track of min position

		
		// start the loop
		for (int i = 0; i < iterations; i++) {
			// execute GA
			curPositions = population.execute(runGA, "uc", selection, curPop); 
			
			//using the curPositions, update particles to be handed in to PSO
			for (int j = 0; j < size; j++) {
				for (int z = 0; z < dimensionality; z++) {
					s.getParticle(j).position[z] = curPositions[j][z];
				}
			}
			
			//execute PSO (now the particles' positions have been updated above)
			curPop = s.execute(runPSO);
			
			//keep track of best position and best evaluation
			if (s.min < min_Val) {
				min_Val = s.min;
				min_pos = s.min_pos;
			}
			
		}
		System.out.println(min_Val);
        }
    }

    // returns the value of the Rosenbrock Function at a given position
    // minimum is 0.0, which occurs at (1.0,...,1.0)
    public static double eval_ros(double[] pos) {
        double sum = 0;
        boolean overflow = false;
        for (int d = 0; d < dimensionality - 1; d++) {
            sum += 100.0 * Math.pow(pos[d + 1] - pos[d] * pos[d], 2.0) + Math.pow(pos[d] - 1.0, 2.0);
        }
        return sum;
    }

    // returns the value of the Ackley Function at a given position
    // minimum is 0.0, which occurs at (0.0,...,0.0)
    public static double eval_ack(double[] pos) {
        double firstSum = 0.0;
        boolean overflow = false;
        for (int i = 0; i < dimensionality; i++) {
            double prev = firstSum;
            firstSum += pos[i] * pos[i];
            if ((pos[i] > 0 && firstSum <= prev))
                overflow = true;
        }

        double secondSum = 0.0;
        for (int i = 0; i < dimensionality; i++) {
            secondSum += Math.cos(2.0 * Math.PI * pos[i]);
        }

        double part1 = Math.exp(-0.2 * Math.sqrt(firstSum / (double) (dimensionality)));
        if (firstSum != 0 && part1 == 0)
            overflow = true;
        double part2 = Math.exp(secondSum / (double) (dimensionality));
        if (secondSum != 0 && part2 == 0)
            overflow = true;
        if (overflow)
            return Double.MAX_VALUE;
        else
            return -20.0 * part1 - part2 + 20.0 + Math.E;
    }

    // returns the value of the Rastrigin Function at a given position
    // minimum is 0.0, which occurs at (0.0,...,0.0)
    public static double eval_ras(double[] pos) {
        double sum = 0.0;
        boolean overflow = false;
        for (int i = 0; i < dimensionality; i++) {
            sum += pos[i] * pos[i] - 10.0 * Math.cos(2.0 * Math.PI * pos[i]) + 10.0;
        }
        return sum;
    }
    // returns the value of the Zakharov Function at a given position
    // minimum is 0.0, which occurs at (0.0,...,0.0)
    public static double eval_zakh(double[] pos) {
        double sum = 0.0;
        double other_term = 0.0;
        for (int i = 0; i < dimensionality; i++) {
            sum += Math.pow(pos[i], 2);
            other_term += 0.5 * (double) (i) * pos[i];
        }
        return sum + Math.pow(other_term, 2) + Math.pow(other_term, 4);
    }
    // returns the value of the Styblinski-Tang Function at a given position
    // minimum is 1174.9797 for dimension=30, which occurs at(2.903534, 2.903534, ..., 2.903534).
    public static double eval_styb(double[] pos) {
        double sum = 0.0;
        for (int i = 0; i < dimensionality; i++) {
            sum += Math.pow(pos[i], 4) - 16 * Math.pow(pos[i], 2) + 5 * pos[i];
        }
        return sum / 2.0;
    }
}