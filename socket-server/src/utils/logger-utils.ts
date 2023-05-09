const logNewMessage = (msg) => console.log(`[${(new Date()).toLocaleTimeString()}] Retrieved message: ${JSON.stringify(msg)}`)
const logUserConnected = () => console.log(`[${(new Date()).toLocaleTimeString()}] User connected to WebSocket`)
const logStateReset = () => console.log(`[${(new Date()).toLocaleTimeString()}] State has been reset`)
const logPowerShortageEvent = (agentName, actionState) => console.log(`[${(new Date()).toLocaleTimeString()}] Power shortage ${actionState} for ${agentName}`)

export {
    logNewMessage,
    logUserConnected,
    logStateReset,
    logPowerShortageEvent
}