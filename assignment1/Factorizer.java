import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class Factorizer {
	static final int NUM_THREADS = 2;
	static final int NUM_COUNTERS = 32;
	static final int NUM_WORK = 50_000_000;

	public static class Counter {
		public volatile long value = 0;
	}

	public static class SharedState {
		int[] workunits;
		Counter[] counters;

		public SharedState(int n) {
			this.workunits = new int[n];
			this.counters = new Counter[NUM_COUNTERS];
			for (int i = 0; i < counters.length; ++i) {
				counters[i] = new Counter();
			}
		}

		public void incrementCounter(int threadId) {
			counters[threadId % counters.length].value++;
		}

		public long sumOfCounters() {
			long sumOfCounters = 0;
			for (int i = 0; i < counters.length; ++i) {
				sumOfCounters += counters[i].value;
			}
			return sumOfCounters;
		}

		public void initializeBalancedWorkload() {
			for (int i = 0; i < workunits.length; ++i) {
				workunits[i] = 10_000;
			}
		}
	}

	public static void countFactors(SharedState state, int workId, int threadId) {
		int composite = state.workunits[workId];
		while (composite > 1) {
			for (int i = 2; i <= composite; ++i) {
				if (composite % i == 0) {
					composite = composite / i;
					state.incrementCounter(threadId);
					break;
				}
			}
		}
	}

	public static void main(String args[]) {
		Factorizer factorizer = new Factorizer();
		System.out.println("=== SEQ: Sequential reference implementation ===");
		factorizer.sequentialFactors();
		// TODO: Run more experiments here.
	}

	// --- SEQ: Sequential reference implementation ----------------------------

	public void sequentialFactors(){
		SharedState state = new SharedState(NUM_WORK);
		state.initializeBalancedWorkload();

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < NUM_WORK; ++i) {
			countFactors(state, i, 0);
		}
		long result = state.sumOfCounters();
		long endTime = System.currentTimeMillis();

		System.out.printf("Total time for computation: %dms\n", (endTime - startTime));
		System.out.printf("Total number of factors: %d\n", result);
	}

	// TODO: Add more experiments here.
}
