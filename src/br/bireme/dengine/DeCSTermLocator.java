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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Heitor Barbieri
 * date: 20150116
 */
public class DeCSTermLocator implements DeCSLocator {
    /**
     * maximum DeCS token size
     */
    public static final int MAX_TOKEN_SIZE = 130;
    /**
     * minimum DeCS token size
     */
    public static final int MIN_TOKEN_SIZE = 2;
            
    static final Character[] ACCEPTABLE_DELIM_ARRAY = new Character[] {
        '\"', '\'', '!', '@', '#', '$', '%', '"', '&', '*', '(', ')', '_', '-',
        '+', '=', '§', '\'', '`', '{', '[', 'ª', '^', '~', '}', ']', 'º', '<',
        ',', '>', '.', ':', ';', '?', '/', '|', '\\' };
        
    static final Character[] WHITE_DELIM_ARRAY = new Character[] {
        ' ', '\t', '\r', '\n', '\f' };
    
    static final Set<Character> ACCEPTABLE_DELIMITER = 
                  new HashSet<Character>(Arrays.asList(ACCEPTABLE_DELIM_ARRAY));        
    
    static final Set<Character> ACCEPTABLE_WHITE_DELIMITER = 
                       new HashSet<Character>(Arrays.asList(WHITE_DELIM_ARRAY));
            
    /**
     *
     * @param str - the input string
     * @param decs - set of DeCS descrptors and qualifiers
     * @param onlyPrecodTerms - process only precodified terms ( ^d11111 )
     * @return set of DeCS terms found in input string
     */
    @Override
    public Set<FoundTerm> getTerms(final String str,
                                   final Map<String,DecsSyn> decs,
                                   final boolean onlyPrecodTerms) {
        final TreeSet<FoundTerm> foundTerms = new TreeSet<FoundTerm>(new 
                                                         FoundTermComparator());        
        getTerms(str, MAX_TOKEN_SIZE, MIN_TOKEN_SIZE, decs, onlyPrecodTerms,
                                                                    foundTerms);
        
        return foundTerms;
    }
    
    /**
     * 
     * @param str - the input string
     * @param maxTokenSize - the maximum token size
     * @param minTokenSize - the minimum token size
     * @param decs - set of DeCS descrptors and qualifiers
     * @param onlyPrecodTerms - process only precodified terms ( ^d11111 )
     * @param foundTerms - 
     * 
     */
    public static void getTerms(final String str,
                                final int maxTokenSize,
                                final int minTokenSize,
                                final Map<String,DecsSyn> decs,
                                final boolean onlyPrecodTerms,
                                final TreeSet<FoundTerm> foundTerms) {
        if (str == null) {
            throw new NullPointerException("str");
        }
        if (minTokenSize < 1) {
            throw new IllegalArgumentException("minTokenSize < 1");
        }        
        if (decs == null) {
            throw new NullPointerException("decs");
        }
        
        final int maxTSize = Math.min(Math.min(maxTokenSize, str.length()), 
                                                                MAX_TOKEN_SIZE);
        final int minTSize = Math.max(minTokenSize, MIN_TOKEN_SIZE); 
        final int endPos = str.length() - 1;
        
        if (!onlyPrecodTerms) {
            searchTermRoot(str.toCharArray(), 0, maxTSize, minTSize, endPos, 
                                                          decs, foundTerms);
        }
        addPrecodTerms(str, decs, foundTerms);
    }
    
