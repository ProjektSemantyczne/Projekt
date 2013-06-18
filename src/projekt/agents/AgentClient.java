package projekt.agents;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JDialog;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import projekt.agents.AgentTrip.RecieveClientMessages;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jadeOWL.base.DataFactory;
import jadeOWL.base.OntologyManager;
import jadeOWL.base.messaging.ACLOWLMessage;


public class AgentClient extends Agent{

	private static final long serialVersionUID = -3212467805232172479L;
	OntologyManager ontologyManager;
	OWLOntology travelOntology;
	Set<OWLNamedIndividual> individuals = new HashSet<OWLNamedIndividual>();
	OWLDataFactory dataFactory;
	public static int step = 0;
	public static boolean finished = false;
	public void setStep(int step) {
		this.step = step;
	}
	protected void setup(){
		System.out.println("Agent " + getLocalName() + " started");
		ontologyManager = new OntologyManager();
		try {
			
			travelOntology = ontologyManager.loadAndMapOntology(new File("ontologies/travelontology.owl"), "http://misio.biz/travelontology.owl");
			
			//Ask for all countries using custom query
			
			//Create a new empty query ontology
			OWLOntology countryQueryOntology = ontologyManager.getQueryManager().createNewOWLQueryOntology(this);
			
			//Get reference to the "Country" class
			OWLClass countryClass = ontologyManager.getDataFactory().getOWLClass(travelOntology, "Country");
			
			//Create a query class that asks for all individuals of class "Country"
			ontologyManager.getQueryManager().createCustomQueryClass(countryQueryOntology, "countryQuery", countryClass);
			addBehaviour(new AskForCountriesBehaviour(this));
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected void takeDown(){
		//Clean up
		ontologyManager.removeAndUnmapAllOntologies();
	}
	protected String stripFromIRI(String s) {
		String[] temp = s.split("#");
		return temp[1].split(">")[0];
	}
	class AskForCountriesBehaviour extends Behaviour
	{
		String[] preferences = new String[15];
		private static final long serialVersionUID = 5639516177887167091L;
    	private OWLOntology m_queryOntology;
    	String nextQuery = null;
    	private Agent agent;
        public AskForCountriesBehaviour(Agent a) {
            super(a);
            agent = a;
        }
        public void action() 
        {
        	ACLOWLMessage msg = new ACLOWLMessage(ACLMessage.QUERY_IF);
        	msg.addReceiver(new AID("trip", AID.ISLOCALNAME));
        	msg.setOntology("http://misio.biz/travelontology.owl");
        	switch(step) {
        	case 0:
        		msg.setConversationId("Preferences");
        		System.out.println("Agent " + getLocalName() + " asking for preferences");
        		try {
					OWLOntology preferencesQueryOntology = ontologyManager.getQueryManager().createNewOWLQueryOntology();
					OWLClass preferencesClass = ontologyManager.getDataFactory().getOWLClass(travelOntology, "Preferences");
					ontologyManager.getQueryManager().createCustomQueryClass(preferencesQueryOntology, "hotelQuery", preferencesClass);
					msg.setContentOntology(preferencesQueryOntology);
				} catch (OWLOntologyCreationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (OWLOntologyStorageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	send(msg);
        		break;
        	case 1:
        		individuals.clear();
        		System.out.println("Agent " + getLocalName() + " asking for cities(Object properties) for preferences");
        		try {
					OWLOntology cityQueryOntology = ontologyManager.getQueryManager().createNewOWLQueryOntology();
					for(int i = 0; i < preferences.length; i++) {
						if(preferences[i]!=null && !preferences[i].isEmpty())
							individuals.add(ontologyManager.getDataFactory().getOWLNamedIndividual(travelOntology, preferences[i]));
					}
					OWLOntology answerOnto = ontologyManager.getQueryManager().prepareQueryAnswerFromInstances(individuals, cityQueryOntology, myAgent);
					msg.setContentOntology(answerOnto);
					msg.setConversationId("City");
				} catch (IllegalArgumentException | OWLOntologyStorageException e) {
					e.printStackTrace();
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				}
        		send(msg);
        		break;
        	}
     		ACLMessage msgAnswer = blockingReceive();
    		System.out.println("Agent " + getLocalName() + " received message");
            if (msgAnswer!=null){
            	OWLOntology ontology;
				try {
					individuals.clear();
					ontology = ontologyManager.getOntologyFromACLMessage(msgAnswer);
					individuals = ontologyManager.getQueryManager().filterAnswerSetInstances(ontology);
					ontologyManager.getQueryManager().removeAnswerSetAxioms(ontology);
					switch(step) {
						case 0:
							PreferencesFrame dialog = new PreferencesFrame();
							preferences = dialog.Create(individuals);
							dialog.setVisible(true);
							dialog.setSize(500,600);
							dialog.addNotify();
							step++;
						break;
						case 1:
							TripsFrame dialog1 = new TripsFrame(individuals);
							dialog1.createAndShowGUI();
						break;
					}
				} catch (OWLOntologyCreationException | IOException e) {
					e.printStackTrace();
				}
            }
                
        }

		@Override
		public boolean done() {
			return finished;
		}
	}
}
