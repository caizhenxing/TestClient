package ctripwireless.testclient.quartz;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;

import ctripwireless.testclient.enums.JobStatus;
import ctripwireless.testclient.enums.LabFolderName;
import ctripwireless.testclient.enums.SeperatorDefinition;
import ctripwireless.testclient.factory.JsonObjectMapperFactory;
import ctripwireless.testclient.httpmodel.ScheduledJobItem;
import ctripwireless.testclient.httpmodel.ScheduledRunningSet;
import ctripwireless.testclient.model.JobContainer;
import ctripwireless.testclient.model.ScheduledRunningSetContainer;
import ctripwireless.testclient.service.BatchTestService;
import ctripwireless.testclient.utils.FileNameUtils;

public class ScheduledRunFolderJob implements Job {
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String path=String.valueOf(dataMap.get("dirPath"));
		((BatchTestService) dataMap.get("batchTestService")).executeBatchTest(path);
		
		String jobName=context.getJobDetail().getKey().getName();
		changeStauts2Complete(jobName,true);
	}
	
	
	private synchronized void changeStauts2Complete(String jobName,boolean isRunningSet){
		if(isRunningSet){
			changeStatus2CompleteInScheduledRSFileContainsJobName(jobName); 
		}else{
			File file=new File("root");
			file=new File(file,FileNameUtils.getScheduledJobFile());
			if(file.exists()){
				try {
					ObjectMapper mapper = JsonObjectMapperFactory.getObjectMapper();
					JobContainer jobs= mapper.readValue(file, JobContainer.class);
					ScheduledJobItem sji= jobs.getJob().get(jobName);
					String status=sji.getStatus();
					if(status.contains(JobStatus.executed)){
						int count=Integer.parseInt(status.replace(JobStatus.executed, ""))+1;
						sji.setStatus(JobStatus.executed+count);
					}
					else
						sji.setStatus(JobStatus.executed+"1");
					jobs.getJob().put(jobName, sji);
					mapper.writeValue(file, jobs);	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private synchronized void changeStatus2CompleteInScheduledRSFileContainsJobName(String jobName){
		File f=new File("root/"+LabFolderName.folder);
    	if(f.exists()){
    		for(File rs : f.listFiles()){
        		File file=new File(rs,FileNameUtils.getScheduledJobFile());
        		if(file.exists() && file.isFile()){
            		ObjectMapper mapper = JsonObjectMapperFactory.getObjectMapper();
        			try {
        				ScheduledRunningSetContainer rsjobs=mapper.readValue(file, ScheduledRunningSetContainer.class);
    					for(ScheduledRunningSet srs : rsjobs.getJob().values()){
    						if(srs.getJobName().equalsIgnoreCase(jobName)){
    							String status=srs.getStatus();
    							if(status.contains(JobStatus.executed)){
    								int count=Integer.parseInt(status.replace(JobStatus.executed, ""))+1;
    								srs.setStatus(JobStatus.executed+count);
    							}
    							else
    								srs.setStatus(JobStatus.executed+"1");
    							rsjobs.getJob().put(jobName, srs);
    							mapper.writeValue(file, rsjobs);	
    						}
    					}
        			} catch (Exception e) {
        				// TODO Auto-generated catch block
        			}
        		}
        	}
    	}
	}
	
}
