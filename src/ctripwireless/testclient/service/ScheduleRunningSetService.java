package ctripwireless.testclient.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.quartz.CronExpression;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ctripwireless.testclient.enums.JobStatus;
import ctripwireless.testclient.enums.LabFolderName;
import ctripwireless.testclient.enums.TimeFormatDefiniation;
import ctripwireless.testclient.factory.JsonObjectMapperFactory;
import ctripwireless.testclient.httpmodel.DataGridJson;
import ctripwireless.testclient.httpmodel.Json;
import ctripwireless.testclient.httpmodel.ScheduledJobItem;
import ctripwireless.testclient.httpmodel.ScheduledRunningSet;
import ctripwireless.testclient.model.ScheduledRunningSetContainer;
import ctripwireless.testclient.quartz.QuartzScheduleMgrService;
import ctripwireless.testclient.quartz.ScheduledRunFolderJob;
import ctripwireless.testclient.utils.FileNameUtils;

@Service("scheduleRunningSetService")
public class ScheduleRunningSetService {
	private static final Logger logger = Logger.getLogger(ScheduleRunningSetService.class);
	@Autowired
	QuartzScheduleMgrService quartzScheduleMgrService;
	@Autowired
	BatchTestService batchTestService;
	@Autowired
	ScheduleJobService scheduleJobService;
	
	private File getScheduledRunningSetFile(String rsPath){
		if(!rsPath.startsWith("root/"+LabFolderName.folder+"/"))
			rsPath="root/"+LabFolderName.folder+"/"+rsPath;
		return new File(rsPath,FileNameUtils.getScheduledJobFile());
	}
	
