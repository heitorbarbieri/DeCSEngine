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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author vinicius.andrade
 * date: 20060721
 */
public class DecsSyn {       
    private final Set<String> category;
    private final List<String> descriptor;
    private final Set<String> synonym;
    private String id;
    private String treeId;
    private String abbreviation;
    
    /** Creates a new instance of Term */
    public DecsSyn() {
        category = new TreeSet<String>();
        descriptor = new ArrayList<String>();
        synonym = new TreeSet<String>();
    }
    
    public void clear() {
        category.clear();
        descriptor.clear();
        synonym.clear();
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public void addCategory(String category) {
        if (category != null) {
            this.category.add(category);
        }
    }
    
    public void addDescriptor(String descriptor) {
        if (descriptor != null) {
            this.descriptor.add(descriptor);
        }
    }
    
    public void addSynonym(String synonym) {
        if (synonym != null) {
            this.synonym.add(synonym);
        }
    }
    
    public String getId() {
        return id;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getTreeId() {
        return treeId;
    }
    
    public Set<String> getCategory() {
        //return category;
        return new HashSet<String>(category);
    }
    
    public List<String> getDescriptor() {
        //return descriptor;
        return new ArrayList<String>(descriptor);
    }
    
    /*
     * Description: retorna o descritor por idioma (1 - inglês, 2-espanhol, 3-português)
     */
    public String getDescriptor(final String lang) {
        final String desc;
        
        if (lang.equals("en")) {
            desc = descriptor.get(0);
        } else if (lang.equals("es")) {
            desc = this.descriptor.get(1);
        } else if (lang.equals("pt")) {
            desc = this.descriptor.get(2);
        } else {
            desc = null;
        }
        
        return desc;
    }
    
    public Set<String> getSynonym() {
        //return synonym;
        return new HashSet<String>(synonym);
    }    
    
    @Override
    public Object clone() {
        final DecsSyn syn = new DecsSyn();
        
        for (String cat : category) {
            syn.addCategory(cat);
        }
        for (String decs: descriptor) {
            syn.addDescriptor(decs);
        }
        for (String sy: synonym) {
            syn.addSynonym(sy);
        }
        syn.setId(id);
        syn.setTreeId(treeId);
        syn.setAbbreviation(abbreviation);
        
        return syn;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.category);
        hash = 29 * hash + Objects.hashCode(this.descriptor);
        hash = 29 * hash + Objects.hashCode(this.synonym);
        hash = 29 * hash + Objects.hashCode(this.id);
        hash = 29 * hash + Objects.hashCode(this.treeId);
        hash = 29 * hash + Objects.hashCode(this.abbreviation);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DecsSyn other = (DecsSyn) obj;
        if (!Objects.equals(this.category, other.category)) {
            return false;
        }
        if (!Objects.equals(this.descriptor, other.descriptor)) {
            return false;
        }
        if (!Objects.equals(this.synonym, other.synonym)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.treeId, other.treeId)) {
            return false;
        }
        if (!Objects.equals(this.abbreviation, other.abbreviation)) {
            return false;
        }
        return true;
    }
}
