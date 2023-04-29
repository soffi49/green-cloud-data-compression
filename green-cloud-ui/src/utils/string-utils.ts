/**
 * String formatter that parse the information about job part
 *
 * @param {string}[jobId] - job part identifier
 * @returns formatted string
 */
export const parseSplitJobId = (jobId: string) => {
   const originalJob = retrieveOriginalJobId(jobId)
   const splitId = jobId.split('#part')[1]

   return `${originalJob} (PART ${splitId})`
}

/**
 * Method retrieves information about original job id
 *
 * @param {string}[jobId] - job part identifier
 * @returns original job id
 */
export const retrieveOriginalJobId = (jobId: string) => {
   return jobId.split('#')[0]
}
