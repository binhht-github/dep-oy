package com.example.demo.a;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class FiledownloadApplication {

	public static void main(String[] args) {
		SpringApplication.run(FiledownloadApplication.class, args);
	}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public ResponseEntity<Object> downloadFile() throws IOException {
		List<String > li = new ArrayList<>();
		li.add("ádgkjashgdkajsgdkjas");
		li.add("ádgkjashgdkajsgdk45645jas");
		li.add("ádgkjashgdkajsgdk21312jas");
		li.add("ádgkjashgdkajsgdkj12312312as");
		li.add("ádgkjashgdkajsg12312312dkjas");
		li.add("ádgkjashgdkajs");
		li.add("ádgkjashgdkajsgdkja12312312s");
		li.add("ádgkjashgdkajsgdkjasádasdasdasdasd");
		
		
		XWPFDocument document = new XWPFDocument();
	    // Create new Paragraph
	    XWPFParagraph tieude = document.createParagraph();
	    XWPFRun tieudeRun = tieude.createRun();
	    tieudeRun.setText("HÓA ĐƠN");
	    tieudeRun.setFontSize(25);
	    tieudeRun.setBold(true);
	    tieude.setAlignment(ParagraphAlignment.CENTER);
	    tieude.setSpacingAfter(1000);
	    
	    XWPFRun run = tieude.createRun();
	    
	    XWPFParagraph hoten = document.createParagraph();
	    run = hoten.createRun();
	    run.setText("Họ Tên Bệnh Nhân: "+ "Hoàng Thanh Bình");
	    run.addBreak();
	    run.setText("Số điện thoại:  "+"0365719297");
	    run.addBreak();
	    run.setText("Dịch vụ đã khám:  ");
	    
	    XWPFTable tb = document.createTable();
	    XWPFTableRow row = tb.getRow(0);
//	    tb.set
	    XWPFTableCell cell = row.getCell(0);
	    cell.setText("Tên dịch vụ");
	    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
	    XWPFTableCell cell1 = row.createCell();
	    cell1.setText("Giá");
	    
	    for(String s :li) {
	    	XWPFTableRow r = tb.createRow();
	    	XWPFTableCell c = r.getCell(0);
	    	c.setText(s);
		    c.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
		    XWPFTableCell c2 = r.getCell(1);
		    c2.setText(s);
		    c2.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
	    }
	    		
	    
	    
	    XWPFParagraph paragraph4 = document.createParagraph();
	    run = paragraph4.createRun();
	    run.setText("Tổng tiền: "+"5.000.000");
	    
	    XWPFParagraph ngaythangnam = document.createParagraph();
	    run = ngaythangnam.createRun();
	    run.setText("Ngày "+java.time.LocalDateTime.now().getDayOfMonth()+" tháng "+java.time.LocalDateTime.now().getMonthValue()+" năm "+java.time.LocalDateTime.now().getYear() );
	    ngaythangnam.setAlignment(ParagraphAlignment.RIGHT);
	    
	    XWPFParagraph chuky = document.createParagraph();
	    run = chuky.createRun();
	    run.setText("Bác sĩ:");
	    run.addBreak();
	    run.setText("Ký ghi rõ họ tên");
	    chuky.setAlignment(ParagraphAlignment.RIGHT);
	    
	    // Write the Document in file system
	    File f = new File("Hoang-Thanh-Binh.docx");
	    FileOutputStream out = new FileOutputStream(f);
	    document.write(out);
//	    document.get
		
//		FileWriter filewriter = null;
		try {
		
//			CSVData csv1 = new CSVData();
//			csv1.setId("1");
//			csv1.setName("talk2amareswaran");
//			csv1.setNumber("5601");
//
//			CSVData csv2 = new CSVData();
//			csv2.setId("2");
//			csv2.setName("Amareswaran");
//			csv2.setNumber("8710");
//
//			List<CSVData> csvDataList = new ArrayList<>();
//			csvDataList.add(csv1);
//			csvDataList.add(csv2);
//
//			StringBuilder filecontent = new StringBuilder("ID, NAME, NUMBER\n");
//			for (CSVData csv : csvDataList) {
//				filecontent.append(csv.getId()).append(",").append(csv.getName()).append(",").append(csv.getNumber())
//						.append("\n");
//			}
//
//			String filename = "H:\\PDF\\a.csv";
//
//			filewriter = new FileWriter(filename);
//			filewriter.write(filecontent.toString());
//			filewriter.flush();
//
//			File file = new File(filename);

			InputStreamResource resource = new InputStreamResource(new FileInputStream(f));
			
//			InputStreamResource resource = new InputStreamResource(new FileInputStream(new File("demo-apache-apoi-word.docx")));
			HttpHeaders headers = new HttpHeaders();

			headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", f.getName()));
//			headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");

			ResponseEntity<Object> responseEntity = ResponseEntity.ok().headers(headers).contentLength(f.length())
					.contentType(MediaType.parseMediaType("application/txt")).body(resource);
			
		    out.close();
			document.close();
			System.out.println("successfully");
			return responseEntity;
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
//			if (filewriter != null)
//				filewriter.close();
		}
	}
}