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

### Remembered failures (`geo_ref_qc.failureCacheMillis`)

When a Getty TGN request fails, after any retries, the failure is remembered for `geo_ref_qc.failureCacheMillis` (default 60000 ms, 
one minute).  Until then, the same request fails immediately without being sent to the service.  This keeps a failing lookup 
from being resent for every record that contains the same country or state/province name.

- **What is remembered:** failures after retries have been exhausted, both connection failures and HTTP error statuses, including 
  non-transient ones (e.g. 400, 404).  Failures are not remembered for requests that were never sent (the circuit breaker was open, 
  or no turn to make the request became available in time), for malformed URLs, or for interrupted requests.  A response that 
  was received but could not be interpreted is not remembered as a failure; it is removed from the response cache, so the next 
  lookup requests it again.
- **What counts as the same request:** failures are remembered for each request URL.  Different `GettyLookup` methods that make 
  the same request share it: `lookupCountry`, `lookupCountryExact`, and `getNamesForCountry` make the same request for a country, 
  and `lookupPrimary`, `lookupUniquePrimary`, and `getPrimaryObjects` make the same request for a state/province.  So a failure 
  affects all of the lookups that make that request, until it expires.
- **How a remembered failure is reported:** the client throws a `ServiceUnavailableException` (an `IOException`), with a message 
  of the form `Getty TGN request failed recently, not resending {url} yet:` followed by the original failure, which is its cause 
  (an `HttpStatusException` carrying the HTTP status code, for an HTTP error).  `GettyLookup` handles it as it handles any other 
  failure to reach the Getty TGN:
  - `lookupCountry`, `lookupCountryExact`, `getCountryObjects`, and `getPrimaryObjects` throw a `SourceAuthorityException`;
  - `getNamesForCountry` throws a `GeorefServiceException`;
  - `lookupPrimary`, `lookupUniquePrimary`, `getPreferredCountryName`, `getParentageForPrimary`, and `getPrimaryObject` return `null`;
  - `lookupParent` returns an empty string.
  
  Tests that use the TGN, such as VALIDATION_COUNTRY_FOUND and VALIDATION_STATEPROVINCE_FOUND, report these failures 
  as EXTERNAL_PREREQUISITES_NOT_MET.
- **The drawback:** if the Getty TGN recovers during the period, lookups that failed shortly before still fail until their 
  failures expire.

Setting `geo_ref_qc.failureCacheMillis=0` turns this off, so every request that is not answered from the response cache is sent 
to the Getty TGN again, with its retries:

    java -Dgeo_ref_qc.failureCacheMillis=0 -jar ...

The `geo_ref_qc.*` settings are read once, when the first Getty TGN lookup is made, so this must be set when the JVM starts (or 
before any lookup).  At any time, `GettyLookup.getGettyClient().reset()` clears the remembered failures, together with the cached 
responses, and closes the circuit breaker.

Related settings work independently of `geo_ref_qc.failureCacheMillis`:

- `geo_ref_qc.cacheSize=0` stops responses (including "no match" responses) being cached by the client, but failures are still 
  remembered (unless `geo_ref_qc.failureCacheMillis=0` is also set), and threads making the same request at the same moment still 
  share it.  Some results are also cached by `GettyLookup` and `GeoUtilSingleton` themselves (e.g. countries and states/provinces 
  that were found or not found); those caches never hold failures, and are not affected by these settings.
- The circuit breaker still stops requests to the Getty TGN while it keeps failing, whatever `geo_ref_qc.failureCacheMillis` 
  is set to.  `geo_ref_qc.circuitBreakerThreshold=0` disables it.  `GettyLookup.getGettyClient().isCircuitOpen()` reports 
  whether it is open, and `GettyLookup.getGettyClient().reset()` closes it.
- With both `geo_ref_qc.failureCacheMillis=0` and `geo_ref_qc.circuitBreakerThreshold=0`, each request to a Getty TGN that is 
  down makes all of its attempts (by default four, with backoff between them) before failing.

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

