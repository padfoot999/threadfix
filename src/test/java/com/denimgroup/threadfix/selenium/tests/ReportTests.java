package com.denimgroup.threadfix.selenium.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.denimgroup.threadfix.data.entities.ApplicationCriticality;
import com.denimgroup.threadfix.data.entities.ChannelType;
import com.denimgroup.threadfix.selenium.pages.AddChannelPage;
import com.denimgroup.threadfix.selenium.pages.AddOrganizationPage;
import com.denimgroup.threadfix.selenium.pages.ApplicationAddPage;
import com.denimgroup.threadfix.selenium.pages.ApplicationDetailPage;
import com.denimgroup.threadfix.selenium.pages.GeneratedReportPage;
import com.denimgroup.threadfix.selenium.pages.LoginPage;
import com.denimgroup.threadfix.selenium.pages.OrganizationDetailPage;
import com.denimgroup.threadfix.selenium.pages.OrganizationIndexPage;
import com.denimgroup.threadfix.selenium.pages.ReportsIndexPage;
import com.denimgroup.threadfix.selenium.pages.UploadScanPage;

public class ReportTests extends BaseTest {
	private FirefoxDriver driver;

	// private WebDriver driver;
	private static LoginPage loginPage;
	public ApplicationDetailPage applicationDetailPage;
	public UploadScanPage uploadScanPage;
	public AddChannelPage addChannelPage;
	public OrganizationIndexPage organizationIndexPage;
	public OrganizationDetailPage organizationDetailPage;
	public ReportsIndexPage reportsIndexPage;
	public GeneratedReportPage generatedReportPage;
	public AddOrganizationPage organizationAddPage;
	public ApplicationAddPage applicationAddPage;

	Random generator = new Random();

	private String[] criticalities = { ApplicationCriticality.LOW,
			ApplicationCriticality.MEDIUM, ApplicationCriticality.HIGH,
			ApplicationCriticality.CRITICAL };

	boolean mySQL = true;

	public String appWasAlreadyUploadedErrorText = "Scan file has already been uploaded.";

	private static Map<String, URL> fileMap = new HashMap<String, URL>();
	static {
		fileMap.put("Microsoft CAT.NET",
				getScanFilePath("Static", "CAT.NET", "catnet_RiskE.xml"));
		fileMap.put("FindBugs",
				getScanFilePath("Static", "FindBugs", "findbugs-normal.xml"));
		fileMap.put("IBM Rational AppScan",
				getScanFilePath("Dynamic", "AppScan", "appscan-php-demo.xml"));
		fileMap.put(
				"Mavituna Security Netsparker",
				getScanFilePath("Dynamic", "NetSparker",
						"netsparker-demo-site.xml"));
		fileMap.put(
				"Skipfish",
				getScanFilePath("Dynamic", "Skipfish", "skipfish-demo-site.zip"));
		fileMap.put("w3af",
				getScanFilePath("Dynamic", "w3af", "w3af-demo-site.xml"));
		fileMap.put("OWASP Zed Attack Proxy",
				getScanFilePath("Dynamic", "ZAP", "zaproxy-normal.xml"));
		fileMap.put(
				"Nessus",
				getScanFilePath("Dynamic", "Nessus",
						"nessus_report_TFTarget.xml"));
		fileMap.put("Arachni",
				getScanFilePath("Dynamic", "Arachni", "php-demo.xml"));
		fileMap.put(
				"WebInspect",
				getScanFilePath("Dynamic", "WebInspect",
						"webinspect-demo-site.xml"));
		fileMap.put("Brakeman",
				getScanFilePath("Static", "Brakeman", "brakeman.json"));
		fileMap.put("Fortify 360",
				getScanFilePath("Static", "Fortify", "ZigguratUtility.fpr"));
		fileMap.put("Acunetix WVS",
				getScanFilePath("Dynamic", "Acunetix", "testaspnet.xml"));
		fileMap.put("Burp Suite",
				getScanFilePath("Dynamic", "Burp", "burp-demo-site.xml"));
		fileMap.put("IBM Rational AppScan Source Edition", null);
	}

	@Before
	public void init() {
		super.init();
		driver = super.getDriver();
		loginPage = LoginPage.open(driver);
	}

	public static URL getScanFilePath(String category, String scannerName,
			String fileName) {
		String string = "SupportingFiles/" + category + "/" + scannerName + "/"
				+ fileName;

		return ClassLoader.getSystemResource(string);// .getFile();
	}

	@After
	public void shutDown() {
		driver.quit();
	}

	@Test
	public void navigationTest() {
		String pageText = loginPage.login("user", "password").clickReportsHeaderLink().getH2Tag();
		assertTrue("Reports Page not found", pageText.contains("Reports"));
	}

