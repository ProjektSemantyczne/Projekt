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
		String[] preferences = new String[10];
		private static final long serialVersionUID = 5639516177887167091L;
		private boolean finished = false;
    	private OWLOntology m_queryOntology;
    	private int step = 0;
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
        	// Nazwa do wyœwietlenia "Wybierz name" - miasto, pañstwo itd 
        	String name = null;
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
            	name = "pañstwo";
            	send(msg);
        		break;
        	case 1:
        		individuals.clear();
        		System.out.println("Agent " + getLocalName() + " asking for cities(Object properties) for preferences");
        		try {
					OWLOntology cityQueryOntology = ontologyManager.getQueryManager().createNewOWLQueryOntology();
					for(int i = 0; i < preferences.length; i++) {
						System.out.println(preferences[i]);
						if(preferences[i]!=null && !preferences[i].isEmpty())
							individuals.add(ontologyManager.getDataFactory().getOWLNamedIndividual(travelOntology, preferences[i]));
					}
					//System.out.println(cityClass.getObjectPropertyValues(travelOntology).toString());
					//System.out.println(cityClass.toString());
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
        	case 2:
        		name = "hotel";
        		System.out.println("Agent " + getLocalName() + " asking for hotels");
        		try {
					OWLOntology cityQueryOntology = ontologyManager.getQueryManager().createNewOWLQueryOntology();
					OWLClass cityClass = ontologyManager.getDataFactory().getOWLClass(travelOntology, nextQuery + "_Hotels");
					ontologyManager.getQueryManager().createCustomQueryClass(cityQueryOntology, "hotelQuery", cityClass);
					msg.setContentOntology(cityQueryOntology);
				} catch (OWLOntologyCreationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (OWLOntologyStorageException e) {
					// TODO Auto-generated catch block
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
					ontology = ontologyManager.getOntologyFromACLMessage(msgAnswer);
					Set<OWLNamedIndividual> individuals = ontologyManager.getQueryManager().filterAnswerSetInstances(ontology);
					ontologyManager.getQueryManager().removeAnswerSetAxioms(ontology);
					//Dialog dialog = new Dialog();
					//String s = dialog.Create(individuals, name);
					//System.out.println(s);
					//nextQuery = s;
					//System.out.println(individuals.toString());
					switch(step) {
						case 0:
							MyFrame dialog = new MyFrame();
							preferences = dialog.Create(individuals);
							dialog.setVisible(true);
							dialog.setSize(500,600);
							dialog.addNotify();
						break;
						case 1:
							Dialog dialog2 = new Dialog();
							String s = dialog2.Create(individuals, "bla");
							
						break;
					}
					step++;
					System.out.println("Step - " + step);
					// Iterujemy po individualach
/*					Iterator<OWLNamedIndividual> it = individuals.iterator();
					System.out.println("Query answer:");
					while(it.hasNext()){
						OWLNamedIndividual ind = it.next();
						// Pobieramy Object Property z individuala
						Map<OWLObjectPropertyExpression, Set<OWLIndividual>> s1 = ind.getObjectPropertyValues(ontology);
						System.out.println(s1.toString());
						// Wyci¹gamy key object property np "hasCity"
						if(!s1.isEmpty() && s1.keySet().iterator().hasNext()) {
							System.out.println(stripFromIRI(s1.keySet().iterator().next().toString()));
						}
						// Trochê zakrêcone ale iterujemy tu po wartoœciach czyli np po miastach
						Iterable<Set<OWLIndividual>> data = s1.values();
						Iterator<Set<OWLIndividual>> iterator = data.iterator();
						Iterator<OWLIndividual> itt = null;
						if(iterator.hasNext()) {
							 itt = iterator.next().iterator();
							while(itt.hasNext())
								System.out.println(stripFromIRI(itt.next().toString()));
						}
						// Wyjmujemy Types individuala, w których mamy akurat np. nasze Pañstwo do danego miasta
						Iterable<OWLClassExpression> x = ind.getTypes(ontology);
						Iterator<OWLClassExpression> it2 = x.iterator();
						System.out.println("Pañstwo = " + it2.next());
						System.out.println("Miasto = " + ind);	
						System.out.println("--------------");
					}*/
				} catch (OWLOntologyCreationException | IOException e) {
					e.printStackTrace();
				}
            }
                
        }

		@Override
		public boolean done() {
			return step==2;
		}
	}
}
