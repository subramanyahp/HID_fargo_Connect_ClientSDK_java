/*
 * HID FARGO Connect Java SDK - Sample Application
 */
package examples;

import com.extensia.xcp.services.restapi.client.*;
import com.extensia.xcp.services.restapi.model.*;
import com.extensia.xcp.services.restapi.model.cardread.CardEdgeData;
import com.extensia.xcp.services.restapi.model.cardread.CardReadResults;
import com.extensia.xcp.services.restapi.model.parameters.ImageParameter;
import com.extensia.xcp.services.restapi.model.parameters.TextParameter;
import com.extensia.xcp.services.restapi.model.parameters.validators.ValidatorUtils;
import com.extensia.xcp.services.restapi.model.service.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.time.Duration;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;


/**
 * FARGO Connect Card Services Client SDK sample application
 */
public class SampleApplication {

  /**
   * Creates and configures a new SDK client instance
   */
  private static CardServicesClient configureClient() {

    CardServicesClientConfig clientConfig = new CardServicesClientConfig();

    /*
     * FARGO Connect server base URI (e.g. "https://my.server.com:18443"). A
     * default port number of 18443 is used if none is specified in the URI.
     */
    clientConfig.setServerBaseUri("https://test.api.hfc.hidglobal.com:18443");

    /*
     * Server Api key assigned to the client. The Api key is configured in the
     * FARGO Connect Portal.
     */
    clientConfig.setApiKey("528DFB0B54B2438A5F631E94754BA4DA088E0EA1CF9700EF4ACE0B4C6D49F957");

    /*
     * Client SSL certificate used for server authentication. The certificate
     * must be in PKCS#12 format and may be configured using a certificate file
     * or alternatively as a stream using the setAuthCertStream method.
     */
    clientConfig.setAuthCertFile("C:/HFCCertification/Schlumberger-Development-Certs/Java/Schlumberger_Card_Services_Client_API_Auth_Cert.p12");

    /*
     * Password for the client authentication certificate
     */
    clientConfig.setAuthCertPassword("xgWkY3uwSDX2JX1qyvi7");

    /*
     * Client data encryption certificate. The certificate must be in PKCS#12
     * format and may be configured using a certificate file or alternatively
     * as a stream using the setEncryptionCertStream method.
     */
    clientConfig.setEncryptionCertFile("C:/HFCCertification/Schlumberger-Development-Certs/Java/Schlumberger_Data_Encryption_Certificate.p12");

    /*
     * Password for the data encryption certificate
     */
    clientConfig.setEncryptionCertPassword("xgWkY3uwSDX2JX1qyvi7");

    /*
     * Client Certificate Authority (CA) trust chain certificate(s). The trust chain
     * certificates must be in PEM format and may be configured using a certificate
     * file or alternatively as a stream using the setCaCertStream method.
     */
    clientConfig.setCaCertFile("C:/HFCCertification/Schlumberger-Development-Certs/Java/Schlumberger_Card_Services_CA_Chain.pem");

    /*
     * Create the SDK client
     */
    return new CardServicesClient(clientConfig);
  }

