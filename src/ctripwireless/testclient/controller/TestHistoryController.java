package ctripwireless.testclient.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ctripwireless.testclient.httpmodel.DataGridJson;
import ctripwireless.testclient.httpmodel.Json;
import ctripwireless.testclient.service.TestHistoryService;


@Controller
public class TestHistoryController {
	private static final Logger logger = Logger.getLogger(TestHistoryController.class);
	@Autowired
	TestHistoryService testHistoryService;
	
	@RequestMapping(value="/getHistories")
	@ResponseBody
	public DataGridJson getHistories(@RequestParam String folderName) throws Exception {
		return testHistoryService.getHistories(folderName);
	}
	
	@RequestMapping(value="/getHistoryDetail" )
	@ResponseBody
	public Json getHistoryDetail(@RequestParam String folderName,@RequestParam String time) throws Exception {
		return testHistoryService.getDetailHistoryByTime(folderName, time);
	}
	
	@RequestMapping(value="/deleteHistory" )
	@ResponseBody
	public Json deleteHistory(@RequestParam String folderName,@RequestParam String time) throws Exception {
		return testHistoryService.deleteHistory(folderName, time);
	}
}
