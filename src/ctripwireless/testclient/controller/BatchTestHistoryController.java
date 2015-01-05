package ctripwireless.testclient.controller;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ctripwireless.testclient.enums.LabFolderName;
import ctripwireless.testclient.enums.SeperatorDefinition;
import ctripwireless.testclient.httpmodel.BatchRunItem;
import ctripwireless.testclient.httpmodel.DataGridJson;
import ctripwireless.testclient.httpmodel.Json;
import ctripwireless.testclient.service.BatchTestHistoryService;

@Controller
public class BatchTestHistoryController {
	private static final Logger logger = Logger.getLogger(BatchTestHistoryController.class);
	
	@Autowired
	BatchTestHistoryService batchTestHistoryService;
	
	@RequestMapping(value="/getBatchHistories")
	@ResponseBody
	public DataGridJson getBatchHistories(@RequestParam String folderName) {
		if(folderName.contains(SeperatorDefinition.seperator))
			folderName="root/"+LabFolderName.folder+"/"+folderName;
		return batchTestHistoryService.getBatchHistories(folderName);
	}
	
	@RequestMapping(value="/getTestRunInfo" )
	@ResponseBody
	public DataGridJson getTestRunInfo(@RequestParam String batchRunPath) {
		return batchTestHistoryService.getTestRunInfo(batchRunPath);
	}
	
	@RequestMapping(value="/getTestResultDetailInfo" )
	@ResponseBody
	public Json getTestResultDetailInfo(@RequestParam String testRunPath) {
		return batchTestHistoryService.getTestResultDetailInfo(testRunPath);
	}
	
	@RequestMapping(value="/deleteBatchHistory" )
	@ResponseBody
	public Json deleteBatchHistory(@RequestParam String batchRunPath) {
		return batchTestHistoryService.deleteBatchHistory(batchRunPath);
	}
	
	@RequestMapping(value="/deleteTestResultDetailInfoInBatchHistory" )
	@ResponseBody
	public void deleteTestResultDetailInfoInBatchHistory(@RequestParam String testRunPath) {
		File f=new File(testRunPath);
		if(f.exists())
			f.delete();
	}
}
