package edu.sru.WebBasedEvaluations.EvalForm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import edu.sru.WebBasedEvaluations.evalform.ComputeRange;
import edu.sru.WebBasedEvaluations.evalform.Question;

@Suite
@SpringBootTest
public class QuestionTest {
	
	static String qText;
	static String qDescription;
	static String qToolTip;
	static String qToolTipVal;
	static String qToolTipMarker;
	static String qResponseType;
	static String qResponse;
	static String contolledByStr;

	static List<String> responseOpt;
	static List<String> responseList;
	static List<ComputeRange> computeRanges;

	static ComputeRange range;
	static int row;
	static int col;
	static int questID;
	static Integer visControlledBy;
	static Integer visControls;

	static boolean required = false;
	
	static Question que;

	@BeforeAll
	static void begin() {
		
		qText = "Text";
		qDescription = "Des";
		qToolTip = "tip";
		qToolTipVal = "val";
		qToolTipMarker = "mark";
		qResponseType = "resType";
		
		row = 5;
		col = 5;
		questID = 8;
		visControlledBy = 10;
		visControls = 15;
		
		responseOpt = new ArrayList<String>();
		responseList = new ArrayList<String>();
		computeRanges = new ArrayList<ComputeRange>();
		
		responseOpt.add("Test");
		responseList.add("Test");
		
		range = new ComputeRange();
		computeRanges.add(range);
		
		que = new Question();
		
		
	}
	
	@Test
	public void getQTextTest() {
		
		que.setQText(qText);
		
		assertEquals(qText, que.getQText());
		
	}
	
	@Test
	public void setQTextTest() {
		
		que.setQText(qText);
		
		assertEquals(qText, que.getQText());
		
	}
	
	@Test
	public void getQDescriptionTest() {
		
		que.setQDescription(qDescription);
		
		assertEquals(qDescription, que.getQDescription());
		
	}
	
	@Test
	public void setQDescriptionTest() {
		
		que.setQDescription(qDescription);
		
		assertEquals(qDescription, que.getQDescription());
		
	}
	
	@Test
	public void getQToolTipTest() {
		
		que.setQToolTip(qToolTip);
		
		assertEquals(qToolTip, que.getQToolTip());
		
	}
	
	@Test
	public void setQToolTipTest() {
		
		que.setQToolTip(qToolTip);
		
		assertEquals(qToolTip, que.getQToolTip());
		
	}
	
	@Test
	public void getQToolTipValTest() {
		
		que.setQToolTipVal(qToolTipVal);
		
		assertEquals(qToolTipVal, que.getQToolTipVal());
		
	}
	
	@Test
	public void setQToolTipValTest() {
		
		que.setQToolTipVal(qToolTipVal);
		
		assertEquals(qToolTipVal, que.getQToolTipVal());
		
	}
	
	@Test
	public void getQToolTipMarkerTest() {
		
		que.setQToolTipMarker(qToolTipMarker);
		
		assertEquals(qToolTipMarker, que.getQToolTipMarker());
		
	}
	
	@Test
	public void setQToolTipMarkerTest() {
		
		que.setQToolTipMarker(qToolTipMarker);
		
		assertEquals(qToolTipMarker, que.getQToolTipMarker());
		
	}
	
	@Test
	public void getQResponseTypeTest() {
		
		que.setQResponseType(qResponseType);
		
		assertEquals(qResponseType, que.getQResponseType());
		
	}
	
	@Test
	public void setQResponseTypeTest() {
		
		que.setQResponseType(qResponseType);
		
		assertEquals(qResponseType, que.getQResponseType());
		
	}
	
	@Test
	public void getRowTest() {
		
		que.setRow(row);
		
		assertEquals(row, que.getRow());
		
	}
	
	@Test
	public void setRowTest() {
		
		que.setRow(row);
		
		assertEquals(row, que.getRow());
		
	}
	
	
	@Test
	public void getColTest() {
		
		que.setCol(col);
		
		assertEquals(col, que.getCol());
		
	}
	
	@Test
	public void setColTest() {
		
		que.setCol(col);
		
		assertEquals(col, que.getCol());
		
	}
	
	@Test
	public void getQuestIDTest() {
		
		que.setQuestID(questID);
		
		assertEquals(questID, que.getQuestID());
		
	}
	
	@Test
	public void setQuestIDTest() {
		
		que.setQuestID(questID);
		
		assertEquals(questID, que.getQuestID());
		
	}
	
	@Test
	public void getQResponseTest() {
		
		que.setQResponse(qResponse);
		
		assertEquals(qResponse, que.getQResponse());
		
	}
	
	@Test
	public void setQResponseTest() {
		
		que.setQResponse(qResponse);
		
		assertEquals(qResponse, que.getQResponse());
		
	}
	
	@Test
	public void getRequiredTest() {
		
		que.setRequired(required);
		
		assertEquals(required, que.getRequired());
		
	}
	
