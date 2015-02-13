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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.lucene.analysis.Analyzer;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * Vinicius Andrade and Heitor Barbieri
 * date: 20060630 and 20141031
 */
public class IndexDecs extends DefaultHandler {   
    /** A buffer for each XML element */
    private final HashMap<String,String> attributeMap;    
    private final StringBuilder elementBuffer;        
    private final DecsSyn decsSyn;
    private final StringBuilder path;        
    private final Analyzer analyzer;
    private final Map<String,DecsSyn> map;
    
    public IndexDecs() throws IOException {        
        attributeMap = new HashMap<String,String>();
        elementBuffer = new StringBuilder();
        decsSyn = new DecsSyn();
        path = new StringBuilder("/");
        analyzer = new SimpleKeywordAnalyzer();
        map = new HashMap<String,DecsSyn>();
    }
    
    public HashMap<String,DecsSyn> indexTerms(final String xml) 
                                            throws ParserConfigurationException, 
                                                     SAXException, IOException {
        if (xml == null) {
            throw new NullPointerException("xml");
        }
        final SAXParserFactory factory = SAXParserFactory.newInstance();                        
        final InputSource xmlInput = new InputSource(xml);
        xmlInput.setEncoding("ISO-8859-1");
        
        final SAXParser sax = factory.newSAXParser();
        final XMLReader reader = sax.getXMLReader();
            
        map.clear();
        
        reader.setEntityResolver(null);
        reader.setContentHandler(this);
        reader.parse(xmlInput);
        
        return new HashMap<String,DecsSyn>(map);
    }
    
    @Override
    public void startDocument() {
        attributeMap.clear();
    }
    
    @Override
    public void startElement(final String uri, 
                             final String localName, 
                             final String qName, 
                             final Attributes atts) throws SAXException {
        
        path.append(qName).append("/");
        
        if (qName.equals("term")) {
            decsSyn.clear();
            decsSyn.setId(atts.getValue("mfn"));
        }
        
        elementBuffer.setLength(0);
        attributeMap.clear();
        if (atts.getLength() > 0) {
            for (int i = 0; i < atts.getLength(); i++) {
                attributeMap.put(atts.getQName(i), atts.getValue(i));
            }
        }
    }
    
    @Override
    public void characters(final char[] text, 
                           final int start, 
                           final int length) {
        elementBuffer.append(text, start, length);
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, 
                                    final int start, 
                                    final int length) throws SAXException {        
    }
    
    @Override
    public void endElement(final String uri, 
                           final String localName, 
                           final String qName) throws SAXException {
        
        final String text = elementBuffer.toString();
        
        try {
            if (qName.equals("descriptor")) {
                decsSyn.addDescriptor(getNormText(text));            
            } else if (qName.equals("synonym")) {
                decsSyn.addSynonym(getNormText(text));            
            } else if( qName.equals("category")) {
                decsSyn.addCategory(getNormText(text));            
            } else if (qName.equals("abbreviation")) {
                decsSyn.setAbbreviation(getNormText(text));
            } else if (qName.equals("term")) {            
                put((DecsSyn)decsSyn.clone());
            //} else {
            //    throw new IOException("qName=" + qName);
            }
            
        } catch(IOException ioe) {
            Logger.getGlobal().warning(ioe.getMessage());
            //ioe.printStackTrace();
        }
    }
    
    private String getNormText(final String in) throws IOException {
        assert in != null;
        
        return AnalyzerUtils.getTokens(analyzer, in);
    }
    
    private void put(final DecsSyn dSyn) throws IOException {
        assert dSyn != null;
        
        for (String term : dSyn.getDescriptor()) {
            final DecsSyn other = map.put(term, dSyn);
            if ((other != null) && !dSyn.equals(other)) {
                throw new IOException("previous key[" + term + "] found.");
            }
        }
        for (String term : dSyn.getSynonym()) {
            final DecsSyn other = map.put(term, dSyn);
            if ((other != null) && !dSyn.equals(other)) {
                throw new IOException("previous key[" + term + "] found.");
            }
        }
        final String abbr = dSyn.getAbbreviation();        
        if (abbr != null) {
            final DecsSyn other = map.put(abbr, dSyn);
            if ((other != null) && !dSyn.equals(other)) {
                throw new IOException("previous key[" + abbr + "] found.");
            }
        }        
        final String id = dSyn.getId();
        if (id != null) {
            final DecsSyn other = map.put(id, dSyn);
            if ((other != null) && !dSyn.equals(other)) {
                throw new IOException("previous key[" + id + "] found.");
            }
        }
    }    
    
    public static void main(String args[]) throws Exception {
        new IndexDecs().indexTerms("resources/decs/xml/decs-metadata.xml");        
    }
}
