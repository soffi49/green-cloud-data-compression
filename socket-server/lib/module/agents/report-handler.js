"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.updateAgentsReportsState = exports.changeCloudNetworkCapacityEvent = void 0;
var _index = require("../../constants/index.js");
var _index2 = require("../../utils/index.js");
var _agentsState = require("./agents-state.js");
var changeCloudNetworkCapacityEvent = function changeCloudNetworkCapacityEvent(cnaName, serverName, capacity, isAdded) {
  var _AGENTS_REPORTS_STATE;
  var events = (_AGENTS_REPORTS_STATE = _agentsState.AGENTS_REPORTS_STATE.agentsReports.filter(function (agentReport) {
    return agentReport.name === cnaName;
  })[0]) === null || _AGENTS_REPORTS_STATE === void 0 ? void 0 : _AGENTS_REPORTS_STATE.events;
  if (events) {
    var eventName = isAdded ? 'New Server' : 'Server disabled';
    var event = isAdded ? "added to ".concat(cnaName) : "disabled from ".concat(cnaName);
    var eventDescription = "Server ".concat(serverName, " with capacity ").concat(capacity, " was ").concat(event);
    events.push({
      type: 'AGENT_CONNECTION_CHANGE',
      time: (0, _index2.getCurrentTime)(),
      name: eventName,
      description: eventDescription
    });
  }
};
exports.changeCloudNetworkCapacityEvent = changeCloudNetworkCapacityEvent;
var reportSystemTraffic = function reportSystemTraffic(time) {
  var currentState = _agentsState.AGENTS_STATE.agents.filter(function (agent) {
    return agent.type === _index.AGENT_TYPES.CLOUD_NETWORK;
  }).reduce(function (sum, agent) {
    sum.capacity = sum.capacity + agent.maximumCapacity;
    sum.traffic = sum.traffic + agent.maximumCapacity * agent.traffic;
    return sum;
  }, {
    capacity: 0,
    traffic: 0
  });
  var currentTraffic = currentState.capacity === 0 ? 0 : currentState.traffic / currentState.capacity;
  return {
    time: time,
    value: currentTraffic
  };
};
var reportSchedulerData = function reportSchedulerData(agent, time) {
  var reports = _agentsState.AGENTS_REPORTS_STATE.agentsReports.filter(function (agentReport) {
    return agentReport.name === agent.name;
  })[0].reports;
  var queueCapacity = agent.maxQueueSize === 0 ? 0 : agent.scheduledJobs.length;
  reports.deadlinePriorityReport.push({
    time: time,
    value: agent.deadlinePriority
  });
  reports.powerPriorityReport.push({
    time: time,
    value: agent.powerPriority
  });
  reports.queueCapacityReport.push({
    time: time,
    value: queueCapacity
  });
  reports.trafficReport.push(reportSystemTraffic(time));
};
var reportCloudNetworkData = function reportCloudNetworkData(agent, time) {
  var _agent$successRatio;
  var reports = _agentsState.AGENTS_REPORTS_STATE.agentsReports.filter(function (agentReport) {
    return agentReport.name === agent.name;
  })[0].reports;
  reports.clientsReport.push({
    time: time,
    value: agent.totalNumberOfClients
  });
  reports.capacityReport.push({
    time: time,
    value: agent.maximumCapacity
  });
  reports.trafficReport.push({
    time: time,
    value: agent.traffic
  });
  reports.successRatioReport.push({
    time: time,
    value: (_agent$successRatio = agent.successRatio) !== null && _agent$successRatio !== void 0 ? _agent$successRatio : 0
  });
};
var reportServerData = function reportServerData(agent, time) {
  var _agent$successRatio2;
  var reports = _agentsState.AGENTS_REPORTS_STATE.agentsReports.filter(function (agentReport) {
    return agentReport.name === agent.name;
  })[0].reports;
  reports.trafficReport.push({
    time: time,
    value: agent.traffic
  });
  reports.capacityReport.push({
    time: time,
    value: agent.currentMaximumCapacity
  });
  reports.greenPowerUsageReport.push({
    time: time,
    value: agent.traffic
  });
  reports.backUpPowerUsageReport.push({
    time: time,
    value: agent.currentMaximumCapacity * agent.backUpTraffic
  });
  reports.successRatioReport.push({
    time: time,
    value: (_agent$successRatio2 = agent.successRatio) !== null && _agent$successRatio2 !== void 0 ? _agent$successRatio2 : 0
  });
};
var reportGreenSourceData = function reportGreenSourceData(agent, time) {
  var _agent$successRatio3;
  var reports = _agentsState.AGENTS_REPORTS_STATE.agentsReports.filter(function (agentReport) {
    return agentReport.name === agent.name;
  })[0].reports;
  reports.trafficReport.push({
    time: time,
    value: agent.traffic
  });
  reports.availableGreenPowerReport.push({
    time: time,
    value: agent.availableGreenEnergy
  });
  reports.capacityReport.push({
    time: time,
    value: agent.currentMaximumCapacity
  });
  reports.jobsOnGreenPowerReport.push({
    time: time,
    value: agent.numberOfExecutedJobs
  });
  reports.jobsOnHoldReport.push({
    time: time,
    value: agent.numberOfJobsOnHold
  });
  reports.successRatioReport.push({
    time: time,
    value: (_agent$successRatio3 = agent.successRatio) !== null && _agent$successRatio3 !== void 0 ? _agent$successRatio3 : 0
  });
};
var updateAgentsReportsState = function updateAgentsReportsState(time) {
  _agentsState.AGENTS_STATE.agents.forEach(function (agent) {
    if (agent.type === _index.AGENT_TYPES.CLOUD_NETWORK) {
      reportCloudNetworkData(agent, time);
    } else if (agent.type === _index.AGENT_TYPES.SERVER) {
      reportServerData(agent, time);
    } else if (agent.type === _index.AGENT_TYPES.GREEN_ENERGY) {
      reportGreenSourceData(agent, time);
    } else if (agent.type === _index.AGENT_TYPES.SCHEDULER) {
      reportSchedulerData(agent, time);
    }
  });
};
exports.updateAgentsReportsState = updateAgentsReportsState;
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJuYW1lcyI6WyJfaW5kZXgiLCJyZXF1aXJlIiwiX2luZGV4MiIsIl9hZ2VudHNTdGF0ZSIsImNoYW5nZUNsb3VkTmV0d29ya0NhcGFjaXR5RXZlbnQiLCJjbmFOYW1lIiwic2VydmVyTmFtZSIsImNhcGFjaXR5IiwiaXNBZGRlZCIsIl9BR0VOVFNfUkVQT1JUU19TVEFURSIsImV2ZW50cyIsIkFHRU5UU19SRVBPUlRTX1NUQVRFIiwiYWdlbnRzUmVwb3J0cyIsImZpbHRlciIsImFnZW50UmVwb3J0IiwibmFtZSIsImV2ZW50TmFtZSIsImV2ZW50IiwiY29uY2F0IiwiZXZlbnREZXNjcmlwdGlvbiIsInB1c2giLCJ0eXBlIiwidGltZSIsImdldEN1cnJlbnRUaW1lIiwiZGVzY3JpcHRpb24iLCJleHBvcnRzIiwicmVwb3J0U3lzdGVtVHJhZmZpYyIsImN1cnJlbnRTdGF0ZSIsIkFHRU5UU19TVEFURSIsImFnZW50cyIsImFnZW50IiwiQUdFTlRfVFlQRVMiLCJDTE9VRF9ORVRXT1JLIiwicmVkdWNlIiwic3VtIiwibWF4aW11bUNhcGFjaXR5IiwidHJhZmZpYyIsImN1cnJlbnRUcmFmZmljIiwidmFsdWUiLCJyZXBvcnRTY2hlZHVsZXJEYXRhIiwicmVwb3J0cyIsInF1ZXVlQ2FwYWNpdHkiLCJtYXhRdWV1ZVNpemUiLCJzY2hlZHVsZWRKb2JzIiwibGVuZ3RoIiwiZGVhZGxpbmVQcmlvcml0eVJlcG9ydCIsImRlYWRsaW5lUHJpb3JpdHkiLCJwb3dlclByaW9yaXR5UmVwb3J0IiwicG93ZXJQcmlvcml0eSIsInF1ZXVlQ2FwYWNpdHlSZXBvcnQiLCJ0cmFmZmljUmVwb3J0IiwicmVwb3J0Q2xvdWROZXR3b3JrRGF0YSIsIl9hZ2VudCRzdWNjZXNzUmF0aW8iLCJjbGllbnRzUmVwb3J0IiwidG90YWxOdW1iZXJPZkNsaWVudHMiLCJjYXBhY2l0eVJlcG9ydCIsInN1Y2Nlc3NSYXRpb1JlcG9ydCIsInN1Y2Nlc3NSYXRpbyIsInJlcG9ydFNlcnZlckRhdGEiLCJfYWdlbnQkc3VjY2Vzc1JhdGlvMiIsImN1cnJlbnRNYXhpbXVtQ2FwYWNpdHkiLCJncmVlblBvd2VyVXNhZ2VSZXBvcnQiLCJiYWNrVXBQb3dlclVzYWdlUmVwb3J0IiwiYmFja1VwVHJhZmZpYyIsInJlcG9ydEdyZWVuU291cmNlRGF0YSIsIl9hZ2VudCRzdWNjZXNzUmF0aW8zIiwiYXZhaWxhYmxlR3JlZW5Qb3dlclJlcG9ydCIsImF2YWlsYWJsZUdyZWVuRW5lcmd5Iiwiam9ic09uR3JlZW5Qb3dlclJlcG9ydCIsIm51bWJlck9mRXhlY3V0ZWRKb2JzIiwiam9ic09uSG9sZFJlcG9ydCIsIm51bWJlck9mSm9ic09uSG9sZCIsInVwZGF0ZUFnZW50c1JlcG9ydHNTdGF0ZSIsImZvckVhY2giLCJTRVJWRVIiLCJHUkVFTl9FTkVSR1kiLCJTQ0hFRFVMRVIiXSwic291cmNlcyI6WyIuLi8uLi8uLi9zcmMvbW9kdWxlL2FnZW50cy9yZXBvcnQtaGFuZGxlci50cyJdLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBBR0VOVF9UWVBFUyB9IGZyb20gXCIuLi8uLi9jb25zdGFudHMvaW5kZXguanNcIlxyXG5pbXBvcnQgeyBnZXRDdXJyZW50VGltZSB9IGZyb20gXCIuLi8uLi91dGlscy9pbmRleC5qc1wiXHJcbmltcG9ydCB7IEFHRU5UU19SRVBPUlRTX1NUQVRFLCBBR0VOVFNfU1RBVEUgfSBmcm9tIFwiLi9hZ2VudHMtc3RhdGUuanNcIlxyXG5cclxuY29uc3QgY2hhbmdlQ2xvdWROZXR3b3JrQ2FwYWNpdHlFdmVudCA9IChjbmFOYW1lLCBzZXJ2ZXJOYW1lLCBjYXBhY2l0eSwgaXNBZGRlZCkgPT4ge1xyXG4gICAgY29uc3QgZXZlbnRzID0gQUdFTlRTX1JFUE9SVFNfU1RBVEUuYWdlbnRzUmVwb3J0cy5maWx0ZXIoYWdlbnRSZXBvcnQgPT4gYWdlbnRSZXBvcnQubmFtZSA9PT0gY25hTmFtZSlbMF0/LmV2ZW50c1xyXG5cclxuICAgIGlmIChldmVudHMpIHtcclxuICAgICAgICBjb25zdCBldmVudE5hbWUgPSBpc0FkZGVkID8gJ05ldyBTZXJ2ZXInIDogJ1NlcnZlciBkaXNhYmxlZCdcclxuICAgICAgICBjb25zdCBldmVudCA9IGlzQWRkZWQgPyBgYWRkZWQgdG8gJHtjbmFOYW1lfWAgOiBgZGlzYWJsZWQgZnJvbSAke2NuYU5hbWV9YFxyXG4gICAgICAgIGNvbnN0IGV2ZW50RGVzY3JpcHRpb24gPSBgU2VydmVyICR7c2VydmVyTmFtZX0gd2l0aCBjYXBhY2l0eSAke2NhcGFjaXR5fSB3YXMgJHtldmVudH1gXHJcblxyXG4gICAgICAgIGV2ZW50cy5wdXNoKHtcclxuICAgICAgICAgICAgdHlwZTogJ0FHRU5UX0NPTk5FQ1RJT05fQ0hBTkdFJyxcclxuICAgICAgICAgICAgdGltZTogZ2V0Q3VycmVudFRpbWUoKSxcclxuICAgICAgICAgICAgbmFtZTogZXZlbnROYW1lLFxyXG4gICAgICAgICAgICBkZXNjcmlwdGlvbjogZXZlbnREZXNjcmlwdGlvblxyXG4gICAgICAgIH0pXHJcbiAgICB9XHJcbn1cclxuXHJcbmNvbnN0IHJlcG9ydFN5c3RlbVRyYWZmaWMgPSAodGltZSkgPT4ge1xyXG4gICAgY29uc3QgY3VycmVudFN0YXRlID0gQUdFTlRTX1NUQVRFLmFnZW50c1xyXG4gICAgICAgIC5maWx0ZXIoYWdlbnQgPT4gYWdlbnQudHlwZSA9PT0gQUdFTlRfVFlQRVMuQ0xPVURfTkVUV09SSylcclxuICAgICAgICAucmVkdWNlKChzdW0sIGFnZW50KSA9PiB7XHJcbiAgICAgICAgICAgIHN1bS5jYXBhY2l0eSA9IHN1bS5jYXBhY2l0eSArIGFnZW50Lm1heGltdW1DYXBhY2l0eVxyXG4gICAgICAgICAgICBzdW0udHJhZmZpYyA9IHN1bS50cmFmZmljICsgYWdlbnQubWF4aW11bUNhcGFjaXR5ICogYWdlbnQudHJhZmZpY1xyXG4gICAgICAgICAgICByZXR1cm4gc3VtXHJcbiAgICAgICAgfSwgKHsgY2FwYWNpdHk6IDAsIHRyYWZmaWM6IDAgfSkpXHJcbiAgICBjb25zdCBjdXJyZW50VHJhZmZpYyA9IGN1cnJlbnRTdGF0ZS5jYXBhY2l0eSA9PT0gMCA/IDAgOiBjdXJyZW50U3RhdGUudHJhZmZpYyAvIGN1cnJlbnRTdGF0ZS5jYXBhY2l0eVxyXG5cclxuICAgIHJldHVybiAoeyB0aW1lLCB2YWx1ZTogY3VycmVudFRyYWZmaWMgfSlcclxufVxyXG5cclxuY29uc3QgcmVwb3J0U2NoZWR1bGVyRGF0YSA9IChhZ2VudCwgdGltZSkgPT4ge1xyXG4gICAgY29uc3QgcmVwb3J0cyA9IEFHRU5UU19SRVBPUlRTX1NUQVRFLmFnZW50c1JlcG9ydHMuZmlsdGVyKGFnZW50UmVwb3J0ID0+IGFnZW50UmVwb3J0Lm5hbWUgPT09IGFnZW50Lm5hbWUpWzBdLnJlcG9ydHNcclxuXHJcbiAgICBjb25zdCBxdWV1ZUNhcGFjaXR5ID0gYWdlbnQubWF4UXVldWVTaXplID09PSAwID8gMCA6IGFnZW50LnNjaGVkdWxlZEpvYnMubGVuZ3RoXHJcblxyXG4gICAgcmVwb3J0cy5kZWFkbGluZVByaW9yaXR5UmVwb3J0LnB1c2goeyB0aW1lLCB2YWx1ZTogYWdlbnQuZGVhZGxpbmVQcmlvcml0eSB9KVxyXG4gICAgcmVwb3J0cy5wb3dlclByaW9yaXR5UmVwb3J0LnB1c2goeyB0aW1lLCB2YWx1ZTogYWdlbnQucG93ZXJQcmlvcml0eSB9KVxyXG4gICAgcmVwb3J0cy5xdWV1ZUNhcGFjaXR5UmVwb3J0LnB1c2goeyB0aW1lLCB2YWx1ZTogcXVldWVDYXBhY2l0eSB9KVxyXG4gICAgcmVwb3J0cy50cmFmZmljUmVwb3J0LnB1c2gocmVwb3J0U3lzdGVtVHJhZmZpYyh0aW1lKSlcclxufVxyXG5cclxuY29uc3QgcmVwb3J0Q2xvdWROZXR3b3JrRGF0YSA9IChhZ2VudCwgdGltZSkgPT4ge1xyXG4gICAgY29uc3QgcmVwb3J0cyA9IEFHRU5UU19SRVBPUlRTX1NUQVRFLmFnZW50c1JlcG9ydHMuZmlsdGVyKGFnZW50UmVwb3J0ID0+IGFnZW50UmVwb3J0Lm5hbWUgPT09IGFnZW50Lm5hbWUpWzBdLnJlcG9ydHNcclxuXHJcbiAgICByZXBvcnRzLmNsaWVudHNSZXBvcnQucHVzaCh7IHRpbWUsIHZhbHVlOiBhZ2VudC50b3RhbE51bWJlck9mQ2xpZW50cyB9KVxyXG4gICAgcmVwb3J0cy5jYXBhY2l0eVJlcG9ydC5wdXNoKHsgdGltZSwgdmFsdWU6IGFnZW50Lm1heGltdW1DYXBhY2l0eSB9KVxyXG4gICAgcmVwb3J0cy50cmFmZmljUmVwb3J0LnB1c2goeyB0aW1lLCB2YWx1ZTogYWdlbnQudHJhZmZpYyB9KVxyXG4gICAgcmVwb3J0cy5zdWNjZXNzUmF0aW9SZXBvcnQucHVzaCh7IHRpbWUsIHZhbHVlOiBhZ2VudC5zdWNjZXNzUmF0aW8gPz8gMCB9KVxyXG59XHJcblxyXG5jb25zdCByZXBvcnRTZXJ2ZXJEYXRhID0gKGFnZW50LCB0aW1lKSA9PiB7XHJcbiAgICBjb25zdCByZXBvcnRzID0gQUdFTlRTX1JFUE9SVFNfU1RBVEUuYWdlbnRzUmVwb3J0cy5maWx0ZXIoYWdlbnRSZXBvcnQgPT4gYWdlbnRSZXBvcnQubmFtZSA9PT0gYWdlbnQubmFtZSlbMF0ucmVwb3J0c1xyXG5cclxuICAgIHJlcG9ydHMudHJhZmZpY1JlcG9ydC5wdXNoKHsgdGltZSwgdmFsdWU6IGFnZW50LnRyYWZmaWMgfSlcclxuICAgIHJlcG9ydHMuY2FwYWNpdHlSZXBvcnQucHVzaCh7IHRpbWUsIHZhbHVlOiBhZ2VudC5jdXJyZW50TWF4aW11bUNhcGFjaXR5IH0pXHJcbiAgICByZXBvcnRzLmdyZWVuUG93ZXJVc2FnZVJlcG9ydC5wdXNoKHsgdGltZSwgdmFsdWU6IGFnZW50LnRyYWZmaWMgfSlcclxuICAgIHJlcG9ydHMuYmFja1VwUG93ZXJVc2FnZVJlcG9ydC5wdXNoKHsgdGltZSwgdmFsdWU6IGFnZW50LmN1cnJlbnRNYXhpbXVtQ2FwYWNpdHkgKiBhZ2VudC5iYWNrVXBUcmFmZmljIH0pXHJcbiAgICByZXBvcnRzLnN1Y2Nlc3NSYXRpb1JlcG9ydC5wdXNoKHsgdGltZSwgdmFsdWU6IGFnZW50LnN1Y2Nlc3NSYXRpbyA/PyAwIH0pXHJcbn1cclxuXHJcbmNvbnN0IHJlcG9ydEdyZWVuU291cmNlRGF0YSA9IChhZ2VudCwgdGltZSkgPT4ge1xyXG4gICAgY29uc3QgcmVwb3J0cyA9IEFHRU5UU19SRVBPUlRTX1NUQVRFLmFnZW50c1JlcG9ydHMuZmlsdGVyKGFnZW50UmVwb3J0ID0+IGFnZW50UmVwb3J0Lm5hbWUgPT09IGFnZW50Lm5hbWUpWzBdLnJlcG9ydHNcclxuXHJcbiAgICByZXBvcnRzLnRyYWZmaWNSZXBvcnQucHVzaCh7IHRpbWUsIHZhbHVlOiBhZ2VudC50cmFmZmljIH0pXHJcbiAgICByZXBvcnRzLmF2YWlsYWJsZUdyZWVuUG93ZXJSZXBvcnQucHVzaCh7IHRpbWUsIHZhbHVlOiBhZ2VudC5hdmFpbGFibGVHcmVlbkVuZXJneSB9KVxyXG4gICAgcmVwb3J0cy5jYXBhY2l0eVJlcG9ydC5wdXNoKHsgdGltZSwgdmFsdWU6IGFnZW50LmN1cnJlbnRNYXhpbXVtQ2FwYWNpdHkgfSlcclxuICAgIHJlcG9ydHMuam9ic09uR3JlZW5Qb3dlclJlcG9ydC5wdXNoKHsgdGltZSwgdmFsdWU6IGFnZW50Lm51bWJlck9mRXhlY3V0ZWRKb2JzIH0pXHJcbiAgICByZXBvcnRzLmpvYnNPbkhvbGRSZXBvcnQucHVzaCh7IHRpbWUsIHZhbHVlOiBhZ2VudC5udW1iZXJPZkpvYnNPbkhvbGQgfSlcclxuICAgIHJlcG9ydHMuc3VjY2Vzc1JhdGlvUmVwb3J0LnB1c2goeyB0aW1lLCB2YWx1ZTogYWdlbnQuc3VjY2Vzc1JhdGlvID8/IDAgfSlcclxufVxyXG5cclxuY29uc3QgdXBkYXRlQWdlbnRzUmVwb3J0c1N0YXRlID0gKHRpbWUpID0+IHtcclxuICAgIEFHRU5UU19TVEFURS5hZ2VudHMuZm9yRWFjaChhZ2VudCA9PiB7XHJcbiAgICAgICAgaWYgKGFnZW50LnR5cGUgPT09IEFHRU5UX1RZUEVTLkNMT1VEX05FVFdPUkspIHtcclxuICAgICAgICAgICAgcmVwb3J0Q2xvdWROZXR3b3JrRGF0YShhZ2VudCwgdGltZSlcclxuICAgICAgICB9XHJcbiAgICAgICAgZWxzZSBpZiAoYWdlbnQudHlwZSA9PT0gQUdFTlRfVFlQRVMuU0VSVkVSKSB7XHJcbiAgICAgICAgICAgIHJlcG9ydFNlcnZlckRhdGEoYWdlbnQsIHRpbWUpXHJcbiAgICAgICAgfVxyXG4gICAgICAgIGVsc2UgaWYgKGFnZW50LnR5cGUgPT09IEFHRU5UX1RZUEVTLkdSRUVOX0VORVJHWSkge1xyXG4gICAgICAgICAgICByZXBvcnRHcmVlblNvdXJjZURhdGEoYWdlbnQsIHRpbWUpXHJcbiAgICAgICAgfVxyXG4gICAgICAgIGVsc2UgaWYgKGFnZW50LnR5cGUgPT09IEFHRU5UX1RZUEVTLlNDSEVEVUxFUikge1xyXG4gICAgICAgICAgICByZXBvcnRTY2hlZHVsZXJEYXRhKGFnZW50LCB0aW1lKVxyXG4gICAgICAgIH1cclxuICAgIH0pXHJcbn1cclxuXHJcbmV4cG9ydCB7XHJcbiAgICBjaGFuZ2VDbG91ZE5ldHdvcmtDYXBhY2l0eUV2ZW50LFxyXG4gICAgdXBkYXRlQWdlbnRzUmVwb3J0c1N0YXRlXHJcbn0iXSwibWFwcGluZ3MiOiI7Ozs7OztBQUFBLElBQUFBLE1BQUEsR0FBQUMsT0FBQTtBQUNBLElBQUFDLE9BQUEsR0FBQUQsT0FBQTtBQUNBLElBQUFFLFlBQUEsR0FBQUYsT0FBQTtBQUVBLElBQU1HLCtCQUErQixHQUFHLFNBQWxDQSwrQkFBK0JBLENBQUlDLE9BQU8sRUFBRUMsVUFBVSxFQUFFQyxRQUFRLEVBQUVDLE9BQU8sRUFBSztFQUFBLElBQUFDLHFCQUFBO0VBQ2hGLElBQU1DLE1BQU0sSUFBQUQscUJBQUEsR0FBR0UsaUNBQW9CLENBQUNDLGFBQWEsQ0FBQ0MsTUFBTSxDQUFDLFVBQUFDLFdBQVc7SUFBQSxPQUFJQSxXQUFXLENBQUNDLElBQUksS0FBS1YsT0FBTztFQUFBLEVBQUMsQ0FBQyxDQUFDLENBQUMsY0FBQUkscUJBQUEsdUJBQXpGQSxxQkFBQSxDQUEyRkMsTUFBTTtFQUVoSCxJQUFJQSxNQUFNLEVBQUU7SUFDUixJQUFNTSxTQUFTLEdBQUdSLE9BQU8s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