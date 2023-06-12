space_tests.csv are the subset of TDWG BDQ TG2 tests related to SPACE (spatial) terms, excluding the broad measure tests.

To extract the current list of space tests from the csv list of tests in a checkout of the tdwg/bdq respository: 

    grep "IE Category" ../../bdq/tg2/core/TG2_tests.csv > space_tests.csv
    grep SPACE ../../bdq/tg2/core/TG2_tests.csv  | grep -v AllDarwinCoreTerms  >> space_tests.csv

DwCGeoRefDQ class and Turtle RDF generated using kurator-ffdq using (from a kurator-ffdq directory in the same parent directory as geo_ref_qc) with:

   ./test-util.sh -config ../geo_ref_qc/generation/geo_ref_qc_DwCGeoRefDQ_kurator_ffdq.config -in ../geo_ref_qc/generation/space_tests.csv -out ../geo_ref_qc/generation/space_tests.ttl -generateClass -srcDir ../geo_ref_qc/src/main/java/
