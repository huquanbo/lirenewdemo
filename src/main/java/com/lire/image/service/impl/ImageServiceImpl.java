package com.lire.image.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.stereotype.Service;

import com.lire.image.entity.Image;
import com.lire.image.service.ImageService;

import net.semanticmetadata.lire.aggregators.AbstractAggregator;
import net.semanticmetadata.lire.aggregators.BOVW;
import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.GlobalFeature;
import net.semanticmetadata.lire.imageanalysis.features.LocalFeatureExtractor;
import net.semanticmetadata.lire.imageanalysis.features.global.AutoColorCorrelogram;
import net.semanticmetadata.lire.imageanalysis.features.global.BinaryPatternsPyramid;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.imageanalysis.features.global.EdgeHistogram;
import net.semanticmetadata.lire.imageanalysis.features.global.FuzzyColorHistogram;
import net.semanticmetadata.lire.imageanalysis.features.global.FuzzyOpponentHistogram;
import net.semanticmetadata.lire.imageanalysis.features.global.JCD;
import net.semanticmetadata.lire.imageanalysis.features.global.JpegCoefficientHistogram;
import net.semanticmetadata.lire.imageanalysis.features.global.LuminanceLayout;
import net.semanticmetadata.lire.imageanalysis.features.global.PHOG;
import net.semanticmetadata.lire.imageanalysis.features.global.RotationInvariantLocalBinaryPatterns;
import net.semanticmetadata.lire.imageanalysis.features.global.SimpleColorHistogram;
import net.semanticmetadata.lire.imageanalysis.features.local.simple.SimpleExtractor;
import net.semanticmetadata.lire.imageanalysis.features.local.opencvfeatures.CvSiftExtractor;
import net.semanticmetadata.lire.imageanalysis.features.local.opencvfeatures.CvSurfExtractor;
import net.semanticmetadata.lire.indexers.parallel.ParallelIndexer;
import net.semanticmetadata.lire.searchers.GenericFastImageSearcher;
import net.semanticmetadata.lire.searchers.ImageSearchHits;
import net.semanticmetadata.lire.searchers.ImageSearcher;

import javax.imageio.ImageIO;
@Service
public class ImageServiceImpl implements ImageService{

	Class<? extends GlobalFeature> globalFeatureClass = CEDD.class;
    Class<? extends LocalFeatureExtractor> localFeatureClass = CvSurfExtractor.class;
    SimpleExtractor.KeypointDetector keypointDetector = SimpleExtractor.KeypointDetector.CVSURF;
    Class<? extends AbstractAggregator> aggregatorClass = BOVW.class;
	
	public List<Image> findSimilarImage(String srcImage, String indexPath,int num) throws Exception{
		
		List<Image> surf = findSimilarImageWithsurf(srcImage,indexPath,num);
		return surf;
	}

