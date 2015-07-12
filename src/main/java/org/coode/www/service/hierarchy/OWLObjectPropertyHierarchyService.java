package org.coode.www.service.hierarchy;

import org.coode.www.model.Tree;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.Comparator;
import java.util.Set;

public class OWLObjectPropertyHierarchyService extends AbstractOWLHierarchyService<OWLObjectPropertyExpression> {

    private final OWLReasoner reasoner;

    public OWLObjectPropertyHierarchyService(final OWLReasoner reasoner,
                                             final Comparator<? super Tree<OWLObjectPropertyExpression>> comparator) {
        super(comparator);
        this.reasoner = reasoner;
    }

    @Override
    protected Node<OWLObjectPropertyExpression> topNode() {
        return reasoner.getTopObjectPropertyNode();
    }

    @Override
    protected Set<Node<OWLObjectPropertyExpression>> subs(OWLObjectPropertyExpression prop) {
        return reasoner.getSubObjectProperties(prop, true).getNodes();
    }

    @Override
    protected Set<Node<OWLObjectPropertyExpression>> ancestors(OWLObjectPropertyExpression prop) {
        return reasoner.getSuperObjectProperties(prop, false).getNodes();
    }

    @Override
    protected Node<OWLObjectPropertyExpression> equivs(OWLObjectPropertyExpression prop) {
        return reasoner.getEquivalentObjectProperties(prop);
    }
}
