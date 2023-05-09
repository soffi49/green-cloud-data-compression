const parseData = (data: any) => {
    try {
        return JSON.parse(data)
    } catch (e) {
        return data
    }
}

export {
    parseData
}