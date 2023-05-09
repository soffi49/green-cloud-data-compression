"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.reportSimulationStatistics = void 0;
var _index = require("../../constants/index.js");
var _index2 = require("../../utils/index.js");
var _index3 = require("../agents/index.js");
var _index4 = require("../clients/index.js");
var _index5 = require("../network/index.js");
var _simulationState = require("./simulation-state.js");
var reportSimulationStatistics = setInterval(function () {
  if (_simulationState.SIMULATION_STATE.systemStartTime !== null) {
    var time = (0, _index2.getCurrentTime)();
    (0, _index3.updateAgentsReportsState)(time);
    (0, _index4.updateClientReportsState)(time);
    (0, _index5.updateNetworkReportsState)(time);
  }
}, _index.REPORTING_TIME * 1000);
exports.reportSimulationStatistics = reportSimulationStatistics;
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJuYW1lcyI6WyJfaW5kZXgiLCJyZXF1aXJlIiwiX2luZGV4MiIsIl9pbmRleDMiLCJfaW5kZXg0IiwiX2luZGV4NSIsIl9zaW11bGF0aW9uU3RhdGUiLCJyZXBvcnRTaW11bGF0aW9uU3RhdGlzdGljcyIsInNldEludGVydmFsIiwiU0lNVUxBVElPTl9TVEFURSIsInN5c3RlbVN0YXJ0VGltZSIsInRpbWUiLCJnZXRDdXJyZW50VGltZSIsInVwZGF0ZUFnZW50c1JlcG9ydHNTdGF0ZSIsInVwZGF0ZUNsaWVudFJlcG9ydHNTdGF0ZSIsInVwZGF0ZU5ldHdvcmtSZXBvcnRzU3RhdGUiLCJSRVBPUlRJTkdfVElNRSIsImV4cG9ydHMiXSwic291cmNlcyI6WyIuLi8uLi8uLi9zcmMvbW9kdWxlL3NpbXVsYXRpb24vcmVwb3J0LWhhbmRsZXIudHMiXSwic291cmNlc0NvbnRlbnQiOlsiaW1wb3J0IHsgUkVQT1JUSU5HX1RJTUUgfSBmcm9tIFwiLi4vLi4vY29uc3RhbnRzL2luZGV4LmpzXCI7XHJcbmltcG9ydCB7IGdldEN1cnJlbnRUaW1lIH0gZnJvbSBcIi4uLy4uL3V0aWxzL2luZGV4LmpzXCI7XHJcbmltcG9ydCB7IHVwZGF0ZUFnZW50c1JlcG9ydHNTdGF0ZSB9IGZyb20gXCIuLi9hZ2VudHMvaW5kZXguanNcIjtcclxuaW1wb3J0IHsgdXBkYXRlQ2xpZW50UmVwb3J0c1N0YXRlIH0gZnJvbSBcIi4uL2NsaWVudHMvaW5kZXguanNcIjtcclxuaW1wb3J0IHsgdXBkYXRlTmV0d29ya1JlcG9ydHNTdGF0ZSB9IGZyb20gXCIuLi9uZXR3b3JrL2luZGV4LmpzXCI7XHJcbmltcG9ydCB7IFNJTVVMQVRJT05fU1RBVEUgfSBmcm9tIFwiLi9zaW11bGF0aW9uLXN0YXRlLmpzXCI7XHJcblxyXG5jb25zdCByZXBvcnRTaW11bGF0aW9uU3RhdGlzdGljcyA9IHNldEludGVydmFsKGZ1bmN0aW9uICgpIHtcclxuICAgIGlmIChTSU1VTEFUSU9OX1NUQVRFLnN5c3RlbVN0YXJ0VGltZSAhPT0gbnVsbCkge1xyXG4gICAgICAgIGNvbnN0IHRpbWUgPSBnZXRDdXJyZW50VGltZSgpXHJcblxyXG4gICAgICAgIHVwZGF0ZUFnZW50c1JlcG9ydHNTdGF0ZSh0aW1lKVxyXG4gICAgICAgIHVwZGF0ZUNsaWVudFJlcG9ydHNTdGF0ZSh0aW1lKVxyXG4gICAgICAgIHVwZGF0ZU5ldHdvcmtSZXBvcnRzU3RhdGUodGltZSlcclxuICAgIH1cclxufSwgUkVQT1JUSU5HX1RJTUUgKiAxMDAwKTtcclxuXHJcbmV4cG9ydCB7XHJcbiAgICByZXBvcnRTaW11bGF0aW9uU3RhdGlzdGljc1xyXG59XHJcbiJdLCJtYXBwaW5ncyI6Ijs7Ozs7O0FBQUEsSUFBQUEsTUFBQSxHQUFBQyxPQUFBO0FBQ0EsSUFBQUMsT0FBQSxHQUFBRCxPQUFBO0FBQ0EsSUFBQUUsT0FBQSxHQUFBRixPQUFBO0FBQ0EsSUFBQUcsT0FBQSxHQUFBSCxPQUFBO0FBQ0EsSUFBQUksT0FBQSxHQUFBSixPQUFBO0FBQ0EsSUFBQUssZ0JBQUEsR0FBQUwsT0FBQTtBQUVBLElBQU1NLDBCQUEwQixHQUFHQyxXQUFXLENBQUMsWUFBWTtFQUN2RCxJQUFJQyxpQ0FBZ0IsQ0FBQ0MsZUFBZSxLQUFLLElBQUksRUFBRTtJQUMzQyxJQUFNQyxJQUFJLEdBQUcsSUFBQUMsc0JBQWMsRUFBQyxDQUFDO0lBRTdCLElBQUFDLGdDQUF3QixFQUFDRixJQUFJLENBQUM7SUFDOUIsSUFBQUcsZ0NBQXdCLEVBQUNILElBQUksQ0FBQztJQUM5QixJQUFBSSxpQ0FBeUIsRUFBQ0osSUFBSSxDQUFDO0VBQ25DO0FBQ0osQ0FBQyxFQUFFSyxxQkFBYyxHQUFHLElBQUksQ0FBQztBQUFDQyxPQUFBLENBQUFWLDBCQUFBLEdBQUFBLDBCQUFBIn0=