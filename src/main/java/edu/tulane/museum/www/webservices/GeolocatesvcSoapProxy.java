package edu.tulane.museum.www.webservices;

public class GeolocatesvcSoapProxy implements edu.tulane.museum.www.webservices.GeolocatesvcSoap {
  private String _endpoint = null;
  private edu.tulane.museum.www.webservices.GeolocatesvcSoap geolocatesvcSoap = null;
  
  public GeolocatesvcSoapProxy() {
    _initGeolocatesvcSoapProxy();
  }
  
  public GeolocatesvcSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initGeolocatesvcSoapProxy();
  }
  
  private void _initGeolocatesvcSoapProxy() {
    try {
      geolocatesvcSoap = (new edu.tulane.museum.www.webservices.GeolocatesvcLocator()).getgeolocatesvcSoap();
      if (geolocatesvcSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)geolocatesvcSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)geolocatesvcSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (geolocatesvcSoap != null)
      ((javax.xml.rpc.Stub)geolocatesvcSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public edu.tulane.museum.www.webservices.GeolocatesvcSoap getGeolocatesvcSoap() {
    if (geolocatesvcSoap == null)
      _initGeolocatesvcSoapProxy();
    return geolocatesvcSoap;
  }
  
  public java.lang.String[] findWaterBodiesWithinLocality(edu.tulane.museum.www.webservices.LocalityDescription localityDescription) throws java.rmi.RemoteException{
    if (geolocatesvcSoap == null)
      _initGeolocatesvcSoapProxy();
    return geolocatesvcSoap.findWaterBodiesWithinLocality(localityDescription);
  }
  
  public edu.tulane.museum.www.webservices.Georef_Result_Set georef(edu.tulane.museum.www.webservices.LocalityDescription localityDescription, boolean hwyX, boolean findWaterbody, boolean restrictToLowestAdm, boolean doUncert, boolean doPoly, boolean displacePoly, boolean polyAsLinkID, int languageKey) throws java.rmi.RemoteException{
    if (geolocatesvcSoap == null)
      _initGeolocatesvcSoapProxy();
    return geolocatesvcSoap.georef(localityDescription, hwyX, findWaterbody, restrictToLowestAdm, doUncert, doPoly, displacePoly, polyAsLinkID, languageKey);
  }
  
  public edu.tulane.museum.www.webservices.Georef_Result_Set georef2(java.lang.String country, java.lang.String state, java.lang.String county, java.lang.String localityString, boolean hwyX, boolean findWaterbody, boolean restrictToLowestAdm, boolean doUncert, boolean doPoly, boolean displacePoly, boolean polyAsLinkID, int languageKey) throws java.rmi.RemoteException{
    if (geolocatesvcSoap == null)
      _initGeolocatesvcSoapProxy();
    return geolocatesvcSoap.georef2(country, state, county, localityString, hwyX, findWaterbody, restrictToLowestAdm, doUncert, doPoly, displacePoly, polyAsLinkID, languageKey);
  }
  
  public edu.tulane.museum.www.webservices.Georef_Result_Set georef3(java.lang.String vLocality, java.lang.String vGeography, boolean hwyX, boolean findWaterbody, boolean restrictToLowestAdm, boolean doUncert, boolean doPoly, boolean displacePoly, boolean polyAsLinkID, int languageKey) throws java.rmi.RemoteException{
    if (geolocatesvcSoap == null)
      _initGeolocatesvcSoapProxy();
    return geolocatesvcSoap.georef3(vLocality, vGeography, hwyX, findWaterbody, restrictToLowestAdm, doUncert, doPoly, displacePoly, polyAsLinkID, languageKey);
  }
  
  public void snapPointToNearestFoundWaterBody(edu.tulane.museum.www.webservices.LocalityDescription localityDescription, edu.tulane.museum.www.webservices.holders.GeographicPointHolder WGS84Coordinate) throws java.rmi.RemoteException{
    if (geolocatesvcSoap == null)
      _initGeolocatesvcSoapProxy();
    geolocatesvcSoap.snapPointToNearestFoundWaterBody(localityDescription, WGS84Coordinate);
  }
  
  public edu.tulane.museum.www.webservices.GeographicPoint snapPointToNearestFoundWaterBody2(java.lang.String country, java.lang.String state, java.lang.String county, java.lang.String localityString, double WGS84Latitude, double WGS84Longitude) throws java.rmi.RemoteException{
    if (geolocatesvcSoap == null)
      _initGeolocatesvcSoapProxy();
    return geolocatesvcSoap.snapPointToNearestFoundWaterBody2(country, state, county, localityString, WGS84Latitude, WGS84Longitude);
  }
  
  public java.lang.String calcUncertaintyPoly(java.lang.String polyGenerationKey) throws java.rmi.RemoteException{
    if (geolocatesvcSoap == null)
      _initGeolocatesvcSoapProxy();
    return geolocatesvcSoap.calcUncertaintyPoly(polyGenerationKey);
  }
  
  public edu.tulane.museum.www.webservices.Georef_Result_Set georef2PlusBG(java.lang.String country, java.lang.String state, java.lang.String county, java.lang.String localityString, boolean hwyX, boolean findWaterbody, boolean restrictToLowestAdm, boolean doUncert, boolean doPoly, boolean displacePoly, boolean polyAsLinkID, int languageKey) throws java.rmi.RemoteException{
    if (geolocatesvcSoap == null)
      _initGeolocatesvcSoapProxy();
    return geolocatesvcSoap.georef2PlusBG(country, state, county, localityString, hwyX, findWaterbody, restrictToLowestAdm, doUncert, doPoly, displacePoly, polyAsLinkID, languageKey);
  }
  
  
}