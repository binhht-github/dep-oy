package com.spring.service.booking;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.dto.model.AccountsDTO;
import com.spring.dto.model.BookingDTO;
import com.spring.dto.model.CustomerProfileDTO;
import com.spring.exception.NotFoundException;
import com.spring.model.Booking;
import com.spring.model.ScheduleTime;
import com.spring.repository.BookingRepository;
import com.spring.repository.ScheduleTimeRepository;
import com.spring.service.account.AccountService;
import com.spring.service.customer.CustomerService;
import com.spring.service.email.MailServices;
import com.spring.service.voucher.VoucherServiceImpl;

@Service
public class BookingServiceImpl implements BookingService {

	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	VoucherServiceImpl voucherServiceImpl;
	@Autowired
	AccountService accountService;
	@Autowired
	CustomerService customerService;
	@Autowired
	MailServices mailServices;
	@Autowired
	ScheduleTimeRepository scheduleTimeRepository;
	
	CustomerProfileDTO customer = new CustomerProfileDTO();
	ScheduleTime s = new ScheduleTime();

	@Override
	public List<BookingDTO> findAll() {
		List<BookingDTO> dtoList = new ArrayList<>();
		List<Booking> entityList = bookingRepository.findAll();
		for (Booking entity : entityList) {
			BookingDTO dto = entity.convertEntityToDTO();
			dtoList.add(dto);
		}
		return dtoList;
	}

	@Override
	public BookingDTO findById(Long id) {
		Optional<Booking> optional = bookingRepository.findById(id);
		if (optional.isPresent()) {
			Booking entity = optional.get();
			BookingDTO dto = entity.convertEntityToDTO();
			return dto;
		}
		return null;
	}

	@Override
	public BookingDTO create(BookingDTO bookingDTO) {
		bookingDTO.setBookingDate(new Date());
		bookingDTO.setStatus(0); // 0-dang cho 1-dat lich thanh cong 2-dat lich that bai
		Booking entity = bookingRepository.save(bookingDTO.convertDTOToEntity());
		
		bookingDTO = entity.convertEntityToDTO();
		String email = null;
		Booking bookingDTO2 = bookingRepository.findById(bookingDTO.getId()).get();
		s=scheduleTimeRepository.test(bookingDTO.getId());
		try {
			customer = customerService.getById(entity.getCustomerProfile().getId());
			email = customerService.getById(this.findById(bookingDTO.getId()).getCustomerProfile().getId())
					.getAccounts().getEmail();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}

//		mailServices.push(email, "NHA KHOA SMAIL DENTAL - TH??NG B??O",
//				"<html>" + "<body>" + "Qu?? kh??ch Y??u c???u ?????t l???ch kh??m nha khoa t???i ph??ng kh??m" + "<br/> Th???i gian: "
//						+ "T??? " + bookingDTO.getScheduleTime().getStart() + " - "
//						+ bookingDTO.getScheduleTime().getEnd() + " Ng??y:"
//						+ bookingDTO.getScheduleTime().getDayOfWeek()
//						+ "<br/> Xin vui l??ng ch??? x??c nh???n c???a ch??ng t??i" + "</body>" + "</html>");
//						
		mailServices.push(email, "NHA KHOA SMAIL DENTAL - TH??NG B??O",
				"<html xmlns='http://www.w3.org/1999/xhtml'>"
				+ "	<body style='margin: 0; padding: 0;'>"
				+ "	    <table align='center' cellpadding='0' cellspacing='0' width='600'>"
				+ "	        <td align='center' bgcolor='#70bbd9' >"
				+ "	            <img src='https://png.pngtree.com/template/20190717/ourlarge/pngtree-dental-logo-template-vector-blue-image_229151.jpg' alt='Creating Email Magic' width='600' height='300' style='display: block;' />"
				+ "	        </td>"
				+ "	        <tr>"
				+ "	            <td bgcolor='#ffffff' style='padding: 40px 30px 40px 30px;'>"
				+ "	                <table cellpadding='0' cellspacing='0' width='100%'>"
				+"                      <tr><td>Xin ch??o "+customer.getFullname()+"</td></tr>"
				+"                      <tr  width='100%'><td colspan='2'>B???n v???a ?????t l???ch ??? ph??ng kh??m Smail Dental</td></tr>"
				+ "	                    <tr>"
				+ "	                        <td width='40%'>"
				+ "                             Th???i gian: "+ s.getDayOfWeek().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
				+ "	                        </td>"
				+ "	                        <td>"
				+ "	                            Khung gi???: "+ "T??? " + s.getStart().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + s.getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
				+ "	                        </td>"
				+ "	                    </tr>"
				+"          			<tr width='100%'>"
				+ "							<td colspan='2'>Xin vui l??ng ch??? x??c nh???n c???a ch??ng t??i</td>"
				+ "         			</tr>"
				+ "						<tr width='100%'>"
				+ "							<td colspan='2'>SMAIL DENTAL N??NG NIU H??M R??NG VI???T</td>"
				+ "						</tr>"
				+ "						<tr width='100%'>"
				+ "							S??T: 0365179297"
				+ "						</tr>"
				+ "						<tr>"
				+ "							<td colspan='2'>?????a ch???: S??? 76, ng?? 66 Nguy???n Ho??ng, Nam T??? Ni??m, H?? N???i </td>"
				+ "						</tr>"
				+ "	                </table>"
				+ "	            </td>"
				+ "	        </tr>"
						
				+ "	    </table>"
				+ "	</body>"
				+ ""
				+ "	</html>");
		return bookingDTO;
	}

