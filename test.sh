cd /opt/lgu-commons
mvn exec:java -Dexec.mainClass="it.cnr.istc.stlab.lgu.commons.arrays.testMerge"  -DjvmArgs="-Xmx64g -XX:-UseGCOverheadLimit"