	private List<Image> findSimilarImageWithsurf(String srcImage, String indexPath,int num){
		List<Image> list = new ArrayList<Image>();
		indexPath += "\\common_index";
		String ip = getLocalIp();
		try {
			BufferedImage images = ImageIO.read(new FileInputStream(new File(srcImage)));
			IndexReader reader = DirectoryReader.open(new RAMDirectory(FSDirectory.open(Paths.get(indexPath)), IOContext.READONCE));
			GenericFastImageSearcher cvsurfsearcher = new GenericFastImageSearcher(num, localFeatureClass, aggregatorClass.newInstance(), 512, true, reader, indexPath + ".config");
			ImageSearchHits cvsurfhits = cvsurfsearcher.search(images, reader);
			String hitFile;
			if(cvsurfhits!=null){
			    for (int y = 0; y < cvsurfhits.length(); y++) {
			    	hitFile = reader.document(cvsurfhits.documentID(y)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
			    	hitFile = hitFile.substring(hitFile.lastIndexOf("\\imgfiles") );
			    	System.out.println(y + ". " + hitFile + " " + cvsurfhits.score(y));
			    	String[] imagepathstr = hitFile.split("\\\\");
			    	list.add(new Image(cvsurfhits.score(y), imagepathstr[imagepathstr.length-1]));
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	private String getLocalIp(){
		String ip = null;
		try {
			InetAddress addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ip;//获得本机IP 　　
	}
	private List<String> findSimilarImageWithAutoColorCorrelogram(String srcImage, String indexPath,int num){
		 List<String> list = new ArrayList<String>(); 
		 indexPath += "\\common_index";
		try {
			IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
			ImageSearcher searcher = new GenericFastImageSearcher(num, AutoColorCorrelogram.class);
		    BufferedImage images = ImageIO.read(new FileInputStream(new File(srcImage)));
		    ImageSearchHits hits = searcher.search(images, ir);
		    for (int i = 0; i < hits.length(); i++) {
		    	
	            String fileName = ir.document(hits.documentID(i)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
//	            list.add(new Image(hits.score(i), fileName));
	            list.add(fileName.substring(fileName.lastIndexOf("\\imgfiles") ));
	            System.out.println(hits.score(i) + ": \t" + fileName);
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
	     
		return list;
	}
	
	
	public boolean createImageIndex(String imagespath,String indexpath){
		 boolean passed = true;
//		 createImageIndexWithAutoColorCorrelogram(imagespath,indexpath);
//		 createImageIndexWithSurf(imagespath,indexpath);
		 //		 createImageIndexWithSift(imagespath,indexpath);
		 createImageIndexWithMultAlgorithm(imagespath,indexpath);
	     return passed;
	}

	private void createImageIndexWithAutoColorCorrelogram(String imagespath,String indexpath){
		ParallelIndexer indexer = new ParallelIndexer(DocumentBuilder.NUM_OF_THREADS, indexpath+"\\autocolor", imagespath);
		indexer.addExtractor(AutoColorCorrelogram.class);
		indexer.run();
	}
	private void createImageIndexWithSift(String imagespath,String indexpath){
		String indexDic = indexpath+"\\sift";
		File dic = new File(indexDic);
		if(!dic.exists()&&!dic.isDirectory()){
			dic.mkdir();
		}
		ParallelIndexer indexer = new ParallelIndexer(DocumentBuilder.NUM_OF_THREADS, indexDic, imagespath);
		indexer.addExtractor(CvSiftExtractor.class);
		indexer.run();
	}
	
	private void createImageIndexWithSurf(String imagespath,String indexpath){
		ParallelIndexer indexer = new ParallelIndexer(DocumentBuilder.NUM_OF_THREADS, indexpath+"\\surf", imagespath);
		indexer.addExtractor(CvSurfExtractor.class);
		indexer.run();
	}
	
	private void createImageIndexWithMultAlgorithm(String imagespath,String indexpath){
		String indexDic = indexpath+"\\common_index";
		File dic = new File(indexDic);
		if(!dic.exists()&&!dic.isDirectory()){
			dic.mkdir();
		}
		ParallelIndexer indexer = new ParallelIndexer(DocumentBuilder.NUM_OF_THREADS, indexDic, imagespath);
//		indexer.addExtractor(CvSiftExtractor.class);
		indexer.addExtractor(CvSurfExtractor.class);
//		indexer.addExtractor(AutoColorCorrelogram.class);
//		indexer.addExtractor(CEDD.class);
//		indexer.addExtractor(PHOG.class);
//		indexer.addExtractor(SimpleColorHistogram.class);
//		indexer.addExtractor(JpegCoefficientHistogram.class);
//		indexer.addExtractor(EdgeHistogram.class);
//		indexer.addExtractor(BinaryPatternsPyramid.class);
//		indexer.addExtractor(JCD.class);
//		indexer.addExtractor(FuzzyColorHistogram.class);
//		indexer.addExtractor(FuzzyOpponentHistogram.class);
//		indexer.addExtractor(RotationInvariantLocalBinaryPatterns.class);
//		indexer.addExtractor(LuminanceLayout.class);
		indexer.run();
	}

}
