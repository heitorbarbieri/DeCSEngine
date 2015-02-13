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
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

/**
 *
 * @author Heitor Barbieri
 * date: 20141114
 */
public class DeCSTokenizer extends Tokenizer {
    public static final int DEF_MIN_TOKEN_SIZE = 2;
    
    private final Map<String,DecsSyn> decs;
    private final DecsParams parameters;
    private final DeCSLocator locator;
    private final Deque<FoundTerm> tokenDeque;
    private final OffsetAttribute offsetAtt;
    private final CharTermAttribute termAtt;
    private final PositionIncrementAttribute posIncrAtt;
    private final int minTokSize;
    private final boolean breakSentence;
    
    private Iterator<FoundTerm> allTokens;
    
    public DeCSTokenizer(final Reader input,
                         final Map<String,DecsSyn> decs,
                         final DecsParams parameters,
                         final boolean breakSentence) throws IOException {
        this(input, decs, parameters, breakSentence, DEF_MIN_TOKEN_SIZE);
    }
    
    public DeCSTokenizer(final Reader input,
                         final Map<String,DecsSyn> decs,
                         final DecsParams parameters,
                         final boolean breakSentence,
                         final int minTokenSize) throws IOException {
        super(input);
        
        if (decs == null) {
            throw new NullPointerException("decs");
        }
        if (parameters == null) {
            throw new NullPointerException("parameters");
        } 
        if (minTokenSize < 1) {
            throw new IllegalArgumentException("token size[" + minTokenSize  + 
                                                                       "] < 1");
        }
        
        this.decs = decs;
        this.parameters = parameters;
        this.locator = breakSentence ? new DeCSTermLocator() 
                                     : new DeCSSentenceTermLocator();  
        this.minTokSize = minTokenSize;
        this.tokenDeque = new ArrayDeque<FoundTerm>();        
        fillDeque(input);        
        this.offsetAtt = addAttribute(OffsetAttribute.class);
        this.termAtt = addAttribute(CharTermAttribute.class);
        this.posIncrAtt = addAttribute(PositionIncrementAttribute.class); 
        this.breakSentence = breakSentence;
        input.close();
    }
        
    @Override
    public final boolean incrementToken() throws IOException {
        final boolean ret;
        
        if (allTokens.hasNext()) {
            final FoundTerm term = allTokens.next();
            
            offsetAtt.setOffset(term.getBeginPos(), term.getEndPos() + 1);
            termAtt.setEmpty();
            termAtt.append(term.getTerm());
            posIncrAtt.setPositionIncrement(term.getSamePos() ? 0 : 1);
            ret = true;
        } else {
            ret = false;
        }
        
        return ret;
    }
    
    final void fillDeque(final Reader input) throws IOException {
        assert input != null;
        assert tokenDeque != null;

        tokenDeque.clear();
        final String in = getReaderContent(input);        
        if (!in.isEmpty()) {
            final Set<FoundTerm> fTerms = locator.getTerms(in, decs, 
                                  parameters.processOnlyPrecodTerms);
            final Iterator<FoundTerm> terms = fTerms.iterator();
            getTokens(in, 0, in.length() - 1, parameters, terms, tokenDeque);            
        }        
        allTokens = tokenDeque.iterator();
    }
    
    private String getReaderContent(final Reader reader) throws IOException {
        assert reader != null;
        
        final StringBuilder builder = new StringBuilder();
        final char[] chars = new char[256];
        
        while (true) {
            final int charsRead = reader.read(chars);
            if (charsRead == -1) {
                break;
            }
            builder.append(chars, 0, charsRead);
        }
        reader.close();
        
        return builder.toString();
    }
    
