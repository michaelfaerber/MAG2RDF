package utilities;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.sentences.IndoEuropeanSentenceModel;
import com.aliasi.sentences.SentenceChunker;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;

public class StringUtility {
	
    private static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append((i > start ? " " : "") + words[i]);
        return sb.toString();
    }
	
	// get ngrams of str. gramno: 1,2,...,5 typically
	// separate only by whitespace, use rather getngramsConsiderAlsoLinebreaks!
    public static List<String> getngramsByOneWhitespace(int gramno, String str) {
        List<String> ngrams = new ArrayList<String>();
        String[] words = str.split(" ");
        for (int i = 0; i < words.length - gramno + 1; i++)
            ngrams.add(concat(words, i, i+gramno));
        return ngrams;
    }
    
    // same as above, but consider also "\n" and "\n " as delimiter
	// get ngrams of str. gramno: 1,2,...,5 typically
    public static List<String> getngramsConsiderAlsoLinebreaks(int gramno, String str) {
        List<String> ngrams = new ArrayList<String>();
        String[] words = str.split("(( )+|( )*\n( )*|( )*\r( )*)");
        for (int i = 0; i < words.length - gramno + 1; i++)
            ngrams.add(concat(words, i, i+gramno));
        return ngrams;
    }
    
    // same as above, but consider also "\n" and "\n " as delimiter
	// get ngrams of str. gramno: 1,2,...,5 typically
    public static HashMap<String, Integer> getngramsConsiderAlsoLinebreaksAlsoCountOwnMethod(int gramno, String str) {
    	HashMap<String, Integer> ngramsWithCountsForString = new HashMap<String, Integer>();
        String[] words = str.split("(( )+|( )*\n( )*|( )*\r( )*)"); // contains also duplicates
        for (int i = 0; i < words.length - gramno + 1; i++) {
        	String currNgram = concat(words, i, i+gramno);
        	if(ngramsWithCountsForString.containsKey(currNgram)) {
        		ngramsWithCountsForString.put(currNgram, ngramsWithCountsForString.get(currNgram) + 1); // update counter by 1
        	}
        	else {
        		ngramsWithCountsForString.put(currNgram, 1); // first time, counter=1
        	}
        }
        return ngramsWithCountsForString;
    }

    /// Start LingPipe section ///
    private static TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
    private static SentenceModel SENTENCE_MODEL = new IndoEuropeanSentenceModel();
    private static SentenceChunker SENTENCE_CHUNKER = new SentenceChunker(
                    TOKENIZER_FACTORY, SENTENCE_MODEL);
    
	public static List<String> getSingleSentences(String text) {
		List<String> listofsents = new ArrayList<String>();
		
		/** use LingPipe for sentence splitting **/
		Chunking chunking = SENTENCE_CHUNKER.chunk(text.toCharArray(), 0, text.length());
		Set<Chunk> sentences = chunking.chunkSet();
		if (sentences.size() < 1) {
		    System.out.println("No sentence chunks found.");
		    return listofsents;
		}
		String slice = chunking.charSequence().toString();
		
		for (Iterator<Chunk> it = sentences.iterator(); it.hasNext(); ) {
		    Chunk sentence = it.next();
		    int start = sentence.start(); // same can be used for substring()
		    int end = sentence.end();
		    String currSent = slice.substring(start,end);
		    listofsents.add(currSent); // here: without storing offsets! (also possible, see WikipediaDumpProcessing)
		}
		return listofsents;
	}
    
}
