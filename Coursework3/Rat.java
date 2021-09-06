import java.util.List;
import java.util.Random;

/**
 * A simple model of a rat.
 * Rats age, move, breed, and die.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Rat extends Animal {
    // Characteristics shared by all rabbits (class variables).

    // The age at which a rat can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a rat can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a rat breeding.
    private static final double BREEDING_PROBABILITY = 0.12;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The rat's gender
    private final boolean gender;
    // The rat's age.
    private int age;

    /**
     * Create a new rat. A rabbit may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the rabbit will have a random age.
     * @param field     The field currently occupied.
     * @param location  The location within the field.
     */
    public Rat(boolean randomAge, Field field, Location location) {
        super(field, location);
        age = 0;
        gender = rand.nextBoolean();
        if (randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }

    /**
     * This is what the rat does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     *
     * @param newRats A list to return newly born rabbits.
     */
    public void act(List<Animal> newRats) {
        incrementAge();
        if (isAlive()) {
            giveBirth(newRats);
            // Try to move into a free location.
            Field currentField = getField();
            Location newLocation = getLocation();
            if (currentField.getTime().equals("night")) {
                currentField.freeAdjacentLocation(getLocation());
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
     * This could result in the rat's death.
     */
    private void incrementAge() {
        age++;
        if (age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this rat is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param newRats A list to return newly born rat,
     */
    private void giveBirth(List<Animal> newRats) {
        // New rats are born into adjacent locations.
        // Get a list of adjacent free locations.
        List<Location> adjacent = getField().adjacentLocations(getLocation());
        Field field = getField();
        int births = 0;
        for (Location space : adjacent) {
            Object animal = field.getObjectAt(space);
            if (field.getObjectAt(space) instanceof Rat) {
                Rat rat = (Rat) animal;
                if (gender ^ rat.getGender() && canBreed()) {
                    births = breed();
                }
                break;
            }
            field.getObjectAt(space);
        }

        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Rat young = new Rat(false, field, loc);
            newRats.add(young);
        }
    }

    /**
     * A rat can breed if it has reached the breeding age.
     *
     * @return true if the rat can breed, false otherwise.
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
