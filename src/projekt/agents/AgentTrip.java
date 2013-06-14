package projekt.agents;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import projekt.agents.AgentClient.AskForCountriesBehaviour;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jadeOWL.base.OntologyManager;
import jadeOWL.base.messaging.ACLOWLMessage;

public class AgentTrip extends Agent{

	private static final long serialVersionUID = 5862645069068601092L;
	OntologyManager ontologyManager;
	OWLOntology travelOntology;
	
	protected void setup(){
		System.out.println("Agent " + getLocalName() + " started");
		
		ontologyManager = new OntologyManager();
		
		//Load the pizza ontology
		//File travelFile = new File("ontologies/travelontology.owl");
		/* REASONER TEST */
		
		
		/* END TEST */
		try {
			travelOntology = ontologyManager.loadAndMapOntology(new File("ontologies/travelontology.owl"), "http://misio.biz/travelontology.owl");
			OWLReasoner reasoner = ontologyManager.getQueryManager().getOWLReasoner(true, travelOntology);
			//Listen for client messages
			addBehaviour(new RecieveClientMessages(this));
		
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected void takeDown(){
		//Clean up
		ontologyManager.removeAndUnmapAllOntologies();
	}
	
	class RecieveClientMessages extends CyclicBehaviour{

		private static final long serialVersionUID = 7569347209298378146L;
		public OWLOntology ontology;
		public RecieveClientMessages(Agent a){
			super(a);
		}
		
		public void action() 
        {

		   System.out.println("Agent " + getLocalName() + " listening for client messages");
		   ACLOWLMessage msg= (ACLOWLMessage) blockingReceive();
		   System.out.println("Agent " + getLocalName() + " received message");
           if (msg!=null){
        	   //Print the content of the message
               //System.out.println( " - " + myAgent.getLocalName() + " <- " + msg.getContent() );
               try {
            	   System.out.println(msg.getContent());
            	//Extract the ontology from the message
            	System.out.println(msg.getConversationId());
				ontology = msg.getContentOntology(ontologyManager,myAgent);
				for (OWLClass cls : ontology.getClassesInSignature())
					System.out.println(cls);
				//System.out.println("Received ontology:\n" + ontology);
				
				//Get query classes from the ontology
				Set<OWLClass> filteredSet = ontologyManager.getQueryManager().filterOWLQueryClasses(ontology);
				
				//Print the found query classes
				/*Iterator<OWLClass> iterator = filteredSet.iterator();
				System.out.println("Filtered query classes:");
				while(iterator.hasNext()){
					System.out.println(iterator.next());
				}*/
				
				if(!filteredSet.isEmpty()){
					//Get the instances that answer the query
					
					//Remember to include the received query ontology...
					//Set<OWLNamedIndividual> individuals = ontologyManager.getQueryManager().getInstancesForClassQuery(filteredSet.iterator().next(), pizzaOntology, ontology);
					
					//...or import the ontologies into the query ontology and let the reasoner do all the work
					ontologyManager.addImportToOntology(ontology, travelOntology);
					Set<OWLNamedIndividual> individuals = ontologyManager.getQueryManager().getInstancesForClassQuery(filteredSet.iterator().next(), ontology);
					//Print all answers 
/*					Iterator<OWLNamedIndividual> it = individuals.iterator();
					System.out.println("Query answer:");
					while(it.hasNext()){
						OWLNamedIndividual ind = it.next();
						Map<OWLObjectPropertyExpression, Set<OWLIndividual>> s = ind.getObjectPropertyValues(travelOntology);
						Iterable<Set<OWLIndividual>> data = s.values();
						Iterator<Set<OWLIndividual>> iterator = data.iterator();
						while(iterator.hasNext())
							System.out.println(iterator.next());
						Iterable<OWLClassExpression> x = ind.getTypes(travelOntology);
						Iterator<OWLClassExpression> it2 = x.iterator();
						System.out.println("Pañstwo = " + it2.next());
						System.out.println("Miasto = " + ind);	
						System.out.println("--------------");
					}*/
					ACLOWLMessage msgAnswer = new ACLOWLMessage(ACLMessage.QUERY_IF);
					OWLOntology answerOnto = ontologyManager.getQueryManager().prepareQueryAnswerFromInstances(individuals, travelOntology, myAgent);
	        		msgAnswer.addReceiver(new AID("client", AID.ISLOCALNAME));
	        		try {
	        			//Fill the content of the message with ontology
	        			//ontologyManager.fillJadeACLMessageContent(msgAnswer, answerOnto);
	        			
	        			//msgAnswer.setConversationId(msg.getConversationId());
	        			msgAnswer.setOntology(msg.getOntology());
	        			msgAnswer.setContentOntology(answerOnto);
	        			//msgAnswer.setLanguage(msg.getLanguage());
	        			//Send the answer
	        			System.out.println("Agent " + getLocalName() + " answering Client");
		        		send(msgAnswer);
					} catch (OWLOntologyStorageException e) {
						e.printStackTrace();
					}
				}
				
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OWLOntologyStorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           }
           block();
        }
	}
	
}
