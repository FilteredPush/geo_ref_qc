package org.filteredpush.qc.georeference.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import com.google.common.collect.TreeMultimap;

public class CountryLookup {

    private static final Log logger = LogFactory.getLog(CountryLookup.class);

	private static TreeMultimap<String,String> countries;
	private static ArrayListMultimap<String, String> codes;
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
		codes = ArrayListMultimap.create();
		
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
		
		if (codes.size() == 0) { 
		
		// Create a multivalue map with the country names as keys and the country codes as values.
		codes.putAll("Andorra", ImmutableSet.of("AD","AND","20"));
		codes.putAll("United Arab Emirates", ImmutableSet.of("AE","ARE","784"));
		codes.putAll("Afghanistan", ImmutableSet.of("AF","AFG","4"));
		codes.putAll("Antigua and Barbuda", ImmutableSet.of("AG","ATG","28"));
		codes.putAll("Anguilla", ImmutableSet.of("AI","AIA","660"));
		codes.putAll("Albania", ImmutableSet.of("AL","ALB","8"));
		codes.putAll("Armenia", ImmutableSet.of("AM","ARM","51"));
		codes.putAll("Angola", ImmutableSet.of("AO","AGO","24"));
		codes.putAll("Antarctica", ImmutableSet.of("AQ","ATA","10"));
		codes.putAll("Argentina", ImmutableSet.of("AR","ARG","32"));
		codes.putAll("American Samoa", ImmutableSet.of("AS","ASM","16"));
		codes.putAll("Austria", ImmutableSet.of("AT","AUT","40"));
		codes.putAll("Australia", ImmutableSet.of("AU","AUS","36"));
		codes.putAll("Aruba", ImmutableSet.of("AW","ABW","533"));
		codes.putAll("Aland", ImmutableSet.of("AX","ALA","248"));
		codes.putAll("Azerbaijan", ImmutableSet.of("AZ","AZE","31"));
		codes.putAll("Bosnia and Herzegovina", ImmutableSet.of("BA","BIH","70"));
		codes.putAll("Barbados", ImmutableSet.of("BB","BRB","52"));
		codes.putAll("Bangladesh", ImmutableSet.of("BD","BGD","50"));
		codes.putAll("Belgium", ImmutableSet.of("BE","BEL","56"));
		codes.putAll("Burkina Faso", ImmutableSet.of("BF","BFA","854"));
		codes.putAll("Bulgaria", ImmutableSet.of("BG","BGR","100"));
		codes.putAll("Bahrain", ImmutableSet.of("BH","BHR","48"));
		codes.putAll("Burundi", ImmutableSet.of("BI","BDI","108"));
		codes.putAll("Benin", ImmutableSet.of("BJ","BEN","204"));
		codes.putAll("Saint Barthelemy", ImmutableSet.of("BL","BLM","652"));
		codes.putAll("Bermuda", ImmutableSet.of("BM","BMU","60"));
		codes.putAll("Brunei", ImmutableSet.of("BN","BRN","96"));
		codes.putAll("Bolivia", ImmutableSet.of("BO","BOL","68"));
		codes.putAll("Brazil", ImmutableSet.of("BR","BRA","76"));
		codes.putAll("The Bahamas", ImmutableSet.of("BS","BHS","44"));
		codes.putAll("Bhutan", ImmutableSet.of("BT","BTN","64"));
		codes.putAll("Botswana", ImmutableSet.of("BW","BWA","72"));
		codes.putAll("Belarus", ImmutableSet.of("BY","BLR","112"));
		codes.putAll("Belize", ImmutableSet.of("BZ","BLZ","84"));
		codes.putAll("Canada", ImmutableSet.of("CA","CAN","124"));
		codes.putAll("Democratic Republic of the Congo", ImmutableSet.of("CD","COD","180"));
		codes.putAll("Central African Republic", ImmutableSet.of("CF","CAF","140"));
		codes.putAll("Republic of Congo", ImmutableSet.of("CG","COG","178"));
		codes.putAll("Switzerland", ImmutableSet.of("CH","CHE","756"));
		codes.putAll("Ivory Coast", ImmutableSet.of("CI","CIV","384"));
		codes.putAll("Cook Islands", ImmutableSet.of("CK","COK","184"));
		codes.putAll("Chile", ImmutableSet.of("CL","CHL","152"));
		codes.putAll("Cameroon", ImmutableSet.of("CM","CMR","120"));
		codes.putAll("China", ImmutableSet.of("CN","CHN","156"));
		codes.putAll("Colombia", ImmutableSet.of("CO","COL","170"));
		codes.putAll("Costa Rica", ImmutableSet.of("CR","CRI","188"));
		codes.putAll("Cuba", ImmutableSet.of("CU","CUB","192"));
		codes.putAll("Cape Verde", ImmutableSet.of("CV","CPV","132"));
		codes.putAll("Curaçao", ImmutableSet.of("CW","CUW","531"));
		codes.putAll("Cyprus", ImmutableSet.of("CY","CYP","196"));
		codes.putAll("Czech Republic", ImmutableSet.of("CZ","CZE","203"));
		codes.putAll("Germany", ImmutableSet.of("DE","DEU","276"));
		codes.putAll("Djibouti", ImmutableSet.of("DJ","DJI","262"));
		codes.putAll("Denmark", ImmutableSet.of("DK","DNK","208"));
		codes.putAll("Dominica", ImmutableSet.of("DM","DMA","212"));
		codes.putAll("Dominican Republic", ImmutableSet.of("DO","DOM","214"));
		codes.putAll("Algeria", ImmutableSet.of("DZ","DZA","12"));
		codes.putAll("Ecuador", ImmutableSet.of("EC","ECU","218"));
		codes.putAll("Estonia", ImmutableSet.of("EE","EST","233"));
		codes.putAll("Egypt", ImmutableSet.of("EG","EGY","818"));
		codes.putAll("Western Sahara", ImmutableSet.of("EH","ESH","732"));
		codes.putAll("Eritrea", ImmutableSet.of("ER","ERI","232"));
		codes.putAll("Spain", ImmutableSet.of("ES","ESP","724"));
		codes.putAll("Ethiopia", ImmutableSet.of("ET","ETH","231"));
		codes.putAll("Finland", ImmutableSet.of("FI","FIN","246"));
		codes.putAll("Fiji", ImmutableSet.of("FJ","FJI","242"));
		codes.putAll("Falkland Islands", ImmutableSet.of("FK","FLK","238"));
		codes.putAll("Federated States of Micronesia", ImmutableSet.of("FM","FSM","583"));
		codes.putAll("Faroe Islands", ImmutableSet.of("FO","FRO","234"));
		codes.putAll("Gabon", ImmutableSet.of("GA","GAB","266"));
		codes.putAll("United Kingdom", ImmutableSet.of("GB","GBR","826"));
		codes.putAll("Grenada", ImmutableSet.of("GD","GRD","308"));
		codes.putAll("Georgia", ImmutableSet.of("GE","GEO","268"));
		codes.putAll("Guernsey", ImmutableSet.of("GG","GGY","831"));
		codes.putAll("Ghana", ImmutableSet.of("GH","GHA","288"));
		codes.putAll("Gibraltar", ImmutableSet.of("GI","GIB","292"));
		codes.putAll("Greenland", ImmutableSet.of("GL","GRL","304"));
		codes.putAll("Gambia", ImmutableSet.of("GM","GMB","270"));
		codes.putAll("Guinea", ImmutableSet.of("GN","GIN","324"));
		codes.putAll("Equatorial Guinea", ImmutableSet.of("GQ","GNQ","226"));
		codes.putAll("Greece", ImmutableSet.of("GR","GRC","300"));
		codes.putAll("South Georgia and South Sandwich Islands", ImmutableSet.of("GS","SGS","239"));
		codes.putAll("Guatemala", ImmutableSet.of("GT","GTM","320"));
		codes.putAll("Guam", ImmutableSet.of("GU","GUM","316"));
		codes.putAll("Guinea Bissau", ImmutableSet.of("GW","GNB","624"));
		codes.putAll("Guyana", ImmutableSet.of("GY","GUY","328"));
		codes.putAll("Hong Kong S.A.R.", ImmutableSet.of("HK","HKG","344"));
		codes.putAll("Heard Island and McDonald Islands", ImmutableSet.of("HM","HMD","334"));
		codes.putAll("Honduras", ImmutableSet.of("HN","HND","340"));
		codes.putAll("Croatia", ImmutableSet.of("HR","HRV","191"));
		codes.putAll("Haiti", ImmutableSet.of("HT","HTI","332"));
		codes.putAll("Hungary", ImmutableSet.of("HU","HUN","348"));
		codes.putAll("Indonesia", ImmutableSet.of("ID","IDN","360"));
		codes.putAll("Ireland", ImmutableSet.of("IE","IRL","372"));
		codes.putAll("Israel", ImmutableSet.of("IL","ISR","376"));
		codes.putAll("Isle of Man", ImmutableSet.of("IM","IMN","833"));
		codes.putAll("India", ImmutableSet.of("IN","IND","356"));
		codes.putAll("British Indian Ocean Territory", ImmutableSet.of("IO","IOT","86"));
		codes.putAll("Iraq", ImmutableSet.of("IQ","IRQ","368"));
		codes.putAll("Iran", ImmutableSet.of("IR","IRN","364"));
		codes.putAll("Iceland", ImmutableSet.of("IS","ISL","352"));
		codes.putAll("Italy", ImmutableSet.of("IT","ITA","380"));
		codes.putAll("Jersey", ImmutableSet.of("JE","JEY","832"));
		codes.putAll("Jamaica", ImmutableSet.of("JM","JAM","388"));
		codes.putAll("Jordan", ImmutableSet.of("JO","JOR","400"));
		codes.putAll("Japan", ImmutableSet.of("JP","JPN","392"));
		codes.putAll("Kenya", ImmutableSet.of("KE","KEN","404"));
		codes.putAll("Kyrgyzstan", ImmutableSet.of("KG","KGZ","417"));
		codes.putAll("Cambodia", ImmutableSet.of("KH","KHM","116"));
		codes.putAll("Kiribati", ImmutableSet.of("KI","KIR","296"));
		codes.putAll("Comoros", ImmutableSet.of("KM","COM","174"));
		codes.putAll("Saint Kitts and Nevis", ImmutableSet.of("KN","KNA","659"));
		codes.putAll("North Korea", ImmutableSet.of("KP","PRK","408"));
		codes.putAll("South Korea", ImmutableSet.of("KR","KOR","410"));
		codes.putAll("Kuwait", ImmutableSet.of("KW","KWT","414"));
		codes.putAll("Cayman Islands", ImmutableSet.of("KY","CYM","136"));
		codes.putAll("Kazakhstan", ImmutableSet.of("KZ","KAZ","398"));
		codes.putAll("Laos", ImmutableSet.of("LA","LAO","418"));
		codes.putAll("Lebanon", ImmutableSet.of("LB","LBN","422"));
		codes.putAll("Saint Lucia", ImmutableSet.of("LC","LCA","662"));
		codes.putAll("Liechtenstein", ImmutableSet.of("LI","LIE","438"));
		codes.putAll("Sri Lanka", ImmutableSet.of("LK","LKA","144"));
		codes.putAll("Liberia", ImmutableSet.of("LR","LBR","430"));
		codes.putAll("Lesotho", ImmutableSet.of("LS","LSO","426"));
		codes.putAll("Lithuania", ImmutableSet.of("LT","LTU","440"));
		codes.putAll("Luxembourg", ImmutableSet.of("LU","LUX","442"));
		codes.putAll("Latvia", ImmutableSet.of("LV","LVA","428"));
		codes.putAll("Libya", ImmutableSet.of("LY","LBY","434"));
		codes.putAll("Morocco", ImmutableSet.of("MA","MAR","504"));
		codes.putAll("Monaco", ImmutableSet.of("MC","MCO","492"));
		codes.putAll("Moldova", ImmutableSet.of("MD","MDA","498"));
		codes.putAll("Montenegro", ImmutableSet.of("ME","MNE","499"));
		codes.putAll("Saint Martin", ImmutableSet.of("MF","MAF","663"));
		codes.putAll("Madagascar", ImmutableSet.of("MG","MDG","450"));
		codes.putAll("Marshall Islands", ImmutableSet.of("MH","MHL","584"));
		codes.putAll("Macedonia", ImmutableSet.of("MK","MKD","807"));
		codes.putAll("Mali", ImmutableSet.of("ML","MLI","466"));
		codes.putAll("Myanmar", ImmutableSet.of("MM","MMR","104"));
		codes.putAll("Mongolia", ImmutableSet.of("MN","MNG","496"));
		codes.putAll("Macao S.A.R", ImmutableSet.of("MO","MAC","446"));
		codes.putAll("Northern Mariana Islands", ImmutableSet.of("MP","MNP","580"));
		codes.putAll("Mauritania", ImmutableSet.of("MR","MRT","478"));
		codes.putAll("Montserrat", ImmutableSet.of("MS","MSR","500"));
		codes.putAll("Malta", ImmutableSet.of("MT","MLT","470"));
		codes.putAll("Mauritius", ImmutableSet.of("MU","MUS","480"));
		codes.putAll("Maldives", ImmutableSet.of("MV","MDV","462"));
		codes.putAll("Malawi", ImmutableSet.of("MW","MWI","454"));
		codes.putAll("Mexico", ImmutableSet.of("MX","MEX","484"));
		codes.putAll("Malaysia", ImmutableSet.of("MY","MYS","458"));
		codes.putAll("Mozambique", ImmutableSet.of("MZ","MOZ","508"));
		codes.putAll("Namibia", ImmutableSet.of("NA","NAM","516"));
		codes.putAll("New Caledonia", ImmutableSet.of("NC","NCL","540"));
		codes.putAll("Niger", ImmutableSet.of("NE","NER","562"));
		codes.putAll("Norfolk Island", ImmutableSet.of("NF","NFK","574"));
		codes.putAll("Nigeria", ImmutableSet.of("NG","NGA","566"));
		codes.putAll("Nicaragua", ImmutableSet.of("NI","NIC","558"));
		codes.putAll("Netherlands", ImmutableSet.of("NL","NLD","528"));
		codes.putAll("Nepal", ImmutableSet.of("NP","NPL","524"));
		codes.putAll("Nauru", ImmutableSet.of("NR","NRU","520"));
		codes.putAll("Niue", ImmutableSet.of("NU","NIU","570"));
		codes.putAll("New Zealand", ImmutableSet.of("NZ","NZL","554"));
		codes.putAll("Oman", ImmutableSet.of("OM","OMN","512"));
		codes.putAll("Panama", ImmutableSet.of("PA","PAN","591"));
		codes.putAll("Peru", ImmutableSet.of("PE","PER","604"));
		codes.putAll("French Polynesia", ImmutableSet.of("PF","PYF","258"));
		codes.putAll("Papua New Guinea", ImmutableSet.of("PG","PNG","598"));
		codes.putAll("Philippines", ImmutableSet.of("PH","PHL","608"));
		codes.putAll("Pakistan", ImmutableSet.of("PK","PAK","586"));
		codes.putAll("Poland", ImmutableSet.of("PL","POL","616"));
		codes.putAll("Saint Pierre and Miquelon", ImmutableSet.of("PM","SPM","666"));
		codes.putAll("Pitcairn Islands", ImmutableSet.of("PN","PCN","612"));
		codes.putAll("Puerto Rico", ImmutableSet.of("PR","PRI","630"));
		codes.putAll("Palestine", ImmutableSet.of("PS","PSE","275"));
		codes.putAll("Portugal", ImmutableSet.of("PT","PRT","620"));
		codes.putAll("Palau", ImmutableSet.of("PW","PLW","585"));
		codes.putAll("Paraguay", ImmutableSet.of("PY","PRY","600"));
		codes.putAll("Qatar", ImmutableSet.of("QA","QAT","634"));
		codes.putAll("Romania", ImmutableSet.of("RO","ROU","642"));
		codes.putAll("Republic of Serbia", ImmutableSet.of("RS","SRB","688"));
		codes.putAll("Russia", ImmutableSet.of("RU","RUS","643"));
		codes.putAll("Rwanda", ImmutableSet.of("RW","RWA","646"));
		codes.putAll("Saudi Arabia", ImmutableSet.of("SA","SAU","682"));
		codes.putAll("Solomon Islands", ImmutableSet.of("SB","SLB","90"));
		codes.putAll("Seychelles", ImmutableSet.of("SC","SYC","690"));
		codes.putAll("Sudan", ImmutableSet.of("SD","SDN","729"));
		codes.putAll("Sweden", ImmutableSet.of("SE","SWE","752"));
		codes.putAll("Singapore", ImmutableSet.of("SG","SGP","702"));
		codes.putAll("Saint Helena", ImmutableSet.of("SH","SHN","654"));
		codes.putAll("Slovenia", ImmutableSet.of("SI","SVN","705"));
		codes.putAll("Slovakia", ImmutableSet.of("SK","SVK","703"));
		codes.putAll("Sierra Leone", ImmutableSet.of("SL","SLE","694"));
		codes.putAll("San Marino", ImmutableSet.of("SM","SMR","674"));
		codes.putAll("Senegal", ImmutableSet.of("SN","SEN","686"));
		codes.putAll("Somalia", ImmutableSet.of("SO","SOM","706"));
		codes.putAll("Suriname", ImmutableSet.of("SR","SUR","740"));
		codes.putAll("South Sudan", ImmutableSet.of("SS","SSD","728"));
		codes.putAll("Sao Tome and Principe", ImmutableSet.of("ST","STP","678"));
		codes.putAll("El Salvador", ImmutableSet.of("SV","SLV","222"));
		codes.putAll("Sint Maarten", ImmutableSet.of("SX","SXM","534"));
		codes.putAll("Syria", ImmutableSet.of("SY","SYR","760"));
		codes.putAll("Swaziland", ImmutableSet.of("SZ","SWZ","748"));
		codes.putAll("Turks and Caicos Islands", ImmutableSet.of("TC","TCA","796"));
		codes.putAll("Chad", ImmutableSet.of("TD","TCD","148"));
		codes.putAll("French Southern and Antarctic Lands", ImmutableSet.of("TF","ATF","260"));
		codes.putAll("Togo", ImmutableSet.of("TG","TGO","768"));
		codes.putAll("Thailand", ImmutableSet.of("TH","THA","764"));
		codes.putAll("Tajikistan", ImmutableSet.of("TJ","TJK","762"));
		codes.putAll("East Timor", ImmutableSet.of("TL","TLS","626"));
		codes.putAll("Turkmenistan", ImmutableSet.of("TM","TKM","795"));
		codes.putAll("Tunisia", ImmutableSet.of("TN","TUN","788"));
		codes.putAll("Tonga", ImmutableSet.of("TO","TON","776"));
		codes.putAll("Turkey", ImmutableSet.of("TR","TUR","792"));
		codes.putAll("Trinidad and Tobago", ImmutableSet.of("TT","TTO","780"));
		codes.putAll("Tuvalu", ImmutableSet.of("TV","TUV","798"));
		codes.putAll("Taiwan", ImmutableSet.of("TW","TWN","158"));
		codes.putAll("United Republic of Tanzania", ImmutableSet.of("TZ","TZA","834"));
		codes.putAll("Ukraine", ImmutableSet.of("UA","UKR","804"));
		codes.putAll("Uganda", ImmutableSet.of("UG","UGA","800"));
		codes.putAll("United States Minor Outlying Islands", ImmutableSet.of("UM","UMI","581"));
		codes.putAll("United States of America", ImmutableSet.of("US","USA","840"));
		codes.putAll("Uruguay", ImmutableSet.of("UY","URY","858"));
		codes.putAll("Uzbekistan", ImmutableSet.of("UZ","UZB","860"));
		codes.putAll("Vatican", ImmutableSet.of("VA","VAT","336"));
		codes.putAll("Saint Vincent and the Grenadines", ImmutableSet.of("VC","VCT","670"));
		codes.putAll("Venezuela", ImmutableSet.of("VE","VEN","862"));
		codes.putAll("British Virgin Islands", ImmutableSet.of("VG","VGB","92"));
		codes.putAll("United States Virgin Islands", ImmutableSet.of("VI","VIR","850"));
		codes.putAll("Vietnam", ImmutableSet.of("VN","VNM","704"));
		codes.putAll("Vanuatu", ImmutableSet.of("VU","VUT","548"));
		codes.putAll("Wallis and Futuna", ImmutableSet.of("WF","WLF","876"));
		codes.putAll("Samoa", ImmutableSet.of("WS","WSM","882"));
		codes.putAll("Yemen", ImmutableSet.of("YE","YEM","887"));
		codes.putAll("South Africa", ImmutableSet.of("ZA","ZAF","710"));
		codes.putAll("Zambia", ImmutableSet.of("ZM","ZMB","894"));
		codes.putAll("Zimbabwe", ImmutableSet.of("ZW","ZWE","716"));
		
		} 
		
