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
    public static void main(String[] args) {
//        if (args.length != 5) {
//            System.out.println("Usage: java Main topology swarm_size iterations function dimensionality");
//        }
//        String topology = args[0];
//        int swarm_size = Integer.parseInt(args[1]);
//        int iterations = Integer.parseInt(args[2]);
//        String function = args[3];
//        int dimensionality = Integer.parseInt(args[4]);

        
    	String topology = "ra";
    	String function = "ras";
    	int size = 49;
    	int dimensionality = 30;
    	double ga_cross_prob = 0.5;
    	double ga_mut_prob = 0.05;
    	
    	
    	Swarm s = new Swarm(topology, size, function, dimensionality);
    	GA pop = new GA(dimensionality, size, function, ga_cross_prob, ga_mut_prob);

        
    }
}