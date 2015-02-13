/*=========================================================================

    Copyright © 2015 BIREME/PAHO/WHO

    This file is part of IAHx-Analyzer.

    IAHx-Analyzer is free software: you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public License
    as published by the Free Software Foundation, either version 2.1 of
    the License, or (at your option) any later version.

    IAHx-Analyzer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with IAHx-Analyzer. If not, see
    <http://www.gnu.org/licenses/>.

=========================================================================*/

package br.bireme.dengine;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 *
 * @author Heitor Barbieri
 * date: 20150126
 */
public class BVSStandardAnalyzerTest {
    private static final String DECS_XML = "resources/decs/xml/decs-metadata.xml";
    
    private final Map<String,DecsSyn> decs;
    private final BVSStandardAnalyzer analyzer;
    
    public BVSStandardAnalyzerTest() throws IOException, 
                                            ParserConfigurationException, 
                                                                  SAXException {
        decs = new IndexDecs().indexTerms(DECS_XML);
        analyzer = new BVSStandardAnalyzer(decs);
    }
    
    private Set<String> toSet(final String[] in) {
        assert in != null;
        
        final Set set = new TreeSet();
        
        set.addAll(Arrays.asList(in));
        
        return set;
    }
    
    private Set<String> toSet(final List<String> in) {
        assert in != null;
        
        final Set set = new TreeSet();
        
        set.addAll(in);
        
        return set;
    }
    
