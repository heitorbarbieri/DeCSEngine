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
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 *
 * @author Heitor Barbieri
 * date: 20141125
 */
public class DeCSKeywordAnalyzerTest {
    private static final String DECS_XML = "resources/decs/xml/decs-metadata.xml";
    
    private final Map<String,DecsSyn> decs;
    private final DeCSKeywordAnalyzer analyzer;
    
    public DeCSKeywordAnalyzerTest() throws IOException, 
                                            ParserConfigurationException, 
                                                                  SAXException {
        decs = new IndexDecs().indexTerms(DECS_XML);
        analyzer = new DeCSKeywordAnalyzer(decs);
    }
    
    /**
     * Test of class DeCSAnalyzer.
     * @throws java.io.IOException
     */
    @Test
    public void testAnalyzer() throws IOException {
        System.out.println("testAnalyzer");
        /*
        final String in1 = "";
        final List<String> expResult1 = new ArrayList<String>();
        final List<String> result1 = AnalyzerUtils.getTokenList(analyzer, in1);
        assertEquals(expResult1, result1);
        System.out.println("Test 1");
               
        final String in2 = "   ";
        final List<String> expResult2 = new ArrayList<String>();
        final List<String> result2 = AnalyzerUtils.getTokenList(analyzer, in2);
        assertEquals(expResult2, result2);               
        System.out.println("Test 2");
                     
        final String in3 = "word";
        final String[] arr3 = { "word" };
        final List<String> expResult3 = Arrays.asList(arr3);
        final List<String> result3 = AnalyzerUtils.getTokenList(analyzer, in3);
        assertEquals(expResult3, result3);               
        System.out.println("Test 3");
        
        final String in4 = " Cação ";
        final String[] arr4 = { "cacao" };
        final List<String> expResult4 = Arrays.asList(arr4);
        final List<String> result4 = AnalyzerUtils.getTokenList(analyzer, in4);
        assertEquals(expResult4, result4);       
        System.out.println("Test 4");
               
        final String in5 = " !@#$% &*() _+{} ";
        final String[] arr5 = { "!@#$%", "&*()", "_+{}" };
        final List<String> expResult5 = Arrays.asList(arr5);
        final List<String> result5 = AnalyzerUtils.getTokenList(analyzer, in5);
        assertEquals(expResult5, result5);        
        System.out.println("Test 5");
                  
        final String in6 = " !@#$% &*)( _+{} ";
        final String[] arr6 = { "!@#$%", "&*)(", "_+{}" };
        final List<String> expResult6 = Arrays.asList(arr6);
        final List<String> result6 = AnalyzerUtils.getTokenList(analyzer, in6);
        assertEquals(expResult6, result6);       
        System.out.println("Test 6");        
             
        final String in7 = "Temefós ";
        final String[] arr7 = { "d02.705.539.900", "d02.886.309.900", "temefos" };
        final List<String> expResult7 = Arrays.asList(arr7);
        final List<String> result7 = AnalyzerUtils.getTokenList(analyzer, in7);
        assertEquals(expResult7, result7);
        System.out.println("Test 7");          
        
        final String in8 = " xxxTemefósyyy ";
        final String[] arr8 = { "xxxtemefosyyy" };
        final List<String> expResult8 = Arrays.asList(arr8);
        final List<String> result8 = AnalyzerUtils.getTokenList(analyzer, in8);
        assertEquals(expResult8, result8);
        System.out.println("Test 8");          
        
        final String in9 = " (Temefós) ";
        final String[] arr9 = { "d02.705.539.900", "d02.886.309.900", "temefos" };
        final List<String> expResult9 = Arrays.asList(arr9);
        final List<String> result9 = AnalyzerUtils.getTokenList(analyzer, in9);
        assertEquals(expResult9, result9);
        System.out.println("Test 9");
                
        final String in10 = " #Temefós# ";
        final String[] arr10 = { "d02.705.539.900", "d02.886.309.900", "temefos" };
        final List<String> expResult10 = Arrays.asList(arr10);
        final List<String> result10 = AnalyzerUtils.getTokenList(analyzer, in10);
        assertEquals(expResult10, result10);
        System.out.println("Test 10");         
               
        final String in11 = " #Temefós# rei";
        final String[] arr11 = { "d02.705.539.900", "d02.886.309.900", "temefos",
                                                                        "rei" };
        final List<String> expResult11 = Arrays.asList(arr11);
        final List<String> result11 = AnalyzerUtils.getTokenList(analyzer, in11);
        assertEquals(expResult11, result11);
        System.out.println("Test 11"); 
        
        final String in12 = " #Temefós#rei";
        final String[] arr12 = { "#temefos#rei" };
        final List<String> expResult12 = Arrays.asList(arr12);
        final List<String> result12 = AnalyzerUtils.getTokenList(analyzer, in12);
        assertEquals(expResult12, result12);
        System.out.println("Test 12"); 
        
        final String in13 = " Eu já disse: Temefós é o meu rei!";
        final String[] arr13 = { "eu", "ja", "disse:", "d02.705.539.900", 
                                   "d02.886.309.900", "temefos", "meu", "rei!"};
        final List<String> expResult13 = Arrays.asList(arr13);
        final List<String> result13 = AnalyzerUtils.getTokenList(analyzer, in13);
        assertEquals(expResult13, result13);
        System.out.println("Test 13"); 
        
        final String in14 = "tais como:   neoplasias abdominais    ";
        final String[] arr14 = { "tais", "como:", "abdominal neoplasms", 
            "c04.588.033", "neoplasias abdominais", "neoplasias abdominales", 
            "tumores abdominais", "tumores abdominales", "tumores do abdome" };
        final List<String> expResult14 = Arrays.asList(arr14);
        final List<String> result14 = AnalyzerUtils.getTokenList(analyzer, in14);
        assertEquals(expResult14, result14);
        System.out.println("Test 14"); 
                
        final String in15 = "   neoplasias    abdominais    ";
        final String[] arr15 = { "neoplasias", "abdominais" };
        final List<String> expResult15 = Arrays.asList(arr15);
        final List<String> result15 = AnalyzerUtils.getTokenList(analyzer, in15);
        assertEquals(expResult15, result15);
        System.out.println("Test 15"); 
               
        final String in16 = "Mãe eles me chamaram de 'abattoirs' lá na rua!";                                      
        final String[] arr16 = { "mae", "eles", "me", "chamaram", "de", 
            "abatedouros", "abattoirs", "j01.576.423.200.700.100", "mataderos",
            "matadouros", "slaughterhouses", "la", "na", "rua!"};
        final List<String> expResult16 = Arrays.asList(arr16);
        final List<String> result16 = AnalyzerUtils.getTokenList(analyzer, in16);
        assertEquals(expResult16, result16);
        System.out.println("Test 16");  
                         
        final String in17 = "Temefós proteja os abatedouros de doenças como "
                                       + "neoplasias abdominais dentre outras.";
        final String[] arr17 = { "d02.705.539.900", "d02.886.309.900", "temefos", 
            "proteja", "os", "abatedouros", "abattoirs", "j01.576.423.200.700.100", 
            "mataderos", "matadouros", "slaughterhouses", "de", "doencas", 
            "como", "abdominal neoplasms", "c04.588.033", "neoplasias abdominais", 
            "neoplasias abdominales", "tumores abdominais", "tumores abdominales", 
            "tumores do abdome", "dentre", "outras."};
        final List<String> expResult17 = Arrays.asList(arr17);
        final List<String> result17 = AnalyzerUtils.getTokenList(analyzer, in17);
        assertEquals(expResult17, result17);
        System.out.println("Test 17");  
        
        final String in18 = " ^d8%   ";
        final String[] arr18 = { "^d8%" };
        final List<String> expResult18 = Arrays.asList(arr18);
        final List<String> result18 = AnalyzerUtils.getTokenList(analyzer, in18);
        assertEquals(expResult18, result18);
        System.out.println("Test 18"); 

        final String in19 = "#^d8%   ";
        final String[] arr19 = { "#^d8%" };
        final List<String> expResult19 = Arrays.asList(arr19);
        final List<String> result19 = AnalyzerUtils.getTokenList(analyzer, in19);
        assertEquals(expResult19, result19);
        System.out.println("Test 19"); 

        final String in20 = "#^d22062   ";
        final String[] arr20 = { "#^d22062" };
        final List<String> expResult20 = Arrays.asList(arr20);
        final List<String> result20 = AnalyzerUtils.getTokenList(analyzer, in20);
        assertEquals(expResult20, result20);
        System.out.println("Test 20");         
        */
        
        /*final String in21 = "Temefós proteja os abatedouros de doenças como "
               + "<^D8> neoplasias abdominais/sangue dentre <^d8><^d22062> outras. "
               + "***[^d22062]! ^d1111111";*/
        /*final String in21 = "Temefós proteja os abatedouros de doenças como "
               + "<^D8> neoplasias abdominais/sangue dentre <^d8><^d22062> outras. ++++++++++++ +++++++++";
        final String[] arr21 = { "d02.705.539.900", "d02.886.309.900", "temefos", 
            "proteja", "os", "abatedouros", 
            "abattoirs", "j01.576.423.200.700.100", "mataderos", "matadouros", 
            "slaughterhouses", "de", "doencas", "como", "<^d8>",                        
            "abdominal neoplasms", "c04.588.033",
            "neoplasias abdominais", "neoplasias abdominales", 
            "tumores abdominais", "tumores abdominales", 
            "tumores do abdome", "/blood", "/sangre", "/sangue", 
            "q50.040.020q05.010", "dentre", "<^d8><^d22062>", 
            "outras.", "***[^d22062]!", "^d1111111" };
        final List<String> expResult21 = Arrays.asList(arr21);
        final List<String> result21 = AnalyzerUtils.getTokenList(analyzer, in21);        
        assertEquals(expResult21, result21);
        System.out.println("Test 21");
        */
        final String in22 = "Abdomen, Acute/síntesis química";
        final String[] arr22 = { "abdome", "abdome agudo", "abdomen", 
            "abdomen agudo", "abdomen en tabla", "abdomen,",  "abdomen, acute",  
            "acute", "agudo", "c23.888.646.100.200", "c23.888.821.030.249", "en", 
            "tabla", "q15.040"};
        final List<String> expResult22 = Arrays.asList(arr22);
        final List<String> result22 = AnalyzerUtils.getTokenList(analyzer, in22);        
        assertEquals(expResult22, result22);
        System.out.println("Test 22");
    }    
}
