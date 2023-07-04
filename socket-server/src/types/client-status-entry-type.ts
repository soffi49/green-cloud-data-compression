import { CommonReportEntry } from "./common-report-entry";

export interface ClientStatusReportEntry extends CommonReportEntry {
	value: {
		status: string;
		value: number;
	}[];
}
