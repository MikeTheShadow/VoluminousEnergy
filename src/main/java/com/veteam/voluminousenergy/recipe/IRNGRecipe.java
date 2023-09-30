package com.veteam.voluminousenergy.recipe;

/**
 * This interface should be implemented by any recipe that uses rng.
 * The way it works is that any output ITEMS will be subject to this interfaces float[] array.
 * Even items that have a 100% drop rate should have their own float[x] = 1
 * This way we don't have to guess or use funky offsets and instead can just figure
 * out in the other logic if RNG is required
 * Example: 1 item is guaranteed and 1 has a 50% drop rate
 * The float[] array should be 2 floats. The first 1 and the second 0.5
 * This way they can all be treated the same when iterating over the results later
 *
 */
public interface IRNGRecipe {

    float[] getRNGOutputs();

    void setRNGOutputs(float[] rngOutputs);

}