		// Invert the map so that a match on any code (now multiple keys) returns the country name (value)
		countries = Multimaps.invertFrom(codes, TreeMultimap.<String,String>create());
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
	
	/**
	 * Obtain the list of two letter country codes. 
	 * 
	 * @return a list of two letter country codes.
	 */
	public static List<String> getCountryCodes2() { 
		List<String> retval = new ArrayList<String>();
		
		if (countries==null) { 
			cl = new CountryLookup();
		}
		//Set<Entry<String,String>> codes = countries.entries();
		Iterator<Entry<String,String>> i =  codes.entries().iterator();
		while (i.hasNext()) { 
			Entry<String,String> entry = i.next();
			String value = entry.getValue();
			if (value.matches("^[A-Z][A-Z]$" )) { 
				retval.add(value);
			}
		}
		
		return retval;
	}
	
	/**
	 * Check if a provided countryCode is an exact match to a known 
	 * two letter country code.
	 * 
	 * @param countryCode to test
	 * @return true if an exact match, otherwise false.
	 */
	public static boolean codeTwoLetterMatched(String countryCode) { 
		boolean retval = false;
		if (countries==null) { 
			cl = new CountryLookup();
		}
		Collection<Entry<String,String>> codeset = codes.entries();
		Iterator<Entry<String,String>> i =  codeset.iterator();
		boolean done = false;
		while (i.hasNext()&&!done) { 
			Entry<String,String> entry = i.next();
			String value = entry.getValue();
			if (value.equals(countryCode)) { 
				retval = true;
				done = true;
			}
		}
		return retval;
	}
	
