import { COMPRESSION_METHOD } from "../constants"

export interface MessageExchangeData {
    messageRetrievalDuration: number
    compressionMethod: COMPRESSION_METHOD
    compressionTime: number
    decompressionTime: number
    bytesSentToBytesReceived: number
    estimatedTransferCost: number
}