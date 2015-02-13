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

import static br.bireme.dengine.DeCSTermLocator.MAX_TOKEN_SIZE;
import static br.bireme.dengine.DeCSTermLocator.MIN_TOKEN_SIZE;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Check if the entire sentence (no break) is a DeCS term.
 * @author Heitor Barbieri
 * date: 20150205
 */
public class DeCSSentenceTermLocator implements DeCSLocator {
    
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
        return getTerms(str, MAX_TOKEN_SIZE, MIN_TOKEN_SIZE, decs, 
                                                               onlyPrecodTerms);
    }
    
    /**
     * 
     * @param str - the input string
     * @param maxTokenSize - the maximum token size
     * @param minTokenSize - the minimum token size
     * @param decs - set of DeCS descrptors and qualifiers
     * @param onlyPrecodTerms - process only precodified terms ( ^d11111 )
     * @return a set with one or no foundTerm
     * 
     */
    public Set<FoundTerm> getTerms(final String str,
                                   final int maxTokenSize,
                                   final int minTokenSize,
                                   final Map<String,DecsSyn> decs,
                                   final boolean onlyPrecodTerms) {                
        if (str == null) {
            throw new NullPointerException("str");
        }
        if (decs == null) {
            throw new NullPointerException("decs");
        }
        
        final TreeSet<FoundTerm> foundTerms = new TreeSet<FoundTerm>(new 
                                                         FoundTermComparator());
        final int minTSize = Math.max(minTokenSize, MIN_TOKEN_SIZE); 
        final int maxTSize = Math.min(Math.min(maxTokenSize, str.length()), 
                                                                MAX_TOKEN_SIZE);
        final int tsize = str.length();
        
        if ((tsize >= minTSize) && (tsize <= maxTSize)) {       
            if (onlyPrecodTerms) {
                addPrecodTerms(str, decs, foundTerms);
            } else {
                searchTerm(str, decs, foundTerms);
                if (foundTerms.isEmpty()) {
                    addPrecodTerms(str, decs, foundTerms);
                }
            }
            
        }
        
        return foundTerms;
    }
    
    private static void searchTerm(final String in,
                                   final Map<String,DecsSyn> decs,
                                   final TreeSet<FoundTerm> foundTerms) {
        assert in != null;
        assert decs != null;
        assert foundTerms != null;
        
        final int pEnd = in.length() - 1;
        final String token = in.trim();
        final int slash = in.indexOf('/');
        final int tslash = token.indexOf('/');
        final String term;
        final String qualif;

        if (tslash == -1) {
            term = token;
            qualif = null;
        } else {
            term = token.substring(0, tslash);
            qualif = token.substring(tslash);
        }
        final DecsSyn tsyn = decs.get(term);

        if (tsyn != null) { // Found a DeCS token
            if (qualif == null) {
                foundTerms.add(new FoundTerm(token, 0, pEnd, tsyn, null, 
                                                                    false));
            } else {
                final DecsSyn qsyn = decs.get(qualif);
                if (qsyn != null) { // Found a DeCS qualifier
                    final FoundTerm fqual = new FoundTerm(qualif, slash,
                                                          pEnd, qsyn, 
                                                           null, false);
                    foundTerms.add(new FoundTerm(term, 0, slash-1, tsyn, 
                                                             fqual, false));
                }       
            }                                                            
        }
    }
    
    static void addPrecodTerms(final String str,
                               final Map<String,DecsSyn> decs,
                               final TreeSet<FoundTerm> foundTerms) {
        assert str != null;
        assert decs != null;
        assert foundTerms != null;
        
        final Matcher mat = Pattern.compile("\\s*(\\^[dD])" + 
                            "(\\d{1,7})\\s*").matcher("");
        mat.reset(str);
        if (mat.matches()) {
            final String id = mat.group(2);
            final DecsSyn dSym = decs.get(id);
            if (dSym != null) {
                final FoundTerm term = new FoundTerm("^d" + id, mat.start(1), 
                                         mat.end(2) - 1, dSym, null, false);
                foundTerms.add(term);
            }
        }
    }
}
