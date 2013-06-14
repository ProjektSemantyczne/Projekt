package projekt.agents;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import projekt.agents.AgentTrip.RecieveClientMessages;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jadeOWL.base.DataFactory;
import jadeOWL.base.OntologyManager;
import jadeOWL.base.messaging.ACLOWLMessage;


public class CopyOfAgentClient extends Agent{

	private static final long serialVersionUID = -3212467805232172479L;
	OntologyManager ontologyManager;
	OWLOntology travelOntology;
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
			addBehaviour(new AskForCountriesBehaviour(this,countryQueryOntology));
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected void takeDown(){
		//Clean up
		ontologyManager.removeAndUnmapAllOntologies();
	}
	class AskForCountriesBehaviour extends SimpleBehaviour
	{

		private static final long serialVersionUID = 5639516177887167091L;
		private boolean finished = false;
    	private int iteration = 0;
    	private OWLOntology m_queryOntology;
    	
        public AskForCountriesBehaviour(Agent a, OWLOntology queryOntology) {
            super(a);
            m_queryOntology = queryOntology;
        }
        
        public void action() 
        {
        	iteration++;
        	if(1 == iteration){
        		block(1000);
        	} else
        	{
        		System.out.println("Agent " + getLocalName() + " asking for countries");
        		ACLOWLMessage msg = new ACLOWLMessage(ACLMessage.QUERY_IF);
        		msg.addReceiver(new AID("trip", AID.ISLOCALNAME));
        		msg.setOntology("http://misio.biz/travelontology.owl");
        		try {
        			//Fill the content of the message with ontology
					msg.setContentOntology(m_queryOntology);
				} catch (OWLOntologyStorageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        		send(msg);
        		
        		System.out.println("Agent " + getLocalName() + " listening for messages");
	     		ACLMessage msgAnswer = blockingReceive();
	    		System.out.println("Agent " + getLocalName() + " received message");
	            if (msgAnswer!=null){
	            	OWLOntology ontology;
					try {
						ontology = ontologyManager.getOntologyFromACLMessage(msgAnswer);
						Set<OWLNamedIndividual> individuals = ontologyManager.getQueryManager().filterAnswerSetInstances(ontology);
						ontologyManager.getQueryManager().removeAnswerSetAxioms(ontology);
						Dialog dialog = new Dialog();
						String s = dialog.Create(individuals, "pañstwo");
						System.out.println(s);
						
					} catch (OWLOntologyCreationException e) {
						e.printStackTrace();
					}
	            }
                
        		finished = true;
        	}
        }

		@Override
		public boolean done() {
			return finished;
		}
	}
   }
