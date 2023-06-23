package org.filteredpush.qc.georeference.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.NavigableSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import com.google.common.collect.TreeMultimap;

public class CountryLookup {

    private static final Log logger = LogFactory.getLog(CountryLookup.class);

	private static TreeMultimap<String,String> countries;
	private static CountryLookup cl; 

	protected class DataHubCountryCode { 
		// data structure for reading https://datahub.io/core/country-list json data
		// License: https://opendatacommons.org/licenses/pddl/
			
		private String name;
		private String code;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		
	}
	
	// TODO data structure for:
	// https://datahub.io/core/country-codes
	// https://datahub.io/core/country-codes/r/country-codes.json
	// License: https://opendatacommons.org/licenses/pddl/
	

	/*
	Field Name 	Order 	Type (Format) 	Description
	FIFA 	1 	string 	Codes assigned by the Fédération Internationale de Football Association
	Dial 	2 	string 	Country code from ITU-T recommendation E.164, sometimes followed by area code
	ISO3166-1-Alpha-3 	3 	string 	Alpha-3 codes from ISO 3166-1 (synonymous with World Bank Codes)
	MARC 	4 	string 	MAchine-Readable Cataloging codes from the Library of Congress
	is_independent 	5 	string 	Country status, based on the CIA World Factbook
	ISO3166-1-numeric 	6 	string 	Numeric codes from ISO 3166-1
	GAUL 	7 	string 	Global Administrative Unit Layers from the Food and Agriculture Organization
	FIPS 	8 	string 	Codes from the U.S. standard FIPS PUB 10-4
	WMO 	9 	string 	Country abbreviations by the World Meteorological Organization
	ISO3166-1-Alpha-2 	10 	string 	Alpha-2 codes from ISO 3166-1
	ITU 	11 	string 	Codes assigned by the International Telecommunications Union
	IOC 	12 	string 	Codes assigned by the International Olympics Committee
	DS 	13 	string 	Distinguishing signs of vehicles in international traffic
	UNTERM Spanish Formal 	14 	string 	Country's formal Spanish name from UN Protocol and Liaison Service
	Global Code 	15 	string 	Country classification from United Nations Statistics Division
	Intermediate Region Code 	16 	string 	Country classification from United Nations Statistics Division
	official_name_fr 	17 	string 	Country or Area official French short name from UN Statistics Divsion
	UNTERM French Short 	18 	string 	Country's short French name from UN Protocol and Liaison Service
	ISO4217-currency_name 	19 	string 	ISO 4217 currency name
	Developed / Developing Countries 	20 	string 	Country classification from United Nations Statistics Division
	UNTERM Russian Formal 	21 	string 	Country's formal Russian name from UN Protocol and Liaison Service
	UNTERM English Short 	22 	string 	Country's short English name from UN Protocol and Liaison Service
	ISO4217-currency_alphabetic_code 	23 	string 	ISO 4217 currency alphabetic code
	Small Island Developing States (SIDS) 	24 	string 	Country classification from United Nations Statistics Division
	UNTERM Spanish Short 	25 	string 	Country's short Spanish name from UN Protocol and Liaison Service
	ISO4217-currency_numeric_code 	26 	string 	ISO 4217 currency numeric code
	UNTERM Chinese Formal 	27 	string 	Country's formal Chinese name from UN Protocol and Liaison Service
	UNTERM French Formal 	28 	string 	Country's formal French name from UN Protocol and Liaison Service
	UNTERM Russian Short 	29 	string 	Country's short Russian name from UN Protocol and Liaison Service
	M49 	30 	number 	UN Statistics M49 numeric codes (nearly synonymous with ISO 3166-1 numeric codes, which are based on UN M49. ISO 3166-1 does not include Channel Islands or Sark, for example)
	Sub-region Code 	31 	string 	Country classification from United Nations Statistics Division
	Region Code 	32 	string 	Country classification from United Nations Statistics Division
	official_name_ar 	33 	string 	Country or Area official Arabic short name from UN Statistics Divsion
	ISO4217-currency_minor_unit 	34 	string 	ISO 4217 currency number of minor units
	UNTERM Arabic Formal 	35 	string 	Country's formal Arabic name from UN Protocol and Liaison Service
	UNTERM Chinese Short 	36 	string 	Country's short Chinese name from UN Protocol and Liaison Service
	Land Locked Developing Countries (LLDC) 	37 	string 	Country classification from United Nations Statistics Division
	Intermediate Region Name 	38 	string 	Country classification from United Nations Statistics Division
	official_name_es 	39 	string 	Country or Area official Spanish short name from UN Statistics Divsion
	UNTERM English Formal 	40 	string 	Country's formal English name from UN Protocol and Liaison Service
	official_name_cn 	41 	string 	Country or Area official Chinese short name from UN Statistics Divsion
	official_name_en 	42 	string 	Country or Area official English short name from UN Statistics Divsion
	ISO4217-currency_country_name 	43 	string 	ISO 4217 country name
	Least Developed Countries (LDC) 	44 	string 	Country classification from United Nations Statistics Division
	Region Name 	45 	string 	Country classification from United Nations Statistics Division
	UNTERM Arabic Short 	46 	string 	Country's short Arabic name from UN Protocol and Liaison Service
	Sub-region Name 	47 	string 	Country classification from United Nations Statistics Division
	official_name_ru 	48 	string 	Country or Area official Russian short name from UN Statistics Divsion
	Global Name 	49 	string 	Country classification from United Nations Statistics Division
	Capital 	50 	string 	Capital city from Geonames
	Continent 	51 	string 	Continent from Geonames
	TLD 	52 	string 	Top level domain from Geonames
	Languages 	53 	string 	Languages from Geonames
	Geoname ID 	54 	number 	Geoname ID
	CLDR display name 	55 	string 	Country's customary English short name (CLDR)
	EDGAR 	56 	string 	EDGAR country code from SEC
	*/
	