	@Test
	public void testCreateBasicApplicationnoscan() {
		String orgName = "testCreateApplicationOrg";
		String appName = "testCreateApplicationApp";
		String urlText = "http://testurl.com";

		// set up an organization
		organizationAddPage = loginPage.login("user", "password")
				.clickAddOrganizationButton();

		organizationAddPage.setNameInput(orgName);

		// add an application
		applicationAddPage = organizationAddPage.clickSubmitButtonValid()
				.clickAddApplicationLink();

		applicationAddPage.setNameInput(appName);
		applicationAddPage.setUrlInput(urlText);
		applicationDetailPage = applicationAddPage.clickAddApplicationButton();

		// Run Trending Report
		driver.findElementById("reportsHeader").click();
		ReportsIndexPage reportsIndexPage = new ReportsIndexPage(driver);
		String PageText = driver.findElementByTagName("h2").getText();
		assertTrue("Reports Page not found", PageText.contains("Reports"));
		reportsIndexPage.fillAllClickSaveReport("Trending Report",
				"testCreateApplicationOrg", "testCreateApplicationApp", "HTML");

		// Generated Report
		generatedReportPage = new GeneratedReportPage(driver);
		generatedReportPage.isTextPresentPageHeader();

		// Navigate to Organization IndexPage
		driver.findElementById("orgHeader").click();
		organizationIndexPage = new OrganizationIndexPage(driver);
		organizationIndexPage.clickOrganizationLink(orgName);

		// Delete and Logout
		organizationDetailPage = new OrganizationDetailPage(driver);
		organizationDetailPage.clickDeleteButton().logout();
	}

	@Ignore // this test consistenly generates OutOfMemoryErrors on my box. We don't want to screw up all the tests.
	@Test
	public void generateAllRpt() {
		String orgName = "testCreateOrg";
		String appName = "testCreataApp";
		String urlText = "http://testurl.com";
		
		//set up an organization
		organizationAddPage = loginPage.login("user", "password").clickAddOrganizationButton();
		
		organizationAddPage.setNameInput(orgName);
		
		boolean first = true;
		
		//add an application
		applicationAddPage = organizationAddPage.clickSubmitButtonValid().clickAddApplicationLink();
		
		applicationAddPage.setNameInput(appName);
		applicationAddPage.setUrlInput(urlText);
		applicationDetailPage = applicationAddPage.clickAddApplicationButton();
		
		
		for (String channel : fileMap.keySet()) {
			if (first) {
				first = false;
				uploadScanPage = applicationDetailPage.clickUploadScanLinkFirstTime()
												 	  .setChannelTypeSelect(channel)
													  .clickAddChannelButton();
													  
			} else {
				uploadScanPage = uploadScanPage.clickAddAnotherChannelLink()
											   .setChannelTypeSelect(channel)
											   .clickAddChannelButton();
			}
		}

		for (Entry<String, URL> mapEntry : fileMap.entrySet()) {
			if (mapEntry.getValue() != null){
				File appScanFile = new File(mapEntry.getValue().getFile());
				assertTrue("The test file did not exist.", appScanFile.exists());
			} else {
				continue;
			}

			uploadScanPage = uploadScanPage
					// clickAddChannelButton()
					.setFileInput(mapEntry.getValue())
					.setChannelSelect(mapEntry.getKey())
					.clickUploadScanButton()
					.clickUploadScanLink();
					
			uploadScanPage.sleep(1000);
		}
		
		//Navigate to Reports Page
		driver.findElementById("reportsHeader").click();
		ReportsIndexPage reportsIndexPage = new ReportsIndexPage(driver);
		String PageText = driver.findElementByTagName("h2").getText();
		assertTrue("Reports Page not found", PageText.contains("Reports"));
		
		//Select Options to Run Trending report
		reportsIndexPage.fillAllClickSaveReport("Trending Report","testCreateOrg", "testCreataApp", "HTML");
		generatedReportPage = new GeneratedReportPage(driver);
		String pageHeader = driver.findElementByTagName("span").getText();
		assertTrue("Trending Report not generated",
				pageHeader.contains("Trending Report"));
		sleep(1000);
		
		// Point In time Report
		reportsIndexPage = generatedReportPage.clickReportsHeaderLink();
		reportsIndexPage.fillAllClickSaveReport("Point in Time Report","testCreateOrg", "testCreataApp", "HTML");
		generatedReportPage = new GeneratedReportPage(driver);
		String pageHeader1 = driver.findElementByTagName("span").getText();
		assertTrue("Point in Time Report not generated",
				pageHeader1.contains("Point in Time Report"));
		sleep(1000);
		
		
		// Vulnerability Progress By type Report
		reportsIndexPage = generatedReportPage.clickReportsHeaderLink();
		generatedReportPage = reportsIndexPage.fillAllClickSaveReport("Vulnerability Progress By Type","testCreateOrg", "testCreataApp", "HTML");
		String pageHeader2 = driver.findElementByTagName("span").getText();
		assertTrue("Vulnerability Progress By type Report not generated",
				pageHeader2.contains("Vulnerability Progress By Type"));
		sleep(1000);

		// Channel Comparison by Vulnerability Type Report
		reportsIndexPage = generatedReportPage.clickReportsHeaderLink();
		reportsIndexPage.fillAllClickSaveReport("Channel Comparison By Vulnerability Types","testCreateOrg", "testCreataApp", "HTML");
		generatedReportPage = new GeneratedReportPage(driver);
		String pageHeader3 = driver.findElementByTagName("span").getText();
		assertTrue("Channel Comparison By Vulnerability Type Report not generated",
				pageHeader3.contains("Channel Comparison By Vulnerability Types"));
		
		sleep(1000);

		// Channel Comparison Summary Report
		reportsIndexPage = generatedReportPage.clickReportsHeaderLink();
		reportsIndexPage.fillAllClickSaveReport("Channel Comparison Summary","testCreateOrg", "testCreataApp", "HTML");
		generatedReportPage = new GeneratedReportPage(driver);
		String pageHeader4 = driver.findElementByTagName("span").getText();
		assertTrue("Channel Comparison Summary Report not generated",
				pageHeader4.contains("Channel Comparison Summary"));
		sleep(1000);	


		// Channel Comparison Detail Report
		reportsIndexPage = generatedReportPage.clickReportsHeaderLink();
		reportsIndexPage.fillAllClickSaveReport("Channel Comparison Detail","testCreateOrg", "testCreataApp", "HTML");
		generatedReportPage = new GeneratedReportPage(driver);
		String pageHeader5 = driver.findElementByTagName("h2").getText();
		assertTrue("Channel Comparison Detail Report not generated",
				pageHeader5.contains("Channel Comparison Detail"));

		sleep(1000);	

		// Monthly Progress Report

		reportsIndexPage = generatedReportPage.clickReportsHeaderLink();
		reportsIndexPage.fillAllClickSaveReport("Monthly Progress Report","testCreateOrg", "testCreataApp", "HTML");
		generatedReportPage = new GeneratedReportPage(driver);
		String pageHeader6 = driver.findElementByTagName("span").getText();
		assertTrue("Monthly Progress Report Report not generated",
				pageHeader6.contains("Monthly Progress Report"));

		sleep(1000);	


		//	Portfolio Report

		reportsIndexPage = generatedReportPage.clickReportsHeaderLink();
		reportsIndexPage.fillAllClickSaveReport("Portfolio Report","testCreateOrg", "testCreataApp", "HTML");
		generatedReportPage = new GeneratedReportPage(driver);
		String pageHeader7 = driver.findElementByTagName("h2").getText();
		assertTrue("Portfolio Report not generated",
				pageHeader7.contains("Portfolio Report"));

		sleep(1000);
	}

