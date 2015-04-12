package org.coode.www.controller;

import org.coode.html.doclet.OWLIndividualSummaryDoclet;
import org.coode.www.kit.OWLHTMLKit;
import org.coode.html.doclet.HierarchyDocletFactory;
import org.coode.html.doclet.HTMLDoclet;
import org.coode.html.doclet.OWLObjectIndexDoclet;
import org.coode.www.exception.NotFoundException;
import org.coode.www.exception.OntServerException;
import org.coode.www.service.OWLIndividualsService;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@Controller
@RequestMapping(value="/individuals")
@SessionAttributes("kit")
public class OWLIndividualsController extends ApplicationController {

    @Autowired
    private OWLIndividualsService service;

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String getOWLIndividuals(@RequestParam(required=false) final String label,
                                final HttpServletRequest request,
                                final Model model) throws OntServerException {

        final OWLHTMLKit kit = sessionManager.getHTMLKit(request, label);

        Set<OWLIndividual> individuals = service.getAllIndividuals(kit);

        OWLObjectIndexDoclet doclet = new OWLObjectIndexDoclet(kit);
        doclet.setTitle("Individuals");
        doclet.addAll(individuals);

        model.addAttribute("options", optionsService.getOptionsAsMap(kit));
        model.addAttribute("activeOntology", kit.getOWLServer().getActiveOntology());
        model.addAttribute("ontologies", kit.getOWLServer().getOntologies());
        model.addAttribute("content", renderDoclets(request, doclet));

        return "doclet";
    }


    @RequestMapping(value="/{propertyId}", method=RequestMethod.GET)
    public String getOWLIndividual(@PathVariable final String propertyId,
                              @RequestParam(required=false) final String label,
                              final HttpServletRequest request,
                              final Model model) throws OntServerException, NotFoundException {

        final OWLHTMLKit kit = sessionManager.getHTMLKit(request, label);

        OWLIndividual owlIndividual = service.getOWLIndividualFor(propertyId, kit);

        // TODO yuck replace this adapter
        HierarchyDocletFactory hierarchyDocletFactory = new HierarchyDocletFactory(kit);
        HTMLDoclet hierarchyDoclet = hierarchyDocletFactory.getHierarchy(OWLNamedIndividual.class);
        hierarchyDoclet.setUserObject(owlIndividual);
        HTMLDoclet summaryDoclet = new OWLIndividualSummaryDoclet(kit);
        summaryDoclet.setUserObject(owlIndividual);

        model.addAttribute("options", optionsService.getOptionsAsMap(kit));
        model.addAttribute("activeOntology", kit.getOWLServer().getActiveOntology());
        model.addAttribute("ontologies", kit.getOWLServer().getOntologies());
        model.addAttribute("content", renderDoclets(request, summaryDoclet, hierarchyDoclet));

        return "doclet";
    }
}
