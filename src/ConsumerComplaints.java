import java.io.*;
import java.util.*;


public class ConsumerComplaints {
	
	public boolean isRecordEnd = false;
	public boolean isFileEnd = false;
	public boolean inQuotation = false;
	//String input = "input/complaints.csv";
	//String output = "output/report.csv";
	//String input = "insight_testsuite/test_1/input/complaints.csv";
	//String output = "insight_testsuite/test_1/output/report.csv";
	
	//use this treemap to store complaints information
	//key of the tree is the Product class which contains product  and year
	//value of the tree is the Stats class which contains company stats information, including total number of complaints, total number of companies, and highest percentage 

	public Map<Product, Stats> complaints = new TreeMap<>((a, b) -> {
		if (a.product.equals(b.product)) {
			return a.year - b.year;
		}
		else {		
			return a.product.compareTo(b.product);
		}
	});
	
	public static void main(String[] args) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
			FileWriter writer = new FileWriter(args[1]);

			ConsumerComplaints instance = new ConsumerComplaints();	
			instance.createComplaintsReport(reader, writer);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		catch (InvalidFormatException ex) {
			ex.getMessage();
		}
		
	}
	
	public void createComplaintsReport(Reader reader, Writer writer) throws IOException, InvalidFormatException {

			
			// getRecord() returns information of each complaint
			// It's return value is a list of fields from each complaint
			
			// First we process header to get the index of date, company and product field
			List<String> header = getRecord(reader);
			
		    int dateField = -1, productField = -1, companyField = -1;
		    
		    for (int i = 0; i < header.size(); i++) {
		    	String field = header.get(i);
		    	switch(field) {
		    		case "Date received" : 
		    			dateField = i;
		    			break;
		    		case "Product" : 
		    			productField = i;
		    			break;
		    		case "Company" : 
		    			companyField = i;
		    			break;
		    		default :
		    			break;
		    	}
		    }
		    // throw exception when column is not found
		    if (companyField == -1 || productField  == -1 || dateField == -1) {
		    	throw new InvalidFormatException("Header does not contain required information.");    	
		    }
		    
		    //process input file content 		    
			while (true) {
		        
				List<String> complaint = getRecord(reader);
				
				// if complaint is null, means we reach file end
				if (complaint == null) break;
				if (complaint.size() != header.size()) {
					throw new InvalidFormatException("Number of fields doesn't match header " + complaint.size() + "!=" + header.size());
				}
				//get the fields that we need to generate report
				String year = complaint.get(dateField).substring(0, 4);
				String product = complaint.get(productField).toLowerCase();
				String company = complaint.get(companyField).toLowerCase();
		 
				//create Product object
				Product p = new Product(product, Integer.parseInt(year));
				
				//add product p into complaints map, update it's company static information 
				complaints.putIfAbsent(p, new Stats());
				Stats companyStats = complaints.get(p);	
				
				Map<String, Integer> companyMap = companyStats.companyMap;
				
				// add company into company map
				companyMap.put(company, companyMap.getOrDefault(company, 0) + 1);
				
				companyStats.totalComplaints++;
				companyStats.maxComplaints = Math.max(companyStats.maxComplaints, companyMap.get(company));

			}
			reader.close();
			
			// read complaints map and write to report
			
			PrintWriter pw  = new PrintWriter(writer);
			for (Map.Entry<Product, Stats> entry : complaints.entrySet()) {
				 Product p = entry.getKey();
				 Stats s = entry.getValue();
				 int percent = (int)Math.round((double)s.maxComplaints / s.totalComplaints * 100);
				 String report = p.product + "," + p.year + "," + s.totalComplaints + "," + s.companyMap.size() + "," + percent;
				 pw.println(report);
				
			}
			pw.close();
			
		
	}

	// getRecord() returns list of fields of each complaint record
	
	public List<String> getRecord(Reader br) throws IOException{
		List<String> record = new ArrayList<>();
		
		// token is the field information between two ',' 
		String token = getToken(br);

		while (token != null) {
			record.add(token);
			token = getToken(br);
		}
		return record.size() == 0 ? null : record;
		
	}
	
	
	//getToken() reads and processes csv file, returns field information between two ","
	//getToken() returns null at the end of one complaint, or at the end of the input file
	
	public String getToken(Reader br) throws IOException{
		if (isFileEnd) {
			return null;
		}
		
		if (isRecordEnd) {
	       	isRecordEnd = false;
			return null;
		}
				
		StringBuilder sb = new StringBuilder();
		
		// read file character by character and append to stringbuilder
		int next; 
		while ( (next = br.read()) != -1) {
			char nextChar = (char) next;
			if (nextChar == '"') {
				// inQuotation is a flag checking if nextChar is inside double quotation
				// if nextChar is quotation, and not inside an existing quotation, inQuotation is true
				if (!inQuotation) {
					inQuotation = true;
					sb.append(nextChar);

				} else {
					
					sb.append(nextChar);
					// check if nextChar is the end of one field, followed by ','
					nextChar = (char)br.read();
					if (nextChar == ',') {
						inQuotation = false;
						return sb.toString();
					}
					else {
						sb.append(nextChar);
					}

				}		
			}
			else if (nextChar == ',') {
				if (inQuotation) {
					sb.append(nextChar);
				}
				else {
					// if not inQuotation, "," means end of the field, return
					return sb.toString();
				}			
			}
			
			// if nextChar is new line, if inQuotation is true, just append to stringbuilder
			// if inQuotation is false, \n means this is the end of one complaint. 
			// we use isRec ordEnd flag to indicate that, so that next time when we call getToken() we can get null.
			
			else if (nextChar == '\n') {
				if (inQuotation) {
					sb.append(nextChar);
				}
				else {
					isRecordEnd = true;
					return sb.toString();
				}
			}
			
			else {
				sb.append(nextChar);
			}
			
		}
		
		isFileEnd = true;
		return sb.length() == 0 ? null : sb.toString();
	}
	
	
	class Product {
		String product;
		int year;
		
		public Product(String product, int year) {
			this.product = product;
			this.year = year;
			
		}
	}

	class Stats {
		int totalComplaints = 0;
		int maxComplaints = 0;
		Map<String, Integer> companyMap = new HashMap<>();
		
		public Stats() {}
		
	}
}
