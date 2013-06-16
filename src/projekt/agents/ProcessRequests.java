package projekt.agents;

import java.io.File;
import java.util.Map;
import java.util.Set;

import jadeOWL.base.OntologyManager;

import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class ProcessRequests {

	OntologyManager ontologyManager;
	OWLOntology travelOntology;
	public ProcessRequests() {
		ontologyManager = new OntologyManager();
		try {
			travelOntology = ontologyManager.loadAndMapOntology(new File("ontologies/travelontology.owl"), "http://misio.biz/travelontology.owl");
			OWLReasoner reasoner = ontologyManager.getQueryManager().getOWLReasoner(true, travelOntology);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Map<OWLObjectPropertyExpression, Set<OWLIndividual>> getObjectProperties(OWLNamedIndividual individual) {
		return individual.getObjectPropertyValues(travelOntology);
	}
	public Map<OWLDataPropertyExpression, Set<OWLLiteral>> getDataProperties(OWLNamedIndividual individual) {
		return individual.getDataPropertyValues(travelOntology);
	}
	public OWLNamedIndividual getIndividual(String i) {
		return ontologyManager.getDataFactory().getOWLNamedIndividual(travelOntology, i);
	}
	public String stripFromIRI(String s) {
		String[] temp = s.split("#");
		return temp[1].split(">")[0];
	}
}