	/**
	 * Given a string that might be a country code or a country name return the 
	 * matching two letter country code if one can be found.
	 *  
	 * @param countryCodeOrName string for which to look up the country code
	 * @return a two letter country code or null if no match is found
	 */
	public static String lookupCode2FromCodeName(String countryCodeOrName) {
		if (countries==null) { 
			cl = new CountryLookup();
		}
		String result = null;
		if (countryCodeOrName!=null) { 
			String country = lookupCountryFromCode(countryCodeOrName);
			if (country!=null) {
				// provided string is a country code
				List<String> values = codes.get(country);
				Iterator<String> iv = values.iterator();
				boolean done = false;
				while (iv.hasNext()&&!done) {
					String v = iv.next();
					if (v.matches("^[A-Z][A-Z]$" )) { 
						result = v;
						done = true;
					}
				}
			} else if (countryCodeOrName.matches("^0[0-9]+")) { 
				country = lookupCountryFromCode(countryCodeOrName.replaceAll("^0+", ""));
				if (country!=null) { 
					// provided string is a numeric country code with leading zeroes
					List<String> values = codes.get(country);
					Iterator<String> iv = values.iterator();
					boolean done = false;
					while (iv.hasNext()&&!done) {
						String v = iv.next();
						if (v.matches("^[A-Z][A-Z]$" )) { 
							result = v;
							done = true;
						}
					}
				}
			} else if (codes.containsKey(countryCodeOrName)) { 
				// provided string is a country name
				List<String> values =  codes.get(countryCodeOrName);
				Iterator<String> iv = values.iterator();
				while (iv.hasNext()) {
					String v = iv.next();
					if (v.matches("^[A-Z][A-Z]$" )) { 
						result = v;
					}
				}
			}
		}
		return result;
	}
	
}
