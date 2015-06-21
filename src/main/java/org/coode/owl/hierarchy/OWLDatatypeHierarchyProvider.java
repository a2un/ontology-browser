package org.coode.owl.hierarchy;

import org.coode.owl.mngr.OWLServer;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class OWLDatatypeHierarchyProvider implements HierarchyProvider<OWLDatatype> {

    private OWLServer server;

    private OWLDatatype root;

    public OWLDatatypeHierarchyProvider(OWLServer server) {
        this.server = server;
        this.root = server.getOWLOntologyManager().getOWLDataFactory().getTopDatatype();
    }

    public Class<? extends OWLDatatype> getNodeClass() {
        return OWLDatatype.class;
    }

    public Set<OWLDatatype> getRoots() {
        return Collections.singleton(root);
    }

    public boolean isRoot(OWLDatatype node) {
        return node.equals(root);
    }

    public boolean isLeaf(OWLDatatype node) {
        return getChildren(node).isEmpty();
    }

    public Set<OWLDatatype> getParents(OWLDatatype node) {
        if (isRoot(node)){
            return Collections.emptySet();
        }
        else{
            return getRoots();
        }
    }

    public Set<OWLDatatype> getChildren(OWLDatatype node) {
        Set<OWLDatatype> subs = new HashSet<OWLDatatype>();
        if (isRoot(node)){
            for (OWLOntology ont : server.getOntologies()){
                subs.addAll(ont.getDatatypesInSignature());
            }
        }
        return subs;
    }

    public Set<OWLDatatype> getEquivalents(OWLDatatype node) {
        return Collections.emptySet();
    }

    public Set<OWLDatatype> getDescendants(OWLDatatype node) {
       return getChildren(node);
    }

    public Set<OWLDatatype> getAncestors(OWLDatatype node) {
        return getParents(node);
    }

    public boolean hasAncestor(OWLDatatype node, OWLDatatype ancestor) {
        return getAncestors(node).contains(ancestor);
    }

    public void dispose() {
        server = null;
    }
}