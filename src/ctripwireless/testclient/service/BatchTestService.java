package ctripwireless.testclient.service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import shelper.iffixture.HTTPFacade;

import ctripwireless.testclient.enums.BatchTestingFolderName;
import ctripwireless.testclient.enums.HistoryFolderName;
import ctripwireless.testclient.enums.LabFolderName;
import ctripwireless.testclient.enums.LoopParameterNameInForm;
import ctripwireless.testclient.enums.SeperatorDefinition;
import ctripwireless.testclient.enums.TestSetFileName;
import ctripwireless.testclient.enums.TestStatus;
import ctripwireless.testclient.enums.TimeFormatDefiniation;
import ctripwireless.testclient.factory.JsonObjectMapperFactory;
import ctripwireless.testclient.httpmodel.BatchTestItem;
import ctripwireless.testclient.httpmodel.DataGridJson;
import ctripwireless.testclient.httpmodel.Json;
import ctripwireless.testclient.httpmodel.TestHistoryItem;
import ctripwireless.testclient.httpmodel.TestResultItem;
import ctripwireless.testclient.model.HttpTarget;
import ctripwireless.testclient.model.KeyValue;
import ctripwireless.testclient.model.Parameter;
import ctripwireless.testclient.utils.Auto;
import ctripwireless.testclient.utils.FileNameUtils;
import ctripwireless.testclient.utils.MyFileUtils;
import ctripwireless.testclient.utils.TemplateUtils;


@Service("batchTestService")
public class BatchTestService {
	private static final Logger logger = Logger.getLogger(TestHistoryService.class);
	@Autowired
	TestExecuteService testExecuteService;
	@Autowired
	LabEnvironmentService labEnvironmentService;
	
	public synchronized void executeBatchTest(String dirPath){
		if(dirPath.contains(SeperatorDefinition.seperator))
			dirPath="root/"+LabFolderName.folder+"/"+dirPath;
		SimpleDateFormat format = new SimpleDateFormat(TimeFormatDefiniation.timeFolderFormat);			
		String runid=format.format(new Date());
		List<String> list=new ArrayList<String>();
		if(dirPath.endsWith("-dir"))
			getTestsInfoUnderSpecificDir(dirPath,list);
		else
			getTestsInfoUnderSpecificRunningSet(dirPath,list);
		for(String defaultTest : list){
			String[] info = defaultTest.split(SeperatorDefinition.seperator);
			String testpath=info[0];
			String testfilename=info[1];
			Map parameters=new HashMap();
			String batchtestingfolderpath=dirPath+"/"+BatchTestingFolderName.folderName+"/"+runid;
			try{
				parameters = testExecuteService.getRequestParameterMap(testpath);
				String looptimes=(String)parameters.get(LoopParameterNameInForm.name);
		        looptimes=looptimes!=null ?looptimes:"1";
		        String[] loopparas=looptimes.split(SeperatorDefinition.seperator);
		        looptimes=loopparas[0];
		        if(!looptimes.isEmpty() && StringUtils.isNumeric(looptimes)){
		        	for(int i=0;i<Integer.parseInt(looptimes);i++){
		        		if(loopparas.length>1)
			        		Thread.sleep(Integer.parseInt(StringUtils.isNumeric(loopparas[1])?loopparas[1]:"1"));
		        		TestResultItem testresult = new TestResultItem();
		        		String teststarttime=testresult.getTime();
		        		try{
		        			testExecuteService.setupAction(testpath);
		        			testresult = testExecuteService.getTestResultItem(testpath,parameters);
		    				if(!testresult.getResult().equals(TestStatus.exception)){
		    					testExecuteService.getCheckpointsAndResultFromFile(testpath,parameters,testresult.getResponseInfo(),testresult);
		    				}
		        		}catch(Exception e){
		    				testresult.setResult(TestStatus.exception);
		    				testresult.setComment("批量执行失败.\n"+e.getClass().toString()+e.getMessage());
		    			}
		    			finally{
		    				try {
		    					testExecuteService.teardownAction(testpath,parameters,testresult.getResponseInfo());
		    					this.generateBatchExecutionResultFiles(batchtestingfolderpath,testpath, teststarttime, testresult.getDuration(),testresult.getResult(), testresult, testfilename);
		    				}catch (Exception e) {
		    					logger.error("写历史记录文件失败：\n" + e.getClass().toString() + e.getMessage());
		    				}
		    			}
		        	}
		        }else{
		    		TestResultItem testresult = new TestResultItem();
		    		testresult.setDuration("");
		    		testresult.setResult(TestStatus.exception);
		    		testresult.setComment("循环次数必须为自然数！");
		        	this.generateBatchExecutionResultFiles(batchtestingfolderpath,
							testpath, testresult.getTime(), "",TestStatus.exception, testresult, testfilename);
		        }
			}catch(Exception e){
				TestResultItem testresult = new TestResultItem();
	    		testresult.setDuration("");
	    		testresult.setResult(TestStatus.exception);
	    		testresult.setComment("getRequestParameterMap失败.\n"+e.getClass().toString()+e.getMessage());
	    		this.generateBatchExecutionResultFiles(batchtestingfolderpath,
						testpath, testresult.getTime(), "",TestStatus.exception, testresult, testfilename);
			}
		}
	}
	
