package org.coode.www.controller;

import java.util.Optional;
import org.coode.www.exception.NotFoundException;
import org.coode.www.exception.OntServerException;
import org.coode.www.kit.OWLHTMLKit;
import org.coode.www.model.Tree;
import org.coode.www.renderer.MediaRenderer;
import org.coode.www.renderer.OWLHTMLRenderer;
import org.coode.www.service.GeoService;
import org.coode.www.service.MediaService;
import org.coode.www.service.OWLIndividualsService;
import org.coode.www.service.ReasonerFactoryService;
import org.coode.www.service.hierarchy.OWLIndividualsByTypeHierarchyService;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.Set;

@Controller
@RequestMapping(value="/individuals")
public class OWLIndividualsController extends ApplicationController {

    @Autowired
    private OWLIndividualsService service;

    @Autowired
    private GeoService geoService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private ReasonerFactoryService reasonerFactoryService;

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String getOWLIndividuals() throws OntServerException, NotFoundException {

        OWLNamedIndividual firstIndividual = service.getFirstIndividual(kit);

        String id = service.getIdFor(firstIndividual);

        return "redirect:/individuals/" + id;
    }


    @RequestMapping(value= "/{individualId}", method=RequestMethod.GET)
    public String getOWLIndividual(@PathVariable final String individualId,
                                   final HttpServletRequest request,
                                   final Model model) throws OntServerException, NotFoundException {

        OWLNamedIndividual owlIndividual = service.getOWLIndividualFor(individualId, kit);

        Comparator<Tree<OWLEntity>> comparator = Comparator.comparing(o -> o.value.iterator().next());

        OWLReasoner r = reasonerFactoryService.getToldReasoner(kit.getActiveOntology());

        OWLIndividualsByTypeHierarchyService hierarchyService = new OWLIndividualsByTypeHierarchyService(r, comparator);

        Tree<OWLEntity> prunedTree = hierarchyService.getPrunedTree(owlIndividual);

        String entityName = kit.getShortFormProvider().getShortForm(owlIndividual);

        OWLHTMLRenderer owlRenderer = new MediaRenderer(kit, Optional.of(owlIndividual));

        Set<OWLOntology> ontologies = kit.getOntologies();

        Optional<GeoService.Loc> maybeLoc = geoService.getLocation(owlIndividual, ontologies);
        if (maybeLoc.isPresent()) {
            GeoService.Loc loc = maybeLoc.get();
            model.addAttribute("geo", loc);
        }

        if (mediaService.isImageURL(owlIndividual.getIRI())) {
            model.addAttribute("image", owlIndividual.getIRI().toString());
        }

        if (mediaService.isSoundURL(owlIndividual.getIRI())) {
            model.addAttribute("sound", owlIndividual.getIRI().toString());
        }

        model.addAttribute("title", entityName + " (Individual)");
        model.addAttribute("type", "Individuals");
        model.addAttribute("iri", owlIndividual.getIRI());
        model.addAttribute("hierarchy", prunedTree);
        model.addAttribute("characteristics", service.getCharacteristics(owlIndividual, kit));
        model.addAttribute("mos", owlRenderer);

        return "owlentity";
    }
}
