package org.filteredpush.qc.geo.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CountryLookupTest.class, GeoTesterTest.class, GeoUtiltsTest.class })
public class AllTests {

	private static final Log logger = LogFactory.getLog(AllTests.class);

}
