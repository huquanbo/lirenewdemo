package lirenewdemo;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.aggregators.AbstractAggregator;
import net.semanticmetadata.lire.aggregators.BOVW;
import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.classifiers.Cluster;
import net.semanticmetadata.lire.imageanalysis.features.GlobalFeature;
import net.semanticmetadata.lire.imageanalysis.features.LocalFeatureExtractor;
import net.semanticmetadata.lire.imageanalysis.features.global.AutoColorCorrelogram;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.imageanalysis.features.global.FCTH;
import net.semanticmetadata.lire.imageanalysis.features.global.JCD;
import net.semanticmetadata.lire.imageanalysis.features.local.opencvfeatures.CvSiftExtractor;
import net.semanticmetadata.lire.imageanalysis.features.local.opencvfeatures.CvSurfExtractor;
import net.semanticmetadata.lire.imageanalysis.features.local.simple.SimpleExtractor;
import net.semanticmetadata.lire.indexers.parallel.ParallelIndexer;
import net.semanticmetadata.lire.searchers.GenericFastImageSearcher;
import net.semanticmetadata.lire.searchers.ImageSearchHits;
import net.semanticmetadata.lire.searchers.ImageSearcher;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

public class TestCases {

	private String indexpath="F:\\lireIndex";
	private String imagepath="C:\\Users\\Administrator\\Desktop\\树叶";
	private String image="C:\\Users\\Administrator\\Desktop\\xxx.jpg";
	
	Class<? extends GlobalFeature> globalFeatureClass = CEDD.class;
    Class<? extends LocalFeatureExtractor> localFeatureClass = CvSurfExtractor.class;
    SimpleExtractor.KeypointDetector keypointDetector = SimpleExtractor.KeypointDetector.CVSURF;
    Class<? extends AbstractAggregator> aggregatorClass = BOVW.class;
    
	@Test
	public void testCreateNewIndex() throws IOException {
		 boolean passed = false;

         // use ParallelIndexer to index all photos from args[0] into "index".
         int numOfDocsForVocabulary = 500;
         Class<? extends AbstractAggregator> aggregator = BOVW.class;
         int[] numOfClusters = new int[] {128, 256};

         ParallelIndexer indexer = new ParallelIndexer(DocumentBuilder.NUM_OF_THREADS, indexpath, imagepath, numOfClusters, numOfDocsForVocabulary, aggregator);
         //Global
         indexer.addExtractor(CEDD.class);
         indexer.addExtractor(FCTH.class);
         indexer.addExtractor(AutoColorCorrelogram.class);
         //Local
         indexer.addExtractor(CvSurfExtractor.class);
         indexer.addExtractor(CvSiftExtractor.class);
         //Simple
         indexer.addExtractor(CEDD.class, SimpleExtractor.KeypointDetector.CVSURF);
         indexer.addExtractor(JCD.class, SimpleExtractor.KeypointDetector.Random);

         indexer.run();
         System.out.println("Finished indexing.");
	   }
	
	@Test
	public void testSearch() throws IOException, InstantiationException, IllegalAccessException{
		 BufferedImage images = ImageIO.read(new FileInputStream(new File(image)));
		 IndexReader reader = DirectoryReader.open(new RAMDirectory(FSDirectory.open(Paths.get(indexpath)), IOContext.READONCE));
		 GenericFastImageSearcher cvsurfsearcher = new GenericFastImageSearcher(10, localFeatureClass, aggregatorClass.newInstance(), 256, true, reader, indexpath + ".config");
		
		 
		 ImageSearchHits cvsurfhits = cvsurfsearcher.search(images, reader);
		 String hitFile;
	        for (int y = 0; y < cvsurfhits.length(); y++) {
	            hitFile = reader.document(cvsurfhits.documentID(y)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
	            hitFile = hitFile.substring(hitFile.lastIndexOf('\\') + 1);
	            System.out.println(y + ". " + hitFile + " " + cvsurfhits.score(y));
	        }
	        System.out.println();
	}
}
