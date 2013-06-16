package projekt.agents;

import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

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
	protected String stripFromIRI(String s) {
		String[] temp = s.split("#");
		return temp[1].split(">")[0];
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
			if (msg!=null) {
				try {
					ontology = msg.getContentOntology(ontologyManager,myAgent);
					if(msg.getConversationId() == "Preferences") {
						//System.out.println(msg.getContent());
						System.out.println(msg.getConversationId());
						Set<OWLClass> filteredSet = ontologyManager.getQueryManager().filterOWLQueryClasses(ontology);
						if(!filteredSet.isEmpty()) {
							ontologyManager.addImportToOntology(ontology, travelOntology);
							Set<OWLNamedIndividual> individuals = ontologyManager.getQueryManager().getInstancesForClassQuery(filteredSet.iterator().next(), ontology);
							ACLOWLMessage msgAnswer = new ACLOWLMessage(ACLMessage.QUERY_IF);
							OWLOntology answerOnto = ontologyManager.getQueryManager().prepareQueryAnswerFromInstances(individuals, travelOntology, myAgent);
							msgAnswer.addReceiver(new AID("client", AID.ISLOCALNAME));
							try {
								msgAnswer.setOntology(msg.getOntology());
								msgAnswer.setContentOntology(answerOnto);
								System.out.println("Agent " + getLocalName() + " answering Client");
								msgAnswer.setConversationId(msg.getConversationId());
								send(msgAnswer);
							} catch (OWLOntologyStorageException e) {
								e.printStackTrace();
							}
						}
					}
					else {
						Set<OWLNamedIndividual> indReturn = new HashSet<OWLNamedIndividual>();
						
						ArrayList<Set<OWLNamedIndividual>> indTemp = new ArrayList<Set<OWLNamedIndividual>>();
						indReturn.clear();
						indTemp.clear();
						System.out.println(msg.getContent());
						System.out.println(msg.getConversationId());
						Set<OWLNamedIndividual> individuals = ontologyManager.getQueryManager().filterAnswerSetInstances(ontology);
						Iterator<OWLNamedIndividual> indIterator = individuals.iterator();
						int licznik = 0, ar = 0;
						while(indIterator.hasNext()) {
							licznik++;
							Map<OWLObjectPropertyExpression, Set<OWLIndividual>> individualProperties = indIterator.next().getObjectPropertyValues(travelOntology);
							Iterator<OWLObjectPropertyExpression> indKeys = individualProperties.keySet().iterator();
							while(indKeys.hasNext()) {
								OWLObjectPropertyExpression key = indKeys.next();
								if(stripFromIRI(key.toString()).equals("hasCity")) {
									Iterator<OWLIndividual> valIterator = individualProperties.get(key).iterator();
									Set<OWLNamedIndividual> temp = new HashSet<OWLNamedIndividual>();
									while(valIterator.hasNext()) {
										OWLNamedIndividual individual = (OWLNamedIndividual) valIterator.next();
										//if(indTemp.contains(individual))
										//		indReturn.add(individual);
										temp.add(individual);
									}
									indTemp.add(temp);
								}
							}
						}
						for(int i = 0; i < indTemp.size(); i++) {
							Iterator<OWLNamedIndividual> iterator = indTemp.get(i).iterator();
							while(iterator.hasNext()) {
								int count = 0;
								OWLNamedIndividual ind = iterator.next();
								for(int z = 0; z < indTemp.size(); z++) {
									if(indTemp.get(z).contains(ind)) {
										count++;
									}
								}
								if(count == indTemp.size()) {
									indReturn.add(ind);
									System.out.println("Add to return: " + ind);
								}
							}
						}
						System.out.println(indReturn.toString());
						ACLOWLMessage msgAnswer = new ACLOWLMessage(ACLMessage.QUERY_IF);
						OWLOntology answerOnto;
						answerOnto = ontologyManager.getQueryManager().prepareQueryAnswerFromInstances(indReturn, travelOntology, myAgent);
						msgAnswer.addReceiver(new AID("client", AID.ISLOCALNAME));
						msgAnswer.setOntology(msg.getOntology());
						msgAnswer.setContentOntology(answerOnto);
						System.out.println("Agent " + getLocalName() + " answering Client");
						msgAnswer.setConversationId(msg.getConversationId());
						send(msgAnswer);
						//System.out.println(individualProperties.toString());
					}
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				} catch (OWLOntologyStorageException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			block();
		}
	}

}
