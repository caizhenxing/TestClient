package ctripwireless.testclient.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import ctripwireless.testclient.enums.Suffix4Deleted;
import ctripwireless.testclient.utils.MyFileUtils;

@Service("treeNodeService")
public class TreeNodeService {
	public List getTree(boolean containsCheckbox) {
		List treeList = new ArrayList<Map<String, Object>>();
		MyFileUtils.makeDir("root");
		File root=new File("root");
		File list[]=root.listFiles();
		for(File f:list){
			getTree(treeList,f,"root",containsCheckbox);
		}
		return treeList;
	}
	
	public List getCheckedTree(String[] checkedTests) {
		List treeList = new ArrayList<Map<String, Object>>();
		MyFileUtils.makeDir("root");
		File root=new File("root");
		File list[]=root.listFiles();
		for(File f:list){
			getCheckedTree(treeList,f,"root",checkedTests);
		}
		return treeList;
	}
	
	private void getTree(List treeList,File f,String fathername,boolean containsCheckbox){
		if(f.isFile())
			return;
		Map node = new HashMap();
		String fname=f.getName();
		if(fname.endsWith("-dir") || fname.endsWith("-leaf")){
			String folderName=fathername+"/"+fname;
			node.put("id", folderName.replaceAll("/", ">"));
			node.put("folderName", folderName);
			if(containsCheckbox)
				node.put("checked", false);
			if(fname.endsWith("-dir")){
				//目录逻辑
				boolean isleaf=false;
				String text=StringUtils.substringBeforeLast(f.getName(), "-dir");
				node.put("text", text);
				node.put("folderName", folderName);
				node.put("leaf", isleaf);
				List subtreeList = new ArrayList<Map<String, Object>>();
				node.put("children", subtreeList);
				treeList.add(node);
				File list[]=f.listFiles();
				for(File ff:list){
					getTree(subtreeList,ff,folderName,containsCheckbox);
				}
			}
			else if(f.getName().endsWith("-leaf")){
				//节点逻辑
				boolean isleaf=true;
				String text=StringUtils.substringBeforeLast(f.getName(), "-leaf");
				node.put("text", text);
				node.put("leaf", isleaf);
				treeList.add(node);
			}
		}
	}
	
	private void getCheckedTree(List treeList,File f,String fathername,String[] checkedTests){
		if(f.isFile())
			return;
		String fname=f.getName();
		if(fname.endsWith("-dir") || fname.endsWith("-leaf")){
			Map node = new HashMap();
			String folderName=fathername+"/"+fname;
			if(StringUtils.join(checkedTests,"\n").contains(folderName)){
				node.put("expanded", true);
			}else
				node.put("expanded", false);
			node.put("id", folderName.replaceAll("/", ">"));
			node.put("folderName", folderName);
			if(fname.endsWith("-dir")){
				//目录逻辑
				String text=StringUtils.substringBeforeLast(fname, "-dir");
				node.put("text", text);
				node.put("folderName", folderName);
				node.put("leaf", false);
				List subtreeList = new ArrayList<Map<String, Object>>();
				node.put("children", subtreeList);
				node.put("checked", false);
				treeList.add(node);
				File list[]=f.listFiles();
				for(File ff:list){
					getCheckedTree(subtreeList,ff,folderName,checkedTests);
				}
			}
			else if(fname.endsWith("-leaf")){
				boolean isSelected=Arrays.asList(checkedTests).contains(folderName)?true:false;
				node.put("checked", isSelected);
				//节点逻辑
				String text=StringUtils.substringBeforeLast(fname, "-leaf");
				node.put("text", text);
				node.put("leaf", true);
				node.put("expanded", false);
				
				treeList.add(node);
			}
		}
	}
	
	public List<String> getNodes(String folderSeperator) {
		List<String> treeList = new ArrayList<String>();
		MyFileUtils.makeDir("root");
		File root=new File("root");
		treeList.add("root");
		File list[]=root.listFiles();
		for(File f:list){
			getNodes(treeList,f,"root",folderSeperator);
		}
		return treeList;
	}
	
	private void getNodes(List<String> treeList,File f,String parentName,String folderSeperator){
		if(f.isFile()){
			return;
		}
		String nodeName=f.getName();
		if(nodeName.endsWith("-dir") || nodeName.endsWith("-leaf")){
			parentName=parentName+folderSeperator+nodeName;
			treeList.add(parentName);
			for(File child:f.listFiles()){
				getNodes(treeList,child,parentName,folderSeperator);
			}
		}
	}
	
	public boolean renameDirectory(String fromDir, String toDir) {
		File from = new File(fromDir);
		if (from.exists() && from.isDirectory()) {
			File to = new File(toDir);				
			//Rename
			if(to.exists() && to.isDirectory()){
				to = new File(toDir+Suffix4Deleted.deleted);
			}
			if (from.renameTo(to))
				return true;
			else
				return false;
		}else
			return false;
	}
}
