package org.coode.www.controller;

import java.util.Optional;
import org.coode.www.exception.NotFoundException;
import org.coode.www.exception.OntServerException;
import org.coode.www.kit.OWLHTMLKit;
import org.coode.www.model.Tree;
import org.coode.www.renderer.OWLHTMLRenderer;
import org.coode.www.service.OWLClassesService;
import org.coode.www.service.OWLIndividualsService;
import org.coode.www.service.ReasonerFactoryService;
import org.coode.www.service.hierarchy.OWLClassHierarchyService;
import org.coode.www.service.hierarchy.OWLIndividualsByTypeHierarchyService;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;

@Controller
@RequestMapping(value="/classes")
public class OWLClassesController extends ApplicationController {

    @Autowired
    private OWLClassesService service;

    @Autowired
    private ReasonerFactoryService reasonerFactoryService;

    @Autowired
    private OWLIndividualsService individualsService;

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String getOWLClasses() throws OntServerException {

        OWLClass owlThing = kit.getOWLOntologyManager().getOWLDataFactory().getOWLThing();

        String id = service.getIdFor(owlThing);

        return "redirect:/classes/" + id;
    }

    @RequestMapping(value="/{classId}", method=RequestMethod.GET)
    public String getOWLClass(@PathVariable final String classId,
                              final Model model) throws OntServerException, NotFoundException {

        OWLClass owlClass = service.getOWLClassFor(classId, kit);

        Comparator<Tree<OWLClass>> comparator = Comparator.comparing(o -> o.value.iterator().next());

        OWLReasoner r = reasonerFactoryService.getToldReasoner(kit.getActiveOntology());
        OWLClassHierarchyService hierarchyService = new OWLClassHierarchyService(r, comparator);

        Tree<OWLClass> prunedTree = hierarchyService.getPrunedTree(owlClass);

        OWLHTMLRenderer owlRenderer = new OWLHTMLRenderer(kit, Optional.of(owlClass));

        String entityName = kit.getShortFormProvider().getShortForm(owlClass);

        model.addAttribute("title", entityName + " (Class)");
        model.addAttribute("type", "Classes");
        model.addAttribute("iri", owlClass.getIRI());
        model.addAttribute("hierarchy", prunedTree);
        model.addAttribute("characteristics", service.getCharacteristics(owlClass, kit));
        model.addAttribute("mos", owlRenderer);

        return "owlentity";
    }

    @RequestMapping(value="/{classId}/children", method=RequestMethod.GET)
    public String getChildren(@PathVariable final String classId,
                              final Model model) throws OntServerException, NotFoundException {

        OWLClass owlClass = service.getOWLClassFor(classId, kit);

        Comparator<Tree<OWLClass>> comparator = Comparator.comparing(o -> o.value.iterator().next());

        OWLReasoner r = reasonerFactoryService.getToldReasoner(kit.getActiveOntology());

        OWLClassHierarchyService hierarchyService = new OWLClassHierarchyService(r, comparator);

        Tree<OWLClass> prunedTree = hierarchyService.getChildren(owlClass);

        OWLHTMLRenderer owlRenderer = new OWLHTMLRenderer(kit, Optional.empty());

        model.addAttribute("t", prunedTree);
        model.addAttribute("mos", owlRenderer);

        return "base :: tree";
    }

    @RequestMapping(value="/{classId}/instances", method=RequestMethod.GET)
    public String getInstances(@PathVariable final String classId,
                               final Model model) throws OntServerException, NotFoundException {

        OWLEntity owlClass = service.getOWLClassFor(classId, kit);

        Comparator<Tree<OWLEntity>> comparator = Comparator.comparing(o -> o.value.iterator().next());

        OWLReasoner r = reasonerFactoryService.getToldReasoner(kit.getActiveOntology());

        OWLIndividualsByTypeHierarchyService hierarchyService = new OWLIndividualsByTypeHierarchyService(r, comparator);

        Tree<OWLEntity> prunedTree = hierarchyService.getChildren(owlClass);

        OWLHTMLRenderer owlRenderer = new OWLHTMLRenderer(kit, Optional.empty());

        model.addAttribute("t", prunedTree);
        model.addAttribute("mos", owlRenderer);

        return "base :: tree";
    }
}
