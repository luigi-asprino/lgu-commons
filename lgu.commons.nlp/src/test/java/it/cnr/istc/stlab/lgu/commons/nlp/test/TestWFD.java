package it.cnr.istc.stlab.lgu.commons.nlp.test;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.cnr.istc.stlab.lgu.commons.nlp.model.AnnotatedText;
import it.cnr.istc.stlab.lgu.commons.nlp.model.AnnotationType;
import it.cnr.istc.stlab.lgu.commons.nlp.wfd.UKBBasedWFD;
import it.cnr.istc.stlab.lgu.commons.nlp.wsd.UKB;

public class TestWFD {

	@Ignore
	@Test
	public void testWFD() {

		ObjectMapper om = new ObjectMapper();

		AnnotatedText at = UKBBasedWFD.getInstance().disambiguate("I bought a car");

		try {
			System.out.println(om.writeValueAsString(at));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

	}

	@Ignore
	@Test
	public void test() {
		String t = "dentifies a mark or symbol punched into a coin or similar object at some point during its time in circulation. hasReverse Identifies a the face of a numismatic object opposed to the obverse. In ancient, hand-struck coinage it is generally the upper die. In English often known as tails. Thing hasDenomination Describes the monetary value assigned to an object within a denominational system. Examples: tetradrachm, chalkous, denarius. hasDie Describes the die from which a numismatic object has been produced. hasShape Describes the shape of a numismatic object. hasStatedAuthority The authority as stated on a numismatic object. This may, but need not, be the same as the real authority behind the object (expressed separately as hasAuthority). hasLegend Describes the inscription or printing placed on a numismatic object as part of the production process. hasMaxDiameter Describes the maximum diameter of a numismatic object. hasEdge Describes characteristics of the edge of a numismatic object. hasMaxWidth Describes the maximum width of a numismatic object. hasDiameter Describes diameter of an object. hasObjectType Identifies the type of numismatic object. E.g. Coin, token, banknote. hasTypeSeriesItem Identifies the position of a numismatic object within a published or recognized reference list of types, such as a catalogue or corpus. hasHeight Describes the height of a numismatic object. hasIssuer Identifies the person administratively responsible for the issue of a numismatic object, generally named in some capacity on the object. hasMinHeight Describes the minimum height of a numismatic object. hasSecondaryTreatment Describes anomalies or unusual features which arise subsequent to the actual production of a numismatic object; for example halving, chop-marking, etc. hasWear Describes wear visible on a numismatic object due to use or circulation, commonly in terms prescribed by Abnutzung und Korrosion: Bestimmmungstafeln zur Bearbeitung von Fundmünzen = Usure et corrosion. (1995). Lausanne. (OCLC 174260520) hasWeight Describes the actual weight of a numismatic object. hasAxis Describes the directional relationship between the obverse and reverse of a numismatic object. hasEndDate Identifies the final date in a range assigned in a numismatic context. hasMaterial Describes the physical material from which a numismatic object is made. hasPeculiarity Describes a notable, characteristic or unusual physical feature of an individual numismatic object marks it out from other examples of the same group, or of a group of numismatic objects that marks it out from other groups. It can be the result of production, such as plating, or post-production activity, such as piercing, cutting or mounting. hasStartDate Identifies the initial date in a range assigned in a numismatic context. hasMaxHeight Describes the maximum height of a numismatic object. hasMintmark Identifies the letter, symbol or an inscription on a numismatic object indicating the mint where it was produced. hasProductionDate Describes the date (range) of the production of a numismatic object. hasReferenceWork Specifies a published work of reference relevant to a numismatic object. hasControlmark Identifies a letter, symbol, monogram or an inscription on a numismatic object intended to distinguish it as part of a group of numismatic objects within an issue or series. representsObjectType To indicate what kind of object type is represented by (for example) an type series item (a coin, banknote, token, ...). hasProductionObject Objects used within the production process. hasCorrosion Describes degradation due to chemical reaction with an environment, commonly in terms prescribed by Abnutzung und Korrosion: Bestimmmungstafeln zur Bearbeitung von Fundmünzen = Usure et corrosion. (1995). Lausanne. (OCLC 174260520) hasWeightStandard Describes the conventional weight system according to which a numismatic object of intrinsic value was produced. hasBearsDate Describes a date that appears on an object hasFindType Describes the nature of and archaeological or other find. E.g. hoard, ritual deposit, single find, find in survey. hasFindspot Describes the location of the discovery of an object, whether by accident or in archaeological excavation. hasContext Describes the context of a numismatic object. hasFace Identifies a particular face of a numismatic object. Where possible and appropriate, the more specific properties hasObverse or hasReverse are preferred. hasAuthority Identifies the authority in whose name (explicitly or implicitly) a numismatic object was issued. Eg. Charlemagne, Augustus, Sparta, Federal Republic of Germany, Bank of England hasMinDepth Describes the minimum depth of a numismatic object. hasIconography Describes an iconography placed on a numismatic object. hasMinWidth Describes the minimum width of a numismatic object. hasDate Describes date (range) assigned in a numismatic context. hasAppearance Describes the appearance of a numismatic object. Eg. Iconography, legends and physical characteristics. hasMint Identifies the place of manufacture or issue of a numismatic object. hasMinDiameter Describes the minimum diameter of a numismatic object. hasMaxDepth Describes the maximum depth of a numismatic object. hasPeculiarityOfProduction Describes a notable, characteristic or unusual physical feature of an individual numismatic object which distinguishes it from other examples of the same group, or of a group of numismatic objects that marks it out from other groups, and which is related to the process of production of a numismatic object. hasWidth Describes the width of a numismatic object. hasDepth Describes the depth of an object. hasPortrait Identifies the person whose portrait appears on a numismatic object. hasManufacture Describes the method of manufacture of a numismatic object. Eg. struck, cast, printed hasCollection Identifies the modern collection or repository in which a numismatic object resides or has resided in the past. Collections are defined by Nomisma IDs. hasObverse Identifies a particular face of a numismatic object. Normally it will be the face carrying the represenation, badge or inscription of the issuing authority. In ancient, hand-struck coinage it is generally the lower (anvil) die. In English often known as";
		UKB wsd = UKB.getInstance();
		wsd.disambiguate(t).getSentences().forEach(s -> {
			s.getMultiWords().forEach(mw -> {
				if (mw.getScoredAnnotation(AnnotationType.WN30) != null) {
					System.out.println(mw.getTopScoredAnnotation(AnnotationType.WN30).getAnnotation() + " "
							+ mw.getTopScoredAnnotation(AnnotationType.WN30).getScore());
				}
			});
		});
	}
}