	@Override
	public BookingDTO update(BookingDTO bookingDTO) {

		Optional<Booking> optional = bookingRepository.findById(bookingDTO.getId());
		if (optional.isPresent()) {
			
			String email = bookingRepository.findById(bookingDTO.getId()).get().getCustomerProfile().getAccounts()
					.getEmail();

			Booking oldEntity = optional.get();
			String dayOfWeekOld = oldEntity.getScheduleTime().getDayOfWeek()
					.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
			String startOld = oldEntity.getScheduleTime().getStart().format(DateTimeFormatter.ofPattern("HH:mm"));
			String endOld = oldEntity.getScheduleTime().getEnd().format(DateTimeFormatter.ofPattern("HH:mm"));

			Boolean check = false;
			if (bookingDTO.getScheduleTime().getId() != oldEntity.getScheduleTime().getId()) {
				check = true;
//				System.out.printf("okkkkkkk change");
			}

			Booking entity = bookingDTO.convertDTOToEntity();
			bookingDTO = bookingRepository.save(entity).convertEntityToDTO();
//			System.out.println(bookingDTO.getScheduleTime().getDayOfWeek()+"aaaaaaaaaa");

			// g???i mail khi thay ?????i l???ch kh??m
			if (check == true) {
				try {
					s=scheduleTimeRepository.test(bookingDTO.getId());
					customer = customerService.getById(bookingDTO.getCustomerProfile().getId());
				} catch (NotFoundException e) {
					System.out.println("L???i ??? update in bookingImpl services");
					e.printStackTrace();
				}
//				mailServices.push(email, "NHA KHOA SMAIL DENTAL - THAY ?????I THAY ?????I L???CH KH??M",
//						"<html>" + "<body>" + "L???ch kh??m c???a b???n ???? thay ?????i t???: <br/>" + "Ng??y: " + dayOfWeekOld + " "
//								+ "khung gi???: " + startOld + "-" + endOld + "<br/>" + " Chuy???n sang: <br/>"
//								+ "<b>Ng??y: "
//								+ bookingDTO.getScheduleTime().getDayOfWeek()
//										.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
//								+ " " + "khung gi???: "
//								+ bookingDTO.getScheduleTime().getStart().format(DateTimeFormatter.ofPattern("HH:mm"))
//								+ "-"
//								+ bookingDTO.getScheduleTime().getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
//								+ "</body>" + "</html>");
				mailServices.push(email, "NHA KHOA SMAIL DENTAL - THAY ?????I L???CH KH??M",
								"<html xmlns='http://www.w3.org/1999/xhtml'>"
								+ "	<body style='margin: 0; padding: 0;'>"
								+ "	    <table align='center' cellpadding='0' cellspacing='0' width='600'>"
								+ "	        <td align='center' bgcolor='#70bbd9' >"
								+ "	            <img src='https://png.pngtree.com/template/20190717/ourlarge/pngtree-dental-logo-template-vector-blue-image_229151.jpg' alt='Creating Email Magic' width='600' height='300' style='display: block;' />"
								+ "	        </td>"
								+ "	        <tr>"
								+ "	            <td bgcolor='#ffffff' style='padding: 40px 30px 40px 30px;'>"
								+ "	                <table cellpadding='0' cellspacing='0' width='100%'>"
								+"                      <tr><td>Xin ch??o "+customer.getFullname()+"</td></tr>"
								+"                      <tr  width='100%'><td colspan='2'>B???n v???a thay ?????i l???ch kh??m</td></tr>"
								+ "	                    <tr>"
								+ "	                        <td width='40%'>"
								+ "                             Th???i gian: "+ s.getDayOfWeek().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
								+ "	                        </td>"
								+ "	                        <td>"
								+ "	                            Khung gi???: "+ "T??? " + s.getStart().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + s.getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
								+ "	                        </td>"
								+ "	                    </tr>"
								+ "						<tr width='100%'>"
								+ "							<td colspan='2'>SMAIL DENTAL N??NG NIU H??M R??NG VI???T</td>"
								+ "						</tr>"
								+ "						<tr width='100%'>"
								+ "							S??T: 0365179297"
								+ "						</tr>"
								+ "						<tr>"
								+ "							<td colspan='2'>?????a ch???: S??? 76, ng?? 66 Nguy???n Ho??ng, Nam T??? Ni??m, H?? N???i </td>"
								+ "						</tr>"
								+ "	                </table>"
								+ "	            </td>"
								+ "	        </tr>"
										
								+ "	    </table>"
								+ "	</body>"
								+ ""
								+ "	</html>");
//				check=false;
			}


		}
		return bookingDTO;
	}

