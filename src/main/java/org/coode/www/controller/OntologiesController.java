package org.coode.www.controller;

import org.coode.html.doclet.NodeDoclet;
import org.coode.owl.mngr.HierarchyProvider;
import org.coode.www.kit.OWLHTMLKit;
import org.coode.html.page.SummaryPageFactory;
import org.coode.html.doclet.HTMLDoclet;
import org.coode.html.doclet.OWLObjectIndexDoclet;
import org.coode.www.ServletUtils;
import org.coode.www.exception.NotFoundException;
import org.coode.www.service.OntologiesService;
import org.coode.www.exception.OntServerException;
import org.coode.www.model.LoadOntology;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Set;

@Controller
@RequestMapping(value="/ontologies")
@SessionAttributes("kit")
public class OntologiesController extends ApplicationController {

    @Autowired
    private OntologiesService service;

    @RequestMapping(method=RequestMethod.GET)
    public String getOntologies(@RequestParam(required=false) final String label,
                                final HttpServletRequest request,
                                Model model) throws OntServerException {

        OWLHTMLKit kit = sessionManager.getHTMLKit(request, label);

        // TODO yuck replace this adapter
        Set<OWLOntology> results = kit.getOWLServer().getActiveOntologies();
        OWLObjectIndexDoclet doclet = new OWLObjectIndexDoclet(kit);
        doclet.setTitle("Ontologies");
        doclet.addAll(results);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        URL pageUrl = ServletUtils.getPageURL(request);
        doclet.renderAll(pageUrl, writer);
        String content = stringWriter.toString();

        model.addAttribute("applicationInfo", applicationInfo);
        model.addAttribute("activeOntology", kit.getOWLServer().getActiveOntology());
        model.addAttribute("ontologies", kit.getOWLServer().getOntologies());
        model.addAttribute("content", content);

        return "doclet";
    }

    @RequestMapping(value="/{ontId}", method=RequestMethod.GET)
    public String getOntology(@PathVariable final String ontId,
                              @RequestParam(required=false) final String label,
                              final HttpServletRequest request,
                              Model model) throws OntServerException, NotFoundException {
        OWLHTMLKit kit = sessionManager.getHTMLKit(request, label);

        OWLOntology ontology = service.getOntologyFor(ontId, kit);

        // TODO yuck replace this adapter
        SummaryPageFactory summaryPageFactory = new SummaryPageFactory(kit);
        HTMLDoclet hierarchyDoclet = summaryPageFactory.getHierarchy(OWLOntology.class);
        hierarchyDoclet.setUserObject(ontology);
        HTMLDoclet summaryDoclet = summaryPageFactory.getSummaryDoclet(ontology);

        model.addAttribute("applicationInfo", applicationInfo);
        model.addAttribute("activeOntology", kit.getOWLServer().getActiveOntology());
        model.addAttribute("ontologies", kit.getOWLServer().getOntologies());
        model.addAttribute("content", renderDoclets(request, summaryDoclet, hierarchyDoclet));

        return "doclet";
    }

    @RequestMapping(method= RequestMethod.POST)
    public String loadOntology(@ModelAttribute final LoadOntology loadOntology,
                               @RequestParam(required=false) final String label,
                               HttpServletRequest request,
                               Model model) throws OntServerException {

        logger.info("Loading ontology from " + loadOntology.getUri());

        OWLHTMLKit kit = sessionManager.getHTMLKit(request, label);

        String ontologyId = service.load(loadOntology.getUri(), loadOntology.isClear(), kit);

        model.addAttribute("kit", kit); // this will add it to the session

        if (loadOntology.getRedirect() != null) {
            return "redirect:" + loadOntology.getRedirect();
        }
        else {
            return "redirect:/ontologies/" + ontologyId;
        }
    }

    @RequestMapping(value="/{ontologyId}/children", method=RequestMethod.GET)
    @ResponseBody
    public String getChildren(@PathVariable final String ontologyId,
                              @RequestParam(required=false) final String label,
                              @RequestHeader final URL referer,
                              final HttpServletRequest request) throws OntServerException, NotFoundException {

        final OWLHTMLKit kit = sessionManager.getHTMLKit(request, label);

        OWLOntology ontology = service.getOntologyFor(ontologyId, kit);
        HierarchyProvider<OWLOntology> hp = service.getHierarchyProvider(kit);
        NodeDoclet<OWLOntology> nodeDoclet = new NodeDoclet<OWLOntology>(kit, ontology, hp);
        nodeDoclet.setUserObject(null); // not sure why wee need this, but otherwise no children

        return renderDoclets(referer, nodeDoclet);
    }
}
