interface GraphState {
    nodes: any[]
    connections: any[]
}

let GRAPH_STATE: GraphState = {
    nodes: [],
    connections: []
}

const resetGraphState = () =>
    Object.assign(GRAPH_STATE,
        ({
            nodes: [],
            connections: []
        }))

export {
    GraphState,
    GRAPH_STATE,
    resetGraphState
}