	/**
	 * This is a smoke test, to be run on a blank database. It adds a bunch of
	 * apps with random criticalities and scan uploads and then tells you what
	 * the basic statistics should be.
	 * 
	 * Requires human checking.
	 */
	@Ignore
	@Test
	public void portfolioTest() {
		organizationIndexPage = loginPage.login("user", "password");

		int numOrgs = 5;
		int numAppsPerOrg = 5;
		String[] orgs = new String[numOrgs];

		Integer[] appsByCriticality = new Integer[] { 0, 0, 0, 0 };
		Integer[] appsNeverScannedByCriticality = new Integer[] { 0, 0, 0, 0 };

		for (int i = 0; i < numOrgs; i++) {
			orgs[i] = getRandomString(20);
			organizationDetailPage = organizationIndexPage
					.clickAddOrganizationButton().setNameInput(orgs[i])
					.clickSubmitButtonValid();

			for (int j = 0; j < numAppsPerOrg; j++) {
				int index = generator.nextInt(4);
				appsByCriticality[index] += 1;
				applicationDetailPage = organizationDetailPage
						.clickAddApplicationLink()
						.setNameInput(getRandomString(i + 5))
						.setUrlInput("http://dummyurl.com")
						.setCriticalitySelect(criticalities[index])
						.clickAddApplicationButton();

				boolean hasScan = generator.nextBoolean();
				if (hasScan) {
					applicationDetailPage
							.clickUploadScanLinkFirstTime()
							.setChannelTypeSelect(ChannelType.ARACHNI)
							.clickAddChannelButton()
							.setFileInput(
									ScanTests.getScanFilePath("Dynamic",
											"Arachni", "php-demo.xml"))
							.setChannelSelect(ChannelType.ARACHNI)
							.clickUploadScanButton();
				} else {
					appsNeverScannedByCriticality[index] += 1;
				}

				organizationDetailPage = applicationDetailPage
						.clickOrganizationHeaderLink().clickOrganizationLink(
								orgs[i]);

			}

			organizationIndexPage = organizationDetailPage
					.clickOrganizationHeaderLink();
		}

		log.debug("Critical: "
				+ (100.0 * appsNeverScannedByCriticality[3] / appsByCriticality[3]));
		log.debug("High: "
				+ (100.0 * appsNeverScannedByCriticality[2] / appsByCriticality[2]));
		log.debug("Medium: "
				+ (100.0 * appsNeverScannedByCriticality[1] / appsByCriticality[1]));
		log.debug("Low: "
				+ (100.0 * appsNeverScannedByCriticality[0] / appsByCriticality[0]));
	}

	private void sleep(int num) {
		try {
			Thread.sleep(num);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
