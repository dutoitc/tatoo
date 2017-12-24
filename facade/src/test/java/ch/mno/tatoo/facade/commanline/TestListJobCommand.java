package ch.mno.tatoo.facade.commanline;

import ch.mno.tatoo.facade.commandline.commands.ListJobCommand;
import ch.mno.tatoo.facade.commandline.data.Job;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by dutoitc on 11/09/15.
 */
public class TestListJobCommand {

	@Test
	public void testSelectByJobName() throws IOException {
		ListJobCommand cmd = new ListJobCommand(Pattern.compile(".*tionAnnoncesCoinRec.*"));
		cmd.setLastData(IOUtils.toString(getClass().getResourceAsStream("/listJob.txt")));

		List<Job> jobs = cmd.extractData();
		Assert.assertEquals(1, jobs.size());
		Assert.assertEquals("C_SelectionCryptoMonnaie.A_Commun.SelectionAnnoncesCoinRechercheNET", jobs.get(0).getFullName());
	}

	@Test
	public void testSelectByPath() throws IOException {
		ListJobCommand cmd = new ListJobCommand(Pattern.compile(".*D_Routage.*"));
		cmd.setLastData(IOUtils.toString(getClass().getResourceAsStream("/listJob.txt")));

		List<Job> jobs = cmd.extractData();
		Assert.assertEquals(2, jobs.size());
		Assert.assertEquals("D_Routage.A_Commun.SOCLE_TransfertBitcoin", jobs.get(0).getFullName());
	}


	@Test
	public void testSelectByPath2() throws IOException {
		ListJobCommand cmd = new ListJobCommand(Pattern.compile(".*A_Commun.*"));
		cmd.setLastData(IOUtils.toString(getClass().getResourceAsStream("/listJob.txt")));

		List<Job> jobs = cmd.extractData();
		Assert.assertEquals(4, jobs.size());
		Assert.assertEquals("C_SelectionCryptoMonnaie.A_Commun.DetectionDoublonsSysteme", jobs.get(0).getFullName());
	}

	@Test
	public void testSelectByPath3() throws IOException {
		ListJobCommand cmd = new ListJobCommand(Pattern.compile(".*Commun.SelectionAnnoncesCoinRech.*"));
		cmd.setLastData(IOUtils.toString(getClass().getResourceAsStream("/listJob.txt")));

		List<Job> jobs = cmd.extractData();
		Assert.assertEquals(1, jobs.size());
		Assert.assertEquals("C_SelectionCryptoMonnaie.A_Commun.SelectionAnnoncesCoinRechercheNET", jobs.get(0).getFullName());
	}


}
