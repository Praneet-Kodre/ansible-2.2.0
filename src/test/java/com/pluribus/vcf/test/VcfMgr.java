package com.pluribus.vcf.test;

import com.pluribus.vcf.helper.TestSetup;
import com.jcabi.ssh.SSHByPassword;
import com.jcabi.ssh.Shell;
import com.pluribus.vcf.helper.PageInfra;
import com.pluribus.vcf.helper.SwitchMethods;
import com.pluribus.vcf.pagefactory.VCFLoginPage;
import com.pluribus.vcf.pagefactory.VCFManagerPage;
import com.pluribus.vcf.pagefactory.VCFHomePage;
import com.pluribus.vcf.pagefactory.VCFIaIndexPage;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;
import org.testng.annotations.Parameters;

public class VcfMgr extends TestSetup{
	private VCFHomePage home1;
	private VCFManagerPage vcfMgr1;
	private VCFLoginPage login;
	private String vcfUserName = "admin";
	
	@BeforeClass(alwaysRun = true)
	public void init() throws Exception{
		login = new VCFLoginPage(getDriver());
		home1 = new VCFHomePage(getDriver());
		vcfMgr1 = new VCFManagerPage(getDriver());
	}
	
	@Parameters({"password"})
    @Test(groups = {"smoke","regression"}, description = "Login to VCF as admin  and Change Password")
    public void loginAdmin(@Optional("test123")String password) {
        login.firstlogin(vcfUserName,password);
        login.waitForLogoutButton();
        login.logout();
    }
	
	 @Parameters({"password"})  
	    @Test(groups = {"smoke","regression"},dependsOnMethods = {"loginAdmin"},description = "Login to VCF as test123 After Password Change")
	    public void loginTest123(@Optional("test123")String password) {
	        login.login(vcfUserName, password);
	        login.waitForLogoutButton();
	        assertEquals(getTitle(), "Pluribus Networks VCFcenter");
	        home1.gotoVCFMgr();
	        try {
	        	vcfMgr1.delAllSeedsVcfMgr();
	        } catch (Exception e) {}
	 	}
	
	
/*
	@Parameters({"switchName", "password"})
	@Test(groups={"smoke","regression"},dependsOnMethods={"logintoVcfMgr"},description="Add seed switch")
	public void addSeedSwitchTest(String switchName,@Optional("test123") String password) throws Exception {
		if(vcfMgr1.addSeedSwitch(switchName,password)) {
			com.jcabi.log.Logger.info("vcfMgraddSeedSwitch","Configured and verified seed switch addition");
		} else {
			com.jcabi.log.Logger.error("cfMgraddSeedSwitch","Seed switch configuration failed");
		}
	}

	@Parameters({"vcfIp"})
	@Test(groups={"smoke","regression"},dependsOnMethods={"loginTest123"},description="Cleanup existing ansible config")
	public void cleanZtpTest(String vcfIp) throws Exception {
		vcfMgr1.delAllSeedsVcfMgr();
		if(vcfMgr1.terminateAndCleanZtp(vcfIp)) {
			com.jcabi.log.Logger.info("vcfMgrconfig","Terminated previous instances of ansible and cleaned ZTP");
		} else {
			com.jcabi.log.Logger.error("vcfMgrconfig","Terminating prev instance failed");
		}
		//home1.gotoVCFMgr();
	}
*/	
	 @Parameters({"hostFile", "csvFile", "password", "selectedPlaybook", "gatewayIp"})
		@Test(groups={"smoke","regression"},dependsOnMethods={"loginTest123"},description="Run Ansible playbooks")
		public void runAnsiblePlaybooks(String hostFile, String csvFile, @Optional("test123") String password, String selectedPlaybook, String gatewayIp) throws Exception {
			File file1 = new File(hostFile);
			if(!file1.exists()) {
				com.jcabi.log.Logger.error("runAnsiblePlaybook","hostFile "+hostFile+" doesn't exist");
				return;
			}
			file1 = new File(csvFile);
			if(!file1.exists()) {
				com.jcabi.log.Logger.error("runAnsiblePlaybook","csvFile "+csvFile+" doesn't exist");
				return;
			}
			int expNodeCount = 6;
				if(vcfMgr1.launchZTP(hostFile,csvFile,password,expNodeCount,selectedPlaybook,gatewayIp)) {
					com.jcabi.log.Logger.info("vcfMgrconfig","Fabric creation & "+selectedPlaybook+" playbooks were configured successfully");
				} else {
					com.jcabi.log.Logger.error("vcfMgrconfig","Fabric creation & "+selectedPlaybook+" playbooks were not configured successfully");
				} 
		}	
	/*
	//SSH into server-ext-3a, invoke scrip
	public void convTestScript(String serverName,String scriptName) {
		Shell ss = new Shell.Verbose(
	            new SSHByPassword(
	            	serverName,
	            	22,
	                "root",
	                "test123"
	            )
	        );
		out1 = new Shell.Plain(ss).exec("cd /usr/test-scripts/pod/;");
	}
	*/
}