	@Override
	public BookingDTO updateGhiChu(BookingDTO bookingDTO) {
		Optional<Booking> optional = bookingRepository.findById(bookingDTO.getId());
		if (optional.isPresent()) {

			String email = bookingRepository.findById(bookingDTO.getId())
					.get().getCustomerProfile().getAccounts()
					.getEmail();

			Booking entity = bookingDTO.convertDTOToEntity();
			bookingDTO = bookingRepository.save(entity).convertEntityToDTO();

			// g???i mail khi c?? k???t qu??? kh??m
			if (bookingDTO.getKetqua() != null && !bookingDTO.getKetqua().equals("")) {
				try {
					s=scheduleTimeRepository.test(bookingDTO.getId());
					customer = customerService.getById(bookingDTO.getCustomerProfile().getId());
				} catch (NotFoundException e) {
					System.out.println("L???i ??? update in bookingImpl services");
					e.printStackTrace();
				}

				mailServices.push(email, "NHA KHOA SMAIL DENTAL - K??T QU???",
						"<html xmlns='http://www.w3.org/1999/xhtml'>"
								+ "	<body style='margin: 0; padding: 0;'>"
								+ "	    <table align='center' cellpadding='0' cellspacing='0' width='600'>"
								+ "	        <td align='center' bgcolor='#70bbd9' >"
								+ "	            <img src='https://png.pngtree.com/template/20190717/ourlarge/pngtree-dental-logo-template-vector-blue-image_229151.jpg' alt='Creating Email Magic' width='600' height='300' style='display: block;' />"
								+ "	        </td>"
								+ "	        <tr>"
								+ "	            <td bgcolor='#ffffff' style='padding: 40px 30px 40px 30px;'>"
								+ "	                <table cellpadding='0' cellspacing='0' width='100%'>"
								+"                      <tr><td>Xin ch??o "+customer.getFullname()+"</td></tr>"
								+"                      <tr  width='100%'><td colspan='2'>K???t qu??? kh??m c???a b???n t???i SMAIL DENTAIL</td></tr>"
								+ "	                    <tr>"
								+ "	                        <td width='40%'>"
								+ "                             Th???i gian: "+ s.getDayOfWeek().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
								+ "	                        </td>"
								+ "	                        <td>"
								+ "	                            Khung gi???: "+ "T??? " + s.getStart().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + s.getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
								+ "	                        </td>"
								+ "	                    </tr>"
								+"          			<tr width='40%'>"
								+ "							<td width='40%'>K???t lu???n c???a nha s??: "+ bookingDTO.getKetqua()+"</td>"
								+ "							<td width='60%'>Nha s??: "+ bookingDTO.getDentistProfile().getFullName()+"</td>"
								+ "         			</tr>"
								+ "						<tr width='100%'>"
								+ "							<td colspan='2'>SMAIL DENTAL N??NG NIU H??M R??NG VI???T</td>"
								+ "						</tr>"
								+ "						<tr width='100%'>"
								+ "							S??T: 0365179297"
								+ "						</tr>"
								+ "						<tr>"
								+ "							<td colspan='2'>?????a ch???: S??? 76, ng?? 66 Nguy???n Ho??ng, Nam T??? Ni??m, H?? N???i </td>"
								+ "						</tr>"
								+ "	                </table>"
								+ "	            </td>"
								+ "	        </tr>"

								+ "	    </table>"
								+ "	</body>"
								+ ""
								+ "	</html>");
			}

		}
		return bookingDTO;
	}

