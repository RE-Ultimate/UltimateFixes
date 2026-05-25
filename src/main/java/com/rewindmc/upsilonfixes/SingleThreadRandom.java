package com.rewindmc.upsilonfixes;

import java.util.Random;

public class SingleThreadRandom extends Random {

	private final Thread owner;
	
	public SingleThreadRandom() {
		this.owner = Thread.currentThread();
	}
	
	private void check() {
		if (owner == null) return;
		if (Thread.currentThread() != owner) {
			UpsilonFixesPremain.log.error("Random is being unsafely used on the wrong thread (owner: {}, user: {})",
					owner.getName(), Thread.currentThread().getName(),
					new Throwable("Stack trace").fillInStackTrace());
		}
	}

	@Override
	public void setSeed(long seed) {
		check();
		super.setSeed(seed);
	}

	@Override
	public void nextBytes(byte[] bytes) {
		check();
		super.nextBytes(bytes);
	}

	@Override
	public int nextInt() {
		check();
		return super.nextInt();
	}

	@Override
	public int nextInt(int bound) {
		check();
		return super.nextInt(bound);
	}

	@Override
	public long nextLong() {
		check();
		return super.nextLong();
	}

	@Override
	public boolean nextBoolean() {
		check();
		return super.nextBoolean();
	}

	@Override
	public float nextFloat() {
		check();
		return super.nextFloat();
	}

	@Override
	public double nextDouble() {
		check();
		return super.nextDouble();
	}

	@Override
	public double nextGaussian() {
		check();
		return super.nextGaussian();
	}
	
}