	private void generateBatchExecutionResultFiles(String batchtestingfolderpath,String testpath,String teststarttime,String duration,String result,TestResultItem testresult,String testfilename){
		try {
			//create folder under BatchTesting
			MyFileUtils.makeDir(batchtestingfolderpath);
			//generate history file
			File historyp=new File(testpath+"/"+HistoryFolderName.folderName);
			if(!historyp.exists()){
				historyp.mkdirs();
			}
			String recordf = FileNameUtils.getResultFile(teststarttime, duration, result);
			File file=new File(historyp,recordf);
			file.createNewFile();
			ObjectMapper mapper = JsonObjectMapperFactory.getObjectMapper();	
			mapper.writeValue(file, testresult);
			//generate test item file under BatchTesting
			testfilename=FileNameUtils.getTestRunFile(testfilename, teststarttime, duration, result);	
			file=new File(batchtestingfolderpath,testfilename);
			file.createNewFile();
			String content=testpath+"/"+HistoryFolderName.folderName+SeperatorDefinition.testInfoSeperator+recordf;
			FileUtils.writeStringToFile(file, content);
		}catch (IOException e) {
			logger.error("写历史记录文件失败：\n" + e.getClass().toString() + e.getMessage());
		}
	}
	
	
	public DataGridJson getRecentTestItems(String dirPath){
		DataGridJson j = new DataGridJson();
		List<BatchTestItem>	row=getDefaultTestItems(dirPath);
		File dir=new File(dirPath+"/"+BatchTestingFolderName.folderName);
		String recentRunid=getRecentTest(dir);
		dir=new File(dirPath+"/BatchTesting/"+recentRunid);
		String[] files = dir.list();
		for(String filename : files){
			String seperator=SeperatorDefinition.seperator;
			String tname=filename.split(seperator)[0];
			for(BatchTestItem bti : row){
				if(bti.getName().equals(tname) && !bti.isDoesrun()){
					BatchTestItem item= new BatchTestItem();
					item.setName(tname);
					item.setTestpath(bti.getTestpath());
					item.setPath(dirPath+"/BatchTesting/"+recentRunid+"/"+filename);
					item.setTime(filename.split(seperator)[1]);
					item.setDuration(filename.split(seperator)[2]);
					item.setStatus(filename.split(seperator)[3]);
					item.setDoesrun(true);
					row.remove(bti);
		            row.add(item);
					break;
				}
			}
		}
		j.setRows(row);
		j.setTotal(row.size());
		return j;
	}

	public Json getDetailTestResultByTestPath(String path){
		Json j =new Json();
		try{
			TestResultItem tri=new TestResultItem();
			File file = new File(path);
			if(file.exists() && file.isFile()){
				String content = FileUtils.readFileToString(file);
				String historypath=content.split(SeperatorDefinition.testInfoSeperator)[0];
				String historyfile=content.split(SeperatorDefinition.testInfoSeperator)[1];
				file=new File(historypath);
				file=new File(file,historyfile);
				if(file.exists()){
					ObjectMapper mapper = JsonObjectMapperFactory.getObjectMapper();
					tri = mapper.readValue(file, TestResultItem.class);
				}
				else{
					tri.setComment("历史记录已删除，是否删除该条目");
				}
			}
			else
				tri.setResult(TestStatus.ready);
			j.setObj(tri);
			j.setSuccess(true);
		}catch (IOException e) {
			j.setSuccess(false);
			j.setMsg("获取历史记录失败,"+e.getClass().toString()+": "+e.getMessage());
			logger.error("获取历史记录失败", e);
		}
		return j;
	}
	
