cd /opt/lgu-commons
mvn exec:java -Dexec.mainClass="it.cnr.istc.stlab.lgu.commons.test.testMerge"  -DjvmArgs="-Xmx64g -XX:-UseGCOverheadLimit"