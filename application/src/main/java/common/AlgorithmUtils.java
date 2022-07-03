package common;

import domain.job.PowerJob;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Service used to perform operations using already existing, implemented algorithms
 */
public class AlgorithmUtils {

    /**
     * Method retrieves from the list of jobs, the ones which summed power will be the closest to the finalPower
     *
     * @param powerJobs  list of jobs to go through
     * @param finalPower power bound
     * @return list of power jobs
     */
    public static List<PowerJob> findJobsWithinPower(final List<PowerJob> powerJobs, final double finalPower) {
        if (finalPower == 0) {
            return Collections.emptyList();
        }

        final AtomicReference<SubJobList> result = new AtomicReference<>(new SubJobList());

        final Set<SubJobList> sums = new HashSet<>();
        sums.add(result.get());

        powerJobs.forEach(powerJob -> {
            final Set<SubJobList> newSums = new HashSet<>();

            sums.forEach(sum -> {
                final List<PowerJob> newSubList = new ArrayList<>(sum.subList);
                newSubList.add(powerJob);
                final SubJobList newSum = new SubJobList(sum.size + powerJob.getPower(), newSubList);

                if (newSum.size <= finalPower) {
                    newSums.add(newSum);
                    if (newSum.size > result.get().size) {
                        result.set(newSum);
                    }
                }
            });
            sums.addAll(newSums);
        });
        return result.get().subList;
    }

    /**
     * Inner class which represents a job list used in finding set of jobs withing given power method
     */
    public static class SubJobList {
        public int size;
        public List<PowerJob> subList;

        public SubJobList() {
            this(0, new ArrayList<>());
        }

        public SubJobList(int size, List<PowerJob> subList) {
            this.size = size;
            this.subList = subList;
        }
    }

}
