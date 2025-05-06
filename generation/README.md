space_tests.csv are the subset of TDWG BDQ TG2 tests related to SPACE (spatial) terms, excluding the broad measure tests.

To extract the current list of space tests from the csv list of tests in a checkout of the tdwg/bdq respository: 

Setup assumptions:

    cd ~/git
    git clone git@github.com:FilteredPush/geo_ref_qc.git
    git clone git@github.com:tdwg/bdq.git
    git clone git@github.com:kurator-org/kurator-ffdq.git

Obtain relevant tests from term-version file csv into the geo_ref_qc/generation directory:

    cd geo_ref_qc/generation
    head -n 1 ../../bdq/tg2/core/TG2_tests.csv > space_tests.csv
    grep SPACE ../../bdq/tg2/core/TG2_tests.csv  | grep -v AllDarwinCoreTerms | grep -v AllValidationTestsRunOnSingleRecord | grep -v "AllAmendmentTestsRunOnSingleRecord" | grep -v DATAGENERALIZATIONS  >> space_tests.csv

From this, RDF can be generated to use to obtain test metadata from the test IRIs, or Java code can be generated or marked as needing updates using kurator-ffdq.

Turtle RDF generated using kurator-ffdq using (from a kurator-ffdq directory in the same parent directory as geo_ref_qc) with:

   ./test-util.sh -config ../geo_ref_qc/generation/geo_ref_qc_DwCGeoRefDQ_kurator_ffdq.config -in ../geo_ref_qc/generation/space_tests.csv -out ../geo_ref_qc/generation/space_tests.ttl  -ieGuidFile ../bdq/tg2/core/information_element_guids.csv  -guidFile ../bdq/tg2/core/TG2_tests_additional_guids.csv additional_guids.csv

Stub Java classes generated using kurator-ffdq (from a kurator-ffdq directory in the same parent directory as geo_ref_qc) with: 

   ./test-util.sh -config ../geo_ref_qc/generation/geo_ref_qc_DwCGeoRefDQ_stubs_kurator_ffdq.config -in ../geo_ref_qc/generation/space_tests.csv -out ../geo_ref_qc/generation/space_tests.ttl -srcDir ../geo_ref_qc/src/main/java -generateClass -ieGuidFile ../bdq/tg2/core/information_element_guids.csv  -guidFile ../bdq/tg2/core/TG2_tests_additional_guids.csv additional_guids.csv

Add comments to the end of Java classes noting out of date implementations using kurator-ffdq (from a kurator-ffdq directory in the same parent directory as geo_ref_qc) with: 

   ./test-util.sh -config ../geo_ref_qc/generation/geo_ref_qc_DwCGeoRefDQ_kurator_ffdq.config -in ../geo_ref_qc/generation/space_tests.csv -out ../geo_ref_qc/generation/space_tests.ttl -srcDir ../geo_ref_qc/src/main/java -checkVersion -appendClass -ieGuidFile ../bdq/tg2/core/information_element_guids.csv  -guidFile ../bdq/tg2/core/TG2_tests_additional_guids.csv additional_guids.csv

