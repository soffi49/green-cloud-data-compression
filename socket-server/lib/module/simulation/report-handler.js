"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.reportSimulationStatistics = void 0;
var _index = require("../../constants/index.js");
var _index2 = require("../../utils/index.js");
var _index3 = require("../agents/index.js");
var _index4 = require("../clients/index.js");
var _reportHandler = require("../managing-system/report-handler.js");
var _index5 = require("../network/index.js");
var _simulationState = require("./simulation-state.js");
var reportSimulationStatistics = setInterval(function () {
  if (_simulationState.SIMULATION_STATE.systemStartTime !== null) {
    var time = (0, _index2.getCurrentTime)();
    (0, _index3.updateAgentsReportsState)(time);
    (0, _index4.updateClientReportsState)(time);
    (0, _index5.updateNetworkReportsState)(time);
    (0, _reportHandler.updateManagingSystemReportsState)(time);
  }
}, _index.REPORTING_TIME * 1000);
exports.reportSimulationStatistics = reportSimulationStatistics;
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJuYW1lcyI6WyJfaW5kZXgiLCJyZXF1aXJlIiwiX2luZGV4MiIsIl9pbmRleDMiLCJfaW5kZXg0IiwiX3JlcG9ydEhhbmRsZXIiLCJfaW5kZXg1IiwiX3NpbXVsYXRpb25TdGF0ZSIsInJlcG9ydFNpbXVsYXRpb25TdGF0aXN0aWNzIiwic2V0SW50ZXJ2YWwiLCJTSU1VTEFUSU9OX1NUQVRFIiwic3lzdGVtU3RhcnRUaW1lIiwidGltZSIsImdldEN1cnJlbnRUaW1lIiwidXBkYXRlQWdlbnRzUmVwb3J0c1N0YXRlIiwidXBkYXRlQ2xpZW50UmVwb3J0c1N0YXRlIiwidXBkYXRlTmV0d29ya1JlcG9ydHNTdGF0ZSIsInVwZGF0ZU1hbmFnaW5nU3lzdGVtUmVwb3J0c1N0YXRlIiwiUkVQT1JUSU5HX1RJTUUiLCJleHBvcnRzIl0sInNvdXJjZXMiOlsiLi4vLi4vLi4vc3JjL21vZHVsZS9zaW11bGF0aW9uL3JlcG9ydC1oYW5kbGVyLnRzIl0sInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7IFJFUE9SVElOR19USU1FIH0gZnJvbSBcIi4uLy4uL2NvbnN0YW50cy9pbmRleC5qc1wiO1xyXG5pbXBvcnQgeyBnZXRDdXJyZW50VGltZSB9IGZyb20gXCIuLi8uLi91dGlscy9pbmRleC5qc1wiO1xyXG5pbXBvcnQgeyB1cGRhdGVBZ2VudHNSZXBvcnRzU3RhdGUgfSBmcm9tIFwiLi4vYWdlbnRzL2luZGV4LmpzXCI7XHJcbmltcG9ydCB7IHVwZGF0ZUNsaWVudFJlcG9ydHNTdGF0ZSB9IGZyb20gXCIuLi9jbGllbnRzL2luZGV4LmpzXCI7XHJcbmltcG9ydCB7IHVwZGF0ZU1hbmFnaW5nU3lzdGVtUmVwb3J0c1N0YXRlIH0gZnJvbSBcIi4uL21hbmFnaW5nLXN5c3RlbS9yZXBvcnQtaGFuZGxlci5qc1wiO1xyXG5pbXBvcnQgeyB1cGRhdGVOZXR3b3JrUmVwb3J0c1N0YXRlIH0gZnJvbSBcIi4uL25ldHdvcmsvaW5kZXguanNcIjtcclxuaW1wb3J0IHsgU0lNVUxBVElPTl9TVEFURSB9IGZyb20gXCIuL3NpbXVsYXRpb24tc3RhdGUuanNcIjtcclxuXHJcbmNvbnN0IHJlcG9ydFNpbXVsYXRpb25TdGF0aXN0aWNzID0gc2V0SW50ZXJ2YWwoZnVuY3Rpb24gKCkge1xyXG4gICAgaWYgKFNJTVVMQVRJT05fU1RBVEUuc3lzdGVtU3RhcnRUaW1lICE9PSBudWxsKSB7XHJcbiAgICAgICAgY29uc3QgdGltZSA9IGdldEN1cnJlbnRUaW1lKClcclxuXHJcbiAgICAgICAgdXBkYXRlQWdlbnRzUmVwb3J0c1N0YXRlKHRpbWUpXHJcbiAgICAgICAgdXBkYXRlQ2xpZW50UmVwb3J0c1N0YXRlKHRpbWUpXHJcbiAgICAgICAgdXBkYXRlTmV0d29ya1JlcG9ydHNTdGF0ZSh0aW1lKVxyXG4gICAgICAgIHVwZGF0ZU1hbmFnaW5nU3lzdGVtUmVwb3J0c1N0YXRlKHRpbWUpXHJcbiAgICB9XHJcbn0sIFJFUE9SVElOR19USU1FICogMTAwMCk7XHJcblxyXG5leHBvcnQge1xyXG4gICAgcmVwb3J0U2ltdWxhdGlvblN0YXRpc3RpY3NcclxufVxyXG4iXSwibWFwcGluZ3MiOiI7Ozs7OztBQUFBLElBQUFBLE1BQUEsR0FBQUMsT0FBQTtBQUNBLElBQUFDLE9BQUEsR0FBQUQsT0FBQTtBQUNBLElBQUFFLE9BQUEsR0FBQUYsT0FBQTtBQUNBLElBQUFHLE9BQUEsR0FBQUgsT0FBQTtBQUNBLElBQUFJLGNBQUEsR0FBQUosT0FBQTtBQUNBLElBQUFLLE9BQUEsR0FBQUwsT0FBQTtBQUNBLElBQUFNLGdCQUFBLEdBQUFOLE9BQUE7QUFFQSxJQUFNTywwQkFBMEIsR0FBR0MsV0FBVyxDQUFDLFlBQVk7RUFDdkQsSUFBSUMsaUNBQWdCLENBQUNDLGVBQWUsS0FBSyxJQUFJLEVBQUU7SUFDM0MsSUFBTUMsSUFBSSxHQUFHLElBQUFDLHNCQUFjLEVBQUMsQ0FBQztJQUU3QixJQUFBQyxnQ0FBd0IsRUFBQ0YsSUFBSSxDQUFDO0lBQzlCLElBQUFHLGdDQUF3QixFQUFDSCxJQUFJLENBQUM7SUFDOUIsSUFBQUksaUNBQXlCLEVBQUNKLElBQUksQ0FBQztJQUMvQixJQUFBSywrQ0FBZ0MsRUFBQ0wsSUFBSSxDQUFDO0VBQzFDO0FBQ0osQ0FBQyxFQUFFTSxxQkFBYyxHQUFHLElBQUksQ0FBQztBQUFDQyxPQUFBLENBQUFYLDBCQUFBLEdBQUFBLDBCQUFBIn0=