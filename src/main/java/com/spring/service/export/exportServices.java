package com.spring.service.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.spring.repository.BookingRepository;

@Service
public class exportServices {
	@Autowired
	BookingRepository bookingRepository;

	public ResponseEntity<Object> test(Long idBooking) throws IOException {
		List<Object[]> list1 = new ArrayList<>();
		List<Object[]> list2 = new ArrayList<>();
		
		list1 = bookingRepository.exportHoaDon(idBooking);
		list2 = bookingRepository.exportHoaDon2(idBooking);
		
		URL url = new URL("https://character-text.herokuapp.com/convert/string-character");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		con.setReadTimeout(10000);
		con.setConnectTimeout(15000);
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setRequestMethod("GET");
		List<NameValuePair> params = new ArrayList<>();
		System.out.println(list1.size());
		params.add(new BasicNameValuePair("string", (list1.size() <=0 ? "Hóa Đơn" : list1.get(0)[0])+""));

		OutputStream os = con.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
		writer.write(getQuery(params));
		writer.flush();
		writer.close();
		os.close();

		con.connect();
		
		// Create new document
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
		run.setText("Họ tên bệnh nhân: " + (list1.size() <=0 ? ""  : list1.get(0)[0]+""));
		run.addBreak();
		run.setText("Số điện thoại:   " + ( list1.size() <= 0 ? ""  : list1.get(0)[1]+""));
		run.addBreak();
		run.setText("Địa chỉ:  "+ (list1.size()  <= 0 ? ""  : list1.get(0)[2]+""));
		run.addBreak();
		run.addBreak();
		run.setText("Dịch vụ đã khám");

		XWPFTable tb = document.createTable();
		
		CTTblWidth width = tb.getCTTbl().addNewTblPr().addNewTblW();
		width.setType(STTblWidth.DXA);
		width.setW(BigInteger.valueOf(9072));
		
		XWPFTableRow row = tb.getRow(0);
	    tb.setColBandSize(250);
	    row.setHeight(25);
		XWPFTableCell cell = row.getCell(0);
		cell.setText("Tên dịch vụ");
	
		cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
		XWPFTableCell cell1 = row.createCell();
		cell1.setText("Giá");

		if(list2.size() >0 ) {
			for (Object[] obj : list2) {
				XWPFTableRow r = tb.createRow();
				XWPFTableCell c = r.getCell(0);
				c.setText(obj[0]+"");
				c.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				XWPFTableCell c2 = r.getCell(1);
				c2.setText(obj[1]+"");
				c2.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			}
		}

		XWPFParagraph paragraph4 = document.createParagraph();
		run = paragraph4.createRun();
		run.setText("Tổng tiền: " + (list1.size() <= 0 ? ""  : list1.get(0)[3]+" VND"));

		XWPFParagraph ngaythangnam = document.createParagraph();
		ngaythangnam.setIndentationFirstLine(5000);
		run = ngaythangnam.createRun();
		run.setText("Ngày " + java.time.LocalDateTime.now().getDayOfMonth() + " tháng "
				+ java.time.LocalDateTime.now().getMonthValue() + " năm " + java.time.LocalDateTime.now().getYear());
		ngaythangnam.setAlignment(ParagraphAlignment.CENTER);

		XWPFParagraph chuky = document.createParagraph();
		chuky.setIndentationFirstLine(5000);
		chuky.setAlignment(ParagraphAlignment.CENTER);
		run = chuky.createRun();
		run.setText("Bác sĩ:");
		run.addBreak();
		XWPFParagraph chuky2 = document.createParagraph();
		chuky2.	setIndentationFirstLine(5000);
		chuky2.setAlignment(ParagraphAlignment.CENTER);
		run = chuky2.createRun();
		run.setText((list1.size() <=0 ? "" : list1.get(0)[4]+""));

		// Write the Document in file system
		File f = new File("Hoa Don.docx");
		FileOutputStream out = new FileOutputStream(f);
		document.write(out);
		try {
			InputStreamResource resource = new InputStreamResource(new FileInputStream(f));
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", con.getHeaderField("string")+".docx"));
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");
			ResponseEntity<Object> responseEntity = ResponseEntity.ok().headers(headers).contentLength(f.length())
					.contentType(MediaType.parseMediaType("application/txt")).body(resource);

			System.out.println("successfully");
			return responseEntity;
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			out.close();
			document.close();
			con.disconnect();
		}
	}
	
	private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (NameValuePair pair : params) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		return result.toString();
	}
}
