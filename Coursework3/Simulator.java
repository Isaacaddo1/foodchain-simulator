import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Simulator {
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a snake will be created in any given grid position.
    private static final double SNAKE_CREATION_PROBABILITY = 0.02;
    // The probability that a rat will be created in any given grid position.
    private static final double RAT_CREATION_PROBABILITY = 0.08;
    // The probability that an owl will be created in any given grid position.
    private static final double OWL_CREATION_PROBABILITY = 0.14;
    // The probability that a lion will be created in any given grid position.
    private static final double LION_CREATION_PROBABILITY = 0.2;
    // The probability that a zebra will be created in any given grid position.
    private static final double ZEBRA_CREATION_PROBABILITY = 0.26;

    // List of animals in the field.
    private final List<Animal> animals;
    // The current state of the field.
    private final Field field;
    // A graphical view of the simulation.
    private final SimulatorView view;
    // The current step of the simulation.
    private int step;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator() {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     *
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) {
        if (width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        animals = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Rat.class, Color.ORANGE);
        view.setColor(Snake.class, Color.BLUE);
        view.setColor(Zebra.class, Color.GREEN);
        view.setColor(Lion.class, Color.RED);
        view.setColor(Owl.class, Color.PINK);

        // Setup a valid starting point.
        reset();
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        step = 0;
        animals.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(step, field);
    }

    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate() {
        Random rand = Randomizer.getRandom();
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                if (rand.nextDouble() <= SNAKE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Snake snake = new Snake(true, field, location);
                    animals.add(snake);
                } else if (rand.nextDouble() <= RAT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Rat rat = new Rat(true, field, location);
                    animals.add(rat);
                } else if (rand.nextDouble() <= ZEBRA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Zebra zebra = new Zebra(true, field, location);
                    animals.add(zebra);
                } else if (rand.nextDouble() <= OWL_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Owl owl = new Owl(true, field, location);
                    animals.add(owl);
                } else if (rand.nextDouble() <= LION_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Lion lion = new Lion(true, field, location);
                    animals.add(lion);
                }
                // else leave the location empty.
            }
        }
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation() {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     *
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps) {
        for (int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            delay();   // uncomment this to run more slowly

        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep() {
        step++;
        field.timeSkip();
        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();
        // Let all rabbits act.
        for (Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals);
            if (!animal.isAlive()) {
                it.remove();
            }
        }

        // Add the newly born foxes and rabbits to the main lists.
        animals.addAll(newAnimals);

        view.showStatus(step, field);
    }

    /**
     * Pause for a given time.
     */
    private void delay() {
        try {
            Thread.sleep(60);
        } catch (InterruptedException ie) {
            // wake up
        }
    }
}
