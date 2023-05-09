"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.handleSetClientJobTimeFrame = exports.handleSetClientJobStatus = exports.handleSetClientJobDurationMap = exports.handleJobSplit = void 0;
var _constants = require("../../constants/constants");
var _agentUtils = require("../../utils/agent-utils");
var _clientsState = require("./clients-state");
function _typeof(obj) { "@babel/helpers - typeof"; return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (obj) { return typeof obj; } : function (obj) { return obj && "function" == typeof Symbol && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }, _typeof(obj); }
function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); enumerableOnly && (symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; })), keys.push.apply(keys, symbols); } return keys; }
function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = null != arguments[i] ? arguments[i] : {}; i % 2 ? ownKeys(Object(source), !0).forEach(function (key) { _defineProperty(target, key, source[key]); }) : Object.getOwnPropertyDescriptors ? Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)) : ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } return target; }
function _defineProperty(obj, key, value) { key = _toPropertyKey(key); if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }
function _toPropertyKey(arg) { var key = _toPrimitive(arg, "string"); return _typeof(key) === "symbol" ? key : String(key); }
function _toPrimitive(input, hint) { if (_typeof(input) !== "object" || input === null) return input; var prim = input[Symbol.toPrimitive]; if (prim !== undefined) { var res = prim.call(input, hint || "default"); if (_typeof(res) !== "object") return res; throw new TypeError("@@toPrimitive must return a primitive value."); } return (hint === "string" ? String : Number)(input); }
var handleSetClientJobStatus = function handleSetClientJobStatus(msg) {
  var agent = (0, _agentUtils.getAgentByName)(_clientsState.CLIENTS_STATE.clients, msg.agentName);
  var jobStatus = msg.data.status;
  var splitJobId = msg.data.splitJobId;
  if (agent) {
    if (jobStatus === _constants.JOB_STATUSES.FAILED) {
      agent.status = jobStatus;
      if (agent.isSplit) {
        agent.splitJobs.forEach(function (job) {
          return job.status = jobStatus;
        });
      }
      return;
    }
    if (splitJobId) {
      var splitJob = agent.splitJobs.find(function (job) {
        return job.splitJobId === splitJobId;
      });
      if (splitJob) {
        splitJob.status = jobStatus;
      }
    } else {
      agent.status = jobStatus;
    }
  }
};
exports.handleSetClientJobStatus = handleSetClientJobStatus;
var handleSetClientJobTimeFrame = function handleSetClientJobTimeFrame(msg) {
  var agent = (0, _agentUtils.getAgentByName)(_clientsState.CLIENTS_STATE.clients, msg.agentName);
  var _msg$data = msg.data,
    start = _msg$data.start,
    end = _msg$data.end;
  var splitJobId = msg.data.splitJobId;
  if (agent) {
    if (splitJobId) {
      var splitJob = agent.splitJobs.find(function (job) {
        return job.splitJobId === splitJobId;
      });
      if (splitJob) {
        splitJob.start = start;
        splitJob.end = end;
      }
    } else {
      agent.job.start = start;
      agent.job.end = end;
    }
  }
};
exports.handleSetClientJobTimeFrame = handleSetClientJobTimeFrame;
var handleSetClientJobDurationMap = function handleSetClientJobDurationMap(msg) {
  var agent = (0, _agentUtils.getAgentByName)(_clientsState.CLIENTS_STATE.clients, msg.agentName);
  if (agent) {
    agent.durationMap = msg.data;
  }
};
exports.handleSetClientJobDurationMap = handleSetClientJobDurationMap;
var handleJobSplit = function handleJobSplit(msg) {
  var clients = _clientsState.CLIENTS_STATE.clients;
  var clientForSplit = clients.find(function (client) {
    return client.job.jobId === msg.jobId;
  });
  if (clientForSplit) {
    var splitData = msg.data.map(function (splitJob) {
      return _objectSpread({
        status: _constants.JOB_STATUSES.CREATED
      }, splitJob);
    });
    clientForSplit.isSplit = true;
    clientForSplit.splitJobs = splitData;
  }
};
exports.handleJobSplit = handleJobSplit;
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJuYW1lcyI6WyJfY29uc3RhbnRzIiwicmVxdWlyZSIsIl9hZ2VudFV0aWxzIiwiX2NsaWVudHNTdGF0ZSIsIl90eXBlb2YiLCJvYmoiLCJTeW1ib2wiLCJpdGVyYXRvciIsImNvbnN0cnVjdG9yIiwicHJvdG90eXBlIiwib3duS2V5cyIsIm9iamVjdCIsImVudW1lcmFibGVPbmx5Iiwia2V5cyIsIk9iamVjdCIsImdldE93blByb3BlcnR5U3ltYm9scyIsInN5bWJvbHMiLCJmaWx0ZXIiLCJzeW0iLCJnZXRPd25Qcm9wZXJ0eURlc2NyaXB0b3IiLCJlbnVtZXJhYmxlIiwicHVzaCIsImFwcGx5IiwiX29iamVjdFNwcmVhZCIsInRhcmdldCIsImkiLCJhcmd1bWVudHMiLCJsZW5ndGgiLCJzb3VyY2UiLCJmb3JFYWNoIiwia2V5IiwiX2RlZmluZVByb3BlcnR5IiwiZ2V0T3duUHJvcGVydHlEZXNjcmlwdG9ycyIsImRlZmluZVByb3BlcnRpZXMiLCJkZWZpbmVQcm9wZXJ0eSIsInZhbHVlIiwiX3RvUHJvcGVydHlLZXkiLCJjb25maWd1cmFibGUiLCJ3cml0YWJsZSIsImFyZyIsIl90b1ByaW1pdGl2ZSIsIlN0cmluZyIsImlucHV0IiwiaGludCIsInByaW0iLCJ0b1ByaW1pdGl2ZSIsInVuZGVmaW5lZCIsInJlcyIsImNhbGwiLCJUeXBlRXJyb3IiLCJOdW1iZXIiLCJoYW5kbGVTZXRDbGllbnRKb2JTdGF0dXMiLCJtc2ciLCJhZ2VudCIsImdldEFnZW50QnlOYW1lIiwiQ0xJRU5UU19TVEFURSIsImNsaWVudHMiLCJhZ2VudE5hbWUiLCJqb2JTdGF0dXMiLCJkYXRhIiwic3RhdHVzIiwic3BsaXRKb2JJZCIsIkpPQl9TVEFUVVNFUyIsIkZBSUxFRCIsImlzU3BsaXQiLCJzcGxpdEpvYnMiLCJqb2IiLCJzcGxpdEpvYiIsImZpbmQiLCJleHBvcnRzIiwiaGFuZGxlU2V0Q2xpZW50Sm9iVGltZUZyYW1lIiwiX21zZyRkYXRhIiwic3RhcnQiLCJlbmQiLCJoYW5kbGVTZXRDbGllbnRKb2JEdXJhdGlvbk1hcCIsImR1cmF0aW9uTWFwIiwiaGFuZGxlSm9iU3BsaXQiLCJjbGllbnRGb3JTcGxpdCIsImNsaWVudCIsImpvYklkIiwic3BsaXREYXRhIiwibWFwIiwiQ1JFQVRFRCJdLCJzb3VyY2VzIjpbIi4uLy4uLy4uL3NyYy9tb2R1bGUvY2xpZW50cy9tZXNzYWdlLWhhbmRsZXIudHMiXSwic291cmNlc0NvbnRlbnQiOlsiaW1wb3J0IHsgSk9CX1NUQVRVU0VTIH0gZnJvbSBcIi4uLy4uL2NvbnN0YW50cy9jb25zdGFudHNcIlxyXG5pbXBvcnQgeyBnZXRBZ2VudEJ5TmFtZSB9IGZyb20gXCIuLi8uLi91dGlscy9hZ2VudC11dGlsc1wiXHJcbmltcG9ydCB7IENMSUVOVFNfU1RBVEUgfSBmcm9tIFwiLi9jbGllbnRzLXN0YXRlXCJcclxuXHJcbmNvbnN0IGhhbmRsZVNldENsaWVudEpvYlN0YXR1cyA9IChtc2cpID0+IHtcclxuICAgIGNvbnN0IGFnZW50ID0gZ2V0QWdlbnRCeU5hbWUoQ0xJRU5UU19TVEFURS5jbGllbnRzLCBtc2cuYWdlbnROYW1lKVxyXG4gICAgY29uc3Qgam9iU3RhdHVzID0gbXNnLmRhdGEuc3RhdHVzXHJcbiAgICBjb25zdCBzcGxpdEpvYklkID0gbXNnLmRhdGEuc3BsaXRKb2JJZFxyXG5cclxuICAgIGlmIChhZ2VudCkge1xyXG4gICAgICAgIGlmIChqb2JTdGF0dXMgPT09IEpPQl9TVEFUVVNFUy5GQUlMRUQpIHtcclxuICAgICAgICAgICAgYWdlbnQuc3RhdHVzID0gam9iU3RhdHVzXHJcbiAgICAgICAgICAgIGlmIChhZ2VudC5pc1NwbGl0KSB7XHJcbiAgICAgICAgICAgICAgICBhZ2VudC5zcGxpdEpvYnMuZm9yRWFjaChqb2IgPT4gam9iLnN0YXR1cyA9IGpvYlN0YXR1cylcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICByZXR1cm5cclxuICAgICAgICB9XHJcbiAgICAgICAgaWYgKHNwbGl0Sm9iSWQpIHtcclxuICAgICAgICAgICAgY29uc3Qgc3BsaXRKb2IgPSBhZ2VudC5zcGxpdEpvYnMuZmluZChqb2IgPT4gam9iLnNwbGl0Sm9iSWQgPT09IHNwbGl0Sm9iSWQpXHJcbiAgICAgICAgICAgIGlmIChzcGxpdEpvYikge1xyXG4gICAgICAgICAgICAgICAgc3BsaXRKb2Iuc3RhdHVzID0gam9iU3RhdHVzXHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICB9IGVsc2Uge1xyXG4gICAgICAgICAgICBhZ2VudC5zdGF0dXMgPSBqb2JTdGF0dXNcclxuICAgICAgICB9XHJcbiAgICB9XHJcbn1cclxuXHJcbmNvbnN0IGhhbmRsZVNldENsaWVudEpvYlRpbWVGcmFtZSA9IChtc2cpID0+IHtcclxuICAgIGNvbnN0IGFnZW50ID0gZ2V0QWdlbnRCeU5hbWUoQ0xJRU5UU19TVEFURS5jbGllbnRzLCBtc2cuYWdlbnROYW1lKVxyXG4gICAgY29uc3QgeyBzdGFydCwgZW5kIH0gPSBtc2cuZGF0YVxyXG4gICAgY29uc3Qgc3BsaXRKb2JJZCA9IG1zZy5kYXRhLnNwbGl0Sm9iSWRcclxuXHJcbiAgICBpZiAoYWdlbnQpIHtcclxuICAgICAgICBpZiAoc3BsaXRKb2JJZCkge1xyXG4gICAgICAgICAgICBjb25zdCBzcGxpdEpvYiA9IGFnZW50LnNwbGl0Sm9icy5maW5kKGpvYiA9PiBqb2Iuc3BsaXRKb2JJZCA9PT0gc3BsaXRKb2JJZClcclxuICAgICAgICAgICAgaWYgKHNwbGl0Sm9iKSB7XHJcbiAgICAgICAgICAgICAgICBzcGxpdEpvYi5zdGFydCA9IHN0YXJ0XHJcbiAgICAgICAgICAgICAgICBzcGxpdEpvYi5lbmQgPSBlbmRcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgIH0gZWxzZSB7XHJcbiAgICAgICAgICAgIGFnZW50LmpvYi5zdGFydCA9IHN0YXJ0XHJcbiAgICAgICAgICAgIGFnZW50LmpvYi5lbmQgPSBlbmRcclxuICAgICAgICB9XHJcbiAgICB9XHJcbn1cclxuXHJcbmNvbnN0IGhhbmRsZVNldENsaWVudEpvYkR1cmF0aW9uTWFwID0gKG1zZykgPT4ge1xyXG4gICAgY29uc3QgYWdlbnQgPSBnZXRBZ2VudEJ5TmFtZShDTElFTlRTX1NUQVRFLmNsaWVudHMsIG1zZy5hZ2VudE5hbWUpXHJcblxyXG4gICAgaWYgKGFnZW50KSB7XHJcbiAgICAgICAgYWdlbnQuZHVyYXRpb25NYXAgPSBtc2cuZGF0YVxyXG4gICAgfVxyXG59XHJcblxyXG5jb25zdCBoYW5kbGVKb2JTcGxpdCA9IChtc2cpID0+IHtcclxuICAgIGNvbnN0IGNsaWVudHMgPSBDTElFTlRTX1NUQVRFLmNsaWVudHNcclxuICAgIGNvbnN0IGNsaWVudEZvclNwbGl0ID0gY2xpZW50cy5maW5kKGNsaWVudCA9PiBjbGllbnQuam9iLmpvYklkID09PSBtc2cuam9iSWQpXHJcblxyXG4gICAgaWYgKGNsaWVudEZvclNwbGl0KSB7XHJcbiAgICAgICAgY29uc3Qgc3BsaXREYXRhID0gbXNnLmRhdGEubWFwKHNwbGl0Sm9iID0+ICh7IHN0YXR1czogSk9CX1NUQVRVU0VTLkNSRUFURUQsIC4uLnNwbGl0Sm9iIH0pKVxyXG4gICAgICAgIGNsaWVudEZvclNwbGl0LmlzU3BsaXQgPSB0cnVlXHJcbiAgICAgICAgY2xpZW50Rm9yU3BsaXQuc3BsaXRKb2JzID0gc3BsaXREYXRhXHJcbiAgICB9XHJcbn1cclxuXHJcblxyXG5leHBvcnQge1xyXG4gICAgaGFuZGxlU2V0Q2xpZW50Sm9iU3RhdHVzLFxyXG4gICAgaGFuZGxlU2V0Q2xpZW50Sm9iVGltZUZyYW1lLFxyXG4gICAgaGFuZGxlU2V0Q2xpZW50Sm9iRHVyYXRpb25NYXAsXHJcbiAgICBoYW5kbGVKb2JTcGxpdFxyXG59Il0sIm1hcHBpbmdzIjoiOzs7Ozs7QUFBQSxJQUFBQSxVQUFBLEdBQUFDLE9BQUE7QUFDQSxJQUFBQyxXQUFBLEdBQUFELE9BQUE7QUFDQSxJQUFBRSxhQUFBLEdBQUFGLE9BQUE7QUFBK0MsU0FBQUcsUUFBQUMsR0FBQSxzQ0FBQUQsT0FBQSx3QkFBQUUsTUFBQSx1QkFBQUEsTUFBQSxDQUFB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