  public static void main(String[] args) {

    try {
      /*
       * Create a FARGO Connect SDK client instance
       */
      CardServicesClient client = configureClient();

      /*
       * List the available organizations and select the first one for demo purposes.
       */
      Organization organization = selectOrganization(client);

      writeLine("Using Organization: " + organization.getName());
      writeLine();

      /*
       * Show information about the selected organization and devices
       */
      showOrganizationInfo(client, organization.getOrganizationId());

      /*
       * List the available production profiles for the selected organization and select
       * the first production profile for demo purposes.
       */
      ProductionProfile productionProfile = selectProductionProfile(client, organization.getOrganizationId());

      writeLine("Using production profile: " + productionProfile.getName());
      writeLine();

      /*
       * List the available print destinations for the selected organization and select
       * the first print destination for demo purposes.
       */
      PrintDestination printDestination = selectPrintDestination(client, organization.getOrganizationId());

      writeLine("Using print destination: " + printDestination.getDestination());
      writeLine();

      /*
       * Retrieve the configuration parameters for the production profile
       */
      ProductionProfileApi profileApi = client.getProductionProfileApi();
      ProductionProfileConfig profileConfig = profileApi.getProductionProfileParameters(productionProfile.getProfileId());

      /*
       * Process the list of profile configuration parameters. By default only a single
       * parameter named "CardType" of type ListParameter is required for card production
       * requests. The "CardType" parameter is essentially a configurable enumerated type that
       * maps a logical card type name (e.g. "Employee", "Student" etc) to a card template.
       */
      for (ProductionProfileParameter profileParam : profileConfig.getProfileParameters()) {

        /*
         * Fail if an unexpected parameter type is present
         */
        if (profileParam.getDataType() != DataType.List || !ProfileParamConst.CardType.equalsIgnoreCase(profileParam.getName())) {
          throw new CardServicesException("Unhandled production profile parameter type: " + profileParam.getDataType().name());
        }

        /*
         * Fail if no card types are available
         */
        if (profileParam.getOptions().isEmpty()) {
          throw new CardServicesException("Production profile has no card types: " + profileParam.getDataType().name());
        }

        /*
         * Show the available card types for debug purposes
         */
        writeLine("Available card types");
        profileParam.getOptions().forEach(cardType -> writeLine("  '%s'", cardType));
        writeLine();

        /*
         * Arbitrarily select the first card type for demo purposes. The supported card
         * types are normally known in advance and the Option property is used to validate
         * whether the desired card type is a valid option.
         */
        String selectedCardType = profileParam.getOptions().get(0);
        profileParam.setValue(selectedCardType);

        writeLine("Selecting CardType: " + selectedCardType);
        writeLine();
      }

      writeLine("Configuring the production request");
      writeLine();

      /*
       * Configure the production profile to obtain a production request template for
       * the previously selected "CardType" value.
       */
      ProductionRequestTemplate requestTemplate = profileApi.configureProductionProfile(profileConfig);

      /*
       * Iterate over the services in the production request template. By default,
       * only one card production service will be present in the request template.
       */
      writeLine("Configuring service parameters");

      for (Service service : requestTemplate.getServices()) {

        /*
         * Fail if an unexpected service type is present
         */
        if (service.getType() != ServiceType.CardRequest) {
          throw new CardServicesException("Unhandled production service type: " + service.getType().name());
        }

        /*
         * Process the card production request parameters
         */
        CardRequestService cardRequest = (CardRequestService) service;

        for (int i = 0; i < cardRequest.getParameters().size(); i++) {

          Parameter dataParameter = cardRequest.getParameters().get(i);

          /*
           * Cast the parameter to the correct class based on DataType. An error is
           * raised for parameter types other than Text and Image since those are the
           * only types configured for the demo.
           */
          switch (dataParameter.getDataType()) {

            case Text:
              /*
               * Set the Text parameter Value to the parameter's Name for demo purposes.
               * The text value would normally be supplied by the application.
               */
              TextParameter textParam = (TextParameter) dataParameter.getData();
              textParam.setValue(textParam.getName());
              writeLine("  Param[%s] (%s): {%s} -> {%s}", i, textParam.getDataType().name(), textParam.getName(), textParam.getValue());
              break;

            case Image:
              /*
               * Set the Image parameter Value to a test image for demo purposes. The image
               * can be provided as a stream as shown here, or by providing a file name
               * using the setFileName method. The image would typically be selected by the
               * application based on the card being produced. The PreferredWidth
               * and PreferredHeight properties indicate the preferred image dimensions
               * and aspect ratio. The default value is -1 (indicates no preference)
               */
              ImageParameter imageParam = (ImageParameter) dataParameter.getData();
              imageParam.setImageStream(new FileInputStream("photos/image1.png"));

              writeLine("  Param[%s] (%s): {%s} (Pref Size: %dx%d pixels)", i,
                imageParam.getDataType().name(), imageParam.getName(),
                imageParam.getPreferredWidth(), imageParam.getPreferredHeight());
              break;

            default:
              throw new CardServicesException("Unexpected service data parameter: " + dataParameter.getData().getName());
          }
        }

        /*
         * Configure the print destination and job displayed on the console
         */
        cardRequest.setDestination(printDestination.getDestination());
        cardRequest.setRequestName("Test card request");

        /*
         * Advanced SDK feature: Override the printer input hopper selection configured in the
         * card template. The default is to use the input hopper selected in the card template
         * unless explicitly overridden by a job service option setting as shown here.
         */
        cardRequest.getServiceOptions().put(PrinterOption.InputHopperSelect, PrinterOption.UseHopper1);
      }

      /*
       * Submit the job to the server for processing. The return job Id is typically
       * stored by the application and used to monitor job status and retrieve
       * job results using JobApi methods.
       */
      String submittedJobId = client.getJobApi().submitProductionRequest(requestTemplate);

      writeLine();
      writeLine("Job submitted successfully. Job unique Id = " + submittedJobId);

      /*
       * Query the server for a list of jobs submitted within the last 24 hours
       */
      showRecentJobs(client);

      /*
       * Query the server for details of the submitted job. The job status will be "Submitted"
       * pending completion of job.
       */
      showJobDetails(client, submittedJobId);

    } catch (CardServicesApiException ex) {
      writeLine("Client API Exception: (Error Code: %s) -> %s", ex.getStatusCode(), ex.getMessage());

    } catch (CardServicesException ex) {
      writeLine("Card Services Exception: " + ex.getMessage());

    } catch (Exception e) {
      writeLine("Invalid configuration-: " + e.getLocalizedMessage());
    }
  }

  
  /*
   * Success Testcases
   */
  public String orgname()
  {
	  CardServicesClient client = configureClient();
      /*
       * List the available organizations and select the first one for demo purposes. 
       */
      Organization organization = selectOrganization(client);     
	  return organization.getName();      
  }
  
  
  public String orgID()
  {
	  CardServicesClient client = configureClient();
      Organization organization = selectOrganization(client);
      showOrganizationInfo(client, organization.getOrganizationId());    
      //showOrganizationInfo(client, organization.getAccountNo());    
      return organization.getOrganizationId();      
  }
  
