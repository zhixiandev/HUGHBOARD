package com.board.hugh.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

public class UploadFileUtils {
	private static final Logger logger = LoggerFactory.getLogger(UploadFileUtils.class);
	
	public static String uploadFiles(String uploadPath, String orginalName, byte[] fileData) throws Exception {
		UUID uuid = UUID.randomUUID(); //겹쳐지지 않는 파일명을 위한 유니크한 값 생성
		String savedName = uuid.toString() + "_" + orginalName; //원본파일 이름과 UUID 결합

		String savedPath = calcPath(uploadPath); 
		File target = new File(uploadPath + savedPath, savedName);
		
		FileCopyUtils.copy(fileData, target);
		
		String formatName = orginalName.substring(orginalName.lastIndexOf(".") + 1);
		
		String uploadedFileName = null;
		
		if (MediaUtils.getMediaType(formatName) != null) {
			uploadedFileName = makeThumbnail(uploadPath, savedPath, savedName);
		} else {
			uploadedFileName = makeIcon(uploadPath, savedPath, savedName);
		}
		
		return uploadedFileName;
	}
	
	@SuppressWarnings("unused")
	private static String calcPath(String uploadPath) {
		Calendar cal = Calendar.getInstance();
		
		String yearPath = File.separator + cal.get(Calendar.YEAR);
		String monthPath = yearPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.MONTH) + 1);
		String datePath = monthPath + File.separator + new DecimalFormat("00").format(Calendar.DATE);
		
		makeDir(uploadPath, yearPath, monthPath, datePath);
		
		logger.info(datePath);
		return datePath;
	}
	
	private static void makeDir(String uploadPath, String... paths) {
		if (new File(uploadPath + paths[paths.length - 1]).exists()) {
			return;
		}
		
		for (String path : paths) {
			File dirPath = new File(uploadPath + path);
			
			if (!dirPath.exists()) {
				dirPath.mkdir();
			}
		}
	}
	
	private static String makeThumbnail(String uploadPath, String path, String fileName) throws Exception {
		BufferedImage sourceImg = ImageIO.read(new File(uploadPath + path, fileName));
		BufferedImage destImg = Scalr.resize(sourceImg, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_HEIGHT, 100);
		
		String thumbnailName = uploadPath + path + File.separator + "s_" + fileName;
		
		File newFile = new File(thumbnailName);
		String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);
		
		ImageIO.write(destImg, formatName.toUpperCase(), newFile);
		
		return thumbnailName.substring(uploadPath.length()).replace(File.separatorChar, '/');
	}
	
	private static String makeIcon(String uploadPath, String path, String fileName) throws Exception {
		String iconName = uploadPath + path + File.separator + fileName;
		
		return iconName.substring(uploadPath.length()).replace(File.separatorChar, '/');
	}
}
