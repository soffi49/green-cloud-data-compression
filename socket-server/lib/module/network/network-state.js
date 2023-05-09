"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.resetNetworkState = exports.resetNetworkReportsState = exports.NETWORK_STATE = exports.NETWORK_REPORTS_STATE = void 0;
var NETWORK_STATE = {
  finishedJobsNo: 0,
  failedJobsNo: 0,
  currPlannedJobsNo: 0,
  currActiveJobsNo: 0,
  currClientsNo: 0
};
exports.NETWORK_STATE = NETWORK_STATE;
var NETWORK_REPORTS_STATE = {
  failJobsReport: [],
  finishJobsReport: [],
  clientsReport: []
};
exports.NETWORK_REPORTS_STATE = NETWORK_REPORTS_STATE;
var resetNetworkState = function resetNetworkState() {
  return Object.assign(NETWORK_STATE, {
    finishedJobsNo: 0,
    failedJobsNo: 0,
    currPlannedJobsNo: 0,
    currActiveJobsNo: 0,
    currClientsNo: 0
  });
};
exports.resetNetworkState = resetNetworkState;
var resetNetworkReportsState = function resetNetworkReportsState() {
  return Object.assign(NETWORK_STATE, {
    failJobsReport: [],
    finishJobsReport: [],
    clientsReport: []
  });
};
exports.resetNetworkReportsState = resetNetworkReportsState;
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJuYW1lcyI6WyJORVRXT1JLX1NUQVRFIiwiZmluaXNoZWRKb2JzTm8iLCJmYWlsZWRKb2JzTm8iLCJjdXJyUGxhbm5lZEpvYnNObyIsImN1cnJBY3RpdmVKb2JzTm8iLCJjdXJyQ2xpZW50c05vIiwiZXhwb3J0cyIsIk5FVFdPUktfUkVQT1JUU19TVEFURSIsImZhaWxKb2JzUmVwb3J0IiwiZmluaXNoSm9ic1JlcG9ydCIsImNsaWVudHNSZXBvcnQiLCJyZXNldE5ldHdvcmtTdGF0ZSIsIk9iamVjdCIsImFzc2lnbiIsInJlc2V0TmV0d29ya1JlcG9ydHNTdGF0ZSJdLCJzb3VyY2VzIjpbIi4uLy4uLy4uL3NyYy9tb2R1bGUvbmV0d29yay9uZXR3b3JrLXN0YXRlLnRzIl0sInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7IFJlcG9ydEVudHJ5IH0gZnJvbSBcIi4uLy4uL3R5cGVzL3JlcG9ydC1lbnRyeS10eXBlXCJcclxuXHJcbmludGVyZmFjZSBOZXR3b3JrU3RhdGUge1xyXG4gIGZpbmlzaGVkSm9ic05vOiBudW1iZXJcclxuICBmYWlsZWRKb2JzTm86IG51bWJlclxyXG4gIGN1cnJQbGFubmVkSm9ic05vOiBudW1iZXJcclxuICBjdXJyQWN0aXZlSm9ic05vOiBudW1iZXJcclxuICBjdXJyQ2xpZW50c05vOiBudW1iZXJcclxufVxyXG5cclxuaW50ZXJmYWNlIE5ldHdvcmtSZXBvcnRzU3RhdGUge1xyXG4gIGZhaWxKb2JzUmVwb3J0OiBSZXBvcnRFbnRyeVtdLFxyXG4gIGZpbmlzaEpvYnNSZXBvcnQ6IFJlcG9ydEVudHJ5W10sXHJcbiAgY2xpZW50c1JlcG9ydDogUmVwb3J0RW50cnlbXSxcclxufVxyXG5cclxubGV0IE5FVFdPUktfU1RBVEU6IE5ldHdvcmtTdGF0ZSA9IHtcclxuICBmaW5pc2hlZEpvYnNObzogMCxcclxuICBmYWlsZWRKb2JzTm86IDAsXHJcbiAgY3VyclBsYW5uZWRKb2JzTm86IDAsXHJcbiAgY3VyckFjdGl2ZUpvYnNObzogMCxcclxuICBjdXJyQ2xpZW50c05vOiAwXHJcbn1cclxuXHJcbmxldCBORVRXT1JLX1JFUE9SVFNfU1RBVEU6IE5ldHdvcmtSZXBvcnRzU3RhdGUgPSB7XHJcbiAgZmFpbEpvYnNSZXBvcnQ6IFtdLFxyXG4gIGZpbmlzaEpvYnNSZXBvcnQ6IFtdLFxyXG4gIGNsaWVudHNSZXBvcnQ6IFtdXHJcbn1cclxuXHJcbmNvbnN0IHJlc2V0TmV0d29ya1N0YXRlID0gKCkgPT5cclxuICBPYmplY3QuYXNzaWduKE5FVFdPUktfU1RBVEUsXHJcbiAgICAoe1xyXG4gICAgICBmaW5pc2hlZEpvYnNObzogMCxcclxuICAgICAgZmFpbGVkSm9ic05vOiAwLFxyXG4gICAgICBjdXJyUGxhbm5lZEpvYnNObzogMCxcclxuICAgICAgY3VyckFjdGl2ZUpvYnNObzogMCxcclxuICAgICAgY3VyckNsaWVudHNObzogMFxyXG4gICAgfSkpXHJcblxyXG5jb25zdCByZXNldE5ldHdvcmtSZXBvcnRzU3RhdGUgPSAoKSA9PlxyXG4gIE9iamVjdC5hc3NpZ24oTkVUV09SS19TVEFURSxcclxuICAgICh7XHJcbiAgICAgIGZhaWxKb2JzUmVwb3J0OiBbXSxcclxuICAgICAgZmluaXNoSm9ic1JlcG9ydDogW10sXHJcbiAgICAgIGNsaWVudHNSZXBvcnQ6IFtdXHJcbiAgICB9KSlcclxuXHJcbmV4cG9ydCB7XHJcbiAgTkVUV09SS19TVEFURSxcclxuICBORVRXT1JLX1JFUE9SVFNfU1RBVEUsXHJcbiAgcmVzZXROZXR3b3JrU3RhdGUsXHJcbiAgcmVzZXROZXR3b3JrUmVwb3J0c1N0YXRlXHJcbn0iXSwibWFwcGluZ3MiOiI7Ozs7OztBQWdCQSxJQUFJQSxhQUEyQixHQUFHO0VBQ2hDQyxjQUFjLEVBQUUsQ0FBQztFQUNqQkMsWUFBWSxFQUFFLENBQUM7RUFDZkMsaUJBQWlCLEVBQUUsQ0FBQztFQUNwQkMsZ0JBQWdCLEVBQUUsQ0FBQztFQUNuQkMsYUFBYSxFQUFFO0FBQ2pCLENBQUM7QUFBQUMsT0FBQSxDQUFBTixhQUFBLEdBQUFBLGFBQUE7QUFFRCxJQUFJTyxxQkFBMEMsR0FBRztFQUMvQ0MsY0FBYyxFQUFFLEVBQUU7RUFDbEJDLGdCQUFnQixFQUFFLEVBQUU7RUFDcEJDLGFBQWEsRUFBRTtBQUNqQixDQUFDO0FBQUFKLE9BQUEsQ0FBQUMscUJBQUEsR0FBQUEscUJBQUE7QUFFRCxJQUFNSSxpQkFBaUIsR0FBRyxTQUFwQkEsaUJBQWlCQSxDQUFBO0VBQUEsT0FDckJDLE1BQU0sQ0FBQ0MsTUFBTSxDQUFDYixhQUFhLEVBQ3hCO0lBQ0NDLGNBQWMsRUFBRSxDQUFDO0lBQ2pCQyxZQUFZLEVBQUUsQ0FBQztJQUNmQyxpQkFBaUIsRUFBRSxDQUFDO0lBQ3BCQyxnQkFBZ0IsRUFBRSxDQUFDO0lBQ25CQyxhQUFhLEVBQUU7RUFDakIsQ0FBRSxDQUFDO0FBQUE7QUFBQUMsT0FBQSxDQUFBSyxpQkFBQSxHQUFBQSxpQkFBQTtBQUVQLElBQU1HLHdCQUF3QixHQUFHLFNBQTNCQSx3QkFBd0JBLENBQUE7RUFBQSxPQUM1QkYsTUFBTSxDQUFDQyxNQUFNLENBQUNiLGFBQWEsRUFDeEI7SUFDQ1EsY0FBYyxFQUFFLEVBQUU7SUFDbEJDLGdCQUFnQixFQUFFLEVBQUU7SUFDcEJDLGFBQWEsRUFBRTtFQUNqQixDQUFFLENBQUM7QUFBQTtBQUFBSixPQUFBLENBQUFRLHdCQUFBLEdBQUFBLHdCQUFBIn0=