  public String orggetname()
  {
	  CardServicesClient client = configureClient();
      Organization organization = selectOrganization(client);   
      showOrganizationName(client, organization.getName());   
      writeLine("Organization Getname" + organization.getName());
      return organization.getName();      
  }
  
  private static void showOrganizationName(CardServicesClient client, String organizationname) {	    
	    writeLine("Organizational Units for Organization %s", organizationname);
	    writeLine();   
	    writeLine("Locations for Organization " + organizationname);
	    writeLine();
	  }

  public String location()
  {
	  CardServicesClient client = configureClient();
      Organization organization = selectOrganization(client);
     
      showOrganizationlocation(client, organization.getOrganizationId());    
      Location Location = showOrganizationlocation(client,organization.getOrganizationId());     
	  return Location.getLocationName();      
  }
  
  private static Location showOrganizationlocation(CardServicesClient client, String organizationId) {

	    writeLine("Organizational Units for Organization %s", organizationId);
	    writeLine();
	    List<Location> locations = client.getOrganizationApi().getOrganizationLocations(organizationId);
	    locations.forEach(location -> writeLine("  %s -> %s", location.getLocationId(), location.getLocationName()));
	   // writeLine();
	   return locations.get(0);
	 }

  public String locationID()
  {
	  CardServicesClient client = configureClient();
      Organization organization = selectOrganization(client);
     
      showOrganizationlocation(client, organization.getOrganizationId());    
      Location Location = showOrganizationlocation(client,organization.getOrganizationId());     
	  return Location.getLocationId();      
     // return loc.get(0).toString();      
  }
  
  public String productionProfileName()
  {
      CardServicesClient client = configureClient();
      Organization organization = selectOrganization(client);
      writeLine("Using Organization: " + organization.getName());
      writeLine();
      showOrganizationInfo(client, organization.getOrganizationId());
      ProductionProfile productionProfile = selectProductionProfile(client, organization.getOrganizationId());
      writeLine("profileiD"+productionProfile.getProfileId());        
      writeLine("Using production profile: " + productionProfile.getName());   
      return productionProfile.getName();      
  }

  public String productionProfileID()
  {
      CardServicesClient client = configureClient();
      Organization organization = selectOrganization(client);
      //writeLine("Using Organization: " + organization.getName());
      //writeLine();
      showOrganizationInfo(client, organization.getOrganizationId());
      ProductionProfile productionProfile = selectProductionProfile(client, organization.getOrganizationId());
      //writeLine("Using production profile: " + productionProfile.getProfileId());
      //writeLine();   
      return productionProfile.getProfileId();      
  }
  
  public String PrintDest()
  {
	  CardServicesClient client = configureClient();
      Organization organization = selectOrganization(client);   
      PrintDestination printDestination = selectPrintDestination(client, organization.getOrganizationId());

      writeLine("Using print destination: " + printDestination.getDestination());
      writeLine();
     
      return printDestination.getDestination();      
  }
  
