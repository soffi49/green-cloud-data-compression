/**
 * Method calculates the average from the given array (optionally, if the array consists of objects, for a given field of those objects)
 *
 * @param {any[]}[arr] - array from which the average is to be calculated
 * @param {string | undefined}[field] - optional field specified if array consists of objects
 * @returns number average
 */
export const getAverage = (arr: any[], field = '') => {
   return arr.length === 0
      ? 0
      : arr.reduce((sum, val2) => sum + (typeof val2 === 'number' ? val2 : val2[field]), 0) / arr.length
}

/**
 * Method calculates the sum from the given array
 *
 * @param {number[]}[arr] - array from which the sum is to be calculated
 * @returns number sum
 */
export const getSum = (arr: any[]) => {
   return arr.reduce((prev, curr) => prev + curr, 0)
}
