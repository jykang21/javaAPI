package kr.co.softbridge.sobroplatform.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;

import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.jsonwebtoken.io.IOException;

public class FileUtils {
	//
	private static FileUtils		fH					= new FileUtils();
	//
	/*************************************************************************************************************
	* @brief  :
	* @method : Constructor
	* @author : 조용선
	**************************************************************************************************************/
	public FileUtils() {}
	//
	/*************************************************************************************************************
	* @brief  :
	* @method : getInstance
	* @author : 조용선
	**************************************************************************************************************/
	public static FileUtils getInstance() {
		return fH;
	}
	//
	/*************************************************************************************************************
	* @brief  : 파일 유무 확인
	* @method : isFileExist
	* @arg0   : fileDirPath - 읽기 파일 FULL PATH
	* @author : 조용선
	**************************************************************************************************************/
	public boolean isFileExist(String fileFullPath) throws Exception {
		//
		boolean			fileExist					= true;
		//
		try{
			//
			File			targetFile					= new File(fileFullPath);
			//
			fileExist		= targetFile.exists();
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//
		return fileExist;
		//
	}
	//
	/*************************************************************************************************************
	* @brief  : 파일 크기 조회
	* @method : getFileSizeInt
	* @arg0   : fileDirPath - 읽기 파일 FULL PATH
	* @author : 조용선
	**************************************************************************************************************/
	public int getFileSizeInt(String fileFullPath) throws Exception {
		//
		int				fileSize					= 0;
		//
		try{
			//
			File			targetFile					= new File(fileFullPath);
			//
			fileSize		= Integer.parseInt(Long.toString(targetFile.length()));
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//
		return fileSize;
		//
	}
	//
	/*************************************************************************************************************
	* @brief  : 파일 크기 조회
	* @method : getFileSizeLong
	* @arg0   : fileDirPath - 읽기 파일 FULL PATH
	* @author : 조용선
	**************************************************************************************************************/
	public long getFileSizeLong(String fileFullPath) throws Exception {
		//
		long			fileSize					= 0;
		//
		try{
			//
			File			targetFile					= new File(fileFullPath);
			//
			fileSize		= targetFile.length();
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//
		return fileSize;
		//
	}
	//
	/*************************************************************************************************************
	* @brief  : 파일경로 조회
	* @method : getFilePath
	* @arg0   : filePath
	* @author : 조용선
	**************************************************************************************************************/
	public String getFilePath(String fileFullPath) throws Exception {
		//
		String			filePath					= "";
		//
		try{
			//
			filePath		= this.getRegexFilePath(fileFullPath, 1);
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//
		return filePath;
		//
	}	
	//
	/*************************************************************************************************************
	* @brief  : 파일명 조회
	* @method : getFileName
	* @arg0   : fileNm
	* @author : 조용선
	**************************************************************************************************************/
	public String getFileName(String fileFullPath) throws Exception {
		//
		String			fileName					= "";
		//
		try{
			//
			fileName		= this.getRegexFilePath(fileFullPath, 3);
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//
		return fileName;
		//
	}		
	//
	/*************************************************************************************************************
	* @brief  : 파일 확장자 조회
	* @method : getFileExt
	* @arg0   : fileNm - 파일명
	* @author : 조용선
	**************************************************************************************************************/
	public String getFileExt(String fileNm) throws Exception {
		//
		String			fileExt						= "";
		//
		try{
			//
			fileExt			= this.getRegexFilePath(fileNm, 5);
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//
		return fileExt;
		//
	}
	//
	/*************************************************************************************************************
	* @brief  : 파일 읽기
	* @method : readFile
	* @arg0   : fileDirPath - 읽기 파일 FULL PATH
	* @author : 조용선
	**************************************************************************************************************/
	public String readFile(String fileFullPath, String fileEnc) throws Exception {
		//
		String				readFileStr					= "";
		//
		FileInputStream		readFileFIS					= null;
		InputStreamReader	readFileISR					= null;
		//
		try{
			//
			File				targetFile					= new File(fileFullPath);
			//
			if(targetFile.exists()){
				//
				readFileFIS			= new FileInputStream(targetFile);
				if(fileEnc!=null && !fileEnc.equals("")){
					readFileISR			= new InputStreamReader(readFileFIS, fileEnc);
				}else{
					readFileISR			= new InputStreamReader(readFileFIS);
				}
				//
				StringBuffer		readFileSB					= new StringBuffer();
				//
				char[]				readFileBUF					= new char[1024];
				//
				int					readInLen					= 0;
				//
				while((readInLen = readFileISR.read(readFileBUF)) != -1 ){
					//
					readFileSB.append(readFileBUF,0,readInLen);
					//
				}
				//
				//readFileStr			= new String((readFileSB.toString()).getBytes());
				readFileStr			= readFileSB.toString();
				//
			}
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(readFileISR != null){	readFileISR.close();	}
				if(readFileFIS != null){	readFileFIS.close();	}
			}catch(Exception ex){}
		}
		//
		return readFileStr;
		//
	}
	//
	public String readFileNoEnc(String fileFullPath) throws Exception {
		//
		String				readFileStr					= "";
		//
		try{
			//
			readFileStr			= this.readFile(fileFullPath, "");
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//
		return readFileStr;
		//
	}
	public String[] readFileToArray(String fileFullPath, String fileEnc) throws Exception {
		//
		String[]			readFileStrArray			= null;
		//
		try{
			//
			String				readFileStr					= "";
			//
			readFileStr			= this.readFile(fileFullPath, fileEnc);
			readFileStr			= readFileStr.replaceAll("\r", "");
			//
			readFileStrArray	= readFileStr.split("\n");
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//
		return readFileStrArray;
		//
	}
	public String[] readFileToArrayNoEnc(String fileFullPath) throws Exception {
		//
		String[]			readFileStrArray			= null;
		//
		try{
			//
			readFileStrArray	= this.readFileToArray(fileFullPath, "");
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//
		return readFileStrArray;
		//
	}
	public ArrayList readFileToList(String fileFullPath, String fileEnc) throws Exception {
		//
		ArrayList			readFileStrList				= new ArrayList();
		//
		try{
			//
			String[]			readFileStrArray			= null;
			//
			readFileStrArray	= this.readFileToArray(fileFullPath, fileEnc);
			//
			for(int readStrIdx=0 ; readStrIdx<readFileStrArray.length ; readStrIdx++){
				readFileStrList.add(readFileStrArray[readStrIdx]);
			}
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//
		return readFileStrList;
		//
	}
	//
	public ArrayList readFileToListNoEnc(String fileFullPath) throws Exception {
		//
		ArrayList			readFileStrList				= new ArrayList();
		//
		try{
			//
			readFileStrList		= this.readFileToList(fileFullPath, "");
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//
		return readFileStrList;
		//
	}
	//
	/*************************************************************************************************************
	* @brief  : 파일 읽기
	* @method : readFileStrList
	* @arg0   : fileDirPath - 읽기 파일 FULL PATH
	* @author : 조용선
	**************************************************************************************************************/
	public ArrayList readFileStrList(String fileFullPath, String fileEnc, String strEnc) throws Exception {
		//
		ArrayList			readFileStrList				= new ArrayList(); 
		//
		FileInputStream		readFileFIS					= null;
		InputStreamReader	readFileISR					= null;
		//
		try{
			//
			String[]			readStrSplit				= null;
			String				readFileStr					= "";
			int					readFilePos					= 0;
			//
			File				targetFile					= new File(fileFullPath);
			//
			if(targetFile.exists()){
				//
				readFileFIS			= new FileInputStream(targetFile);
				readFileISR			= new InputStreamReader(readFileFIS, fileEnc);
				//
				StringBuffer		readFileSB					= new StringBuffer();
				//
				char[]				readFileBUF					= new char[1024];
				//
				int					readInLen					= 0;
				//
				while((readInLen = readFileISR.read(readFileBUF)) != -1 ){
					//
					readFileSB.append(readFileBUF,0,readInLen);
					//
					readFilePos			= readFilePos + 1;
					//
					if(readFilePos%100 == 0){
						//
						readFileStr			= readFileSB.toString();
						readStrSplit		= readFileStr.split("\n");
						//
						readFilePos			= 0;
						//
						readFileSB			= new StringBuffer();
						//
						for(int readStrIdx=0 ; readStrIdx<readStrSplit.length ; readStrIdx++){
							//
							if(readStrIdx == readStrSplit.length-1){
								readFileSB.append(readStrSplit[readStrIdx]);
							}else{
								readFileStrList.add(readStrSplit[readStrIdx]);
							}
							//
						}
						//
					}
					//
				}
				//
				readFileStr			= readFileSB.toString();
				readStrSplit		= readFileStr.split("\n");
				//
				for(int readStrIdx=0 ; readStrIdx<readStrSplit.length ; readStrIdx++){
					readFileStrList.add(readStrSplit[readStrIdx]);
				}
				//
			}
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(readFileFIS != null){	readFileFIS.close();	}
				if(readFileISR != null){	readFileISR.close();	}
			}catch(Exception ex){}
		}
		//
		return readFileStrList;
		//
	}
	//
	/*************************************************************************************************************
	* @brief  : 파일 쓰기
	* @method : writeFile
	* @arg0   : fileDirPath - 쓰기 파일 FULL PATH
	* @arg0   : writeStr - 쓰기 문자열
	* @arg0   : fileEnc - 파일 인코딩
	* @author : 조용선
	**************************************************************************************************************/
	public void writeFile(String fileFullPath, String writeStr, String fileEnc) throws Exception {
		//
		FileOutputStream	writeFileFOS				= null;
		OutputStreamWriter	writeFileOSW				= null;
		//
		try{
			//
			File				writeFile					= new File(fileFullPath);
			//
			if(!writeFile.exists()){
				writeFile.createNewFile();
			}
			//
			writeFileFOS		= new FileOutputStream(writeFile, true);
			writeFileOSW		= new OutputStreamWriter(writeFileFOS, fileEnc);
			//
			writeFileOSW.write(writeStr);
			writeFileOSW.flush();
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(writeFileFOS != null){	writeFileFOS.close();	}
				if(writeFileOSW != null){	writeFileOSW.close();	}
			}catch(Exception ex){}
			//
		}
		//
	}
	//
	/*************************************************************************************************************
	* @brief  : 파일 쓰기
	* @method : writeFile
	* @arg0   : fileDirPath - 쓰기 파일 FULL PATH
	* @arg0   : writeStr - 쓰기 문자열
	* @arg0   : fileEnc - 파일 인코딩
	* @author : 조용선
	**************************************************************************************************************/
	public void writeFileNoEnc(String fileFullPath, String writeStr) throws Exception {
		//
		FileOutputStream	writeFileFOS				= null;
		OutputStreamWriter	writeFileOSW				= null;
		//
		try{
			//
			File				writeFile					= new File(fileFullPath);
			//
			if(!writeFile.exists()){
				writeFile.createNewFile();
			}
			//
			writeFileFOS		= new FileOutputStream(writeFile, true);
			writeFileOSW		= new OutputStreamWriter(writeFileFOS);
			//
			writeFileOSW.write(writeStr);
			writeFileOSW.flush();
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(writeFileOSW != null){	writeFileOSW.close();	}
				if(writeFileFOS != null){	writeFileFOS.close();	}
			}catch(Exception ex){}
			//
		}
		//
	}
	//
	/*************************************************************************************************************
	* @brief  : 파일 복사
	* @method : copyFile
	* @author : 조용선
	**************************************************************************************************************/
	public void copyFile(String tarFilePath, String copyFilePath) throws Exception {
		//
		FileInputStream			tarFileFIS					= null;
		FileOutputStream		copyFileFOS					= null;
		//
		try{
			//
			tarFileFIS			= new FileInputStream(new File(tarFilePath));
			copyFileFOS			= new FileOutputStream(new File(copyFilePath));
			//
			byte[]				readFileBUF					= new byte[1024];
			//
			int					readInLen					= 0;
			//
			while((readInLen = tarFileFIS.read(readFileBUF)) > 0 ){
				copyFileFOS.write(readFileBUF, 0, readInLen);
			}
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(tarFileFIS != null) {
				try {
					tarFileFIS.close();
				}catch (IOException ie) {
					ie.printStackTrace();
				}
			}
			
			if(copyFileFOS != null) {
				try {
					copyFileFOS.close();
				}catch (IOException ie) {
					ie.printStackTrace();
				}
			}
		}
		//
	}
	//
	/*************************************************************************************************************
	* @brief  : 파일 이동
	* @method : moveFile
	* @arg0   : targetFileFullPath - 대상 파일 FULL PATH
	* @arg1   : moveFileFullPath - 이동 파일 FULL PATH
	* @author : 조용선
	**************************************************************************************************************/
	public boolean moveFile(String targetFileFullPath, String moveFileFullPath) throws Exception {
		//
		boolean		resultCd				= false;
		//
		try{
			//
			File		targetFile				= new File(targetFileFullPath);
			File		moveFile				= new File(moveFileFullPath);
			//
			resultCd	= targetFile.renameTo(moveFile);
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//
		return resultCd;
		//
	}
	//
	/*************************************************************************************************************
	* @brief  : 임시폴더 생성
	* @method : makeTempDir
	* @arg0   : fileExt
	* @author : 조용선
	**************************************************************************************************************/
	public String makeTempDir(String fileMainPath) throws Exception {
		//
		String			makeTempDirPath				= "";
		//
		try{
			//
			String			vmId						= new java.rmi.dgc.VMID().toString();
			// String			uniqueId					= (new sun.misc.BASE64Encoder().encode(vmId.getBytes())).replaceAll("=", "");
			//
			makeTempDirPath			= fileMainPath + "/";
			//
			makeTempDirPath			= makeTempDirPath.replaceAll("////", "//");
			//
			this.makeDir(makeTempDirPath);
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//
		return makeTempDirPath;
		//
	}
	//
	/*************************************************************************************************************
	* @brief  : 임시파일명 생성
	* @method : makeTempFileName
	* @arg0   : fileExt
	* @author : 조용선
	**************************************************************************************************************/
	public String makeTempFileName(String fileExt) throws Exception {
		//
		String			tempFileName				= null;
		//
		try{
			//
			String			vmId						= new java.rmi.dgc.VMID().toString();
			// String			uniqueId					= (new sun.misc.BASE64Encoder().encode(vmId.getBytes())).replaceAll("=", "");
			//
			if(fileExt!=null && fileExt.length()>0){
				tempFileName	= (vmId + "." + fileExt).toLowerCase();
			}else{
				tempFileName	= (vmId).toLowerCase();
			}
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//
		return tempFileName;
		//
	}
	//
	/*************************************************************************************************************
	* @brief  : 폴더 생성
	* @method : makeDir
	* @arg0   : dirPath
	* @author : 조용선
	**************************************************************************************************************/
	public String makeDirRePath(String dirPath) throws Exception {
		//
		String			makeDirPath					= "";
		//
		try{
			//
			boolean			createCheck					= true;
			//
			makeDirPath		= dirPath;
			//
			while(makeDirPath.indexOf("//")>-1){
				makeDirPath		= makeDirPath.replaceAll("//", "/");
			}
			//
			createCheck		= this.makeDir(makeDirPath);
			//
			if(createCheck == false){
				makeDirPath		= "";
			}
			//
		}catch(Exception ex){
			makeDirPath		= "";
			ex.printStackTrace();
		}
		//
		return makeDirPath;
		//
	}
	public boolean makeDir(String dirPath) throws Exception {
		//
		boolean			dirCreate					= true;
		//
		try{
			//
			while(dirPath.indexOf("//")>-1){
				dirPath			= dirPath.replaceAll("//", "/");
			}
			//
			File			targetDir					= new File(dirPath);
			//
			if(!targetDir.exists()){
				targetDir.mkdirs();
			}
			//
		}catch(Exception ex){
			dirCreate		= false;
			ex.printStackTrace();
		}
		//
		return dirCreate;
		//
	}
	//
	/*************************************************************************************************************
	* @brief  : 파일 생성
	* @method : makeNewFile
	* @arg0   : fileFullPath
	* @author : 조용선
	**************************************************************************************************************/
	public boolean makeNewFile(String fileFullPath) throws Exception {
		//
		boolean			fileCreate					= true;
		//
		try{
			//
			File			targetFile					= new File(fileFullPath);
			//
			this.makeDir(targetFile.getParent());
			//
			if(!targetFile.exists()){
				targetFile.createNewFile();
			}
			//
		}catch(Exception ex){
			fileCreate		= false;
			ex.printStackTrace();
		}
		//
		return fileCreate;
		//
	}
	//
	/*************************************************************************************************************
	* @brief  : 파일 삭제
	* @method : delFile
	* @arg0   : fileDirPath - 삭제 파일 FULL PATH
	* @author : 조용선
	**************************************************************************************************************/
	public void delFile(String fileFullPath) throws Exception {
		//
		try{
			//
			File			targetFile					= new File(fileFullPath);
			//
			if(targetFile.exists()){
				targetFile.delete();
			}
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//
	}
	//
	/*************************************************************************************************************
	* @brief  : 디렉토리 삭제
	* @method : delDir
	* @arg0   : fileDirPath - 삭제 디렉토리 PATH
	* @author : 조용선
	**************************************************************************************************************/
	public void delDir(String fileDirPath) throws Exception {
		//
		try{
			//
			File			targetDir					= new File(fileDirPath);
			//
			if(targetDir.exists()){
				//
				if(targetDir.isDirectory()) {
					//
					String			targetFileList[]			= targetDir.list();
					//
					for(int targetFileListIdx=0 ; targetFileListIdx<targetFileList.length ; targetFileListIdx++) {
						//
						File			targetFile					= new File(fileDirPath, targetFileList[targetFileListIdx]);
						//
						if(targetFile.isDirectory()){
							//
							String			subDirPath					= fileDirPath + "/" + targetFileList[targetFileListIdx];
							//
							this.delDir(subDirPath);
							//
						}else{
							//
							targetFile.delete();
							//
						}
						//
					}
					//
					targetDir.delete();
					//
				}else if(targetDir.isFile()){
					//
					targetDir.delete();
					//
				}
				//
			}
			//
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//
	}
	//
	/*************************************************************************************************************
	* @brief  : 파일 PATH 분석
	* @method : getRegexFilePath
	* @arg0   : fileFullpath - 대상 파일 FULL PATH
	* @arg1   : regexIndex - 분석인자
	* @author : 조용선
	* 
	* regexIndex 1 : 디렉토리 (ex: "C:\Windows\regedit.exe" -> "C:\Windows\" )
	* regexIndex 2 : 마지막 구분자 (ex: "C:\Windows\regedit.exe" -> "\" )
	* regexIndex 3 : 파일명 (ex: "C:\Windows\regedit.exe" -> "regedit.exe" )
	* regexIndex 4 : 확장자 없는 파일명 (ex: "C:\Windows\regedit" -> "regedit" )
	* regexIndex 5 : 확장자 (ex: "C:\Windows\regedit.exe" -> "exe" )
	**************************************************************************************************************/
	public String getRegexFilePath(String fileFullpath, int regexIndex) {
		//
		String		resultStr				= null;
		//
		String		regexPatternStr			= "^(.*(/|\\\\))?(([^/\\\\]+)\\.([^/\\\\.]*)|[^/\\\\.]*)$";
		//
		Pattern		regexPattern			= Pattern.compile(regexPatternStr);
		//
		Matcher		regexMatcher			= regexPattern.matcher(fileFullpath);
		//
		if(regexMatcher.find()){
			//
			resultStr		= regexMatcher.group(regexIndex);
			//
			//확장자가 없는 파일이면 3 값을 사용
			if((regexIndex == 4) && "".equals(resultStr)){
				//
				resultStr		= regexMatcher.group(3);
				//
			}
			//
		}
		//
		return resultStr;
		//
	}
	//
}