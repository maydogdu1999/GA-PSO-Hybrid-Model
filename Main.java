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
	static int runGA = 100; // 100
	static int runPSO = 5;
	static String topology = "vn";
	static String function = "styb";
	static int size = 49;
	static int dimensionality = 30;
	static double ga_cross_prob = 0.5; // 0.5
	static double ga_mut_prob = 0.5; // 0.5
	static int iterations = 1000;
	static double shift = 0.5; // 0.5

	public static void main(String[] args) {

		Swarm s = new Swarm(topology, size, function, dimensionality);
		Particle[] init_swarm = Swarm.generate_swarm(function, topology);
		s.swarm = init_swarm;
		ArrayList<Individual> curPop = s.execute(runPSO);
		GA population = new GA(dimensionality, size, function, ga_cross_prob, ga_mut_prob, shift);
		double[][] curPositions;
		System.out.println("orig " + s.min);

		for (int i = 0; i < iterations; i++) {

			curPositions = population.execute(runGA, "uc", "bs", curPop);

			Particle[] new_pos = new Particle[size];
			for (int j = 0; j < size; j++) {
				for (int z = 0; z < dimensionality; z++) {
					s.getParticle(j).position[z] = curPositions[j][z];
				}
			}
			curPop = s.execute(runPSO);
		}

		System.out.println("best " + s.min);
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

	public static double eval_zakh(double[] pos) {
		double sum = 0.0;
		double other_term = 0.0;
		for (int i = 0; i < dimensionality; i++) {
			sum += Math.pow(pos[i], 2);
			other_term += 0.5 * (double) (i) * pos[i];
		}
		return sum + Math.pow(other_term, 2) + Math.pow(other_term, 4);
	}

	public static double eval_styb(double[] pos) {
		double sum = 0.0;
		for (int i = 0; i < dimensionality; i++) {
			sum += Math.pow(pos[i], 4) - 16 * Math.pow(pos[i], 2) + 5 * pos[i];
		}
		return sum / 2.0;
	}
}