  public String Prod()
  {
	  String cardtypes="";
  CardServicesClient client = configureClient();
  Organization organization = selectOrganization(client);   
  ProductionProfile productionProfile = selectProductionProfile(client, organization.getOrganizationId());
  ProductionProfileApi profileApi = client.getProductionProfileApi();
  ProductionProfileConfig profileConfig = profileApi.getProductionProfileParameters(productionProfile.getProfileId());
  for (ProductionProfileParameter profileParam : profileConfig.getProfileParameters()) {
      if (profileParam.getDataType() != DataType.List || !ProfileParamConst.CardType.equalsIgnoreCase(profileParam.getName())) {
        throw new CardServicesException("Unhandled production profile parameter type: " + profileParam.getDataType().name());
      }
      if (profileParam.getOptions().isEmpty()) {
        throw new CardServicesException("Production profile has no card types: " + profileParam.getDataType().name());
      }
      writeLine("Available card types");
      profileParam.getOptions().forEach(cardType -> writeLine("  '%s'", cardType));
      writeLine();
     
      String selectedCardType = profileParam.getOptions().get(0);
      profileParam.setValue(selectedCardType);
      writeLine("Selecting CardType: " + selectedCardType);
      cardtypes = selectedCardType;
      writeLine();
    }

  return cardtypes;
  }
  
  public String Prodservice() throws FileNotFoundException
  {
  CardServicesClient client = configureClient();
  Organization organization = selectOrganization(client);   
  PrintDestination printDestination = selectPrintDestination(client, organization.getOrganizationId());
  ProductionProfile productionProfile = selectProductionProfile(client, organization.getOrganizationId());
  ProductionProfileApi profileApi = client.getProductionProfileApi();
  ProductionProfileConfig profileConfig = profileApi.getProductionProfileParameters(productionProfile.getProfileId());
  for (ProductionProfileParameter profileParam : profileConfig.getProfileParameters()) {
      if (profileParam.getDataType() != DataType.List || !ProfileParamConst.CardType.equalsIgnoreCase(profileParam.getName())) {
        throw new CardServicesException("Unhandled production profile parameter type: " + profileParam.getDataType().name());
      }
      if (profileParam.getOptions().isEmpty()) {
        throw new CardServicesException("Production profile has no card types: " + profileParam.getDataType().name());
      }
      writeLine("Available card types");
      profileParam.getOptions().forEach(cardType -> writeLine("  '%s'", cardType));
      writeLine();
      String selectedCardType = profileParam.getOptions().get(0);
      profileParam.setValue(selectedCardType);
      writeLine("Selecting CardType: " + selectedCardType);
      writeLine();
    } 
  ProductionRequestTemplate requestTemplate = profileApi.configureProductionProfile(profileConfig);
  for (Service service : requestTemplate.getServices()) {
      if (service.getType() != ServiceType.CardRequest) {
        throw new CardServicesException("Unhandled production service type: " + service.getType().name());
      }
      CardRequestService cardRequest = (CardRequestService) service;

      for (int i = 0; i < cardRequest.getParameters().size(); i++) {

        Parameter dataParameter = cardRequest.getParameters().get(i);
        switch (dataParameter.getDataType()) {

          case Text:

            TextParameter textParam = (TextParameter) dataParameter.getData();
            textParam.setValue(textParam.getName());
            writeLine("  Param[%s] (%s): {%s} -> {%s}", i, textParam.getDataType().name(), textParam.getName(), textParam.getValue());
            break;

          case Image:
            ImageParameter imageParam = (ImageParameter) dataParameter.getData();
            imageParam.setImageStream(new FileInputStream("photos/image1.png"));

            writeLine("  Param[%s] (%s): {%s} (Pref Size: %dx%d pixels)", i,
              imageParam.getDataType().name(), imageParam.getName(),
              imageParam.getPreferredWidth(), imageParam.getPreferredHeight());
            break;

          default:
            throw new CardServicesException("Unexpected service data parameter: " + dataParameter.getData().getName());
        }
      }
      cardRequest.setDestination(printDestination.getDestination());
      cardRequest.setRequestName("Test card request1");
      cardRequest.getServiceOptions().put(PrinterOption.InputHopperSelect, PrinterOption.UseHopper1);
      cardRequest.getServiceOptions().put("CardRender.Enabled", "true"); 
      cardRequest.getServiceOptions().put(CardRenderOption.Enable, "true");
      cardRequest.getServiceOptions().put(CardRenderOption.CardSides, CardRenderOption.FrontOnly);
      cardRequest.getServiceOptions().put(CardRenderOption.ImageRotation, CardRenderOption.Clockwise90);
      cardRequest.getServiceOptions().put(CardRenderOption.ImageQuality, "1");
      cardRequest.getServiceOptions().put(CardRenderOption.OutputMode, CardRenderOption.RenderOnly);
    
  }
  String submittedJobId = client.getJobApi().submitProductionRequest(requestTemplate);
  writeLine(submittedJobId);
  
  return submittedJobId;
  
  }
  
  
  public String JobStatusPrinted() 
  {
	  CardServicesClient client = configureClient();
	  String Status="";
	    Job jobDetails = client.getJobApi().getJob("JOB055FB17381DF4788891DD1277399F9F5");

	    writeLine("Job Name..............: " + jobDetails.getJobName());
	    writeLine("Job Unique Id.........: " + jobDetails.getJobUniqueId());
	    writeLine("Job Status............: " + jobDetails.getJobStatus());
	    writeLine("Status Message........: " + jobDetails.getJobStatusMessage());
	    writeLine("Date Submitted........: " + jobDetails.getSubmitDate().toLocalTime());
	    writeLine("Last Updated..........: " + jobDetails.getLastUpdate().toLocalTime());
	    
	    Status =jobDetails.getJobStatus();
	  
	return Status;
  }
  
