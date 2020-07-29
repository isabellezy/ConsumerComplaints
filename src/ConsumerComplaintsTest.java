import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConsumerComplaintsTest {

	private ConsumerComplaints cc;

	@BeforeEach
	void setUp() {
		cc = new ConsumerComplaints();
	}
	
	@Test
	void testSampleInput() throws IOException, InvalidFormatException{
		BufferedReader reader = new BufferedReader(new FileReader("insight_testsuite/test_1/input/complaints.csv"));
		StringWriter writer = new StringWriter();
		
		cc.createComplaintsReport(reader, writer);
		String result = writer.toString();
		String expectedResult = "\"credit reporting, credit repair services, or other personal consumer reports\",2019,3,2,67\n" + 
				"\"credit reporting, credit repair services, or other personal consumer reports\",2020,1,1,100\n" + 
				"debt collection,2019,1,1,100\n";
		assertEquals(expectedResult, result);
	}
	
	@Test
	void testHeaderMissing() throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader("insight_testsuite/test_header_missing/input/complaints.csv"));
		StringWriter writer = new StringWriter();
		
		assertThrows(InvalidFormatException.class, () -> {
			cc.createComplaintsReport(reader, writer);
		  });
	}
	
	@Test
	void testInputNotMatchHeader() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("insight_testsuite/test_input_not_match_header/input/complaints.csv"));
		StringWriter writer = new StringWriter();
		
		assertThrows(InvalidFormatException.class, () -> {
			cc.createComplaintsReport(reader, writer);
		  });
		
	}
	
	@Test
	void testNewlineInDoublequotes() throws IOException, InvalidFormatException {
		BufferedReader reader = new BufferedReader(new FileReader("insight_testsuite/test_newline_in_doublequotes/input/complaints.csv"));
		StringWriter writer = new StringWriter();
		cc.createComplaintsReport(reader, writer);
		String result = writer.toString();
		String expectedResult = "\"credit reporting, credit repair services, or other personal consumer reports\",2019,3,2,67\n" + 
				"\"credit reporting, credit repair services, or other personal consumer reports\",2020,1,1,100\n" + 
				"debt collection,2019,1,1,100\n";
		assertEquals(expectedResult, result);
	}
	
	@Test
	void testCommaInDoublequotes() throws IOException, InvalidFormatException {
		BufferedReader reader = new BufferedReader(new FileReader("insight_testsuite/test_quotation_in_doublequotes/input/complaints.csv"));
		StringWriter writer = new StringWriter();
		cc.createComplaintsReport(reader, writer);
		String result = writer.toString();
		String expectedResult = "\"credit reporting, credit repair services, or other personal consumer reports\",2019,3,2,67\n" + 
				"\"credit reporting, credit repair services, or other personal consumer reports\",2020,1,1,100\n" + 
				"debt collection,2019,1,1,100\n";
		assertEquals(expectedResult, result);
	}

	@Test
	void testFileNotEndWithNewline() throws IOException, InvalidFormatException {
		BufferedReader reader = new BufferedReader(new FileReader("insight_testsuite/test_file_not_end_with_newline/input/complaints.csv"));
		StringWriter writer = new StringWriter();
		cc.createComplaintsReport(reader, writer);
		String result = writer.toString();
		String expectedResult = "\"credit reporting, credit repair services, or other personal consumer reports\",2019,3,2,67\n" + 
				"\"credit reporting, credit repair services, or other personal consumer reports\",2020,1,1,100\n" + 
				"debt collection,2019,1,1,100\n";
		assertEquals(expectedResult, result);
	}
	


}
