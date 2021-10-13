package junit_testing;

import static org.junit.Assert.*;

import org.junit.Test;

import examples.SampleApplication;

public class successTest {
	
	SampleApplication myclass=new SampleApplication();

	@Test
	public void OrgName() {
		String Orgname = myclass.orgname();	
		assertEquals("Junittesting", Orgname);
	}

	@Test
	public void OrgID() {
		String OrgID = myclass.orgID();	
		assertEquals("ORG66EEA7F7C73141849E4D0CB4B733A0CD", OrgID);
	}

	@Test
	public void OrgGetName() {
		String Orggetname = myclass.orggetname();	
		assertEquals("Junittesting", Orggetname);
	}

	@Test
	public void Location() {
		String location = myclass.location();	
		assertEquals("subramanya H P", location);
	}
	
	public void LocationID() {
		String locationID = myclass.locationID();	
		assertEquals("LOC7B00503775AF437FB2AAB161A70D2627", locationID);
	}
	
	@Test
	public void ProdProfileName() {
		String Prodname = myclass.productionProfileName();	
		assertEquals("testing", Prodname);
	}
	
	@Test
	public void ProdProfileID() {
		String ProdID = myclass.productionProfileID();	
		assertEquals("PRAED88ED3EEA94F94B012211145E44742", ProdID);
	}
	
	@Test
	public void PrintDestination() {
		String Printdest = myclass.PrintDest();	
		assertEquals("MFA190F19BBA9634BBE850DCE852452E814@Printer1", Printdest);
	}
	
	@Test
	public void Prod() {
		String Cardtype = myclass.Prod();	
		assertEquals("blankcard", Cardtype);
	}
	
	@Test
	public void PrintedJob() {
		String printed = myclass.JobStatusPrinted();	
		assertEquals("Printed", printed);
	}
	
	@Test
	public void PrintJobFailed() {
		String Failed = myclass.JobStatusFailed();	
		assertEquals("Failed", Failed);
	}
	
	
}