  public String JobStatusFailed() 
  {
	  CardServicesClient client = configureClient();
	  String Status="";
	  Job jobDetails = client.getJobApi().getJob("JOBC9914E45D536449BB5A5A8317A344A07");
	    //Job jobDetails = client.getJobApi().getJob("JOBFE61803EF19A40A8B69E7CA25BF0322E");
	     
	    writeLine("Job Name..............: " + jobDetails.getJobName());
	    writeLine("Job Unique Id.........: " + jobDetails.getJobUniqueId());
	    writeLine("Job Status............: " + jobDetails.getJobStatus());
	    writeLine("Status Message........: " + jobDetails.getJobStatusMessage());
	    writeLine("Date Submitted........: " + jobDetails.getSubmitDate().toLocalTime());
	    writeLine("Last Updated..........: " + jobDetails.getLastUpdate().toLocalTime());
	    
	    Status =jobDetails.getJobStatus();
	  
	return Status;
  }
  
  

  /*
   * Exception Testcases
   */
  public void configureServerUri(String serverBaseUri)throws CardServicesException
  {
	    int port;
	    String host;
	    String scheme;
	    if (serverBaseUri == null || ValidatorUtils.isBlank(serverBaseUri)) {
	      throw new CardServicesException("ServerBaseUri setting cannot be null or blank");
	    }
	    else {
    
	    try {
	      URI parsedBaseUri = new URI(serverBaseUri);
	      scheme = parsedBaseUri.getScheme();
	      host = parsedBaseUri.getHost();
	      port = (parsedBaseUri.getPort() != -1) ? parsedBaseUri.getPort() : 18443;
	    } catch (Exception ex) {
	      throw new CardServicesException("Error parsing ServerBaseUri setting: " + serverBaseUri, ex);
	    } 

	    if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
	      throw new CardServicesException("ServerBaseUri protocol must be HTTP or HTTPS: " + serverBaseUri);
	    }
	    
	    if (ValidatorUtils.isBlank(host)) {
	      throw new CardServicesException("ServerBaseUri does not specify a host name: " + serverBaseUri);
	    }
	    
	    if (port <= 0 || port > 65535) {
	      throw new CardServicesException("ServerBaseUri port number is invalid: " + serverBaseUri);
	    }
	    }
	    
		}

    
public TrustManager[] loadClientTrustChain(CardServicesClientConfig clientConfig) throws CardServicesException
{
  try {
      InputStream trustCertStream = clientConfig.getCaCertStream();
      
      if (!ValidatorUtils.isBlank(clientConfig.getCaCertFile())) {
        Path certFilePath = Paths.get(clientConfig.getCaCertFile(), new String[0]);
        if (!Files.exists(certFilePath, new java.nio.file.LinkOption[0])) {
          throw new CardServicesException("Client CA certificate file not found: " + clientConfig.getCaCertFile());
        }
        trustCertStream = new ByteArrayInputStream(Files.readAllBytes(certFilePath));
      } 



      
      if (trustCertStream == null) {
        throw new CardServicesException("Client CaCertFile or CaCertStream setting must be configured");
      }

      KeyStore trustChainStore = KeyStore.getInstance(KeyStore.getDefaultType());
      trustChainStore.load(null);
      
      CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
      int certCount = 1;
      
      TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(trustChainStore);
      return tmf.getTrustManagers();
    }
    catch (CardServicesException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new CardServicesException("Error loading client CA certificate chain", ex);
    } 
}

