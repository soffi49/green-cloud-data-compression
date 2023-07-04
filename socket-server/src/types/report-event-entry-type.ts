import { EVENT_TYPE } from "../constants";
import { CommonReportEntry } from "./common-report-entry";

export interface ReportEventEntry extends CommonReportEntry {
	name: string;
	description: string;
	type: EVENT_TYPE;
}
