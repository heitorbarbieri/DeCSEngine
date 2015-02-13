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
 * date: 20150202
 */
public class DeCSQualifierAnalyzerTest {
    private static final String DECS_XML = "resources/decs/xml/decs-metadata.xml";
    
    private final Map<String,DecsSyn> decs;
    private final DeCSQualifierAnalyzer analyzer;
    
    public DeCSQualifierAnalyzerTest() throws IOException, ParserConfigurationException, 
                                                                  SAXException {
        decs = new IndexDecs().indexTerms(DECS_XML);
        analyzer = new DeCSQualifierAnalyzer(decs);
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
        
        // It is not a qualifier because it does not follow a descritor, so the
        // generated token is the input string.
        final String in1 = "/sangue";
        final String[] arr1 = { "/sangue" };
        final Set<String> expResult1 = toSet(arr1);
        final Set<String> result1 = toSet(AnalyzerUtils.getTokenList(analyzer, in1));
        assertEquals(expResult1, result1);
        System.out.println("Test 1");
               
        // Same as the previous example
        final String in2 = "/blood";
        final String[] arr2 = { "/blood" };
        final Set<String> expResult2 = toSet(arr2);
        final Set<String> result2 = toSet(AnalyzerUtils.getTokenList(analyzer, in2));
        assertEquals(expResult2, result2);               
        System.out.println("Test 2");
                     
        // Same as the previous example
        final String in3 = " / ";
        final String[] arr3 = { };
        final Set<String> expResult3 = toSet(arr3);
        final Set<String> result3 = toSet(AnalyzerUtils.getTokenList(analyzer, in3));
        assertEquals(expResult3, result3);               
        System.out.println("Test 3");
        
        // Same as the previous example
        final String in4 = " ++/sangue)";
        final String[] arr4 = { "++/sangue)" };         
        final Set<String> expResult4 = toSet(arr4);
        final Set<String> result4 = toSet(AnalyzerUtils.getTokenList(analyzer, in4));
        assertEquals(expResult4, result4);       
        System.out.println("Test 4");
               
        // Same as the previous example
        final String in5 = "/sangue /blood ";
        final String[] arr5 = { "/sangue", "/blood" };
        final Set<String> expResult5 = toSet(arr5);
        final Set<String> result5 = toSet(AnalyzerUtils.getTokenList(analyzer, in5));
        assertEquals(expResult5, result5);        
        System.out.println("Test 5");
                  
        // Same as the previous example
        final String in6 = " /sangue/blood";
        final String[] arr6 = { "/sangue/blood" };
        final Set<String> expResult6 = toSet(arr6);
        final Set<String> result6 = toSet(AnalyzerUtils.getTokenList(analyzer, in6));
        assertEquals(expResult6, result6);       
        System.out.println("Test 6");        
             
        // Same as the previous example
        final String in7 = " //sangue ";
        final String[] arr7 = { "//sangue" };
        final Set<String> expResult7 = toSet(arr7);
        final Set<String> result7 = toSet(AnalyzerUtils.getTokenList(analyzer, in7));
        assertEquals(expResult7, result7);
        System.out.println("Test 7");          
        
        final String in8 = "Temefós/sangue";
        final String[] arr8 = { "/blood", "/sangre", "/sangue" };
        final Set<String> expResult8 = toSet(arr8);
        final Set<String> result8 = toSet(AnalyzerUtils.getTokenList(analyzer, in8));
        assertEquals(expResult8, result8);
        System.out.println("Test 8");          
        
        final String in9 = " (Temefós/sangue) ";
        final String[] arr9 = { "/blood", "/sangre", "/sangue" };
        final Set<String> expResult9 = toSet(arr9);
        final Set<String> result9 = toSet(AnalyzerUtils.getTokenList(analyzer, in9));
        assertEquals(expResult9, result9);
        System.out.println("Test 9");
                
        final String in10 = " #Temefós/sangue# ";
        final String[] arr10 = { "/blood", "/sangre", "/sangue" };
        final Set<String> expResult10 = toSet(arr10);
        final Set<String> result10 = toSet(AnalyzerUtils.getTokenList(analyzer, in10));
        assertEquals(expResult10, result10);
        System.out.println("Test 10");         
               
        final String in11 = " xxxTemefós/sanguexxx ";
        final String[] arr11 = { "xxxtemefos/sanguexxx" };
        final Set<String> expResult11 = toSet(arr11);
        final Set<String> result11 = toSet(AnalyzerUtils.getTokenList(analyzer, in11));
        assertEquals(expResult11, result11);
        System.out.println("Test 11");         
        
        final String in12 = " Temefós / sangue";
        final String[] arr12 = { "sangue" };
        final Set<String> expResult12 = toSet(arr12);
        final Set<String> result12 = toSet(AnalyzerUtils.getTokenList(analyzer, in12));
        assertEquals(expResult12, result12);
        System.out.println("Test 12"); 
                
        final String in13 = " Temefós/rei";
        final String[] arr13 = { "temefos/rei" };
        final Set<String> expResult13 = toSet(arr13);
        final Set<String> result13 = toSet(AnalyzerUtils.getTokenList(analyzer, in13));
        assertEquals(expResult13, result13);
        System.out.println("Test 13"); 
        
        final String in14 = " Temefós/ sangue";
        final String[] arr14 = { "sangue"};
        final Set<String> expResult14 = toSet(arr14);
        final Set<String> result14 = toSet(AnalyzerUtils.getTokenList(analyzer, in14));
        assertEquals(expResult14, result14);
        System.out.println("Test 14"); 
        
        final String in15 = " Temefós /sangue";
        final String[] arr15 = { "/sangue" };
        final Set<String> expResult15 = toSet(arr15);
        final Set<String> result15 = toSet(AnalyzerUtils.getTokenList(analyzer, in15));
        assertEquals(expResult15, result15);
        System.out.println("Test 15");
               
        final String in16 = "   xxxxx/sangue";
        final String[] arr16 = { "xxxxx/sangue" };
        final Set<String> expResult16 = toSet(arr16);
        final Set<String> result16 = toSet(AnalyzerUtils.getTokenList(analyzer, in16));
        assertEquals(expResult16, result16);
        System.out.println("Test 16"); 
        
        final String in17 = "O qualificador (Temefós/sangue) funciona.";                                      
        final String[] arr17 = { "qualificador", "/blood", "/sangre", "/sangue", 
                                                                   "funciona."};
        final Set<String> expResult17 = toSet(arr17);
        final Set<String> result17 = toSet(AnalyzerUtils.getTokenList(analyzer, in17));
        assertEquals(expResult17, result17);
        System.out.println("Test 17");  
                         
        final String in18 = "O qualificador (Temefós/sangue)não funciona.";
        final String[] arr18 = { "qualificador", "(temefos/sangue)nao", 
                                                                   "funciona."};
        final Set<String> expResult18 = toSet(arr18);
        final Set<String> result18 = toSet(AnalyzerUtils.getTokenList(analyzer, in18));
        assertEquals(expResult18, result18);
        System.out.println("Test 18");
        
        final String in19 = " /sangue/sangue";
        final String[] arr19 = { "/sangue/sangue" };
        final Set<String> expResult19 = toSet(arr19);
        final Set<String> result19 = toSet(AnalyzerUtils.getTokenList(analyzer, in19));
        assertEquals(expResult19, result19);
        System.out.println("Test 19");
        
        final String in20 = " / sangue";
        final String[] arr20 = { "sangue" };
        final Set<String> expResult20 = toSet(arr20);
        final Set<String> result20 = toSet(AnalyzerUtils.getTokenList(analyzer, in20));
        assertEquals(expResult20, result20);
        System.out.println("Test 20");
        
        final String in21 = "neoplasias abdominais/sangue";
        final String[] arr21 = { "/blood", "/sangre", "/sangue" };
        final Set<String> expResult21 = toSet(arr21);
        final Set<String> result21 = toSet(AnalyzerUtils.getTokenList(analyzer, in21));
        assertEquals(expResult21, result21);
        System.out.println("Test 21");        
        
        final String in22 = "xxneoplasias abdominais/sangue";
        final String[] arr22 = { "xxneoplasias", "abdominais/sangue" };
        final Set<String> expResult22 = toSet(arr22);
        final Set<String> result22 = toSet(AnalyzerUtils.getTokenList(analyzer, in22));
        assertEquals(expResult22, result22);
        System.out.println("Test 22");     
        
        final String in23 = " ^d8%   ";
        final String[] arr23 = { "^d8%" };
        final Set<String> expResult23 = toSet(arr23);
        final Set<String> result23 = toSet(AnalyzerUtils.getTokenList(analyzer, in23));
        assertEquals(expResult23, result23);
        System.out.println("Test 23"); 

        final String in24 = "#^d8%   ";
        final String[] arr24 = { "#^d8%" };
        final Set<String> expResult24 = toSet(arr24);
        final Set<String> result24 = toSet(AnalyzerUtils.getTokenList(analyzer, in24));
        assertEquals(expResult24, result24);
        System.out.println("Test 24"); 

        final String in25 = "#^d22062   ";
        final String[] arr25 = { "#^d22062" };
        final Set<String> expResult25 = toSet(arr25);
        final Set<String> result25 = toSet(AnalyzerUtils.getTokenList(analyzer, in25));
        assertEquals(expResult25, result25);
        System.out.println("Test 25");         
        
        final String in26 = "Temefós proteja os abatedouros de doenças como "
               + "<^D8> neoplasias abdominais/sangue dentre <^d8><^d22062> "
               + "outras. ***[^d22062]! ^d1111111";
        final String[] arr26 = { "temefos", "proteja", "os",  
            "de", "doencas", "como", "<^d8>",                       
            "/blood", "/sangre", "/sangue", "dentre", "<^d8><^d22062>", 
            "outras.", "***[^d22062]!", "^d1111111" };
        final Set<String> expResult26 = toSet(arr26);
        final Set<String> result26 = toSet(AnalyzerUtils.getTokenList(analyzer, in26));
        assertEquals(expResult26, result26);
        System.out.println("Test 26");
        
        final String in27 = "Abdomen, Acute/síntesis química";
        final String[] arr27 = { "/chemical synthesis", "/sintese quimica", 
                                    "/sintesis quimica"};
        final Set<String> expResult27 = toSet(arr27);
        final Set<String> result27 = toSet(AnalyzerUtils.getTokenList(analyzer, in27));
        assertEquals(expResult27, result27);
        System.out.println("Test 27");
    }    
}