    private static int searchTermRoot(final char[] in,
                                      final int curPos,
                                      final int tokenSize,
                                      final int minTokenSize,
                                      final int endPos,
                                      final Map<String,DecsSyn> decs,
                                      final TreeSet<FoundTerm> foundTerms) {
        assert in != null;
        assert curPos >= 0;
        assert minTokenSize > 0;
        assert endPos >= 0;
        assert decs != null;
        assert foundTerms != null;
        
        final int nextPos;
        final int endTokenPos = curPos + tokenSize - 1;

//System.out.println("#curPos=" + curPos + " tokenSize=" + tokenSize);
        
        if (tokenSize < minTokenSize) {
            nextPos = -1;
        } else if (endTokenPos <= endPos) {
            final int pStart = possibleStart(in, curPos);
            final int pEnd = possibleEnd(in, endTokenPos, endPos);
            
            if ((pStart != -1) && (pEnd != -1)) { // Found a possible place for a token
                final String token = new String(in, curPos, tokenSize).trim();
                final int slash = token.indexOf('/');
                final String term;
                final String qualif;
                
                if (slash == -1) {
                    term = token;
                    qualif = null;
                } else {
                    term = token.substring(0, slash);
                    qualif = token.substring(slash);
                }
                
                final DecsSyn tsyn = decs.get(term);

                if (tsyn == null) { // Do not find a DeCS token
                    final int auxPos = searchTerm(in, pStart, curPos, 
                         tokenSize - 1, minTokenSize, endPos, decs, foundTerms);
                    if (auxPos == -1) {
                        nextPos = searchTermRoot(in, curPos + 1, tokenSize, 
                                        minTokenSize, endPos, decs, foundTerms);
                    } else {
                        nextPos = searchTermRoot(in, auxPos, tokenSize, 
                                        minTokenSize, endPos, decs, foundTerms);
                    }
                } else { // Found a DeCS token
                    if (qualif == null) {
                        foundTerms.add(new FoundTerm(token, pStart, pEnd, 
                                                            tsyn, null, false));
                        nextPos = searchTermRoot(in, curPos + tokenSize, 
                                                 tokenSize, minTokenSize, endPos, 
                                                               decs, foundTerms);
                    } else {
                        final DecsSyn qsyn = decs.get(qualif);
                        if (qsyn == null) { // Do not find a DeCS qualifier
                            final int auxPos = searchTerm(in, pStart, curPos, 
                                               tokenSize - 1, minTokenSize, 
                                               endPos, decs, foundTerms);
                            if (auxPos == -1) {
                                nextPos = searchTermRoot(in, curPos + 1, 
                                        tokenSize, minTokenSize, endPos, decs, 
                                                                    foundTerms);
                            } else {
                                nextPos = searchTermRoot(in, auxPos, tokenSize, 
                                        minTokenSize, endPos, decs, foundTerms);
                            }
                        } else { // Find a DeCS qualifier
                            final FoundTerm fqual = new FoundTerm(qualif, slash,
                                                                  pEnd, qsyn, 
                                                                   null, false);
                            foundTerms.add(new FoundTerm(term, pStart, 
                                                         curPos + slash-1, tsyn, 
                                                         fqual, false));
                            nextPos = searchTermRoot(in, curPos + tokenSize, 
                                                     tokenSize, minTokenSize, 
                                                     endPos, decs, foundTerms);
                        }       
                    }                                                            
                }
            } else { // Did not find a possible place for a token
                nextPos = searchTermRoot(in, curPos + 1, tokenSize, minTokenSize, 
                                                      endPos, decs, foundTerms);
            }        
        } else { // Not enought size for a DeCS token here
            nextPos = searchTermRoot(in, curPos, endPos - curPos + 1, 
                                        minTokenSize, endPos, decs, foundTerms);
        }
        
        return nextPos;
    }
    
    /**
     * Add to foundTerms a DeCS term if found in the input string from
     * current position inside a range size.
     * @param in - the input string
     * @param possibleStart - 
     * @param curPos - the current position in the input string
     * @param tokenSize - the supposed size of the token
     * @param minTokenSize - the minimum token size
     * @param decs - set of DeCS descrptors and qualifiers
     * @param foundTerms - set of DeCS terms found in input string
     */
    private static int searchTerm(final char[] in,
                                  final int possibleStart,
                                  final int curPos,
                                  final int tokenSize,
                                  final int minTokenSize,
                                  final int endPos,
                                  final Map<String,DecsSyn> decs,
                                  final TreeSet<FoundTerm> foundTerms) {
        assert in != null;
        assert possibleStart >= 0;
        assert curPos >= 0;
        assert tokenSize > 0;
        assert minTokenSize > 0;
        assert endPos >= 0;
        assert decs != null;
        assert foundTerms != null;
        
        final int nextPos;
        final int endTokenPos = curPos + tokenSize - 1;

//System.out.println("curPos=" + curPos + " possibleStart=" + possibleStart + " tokenSize=" + tokenSize);
        
        if ((tokenSize >= minTokenSize) && (endTokenPos <= endPos)) {
            final int pEnd = possibleEnd(in, endTokenPos, endPos);
            
            if (pEnd != -1) { // Found a possible place for a token
                final String token = new String(in, curPos, tokenSize).trim();
                final int slash = token.indexOf('/');
                final String term;
                final String qualif;
                
                if (slash == -1) {
                    term = token;
                    qualif = null;
                } else {
                    term = token.substring(0, slash);
                    qualif = token.substring(slash);
                }
                                
                final DecsSyn tsyn = decs.get(term);
                if (tsyn == null) { // Do not find a DeCS token
                    nextPos = searchTerm(in, possibleStart,curPos, tokenSize -1, 
                                        minTokenSize, endPos, decs, foundTerms);
                } else { // Found a DeCS token
                    if (qualif == null) {
                        foundTerms.add(new FoundTerm(token, possibleStart, 
                                                     pEnd, tsyn, null, false));
                        nextPos = curPos + tokenSize;
                    } else {
                        final DecsSyn qsyn = decs.get(qualif);
                        if (qsyn == null) { // Do not find a DeCS qualifier
                            nextPos = searchTerm(in, possibleStart ,curPos, 
                                              tokenSize-1, minTokenSize, endPos, 
                                                              decs, foundTerms);
                        } else { // Find a DeCS qualifier
                            final FoundTerm fqual = new FoundTerm(qualif, 
                                                        curPos + slash - 1,
                                                        pEnd,qsyn, null, false);
                            foundTerms.add(new FoundTerm(term, possibleStart, 
                                                         curPos + slash-1, tsyn, 
                                                         fqual, false));
                            nextPos = curPos + tokenSize;
                        }       
                    }
                }
            } else { // Did not find a possible place for a token
                nextPos = searchTerm(in, possibleStart, curPos, tokenSize - 1, 
                                        minTokenSize, endPos, decs, foundTerms);
            }        
        } else { // Not enought size for a DeCS token here
            nextPos = -1;
        }
        
        return nextPos;
    }

