package org.saltations.endeavour;

sealed interface Success<T> extends Result<T> permits Value, NoValue {}

