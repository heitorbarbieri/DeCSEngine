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

/**
 *
 * @author Heitor Barbieri
 * date: 20141126
 */
public class RemoveAccentsReader extends Reader {
    private final Reader in;
    
    public RemoveAccentsReader(final Reader in) {
        if (in == null) {
            throw new NullPointerException("in");
        }
        this.in = in;
    }
    
    @Override
    public int read(final char[] chars, 
                    final int off, 
                    final int len) throws IOException {
        
        final int ret;
        final int tot = in.read(chars, off, len);
        
        if (tot == -1) {
            ret = -1;
        } else {            
            final String inStr = new String(chars, off, tot);
            final String outStr = RemoveAccentsFromString.filter(inStr);
            
            ret = outStr.length();
            for (int idx = off; idx < ret; idx++) {
                chars[idx] = outStr.charAt(idx);
            }
        }        
        
        return ret;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
    
}
