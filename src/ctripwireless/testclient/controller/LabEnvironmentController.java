package ctripwireless.testclient.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.context.SecurityContextHolder;

import ctripwireless.testclient.enums.LabFolderName;
import ctripwireless.testclient.enums.SeperatorDefinition;
import ctripwireless.testclient.enums.TestSetFileName;
import ctripwireless.testclient.enums.TimeFormatDefiniation;
import ctripwireless.testclient.httpmodel.DataGridJson;
import ctripwireless.testclient.httpmodel.Json;
import ctripwireless.testclient.httpmodel.RunningSet;
import ctripwireless.testclient.service.LabEnvironmentService;
import ctripwireless.testclient.utils.MyFileUtils;

@Controller
public class LabEnvironmentController {
	@Autowired
	LabEnvironmentService labEnvironmentService;
	
	private static final Logger logger = Logger.getLogger(LabEnvironmentController.class);
	
	@RequestMapping(value="/addRunningSet", method=RequestMethod.POST)
	@ResponseBody
	public Json addRunningSet(@RequestParam String name,@RequestParam String[] tests) {
		return labEnvironmentService.addRunningSet(name, StringUtils.join(tests,SeperatorDefinition.seperator));
	}
	
	@RequestMapping(value="/getAllRunningSet" )
	@ResponseBody
	public DataGridJson getAllRunningSet() {
		return labEnvironmentService.getAllRunningSet();
	}
	
	@RequestMapping(value="/deleteRunningSet" )
	@ResponseBody
	public void deleteRunningSet(@RequestParam String foldername){
		labEnvironmentService.deleteRunningSet(foldername);
	}
	
	@RequestMapping(value="/getSelectedTestPaths" )
	@ResponseBody
	public Json getSelectedTestPaths(@RequestParam String foldername) {
		Json j=new Json();
		String[] tests=labEnvironmentService.getAllTestPathInRunningSet("root/"+LabFolderName.folder+"/"+foldername);
		if(!tests[0].isEmpty()){
			j.setObj(tests);
			j.setSuccess(true);
		}else
			j.setSuccess(false);
		return j;
	}

	@RequestMapping(value="/renameRunningSet" )
	@ResponseBody
	public Json renameRunningSet(@RequestParam String runningset,@RequestParam String updatedname){
		Json j= new Json();
		File from = new File("root/"+LabFolderName.folder,runningset);
		String author=SecurityContextHolder.getContext().getAuthentication().getName();
		SimpleDateFormat format = new SimpleDateFormat(TimeFormatDefiniation.runningSet);			
		String time=format.format(new Date());
		String foldername=updatedname+SeperatorDefinition.seperator+time+SeperatorDefinition.seperator+author;
		File to = new File("root/"+LabFolderName.folder,foldername);
    	j.setSuccess(MyFileUtils.renameDirectory(from, to));
    	return j;
	}
	
	@RequestMapping(value="/updateRunningSet" )
	@ResponseBody
	public Json updateRunningSet(@RequestParam String runningset,@RequestParam String updatedname,
			@RequestParam String[] updatedtests,@RequestParam boolean deletehistory){
		String str=StringUtils.join(updatedtests,SeperatorDefinition.seperator);
		return labEnvironmentService.modifyRunningSet(runningset, updatedname, str,deletehistory);
	}
	
	@RequestMapping(value="/checkAllTestPathExist" )
	@ResponseBody
	public Json checkAllTestPathExist(@RequestParam String runningSet){
		Json j=new Json();
		List<String> list=labEnvironmentService.getNotExistingTestPaths("root/"+LabFolderName.folder+"/"+runningSet);
		if(list.size()==0){
			j.setSuccess(true);
		}else{
			j.setObj(list);
			j.setSuccess(false);
		}
		return j;
	}
	
	@RequestMapping(value="/syncTestUnderRunningSet" )
	@ResponseBody
	public Json SyncTestUnderRunningSet(@RequestParam String runningSet,@RequestParam String[] removedTests){
		Json j=new Json();
		File f=new File("root/"+LabFolderName.folder+"/"+runningSet,TestSetFileName.TestSet);
		try {
			String tests=FileUtils.readFileToString(f);
			for(String removed : removedTests){
				if(tests.endsWith(removed))
					tests=tests.replace(removed, "");
				else
					tests=tests.replace(removed+SeperatorDefinition.seperator, "");
			}
			FileUtils.writeStringToFile(f, tests);
			j.setSuccess(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getClass()+e.getMessage());
		}
		return j;
	}
	
}
