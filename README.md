# geo_ref_qc
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.filteredpush/geo_ref_qc/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.filteredpush/geo_ref_qc)

Data Quality library for dwc:decimalLatitude, dwc:decimalLongitude and other Locality terms.

DOI: 10.5281/zenodo.14064324

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.14064324.svg)](https://doi.org/10.5281/zenodo.14064324)

Tools for working with georeferences in forms found in biodiversity data.  This library provides tools for validating and correcting georeferences in biodiversity data.  It is designed to support the tests defined in the (draft) [BDQ Standard](https://github.com/tdwg/bdq/blob/master/tg2/_review/index.md) for biodiversity data quality, and supports data quality assertions framed using the bdqffdq vocabulary.  The library is designed to be used in a variety of contexts, including as a standalone library, within a a data quality service, or as part of a a data quality workflow.  It takes inputs expressed using Darwin Core terms for spatial concepts, but can be used for quality control of spatial data in other contexts.

## BDQ Standard Tests

The geo_ref_qc library implements the following BDQ Standard tests:

- AMENDMENT_MINDEPTH-MAXDEPTH_FROM_VERBATIM 
- AMENDMENT_COUNTRYCODE_STANDARDIZED 
- VALIDATION_GEODETICDATUM_NOTEMPTY 
- VALIDATION_COUNTRY_COUNTRYCODE_CONSISTENT 
- VALIDATION_COUNTRYSTATEPROVINCE_UNAMBIGUOUS 
- AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT 
- VALIDATION_MINDEPTH_LESSTHAN_MAXDEPTH 
- VALIDATION_DECIMALLATITUDE_INRANGE 
- VALIDATION_LOCATION_NOTEMPTY 
- ISSUE_COORDINATES_CENTEROFCOUNTRY 
- VALIDATION_MINDEPTH_INRANGE 
- AMENDMENT_COORDINATES_FROM_VERBATIM 
- VALIDATION_STATEPROVINCE_FOUND 
- AMENDMENT_COUNTRYCODE_FROM_COORDINATES 
- VALIDATION_COUNTRY_NOTEMPTY 
- VALIDATION_COORDINATEUNCERTAINTY_INRANGE 
- AMENDMENT_GEODETICDATUM_STANDARDIZED 
- VALIDATION_MINELEVATION_INRANGE 
- VALIDATION_DECIMALLONGITUDE_NOTEMPTY 
- VALIDATION_COORDINATES_STATE-PROVINCE_CONSISTENT 
- VALIDATION_DECIMALLONGITUDE_INRANGE 
- VALIDATION_COUNTRYCODE_STANDARD 
- AMENDMENT_COORDINATES_TRANSPOSED 
- VALIDATION_COORDINATES_COUNTRYCODE_CONSISTENT 
- VALIDATION_MAXDEPTH_INRANGE 
- VALIDATION_COORDINATES_ZERO 
- VALIDATION_MINELEVATION_LESSTHAN_MAXELEVATION 
- VALIDATION_GEODETICDATUM_STANDARD 
- VALIDATION_COORDINATES_TERRESTRIALMARINE 
- AMENDMENT_MINELEVATION-MAXELEVATION_FROM_VERBATIM 
- VALIDATION_COUNTRYCODE_NOTEMPTY 
- VALIDATION_MAXELEVATION_INRANGE 
- VALIDATION_DECIMALLATITUDE_NOTEMPTY 
- VALIDATION_COUNTRY_FOUND 

## Remote services and spatial data

Tests that use the Getty Thesaurus of Geographic Names (TGN) make requests to its web services through a 
single shared client (`org.filteredpush.qc.georeference.util.RemoteServiceClient`), which:

- identifies itself with the User-Agent `FilteredPush-geo_ref_qc/{version} (+https://github.com/FilteredPush/geo_ref_qc)` and has explicit connect and read timeouts,
- limits the number of concurrent requests, and the minimum interval between requests, so that many concurrent 
  callers (e.g. multithreaded test execution) produce a throttled stream of requests rather than a burst,
- retries only plausibly transient failures (HTTP 408, 429, 500, 502, 503, 504, and connection failures) with 
  exponential backoff and jitter, honoring `Retry-After`,
- caches responses, including responses that find no match, so repeated lookups are not resent, and sends only 
  one request at a time for the same lookup,
- remembers failed requests for a period, during which the same request fails without being resent, 
- has a circuit breaker: after a number of consecutive failed requests, requests fail without being sent for a period 
  (tests report EXTERNAL_PREREQUISITES_NOT_MET), then a single trial request is allowed, and requests resume if it succeeds.

These settings can be changed with java system properties, set before the first lookup is made:

| System property | Default | Meaning |
| --- | --- | --- |
| `geo_ref_qc.userAgent` | `FilteredPush-geo_ref_qc/{version} (+https://github.com/FilteredPush/geo_ref_qc)` | User-Agent header |
| `geo_ref_qc.connectTimeoutMillis` | 10000 | Connect timeout |
| `geo_ref_qc.readTimeoutMillis` | 30000 | Read timeout |
| `geo_ref_qc.maxConcurrentRequests` | 2 | Maximum concurrent requests to the service |
| `geo_ref_qc.minRequestIntervalMillis` | 100 | Minimum interval between the start of requests (0 for none) |
| `geo_ref_qc.acquireTimeoutMillis` | 60000 | Longest wait for a turn to make a request before failing |
| `geo_ref_qc.maxRetries` | 3 | Retries after a transient failure (total attempts = maxRetries + 1) |
| `geo_ref_qc.backoffBaseMillis` | 500 | Base delay for exponential backoff |
| `geo_ref_qc.backoffMaxMillis` | 8000 | Maximum backoff delay |
| `geo_ref_qc.maxRetryAfterMillis` | 30000 | Longest `Retry-After` that will be waited for, longer requests fail without retrying |
| `geo_ref_qc.cacheSize` | 5000 | Maximum number of cached responses, 0 disables caching |
| `geo_ref_qc.failureCacheMillis` | 60000 | How long a failed request is remembered, 0 to not remember failures |
| `geo_ref_qc.circuitBreakerThreshold` | 5 | Consecutive failed requests that trip the circuit breaker, 0 to disable it |
| `geo_ref_qc.circuitBreakerOpenMillis` | 60000 | How long requests fail without being sent once the circuit breaker has tripped |

For example:

    java -Dgeo_ref_qc.maxConcurrentRequests=4 -Dgeo_ref_qc.maxRetries=5 -jar ...

Tests that use WoRMS do so through sci_name_qc, see its documentation for the equivalent settings.

The shapefiles used by spatial tests are opened once and shared between threads 
(`org.filteredpush.qc.georeference.util.SharedShapefiles`).  When running from the geo_ref_qc jar, 
the shapefiles are first copied from the jar to a temporary directory (deleted when the JVM exits), 
as reading a shapefile from inside a jar requires decompressing the whole file for every query.  
A container that unloads this library can call `SharedShapefiles.disposeAll()` to release them.

# Include using maven

Available in Maven Central.  

    <dependency>
        <groupId>org.filteredpush</groupId>
        <artifactId>geo_ref_qc</artifactId>
        <version>2.1.1</version>
    </dependency>


# Building

    mvn package

# Developer deployment: 

To deploy a snapshot to the snapshotRepository: 

    mvn clean deploy

To deploy a new release to maven central, set the version in pom.xml to a non-snapshot version, then deploy with the release profile (which adds package signing and deployment to release staging:

    mvn clean deploy -P release

After this, you will need to login to the sonatype oss repository hosting nexus instance, find the staged release in the staging repositories, and perform the release.  It should be possible (haven't verified this yet) to perform the release from the command line instead by running: 

    mvn nexus-staging:release -P release

