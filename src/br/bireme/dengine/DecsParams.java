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
 * date: 20141007
 */
public class DecsParams {
    final boolean addCategory;            // gera token para categoria 
    final boolean addSyn;                 // gera tokens para sinonimos
    final boolean addWords;               // gera tokens quebrando em palavras os tokens gerados
    final boolean keysForQualifiers;      // gera tokens para os qualificadores
    final boolean onlyQualifiers;
    final boolean processOnlyPrecodTerms; // somente gera tokens para descritores que estiverem
                                          // precodificados no formato ^didentificador^squalificador

    public DecsParams(final boolean addCategory, 
                      final boolean addSyn, 
                      final boolean addWords, 
                      final boolean keysForQualifiers, 
                      final boolean onlyQualifiers, 
                      final boolean processOnlyPrecodTerms) {
        this.addCategory = addCategory;
        this.addSyn = addSyn;
        this.addWords = addWords;
        this.keysForQualifiers = keysForQualifiers;
        this.onlyQualifiers = onlyQualifiers;
        this.processOnlyPrecodTerms = processOnlyPrecodTerms;
    }    
}