    private void getTokens(final String in,
                           final int init,
                           final int end,                               
                           final DecsParams parameters,
                           final Iterator<FoundTerm> terms,
                           final Deque<FoundTerm> tokenDeque) {
        assert in != null;
        assert init >= 0;
        assert end >= init;        
        assert parameters != null;        
        assert terms != null;
        assert tokenDeque != null;
        
        if (terms.hasNext()) {
            final FoundTerm fterm = terms.next();
            final FoundTerm qfterm = fterm.getQualifier();
            final int beginPos = fterm.getBeginPos();
            final int endPos = (qfterm == null) ? fterm.getEndPos()
                                                : qfterm.getEndPos();
            
            if (breakSentence) {
                if (init < beginPos) {
                    addStrTokens(in, init, beginPos - 1, tokenDeque);
                }
                addDeCSTokens(fterm, parameters, tokenDeque);
                if (endPos < end) {
                    getTokens(in, endPos + 1, end, parameters, terms, tokenDeque);
                }
            } else {
                addDeCSTokens(fterm, parameters, tokenDeque);
            }
        } else {
            
            addStrTokens(in, init, end, tokenDeque);
        }                
    }
    
    private void addDeCSTokens(final FoundTerm fterm,
                               final DecsParams parameters,
                               final Deque<FoundTerm> tokenDeque) {
        assert fterm != null;
        assert parameters != null;
        assert tokenDeque != null;
        
        final TreeSet<FoundTerm> tokenSet = new TreeSet<FoundTerm>();
        final FoundTerm qualifier = fterm.getQualifier();
        
        if (qualifier == null) {
            if (parameters.onlyQualifiers) {
                // Do nothing
            } else {
                includeDescriptors(fterm, parameters, tokenSet);
                includeOtherFields(fterm, parameters, tokenSet);
            }
        } else {
            if (! parameters.onlyQualifiers) {
                includeDescriptors(fterm, parameters, tokenSet);
                includeOtherFields(fterm, parameters, tokenSet);
            }           
            if (parameters.keysForQualifiers) {
                includeDescriptors(qualifier, parameters, tokenSet);
            }
            includeOtherFields(qualifier, parameters, tokenSet);
            includeJoins(fterm, parameters, tokenSet);
        }
        for (FoundTerm foterm : tokenSet) {
            tokenDeque.addLast(foterm);
        }
    }
    
    private void includeDescriptors(final FoundTerm fterm,
                                    final DecsParams parameters,
                                    final TreeSet<FoundTerm> tokenSet) {
        assert fterm != null;
        assert parameters != null;
        assert tokenSet != null;
        
        final DecsSyn syn = fterm.getSyn();        
        final List<String> descriptors = syn.getDescriptor();
        final String term = fterm.getTerm();
        
        for (int idx = 0; idx < descriptors.size(); idx++) {
            final String descriptor = descriptors.get(idx);  
            splitWords(descriptor, fterm, parameters, idx != 0, tokenSet);            
        }
        
        // Include term ^d111111        
        if (term.startsWith("^d")) {
            splitWords(term, fterm, parameters, true, tokenSet);
        }                
    }
    
    /**
     * Add 'descriptor/qualifier' tokens
     * @param fterm
     * @param parameters
     * @param tokenSet 
     */
    private void includeJoins(final FoundTerm fterm,
                              final DecsParams parameters,
                              final TreeSet<FoundTerm> tokenSet) {
        assert fterm != null;
        assert parameters != null;
        assert tokenSet != null;
     
        if ((! parameters.onlyQualifiers) && (parameters.keysForQualifiers)) {            
            final FoundTerm qualifier = fterm.getQualifier();
            
            if (qualifier != null) {
                final DecsSyn qsyn = qualifier.getSyn();
                final DecsSyn syn = fterm.getSyn();
                final List<String> descriptors = syn.getDescriptor();
                final List<String> qdescriptors = qsyn.getDescriptor();
                //final TreeSet<String> sdescriptors = (TreeSet<String>)syn.getSynonym();
                final int qdsize = qdescriptors.size();
                
                for (int idx = 0; idx < qdsize; idx++) {
                    final String join = descriptors.get(idx) + 
                                                          qdescriptors.get(idx);
                    tokenSet.add(new FoundTerm(join, fterm.getBeginPos(), 
                                                     qualifier.getEndPos(), syn, 
                                                              qualifier, true));
                }
                /*
                for (int idx = 0; idx < qdsize; idx++) {
                    final String join = sdescriptors.get(idx) + 
                                                          qdescriptors.get(idx);
                    splitWords(join, fterm, parameters, true, tokenSet);
                }
                */
            }
        }
    }
    
