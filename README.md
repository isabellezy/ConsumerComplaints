# Consumer Complaints

##How to run the code
Make sure the input file is `input/complaints.csv`, and then run `./run.sh` under the repository root, and find the result in `output/report.csv`    

##How to run unit tests
Run `./run_unit_test.sh` under the repository root.


## Solution

### CSV parsing 
Java does not have internal CSV processing library, so I wrote my own functions to process CSV file. 

I implemented a tokenizer that reads the next field from the file. To read a record, I keep reading the next token until the end of line is reached.  

To parse the file, first I read the header of the CSV file, and extract the indices of the *date, product, and company* fields.
Then I read the remaining of the input file record by record. Each record is one complaint.

My code can handle the following special cases:  
* Missing critical fields e.g. Date, Company or Product from the header line
* Data records have missing or extra fields. i.e. the number of fields in a data record is not equal to the number of fields in the header
* Escaping quotation symbol (" or ') using "" or ''
* Commas and newlines in quoted fields
* File does not end with a newline  

### Aggregating Data

After a record is read, I put the extracted fields into a TreeMap, and update their statistics.

##### Data Structure - TreeMap
The *key* of TreeMap is a `Product` class which contains product and year fields   
 
The *value* of TreeMap is a `Stats` class which has three fields:   

`totalComplaints`: total number of complaints that happens in this year for this product  
`maxComplaints`: the maximum number of complaints that come from one company  
`CompanyMap`: A HashMap contains information of the company and the number of complaints of this company  

I defined the comparator of the TreeMap so that the key is sorted by product name and year.  

After reading all the complaints from input file, I generate report.csv according to the above TreeMap.
  
**Product and year** are from the Key of the TreeMap, sorted by self-defined comparator  
**Total number of complaints** is from `totalComplaints` variable  
**Highest percentage of complaints** is calculated by `maxComplaints/totalComplaints`   
**Number of companies receiving a complaint** is the size of the `companyMap`


## Implementation Details
I solved this problem in Java.   

There are three java files in ./src package.
* `ConsumerComplaints.java` - Main function of this problem. It reads and cleans the input .csv file,  processes and aggregates data using TreeMap data structure, generates output into report.csv.  
* `ConsumerComplaintsTest.java` - Unit tests for ConsumerComplaints class. Test cases are under ./insight_testsuite/. For example: inishgt_testsuite/test_quotation_in_doublequotes/  
* `InvalidFormatException.java` - Customized exception for invalid input format. Like when there is missing header, or input fields does not match header, etc.  
  
## Unit Tests

I used JUnit5 to implement the unit tests. I included JUnit jars in `JUnitJar` folder.

I have implemented the following test cases:
1. Test for sample input
1. Test when input file does not end with newline  
1. Test when there are newlines inside double quotation
1. Test when there are quotations inside double quotation
1. Test when header is missing, or does not contain all information that I need to solve this problem
1. Test when the content of the complaint does not match the header, or has missing information

