package ctripwireless.testclient.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Service;

import ctripwireless.testclient.factory.JsonObjectMapperFactory;
import ctripwireless.testclient.httpmodel.Json;
import ctripwireless.testclient.model.HttpTarget;
import ctripwireless.testclient.model.Parameter;
import ctripwireless.testclient.utils.FileNameUtils;

@Service("buildRequestService")
public class BuildRequestService {
	private static final Logger logger = Logger.getLogger(BuildRequestService.class);
	
	public Json buildRequest(String foldername) throws Exception{
		Json j=new Json();
		
		File f=new File(FileNameUtils.getHttpTarget(foldername));
		
		if(!f.exists()){
			j.setSuccess(true);
			return j;
		}
		
		ObjectMapper mapper = JsonObjectMapperFactory.getObjectMapper();
		HttpTarget target = mapper.readValue(f, HttpTarget.class);
		
		Map<String,Parameter> pm=target.getParameters();
		j.setObj(pm);
		j.setSuccess(true);
		
		return j;
	}
	
}
