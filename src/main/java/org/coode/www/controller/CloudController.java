package org.coode.www.controller;

import org.coode.www.cloud.*;
import org.coode.www.exception.OntServerException;
import org.coode.www.kit.OWLHTMLKit;
import org.coode.www.renderer.OWLHTMLRenderer;
import org.coode.www.service.CloudHelper;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

/**
 * Could have gone in each OWLEntity type Controller, but this is a bit tangential.
 */
@Controller
@RequestMapping(value = "/clouds")
@SessionAttributes("kit")
public class CloudController extends ApplicationController {

    @Value("${cloud.threshold.default}")
    private int thresholdDefault;

    @Value("${cloud.zoom.default}")
    private int zoomDefault;

    @RequestMapping(value = "/classes")
    public String getClassesCloud(@ModelAttribute("kit") final OWLHTMLKit kit,
                                  @RequestParam Optional<Integer> zoom,
                                  @RequestParam Optional<Integer> threshold,
                                  @RequestParam(defaultValue="false") boolean normalise,
                                  final Model model) throws OntServerException {

        ClassesByUsageCloud cloudModel = new ClassesByUsageCloud(kit.getOntologies());

        return cloud(kit, model, "Classes Usage Cloud", cloudModel, zoom, threshold, normalise);
    }

    @RequestMapping(value = "/individuals")
    public String getIndividualsCloud(@ModelAttribute("kit") final OWLHTMLKit kit,
                                      @RequestParam Optional<Integer> zoom,
                                      @RequestParam Optional<Integer> threshold,
                                      @RequestParam(defaultValue="false") boolean normalise,
                                      final Model model) throws OntServerException {

        IndividualsByUsageCloud cloudModel = new IndividualsByUsageCloud(kit.getOntologies());

        return cloud(kit, model, "Individuals Usage Cloud", cloudModel, zoom, threshold, normalise);
    }

    @RequestMapping(value = "/objectproperties")
    public String getObjectPropertiesCloud(@ModelAttribute("kit") final OWLHTMLKit kit,
                                           @RequestParam Optional<Integer> zoom,
                                           @RequestParam Optional<Integer> threshold,
                                           @RequestParam(defaultValue="false") boolean normalise,
                                           final Model model) throws OntServerException {

        ObjectPropsByUsageCloud cloudModel = new ObjectPropsByUsageCloud(kit.getOntologies());

        return cloud(kit, model, "Object Properties Usage Cloud", cloudModel, zoom, threshold, normalise);
    }

    @RequestMapping(value = "/dataproperties")
    public String getDataPropertiesCloud(@ModelAttribute("kit") final OWLHTMLKit kit,
                                         @RequestParam Optional<Integer> zoom,
                                         @RequestParam Optional<Integer> threshold,
                                         @RequestParam(defaultValue="false") boolean normalise,
                                         final Model model) throws OntServerException {

        DataPropsByUsageCloud cloudModel = new DataPropsByUsageCloud(kit.getOntologies());

        return cloud(kit, model, "Data Properties Usage Cloud", cloudModel, zoom, threshold, normalise);
    }

    @RequestMapping(value = "/annotationproperties")
    public String getAnnotationPropertiesCloud(@ModelAttribute("kit") final OWLHTMLKit kit,
                                               @RequestParam Optional<Integer> zoom,
                                               @RequestParam Optional<Integer> threshold,
                                               @RequestParam(defaultValue="false") boolean normalise,
                                               final Model model) throws OntServerException {

        AnnotationPropsByUsageCloud cloudModel = new AnnotationPropsByUsageCloud(kit.getOntologies());

        return cloud(kit, model, "Annotation Properties Usage Cloud", cloudModel, zoom, threshold, normalise);
    }

    @RequestMapping(value = "/datatypes")
    public String getDatatypesCloud(@ModelAttribute("kit") final OWLHTMLKit kit,
                                    @RequestParam Optional<Integer> zoom,
                                    @RequestParam Optional<Integer> threshold,
                                    @RequestParam(defaultValue="false") boolean normalise,
                                    final Model model) throws OntServerException {

        DatatypesByUsageCloud cloudModel = new DatatypesByUsageCloud(kit.getOntologies());

        return cloud(kit, model, "Datatypes Usage Cloud", cloudModel, zoom, threshold, normalise);
    }

    public <T extends OWLEntity>String cloud(final OWLHTMLKit kit,
                                             final Model model,
                                             final String title,
                                             final CloudModel<T> cloudModel,
                                             final Optional<Integer> zoom,
                                             final Optional<Integer> threshold,
                                             boolean normalise) {

        CloudHelper<T> helper = new CloudHelper<>(cloudModel);
        helper.setZoom(zoom.orElse(zoomDefault));
        helper.setThreshold(threshold.orElse(thresholdDefault));
        helper.setNormalise(normalise);

        OWLHTMLRenderer owlRenderer = new OWLHTMLRenderer(kit, Optional.empty());

        Set<OWLOntology> ontologies = kit.getOntologies();

        model.addAttribute("title", title);
        model.addAttribute("activeOntology", kit.getActiveOntology());
        model.addAttribute("ontologies", ontologies);
        model.addAttribute("helper", helper);
        model.addAttribute("mos", owlRenderer);

        return "cloud";
    }
}
