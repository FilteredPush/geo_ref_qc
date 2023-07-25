package org.filteredpush.qc.geo.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	GeoLocateServiceTestIT.class,
	GeoUtilsTestIT.class
	})
public class AllTestsIT {

	private static final Log logger = LogFactory.getLog(AllTestsIT.class);

}
