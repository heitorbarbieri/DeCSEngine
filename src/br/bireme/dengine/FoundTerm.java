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

/**
 *
 * @author Heitor Barbieri
 * date: 20141114
 */
public class FoundTerm implements Comparable<FoundTerm>{
    private final int beginPos;
    private final int endPos;
    private final String term;
    private final DecsSyn syn;
    private final FoundTerm qualifier;
    private final boolean samePos;

    public FoundTerm(final String term, 
                     final int beginPos, 
                     final int endPos,
                     final DecsSyn syn,
                     final FoundTerm qualifier,
                     final boolean samePos) {
        if (term == null) {
            throw new NullPointerException("term");
        }
        if (beginPos < 0) {
            throw new IllegalArgumentException("beginPos[" + beginPos 
                                                                     + "] < 0");
        }
        if (endPos < beginPos) {
            throw new IllegalArgumentException("endPos[" + endPos 
                                            + "] < beginPos[" + beginPos + "]");
        }
        this.term = term;
        this.beginPos = beginPos;
        this.endPos = endPos;
        this.syn = syn;
        this.qualifier = qualifier;
        this.samePos = samePos;
    }    

    public int getBeginPos() {
        return beginPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public String getTerm() {
        return term;
    }    
    
    public DecsSyn getSyn() {
        return syn;
    }
    
    public FoundTerm getQualifier() {
        return qualifier;
    }
    
    public boolean getSamePos() {
        return samePos;
    }
    
    @Override
    public int compareTo(final FoundTerm t) {
        final int i1 = (beginPos - t.getBeginPos());
        
        return (i1 == 0) ? term.compareTo(t.getTerm()) : i1;        
    }
}
