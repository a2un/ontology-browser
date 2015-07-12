package org.coode.www.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class OWLClassHierarchyService {

    private final OWLReasoner reasoner;
    private final Comparator<? super Tree<OWLClass>> comparator;

    public OWLClassHierarchyService(final OWLReasoner reasoner,
                                    final Comparator<? super Tree<OWLClass>> comparator) {
        this.reasoner = reasoner;
        this.comparator = comparator;
    }

    public Tree<OWLClass> getPrunedTree(final OWLClass aClass) {
        NodeSet<OWLClass> ancestors = reasoner.getSuperClasses(aClass, false);
        Set<Node<OWLClass>> nodes = Sets.newHashSet(ancestors.getNodes());
        nodes.add(reasoner.getEquivalentClasses(aClass));
        return buildTree(reasoner.getTopClassNode(), new OWLClassNodeSet(nodes));
    }

    private Tree<OWLClass> buildTree(final Node<OWLClass> current, final NodeSet<OWLClass> ancestors) {
        List<Tree<OWLClass>> subs = Lists.newArrayList();
        for (Node<OWLClass> subNode : reasoner.getSubClasses(current.getRepresentativeElement(), true)) {
            if (subNode.isBottomNode()) {
                // ignore Nothing
            }
            else if (ancestors.getNodes().contains(subNode)) { // recurse
                subs.add(buildTree(subNode, nodeSetWithout(ancestors, subNode)));
            }
            else {
                subs.add(new Tree<>(subNode));
            }
        }
        subs.sort(comparator);
        return new Tree<>(current, subs);
    }

    private NodeSet<OWLClass> nodeSetWithout(final NodeSet<OWLClass> original, final Node<OWLClass> node) {
        Set<Node<OWLClass>> nodes = original.getNodes();
        nodes.remove(node);
        return new OWLClassNodeSet(nodes);
    }

    public static class Tree<T extends OWLEntity> {
        public final Node<T> value;
        public final List<Tree<T>> children;

        public Tree(final Node<T> value) {
            this(value, Collections.EMPTY_LIST);
        }

        public Tree(final Node<T> value, final List<Tree<T>> children) {
            this.value = value;
            this.children = children;
        }

        @Override
        public String toString() {
            String node = "\"node\": \"[" + StringUtils.collectionToCommaDelimitedString(value.getEntities()) + "]\"";
            String subs = "\"children\": [" + StringUtils.collectionToCommaDelimitedString(children) + "]";
            return "\n{" + node + "," + subs + "}";
        }
    }
}