    /**
     * Add Synonyns and category
     * @param fterm
     * @param parameters
     * @param tokenSet 
     */
    private void includeOtherFields(final FoundTerm fterm,
                                    final DecsParams parameters,
                                    final TreeSet<FoundTerm> tokenSet) {
        assert fterm != null;
        assert parameters != null;
        assert tokenSet != null;
        
        final DecsSyn syn = fterm.getSyn();
        
        if (parameters.addSyn) {
            final Set<String> synonyns = syn.getSynonym();
            if (synonyns != null) {
                for (String synonym : synonyns) {
                    splitWords(synonym, fterm, parameters, true, tokenSet);
                }
            }
        }        
        if (parameters.addCategory) {
            final Set<String> categories = syn.getCategory();
            if (categories != null) {
                for (String category : categories) {
                    splitWords(category, fterm, parameters, true, tokenSet);
                }
            }
        }
    }
        
    private void splitWords(final String str,
                            final FoundTerm fterm,
                            final DecsParams parameters,
                            final boolean samePos,
                            final TreeSet<FoundTerm> tokenSet) {
    
        assert str != null;
        assert fterm != null;
        assert parameters != null;
        assert tokenSet != null;
            
        final DecsSyn syn = fterm.getSyn();
        final int begin = fterm.getBeginPos();
        final int end = fterm.getEndPos();
        final FoundTerm qualifier = fterm.getQualifier();
        
        final String strTrim = str.trim();
        final String str2;
        
        if (strTrim.charAt(0) == '/') {
            tokenSet.add(new FoundTerm(strTrim, begin, end, syn, qualifier, 
                                                                      samePos));
            str2 = strTrim.substring(1);
        } else {
            str2 = strTrim;
        }
        
        tokenSet.add(new FoundTerm(str2, begin, end, syn, qualifier, samePos));
        
        if (parameters.addWords) {
            final String[] splitSpace = str2.split("[\\s\\-]+");
            if (splitSpace.length > 1) {
                for (String str3 : splitSpace) {
                    tokenSet.add(new FoundTerm(str3, begin, end, syn, 
                                                          qualifier, true));
                }              
            }
        }        
    }

    private void addStrTokens(final String in,
                              final int init,
                              final int end,
                              final Deque<FoundTerm> tokenDeque) {
        assert in != null;
        assert init >= 0;
        assert end >= 0;
        assert tokenDeque != null;
        
        if (breakSentence) {
            if (init <= end) {
                if (in.charAt(init) == ' ') {
                    addStrTokens(in, init + 1, end, tokenDeque);
                } else if (init < end) {
                    final int pos2 = findEndTokenPos(in, init, end);
                    if (pos2 == -1) {
                        if (end - init + 1 >= minTokSize) {
                            tokenDeque.addLast(new FoundTerm(
                                                 in.substring(init, end + 1),
                                                 init, end, null, null, false));
                        }
                    } else {
                        if (pos2 - init + 1 >= minTokSize) {
                            tokenDeque.addLast(new FoundTerm(
                                                in.substring(init, pos2 + 1),
                                                init, pos2, null, null, false));
                        }
                        if (pos2 < end) {
                            addStrTokens(in, pos2 + 1, end, tokenDeque);
                        }
                    }
                } else { // init == end. 
                    if (minTokSize >= 1) {
                        tokenDeque.addLast(new FoundTerm(
                                                 in.substring(init, end + 1),
                                                 init, end, null, null, false));
                    }
                }
            }
        } else {
            final String str = in.substring(init, end + 1).trim();
            if (str.length() >= minTokSize) {
                final FoundTerm nterm = new FoundTerm(str, init, end, null, 
                                                                   null, false);
                tokenDeque.add(nterm);
            }
        }
    }
    
    private int findEndTokenPos(final String in,
                                final int init,
                                final int end) {
        assert in != null;
        assert init >= 0;
        assert end >= 0;
        
        final int ret;
        
        if (init > end) {
            ret = -1;
        } else {
            if (in.charAt(init) == ' ') {
                ret = -1;
            } else {
                final int ret2 = findEndTokenPos(in, init + 1, end);
                ret = (ret2 == -1) ? init : ret2;
            }
        }
        
        return ret;
    }        
}
