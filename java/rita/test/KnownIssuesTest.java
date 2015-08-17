package rita.test;

import static rita.support.QUnitStubs.deepEqual;
import static rita.support.QUnitStubs.equal;
import static rita.support.QUnitStubs.ok;
import static rita.support.QUnitStubs.setEqual;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import rita.RiGrammar;
import rita.RiLexicon;
import rita.RiString;
import rita.RiTa;
import rita.RiWordNet;
import rita.support.Constants;
import rita.support.JSONLexicon;
import rita.support.LetterToSound;

public class KnownIssuesTest implements Constants
{
  
  @Test
  public void testPlingStemmerFails()
  {
    String type = RiTa.PLING;
    
    // TODO: add all failing tests
  }
  
  @Test
  public void testLancasterStemmerFails()
  {
    String type = RiTa.LANCASTER;
  }
  
  @Test
  public void testPorterStemmerFails()
  {
    String type = RiTa.PORTER;
  }  
  
  @Test
  public void testSplitSentences()
  {
    //  Q. or A. at start of sentence 
    String input = "Q. Do I need a visa to visit Hong Kong? A. Visitors from most countries can enter Hong Kong without a visa for periods of seven to 180 days, depending on nationality.";
    String[] output = RiTa.splitSentences(input);
    System.out.println(Arrays.asList(output));
    String[] expected = new String[] { 
	"Q. Do I need a visa to visit Hong Kong?", 
	"A. Visitors from most countries can enter Hong Kong without a visa for periods of seven to 180 days, depending on nationality."};
    deepEqual(output, expected);
    
    // nextToken does not begin with an upper case
    input = "What did he buy? iPad or iPhone?";
    output = RiTa.splitSentences(input);
    expected = new String[] { 
	"What did he buy?",
	"iPad or iPhone?"};
    System.out.println(Arrays.asList(output));
    deepEqual(output, expected);
  }
      
  @Test
  public void testSpecialCharsInRiGrammar()
  {
    String s = "{ \"<start>\": \"&#8220;hello!&#8221;\" }";
    RiGrammar rg = new RiGrammar(s);
    //ok("fails b/c of editor?");
    String res = rg.expand();
    ok(res.equals("���hello!���")); // fails bc of editor
  }

  @Test
  public void testRhyming()
  {
    RiLexicon lex = new RiLexicon();
    String[] result = lex.rhymes("savage");
    String[] answer = new String[] { "ravage", "disparage", "cabbage", "etc" };
    deepEqual(answer, result);
  }

  @Test
  public void testPosTagging()
  {
    String[] result = RiTa.getPosTags("fucking", false);
    System.out.println(Arrays.asList(result));
    String[] answer = new String[] { "jj" };
    deepEqual(result, answer);

    result = RiTa.getPosTags("shitting", false);
    System.out.println(Arrays.asList(result));
    answer = new String[] { "jj" };
    deepEqual(result, answer);

    result = RiTa.getPosTags("shitty", false);
    // System.out.println(Arrays.asList(result));
    answer = new String[] { "jj" };
    deepEqual(result, answer);

    result = RiTa.getPosTags("shitty", true);
    answer = new String[] { "a" };
    deepEqual(result, answer);
  }

  @Test
  public void testAnalyzeNums() // Cardinal numbers
  {
    // FROM RiStringTest
    Map features = new RiString("123.").analyze().features();
    ok(features);
    equal(features.get(PHONEMES), "w-ah-n-t-uw-th-r-iy");
    equal(features.get(SYLLABLES), "w-ah-n/t-uw/th-r-iy");
    equal(features.get(STRESSES), "0/0/0");

    features = new RiString("1 2 7").analyze().features();
    ok(features);
    System.out.println("Analyze: " + features);
    equal(features.get(PHONEMES), "w-ah-n t-uw s-eh-v-ax-n");
    equal(features.get(SYLLABLES), "w-ah-n t-uw s-eh-v/ax-n");
    equal(features.get(STRESSES), "1 1 1/0");

    /*
     * in RITAJS equal(features.get(SYLLABLES), "w-ah-n t-uw s-eh/v-ax-n");
     * equal(features.get(STRESSES), "0 0 1/0");
     */

    features = new RiString("123").analyze().features();
    ok(features);
    equal(features.get(PHONEMES), "w-ah-n-t-uw-th-r-iy");
    equal(features.get(SYLLABLES), "w-ah-n/t-uw/th-r-iy");
    equal(features.get(STRESSES), "0/0/0");

    /*
     * In RITAJS equal(features.get(PHONEMES), "w-ah-n-t-uw-th-r-iy");
     * equal(features.get(SYLLABLES), "w-ah-n/t-uw/th-r-iy");
     * equal(features.get(STRESSES), "0/0/0");
     */
  }

  @Test
  public void testRandomIterator()
  {

    JSONLexicon lexicon = JSONLexicon.getInstance();
    long ts = System.currentTimeMillis();
    lexicon.randomPosIterator("nns");
    System.out.println("randomPosIterator in " + (System.currentTimeMillis() - ts) + "ms");
    equal("TODO:", "improve performance or remove!");
  }

  /*
   * Checks output of RiString.syllabify against syllabifications in rita_dict
   * Note(dch): added a hack to temp.fix bad output at end of RiString.syllabify, syllables like: l-ow-1
   */
  @Test
  public void testSyllabify() 
  {
    equal(1,1);
    RiLexicon lex = new RiLexicon();
    String[] failing = { "dog", "dragging", "mellow" };
    for (int i = 0; i < failing.length; i++) {
      String phones = LetterToSound.getInstance().getPhones(failing[i]);
      String phones2 = lex.lexImpl.getRawPhones(failing[i]);
      System.out.println(failing[i] + " -> "+phones + "[lts] ?= " + phones2
	  + " \t\t ["+phones.equals(phones2)+"]");
      //equal(phones, phones2);
    }
  }
  
    @Test
  public void testSyllabify2() 
  {
    String s = new RiLexicon().lexImpl.getRawPhones("yoyo",true);
    equal(s,"y-oy1 ow1"); // TODO: Is this correct? check in JS
  }
    
}
