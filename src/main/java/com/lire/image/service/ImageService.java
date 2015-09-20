package com.lire.image.service;

import java.io.IOException;
import java.util.List;

import com.lire.image.entity.Image;

public interface ImageService {
	/**
	 * 查找相似图片
	 * @return 相似图片列表列表
	 */
	public List<Image> findSimilarImage(String srcImage,String indexPath,int num) throws Exception;
	/**
	 * 创建图片索引
	 * @return true or false
	 */
	public boolean createImageIndex(String imagespath,String indexpat) throws IOException ;
	
//	public int copyImagesToOtherName(String srcImagespath,String desImagespath);
}