public KeyManager[] loadClientAuthCert(CardServicesClientConfig clientConfig) throws CardServicesException{
    if (ValidatorUtils.isBlank(clientConfig.getAuthCertPassword())) {
      throw new CardServicesException("Client AuthCertPassword setting must be configured");
    }

    try {
      KeyStore clientAuthCertStore;

      InputStream authCertStream = clientConfig.getAuthCertStream();
      
      if (!ValidatorUtils.isBlank(clientConfig.getAuthCertFile())) {
        Path certFilePath = Paths.get(clientConfig.getAuthCertFile(), new String[0]);
        if (!Files.exists(certFilePath, new java.nio.file.LinkOption[0])) {
          throw new CardServicesException("Client authentication cert file not found: " + clientConfig.getAuthCertFile());
        }
        authCertStream = new ByteArrayInputStream(Files.readAllBytes(certFilePath));
      } 

      if (authCertStream == null) {
        throw new CardServicesException("Client AuthCertFile or AuthCertStream setting must be configured");
      }
      
      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      //keyManagerFactory.init(clientAuthCertStore, clientConfig.getAuthCertPassword().toCharArray());
      return keyManagerFactory.getKeyManagers();
    }
    catch (CardServicesException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new CardServicesException("Error loading client authentication certificate", ex);
    }
	
  }


