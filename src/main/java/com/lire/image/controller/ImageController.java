package com.lire.image.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.lire.image.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.lire.image.entity.Image;
import com.lire.image.service.ImageService;
/**
image controller test
test github
*/
@Controller
public class ImageController {
	
	@Autowired
	private ImageService imageService;
	
	@RequestMapping(value="linkFindSimilarImage")
	public String linkFindSimilarImage(){
		return "find_similar_image";
	}
	
	@RequestMapping(value="findSimilarImage",method=RequestMethod.POST)
	public ModelAndView findSimilarImage(@RequestParam("doc") MultipartFile doc,
			HttpServletRequest request) throws Exception{
		System.out.println(Config.get("abc"));
		if(doc.isEmpty()){
			request.setAttribute("message", "未提交图片");
			return new ModelAndView("find_similar_image");
		}
		ServletContext sc = request.getSession().getServletContext();
		String uploadpath = sc.getRealPath("/src/upload");
		String indexpath = sc.getRealPath("/src/imgindex");
		String imageName = System.currentTimeMillis()+".png";
		File savefile = new File(new File(uploadpath),imageName);
        if (!savefile.getParentFile().exists())
            savefile.getParentFile().mkdirs();
		doc.transferTo(savefile);//save file
		List<Image> list = imageService.findSimilarImage(uploadpath+"/"+imageName, indexpath, 100);
		if(list!=null&&list.size()>0){
			request.setAttribute("similarImage", list);
		}else{
			request.setAttribute("error_message", "<hr/><p><font color='red'>未找到相关图像信息</font></p><hr/>");
		}
		return new ModelAndView("find_similar_image");
	}
	
	@RequestMapping(value="createImageIndex",method=RequestMethod.POST)
	public @ResponseBody boolean createImageIndex(HttpServletRequest request) 
			throws IOException{
		ServletContext sc = request.getSession().getServletContext();
		String imagespath = sc.getRealPath("/src/imgfiles");
		String indexpath = sc.getRealPath("/src/imgindex");
		return imageService.createImageIndex(imagespath, indexpath);
	}
	
	@RequestMapping(value="copyImages",method=RequestMethod.POST)
	public @ResponseBody int copyImages(HttpServletRequest request){
		ServletContext sc = request.getSession().getServletContext();
		String imagespath = sc.getRealPath("/src/imgfiles");
		String imgCopyFiles = sc.getRealPath("/src/imgCopyFiles");
		return imageService.copyImagesToOtherName(imagespath,imgCopyFiles);
	}
	
}
