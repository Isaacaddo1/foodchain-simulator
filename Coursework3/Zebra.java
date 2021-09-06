import java.util.List;
import java.util.Random;

/**
 * A simple model of a Zebra.
 * Zebras age, move, breed, and die.
 *
 * @author (Isaac Addo)
 * @version 26.02.2021
 */
public class Zebra extends Animal {
    // Characteristics shared by all zebras (class variables).

    // The age at which a zebras can start to breed.
    private static final int BREEDING_AGE = 6;
    // The age to which a zebras can live.
    private static final int MAX_AGE = 48;
    // The likelihood of a zebras breeding.
    private static final double BREEDING_PROBABILITY = 0.075;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The zebra's gender
    private final boolean gender;
    // The zebra's age.
    private int age;

    /**
     * Create a new zebra. A zebra may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the zebra will have a random age.
     * @param field     The field currently occupied.
     * @param location  The location within the field.
     */
    public Zebra(boolean randomAge, Field field, Location location) {
        super(field, location);
        age = 0;
        gender = rand.nextBoolean();
        if (randomAge) {
            age = rand.nextInt(MAX_AGE);
        }

    }

    /**
     * This is what the zebra does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     *
     * @param newZebras A list to return newly born rabbits.
     */
    public void act(List<Animal> newZebras) {
        incrementAge();
        if (isAlive()) {
            giveBirth(newZebras);
            // Try to move into a free location.
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if (getField().getTime().equals("night")) {
                newLocation = getLocation();
            }
            if (newLocation != null) {
                setLocation(newLocation);
            } else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the zebra's death.
     */
    private void incrementAge() {
        age++;
        if (age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this zebra is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param newZebras A list to return newly born zebras.
     */
    private void giveBirth(List<Animal> newZebras) {
        // New zebras are born into adjacent locations.
        // Get a list of adjacent free locations.
        List<Location> adjacent = getField().adjacentLocations(getLocation());
        Field field = getField();
        int births = 0;
        for (Location space : adjacent) {
            Object animal = field.getObjectAt(space);
            if (field.getObjectAt(space) instanceof Zebra) {
                Zebra zebra = (Zebra) animal;
                if (gender ^ zebra.getGender() && canBreed()) {
                    births = breed();
                }
                break;
            }
            field.getObjectAt(space);
        }

        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Zebra young = new Zebra(false, field, loc);
            newZebras.add(young);
        }
    }

    /**
     * A rabbit can breed if it has reached the breeding age.
     *
     * @return true if the rabbit can breed, false otherwise.
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
