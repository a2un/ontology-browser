package org.coode.www.controller;

import com.google.common.base.Optional;
import org.coode.owl.mngr.OWLServer;
import org.coode.www.exception.NotFoundException;
import org.coode.www.exception.OntServerException;
import org.coode.www.kit.OWLHTMLKit;
import org.coode.www.model.Tree;
import org.coode.www.renderer.OWLHTMLRenderer;
import org.coode.www.service.OWLDatatypesService;
import org.coode.www.service.hierarchy.OWLDatatypeHierarchyService;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Set;

@Controller
@RequestMapping(value="/datatypes")
@SessionAttributes("kit")
public class OWLDatatypesController extends ApplicationController {

    @Autowired
    private OWLDatatypesService service;

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String getOWLDatatypes(@ModelAttribute("kit") final OWLHTMLKit kit) throws OntServerException {

        final OWLDataFactory df = kit.getOWLServer().getOWLOntologyManager().getOWLDataFactory();

        OWLDatatype owlTopDatatype = df.getTopDatatype();

        String id = service.getIdFor(owlTopDatatype);

        return "redirect:/datatypes/" + id;
    }


    @RequestMapping(value="/{propertyId}", method=RequestMethod.GET)
    public String getOWLDatatype(@PathVariable final String propertyId,
                                 @ModelAttribute("kit") final OWLHTMLKit kit,
                                 final Model model) throws OntServerException, NotFoundException {

        OWLDatatype owlDatatype = service.getOWLDatatypeFor(propertyId, kit);

        OWLServer owlServer = kit.getOWLServer();

        Set<OWLOntology> ontologies = owlServer.getOntologies();

        Comparator<Tree<OWLDatatype>> comparator = (o1, o2) ->
                o1.value.iterator().next().compareTo(o2.value.iterator().next());

        OWLDatatypeHierarchyService hierarchyService = new OWLDatatypeHierarchyService(
                owlServer.getOWLOntologyManager().getOWLDataFactory(),
                ontologies,
                comparator);

        Tree<OWLDatatype> prunedTree = hierarchyService.getPrunedTree(owlDatatype);

        String entityName = kit.getOWLServer().getShortFormProvider().getShortForm(owlDatatype);

        OWLHTMLRenderer owlRenderer = new OWLHTMLRenderer(kit, Optional.of(owlDatatype));

        model.addAttribute("title", entityName + " (Datatype)");
        model.addAttribute("type", "Datatypes");
        model.addAttribute("iri", owlDatatype.getIRI());
        model.addAttribute("options", optionsService.getOptionsAsMap(kit));
        model.addAttribute("activeOntology", owlServer.getActiveOntology());
        model.addAttribute("ontologies", ontologies);
        model.addAttribute("hierarchy", prunedTree);
        model.addAttribute("characteristics", service.getCharacteristics(owlDatatype, kit));
        model.addAttribute("mos", owlRenderer);

        return "owlentity";
    }

    @RequestMapping(value="/{propertyId}/children", method=RequestMethod.GET)
    public String getChildren(@PathVariable final String propertyId,
                              @ModelAttribute("kit") final OWLHTMLKit kit,
                              final Model model) throws OntServerException, NotFoundException {

        OWLDatatype property = service.getOWLDatatypeFor(propertyId, kit);

        OWLServer owlServer = kit.getOWLServer();

        Comparator<Tree<OWLDatatype>> comparator = (o1, o2) ->
                o1.value.iterator().next().compareTo(o2.value.iterator().next());

        OWLDatatypeHierarchyService hierarchyService = new OWLDatatypeHierarchyService(
                owlServer.getOWLOntologyManager().getOWLDataFactory(),
                owlServer.getActiveOntologies(),
                comparator);

        Tree<OWLDatatype> prunedTree = hierarchyService.getChildren(property);

        OWLHTMLRenderer owlRenderer = new OWLHTMLRenderer(kit, Optional.absent());

        model.addAttribute("t", prunedTree);
        model.addAttribute("mos", owlRenderer);

        return "base :: tree";
    }
}
