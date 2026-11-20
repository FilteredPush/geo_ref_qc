# geo_ref_qc
Data Quality library for dwc:decimalLatitude, dwc:decimalLongitude and other Locality terms.

DOI: 10.5281/zenodo.14064324

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.14064324.svg)](https://doi.org/10.5281/zenodo.14064324)

Tools for working with georeferences in forms found in biodiversity data.  This library provides tools for validating and correcting georeferences in biodiversity data.  It is designed to support the tests defined in the (draft) [BDQ Standard](https://github.com/tdwg/bdq/blob/master/tg2/_review/index.md) for biodiversity data quality, and supports data quality assertions framed using the bdqffdq vocabulary.  The library is designed to be used in a variety of contexts, including as a standalone library, within a a data quality service, or as part of a a data quality workflow.  It takes inputs expressed using Darwin Core terms for spatial concepts, but can be used for quality control of spatial data in other contexts.


## Running these Tests on a data set

To run these Tests over a whole data set without writing code, use [bdq_workbench](https://github.com/FilteredPush/bdq_workbench). It reads a Darwin Core Archive or a Darwin Core Data Package, finds the Tests that a chosen BDQ Use Case calls for among the Test implementations it bundles (geo_ref_qc's SPACE Tests, together with those in sci_name_qc, event_date_qc and rec_occur_qc), and runs them on each record: before amendment, then the Amendments, then again with the proposed amendments applied. It reports the results as HTML and Markdown with overview charts, as a spreadsheet (XLSX) with cells colored by outcome, and as RDF following the BDQ Standard.

Download the executable jar from the [latest bdq_workbench release](https://github.com/FilteredPush/bdq_workbench/releases/latest) (Java 17 or later), then:

```bash
java -jar bdq_workbench-<version>.jar                                  # desktop GUI: choose the data set and the Use Case
java -jar bdq_workbench-<version>.jar --help                            # options for running from the command line
```

To check the implementations against the BDQ Test validation data instead, use [bdqtestrunner](https://github.com/FilteredPush/bdqtestrunner).

# Include using maven

Available in [![Maven Central](https://img.shields.io/maven-central/v/org.filteredpush/geo_ref_qc)](https://central.sonatype.com/artifact/org.filteredpush/geo_ref_qc).

    <dependency>
        <groupId>org.filteredpush</groupId>
        <artifactId>geo_ref_qc</artifactId>
        <version>2.2.0</version>
    </dependency>



## BDQ Standard Tests

geo_ref_qc implements the following CORE Tests of the draft TDWG [BDQ Standard](https://bdq.tdwg.org/) ([review draft](https://bdq.tdwg.org/draft/)). Each links to its entry in the BDQ Quick Reference Guide.

**Validations**

- [VALIDATION_COORDINATESCOUNTRYCODE_CONSISTENT](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_COORDINATESCOUNTRYCODE_CONSISTENT)
- [VALIDATION_COORDINATESSTATEPROVINCE_CONSISTENT](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_COORDINATESSTATEPROVINCE_CONSISTENT)
- [VALIDATION_COORDINATESTERRESTRIALMARINE_CONSISTENT](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_COORDINATESTERRESTRIALMARINE_CONSISTENT)
- [VALIDATION_COORDINATES_NOTZERO](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_COORDINATES_NOTZERO)
- [VALIDATION_COORDINATEUNCERTAINTY_INRANGE](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_COORDINATEUNCERTAINTY_INRANGE)
- [VALIDATION_COUNTRYCODE_NOTEMPTY](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_COUNTRYCODE_NOTEMPTY)
- [VALIDATION_COUNTRYCODE_STANDARD](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_COUNTRYCODE_STANDARD)
- [VALIDATION_COUNTRYCOUNTRYCODE_CONSISTENT](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_COUNTRYCOUNTRYCODE_CONSISTENT)
- [VALIDATION_COUNTRYSTATEPROVINCE_UNAMBIGUOUS](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_COUNTRYSTATEPROVINCE_UNAMBIGUOUS)
- [VALIDATION_COUNTRY_FOUND](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_COUNTRY_FOUND)
- [VALIDATION_COUNTRY_NOTEMPTY](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_COUNTRY_NOTEMPTY)
- [VALIDATION_DECIMALLATITUDE_INRANGE](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_DECIMALLATITUDE_INRANGE)
- [VALIDATION_DECIMALLATITUDE_NOTEMPTY](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_DECIMALLATITUDE_NOTEMPTY)
- [VALIDATION_DECIMALLONGITUDE_INRANGE](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_DECIMALLONGITUDE_INRANGE)
- [VALIDATION_DECIMALLONGITUDE_NOTEMPTY](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_DECIMALLONGITUDE_NOTEMPTY)
- [VALIDATION_GEODETICDATUM_NOTEMPTY](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_GEODETICDATUM_NOTEMPTY)
- [VALIDATION_GEODETICDATUM_STANDARD](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_GEODETICDATUM_STANDARD)
- [VALIDATION_LOCATION_NOTEMPTY](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_LOCATION_NOTEMPTY)
- [VALIDATION_MAXDEPTH_INRANGE](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_MAXDEPTH_INRANGE)
- [VALIDATION_MAXELEVATION_INRANGE](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_MAXELEVATION_INRANGE)
- [VALIDATION_MINDEPTH_INRANGE](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_MINDEPTH_INRANGE)
- [VALIDATION_MINDEPTH_LESSTHAN_MAXDEPTH](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_MINDEPTH_LESSTHAN_MAXDEPTH)
- [VALIDATION_MINELEVATION_INRANGE](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_MINELEVATION_INRANGE)
- [VALIDATION_MINELEVATION_LESSTHAN_MAXELEVATION](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_MINELEVATION_LESSTHAN_MAXELEVATION)
- [VALIDATION_STATEPROVINCE_FOUND](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_STATEPROVINCE_FOUND)

**Amendments**

- [AMENDMENT_COORDINATES_FROM_VERBATIM](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_COORDINATES_FROM_VERBATIM)
- [AMENDMENT_COORDINATES_TRANSPOSED](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_COORDINATES_TRANSPOSED)
- [AMENDMENT_COUNTRYCODE_FROM_COORDINATES](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_COUNTRYCODE_FROM_COORDINATES)
- [AMENDMENT_COUNTRYCODE_STANDARDIZED](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_COUNTRYCODE_STANDARDIZED)
- [AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT)
- [AMENDMENT_GEODETICDATUM_STANDARDIZED](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_GEODETICDATUM_STANDARDIZED)
- [AMENDMENT_MINDEPTHMAXDEPTH_FROM_VERBATIM](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_MINDEPTHMAXDEPTH_FROM_VERBATIM)
- [AMENDMENT_MINELEVATIONMAXELEVATION_FROM_VERBATIM](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_MINELEVATIONMAXELEVATION_FROM_VERBATIM)

**Issues**

- [ISSUE_COORDINATES_CENTEROFCOUNTRY](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#ISSUE_COORDINATES_CENTEROFCOUNTRY)

### Supplementary Tests

These Tests are not in the BDQ Standard; each links to its tdwg/bdq issue.

**Validations**

- VALIDATION_COUNTRYSTATEPROVINCE_CONSISTENT ([tdwg/bdq#200](https://github.com/tdwg/bdq/issues/200))

Also experimental: AMENDMENT_COORDINATES_CONVERTED, from an immature Test proposal ([tdwg/bdq#43](https://github.com/tdwg/bdq/issues/43)).

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
  
  The tests that use the TGN (VALIDATION_COUNTRY_FOUND, VALIDATION_STATEPROVINCE_FOUND, VALIDATION_COUNTRY_COUNTRYCODE_CONSISTENT, 
  VALIDATION_COUNTRYSTATEPROVINCE_CONSISTENT, and VALIDATION_COUNTRYSTATEPROVINCE_UNAMBIGUOUS) report these failures as 
  EXTERNAL_PREREQUISITES_NOT_MET, with the reason in the result comments.
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

# Building

    mvn package

# Developer deployment: 

To deploy a snapshot to the snapshotRepository: 

    mvn clean deploy

To deploy a new release to maven central, set the version in pom.xml to a non-snapshot version, then deploy with the release profile (which adds package signing and deployment to release staging:

    mvn clean deploy -P release

After this, you will need to login to the sonatype oss repository hosting nexus instance, find the staged release in the staging repositories, and perform the release.  It should be possible (haven't verified this yet) to perform the release from the command line instead by running: 

    mvn nexus-staging:release -P release

## Related BDQ libraries and applications

This library is one of a set of libraries and applications for the draft TDWG [BDQ Standard](https://bdq.tdwg.org/) (Biodiversity Data Quality), about to go into public review ([review draft](https://bdq.tdwg.org/draft/)). None of them are part of the standard.

| Repository | Kind | Role | Available from |
|---|---|---|---|
| [ffdq-api](https://github.com/kurator-org/ffdq-api) | Library | Java annotations and result API for Test implementations | [Maven Central](https://central.sonatype.com/artifact/org.datakurator/ffdq-api), [latest release](https://github.com/kurator-org/ffdq-api/releases/latest) |
| [kurator-ffdq](https://github.com/kurator-org/kurator-ffdq) | Library | bdqffdq model, RDF reports, Test RDF generation and Test runner | [Maven Central](https://central.sonatype.com/artifact/org.datakurator/kurator-ffdq), [latest release](https://github.com/kurator-org/kurator-ffdq/releases/latest) |
| [sci_name_qc](https://github.com/FilteredPush/sci_name_qc) | Test implementations | NAME Tests | [Maven Central](https://central.sonatype.com/artifact/org.filteredpush/sci_name_qc), [latest release](https://github.com/FilteredPush/sci_name_qc/releases/latest) |
| [event_date_qc](https://github.com/FilteredPush/event_date_qc) | Test implementations | TIME Tests | [Maven Central](https://central.sonatype.com/artifact/org.filteredpush/event_date_qc), [latest release](https://github.com/FilteredPush/event_date_qc/releases/latest) |
| [rec_occur_qc](https://github.com/FilteredPush/rec_occur_qc) | Test implementations | OTHER (metadata) Tests and SingleRecord Measures | [Maven Central](https://central.sonatype.com/artifact/org.filteredpush/rec_occur_qc), [latest release](https://github.com/FilteredPush/rec_occur_qc/releases/latest) |
| [geo_ref_qc](https://github.com/FilteredPush/geo_ref_qc) (this repository) | Test implementations | SPACE Tests | [Maven Central](https://central.sonatype.com/artifact/org.filteredpush/geo_ref_qc), [latest release](https://github.com/FilteredPush/geo_ref_qc/releases/latest) |
| [bdqtestrunner](https://github.com/FilteredPush/bdqtestrunner) | Application | Checks Test implementations against the BDQ Test validation data | [latest release](https://github.com/FilteredPush/bdqtestrunner/releases/latest) |
| [bdq_workbench](https://github.com/FilteredPush/bdq_workbench) | Application | Runs BDQ Tests over Darwin Core data for a Use Case | [latest release](https://github.com/FilteredPush/bdq_workbench/releases/latest) |
| [bdq_usecase_builder](https://github.com/FilteredPush/bdq_usecase_builder) | Application | Authors Use Cases and Test definitions | [latest release](https://github.com/FilteredPush/bdq_usecase_builder/releases/latest) |
| [bdq_issue_to_csv](https://github.com/kurator-org/bdq_issue_to_csv) | Application | Converts tdwg/bdq Test issues to a term-version CSV | [latest release](https://github.com/kurator-org/bdq_issue_to_csv/releases/latest) |
