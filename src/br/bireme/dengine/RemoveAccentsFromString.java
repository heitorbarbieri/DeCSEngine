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
import java.text.Normalizer;
import java.text.Normalizer.Form;

/**
 *
 * @author Heitor Barbieri
 * date: 20141114
 */
public class RemoveAccentsFromString {
    public static String removeAccents(final String text) {
        return text == null ? null :
            Normalizer.normalize(text, Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
    
    public static String filter(final String text) {
        return text == null ? null : removeAccents(text).toLowerCase();
    }
    
    public static String filter(final Reader reader) throws IOException {
        if (reader == null) {
            throw new NullPointerException("reader");
        }
        final StringBuilder builder = new StringBuilder();
        final char[] buffer = new char[100];
        
        while (true) {
            final int tot = reader.read(buffer);
            
            if (tot == -1) {
                break;
            }
            builder.append(filter(new String(buffer, 0, tot)));
        }
        reader.close();
        
        return builder.toString();
    }
}
