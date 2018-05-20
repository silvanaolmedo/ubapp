package ar.edu.ub.ubapplication.domain.factory;

/**
 * Created by Silvana Olmedo on 20/05/2018.
 */

public interface Factory<I, O> {

    O create(I input);
}