public KeyStore loadDataEncryptionCert(CardServicesClientConfig clientConfig) {
    if (ValidatorUtils.isBlank(clientConfig.getEncryptionCertPassword())) {
      throw new CardServicesException("Client EncryptionCertPassword setting must be configured");
    }
    
    try {
      KeyStore encryptionCertStore;

      InputStream encryptionCertStream = clientConfig.getEncryptionCertStream();
      
      if (!ValidatorUtils.isBlank(clientConfig.getEncryptionCertFile())) {
        Path certFilePath = Paths.get(clientConfig.getEncryptionCertFile(), new String[0]);
        if (!Files.exists(certFilePath, new java.nio.file.LinkOption[0])) {
          throw new CardServicesException("Client data encryption cert file not found: " + clientConfig.getEncryptionCertFile());
        }
        encryptionCertStream = new ByteArrayInputStream(Files.readAllBytes(certFilePath));
      } 

      if (encryptionCertStream == null) {
        throw new CardServicesException("Client EncryptionCertFile or EncryptionCertStream setting must be configured");
      }
      encryptionCertStore = KeyStore.getInstance("PKCS12");
     // inputStream = encryptionCertStream; throwable = null; 
      //try { 
        //encryptionCertStore.load(inputStream, clientConfig.getEncryptionCertPassword().toCharArray()); } catch (Throwable throwable1) { throwable = throwable1 = null; throw throwable1; }
      //finally { if (inputStream != null) $closeResource(throwable, inputStream);
        // }


      
      return encryptionCertStore;
    }
    catch (CardServicesException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new CardServicesException("Error loading data encryption certificate", ex);
    } 
  }
  
  
  
  /**
   * Lists the available organizations and arbitrarily selects and returns the first
   * organization for demo purposes. Organization identifiers are designed to be stable
   * over time and can be stored and reused.
   *
   * @param client Card services client
   * @return Selected organization
   */
  private static Organization selectOrganization(CardServicesClient client) {

    OrganizationApi organizationApi = client.getOrganizationApi();
    List<Organization> organizations = organizationApi.getOrganizations();

    if (organizations.isEmpty()) {
      throw new CardServicesException("No organizations found");
    }

    writeLine("Available organizations");
    organizations.forEach(org -> writeLine("  %s -> %s", org.getOrganizationId(), org.getName()));
    writeLine();

    return organizations.get(0);
  }

  /**
   * Lists the organizational units and locations within the specified organization. The
   * organizational structure is loosely based on the X.500 directory model and has a fixed
   * three-tier hierarchy of Organization, Organizational Unit and Location. Devices are
   * defined at the Location level of the hierarchy. Organizational identifiers are designed
   * to be stable over time and can be stored and reused.
   *
   * @param client Card services client
   * @param organizationId Organization unique Id
   */
  private static void showOrganizationInfo(CardServicesClient client, String organizationId) {

    /*
     * Enumerate the organizational units within the organization
     */
    writeLine("Organizational Units for Organization %s", organizationId);
    List<OrganizationalUnit> orgUnits = client.getOrganizationApi().getOrganizationalUnits(organizationId);
    orgUnits.forEach(orgUnit -> writeLine("  %s -> %s", orgUnit.getOrganizationUnitId(), orgUnit.getName()));
    writeLine();

    /*
     * Enumerate the locations for the organization. Note that Locations exist within
     * Organizational Units, but are listed here across all Organizational Units for
     * simplicity. Use the GetOrganizationUnitLocations method to query Locations by
     * organizational unit.
     */
    writeLine("Locations for Organization " + organizationId);
    List<Location> locations = client.getOrganizationApi().getOrganizationLocations(organizationId);
    locations.forEach(location -> writeLine("  %s -> %s", location.getLocationId(), location.getLocationName()));
    writeLine();
  }

  /**
   * Lists the organizational units and locations within the specified organization. The
   * organizational structure is loosely based on the X.500 directory model and has a fixed
   * three-tier hierarchy of Organization, Organizational Unit and Location. Devices are
   * defined at the Location level of the hierarchy. Organizational identifiers are designed
   * to be stable over time and can be stored and reused.
   *
   * @param client Card services client
   * @param organizationId Organization unique Id
   * @return Selected production profile
   */
  private static ProductionProfile selectProductionProfile(CardServicesClient client, String organizationId) {

    List<ProductionProfile> productionProfiles = client.getProductionProfileApi().getProductionProfiles(organizationId);

    if (productionProfiles.isEmpty()) {
      throw new CardServicesException("No production profiles found");
    }

    writeLine("Available production profiles");
    productionProfiles.forEach(profile -> writeLine("  %s -> %s", profile.getProfileId(), profile.getName()));
    writeLine();

    return productionProfiles.get(0);
  }

  /**
   * Lists the available print destinations for the given organization and arbitrarily
   * selects and returns the first print destination for demo purposes. Print destination
   * identifiers are designed to be stable over time and can be stored and reused.
   *
   * @param client Card services client
   * @param organizationId Organization unique Id
   * @return Selected print destination
   */
  private static PrintDestination selectPrintDestination(CardServicesClient client, String organizationId) {

    List<PrintDestination> printDestinations = client.getDeviceApi().getPrintDestinations(organizationId);

    if (printDestinations.isEmpty()) {
      throw new CardServicesException("No print destinations found");
    }

    writeLine("Available print destinations");
    printDestinations.forEach(destination -> writeLine("  %s -> %s", destination.getDestination(), destination.getPrinterName()));
    writeLine();

    return printDestinations.get(0);
  }

  /**
   * Demonstrates querying the server for jobs submitted within on a defines look-back
   * period. This is useful for displaying recent job activity. The GetJobsForDateRange
   * method can be used for the same purpose, but provides additional flexibility.
   *
   * @param client Card services client
   */
  private static void showRecentJobs(CardServicesClient client) {

    writeLine();
    writeLine("Recent job  details");
    writeLine();

    List<Job> recentJobs = client.getJobApi().getJobsForTimePeriod(100, Duration.ofHours(24));

    for (Job jobDetails : recentJobs) {
      writeLine("Job Name..............: " + jobDetails.getJobName());
      writeLine("Job Status............: " + jobDetails.getJobStatus());
      writeLine("Status Message........: " + jobDetails.getJobStatusMessage());
      writeLine("Date Submitted........: " + jobDetails.getSubmitDate().toLocalDateTime());
      writeLine();
    }
  }

  /**
   * Queries the sever for the job corresponding to the specified unique job Id.
   *
   * @param client Card services client
   * @param jobUniqueId Job unique Id returned during job submission
   */
  private static void showJobDetails(CardServicesClient client, String jobUniqueId) {

    writeLine();
    writeLine("Retrieving job details");
    writeLine();

    Job jobDetails = client.getJobApi().getJob(jobUniqueId);

    writeLine("Job Name..............: " + jobDetails.getJobName());
    writeLine("Job Unique Id.........: " + jobDetails.getJobUniqueId());
    writeLine("Job Status............: " + jobDetails.getJobStatus());
    writeLine("Status Message........: " + jobDetails.getJobStatusMessage());
    writeLine("Date Submitted........: " + jobDetails.getSubmitDate().toLocalTime());
    writeLine("Last Updated..........: " + jobDetails.getLastUpdate().toLocalTime());

    /*
     * Show job results if the job printed successfully
     */
    if (JobStatus.Printed.equalsIgnoreCase(jobDetails.getJobStatus())) {

      CardReadResults cardReadResults = jobDetails.getServiceData().getCardReadResults();
      writeLine("Card Read Results.....: %d Card Edge(s) Found", cardReadResults.getCardEdges().size());

      if (!cardReadResults.getCardEdges().isEmpty()) {

        /*
         * Show the details for each of the card edges found. All card edges discovered
         * are returned. Card types enabled in the Card Read Service configuration in
         * the card template have an Enabled value of true.
         *
         * Note: Some card technologies such as HID_ICLASS support multiple frame
         *   protocols and may be reported in the results more than once. The
         *   CardSerialNumber and PACS data should be identical when this occurs.
         */
        for (CardEdgeData cardEdge : cardReadResults.getCardEdges()) {
          writeLine();
          writeLine("  Card Edge Type......: " + cardEdge.getEdgeType());
          writeLine("  Card Protocol.......: " + cardEdge.getCardProtocol());
          writeLine("  Card Edge Enabled...: " + cardEdge.getEnabled());
          writeLine("  Card Read Status....: " + cardEdge.getStatus());
          writeLine("  Card Read Message...: " + cardEdge.getStatusMessage());
          writeLine("  Card Serial Number..: " + cardEdge.getCardSerialNumber());
          writeLine("  PACS Data Available.: " + cardEdge.isPacsDataAvailable());

          /*
           * Display the card PACS bits and decoded PACS data for the card edge
           */
          if (cardEdge.isPacsDataAvailable()) {
            writeLine("  PACS Bit Data.......: 0x" + cardEdge.getCardPacsBitData());
            writeLine("  PACS Bit Count......: " + cardEdge.getCardPacsBitCount());

            /*
             * Show the PACS decode results for each card format configured for
             * this card edge in the card designer. The decoded PACS data is only
             * valid when the DecodeStatus is "Success". The application developer
             * must ensure the correct format(s) are configured in the card designer
             * and provide appropriate format selection and error handling logic.
             */
            cardEdge.getPacsData().forEach(decodeResult -> {
              writeLine();
              writeLine("    Card Format.......: " + decodeResult.getFormatName());
              writeLine("    Format Bit Count..: " + decodeResult.getFormatBitCount());
              writeLine("    Decode Status.....: " + decodeResult.getDecodeStatus());
              writeLine("    Status Message....: " + decodeResult.getStatusMessage());
              writeLine("    Card Number.......: " + decodeResult.getCardNumber());
              writeLine("    PACS Data Fields:");

              /*
               * List the data fields extracted from the PACS bits. The list
               * of fields and their names are defined by the card format and
               * should not be assumed to be consistent across card formats.
               *
               * Note: The card number is always included in this list since
               *   is is a mandatory field, but the name of the field may vary
               *   across card formats. Please use the CardNumber property
               *   instead.
               */
              decodeResult.getPacsFields().forEach((key, value) ->
                writeLine("      %s -> %s", key, value));
            });
          }

          /*
           * Show additional key/value pairs returned for the card edge. This is
           * used to return ad-hoc data values for specialized applications.
           */
          if (!cardEdge.getData().isEmpty()) {
            writeLine();
            writeLine("  Additional Data");

            cardEdge.getData().forEach((key, value) -> {
              writeLine("    %s -> %s", key, value);
            });
          }
        }
      }
    }

    //
    //  Sample Output - Dual technology MIFARE/SEOS card
    //
    //  Job Name..............: Card Read Test
    //  Job Unique Id.........: JOBA5D31540B8AC4CCDB930FF914D9228D3
    //  Job Status............: Printed
    //  Status Message........: Job printed successfully
    //  Date Submitted........: 4/9/2018 3:31:28 PM
    //  Last Updated..........: 4/9/2018 3:34:22 PM
    //  Card Read Results.....: 2 Card Edge(s) Found
    //
    //    Card Edge Type......: MIFARE_CLASSIC
    //    Card Protocol.......: ISO14443A_3
    //    Card Edge Enabled...: True
    //    Card Read Status....: Success
    //    Card Read Message...: Card edge found and processed successfully
    //    Card Serial Number..: 088931D3
    //    PACS Data Available.: False
    //
    //    Card Edge Type......: SEOS
    //    Card Protocol.......: ISO14443A
    //    Card Edge Enabled...: True
    //    Card Read Status....: Success
    //    Card Read Message...: Card edge found and processed successfully
    //    Card Serial Number..: 08A62E6B
    //    PACS Data Available.: True
    //    PACS Bit Data.......: 0x1D682250
    //    PACS Bit Count......: 35
    //
    //      Card Format.......: H234561
    //      Format Bit Count..: 26
    //      Decode Status.....: Success
    //      Status Message....: Decode succeeded
    //      Card Number.......: 679123
    //      PACS Data Fields
    //        Facility Code-> 115
    //        Card Number -> 679123
  }

  /**
   * Writes the given message to the console
   */
  private static void writeLine(String message) {

    System.out.println(message);
  }

  /**
   * Writes a blank line to the console
   */
  private static void writeLine() {

    System.out.println();
  }

  /**
   * Writes a formatted message to the console
   */
  private static void writeLine(String format, Object... args) {

    System.out.println(String.format(format, args));
  }
}


