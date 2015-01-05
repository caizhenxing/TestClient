package ctripwireless.testclient.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ctripwireless.testclient.enums.LabFolderName;
import ctripwireless.testclient.enums.SeperatorDefinition;
import ctripwireless.testclient.httpmodel.DataGridJson;
import ctripwireless.testclient.service.ChartService;

@Controller
public class ChartController {
	private static final Logger logger = Logger.getLogger(ChartController.class);
	
	@Autowired
	ChartService chartService;
	
	@RequestMapping(value="/getGridStoreByDate", method=RequestMethod.GET )
	@ResponseBody
	public DataGridJson getGridStoreByDate(@RequestParam String dirPath,@RequestParam String date) throws Exception {
		if(dirPath.contains(SeperatorDefinition.seperator))
			dirPath="root/"+LabFolderName.folder+"/"+dirPath;
		return chartService.getGridStoreByDate(dirPath, date);
	}
	
	@RequestMapping(value="/getTestStatusDistributionByDate", method=RequestMethod.GET )
	@ResponseBody
	public DataGridJson getTestStatusDistributionByDate(@RequestParam String dirPath,@RequestParam String date) throws Exception {
		if(dirPath.contains(SeperatorDefinition.seperator))
			dirPath="root/"+LabFolderName.folder+"/"+dirPath;
		return chartService.getTestStatusDistributionByDate(dirPath,date);
	}
	
	@RequestMapping(value="/getTestPassedRateInAWeek", method=RequestMethod.GET )
	@ResponseBody
	public DataGridJson getTestPassedRateInAWeek(@RequestParam String dirPath) throws Exception {
		if(dirPath.contains(SeperatorDefinition.seperator))
			dirPath="root/"+LabFolderName.folder+"/"+dirPath;
		return chartService.getTestPassedRateInAWeek(dirPath);
	}
}