    /**
     * Check if a token can start here, i.e., preceded by a space followed by not 
     * alphanumerical characters.
     * @param in - the input string
     * @param pos - current position
     * @return position of the previous white char or 0 if the beginning of the
     * string or -1 if here is not a possible token start position.
     */
    static int possibleStart(final char[] in,
                             final int pos) {
        assert in != null;
        assert pos >= 0;
 
        final int whitePos;
        
        if (pos == 0) {
            whitePos = (ACCEPTABLE_WHITE_DELIMITER.contains(in[0])) ? -1 : 0;                
        } else {
            final Character ch = in[pos - 1];
            
            if (ACCEPTABLE_WHITE_DELIMITER.contains(ch)) {
                whitePos = pos - 1;
            } else {
                whitePos = ACCEPTABLE_DELIMITER.contains(ch) ?
                           possibleStart(in, pos - 1) : -1;
            }
        }
        
        return whitePos;
    }
    
    /**
     * Check if a token can end here, i.e., succeeded by alphanumerical 
     * charactersa and a space.
     * @param in - the input string
     * @param pos - current position
     * @param endPos - end string position
     * @return position before the next white char or endPos if the end of the
     * string or -1 if here is not a possible token end position.
     */
    static int possibleEnd(final char[] in,
                           final int pos,
                           final int endPos) {
        assert in != null;
        assert pos >= 0;
        assert endPos >= 0;
        
        final int whitePos;
        
        if (pos > endPos) {
            whitePos = -1;
        } else if (pos == endPos) {
            whitePos = endPos;
        } else {
            final Character ch = in[pos + 1];
            
            if (ACCEPTABLE_WHITE_DELIMITER.contains(ch)) {
                whitePos = pos;
            } else {
                whitePos = ACCEPTABLE_DELIMITER.contains(ch) ?
                                          possibleEnd(in, pos + 1, endPos) : -1;
            }
        }
        
        return whitePos;
    }
    
    static void addPrecodTerms(final String str,
                               final Map<String,DecsSyn> decs,
                               final TreeSet<FoundTerm> foundTerms) {
        assert str != null;
        assert decs != null;
        assert foundTerms != null;
        
        final Matcher mat = Pattern.compile("(?<=(\\s|^))([\\W&&\\S]*\\^[dD])" + 
                "(\\d{1,7})([\\W&&\\S]*|(\\^\\w\\S+)*)(?=(\\s|^))").matcher("");
        int lastPos = 0;     
        
        if (foundTerms.isEmpty()) {
            mat.reset(str);
            while (mat.find()) {
                final String id = mat.group(3);
                final DecsSyn dSym = decs.get(id);
                if (dSym != null) {
                    final FoundTerm term = new FoundTerm("^d" + id, mat.start(1), 
                                             mat.end(4) - 1, dSym, null, false);
                    foundTerms.add(term);
                }
            }
        } else {
            for (FoundTerm fterm : foundTerms) {
                final int curPos = fterm.getBeginPos();

                mat.reset(str.subSequence(lastPos, curPos));
                while (mat.find()) {
                    final String id = mat.group(3);
                    final DecsSyn dSym = decs.get(id);

                    if (dSym != null) {
                        final FoundTerm term = new FoundTerm("^d" + id, 
                                                   mat.start(1), mat.end(4) - 1, 
                                                   dSym, null, false);
                        foundTerms.add(term);
                    }
                }
                lastPos = curPos;
            }       
        }
    }
}
