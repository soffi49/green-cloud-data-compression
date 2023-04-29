import { store } from '@store'

/**
 * Method converts date to formatted string
 *
 * @param {Date|number}[date] - date in format Date or unix time stamp
 * @returns formatted string
 */
export const convertTimeToString = (dateToParse: number | Date, isFullDate = true) => {
   const date = typeof dateToParse === 'number' ? new Date(dateToParse * 1000) : dateToParse

   const hour = date.getHours()
   const min = date.getMinutes()

   const hourFormatted = hour < 10 ? '0' + hour : hour
   const minFormatted = min < 10 ? '0' + min : min

   if (isFullDate) {
      const year = date.getFullYear()
      const month = date.getMonth() + 1
      const day = date.getDate()

      return day + '/' + month + '/' + year + ' ' + hourFormatted + ':' + minFormatted
   }
   return hourFormatted + ':' + minFormatted
}

/**
 * Method retrieves current time (in real time) based on the simulation start time
 *
 * @returns unix time stamp
 */
export const getCurrentTime = () => {
   const currentState = store.getState()
   const systemStartTime = currentState.reports.systemStartTime
   const secondsPerHour = currentState.reports.secondsPerHour

   if (systemStartTime !== null && secondsPerHour !== null) {
      const timeDiff = new Date().getTime() - systemStartTime
      const timeMultiplier = 3600 / secondsPerHour
      const realTimeDifference = timeDiff * timeMultiplier
      return systemStartTime + Math.round(realTimeDifference)
   }
   return new Date().getTime()
}

/**
 * Method verifies if a time is within given minutes bound (calculated from the current time)
 *
 * @param {Date}[time] - time to be verified
 * @param {number}[minutes] - number of minutes that specify the bound
 * @returns boolean value
 */
export const isTimeWithinBounds = (time: Date, minutes: number) => {
   return time.getTime() >= new Date(getCurrentTime() - minutes * 60 * 1000).getTime()
}
