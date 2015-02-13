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
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 *
 * @author Heitor Barbieri
 * date: 20141023
 */
public class LimitTokenSizeFilter extends TokenFilter {
    private final CharTermAttribute termAtt;
    private int maxSize;
    
     public LimitTokenSizeFilter(final TokenStream input,
                                 final int maxSize) {
        super(input);
        
        if ((maxSize < 1) || 
            (maxSize > StandardAnalyzer.DEFAULT_MAX_TOKEN_LENGTH)) {
            throw new IllegalArgumentException("input=" + input);
        }
        this.termAtt = addAttribute(CharTermAttribute.class);
        this.maxSize = maxSize;
    }
     
    @Override
    public final boolean incrementToken() throws IOException {
        final boolean ret;
        
        if (input.incrementToken()) {
            if (termAtt.length() > maxSize) {
                termAtt.setLength(maxSize);
            }
            ret = true;
        } else {
            ret = false;
        }
        
        return ret;
    }
    
}
