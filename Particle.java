import java.util.Random;

/**
 * Particle
 *
 * Representation of a particle in a swarm
 * 
 * @author Mustafa Aydogdu
 * @author Griffin Ott
 * @author Evan Phillips
 */
public class Particle {
    private double[] pbest;
    private double[] gbest;
    public double[] velocity;
    public double[] position;
    public Particle[] neighbors;
    private static double CONSTRICTION_FACTOR = 0.7298;
    private static double PHI = 2.05;
    private double best_val = Double.MAX_VALUE;

    /**
     * Initializes an instance of a Particle object
     *
     * @param position the position of a given particle in d dimensions
     * @param velocity the velocity of a given particle in d dimensions
     */

    public Particle(double[] position, double[] velocity) {
        int d = position.length;
        this.pbest = new double[d];
        this.gbest = new double[d];
        this.velocity = new double[d];
        this.position = new double[d];
        for (int i = 0; i < position.length; i++) {
            this.pbest[i] = position[i];
            this.gbest[i] = position[i];
            this.position[i] = position[i];
        }
        for (int i = 0; i < velocity.length; i++) {
            this.velocity[i] = velocity[i];
        }
        set_pbest();
    }

    public void set_neighbors(Particle[] neighbors) {
        this.neighbors = neighbors;
    }

    public double getBestVal() {
        return best_val;
    }

    public double[] getPbest() {
        return pbest;
    }

    /**
     * Sets the personal for a given particle with the assumption that we are
     * searching for a minimum
     */
    public void set_pbest() {
        if (Swarm.function.equals("ros")) {
            if (Main.eval_ros(position) < this.best_val) {
                for (int i = 0; i < Swarm.dim; i++)
                    pbest[i] = position[i];
                best_val = Main.eval_ros(position);
            }
        }

        else if (Swarm.function.equals("ack")) {
            if (Main.eval_ack(position) < this.best_val) {
                for (int i = 0; i < Swarm.dim; i++)
                    pbest[i] = position[i];
                best_val = Main.eval_ack(position);
            }
        }

        else if (Swarm.function.equals("ras")) {
            if (Main.eval_ras(position) < this.best_val) {
                for (int i = 0; i < Swarm.dim; i++)
                    pbest[i] = position[i];
                best_val = Main.eval_ras(position);
            }
        }

        else if (Swarm.function.equals("zakh")) {
            if (Main.eval_zakh(position) < this.best_val) {
                for (int i = 0; i < Swarm.dim; i++)
                    pbest[i] = position[i];
                best_val = Main.eval_zakh(position);
            }
        }

        else {
            if (Main.eval_styb(position) < this.best_val) {
                for (int i = 0; i < Swarm.dim; i++)
                    pbest[i] = position[i];
                best_val = Main.eval_styb(position);
            }
        }

    }

    public void set_gbest_global(double[] gbest) {
        this.gbest = gbest;
    }

    /**
     * Sets the group best for a given neighborhood with the assumption that we are
     * searching for a minimum
     */
    public void set_gbest() {
        if (Swarm.function.equals("ros")) {
            for (Particle particle : this.neighbors) {
                if (Main.eval_ros(particle.pbest) < Main.eval_ros(gbest)) {
                    for (int i = 0; i < Swarm.dim; i++)
                        gbest[i] = particle.pbest[i];
                }
            }
        } else if (Swarm.function.equals("ack")) {
            for (Particle particle : this.neighbors) {
                if (Main.eval_ack(particle.pbest) < Main.eval_ack(gbest)) {
                    for (int i = 0; i < Swarm.dim; i++)
                        gbest[i] = particle.pbest[i];
                }
            }
        } else if (Swarm.function.equals("ras")) {
            for (Particle particle : this.neighbors) {
                if (Main.eval_ras(particle.pbest) < Main.eval_ras(gbest)) {
                    for (int i = 0; i < Swarm.dim; i++)
                        gbest[i] = particle.pbest[i];
                }
            }
        }

        else if (Swarm.function.equals("zakh")) {
            for (Particle particle : this.neighbors) {
                if (Main.eval_zakh(particle.pbest) < Main.eval_zakh(gbest)) {
                    for (int i = 0; i < Swarm.dim; i++)
                        gbest[i] = particle.pbest[i];
                }
            }
        }

        else {
            for (Particle particle : this.neighbors) {
                if (Main.eval_styb(particle.pbest) < Main.eval_styb(gbest)) {
                    for (int i = 0; i < Swarm.dim; i++)
                        gbest[i] = particle.pbest[i];
                }
            }
        }

    }

    /**
     * updates the velocity and the position of a given particle
     */
    public void update_velocity_position() {

        Random rand = new Random();

        // generate u1 and u2
        double[] u1 = new double[Swarm.dim];
        double[] u2 = new double[Swarm.dim];
        for (int i = 0; i < Swarm.dim; i++) {
            double entry = rand.nextDouble() * PHI;
            u1[i] = entry;
            entry = rand.nextDouble() * PHI;
            u2[i] = entry;
        }

        // get the term for pbest influence
        double[] pbest_influence = new double[Swarm.dim];
        for (int i = 0; i < Swarm.dim; i++) {
            pbest_influence[i] = u1[i] * (pbest[i] - position[i]);
        }

        // get the term for gbest influence
        double[] gbest_influence = new double[Swarm.dim];
        for (int i = 0; i < Swarm.dim; i++) {
            gbest_influence[i] = u2[i] * (gbest[i] - position[i]);
        }

        // update velocity
        for (int i = 0; i < Swarm.dim; i++) {
            velocity[i] = CONSTRICTION_FACTOR * (velocity[i] + pbest_influence[i] + gbest_influence[i]);
        }

        // update position
        for (int i = 0; i < Swarm.dim; i++) {
            position[i] = position[i] + velocity[i];
        }

    }

}
