import { EVENT_STATE, EVENT_TYPE } from "../../constants/index.js"
import { getAgentByName } from "../../utils/index.js"
import { logPowerShortageEvent } from "../../utils/logger-utils.js"
import { AGENTS_STATE } from "./agents-state.js"

const getEventByType = (events, type) => {
    return events.find(event => event.type === type)
}

const getEventOccurenceTime = () => {
    const occurenceTime = new Date()
    occurenceTime.setSeconds(occurenceTime.getSeconds() + 2)
    return occurenceTime
}

async function unlockEvent(event) {
    await new Promise(f => setTimeout(f, 3000));
    event.disabled = false
}

const handlePowerShortage = (data) => {
    const agent = getAgentByName(AGENTS_STATE.agents, data.agentName)

    if (agent) {
        const event = getEventByType(agent.events, EVENT_TYPE.POWER_SHORTAGE_EVENT)

        if (event) {
            const isEventActive = event.state === EVENT_STATE.ACTIVE
            const eventState = isEventActive ? 'triggered' : 'finished'
            logPowerShortageEvent(agent.name, eventState)
            const maxCapacity = isEventActive ? data.data.newMaximumCapacity : agent.initialMaximumCapacity

            agent.currentMaximumCapacity = maxCapacity
            event.disabled = true
            const dataToReturn = {
                agentName: agent.name,
                type: EVENT_TYPE.POWER_SHORTAGE_EVENT,
                data: {
                    newMaximumCapacity: maxCapacity,
                    occurrenceTime: getEventOccurenceTime(),
                    isFinished: event.state !== EVENT_STATE.ACTIVE
                }
            }
            event.state = isEventActive ? EVENT_STATE.INACTIVE : EVENT_STATE.ACTIVE;

            unlockEvent(event)
            return dataToReturn
        }
    }
}

export {
    handlePowerShortage
}