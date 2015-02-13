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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;

/**
 *
 * @author Heitor Barbieri
 * date: 20150122
 */
public class DeCSQualifierAnalyzer extends Analyzer {   
    private class DeCSTokenStreamComponents extends TokenStreamComponents {

        public DeCSTokenStreamComponents(final Reader reader) 
                                                            throws IOException {            
            super(new DeCSTokenizer(reader, decs, parameters, true));
        }
        
        @Override
        protected void setReader(final Reader reader) throws IOException {
            assert reader != null;
            
            ((DeCSTokenizer)getTokenizer())
                                    .fillDeque(new RemoveAccentsReader(reader));            
        }
    }
    
    private final DecsParams parameters;
    private final Map<String,DecsSyn> decs;
    
    public DeCSQualifierAnalyzer(final Map<String,DecsSyn> decs) {
        if (decs == null) {
            throw new NullPointerException("decs");
        }        
        this.parameters = new DecsParams(false, true, false, true, true, false);
        this.decs = decs;
    }

    @Override
    protected TokenStreamComponents createComponents(final String string, 
                                                     final Reader reader) {
        assert string != null;
        assert reader != null;
        
        TokenStreamComponents tsc;
        
        try {
            final Reader filter = (new RemoveAccentsReader(reader));
            tsc = new DeCSTokenStreamComponents(filter);
        } catch (IOException ex) {
            tsc = null;
            Logger.getLogger(DeCSQualifierAnalyzer.class.getName())
                                                   .log(Level.SEVERE, null, ex);
        }
        
        return tsc;
    }        
}
