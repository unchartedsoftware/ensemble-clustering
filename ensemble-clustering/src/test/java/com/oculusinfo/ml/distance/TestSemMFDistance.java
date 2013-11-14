/**
 * Copyright (c) 2013 Oculus Info Inc.
 * http://www.oculusinfo.com/
 *
 * Released under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oculusinfo.ml.distance;

import com.oculusinfo.ml.feature.semantic.distance.Concept;

public class TestSemMFDistance {

	double epsilon = 0.00001;
	
	@SuppressWarnings("unused")
	private boolean isEqual(double d1, double d2) {
		return (Math.abs( d1 - d2 ) < epsilon );
	}
	
	@SuppressWarnings("unused")
	private Concept buildTaxonomy() {
		
		// Create root
		Concept root = new Concept("Thing");
		Concept entity = new Concept("Entity", root);
		Concept event = new Concept("Event", root);
		Concept eventAttribute = new Concept("EventAttribute", root);
		Concept mention = new Concept("Mention", root);
		Concept fact = new Concept("Fact", root);
		Concept stringValue = new Concept("StringValue", root);
		root.addChild(entity);
		root.addChild(event);
		root.addChild(eventAttribute);
		root.addChild(mention);
		root.addChild(fact);
		root.addChild(stringValue);
		
		// create entity
		Concept facility = new Concept("Facility", entity);
		Concept gpe = new Concept("GPE", entity);
		Concept location = new Concept("Location", entity);
		Concept organization = new Concept("Organization", entity);
		Concept person = new Concept("Person", entity);
		Concept vehicle = new Concept("Vehicle", entity);
		Concept weapon = new Concept("Weapon", entity);
		entity.addChild(new Concept("Document", entity));
		entity.addChild(new Concept("DocumentGroup", entity));
		entity.addChild(new Concept("DocumentSegment", entity));
		entity.addChild(facility);
		entity.addChild(gpe);
		entity.addChild(location);
		entity.addChild(organization);
		entity.addChild(person);
		entity.addChild(vehicle);
		entity.addChild(weapon);
		
		// create facility
		facility.addChild(new Concept("Airport", facility));
		facility.addChild(new Concept("BuildingOrGrounds", facility));
		facility.addChild(new Concept("IndustrialPlant", facility));
		facility.addChild(new Concept("Path", facility));
		facility.addChild(new Concept("SubAreaFacility", facility));
		
		// create GPE
		gpe.addChild(new Concept("City", gpe));
		gpe.addChild(new Concept("Continent", gpe));
		gpe.addChild(new Concept("CountyOrDistricct", gpe));
		gpe.addChild(new Concept("GPECluster", gpe));
		gpe.addChild(new Concept("Nation", gpe));
		gpe.addChild(new Concept("PopulationCenter", gpe));
		gpe.addChild(new Concept("SpecialGPE", gpe));
		gpe.addChild(new Concept("StateOrProvince", gpe));
		
		// create Location
		location.addChild(new Concept("Address", location));
		location.addChild(new Concept("BoundaryLocation", location));
		location.addChild(new Concept("CelestialLocation", location));
		location.addChild(new Concept("GeneralRegion", location));
		location.addChild(new Concept("InternationalRegion", location));
		location.addChild(new Concept("NaturalLandRegion", location));
		location.addChild(new Concept("WaterBody", location));
		
		// create Organization
		organization.addChild(new Concept("Clan", organization));
		organization.addChild(new Concept("CommercialOrganization", organization));
		organization.addChild(new Concept("EducationalOrganization", organization));
		organization.addChild(new Concept("EntertainmentOrganization", organization));
		organization.addChild(new Concept("Family", organization));
		organization.addChild(new Concept("GovernmentalOrganization", organization));
		organization.addChild(new Concept("MediaOrganization", organization));
		organization.addChild(new Concept("MedicalScienceOrganization", organization));
		organization.addChild(new Concept("Network", organization));
		organization.addChild(new Concept("NonGovernmentalOrganization", organization));
		organization.addChild(new Concept("ReligiousOrganization", organization));
		organization.addChild(new Concept("SportsOrganization", organization));
		organization.addChild(new Concept("Tribe", organization));
		
		// create Person
		person.addChild(new Concept("Group", person));
		person.addChild(new Concept("IndividualPerson", person));
		
		// create Vehicle
		vehicle.addChild(new Concept("AirVehicle", vehicle));
		vehicle.addChild(new Concept("LandVehicle", vehicle));
		vehicle.addChild(new Concept("SubAreaVehicle", vehicle));
		vehicle.addChild(new Concept("WaterVehicle", vehicle));
		
		// create Weapon
		weapon.addChild(new Concept("BiologicalWeapon", weapon));
		weapon.addChild(new Concept("BluntWeapon", weapon));
		weapon.addChild(new Concept("ChemicalWeapon", weapon));
		weapon.addChild(new Concept("ExplodingWeapon", weapon));
		weapon.addChild(new Concept("NuclearWeapon", weapon));
		weapon.addChild(new Concept("Projectile", weapon));
		weapon.addChild(new Concept("SharpWeapon", weapon));
		weapon.addChild(new Concept("ShootingWeapon", weapon));
		
		// create Event
		Concept businessEvent = new Concept("BusinessEvent", event);
		Concept bankruptcyEvent = new Concept("BankruptcyDeclaration", businessEvent);
		Concept mergerEvent = new Concept("Merger", businessEvent);
		Concept orgCessationEvent = new Concept("OrganizationalCessation", businessEvent);
		Concept orgFoundingEvent = new Concept("OrganizationalFounding", businessEvent);
		businessEvent.addChild(bankruptcyEvent);
		businessEvent.addChild(mergerEvent);
		businessEvent.addChild(orgCessationEvent);
		businessEvent.addChild(orgFoundingEvent);
		
		Concept conflictEvent = new Concept("ConflictEvent", event);
		Concept attack = new Concept("Attack", conflictEvent);
		Concept demonstration = new Concept("Demonstration", conflictEvent);
		conflictEvent.addChild(attack);
		conflictEvent.addChild(demonstration);
		
		Concept contactEvent = new Concept("ContactEvent", event);
		Concept meeting = new Concept("Meeting", contactEvent);
		Concept phone = new Concept("PhoneOrWrite", contactEvent);
		contactEvent.addChild(meeting);
		contactEvent.addChild(phone);
		
		Concept justiceEvent = new Concept("JusticeEvent", event);
		Concept acquital = new Concept("Acquital", justiceEvent);
		Concept appeal = new Concept("Appeal", justiceEvent);
		Concept arrest = new Concept("Arrest", justiceEvent);
		Concept charge = new Concept("ChargeOrIndictment", justiceEvent);
		Concept conviction = new Concept("Conviction", justiceEvent);
		Concept execution = new Concept("Execution", justiceEvent);
		Concept extradiction = new Concept("Extradiction", justiceEvent);
		Concept lawsuit = new Concept("Lawsuit", justiceEvent);
		Concept levyingOfFine = new Concept("LevyingOfFine", justiceEvent);
		Concept pardon = new Concept("Pardon", justiceEvent);
		Concept releaseOrParole = new Concept("ReleaseOrParole", justiceEvent);
		Concept sentencing = new Concept("Sentencing", justiceEvent);
		Concept trialOrHearing = new Concept("TrialOrHearing", justiceEvent);
		justiceEvent.addChild(acquital);
		justiceEvent.addChild(appeal);
		justiceEvent.addChild(arrest);
		justiceEvent.addChild(charge);
		justiceEvent.addChild(conviction);
		justiceEvent.addChild(execution);
		justiceEvent.addChild(extradiction);
		justiceEvent.addChild(lawsuit);
		justiceEvent.addChild(levyingOfFine);
		justiceEvent.addChild(pardon);
		justiceEvent.addChild(releaseOrParole);
		justiceEvent.addChild(sentencing);
		justiceEvent.addChild(trialOrHearing);
		
		Concept lifeEvent = new Concept("LifeEvent", event);
		Concept birth = new Concept("Birth", lifeEvent);
		Concept death = new Concept("Death", lifeEvent);
		Concept divorce = new Concept("Divorce", lifeEvent);
		Concept injury = new Concept("Injury", lifeEvent);
		Concept marriage = new Concept("MarriageEvent", lifeEvent);
		lifeEvent.addChild(birth);
		lifeEvent.addChild(death);
		lifeEvent.addChild(divorce);
		lifeEvent.addChild(injury);
		lifeEvent.addChild(marriage);
		
		Concept movement = new Concept("Movement", event);
		Concept transport = new Concept("Transport", movement);
		movement.addChild(transport);
		
		Concept personnelEvent = new Concept("PersonnelEvent", event);
		Concept election = new Concept("Election", personnelEvent);
		Concept nomination = new Concept("Nomination", personnelEvent);
		Concept positionEnd = new Concept("PositionEnd", personnelEvent);
		Concept positionStart = new Concept("PositionStart", personnelEvent);
		personnelEvent.addChild(election);
		personnelEvent.addChild(nomination);
		personnelEvent.addChild(positionEnd);
		personnelEvent.addChild(positionStart);
		
		Concept transaction = new Concept("Transaction", event);
		Concept moneyTransfer = new Concept("MoneyTransfer", transaction);
		Concept ownershipTransfer = new Concept("OwnershipTransfer", transaction);
		transaction.addChild(moneyTransfer);
		transaction.addChild(ownershipTransfer);
		
		event.addChild(businessEvent);
		event.addChild(conflictEvent);
		event.addChild(contactEvent);
		event.addChild(new Concept("EducationEvent", event));
		event.addChild(justiceEvent);
		event.addChild(lifeEvent);
		event.addChild(movement);
		event.addChild(personnelEvent);
		event.addChild(transaction);
		event.addChild(new Concept("hasPlaceOfResidenceEvent", event));
		event.addChild(new Concept("isSeenAt", event));
		
		// create EventAttribute
		eventAttribute.addChild(new Concept("Genericity", eventAttribute));
		eventAttribute.addChild(new Concept("Modality", eventAttribute));
		eventAttribute.addChild(new Concept("Polarity", eventAttribute));
		eventAttribute.addChild(new Concept("Tense", eventAttribute));
		
		// create Fact
		fact.addChild(new Concept("UWElement", fact));
		fact.addChild(new Concept("UWTriple", fact));
		
		// create Mention
		mention.addChild(new Concept("FacilityMention", mention));
		mention.addChild(new Concept("GPEMention", mention));
		mention.addChild(new Concept("LocationMention", mention));
		mention.addChild(new Concept("OrganizationMention", mention));
		mention.addChild(new Concept("PersonMention", mention));
		mention.addChild(new Concept("VehicleMention", mention));
		mention.addChild(new Concept("WeaponMention", mention));
		
		// create StringValue
		Concept contactInfo = new Concept("ContactInformation", stringValue);
		Concept numericValue = new Concept("NumericValue", stringValue);
		stringValue.addChild(contactInfo);
		stringValue.addChild(new Concept("Crime", stringValue));
		stringValue.addChild(new Concept("JobTitle", stringValue));
		stringValue.addChild(numericValue);
		stringValue.addChild(new Concept("OrganizationName", stringValue));
		stringValue.addChild(new Concept("PenalSentence", stringValue));
		stringValue.addChild(new Concept("PersonName", stringValue));
		stringValue.addChild(new Concept("Timex2", stringValue));
		
		// create ContactInformation
		contactInfo.addChild(new Concept("EmailAddress", contactInfo));
		contactInfo.addChild(new Concept("PhoneNumber", contactInfo));
		contactInfo.addChild(new Concept("URL", contactInfo));
		
		// create NumericValue
		numericValue.addChild(new Concept("MonetaryAmount", numericValue));
		numericValue.addChild(new Concept("Percent", numericValue));
		
		return root;
	}
	
//	@Test
//	public void testTiming() {
//		SemanticFeature t1 = new SemanticFeature();
//		t1.setValue("Sentencing", "");
//		
//		SemanticFeature t2 = new SemanticFeature();
//		t2.setValue("Attack", "");
//		
//		Concept taxonomy = buildTaxonomy();
//		SemMFDistance d = new SemMFDistance(taxonomy);
////		EuclideanDistance d = new EuclideanDistance(1);
//		
//		long start = System.currentTimeMillis();
//		double distance = 0;
//
//		for (int i=0; i < 30000*3000; i++) {
////			distance = d.distance(t1, t2);
//			distance = d.aveMinDistance(Collections.singleton(t1), Collections.singleton(t2));
////			Concept cx = taxonomy.findConcept(t1.getConcept());
////			Concept cy = taxonomy.findConcept(t2.getConcept());
////			Concept lca = cx.findCommonAncestor(cy);
//		}
//		double distanceTime = System.currentTimeMillis() - start;
//		System.out.println("Time: " + distanceTime/1000);
//	}
	
//	@Test
//	public void testSameConcept() {
//		SemanticFeature t1 = new SemanticFeature();
//		t1.setValue("Event", "");
//		
//		SemanticFeature t2 = new SemanticFeature();
//		t2.setValue("Event", "");
//		
//		Concept taxonomy = buildTaxonomy();
//		SemMFDistance d = new SemMFDistance(taxonomy);
//		
//		double distance = d.aveMinDistance(Collections.singleton(t1), Collections.singleton(t2));
//		System.out.println(distance);
//		Assert.assertTrue(isEqual(distance, 0));
//	}
//	
//	@Test
//	public void testDiffSubConcept() {
//		SemanticFeature t1 = new SemanticFeature();
//		t1.setValue("Event", "");
//		
//		SemanticFeature t2 = new SemanticFeature();
//		t2.setValue("BusinessEvent", "");
//		
//		Concept taxonomy = buildTaxonomy();
//		SemMFDistance d = new SemMFDistance(taxonomy);
//		
//		double distance = d.aveMinDistance(Collections.singleton(t1), Collections.singleton(t2));
//		System.out.println(distance);
//		Assert.assertTrue(isEqual(distance, 0.96875));
//	}
//	
//	@Test
//	public void testDiffSubConcept2() {
//		SemanticFeature t1 = new SemanticFeature();
//		t1.setValue("Event", "");
//		
//		SemanticFeature t2 = new SemanticFeature();
//		t2.setValue("Merger", "");
//		
//		Concept taxonomy = buildTaxonomy();
//		SemMFDistance d = new SemMFDistance(taxonomy);
//		
//		double distance = d.aveMinDistance(Collections.singleton(t1), Collections.singleton(t2));
//		System.out.println(distance);
//		Assert.assertTrue(isEqual(distance, 0.33333));
//	}
//	
//	@Test
//	public void testDiffConceptSameLevel() {
//		SemanticFeature t1 = new SemanticFeature();
//		t1.setValue("BusinessEvent", "");
//		
//		SemanticFeature t2 = new SemanticFeature();
//		t2.setValue("JusticeEvent", "");
//		
//		Concept taxonomy = buildTaxonomy();
//		SemMFDistance d = new SemMFDistance(taxonomy);
//		
//		double distance = d.aveMinDistance(Collections.singleton(t1), Collections.singleton(t2));
//		System.out.println(distance);
//		Assert.assertTrue(isEqual(distance, 0.33333));
//	}
//	
//	@Test
//	public void testDiffConceptDiffLevel() {
//		SemanticFeature t1 = new SemanticFeature();
//		t1.setValue("BusinessEvent", "");
//		
//		SemanticFeature t2 = new SemanticFeature();
//		t2.setValue("Sentencing", "");
//		
//		Concept taxonomy = buildTaxonomy();
//		SemMFDistance d = new SemMFDistance(taxonomy);
//		
//		double distance = d.aveMinDistance(Collections.singleton(t1), Collections.singleton(t2));
//		System.out.println(distance);
//		Assert.assertTrue(isEqual(distance, 0.42857));
//	}
//	
//	@Test
//	public void testDiffConceptDiffLevel2() {
//		SemanticFeature t1 = new SemanticFeature();
//		t1.setValue("Merger", "");
//		
//		SemanticFeature t2 = new SemanticFeature();
//		t2.setValue("Sentencing", "");
//		
//		Concept taxonomy = buildTaxonomy();
//		SemMFDistance d = new SemMFDistance(taxonomy);
//		
//		double distance = d.aveMinDistance(Collections.singleton(t1), Collections.singleton(t2));
//		System.out.println(distance);
//		Assert.assertTrue(isEqual(distance, 0.5));
//	}
//	
//	@Test
//	public void testDiffConceptDiffLevel3() {
//		SemanticFeature t1 = new SemanticFeature();
//		t1.setValue("Address", "");
//		
//		SemanticFeature t2 = new SemanticFeature();
//		t2.setValue("Sentencing", "");
//		
//		Concept taxonomy = buildTaxonomy();
//		SemMFDistance d = new SemMFDistance(taxonomy);
//		
//		double distance = d.aveMinDistance(Collections.singleton(t1), Collections.singleton(t2));
//		System.out.println(distance);
//		Assert.assertTrue(isEqual(distance, 0.75));
//	}
}