	private CountryLookup() {
		// Country names along with two letter, three letter, and numeric codes from datahub, or fallback hardcoded list.
		ArrayListMultimap<String, String> multimap = ArrayListMultimap.create();
		
//		ObjectMapper mapper = new ObjectMapper();
//		
//		try {
//			URL datahubcountryurl = new URL("https://datahub.io/core/country-list/r/data.json");
//			BufferedReader is =  new BufferedReader(new InputStreamReader(datahubcountryurl.openStream()));
//			ObjectReader reader = mapper.readerForArrayOf(DataHubCountryCode.class);
//			
//			MappingIterator<DataHubCountryCode> values = reader.readValues(is);
//			
//			while (values.hasNext()) { 
//				DataHubCountryCode value = values.next();
//				multimap.putAll(value.getName(), ImmutableSet.of(value.getCode()));
//			}
//		} catch (MalformedURLException e) {
//			logger.error(e.getMessage(), e);
//		} catch (IOException e) {
//			logger.error(e.getMessage(), e);
//		}
		
		// TODO: Separate out short list of country name/country code from 
		// country matching against name variants and country code variants for
		// lookup of official country name from code purposes from 
		// other purposes (including lookup of name used in natural earth
		// GIS data set, and separate out various lookup functions for different purposes.
		
		// TODO: Manage caching of lookup and fallbacks.
		
		if (multimap.size() == 0) { 
		
		// Create a multivalue map with the country names as keys and the country codes as values.
		multimap.putAll("Andorra", ImmutableSet.of("AD","AND","20"));
		multimap.putAll("United Arab Emirates", ImmutableSet.of("AE","ARE","784"));
		multimap.putAll("Afghanistan", ImmutableSet.of("AF","AFG","4"));
		multimap.putAll("Antigua and Barbuda", ImmutableSet.of("AG","ATG","28"));
		multimap.putAll("Anguilla", ImmutableSet.of("AI","AIA","660"));
		multimap.putAll("Albania", ImmutableSet.of("AL","ALB","8"));
		multimap.putAll("Armenia", ImmutableSet.of("AM","ARM","51"));
		multimap.putAll("Angola", ImmutableSet.of("AO","AGO","24"));
		multimap.putAll("Antarctica", ImmutableSet.of("AQ","ATA","10"));
		multimap.putAll("Argentina", ImmutableSet.of("AR","ARG","32"));
		multimap.putAll("American Samoa", ImmutableSet.of("AS","ASM","16"));
		multimap.putAll("Austria", ImmutableSet.of("AT","AUT","40"));
		multimap.putAll("Australia", ImmutableSet.of("AU","AUS","36"));
		multimap.putAll("Aruba", ImmutableSet.of("AW","ABW","533"));
		multimap.putAll("Aland", ImmutableSet.of("AX","ALA","248"));
		multimap.putAll("Azerbaijan", ImmutableSet.of("AZ","AZE","31"));
		multimap.putAll("Bosnia and Herzegovina", ImmutableSet.of("BA","BIH","70"));
		multimap.putAll("Barbados", ImmutableSet.of("BB","BRB","52"));
		multimap.putAll("Bangladesh", ImmutableSet.of("BD","BGD","50"));
		multimap.putAll("Belgium", ImmutableSet.of("BE","BEL","56"));
		multimap.putAll("Burkina Faso", ImmutableSet.of("BF","BFA","854"));
		multimap.putAll("Bulgaria", ImmutableSet.of("BG","BGR","100"));
		multimap.putAll("Bahrain", ImmutableSet.of("BH","BHR","48"));
		multimap.putAll("Burundi", ImmutableSet.of("BI","BDI","108"));
		multimap.putAll("Benin", ImmutableSet.of("BJ","BEN","204"));
		multimap.putAll("Saint Barthelemy", ImmutableSet.of("BL","BLM","652"));
		multimap.putAll("Bermuda", ImmutableSet.of("BM","BMU","60"));
		multimap.putAll("Brunei", ImmutableSet.of("BN","BRN","96"));
		multimap.putAll("Bolivia", ImmutableSet.of("BO","BOL","68"));
		multimap.putAll("Brazil", ImmutableSet.of("BR","BRA","76"));
		multimap.putAll("The Bahamas", ImmutableSet.of("BS","BHS","44"));
		multimap.putAll("Bhutan", ImmutableSet.of("BT","BTN","64"));
		multimap.putAll("Botswana", ImmutableSet.of("BW","BWA","72"));
		multimap.putAll("Belarus", ImmutableSet.of("BY","BLR","112"));
		multimap.putAll("Belize", ImmutableSet.of("BZ","BLZ","84"));
		multimap.putAll("Canada", ImmutableSet.of("CA","CAN","124"));
		multimap.putAll("Democratic Republic of the Congo", ImmutableSet.of("CD","COD","180"));
		multimap.putAll("Central African Republic", ImmutableSet.of("CF","CAF","140"));
		multimap.putAll("Republic of Congo", ImmutableSet.of("CG","COG","178"));
		multimap.putAll("Switzerland", ImmutableSet.of("CH","CHE","756"));
		multimap.putAll("Ivory Coast", ImmutableSet.of("CI","CIV","384"));
		multimap.putAll("Cook Islands", ImmutableSet.of("CK","COK","184"));
		multimap.putAll("Chile", ImmutableSet.of("CL","CHL","152"));
		multimap.putAll("Cameroon", ImmutableSet.of("CM","CMR","120"));
		multimap.putAll("China", ImmutableSet.of("CN","CHN","156"));
		multimap.putAll("Colombia", ImmutableSet.of("CO","COL","170"));
		multimap.putAll("Costa Rica", ImmutableSet.of("CR","CRI","188"));
		multimap.putAll("Cuba", ImmutableSet.of("CU","CUB","192"));
		multimap.putAll("Cape Verde", ImmutableSet.of("CV","CPV","132"));
		multimap.putAll("Curaçao", ImmutableSet.of("CW","CUW","531"));
		multimap.putAll("Cyprus", ImmutableSet.of("CY","CYP","196"));
		multimap.putAll("Czech Republic", ImmutableSet.of("CZ","CZE","203"));
		multimap.putAll("Germany", ImmutableSet.of("DE","DEU","276"));
		multimap.putAll("Djibouti", ImmutableSet.of("DJ","DJI","262"));
		multimap.putAll("Denmark", ImmutableSet.of("DK","DNK","208"));
		multimap.putAll("Dominica", ImmutableSet.of("DM","DMA","212"));
		multimap.putAll("Dominican Republic", ImmutableSet.of("DO","DOM","214"));
		multimap.putAll("Algeria", ImmutableSet.of("DZ","DZA","12"));
		multimap.putAll("Ecuador", ImmutableSet.of("EC","ECU","218"));
		multimap.putAll("Estonia", ImmutableSet.of("EE","EST","233"));
		multimap.putAll("Egypt", ImmutableSet.of("EG","EGY","818"));
		multimap.putAll("Western Sahara", ImmutableSet.of("EH","ESH","732"));
		multimap.putAll("Eritrea", ImmutableSet.of("ER","ERI","232"));
		multimap.putAll("Spain", ImmutableSet.of("ES","ESP","724"));
		multimap.putAll("Ethiopia", ImmutableSet.of("ET","ETH","231"));
		multimap.putAll("Finland", ImmutableSet.of("FI","FIN","246"));
		multimap.putAll("Fiji", ImmutableSet.of("FJ","FJI","242"));
		multimap.putAll("Falkland Islands", ImmutableSet.of("FK","FLK","238"));
		multimap.putAll("Federated States of Micronesia", ImmutableSet.of("FM","FSM","583"));
		multimap.putAll("Faroe Islands", ImmutableSet.of("FO","FRO","234"));
		multimap.putAll("Gabon", ImmutableSet.of("GA","GAB","266"));
		multimap.putAll("United Kingdom", ImmutableSet.of("GB","GBR","826"));
		multimap.putAll("Grenada", ImmutableSet.of("GD","GRD","308"));
		multimap.putAll("Georgia", ImmutableSet.of("GE","GEO","268"));
		multimap.putAll("Guernsey", ImmutableSet.of("GG","GGY","831"));
		multimap.putAll("Ghana", ImmutableSet.of("GH","GHA","288"));
		multimap.putAll("Gibraltar", ImmutableSet.of("GI","GIB","292"));
		multimap.putAll("Greenland", ImmutableSet.of("GL","GRL","304"));
		multimap.putAll("Gambia", ImmutableSet.of("GM","GMB","270"));
		multimap.putAll("Guinea", ImmutableSet.of("GN","GIN","324"));
		multimap.putAll("Equatorial Guinea", ImmutableSet.of("GQ","GNQ","226"));
		multimap.putAll("Greece", ImmutableSet.of("GR","GRC","300"));
		multimap.putAll("South Georgia and South Sandwich Islands", ImmutableSet.of("GS","SGS","239"));
		multimap.putAll("Guatemala", ImmutableSet.of("GT","GTM","320"));
		multimap.putAll("Guam", ImmutableSet.of("GU","GUM","316"));
		multimap.putAll("Guinea Bissau", ImmutableSet.of("GW","GNB","624"));
		multimap.putAll("Guyana", ImmutableSet.of("GY","GUY","328"));
		multimap.putAll("Hong Kong S.A.R.", ImmutableSet.of("HK","HKG","344"));
		multimap.putAll("Heard Island and McDonald Islands", ImmutableSet.of("HM","HMD","334"));
		multimap.putAll("Honduras", ImmutableSet.of("HN","HND","340"));
		multimap.putAll("Croatia", ImmutableSet.of("HR","HRV","191"));
		multimap.putAll("Haiti", ImmutableSet.of("HT","HTI","332"));
		multimap.putAll("Hungary", ImmutableSet.of("HU","HUN","348"));
		multimap.putAll("Indonesia", ImmutableSet.of("ID","IDN","360"));
		multimap.putAll("Ireland", ImmutableSet.of("IE","IRL","372"));
		multimap.putAll("Israel", ImmutableSet.of("IL","ISR","376"));
		multimap.putAll("Isle of Man", ImmutableSet.of("IM","IMN","833"));
		multimap.putAll("India", ImmutableSet.of("IN","IND","356"));
		multimap.putAll("British Indian Ocean Territory", ImmutableSet.of("IO","IOT","86"));
		multimap.putAll("Iraq", ImmutableSet.of("IQ","IRQ","368"));
		multimap.putAll("Iran", ImmutableSet.of("IR","IRN","364"));
		multimap.putAll("Iceland", ImmutableSet.of("IS","ISL","352"));
		multimap.putAll("Italy", ImmutableSet.of("IT","ITA","380"));
		multimap.putAll("Jersey", ImmutableSet.of("JE","JEY","832"));
		multimap.putAll("Jamaica", ImmutableSet.of("JM","JAM","388"));
		multimap.putAll("Jordan", ImmutableSet.of("JO","JOR","400"));
		multimap.putAll("Japan", ImmutableSet.of("JP","JPN","392"));
		multimap.putAll("Kenya", ImmutableSet.of("KE","KEN","404"));
		multimap.putAll("Kyrgyzstan", ImmutableSet.of("KG","KGZ","417"));
		multimap.putAll("Cambodia", ImmutableSet.of("KH","KHM","116"));
		multimap.putAll("Kiribati", ImmutableSet.of("KI","KIR","296"));
		multimap.putAll("Comoros", ImmutableSet.of("KM","COM","174"));
		multimap.putAll("Saint Kitts and Nevis", ImmutableSet.of("KN","KNA","659"));
		multimap.putAll("North Korea", ImmutableSet.of("KP","PRK","408"));
		multimap.putAll("South Korea", ImmutableSet.of("KR","KOR","410"));
		multimap.putAll("Kuwait", ImmutableSet.of("KW","KWT","414"));
		multimap.putAll("Cayman Islands", ImmutableSet.of("KY","CYM","136"));
		multimap.putAll("Kazakhstan", ImmutableSet.of("KZ","KAZ","398"));
		multimap.putAll("Laos", ImmutableSet.of("LA","LAO","418"));
		multimap.putAll("Lebanon", ImmutableSet.of("LB","LBN","422"));
		multimap.putAll("Saint Lucia", ImmutableSet.of("LC","LCA","662"));
		multimap.putAll("Liechtenstein", ImmutableSet.of("LI","LIE","438"));
		multimap.putAll("Sri Lanka", ImmutableSet.of("LK","LKA","144"));
		multimap.putAll("Liberia", ImmutableSet.of("LR","LBR","430"));
		multimap.putAll("Lesotho", ImmutableSet.of("LS","LSO","426"));
		multimap.putAll("Lithuania", ImmutableSet.of("LT","LTU","440"));
		multimap.putAll("Luxembourg", ImmutableSet.of("LU","LUX","442"));
		multimap.putAll("Latvia", ImmutableSet.of("LV","LVA","428"));
		multimap.putAll("Libya", ImmutableSet.of("LY","LBY","434"));
		multimap.putAll("Morocco", ImmutableSet.of("MA","MAR","504"));
		multimap.putAll("Monaco", ImmutableSet.of("MC","MCO","492"));
		multimap.putAll("Moldova", ImmutableSet.of("MD","MDA","498"));
		multimap.putAll("Montenegro", ImmutableSet.of("ME","MNE","499"));
		multimap.putAll("Saint Martin", ImmutableSet.of("MF","MAF","663"));
		multimap.putAll("Madagascar", ImmutableSet.of("MG","MDG","450"));
		multimap.putAll("Marshall Islands", ImmutableSet.of("MH","MHL","584"));
		multimap.putAll("Macedonia", ImmutableSet.of("MK","MKD","807"));
		multimap.putAll("Mali", ImmutableSet.of("ML","MLI","466"));
		multimap.putAll("Myanmar", ImmutableSet.of("MM","MMR","104"));
		multimap.putAll("Mongolia", ImmutableSet.of("MN","MNG","496"));
		multimap.putAll("Macao S.A.R", ImmutableSet.of("MO","MAC","446"));
		multimap.putAll("Northern Mariana Islands", ImmutableSet.of("MP","MNP","580"));
		multimap.putAll("Mauritania", ImmutableSet.of("MR","MRT","478"));
		multimap.putAll("Montserrat", ImmutableSet.of("MS","MSR","500"));
		multimap.putAll("Malta", ImmutableSet.of("MT","MLT","470"));
		multimap.putAll("Mauritius", ImmutableSet.of("MU","MUS","480"));
		multimap.putAll("Maldives", ImmutableSet.of("MV","MDV","462"));
		multimap.putAll("Malawi", ImmutableSet.of("MW","MWI","454"));
		multimap.putAll("Mexico", ImmutableSet.of("MX","MEX","484"));
		multimap.putAll("Malaysia", ImmutableSet.of("MY","MYS","458"));
		multimap.putAll("Mozambique", ImmutableSet.of("MZ","MOZ","508"));
		multimap.putAll("Namibia", ImmutableSet.of("NA","NAM","516"));
		multimap.putAll("New Caledonia", ImmutableSet.of("NC","NCL","540"));
		multimap.putAll("Niger", ImmutableSet.of("NE","NER","562"));
		multimap.putAll("Norfolk Island", ImmutableSet.of("NF","NFK","574"));
		multimap.putAll("Nigeria", ImmutableSet.of("NG","NGA","566"));
		multimap.putAll("Nicaragua", ImmutableSet.of("NI","NIC","558"));
		multimap.putAll("Netherlands", ImmutableSet.of("NL","NLD","528"));
		multimap.putAll("Nepal", ImmutableSet.of("NP","NPL","524"));
		multimap.putAll("Nauru", ImmutableSet.of("NR","NRU","520"));
		multimap.putAll("Niue", ImmutableSet.of("NU","NIU","570"));
		multimap.putAll("New Zealand", ImmutableSet.of("NZ","NZL","554"));
		multimap.putAll("Oman", ImmutableSet.of("OM","OMN","512"));
		multimap.putAll("Panama", ImmutableSet.of("PA","PAN","591"));
		multimap.putAll("Peru", ImmutableSet.of("PE","PER","604"));
		multimap.putAll("French Polynesia", ImmutableSet.of("PF","PYF","258"));
		multimap.putAll("Papua New Guinea", ImmutableSet.of("PG","PNG","598"));
		multimap.putAll("Philippines", ImmutableSet.of("PH","PHL","608"));
		multimap.putAll("Pakistan", ImmutableSet.of("PK","PAK","586"));
		multimap.putAll("Poland", ImmutableSet.of("PL","POL","616"));
		multimap.putAll("Saint Pierre and Miquelon", ImmutableSet.of("PM","SPM","666"));
		multimap.putAll("Pitcairn Islands", ImmutableSet.of("PN","PCN","612"));
		multimap.putAll("Puerto Rico", ImmutableSet.of("PR","PRI","630"));
		multimap.putAll("Palestine", ImmutableSet.of("PS","PSE","275"));
		multimap.putAll("Portugal", ImmutableSet.of("PT","PRT","620"));
		multimap.putAll("Palau", ImmutableSet.of("PW","PLW","585"));
		multimap.putAll("Paraguay", ImmutableSet.of("PY","PRY","600"));
		multimap.putAll("Qatar", ImmutableSet.of("QA","QAT","634"));
		multimap.putAll("Romania", ImmutableSet.of("RO","ROU","642"));
		multimap.putAll("Republic of Serbia", ImmutableSet.of("RS","SRB","688"));
		multimap.putAll("Russia", ImmutableSet.of("RU","RUS","643"));
		multimap.putAll("Rwanda", ImmutableSet.of("RW","RWA","646"));
		multimap.putAll("Saudi Arabia", ImmutableSet.of("SA","SAU","682"));
		multimap.putAll("Solomon Islands", ImmutableSet.of("SB","SLB","90"));
		multimap.putAll("Seychelles", ImmutableSet.of("SC","SYC","690"));
		multimap.putAll("Sudan", ImmutableSet.of("SD","SDN","729"));
		multimap.putAll("Sweden", ImmutableSet.of("SE","SWE","752"));
		multimap.putAll("Singapore", ImmutableSet.of("SG","SGP","702"));
		multimap.putAll("Saint Helena", ImmutableSet.of("SH","SHN","654"));
		multimap.putAll("Slovenia", ImmutableSet.of("SI","SVN","705"));
		multimap.putAll("Slovakia", ImmutableSet.of("SK","SVK","703"));
		multimap.putAll("Sierra Leone", ImmutableSet.of("SL","SLE","694"));
		multimap.putAll("San Marino", ImmutableSet.of("SM","SMR","674"));
		multimap.putAll("Senegal", ImmutableSet.of("SN","SEN","686"));
		multimap.putAll("Somalia", ImmutableSet.of("SO","SOM","706"));
		multimap.putAll("Suriname", ImmutableSet.of("SR","SUR","740"));
		multimap.putAll("South Sudan", ImmutableSet.of("SS","SSD","728"));
		multimap.putAll("Sao Tome and Principe", ImmutableSet.of("ST","STP","678"));
		multimap.putAll("El Salvador", ImmutableSet.of("SV","SLV","222"));
		multimap.putAll("Sint Maarten", ImmutableSet.of("SX","SXM","534"));
		multimap.putAll("Syria", ImmutableSet.of("SY","SYR","760"));
		multimap.putAll("Swaziland", ImmutableSet.of("SZ","SWZ","748"));
		multimap.putAll("Turks and Caicos Islands", ImmutableSet.of("TC","TCA","796"));
		multimap.putAll("Chad", ImmutableSet.of("TD","TCD","148"));
		multimap.putAll("French Southern and Antarctic Lands", ImmutableSet.of("TF","ATF","260"));
		multimap.putAll("Togo", ImmutableSet.of("TG","TGO","768"));
		multimap.putAll("Thailand", ImmutableSet.of("TH","THA","764"));
		multimap.putAll("Tajikistan", ImmutableSet.of("TJ","TJK","762"));
		multimap.putAll("East Timor", ImmutableSet.of("TL","TLS","626"));
		multimap.putAll("Turkmenistan", ImmutableSet.of("TM","TKM","795"));
		multimap.putAll("Tunisia", ImmutableSet.of("TN","TUN","788"));
		multimap.putAll("Tonga", ImmutableSet.of("TO","TON","776"));
		multimap.putAll("Turkey", ImmutableSet.of("TR","TUR","792"));
		multimap.putAll("Trinidad and Tobago", ImmutableSet.of("TT","TTO","780"));
		multimap.putAll("Tuvalu", ImmutableSet.of("TV","TUV","798"));
		multimap.putAll("Taiwan", ImmutableSet.of("TW","TWN","158"));
		multimap.putAll("United Republic of Tanzania", ImmutableSet.of("TZ","TZA","834"));
		multimap.putAll("Ukraine", ImmutableSet.of("UA","UKR","804"));
		multimap.putAll("Uganda", ImmutableSet.of("UG","UGA","800"));
		multimap.putAll("United States Minor Outlying Islands", ImmutableSet.of("UM","UMI","581"));
		multimap.putAll("United States of America", ImmutableSet.of("US","USA","840"));
		multimap.putAll("Uruguay", ImmutableSet.of("UY","URY","858"));
		multimap.putAll("Uzbekistan", ImmutableSet.of("UZ","UZB","860"));
		multimap.putAll("Vatican", ImmutableSet.of("VA","VAT","336"));
		multimap.putAll("Saint Vincent and the Grenadines", ImmutableSet.of("VC","VCT","670"));
		multimap.putAll("Venezuela", ImmutableSet.of("VE","VEN","862"));
		multimap.putAll("British Virgin Islands", ImmutableSet.of("VG","VGB","92"));
		multimap.putAll("United States Virgin Islands", ImmutableSet.of("VI","VIR","850"));
		multimap.putAll("Vietnam", ImmutableSet.of("VN","VNM","704"));
		multimap.putAll("Vanuatu", ImmutableSet.of("VU","VUT","548"));
		multimap.putAll("Wallis and Futuna", ImmutableSet.of("WF","WLF","876"));
		multimap.putAll("Samoa", ImmutableSet.of("WS","WSM","882"));
		multimap.putAll("Yemen", ImmutableSet.of("YE","YEM","887"));
		multimap.putAll("South Africa", ImmutableSet.of("ZA","ZAF","710"));
		multimap.putAll("Zambia", ImmutableSet.of("ZM","ZMB","894"));
		multimap.putAll("Zimbabwe", ImmutableSet.of("ZW","ZWE","716"));
		
		} 
		
		// Invert the map so that a match on any code (now multiple keys) returns the country name (value)
		countries = Multimaps.invertFrom(multimap, TreeMultimap.<String,String>create());
	}
	
	/**
	 * Given a string that might be a country code, return a matching country, if any 
	 * from the list of country names used in the Natural Earth 10m-admin-0-countries 
	 * dataset.  If more than one match exists, or no match exists, return null.
	 * 
	 * @param countryCode for which to look up the country name
	 * @return country name or null if no match was found
	 */
	public static String lookupCountryFromCode(String countryCode) {
		if (countries==null) { 
			cl = new CountryLookup();
		}
		String result = null;
		if (countryCode!=null) { 
			NavigableSet<String> co = countries.get(countryCode);
			if (co!=null && co.size()==1) { 
				result = co.first();
			}
		}
		return result;
	}
	
	public static boolean countryExistsHasCode(String countryName) { 
		boolean retval = false;
		if (countries==null) { 
			cl = new CountryLookup();
		}
		if (countries.containsValue(countryName)) { 
			retval = true;
		}
		return retval;
	}
	
	/**
	 * 
	 * @param countryName
	 * @return
	 */
	public static Boolean countryExistsGettyNation(String countryName) { 
		Boolean retval = false;
		
		GeoUtilSingleton.getInstance().isTgnNation(countryName);
		
		return retval;
	}
	
}
