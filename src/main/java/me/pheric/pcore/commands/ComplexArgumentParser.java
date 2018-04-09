package me.pheric.pcore.commands;

public abstract class ComplexArgumentParser {
    /**
     * Getter for the number of arguments {@link ComplexArgumentParser#parseInput(String...)} needs
     *
     * @return Number of required arguments
     */
    abstract public int getRequiredArguments();

    /**
     * Parse input and return
     *
     * @param args String arguments supplied by the command sender. Guaranteed to be exactly the required number.
     * @return The type the strings will be parsed to or null if they could not be parsed.
     */
    abstract public Object parseInput(String... args);
}