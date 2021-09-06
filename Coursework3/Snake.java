import java.util.List;
import java.util.Random;

/**
 * A simple model of a fox.
 * Foxes age, move, eat rabbits, and die.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Snake extends Animal {
    // Characteristics shared by all foxes (class variables).

    // The age at which a Snake can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a Snake can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a Snake breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single rat. In effect, this is the
    // number of steps a Snake can go before it has to eat again.
    private static final int SNAKE_FOOD_VALUE = 9; 
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The Snake's gender
    private final boolean gender;
    // Individual characteristics (instance fields).
    // The Snake's age.
    private int age;
    // The Snake's food level, which is increased by eating rabbits.
    private int foodLevel;

    /**
     * Create a fox. A Snake can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field     The field currently occupied.
     * @param location  The location within the field.
     */
    public Snake(boolean randomAge, Field field, Location location) {
        super(field, location);
        gender = rand.nextBoolean();
        if (randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(SNAKE_FOOD_VALUE);
        } else {
            age = 0;
            foodLevel = SNAKE_FOOD_VALUE;
        }
    }

    /**
     * This is what the snake does most of the time: it hunts for
     * rats. In the process, it might breed, die of hunger,
     * or die of old age.
     *
     * @param newSnakes A list to return newly born snakes.
     */
    public void act(List<Animal> newSnakes) {
        incrementAge();
        incrementHunger();
        if (isAlive()) {
            giveBirth(newSnakes);
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if (newLocation == null) {
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if (newLocation != null) {
                setLocation(newLocation);
            } else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age. This could result in the Snake's death.
     */
    private void incrementAge() {
        age++;
        if (age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this Snake more hungry. This could result in the Snake's death.
     */
    private void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Check whether or not this Snake is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param newSnakes list to return newly born Snake.
     */
    private void giveBirth(List<Animal> newSnakes) {
        // New snakes are born into adjacent locations.
        // Get a list of adjacent free locations.
        List<Location> adjacent = getField().adjacentLocations(getLocation());
        Field field = getField();
        int births = 0;
        for (Location space : adjacent) {
            Object animal = field.getObjectAt(space);
            if (field.getObjectAt(space) instanceof Snake) {
                Snake snake = (Snake) animal;
                if (gender ^ snake.getGender() && canBreed()) {
                    births = breed();
                }
                break;
            }
            field.getObjectAt(space);
        }

        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Snake young = new Snake(false, field, loc);
            newSnakes.add(young);
        }
    }

    /**
     * Look for snake adjacent to the current location.
     * Only the first live rat is eaten.
     *
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for (Location where : adjacent) {
            Object animal = field.getObjectAt(where);
            if (animal instanceof Snake) {
                Snake snake = (Snake) animal;
                if (snake.isAlive()) {
                    snake.setDead();
                    foodLevel = SNAKE_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * A snake can breed if it has reached the breeding age.
     */
    private boolean canBreed() {
        return age >= BREEDING_AGE;
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     *
     * @return The number of births (may be zero).
     */
    private int breed() {
        int births = 0;
        if (canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }
}