	public DataGridJson getAllScheduledRunningSet(String rsPath){
		DataGridJson dgj=new DataGridJson();
		List<ScheduledRunningSet> row=new ArrayList<ScheduledRunningSet>();
		File f=getScheduledRunningSetFile(rsPath);
		if(f.exists()){
			try {
				ObjectMapper mapper = JsonObjectMapperFactory.getObjectMapper();
				ScheduledRunningSetContainer jobs = new ScheduledRunningSetContainer();
				jobs = mapper.readValue(f, ScheduledRunningSetContainer.class);
				for(Entry<String,ScheduledRunningSet> entry:jobs.getJob().entrySet()){
					ScheduledRunningSet srs=entry.getValue();
					if(isCronExpressionExpired(srs.getCronExpression())){
						srs.setStatus(srs.getStatus()+JobStatus.expired);
					}
					row.add(srs);
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		dgj.setRows(row);
		dgj.setTotal(row.size());
		return dgj;
	}
	
	public Json addScheduledRunningSet(String rsPath,ScheduledRunningSet item){
		Json j = new Json();	
		try {
			ObjectMapper mapper = JsonObjectMapperFactory.getObjectMapper();
			item.setStatus(JobStatus.active);
			ScheduledRunningSetContainer jobs=new ScheduledRunningSetContainer();
			File file=getScheduledRunningSetFile(rsPath);
			if(file.exists()){
				jobs= mapper.readValue(file, ScheduledRunningSetContainer.class);
			}else{
				file.createNewFile();
			}
			jobs.getJob().put(item.getJobName(), item);
			mapper.writeValue(file, jobs);
			j.setSuccess(true);
		} catch (IOException e) {
			j.setSuccess(false);
			j.setMsg(e.getClass().toString()+": "+e.getMessage());
		}
		return j;
	}
	
	private boolean isCronExpressionExpired(String expression){
		try {
			if(CronExpression.isValidExpression(expression)){
				CronExpression cronEx;
				cronEx = new CronExpression(expression);
				Date current = new Date();
				Date nextDate = cronEx.getNextValidTimeAfter(current);
				if(nextDate!=null){
					if(current.before(nextDate)){
						return false;
					}
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public Json addScheduledJob(String runningSetName,ScheduledRunningSet item){
		Json j = new Json();
		try{
			String expression=item.getCronExpression();
			if(!isCronExpressionExpired(expression)){
				Date current = new Date();
				SimpleDateFormat format = new SimpleDateFormat(TimeFormatDefiniation.format);			
				String jobName=format.format(current);
				Trigger trigger=new CronTriggerImpl(jobName, Scheduler.DEFAULT_GROUP, expression);
				JobDetail jobdetail=new JobDetailImpl(jobName, Scheduler.DEFAULT_GROUP, ScheduledRunFolderJob.class);
				jobdetail.getJobDataMap().put("dirPath", runningSetName);
				jobdetail.getJobDataMap().put("batchTestService", batchTestService);
				quartzScheduleMgrService.scheduleJob(jobdetail, trigger);
				item.setJobName(jobName);
				j.setObj(item);
				j.setSuccess(true);
			}else{
				j.setSuccess(false);
				j.setMsg("创建任务失败，Cron表达式非法或设定时间早于当前时间！");
			}
		}catch(Exception e){
			j.setSuccess(false);
			j.setMsg(e.getClass().toString()+": "+e.getMessage());
		}
		return j;
	}
	
	public Json deleteScheduledRunningSet(String rsPath,String jobName){
		Json j = new Json();
		try {
			ObjectMapper mapper = JsonObjectMapperFactory.getObjectMapper();
			File f=getScheduledRunningSetFile(rsPath);
			if(f.exists()){
				ScheduledRunningSetContainer jobs = mapper.readValue(f, ScheduledRunningSetContainer.class);
				jobs.getJob().remove(jobName);
				mapper.writeValue(f, jobs);
				j.setObj(jobName);
				j.setSuccess(true);
			}else{
				j.setSuccess(false);
				j.setMsg("定时文件不存在");
			}
		} catch (IOException e) {
			j.setSuccess(false);
			j.setMsg(e.getClass().toString()+": "+e.getMessage());
		}
		return j;
	}
	
	public Json deleteAllJobsUnderRunningSet(String rsPath){
		Json j = new Json();	
		try {
			String jobstr="";
			List<ScheduledRunningSet> row=getAllScheduledRunningSet(rsPath).getRows();
			for(ScheduledRunningSet rs : row){
				String jobName=rs.getJobName();
				boolean isDeleted1 = quartzScheduleMgrService.unscheduleJob(new TriggerKey(jobName));
				boolean isDeleted2 = quartzScheduleMgrService.deleteJob(jobName);
			}
			j.setSuccess(true);
		} catch (SchedulerException e) {
			j.setSuccess(false);
			j.setMsg(e.getClass().toString()+": "+e.getMessage());
		}
		return j;
	}
	
	public Json updateScheduledJob(String rsPath,ScheduledRunningSet srs){
		Json j = new Json();
		String jobname=srs.getJobName();
		j = scheduleJobService.deleteScheduledJob(jobname);
		if(j.isSuccess()){
			j = deleteScheduledRunningSet(rsPath,jobname);
			if(j.isSuccess()){
				j = addScheduledJob(rsPath,srs);
				if(j.isSuccess()){
					ScheduledRunningSet rs =(ScheduledRunningSet)j.getObj();
					j.setObj(rs);
					j.setSuccess(true);
				}
			}
		}
		return j;
	}
	
	public void setJobStatus(String rsPath,String jobName,String status){
		try {
			ObjectMapper mapper = JsonObjectMapperFactory.getObjectMapper();
			File f=getScheduledRunningSetFile(rsPath);
			ScheduledRunningSetContainer jobs = mapper.readValue(f, ScheduledRunningSetContainer.class);
			ScheduledRunningSet item = jobs.getJob().get(jobName);
			item.setStatus(status);
			jobs.getJob().put(jobName, item);
			mapper.writeValue(f, jobs);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
	}
	
	public void setJobsStatus(String rsPath,List<String> jobNames,String status){
		try {
			ObjectMapper mapper = JsonObjectMapperFactory.getObjectMapper();
			File f=getScheduledRunningSetFile(rsPath);
			ScheduledRunningSetContainer jobs = mapper.readValue(f, ScheduledRunningSetContainer.class);
			for(String jobName : jobNames){
				ScheduledRunningSet item = jobs.getJob().get(jobName);
				item.setStatus(status);
				jobs.getJob().put(jobName, item);
			}
			mapper.writeValue(f, jobs);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
	}
	
}
