package com.hotelpal.service.web.controller;

import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.vo.AudioPropertyVO;
import com.hotelpal.service.common.vo.PackVO;
import com.hotelpal.service.service.ContentService;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFileFormat;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Controller
public class FileController extends BaseController {
	
	@Resource
	private ContentService contentService;
	
	@RequestMapping(value = "/image/uploadImg")
	@ResponseBody
	public PackVO<String> uploadImg(@RequestParam MultipartFile imgFile) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String url;
		try {
			IOUtils.copy(imgFile.getInputStream(), baos);
			byte[] bytes = baos.toByteArray();
//			if (bytes.length > 5 * 1024 * 1024) {
//				throw new ServiceException(ServiceException.FILE_TOO_LARGE);
//			}
			String key = imgFile.getOriginalFilename();
			url = contentService.uploadImg(imgFile.getBytes(), key);
		} catch (IOException e) {
			throw new ServiceException(ServiceException.FILE_SERVER_FAILED, e);
		}
		PackVO<String> pack = new PackVO<>();
		pack.setVo(url);
		return pack;
	}
	
	@RequestMapping(value = "/audio/uploadAudio")
	@ResponseBody
	public PackVO<AudioPropertyVO> uploadAudio(@RequestParam MultipartFile audioFile) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(audioFile.getInputStream(), baos);
			byte[] bytes = baos.toByteArray();
//			if (bytes.length > 5 * 1024 * 1024) {
//				throw new ServiceException(ServiceException.FILE_TOO_LARGE);
//			}
			CommonsMultipartFile cf = (CommonsMultipartFile) audioFile;
			DiskFileItem fi = (DiskFileItem) cf.getFileItem();
			File f = fi.getStoreLocation();
			AudioFileFormat baseFileFormat = new MpegAudioFileReader().getAudioFileFormat(f);
			Map properties = baseFileFormat.properties();
			Long duration = (Long) properties.get("duration");
			if (duration != null) {
				duration = duration / 1000000;
			}
			Long fileSize = audioFile.getSize();
			String key = audioFile.getOriginalFilename();
			String url = contentService.uploadAudio(audioFile.getBytes(), key);
			AudioPropertyVO vo = new AudioPropertyVO();
			vo.setAudioUrl(url);
			if (duration != null) {
				vo.setAudioLen(duration.intValue());
			}
			vo.setAudioSize(fileSize.intValue());
			PackVO<AudioPropertyVO> pack = new PackVO<>();
			pack.setVo(vo);
			return pack;
		} catch (Exception e) {
			throw new ServiceException(ServiceException.FILE_SERVER_FAILED, e);
		}
	}

	@RequestMapping(value = "/image/staticImg1")
	@ResponseBody
	public PackVO staticImg1(HttpServletResponse response) {
		try {
			response.sendRedirect(contentService.getStaticImgUrl(1));
		} catch (Exception ignored){}
		return new PackVO();
	}
	@RequestMapping(value = "/image/staticImg2")
	@ResponseBody
	public PackVO staticImg2(HttpServletResponse response) {
		try {
			response.sendRedirect(contentService.getStaticImgUrl(2));
		} catch (Exception ignored){}
		return new PackVO();
	}
}
