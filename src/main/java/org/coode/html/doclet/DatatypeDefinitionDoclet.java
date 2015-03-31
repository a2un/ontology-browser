package org.coode.html.doclet;

import org.coode.www.kit.OWLHTMLKit;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Aug 5, 2009<br><br>
 */
public class DatatypeDefinitionDoclet extends AbstractOWLElementsDoclet<OWLDatatype, OWLDataRange> {

    public DatatypeDefinitionDoclet(OWLHTMLKit kit) {
        super("Datatype Definitions", Format.list, kit);
    }


    protected Collection<OWLDataRange> getAssertedElements(Set<OWLOntology> ontologies) {
        Set<OWLDataRange> ranges = new HashSet<OWLDataRange>();
        for (OWLOntology ont : ontologies){
            for (OWLDatatypeDefinitionAxiom ax : ont.getDatatypeDefinitions(getUserObject())){
                ranges.add(ax.getDataRange());
            }
        }
        return ranges;
    }
}
