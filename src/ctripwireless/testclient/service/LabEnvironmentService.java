package ctripwireless.testclient.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ctripwireless.testclient.enums.BatchTestingFolderName;
import ctripwireless.testclient.enums.LabFolderName;
import ctripwireless.testclient.enums.SeperatorDefinition;
import ctripwireless.testclient.enums.TestSetFileName;
import ctripwireless.testclient.enums.TimeFormatDefiniation;
import ctripwireless.testclient.httpmodel.DataGridJson;
import ctripwireless.testclient.httpmodel.Json;
import ctripwireless.testclient.httpmodel.RunningSet;
import ctripwireless.testclient.utils.MyFileUtils;

@Service("labEnvironmentService")
public class LabEnvironmentService {
	private static final Logger logger = Logger.getLogger(LabEnvironmentService.class);
	
	public Json addRunningSet(String name, String testset){	
		Json j = new Json();
		try {
			String author=SecurityContextHolder.getContext().getAuthentication().getName();
			SimpleDateFormat format = new SimpleDateFormat(TimeFormatDefiniation.runningSet);			
			String time=format.format(new Date());
			String filename=name+SeperatorDefinition.seperator+time+SeperatorDefinition.seperator+author;
			MyFileUtils.makeDir("root/"+LabFolderName.folder);
			MyFileUtils.makeDir("root/"+LabFolderName.folder+"/"+filename);
			File f=new File("root/"+LabFolderName.folder,filename);
			f=new File(f,TestSetFileName.TestSet);
			f.createNewFile();
			FileUtils.writeStringToFile(f, testset);
			j.setSuccess(true);
		} catch (Exception e) {
			j.setMsg(e.getClass()+": "+e.getMessage());
			j.setSuccess(false);
		}
		return j;
	} 
	
	public Json modifyRunningSet(String runningset, String name, String testset,boolean isDeletion){	
		Json j = new Json();
		try {
			File f=new File("root/"+LabFolderName.folder+"/"+runningset);
			if(f.exists() && f.isDirectory()){
				if(isDeletion){
					File folder=new File(f,BatchTestingFolderName.folderName);
					if(folder.exists()){
						MyFileUtils.deleteDirectory(folder.getPath());
					}
				}
				File ts=new File(f,TestSetFileName.TestSet);
				FileUtils.writeStringToFile(ts, testset);
				String author=SecurityContextHolder.getContext().getAuthentication().getName();
				SimpleDateFormat format = new SimpleDateFormat(TimeFormatDefiniation.runningSet);			
				String time=format.format(new Date());
				String filename=name+SeperatorDefinition.seperator+time+SeperatorDefinition.seperator+author;
				File updateddir=new File("root/"+LabFolderName.folder+"/"+filename);
				f.renameTo(updateddir);
				j.setSuccess(true);
			}else{
				j.setMsg("没有找到该运行集！");
				j.setSuccess(false);
			}
		} catch (Exception e) {
			j.setMsg(e.getClass()+": "+e.getMessage());
			j.setSuccess(false);
		}
		return j;
	} 
	
	public DataGridJson getAllRunningSet(){
		DataGridJson j = new DataGridJson();
		List<RunningSet> row=new ArrayList<RunningSet>();
		File folder=new File("root",LabFolderName.folder);
		if(folder.exists()){
			String[] children=folder.list();
			for(String runset : children){
				String[] fields=runset.split(SeperatorDefinition.seperator);
				row.add(new RunningSet(fields[0],fields[1],fields[2]));
			}
			j.setRows(row);
		}
		j.setTotal(row.size());
		return j;	
	}
	
	public void deleteRunningSet(String foldername){
		File folder=new File("root/"+LabFolderName.folder+"/"+foldername);
		if(folder.exists()){
			MyFileUtils.deleteDirectory("root/"+LabFolderName.folder+"/"+foldername);
		}
	}
	
	public String[] getAllTestPathInRunningSet(String runningSetPath){
		String content="";
		File f=new File(runningSetPath);
		if(f.exists()){
			f=new File(f,TestSetFileName.TestSet);
			if(f.exists() && f.isFile()){
				try {
					content=FileUtils.readFileToString(f);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("读文件异常，"+e.getClass()+e.getMessage());
				}
			}
		}
		return content.split(SeperatorDefinition.seperator);
	}
	
	public List<String> getNotExistingTestPaths(String runningSetPath){
		List<String> list = new ArrayList<String>();
		String[] testpaths=getAllTestPathInRunningSet(runningSetPath);
		for(String path : testpaths){
			if(!new File(path).exists())
				list.add(path);
		}
		return list;
	}
	
}
