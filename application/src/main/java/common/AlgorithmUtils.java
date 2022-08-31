package common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import domain.job.Job;
import domain.job.PowerJob;

/**
 * Service used to perform operations using already existing, implemented algorithms
 */
public class AlgorithmUtils {

	/**
	 * Method retrieves from the list of jobs, the ones which summed power will be the closest to the finalPower
	 *
	 * @param jobs       list of jobs to go through
	 * @param finalPower power bound
	 * @param type       type of the list objects
	 * @return list of power jobs
	 */
	public static <T> List<T> findJobsWithinPower(final List<?> jobs, final double finalPower, final Class<T> type) {
		if (finalPower == 0) {
			return Collections.emptyList();
		}

		final AtomicReference<SubJobList> result = new AtomicReference<>(new SubJobList());

		final Set<SubJobList> sums = new HashSet<>();
		sums.add(result.get());

		jobs.forEach(job -> {
			final Set<SubJobList> newSums = new HashSet<>();
			sums.forEach(sum -> {
				final List<T> newSubList = new ArrayList<>((Collection<? extends T>) sum.subList);
				newSubList.add((T) job);
				final int power = type.equals(PowerJob.class) ? ((PowerJob) job).getPower() : ((Job) job).getPower();
				final SubJobList newSum = new SubJobList(sum.size + power, newSubList);

				if (newSum.size <= finalPower) {
					newSums.add(newSum);
					if (newSum.size > result.get().size) {
						result.set(newSum);
					}
				}
			});
			sums.addAll(newSums);
		});
		return (List<T>) result.get().subList;
	}

	/**
	 * Inner class which represents a job list used in finding set of jobs withing given power method
	 */
	public static class SubJobList {
		public int size;
		public List<?> subList;

		public SubJobList() {
			this(0, new ArrayList<>());
		}

		public SubJobList(int size, List<?> subList) {
			this.size = size;
			this.subList = subList;
		}
	}

}
