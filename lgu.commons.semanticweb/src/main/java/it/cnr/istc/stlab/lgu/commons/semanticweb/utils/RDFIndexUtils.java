package it.cnr.istc.stlab.lgu.commons.semanticweb.utils;

import it.cnr.istc.stlab.rocksmap.RocksMultiMap;
import it.cnr.istc.stlab.rocksmap.transformer.StringRocksTransformer;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFBase;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class RDFIndexUtils {

    private static final Logger logger = LoggerFactory.getLogger(RDFIndexUtils.class);

    public static void main(String[] args) throws RocksDBException {
        createIndexOfLiterals("/Users/lgu/Desktop/NOTime/SPICE/DBPediaCategories/categories_lang=en_labels.ttl.bz2", "/Users/lgu/Desktop/NOTime/SPICE/DBPediaCategories/categories_labels", RDFS.label.getURI());
    }

    public static void createIndexOfLiterals(final String inputFile, final  String dbPath, final String predicate) throws RocksDBException {
        logger.info("Creating An Index for Datatype Property {}", predicate);
        final AtomicInteger ai = new AtomicInteger();
        final RocksMultiMap<String, String> subjectMultiMap = new RocksMultiMap<>(dbPath, new StringRocksTransformer(), new StringRocksTransformer());
        final Property predicateToMatch = ResourceFactory.createProperty(predicate);
        StreamRDF destination = new StreamRDFBase() {

            @Override
            public void triple(Triple triple) {
                if (triple.getPredicate().matches(predicateToMatch.asNode())) {
                    logger.trace("Adding {} {}", triple.getSubject().getURI(), triple.getObject().getLiteral().getValue().toString());
                    subjectMultiMap.put(triple.getSubject().getURI(), triple.getObject().getLiteral().getValue().toString());
                }
                if (ai.incrementAndGet() % 100000 == 0) {
                    logger.info("{} triples processed", ai.get());
                }
            }

            @Override
            public void quad(Quad quad) {
                triple(quad.asTriple());
            }
        };
        RDFDataMgr.parse(destination, inputFile);
        subjectMultiMap.close();
        logger.info("End");
    }
}
