package junit_testing;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.extensia.xcp.services.restapi.client.CardServicesClientConfig;
import com.extensia.xcp.services.restapi.client.CardServicesException;

import examples.SampleApplication;

public class ExceptionTest {

	
	@Test(expected = CardServicesException.class)
	public void configureServerUriException() throws CardServicesException
	{
		SampleApplication baseuri=new SampleApplication();
		baseuri.configureServerUri("");
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void configureServerUriExceptionMessage() throws CardServicesException
	{ 
	thrown.expect(CardServicesException.class);
	thrown.expectMessage("ServerBaseUri setting cannot be null or blank");
	//thrown.expectMessage("Error parsing ServerBaseUri setting: ");
	SampleApplication baseuri=new SampleApplication();
	baseuri.configureServerUri(""); 
	}
	
	/* Exception for "ServerBaseUri port number is invalid:"
	 */
	
	@Test(expected = CardServicesException.class)
	public void configureServerUriPortException() throws CardServicesException
	{
		SampleApplication baseuri=new SampleApplication();
		baseuri.configureServerUri("https://test.api.hfc.hidglobal.com:184432");
	}
	
	@Test
	public void configureServerUriExceptionTestMessage() throws CardServicesException
	{ 
	thrown.expect(CardServicesException.class);
	thrown.expectMessage("ServerBaseUri port number is invalid: " + "https://test.api.hfc.hidglobal.com:184432");
	SampleApplication baseuri=new SampleApplication();
	baseuri.configureServerUri("https://test.api.hfc.hidglobal.com:184432");
	}
	

	/* Exception for "ServerBaseUri does not specify a host name:"
	 */
	
	@Test(expected = CardServicesException.class)
	public void configureServerUriHostException() throws CardServicesException
	{
		SampleApplication baseuri=new SampleApplication();
		baseuri.configureServerUri("httpss://:18443");
	}
	
	@Test
	public void configureServerUriExceptionHostMessage() throws CardServicesException
	{ 
	thrown.expect(CardServicesException.class);
	thrown.expectMessage("ServerBaseUri does not specify a host name: " + "https://:184432");
	SampleApplication baseuri=new SampleApplication();
	baseuri.configureServerUri("https://:184432");
	}
	
	/* Exception for "ServerBaseUri protocol must be HTTP or HTTPS: "
	 */
	
	@Test(expected = CardServicesException.class)
	public void configureServerUriHTTPException() throws CardServicesException
	{
		SampleApplication baseuri=new SampleApplication();
		baseuri.configureServerUri("httpss://test.api.hfc.hidglobal.com:18443");
	}
	
	@Test
	public void configureServerUriExceptionHTTPMessage() throws CardServicesException
	{ 
	thrown.expect(CardServicesException.class);
	thrown.expectMessage("ServerBaseUri protocol must be HTTP or HTTPS: " + "httpss://test.api.hfc.hidglobal.com:18443");
	SampleApplication baseuri=new SampleApplication();
	baseuri.configureServerUri("httpss://test.api.hfc.hidglobal.com:18443");
	}
	
	/* Exception for "Error parsing ServerBaseUri setting: "
	 */
	
	@Test(expected = CardServicesException.class)
	public void configureServerUriParsingException() throws CardServicesException
	{
		SampleApplication baseuri=new SampleApplication();
		baseuri.configureServerUri("https:\test.api.hfc.hidglobal.com:\18443");
	}
	
	@Test
	public void configureServerUriExceptionParsingMessage() throws CardServicesException
	{ 
	thrown.expect(CardServicesException.class);
	thrown.expectMessage("Error parsing ServerBaseUri setting: " + "https:\test.api.hfc.hidglobal.com:\18443");
	SampleApplication baseuri=new SampleApplication();
	baseuri.configureServerUri("https:\test.api.hfc.hidglobal.com:\18443");
	}
	
	/* Exception for Client CA certificate file not found: 
	 */
	@Test(expected = CardServicesException.class)
	public void TrustCAcertException() throws CardServicesException
	{
		SampleApplication baseuri=new SampleApplication();
		 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
		 clientConfig.setCaCertFile("C:/Users/subheg/Document/subramanya/Fargo/HFC Code/CBORD-HID FARGO Connect/HIDCertChain2.pem");
		baseuri.loadClientTrustChain(clientConfig);
	}

	@Test
	public void TrustCAcertExceptiongMessage() throws CardServicesException
	{ 
	thrown.expect(CardServicesException.class);
	thrown.expectMessage("Client CA certificate file not found: " + "C:/Users/subheg/Document/subramanya/Fargo/HFC Code/CBORD-HID FARGO Connect/HIDCertChain2.pem");
	SampleApplication baseuri=new SampleApplication();
	 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
	 clientConfig.setCaCertFile("C:/Users/subheg/Document/subramanya/Fargo/HFC Code/CBORD-HID FARGO Connect/HIDCertChain2.pem");
	baseuri.loadClientTrustChain(clientConfig);
	}
	
	
	/* Exception for Client CaCertFile or CaCertStream setting must be configured: 
	 */
	@Test(expected = CardServicesException.class)
	public void TrustCAcertnullException() throws CardServicesException
	{
		SampleApplication baseuri=new SampleApplication();
		 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
		 clientConfig.setCaCertFile(null);
		baseuri.loadClientTrustChain(clientConfig);
	}

	@Test
	public void TrustCAcertnullExceptiongMessage() throws CardServicesException
	{ 
	thrown.expect(CardServicesException.class);
	thrown.expectMessage("Client CaCertFile or CaCertStream setting must be configured");
	SampleApplication baseuri=new SampleApplication();
	CardServicesClientConfig clientConfig = new CardServicesClientConfig();
	clientConfig.setCaCertFile(null);
	baseuri.loadClientTrustChain(clientConfig);
	}
	
	/* Exception for Error loading client CA certificate chain
	 */
	@Test(expected = CardServicesException.class)
	public void TrustCAcertErrorloadingException() throws CardServicesException
	{
		SampleApplication baseuri=new SampleApplication();
		 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
		 clientConfig.setCaCertFile("C:/C:\\\\HFCCertification/Schlumberger-Development-Certs/Java/Schlumberger_Card_Services_CA_Chain.pem");
		baseuri.loadClientTrustChain(clientConfig);
	}

	@Test
	public void TrustCAcertErrorloadingExceptiongMessage() throws CardServicesException
	{ 
	thrown.expect(CardServicesException.class);
	thrown.expectMessage("Error loading client CA certificate chain");
	SampleApplication baseuri=new SampleApplication();
	 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
	 clientConfig.setCaCertFile("C:/C:\\HFCCertification/Schlumberger-Development-Certs/Java/Schlumberger_Card_Services_CA_Chain.pem");
	baseuri.loadClientTrustChain(clientConfig);
	}
	
	
	/* Exception for Client AuthCertPassword setting must be configured
	 */
	@Test(expected = CardServicesException.class)
	public void loadClientAuthCertException() throws CardServicesException
	{
		SampleApplication baseuri=new SampleApplication();
		 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
		 clientConfig.setAuthCertPassword(" ");
		baseuri.loadClientAuthCert(clientConfig);
	}

	@Test
	public void loadClientAuthCertExceptiongMessage() throws CardServicesException
	{ 
	thrown.expect(CardServicesException.class);
	thrown.expectMessage("Client AuthCertPassword setting must be configured");
	SampleApplication baseuri=new SampleApplication();
	 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
	 //clientConfig.setAuthCertPassword("418n94feLbHPOW86ymXp");
	 clientConfig.setAuthCertPassword(" ");
	baseuri.loadClientAuthCert(clientConfig);
	}
	
	
	/* Exception for Client authentication cert file not found: 
	 */
	@Test(expected = CardServicesException.class)
	public void loadClientAuthCertFileException() throws CardServicesException
	{
		SampleApplication baseuri=new SampleApplication();
		 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
		 clientConfig.setAuthCertFile("C:/Users/subheg/Document/subramanya/Fargo/HFC Code/CBORD-HID FARGO Connect/HIDCertChain2.pem");
		 clientConfig.setAuthCertPassword("418n94feLbHPOW86ymXp");
		baseuri.loadClientAuthCert(clientConfig);
	}

	@Test
	public void loadClientAuthCertFileExceptiongMessage() throws CardServicesException
	{ 
	thrown.expect(CardServicesException.class);	
	SampleApplication baseuri=new SampleApplication();
	 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
	 clientConfig.setAuthCertPassword("418n94feLbHPOW86ymXp");
	 clientConfig.setAuthCertFile("C:/Users/subheg/Document/subramanya/Fargo/HFC Code/CBORD-HID FARGO Connect/HIDCertChain2.pem");
	 thrown.expectMessage("Client authentication cert file not found: " + clientConfig.getAuthCertFile());
	baseuri.loadClientAuthCert(clientConfig);
	}
	
	
	/* Exception for Client AuthCertFile or AuthCertStream setting must be configured
	 */
	@Test(expected = CardServicesException.class)
	public void loadClientAuthStreamException() throws CardServicesException
	{
		SampleApplication baseuri=new SampleApplication();
		 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
		 clientConfig.setAuthCertStream(null);
		// clientConfig.setAuthCertPassword("418n94feLbHPOW86ymXps");
		baseuri.loadClientAuthCert(clientConfig);
	}

	@Test
	public void loadClientAuthStreamExceptiongMessage() throws CardServicesException
	{ 
	thrown.expect(CardServicesException.class);	
	SampleApplication baseuri=new SampleApplication();
	 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
	 clientConfig.setAuthCertFile(null);
	 clientConfig.setAuthCertPassword("418n94feLbHPOW86ymXp");
	 thrown.expectMessage("Client AuthCertFile or AuthCertStream setting must be configured");
	 baseuri.loadClientAuthCert(clientConfig);
	}
	
	/* Exception for Error loading Auth CA certificate chain
	 */
	@Test(expected = CardServicesException.class)
	public void loadClientAuthLoadingException() throws CardServicesException
	{
		SampleApplication baseuri=new SampleApplication();
		 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
		 clientConfig.setAuthCertFile("C:/C:\\\\HFCCertification/Schlumberger-Development-Certs/Java/Schlumberger_Card_Services_CA_Chain.pem");
		baseuri.loadClientAuthCert(clientConfig);
	}

	@Test
	public void loadClientAuthLoadingExceptiongMessage() throws CardServicesException
	{ 
	thrown.expect(CardServicesException.class);
	thrown.expectMessage("Error loading client authentication certificate");
	SampleApplication baseuri=new SampleApplication();
	 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
	 clientConfig.setAuthCertFile("C://HFCCertification/Schlumberger-Development-Certs/Java/Schlumberger_Card_Services_CA_Chain.pem");
	 clientConfig.setAuthCertPassword("418n94feLbHPOW86ymXp");
	 baseuri.loadClientAuthCert(clientConfig);
	}
	
	/* Exception for Client EncryptionCertPassword setting must be configured
	 */
	@Test(expected = CardServicesException.class)
	public void loadDataEncryptionAuthpasswordException() throws CardServicesException
	{
		SampleApplication baseuri=new SampleApplication();
		 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
		clientConfig.setEncryptionCertPassword(null);
		baseuri.loadDataEncryptionCert(clientConfig);
	}

	@Test
	public void loadDataEncryptionAuthpasswordMessage() throws CardServicesException
	{ 
	thrown.expect(CardServicesException.class);
	thrown.expectMessage("Client EncryptionCertPassword setting must be configured");
	SampleApplication baseuri=new SampleApplication();
	 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
	 clientConfig.setEncryptionCertPassword(null);
	baseuri.loadDataEncryptionCert(clientConfig);
	}
	
	
	/* Exception for Client authentication cert file not found: 
	 */
	@Test(expected = CardServicesException.class)
	public void loadDataEncryptionAuthfileException() throws CardServicesException
	{
		SampleApplication baseuri=new SampleApplication();
		 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
		 clientConfig.setEncryptionCertFile("C:/Users/subheg/Document/subramanya/Fargo/HFC Code/CBORD-HID FARGO Connect/HIDEncryptCert2.p12");
		 clientConfig.setEncryptionCertPassword("418n94feLbHPOW86ymXp");
			baseuri.loadDataEncryptionCert(clientConfig);
	}

	@Test
	public void loadDataEncryptionAuthfileMessage() throws CardServicesException
	{ 
	thrown.expect(CardServicesException.class);	
	SampleApplication baseuri=new SampleApplication();
	 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
	 clientConfig.setEncryptionCertPassword("418n94feLbHPOW86ymXp");
	 clientConfig.setEncryptionCertFile("C:/Users/subheg/Document/subramanya/Fargo/HFC Code/CBORD-HID FARGO Connect/HIDEncryptCert.p12");
	 thrown.expectMessage("Client data encryption cert file not found: " + clientConfig.getEncryptionCertFile());
		baseuri.loadDataEncryptionCert(clientConfig);
	}
	
	
	/* Exception for Client EncryptionCertFile or EncryptionCertStream setting must be configured
	 */
	@Test(expected = CardServicesException.class)
	public void encryptionCertStreamException() throws CardServicesException
	{
		SampleApplication baseuri=new SampleApplication();
		 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
		 clientConfig.setEncryptionCertStream(null);
		baseuri.loadDataEncryptionCert(clientConfig);
	}

	@Test
	public void encryptionCertStreamExceptionMessage() throws CardServicesException
	{ 
	thrown.expect(CardServicesException.class);	
	SampleApplication baseuri=new SampleApplication();
	 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
	 clientConfig.setEncryptionCertStream(null);
	 clientConfig.setEncryptionCertPassword("418n94feLbHPOW86ymXp");
	 thrown.expectMessage("Client EncryptionCertFile or EncryptionCertStream setting must be configured");
	 baseuri.loadDataEncryptionCert(clientConfig);
	}
	
	
	/* Exception for Error loading Auth CA certificate chain
	 */
	@Test(expected = CardServicesException.class)
	public void loadClientEncryLoadingException() throws CardServicesException
	{
		SampleApplication baseuri=new SampleApplication();
		 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
		 clientConfig.setEncryptionCertFile("C:c:/Users/subheg/Document/subramanya/Fargo/HFC Code/CBORD-HID FARGO Connect/HIDEncryptCert.p12");
		 baseuri.loadDataEncryptionCert(clientConfig);
	}

	@Test
	public void loadClientEncryLoadingExceptiongMessage() throws CardServicesException
	{ 
	thrown.expect(CardServicesException.class);
	thrown.expectMessage("Error loading data encryption certificate");
	SampleApplication baseuri=new SampleApplication();
	 CardServicesClientConfig clientConfig = new CardServicesClientConfig();
	 clientConfig.setEncryptionCertFile("C:c:/Users/subheg/Document/subramanya/Fargo/HFC Code/CBORD-HID FARGO Connect/HIDEncryptCert.p12");
	 clientConfig.setEncryptionCertPassword("418n94feLbHPOW86ymXp");
	 baseuri.loadDataEncryptionCert(clientConfig);
	}
	
}
