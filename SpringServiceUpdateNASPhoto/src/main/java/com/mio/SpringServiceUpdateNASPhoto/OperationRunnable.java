package com.mio.SpringServiceUpdateNASPhoto;

public interface OperationRunnable<T> extends Runnable {

	public boolean isDone();

	public T getResult();

}