    /**
     * Test of class DeCSAnalyzer.
     * @throws java.io.IOException
     */
    @Test
    public void testAnalyzer() throws IOException {
        System.out.println("testAnalyzer");
        
        final String in1 = "";
        final String[] arr1 = { };
        final Set<String> expResult1 = toSet(arr1);
        final Set<String> result1 = toSet(AnalyzerUtils.getTokenList(analyzer, in1));
        assertEquals(expResult1, result1);
        System.out.println("Test 1");
               
        final String in2 = "   ";
        final String[] arr2 = { };
        final Set<String> expResult2 = toSet(arr2);
        final Set<String> result2 = toSet(AnalyzerUtils.getTokenList(analyzer, in2));
        assertEquals(expResult2, result2);               
        System.out.println("Test 2");
                     
        final String in3 = "word";
        final String[] arr3 = { "word" };
        final Set<String> expResult3 = toSet(arr3);
        final Set<String> result3 = toSet(AnalyzerUtils.getTokenList(analyzer, in3));
        assertEquals(expResult3, result3);               
        System.out.println("Test 3");
        
        final String in4 = " Cação ";
        final String[] arr4 = { "cacao" };
        final Set<String> expResult4 = toSet(arr4);
        final Set<String> result4 = toSet(AnalyzerUtils.getTokenList(analyzer, in4));
        assertEquals(expResult4, result4);       
        System.out.println("Test 4");
               
        final String in5 = " !@#$% &*() _+{} ";
        final String[] arr5 = { "!@#$%", "&*()", "_+{}" };
        final Set<String> expResult5 = toSet(arr5);
        final Set<String> result5 = toSet(AnalyzerUtils.getTokenList(analyzer, in5));
        assertEquals(expResult5, result5);        
        System.out.println("Test 5");
                  
        final String in6 = " !@#$% &*)( _+{} ";
        final String[] arr6 = { "!@#$%", "&*)(", "_+{}" };
        final Set<String> expResult6 = toSet(arr6);
        final Set<String> result6 = toSet(AnalyzerUtils.getTokenList(analyzer, in6));
        assertEquals(expResult6, result6);       
        System.out.println("Test 6");        
             
        final String in7 = " Temefós ";
        final String[] arr7 = { "temefos" };
        final Set<String> expResult7 = toSet(arr7);
        final Set<String> result7 = toSet(AnalyzerUtils.getTokenList(analyzer, in7));
        assertEquals(expResult7, result7);
        System.out.println("Test 7");          
        
        final String in8 = " xxxTemefósyyy ";
        final String[] arr8 = { "xxxtemefosyyy" };
        final Set<String> expResult8 = toSet(arr8);
        final Set<String> result8 = toSet(AnalyzerUtils.getTokenList(analyzer, in8));
        assertEquals(expResult8, result8);
        System.out.println("Test 8");          
        
        final String in9 = " (Temefós) ";
        final String[] arr9 = { "(temefos)" };
        final Set<String> expResult9 = toSet(arr9);
        final Set<String> result9 = toSet(AnalyzerUtils.getTokenList(analyzer, in9));
        assertEquals(expResult9, result9);
        System.out.println("Test 9");
        
        final String in10 = " ^d8 ";
        final String[] arr10 = { "^d8", "abdome", "abdominais", "abdominal", 
            "abdominal neoplasms", "abdominales", "c04.588.033", "do", 
            "neoplasias", "neoplasias abdominais", "neoplasias abdominales", 
            "neoplasms", "tumores", "tumores abdominais", "tumores abdominales", 
            "tumores do abdome" };
        final Set<String> expResult10 = toSet(arr10);
        final Set<String> result10 = toSet(AnalyzerUtils.getTokenList(analyzer, in10));
        assertEquals(expResult10, result10);
        System.out.println("Test 10");
        
        final String in11 = " ^D8 ";
        final String[] arr11 = { "^d8", "abdome", "abdominais", "abdominal", 
            "abdominal neoplasms", "abdominales", "c04.588.033", "do", 
            "neoplasias", "neoplasias abdominais", "neoplasias abdominales", 
            "neoplasms", "tumores", "tumores abdominais", "tumores abdominales", 
            "tumores do abdome" };
        final Set<String> expResult11 = toSet(arr11);
        final Set<String> result11 = toSet(AnalyzerUtils.getTokenList(analyzer, in11));
        assertEquals(expResult11, result11);
        System.out.println("Test 11");
        
        final String in12 = " xxx^d8yyy ";
        final String[] arr12 = { "xxx^d8yyy" };
        final Set<String> expResult12 = toSet(arr12);
        final Set<String> result12 = toSet(AnalyzerUtils.getTokenList(analyzer, in12));
        assertEquals(expResult12, result12);
        System.out.println("Test 12"); 
        
        final String in13 = " #^d8# ";
        final String[] arr13 = { "^d8", "abdome", "abdominais", "abdominal", 
            "abdominal neoplasms", "abdominales", "c04.588.033", "do", 
            "neoplasias", "neoplasias abdominais", "neoplasias abdominales", 
            "neoplasms", "tumores", "tumores abdominais", "tumores abdominales", 
            "tumores do abdome" };
        final Set<String> expResult13 = toSet(arr13);
        final Set<String> result13 = toSet(AnalyzerUtils.getTokenList(analyzer, in13));
        assertEquals(expResult13, result13);
        System.out.println("Test 13");         
                
        final String in14 = " #^d8# Temefos";
        final String[] arr14 = { "^d8", "abdome", "abdominais", "abdominal", 
            "abdominal neoplasms", "abdominales", "c04.588.033", "do", 
            "neoplasias", "neoplasias abdominais", "neoplasias abdominales", 
            "neoplasms", "temefos", "tumores", "tumores abdominais", 
            "tumores abdominales", "tumores do abdome"};
        final Set<String> expResult14 = toSet(arr14);
        final Set<String> result14 = toSet(AnalyzerUtils.getTokenList(analyzer, in14));
        assertEquals(expResult14, result14);
        System.out.println("Test 14"); 
        
        final String in15 = " #^d8# (^d8)   ";
        final String[] arr15 = { "^d8", "abdome", "abdominais", "abdominal", 
            "abdominal neoplasms", "abdominales", "c04.588.033", "do", 
            "neoplasias", "neoplasias abdominais", "neoplasias abdominales", 
            "neoplasms", "tumores", "tumores abdominais", "tumores abdominales", 
            "tumores do abdome" };
        final Set<String> expResult15 = toSet(arr15);
        final Set<String> result15 = toSet(AnalyzerUtils.getTokenList(analyzer, in15));
        assertEquals(expResult15, result15);
        System.out.println("Test 15"); 
        
        final String in16 = " #^d8#(^D8)   ";
        final String[] arr16 = { "#^d8#(^d8)" };
        final Set<String> expResult16 = toSet(arr16);
        final Set<String> result16 = toSet(AnalyzerUtils.getTokenList(analyzer, in16));
        assertEquals(expResult16, result16);
        System.out.println("Test 16"); 
        
        final String in17 = " ^d22062   ";
        final String[] arr17 = { "/blood", "/sangre", "/sangue", "^d22062", 
                                 "blood", "q50.040.020q05.010", "sangre", 
                                                                      "sangue"};
        final Set<String> expResult17 = toSet(arr17);
        final Set<String> result17 = toSet(AnalyzerUtils.getTokenList(analyzer, in17));
        assertEquals(expResult17, result17);
        System.out.println("Test 17"); 

        final String in18 = " ^d99999   ";
        final String[] arr18 = { "^d99999" };
        final Set<String> expResult18 = toSet(arr18);
        final Set<String> result18 = toSet(AnalyzerUtils.getTokenList(analyzer, in18));
        assertEquals(expResult18, result18);
        System.out.println("Test 18"); 
        
        final String in19 = "#^d22062   ";
        final String[] arr19 = { "/blood", "/sangre", "/sangue",
                                 "^d22062", "blood", "q50.040.020q05.010", 
                                                           "sangre", "sangue" };
        final Set<String> expResult19 = toSet(arr19);
        final Set<String> result19 = toSet(AnalyzerUtils.getTokenList(analyzer, in19));
        assertEquals(expResult19, result19);
        System.out.println("Test 19"); 
        
        final String in20 = "Temefós proteja os abatedouros de doenças como "
               + "<^D8> neoplasias abdominais/sangue dentre <^d8><^d22062> outras. "
               + "***[^d22062]! ^d1111111"; 
        final String[] arr20 = { "/blood", "/sangre", "/sangue",
            "<^d8><^d22062>", "^d1111111", "^d22062",
            "^d8", "abatedouros", "abdome", "abdominais", "abdominais/sangue", 
            "abdominal", "abdominal neoplasms", "abdominales", "blood",
            "c04.588.033", "como", "de", "dentre", "do", "doencas",
            "neoplasias", "neoplasias abdominais", "neoplasias abdominales", 
            "neoplasms", "os", "outras.", "proteja", "q50.040.020q05.010", 
            "sangre", "sangue", "temefos", "tumores", "tumores abdominais", 
            "tumores abdominales", "tumores do abdome" };
        final Set<String> expResult20 = toSet(arr20);
        final Set<String> result20 = toSet(AnalyzerUtils.getTokenList(analyzer, in20));
        assertEquals(expResult20, result20);
        System.out.println("Test 20");
        
        final String in21 = "#^d22062^kxxxx   ";
        final String[] arr21 = { "/blood", "/sangre", "/sangue",
                                 "^d22062", "blood", "q50.040.020q05.010", 
                                                            "sangre", "sangue"};
        final Set<String> expResult21 = toSet(arr21);
        final Set<String> result21 = toSet(AnalyzerUtils.getTokenList(analyzer, in21));
        assertEquals(expResult21, result21);
        System.out.println("Test 21"); 
        
        final String in22 = " Temefós/sangre   ";
        final String[] arr22 = { "temefos/sangre" };
        final Set<String> expResult22 = toSet(arr22);
        final Set<String> result22 = toSet(AnalyzerUtils.getTokenList(analyzer, in22));
        assertEquals(expResult22, result22);
        System.out.println("Test 22");
    }    
}