	@Test
	public void setRequiredTest() {
		
		que.setRequired(required);
		
		assertEquals(required, que.getRequired());
		
	}
	
	@Test
	public void getVisControlledByTest() {
		
		que.setVisControlledBy(visControlledBy);
		
		assertEquals(visControlledBy, que.getVisControlledBy());
		
	}
	
	@Test
	public void setVisControlledByTest() {
		
		que.setVisControlledBy(visControlledBy);
		
		assertEquals(visControlledBy, que.getVisControlledBy());
		
	}
	
	@Test
	public void getVisControlsTest() {
		
		que.setVisControls(visControls);
		
		assertEquals(visControls, que.getVisControls());
		
	}
	
	@Test
	public void setVisControlsTest() {
		
		que.setVisControls(visControls);
		
		assertEquals(visControls, que.getVisControls());
		
	}
	
	@Test
	public void getContolledByStrTest() {
		
		que.setContolledByStr(contolledByStr);
		
		assertEquals(contolledByStr, que.getContolledByStr());
		
	}
	
	@Test
	public void setContolledByStrTest() {
		
		que.setContolledByStr(contolledByStr);
		
		assertEquals(contolledByStr, que.getContolledByStr());
		
	}
	
	@Test
	public void addOptionTest() {
		
		que.setOptions(responseOpt);
		
		for(int i = 0; i < responseOpt.size(); i++)
		{
			assertEquals(responseOpt.get(i), que.getOptions().get(i));
		}
		
	}
	
	@Test
	public void setOptionsTest() {
		
		que.setOptions(responseOpt);
		
		for(int i = 0; i < responseOpt.size(); i++)
		{
			assertEquals(responseOpt.get(i), que.getOptions().get(i));
		}
		
		
	}
	
	@Test
	public void getOptionTest() {
		
		assertEquals(que.getOption(0), "Test");
		
	}
	
	@Test
	public void getOptionCountTest() {
		
		assertEquals(que.getOptionCount(), responseOpt.size());
		
	}
	
	@Test
	public void getOptionsTest() {
		
		que.setOptions(responseOpt);
		
		for(int i = 0; i < responseOpt.size(); i++)
		{
			assertEquals(responseOpt.get(i), que.getOptions().get(i));
		}
		
	}
	
	@Test
	public void addResponseTest() {
		
		que.addResponse("Test");
		
		for(int i = 0; i < responseList.size(); i++)
		{
			assertEquals(responseList.get(i), que.getResponse(i));
		}
		
	}
	
	@Test
	public void getResponseTest() {
		
		que.addResponse("Test");
		responseList.add("Test");
		
		for(int i = 0; i < responseList.size() -1; i++)
		{
			assertEquals(responseList.get(i), que.getResponse(i));
		}
		
	}
	
	@Test
	public void getResponsesTest() {
		
		que.addResponse("Test");
		
		for(int i = 0; i < responseList.size(); i++)
		{
			assertEquals(responseList.get(i), que.getResponse(i));
		}
		
	}
	
	@Test
	public void clearResponseListTest() {
		
		que.clearResponseList();
		
		assertTrue(que.getResponses().isEmpty());
		
	}
	
	@Test
	public void clearAllResponsesTest() {
		
		que.clearAllResponses();
		
		assertEquals("", que.getQResponse());
		
	}
	
	@Test
	public void getResponseCountTest() {
		
		responseList.clear();
		
		assertEquals(que.getResponseCount(), responseList.size());
		
	}
	
	@Test
	public void responseListIsEmptyTest() {
		
		que.clearResponseList();
		
		assertTrue(que.responseListIsEmpty());
		
	}
	
	@Test
	public void presentInResponsesTest() {
		
		que.addResponse("Test");
		
		assertTrue(que.presentInResponses("Test"));
		
	}
	
	@Test
	public void setComputeRangesTest() {
		
		que.setComputeRanges(computeRanges);
		
		for (int i = 0; i < computeRanges.size(); i++)
		{
			assertEquals(que.getComputeRange(i), computeRanges.get(i));
		}
		
	}
	
	@Test
	public void addComputeRangeTest() {
		
		que.addComputeRange(range);
		computeRanges.add(range);
		
		for (int i = 0; i < computeRanges.size(); i++)
		{
			assertEquals(que.getComputeRange(i), computeRanges.get(i));
		}
		
	}
	
	@Test
	public void getComputeRangeTest() {
		
		assertEquals(que.getComputeRange(0), computeRanges.get(0));
		
	}
	
	@Test
	public void getComputeRangeCountTest() {
		
		assertEquals(que.getComputeRangeCount(), computeRanges.size());
		
	}
	
	@Test
	public void getComputeRangesTest() {
		
		for (int i = 0; i < computeRanges.size(); i++)
		{
			assertEquals(que.getComputeRange(i), computeRanges.get(i));
		}
		
	}
	
}
