/**
 * Method computes the time in minutes in which the job had a given status
 *
 * @param {string}key job status
 * @param {number}val time received from the job duration map
 *
 * @returns time in min
 */
const getJobStatusTimeInMin = (key: string, val: number) => {
   if (['PROCESSED', 'CREATED', 'SCHEDULED'].includes(key)) {
      return parseFloat((val / 60000).toFixed(2))
   }
   return val
}

/**
 * Method format the time in which the job had a given status with respect to the number of hours or minutes
 *
 * @param {string}key job status
 * @param {number}val time received from the job duration map
 *
 * @returns time in min
 */
const getJobStatusDuration = (key: string, val: number) => {
   if (['PROCESSED', 'CREATED', 'SCHEDULED'].includes(key)) {
      const minutes = Math.floor(val / 60000)
      const seconds = parseInt(((val % 60000) / 1000).toFixed(0))

      return minutes > 0 ? `${minutes} MINUTES ${seconds} SECONDS` : `${seconds} SECONDS`
   }
   const hours = Math.floor(val / 60)
   const minutes = parseInt((val % 60).toFixed(0))
   const minReminderFixed = minutes === 60 ? 0 : minutes

   return hours > 0 ? `${hours} HOURS ${minReminderFixed} MINUTES` : `${minReminderFixed} MINUTES`
}

export { getJobStatusTimeInMin, getJobStatusDuration }
