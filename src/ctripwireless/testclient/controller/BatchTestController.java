package ctripwireless.testclient.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ctripwireless.testclient.enums.LabFolderName;
import ctripwireless.testclient.enums.SeperatorDefinition;
import ctripwireless.testclient.httpmodel.DataGridJson;
import ctripwireless.testclient.httpmodel.Json;
import ctripwireless.testclient.service.BatchTestService;

@Controller
public class BatchTestController {
	private static final Logger logger = Logger.getLogger(BatchTestController.class);
	
	@Autowired
	BatchTestService batchTestService;

	@RequestMapping(value="/executeBatchTest")
	@ResponseBody
	public void executeBatchTest(@RequestParam String dirPath){
		batchTestService.executeBatchTest(dirPath);
	}
	
	@RequestMapping(value="/getTestItems")
	@ResponseBody
	public DataGridJson getTestItems(@RequestParam String folderName){
		if(folderName.contains(SeperatorDefinition.seperator))
			folderName="root/"+LabFolderName.folder+"/"+folderName;
		return batchTestService.getRecentTestItems(folderName);
	}
	
	@RequestMapping(value="/getTestDetailResult")
	@ResponseBody
	public Json getTestDetailResult(@RequestParam String testPath){
		return batchTestService.getDetailTestResultByTestPath(testPath);
	}
	
	@RequestMapping(value="/deleteTestItem")
	@ResponseBody
	public Json deleteTestItem(@RequestParam String folderName){
		if(folderName.contains(SeperatorDefinition.seperator))
			folderName="root/"+LabFolderName.folder+"/"+folderName;
		return batchTestService.deleteTestItem(folderName);
	}
	
	@RequestMapping(value="/deleteTestInfo")
	@ResponseBody
	public Json deleteTestInfo(@RequestParam String folder,@RequestParam String time){
		return batchTestService.deleteTestInfo(folder, time);
	}
	
	
	
}