	public Json deleteTestInfo(String folder, String time){
		Json j =new Json();
		try{
			File dir = new File(folder+"/"+BatchTestingFolderName.folderName);
			String[] files = dir.list();
			for(int i=0;i<files.length;i++){
				String recentTimeFolder = getRecentTest(files);
				if(recentTimeFolder!=""){
					File timeFolder=new File(folder+"/BatchTesting/"+recentTimeFolder);
					for(String testInfo : timeFolder.list()){
						if(testInfo.startsWith(time)){
							File f=new File(timeFolder,testInfo);
			        		if(f.isFile() && f.exists()){     
			                    f.delete();
			                    j.setSuccess(true);
			                    break;
			        		}	
						}
					}
					files=files.toString().replace(recentTimeFolder, "").replace("[", "").replace("]", "").replace(" ", "").split(",");
				}
			}
		} catch (Exception e) {
			j.setSuccess(false);
			logger.error("删除TestInfo失败", e);
		}
		return j;
	}
	
	public Json deleteTestItem(String testpath){
		Json j= new Json();
		File file=new File(testpath);
		
		try {
			if(file.exists() && file.isFile()){
				String content = FileUtils.readFileToString(file);
				String dir=content.split(SeperatorDefinition.testInfoSeperator)[0];
				String historyf=content.split(SeperatorDefinition.testInfoSeperator)[1];
				File history=new File(dir);
				history=new File(file,historyf);
				history.delete();
				file.delete();
				j.setSuccess(true);
			}
		} catch (IOException e) {
			j.setSuccess(false);
			j.setMsg("获取测试记录失败,"+e.getClass().toString()+": "+e.getMessage());
		}
		return j;
	}
	
	
	private String getRecentTest(File dir){
		String[] list = dir.list();
		if(list.length>0)
			return Collections.max(Arrays.asList(list));
		else
			return "";
	}
	
	private String getRecentTest(String[] list){
		if(list.length>0)
			return Collections.max(Arrays.asList(list));
		else
			return "";
	}
	
	private List<BatchTestItem> getDefaultTestItems(String dirPath){
		List<BatchTestItem> row=new ArrayList<BatchTestItem>();
		List<String> testsinfo=new ArrayList<String>();
		returnTestsInfoUnderSpecificDir(dirPath,testsinfo);
		for(String info : testsinfo){
			String[] arr=info.split(SeperatorDefinition.seperator);
			if(arr.length==2){
				BatchTestItem bti = new BatchTestItem();
				bti.setTestpath(arr[0]);
				bti.setName(arr[1]);
				bti.setStatus(TestStatus.ready);
				row.add(bti);
			}
		}
		return row;
	}
	
	public void returnTestsInfoUnderSpecificDir(String dirPath,List<String> testPaths){
		MyFileUtils.makeDir(dirPath+"/"+BatchTestingFolderName.folderName);
		if(dirPath.endsWith("-dir"))
			getTestsInfoUnderSpecificDir(dirPath,testPaths);
		else
			getTestsInfoUnderSpecificRunningSet(dirPath,testPaths);
	}
	
	private void getTestsInfoUnderSpecificDir(String dirPath,List<String> list){
		File dir=new File(dirPath);
		String folder[] = dir.list();
		for (String f : folder) {
			String childpath=dirPath.replace("\\", "/")+"/"+f;
            if(f.endsWith("-dir")){
            	getTestsInfoUnderSpecificDir(childpath, list);
            }
            else if(f.endsWith("-leaf")){
            	String filename=f.substring(0, f.length()-5);
            	list.add(childpath+SeperatorDefinition.seperator+filename);
            }
        }
	}
	
	private void getTestsInfoUnderSpecificRunningSet(String runningSetPath,List<String> list){
		if(!runningSetPath.startsWith("root/"+LabFolderName.folder+"/"))
			runningSetPath="root/"+LabFolderName.folder+"/"+runningSetPath;
		String[] tests=labEnvironmentService.getAllTestPathInRunningSet(runningSetPath);
		for(String testpath : tests){
			String[] arr=testpath.split("/");
			String filename=arr[arr.length-1];
			if(filename.endsWith("-leaf")){
				String testname=filename.substring(0, filename.length()-5);
				list.add(testpath+SeperatorDefinition.seperator+testname);
			}
		}
	}
}
