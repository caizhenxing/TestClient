package ctripwireless.testclient.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ctripwireless.testclient.enums.SeperatorDefinition;
import ctripwireless.testclient.enums.Suffix4Deleted;
import ctripwireless.testclient.httpmodel.Json;
import ctripwireless.testclient.service.TreeNodeService;
import ctripwireless.testclient.utils.MyFileUtils;

@Controller
public class TreeController {
	private static final Logger logger = Logger.getLogger(TreeController.class);
	@Autowired
	TreeNodeService treeNodeService;
	
	@RequestMapping(value="/copyNodeWithoutHistory", method=RequestMethod.POST )
	@ResponseBody
	public Json copyNodeWithoutHistory(@RequestParam("srcPath")String srcPath, @RequestParam("targetPath")String targetPath) {
		Json j=new Json();
		try{
			MyFileUtils.copyTestWithoutHistory(srcPath, targetPath);
			j.setSuccess(true);
		}
		catch(IOException ioe){
			j.setSuccess(false);
			logger.error(ioe.getClass().toString()+": "+ioe.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="/copyNode", method=RequestMethod.POST )
	@ResponseBody
	public Json copyNode(@RequestParam("srcPath")String srcPath, @RequestParam("targetPath")String targetPath) {
		Json j=new Json();
		try{
			MyFileUtils.copyArtifact(srcPath, targetPath);
			j.setSuccess(true);
		}
		catch(IOException ioe){
			j.setSuccess(false);
			logger.error(ioe.getClass().toString()+": "+ioe.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="/batchCopyTest", method=RequestMethod.POST )
	@ResponseBody
	public Json batchCopyTest(@RequestParam String testPath, @RequestParam int number) {
		Json j=new Json();
		try{
			MyFileUtils.copyMultipleTestsAtSameDir(testPath, number);
			j.setSuccess(true);
		}
		catch(IOException ioe){
			j.setSuccess(false);
			logger.error(ioe.getClass().toString()+": "+ioe.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="/addNode", method=RequestMethod.POST )
	@ResponseBody
	public Json addNode(@RequestBody Map<String,Object> nodeitem,@RequestParam("folderName")String  foldername) {
		Json j=new Json();
		File f=new File(foldername);
		System.out.println(f.getAbsolutePath());
		MyFileUtils.makeDir(foldername);
		j.setSuccess(true);
		return j;
	}
	
	@RequestMapping(value="/delNode", method=RequestMethod.POST )
	@ResponseBody
	public Json delNode(@RequestBody Map<String,Object> nodeitem,@RequestParam("folderName")String  foldername) {
		Json j=new Json();
		try {
			FileUtils.forceDelete(new File(foldername));
			j.setSuccess(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("delNode 失败", e);
			j.setSuccess(false);
			j.setMsg("删除节点失败");
		}
		return j;
	}
	
	@RequestMapping(value="/logicalDelNode", method=RequestMethod.POST )
	@ResponseBody
	public Json logicalDelNode(@RequestParam("nodePath")String nodePath) {
		Json j=new Json();
		try {
			String renamedPath=nodePath+Suffix4Deleted.deleted;
			boolean isSuccess = treeNodeService.renameDirectory(nodePath, renamedPath);
			j.setSuccess(isSuccess);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("逻辑删除节点失败", e);
			j.setSuccess(false);
			j.setMsg("逻辑删除节点失败");
		}
		return j;
	}
	
	@RequestMapping(value="/modifyNode", method=RequestMethod.POST )
	@ResponseBody
	public Json modifyNode(@RequestBody Map<String,Object> nodeitem,@RequestParam("folderName")String  foldername,@RequestParam("oldFolderName")String  oldFoldername) {
		System.out.println("modifyNode");
		System.out.println(oldFoldername);
		Json j=new Json();
		MyFileUtils.makeDir(oldFoldername);
		File path=new File(oldFoldername);
		path.renameTo(new File(foldername));
		j.setSuccess(true);
		return j;
		
	}

	@RequestMapping(value="/gettree")
	@ResponseBody
	public List getTree() {
		return treeNodeService.getTree(false);
	}
	
	@RequestMapping(value="/getSelectingTree")
	@ResponseBody
	public List getSelectingTree() {
		return treeNodeService.getTree(true);
	}
	
	@RequestMapping(value="/getSelectedTree")
	@ResponseBody
	public List getSelectedTree(@RequestParam("testset") String[] testset) {
		return treeNodeService.getCheckedTree(testset);
	}
	
	@RequestMapping(value="/getNodeIdByText", method=RequestMethod.POST )
	@ResponseBody
	public String getNodeIdByText(@RequestParam String text,@RequestParam boolean isTest){
		List<String> nodes=treeNodeService.getNodes(">");
		for(String node : nodes){
			String[] arr=node.split(">");
			String name=arr[arr.length-1].toUpperCase();
			if(name.contains(text.toUpperCase())){
				if(isTest){
					return name.endsWith("-LEAF") ? node : "";
				}else
					return name.endsWith("-DIR") ? node : "";
			}
		}
		return "";
	}
}