	@Override
	public BookingDTO findByScheduleTime(Long id) {
		Optional<Booking> optional = bookingRepository.findByScheduleTimeId(id);
		if (optional.isPresent()) {
			Booking entity = optional.get();
			BookingDTO dto = entity.convertEntityToDTO();
			return dto;
		}
		return null;
	}

	@Override
	public List<BookingDTO> findByCustomerId(Long id) {
		List<BookingDTO> dtoList = new ArrayList<>();
		List<Booking> entityList = bookingRepository.findByCustomerId(id);
		for (Booking entity : entityList) {
			BookingDTO dto = entity.convertEntityToDTO();
			dtoList.add(dto);
		}
		return dtoList;
	}

	@Override
	public List<BookingDTO> findByDentistId(Long id) {
		List<BookingDTO> dtoList = new ArrayList<>();
		List<Booking> entityList = bookingRepository.findByDentistId(id);
		for (Booking entity : entityList) {
			BookingDTO dto = entity.convertEntityToDTO();
			dtoList.add(dto);
		}
		return dtoList;
	}

	// g???i voucher
	@Override
	public BookingDTO updateStatus(Long id, Integer status) {
		Optional<Booking> optional = bookingRepository.findById(id);
//		System.out.println("Email 123 "+bookingRepository.findById(id).get().getCustomerProfile().getAccounts().getEmail());
		BookingDTO dto = new BookingDTO();
		if (optional.isPresent()) {
			Booking entity = optional.get();
			entity.setStatus(status);
			bookingRepository.save(entity);
			dto = entity.convertEntityToDTO();
			String email = bookingRepository.findById(id).get().getCustomerProfile().getAccounts().getEmail();
			if (dto.getKetqua() == null) {
				dto.setKetqua("");
			}
			if (dto.getGhichu() == null) {
				dto.setGhichu("");
			}
			switch (status) {
			case 1:
//				mailServices.push(email, "NHA KHOA SMAIL DENTAL - TH??NG B??O", "<html>" + "<body> <h2>X??c nh???n ?????t l???ch</h2> <br/>"
//						+ "C???m ??n qu?? kh??ch ???? s??? d???ng d???ch v??? c???a ch??ng t??i <br/> Qu?? kh??ch ???? ?????t l???ch kh??m v??o ng??y : "
//						+ new SimpleDateFormat("dd-MM-yyyy").format(dto.getBookingDate()) + "<br/>Khung gi??? kh??m: "
//						+ dto.getScheduleTime().getStart().format(DateTimeFormatter.ofPattern("HH:mm")) + " - "
//						+ dto.getScheduleTime().getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
//						+ "<br/> Qu?? kh??ch nh??? ?????n kh??m ????ng gi???</body>" + "</html>");
//				voucherServiceImpl.sentVoucher(
//						Integer.parseInt(bookingRepository.findById(id).get().getCustomerProfile().getId() + ""),
//						email);
				try {
					s=scheduleTimeRepository.test(dto.getId());
					customer = customerService.getById(dto.getCustomerProfile().getId());
				} catch (NotFoundException e) {
					System.out.println("L???i ??? update in bookingImpl services");
					e.printStackTrace();
				}
				mailServices.push(email, "NHA KHOA SMAIL DENTAL - X??C NH???N L???CH KH??M",
								"<html xmlns='http://www.w3.org/1999/xhtml'>"
								+ "	<body style='margin: 0; padding: 0;'>"
								+ "	    <table align='center' cellpadding='0' cellspacing='0' width='600'>"
								+ "	        <td align='center' bgcolor='#70bbd9' >"
								+ "	            <img src='https://png.pngtree.com/template/20190717/ourlarge/pngtree-dental-logo-template-vector-blue-image_229151.jpg' alt='Creating Email Magic' width='600' height='300' style='display: block;' />"
								+ "	        </td>"
								+ "	        <tr>"
								+ "	            <td bgcolor='#ffffff' style='padding: 40px 30px 40px 30px;'>"
								+ "	                <table cellpadding='0' cellspacing='0' width='100%'>"
								+"                      <tr><td>Xin ch??o "+customer.getFullname()+"</td></tr>"
								+"                      <tr  width='100%'><td colspan='2'>Ch??ng t??i ???? x??c nh???n l???ch kh??m c???a b???n</td></tr>"
								+ "	                    <tr>"
								+ "	                        <td width='40%'>"
								+ "                             Th???i gian:  "+ s.getDayOfWeek().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
								+ "	                        </td>"
								+ "	                        <td>"
								+ "	                            Khung gi???:  "+ "T???:  " + s.getStart().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + s.getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
								+ "	                        </td>"
								+ "	                    </tr>"
								+"          			<tr width='100%'>"
								+ "							<td colspan='2'>Qu?? kh??ch nh??? ?????n kh??m ????ng gi???</td>"
								+ "         			</tr>"
								+ "						<tr width='100%'>"
								+ "							<td colspan='2'>SMAIL DENTAL N??NG NIU H??M R??NG VI???T</td>"
								+ "						</tr>"
								+ "						<tr width='100%'>"
								+ "							S??T: 0365179297"
								+ "						</tr>"
								+ "						<tr>"
								+ "							<td colspan='2'>?????a ch???: S??? 76, ng?? 66 Nguy???n Ho??ng, Nam T??? Ni??m, H?? N???i </td>"
								+ "						</tr>"
								+ "	                </table>"
								+ "	            </td>"
								+ "	        </tr>"
										
								+ "	    </table>"
								+ "	</body>"
								+ ""
								+ "	</html>");
				break;
			case 2:
//				mailServices.push(email, "NHA KHOA SMAIL DENTAL - TH?? C???M ??N",
//						"<html>" + "<body>" + "C???m ??n qu?? kh??ch ???? s??? d???ng d???ch v??? c???a ch??ng t??i, <br/>"
//								+ "k???t qu??? kh??m s??? ???????c c???p nh???t v?? th??ng b??o ?????n b???n s???m nh???t !" + "</body>"
//								+ "</html>");
				try {
					s=scheduleTimeRepository.test(dto.getId());
					customer = customerService.getById(dto.getCustomerProfile().getId());
				} catch (NotFoundException e) {
					System.out.println("L???i ??? update in bookingImpl services");
					e.printStackTrace();
				}
				mailServices.push(email, "NHA KHOA SMAIL DENTAL - TH?? C???M ??N",
								"<html xmlns='http://www.w3.org/1999/xhtml'>"
								+ "	<body style='margin: 0; padding: 0;'>"
								+ "	    <table align='center' cellpadding='0' cellspacing='0' width='600'>"
								+ "	        <td align='center' bgcolor='#70bbd9' >"
								+ "	            <img src='https://png.pngtree.com/template/20190717/ourlarge/pngtree-dental-logo-template-vector-blue-image_229151.jpg' alt='Creating Email Magic' width='600' height='300' style='display: block;' />"
								+ "	        </td>"
								+ "	        <tr>"
								+ "	            <td bgcolor='#ffffff' style='padding: 40px 30px 40px 30px;'>"
								+ "	                <table cellpadding='0' cellspacing='0' width='100%'>"
								+"                      <tr><td>Xin ch??o "+customer.getFullname()+"</td></tr>"
								+"                      <tr  width='100%'><td colspan='2'>C???m ??n qu?? kh??ch ???? s??? d???ng d???ch v??? c???a ch??ng t??i</td></tr>"
								+ "	                    <tr>"
								+ "	                        <td width='40%'>"
								+ "                             Th???i gian:  "+ s.getDayOfWeek().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
								+ "	                        </td>"
								+ "	                        <td>"
								+ "	                            Khung gi???:  "+ "T???:  " + s.getStart().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + s.getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
								+ "	                        </td>"
								+ "	                    </tr>"
								+"          			<tr width='100%'>"
								+ "							<td colspan='2'>K???t qu??? kh??m s??? ???????c c???p nh???t v?? th??ng b??o ?????n b???n s???m nh???t</td>"
								+ "         			</tr>"
								+ "						<tr width='100%'>"
								+ "							<td colspan='2'>SMAIL DENTAL N??NG NIU H??M R??NG VI???T</td>"
								+ "						</tr>"
								+ "						<tr width='100%'>"
								+ "							S??T: 0365179297"
								+ "						</tr>"
								+ "						<tr>"
								+ "							<td colspan='2'>?????a ch???: S??? 76, ng?? 66 Nguy???n Ho??ng, Nam T??? Ni??m, H?? N???i </td>"
								+ "						</tr>"
								+ "	                </table>"
								+ "	            </td>"
								+ "	        </tr>"
										
								+ "	    </table>"
								+ "	</body>"
								+ ""
								+ "	</html>");
				break;
			case 3:
//				mailServices.push(email, "NHA KHOA SMAIL DENTAL - H???Y L???CH",
//						"<html>" + "<body>" + "L???ch kh??m c???a qu?? kh??ch v???a b??? h???y <br/> L?? do h???y: " + dto.getGhichu()
//								+ "</body>" + "</html>");
				
				try {
					s=scheduleTimeRepository.test(dto.getId());
					customer = customerService.getById(dto.getCustomerProfile().getId());
				} catch (NotFoundException e) {
					System.out.println("L???i ??? update in bookingImpl services");
					e.printStackTrace();
				}
				mailServices.push(email, "NHA KHOA SMAIL DENTAL - H???Y L???CH",
								"<html xmlns='http://www.w3.org/1999/xhtml'>"
								+ "	<body style='margin: 0; padding: 0;'>"
								+ "	    <table align='center' cellpadding='0' cellspacing='0' width='600'>"
								+ "	        <td align='center' bgcolor='#70bbd9' >"
								+ "	            <img src='https://png.pngtree.com/template/20190717/ourlarge/pngtree-dental-logo-template-vector-blue-image_229151.jpg' alt='Creating Email Magic' width='600' height='300' style='display: block;' />"
								+ "	        </td>"
								+ "	        <tr>"
								+ "	            <td bgcolor='#ffffff' style='padding: 40px 30px 40px 30px;'>"
								+ "	                <table cellpadding='0' cellspacing='0' width='100%'>"
								+"                      <tr><td>Xin ch??o "+customer.getFullname()+"</td></tr>"
								+"                      <tr  width='100%'><td colspan='2'>L???ch kh??m c???a qu?? kh??ch v???a b??? h???y</td></tr>"
								+"                      <tr  width='100%'><td colspan='2'>L?? do: " +dto.getGhichu()+"</td></tr>"
								+ "	                    <tr>"
								+ "	                        <td width='40%'>"
								+ "                             Th???i gian: "+ s.getDayOfWeek().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
								+ "	                        </td>"
								+ "	                        <td>"
								+ "	                            Khung gi???:  "+ "T???:  " + s.getStart().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + s.getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
								+ "	                        </td>"
								+ "	                    </tr>"
								+"          			<tr width='100%'>"
								+ "							<td colspan='2'>N???u qu?? kh??ch kh??ng h??i l??ng vui l??ng ????ng g??p ?? ki???n v???i ch??ng t??i</td>"
								+ "         			</tr>"
								+ "						<tr width='100%'>"
								+ "							<td colspan='2'>SMAIL DENTAL N??NG NIU H??M R??NG VI???T</td>"
								+ "						</tr>"
								+ "						<tr width='100%'>"
								+ "							S??T: 0365179297"
								+ "						</tr>"
								+ "						<tr>"
								+ "							<td colspan='2'>?????a ch???: S??? 76, ng?? 66 Nguy???n Ho??ng, Nam T??? Ni??m, H?? N???i </td>"
								+ "						</tr>"
								+ "	                </table>"
								+ "	            </td>"
								+ "	        </tr>"
										
								+ "	    </table>"
								+ "	</body>"
								+ ""
								+ "	</html>"); 
				break;
			default:
				try {
					s=scheduleTimeRepository.test(dto.getId());
					customer = customerService.getById(dto.getCustomerProfile().getId());
				} catch (NotFoundException e) {
					System.out.println("L???i ??? update in bookingImpl services");
					e.printStackTrace();
				}
				mailServices.push(email, "NHA KHOA SMAIL DENTAL - TH??NG B??O",
						"<html xmlns='http://www.w3.org/1999/xhtml'>"
						+ "	<body style='margin: 0; padding: 0;'>"
						+ "	    <table align='center' cellpadding='0' cellspacing='0' width='600'>"
						+ "	        <td align='center' bgcolor='#70bbd9' >"
						+ "	            <img src='https://png.pngtree.com/template/20190717/ourlarge/pngtree-dental-logo-template-vector-blue-image_229151.jpg' alt='Creating Email Magic' width='600' height='300' style='display: block;' />"
						+ "	        </td>"
						+ "	        <tr>"
						+ "	            <td bgcolor='#ffffff' style='padding: 40px 30px 40px 30px;'>"
						+ "	                <table cellpadding='0' cellspacing='0' width='100%'>"
						+"                      <tr><td>Xin ch??o "+customer.getFullname()+"</td></tr>"
						+"                      <tr  width='100%'>B???n v???a ?????t l???ch ??? ph??ng kh??m Smail Dental</tr>"
						+ "	                    <tr>"
						+ "	                        <td width='40%'>"
						+ "                             Th???i gian: "+ s.getDayOfWeek().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
						+ "	                        </td>"
						+ "	                        <td>"
						+ "	                            Khung gi???: "+ "T???: " + s.getStart().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + s.getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
						+ "	                        </td>"
						+ "	                    </tr>"
						+"          			<tr width='40%'>"
						+ "							<td colspan='2'>Xin vui l??ng ch??? x??c nh???n c???a c??ng t??i</td>"
						+ "         			</tr>"
						+ "						<tr width='100%'>"
						+ "							<td colspan='2'>SMAIL DENTAL N??NG NIU H??M R??NG VI???T</td>"
						+ "						</tr>"
						+ "						<tr width='100%'>"
						+ "							S??T: 0365179297"
						+ "						</tr>"
						+ "						<tr>"
						+ "							<td colspan='2'>?????a ch???: S??? 76, ng?? 66 Nguy???n Ho??ng, Nam T??? Ni??m, H?? N???i </td>"
						+ "						</tr>"
						+ "	                </table>"
						+ "	            </td>"
						+ "	        </tr>"
								
						+ "	    </table>"
						+ "	</body>"
						+ ""
						+ "	</html>");
				break;
			}
		}
		return dto;
	}

	// check tr??ng l???ch
	@Override
	public Optional<Booking> checkIfScheduleTimeExists(Long dentistId, Long scheduleTimeId) {
		return this.bookingRepository.checkScheduleTimeExists(dentistId, scheduleTimeId);
	}
//    @Override
//    public BookingDTO updateStatus(Long id, Integer status) {
//        Optional<Booking> optional = bookingRepository.findById(id);
//        BookingDTO dto = new BookingDTO();
//        if(optional.isPresent()){
//            Booking entity = optional.get();
//            entity.setStatus(status);
//            bookingRepository.save(entity);
//            dto = entity.convertEntityToDTO();
//            if(status == 1) {
//            	BookingDTO b = findById(id);
////            	voucherServiceImpl.sentVoucher(Integer.parseInt(b.getId() + ""), b.getAccountsDTO().getEmail());
//            	voucherServiceImpl.sentVoucher(122, "binhhtph11879@fpt.edu.vn");
//            }
//        }
//        return dto;
//    }

}
