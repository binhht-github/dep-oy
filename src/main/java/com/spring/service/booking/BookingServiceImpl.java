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

//		mailServices.push(email, "NHA KHOA SMAIL DENTAL - THÔNG BÁO",
//				"<html>" + "<body>" + "Quý khách Yêu cầu đặt lịch khám nha khoa tại phòng khám" + "<br/> Thời gian: "
//						+ "Từ " + bookingDTO.getScheduleTime().getStart() + " - "
//						+ bookingDTO.getScheduleTime().getEnd() + " Ngày:"
//						+ bookingDTO.getScheduleTime().getDayOfWeek()
//						+ "<br/> Xin vui lòng chờ xác nhận của chúng tôi" + "</body>" + "</html>");
//						
		mailServices.push(email, "NHA KHOA SMAIL DENTAL - THÔNG BÁO",
				"<html xmlns='http://www.w3.org/1999/xhtml'>"
				+ "	<body style='margin: 0; padding: 0;'>"
				+ "	    <table align='center' cellpadding='0' cellspacing='0' width='600'>"
				+ "	        <td align='center' bgcolor='#70bbd9' >"
				+ "	            <img src='https://png.pngtree.com/template/20190717/ourlarge/pngtree-dental-logo-template-vector-blue-image_229151.jpg' alt='Creating Email Magic' width='600' height='300' style='display: block;' />"
				+ "	        </td>"
				+ "	        <tr>"
				+ "	            <td bgcolor='#ffffff' style='padding: 40px 30px 40px 30px;'>"
				+ "	                <table cellpadding='0' cellspacing='0' width='100%'>"
				+"                      <tr><td>Xin chào "+customer.getFullname()+"</td></tr>"
				+"                      <tr  width='100%'><td colspan='2'>Bạn vừa đặt lịch ở phòng khám Smail Dental</td></tr>"
				+ "	                    <tr>"
				+ "	                        <td width='40%'>"
				+ "                             Thời gian: "+ s.getDayOfWeek().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
				+ "	                        </td>"
				+ "	                        <td>"
				+ "	                            Khung giờ: "+ "Từ " + s.getStart().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + s.getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
				+ "	                        </td>"
				+ "	                    </tr>"
				+"          			<tr width='100%'>"
				+ "							<td colspan='2'>Xin vui lòng chờ xác nhận của chúng tôi</td>"
				+ "         			</tr>"
				+ "						<tr width='100%'>"
				+ "							<td colspan='2'>SMAIL DENTAL NÂNG NIU HÀM RĂNG VIỆT</td>"
				+ "						</tr>"
				+ "						<tr width='100%'>"
				+ "							SĐT: 0365179297"
				+ "						</tr>"
				+ "						<tr>"
				+ "							<td colspan='2'>Địa chỉ: Số 76, ngõ 66 Nguyễn Hoàng, Nam Từ Niêm, Hà Nội </td>"
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

			// gửi mail khi thay đổi lịch khám
			if (check == true) {
				try {
					s=scheduleTimeRepository.test(bookingDTO.getId());
					customer = customerService.getById(bookingDTO.getCustomerProfile().getId());
				} catch (NotFoundException e) {
					System.out.println("Lỗi ở update in bookingImpl services");
					e.printStackTrace();
				}
//				mailServices.push(email, "NHA KHOA SMAIL DENTAL - THAY ĐỔI THAY ĐỔI LỊCH KHÁM",
//						"<html>" + "<body>" + "Lịch khám của bạn đã thay đồi từ: <br/>" + "Ngày: " + dayOfWeekOld + " "
//								+ "khung giờ: " + startOld + "-" + endOld + "<br/>" + " Chuyển sang: <br/>"
//								+ "<b>Ngày: "
//								+ bookingDTO.getScheduleTime().getDayOfWeek()
//										.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
//								+ " " + "khung giờ: "
//								+ bookingDTO.getScheduleTime().getStart().format(DateTimeFormatter.ofPattern("HH:mm"))
//								+ "-"
//								+ bookingDTO.getScheduleTime().getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
//								+ "</body>" + "</html>");
				mailServices.push(email, "NHA KHOA SMAIL DENTAL - THAY ĐỔI LỊCH KHÁM",
								"<html xmlns='http://www.w3.org/1999/xhtml'>"
								+ "	<body style='margin: 0; padding: 0;'>"
								+ "	    <table align='center' cellpadding='0' cellspacing='0' width='600'>"
								+ "	        <td align='center' bgcolor='#70bbd9' >"
								+ "	            <img src='https://png.pngtree.com/template/20190717/ourlarge/pngtree-dental-logo-template-vector-blue-image_229151.jpg' alt='Creating Email Magic' width='600' height='300' style='display: block;' />"
								+ "	        </td>"
								+ "	        <tr>"
								+ "	            <td bgcolor='#ffffff' style='padding: 40px 30px 40px 30px;'>"
								+ "	                <table cellpadding='0' cellspacing='0' width='100%'>"
								+"                      <tr><td>Xin chào "+customer.getFullname()+"</td></tr>"
								+"                      <tr  width='100%'><td colspan='2'>Bạn vừa thay đổi lịch khám</td></tr>"
								+ "	                    <tr>"
								+ "	                        <td width='40%'>"
								+ "                             Thời gian: "+ s.getDayOfWeek().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
								+ "	                        </td>"
								+ "	                        <td>"
								+ "	                            Khung giờ: "+ "Từ " + s.getStart().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + s.getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
								+ "	                        </td>"
								+ "	                    </tr>"
								+ "						<tr width='100%'>"
								+ "							<td colspan='2'>SMAIL DENTAL NÂNG NIU HÀM RĂNG VIỆT</td>"
								+ "						</tr>"
								+ "						<tr width='100%'>"
								+ "							SĐT: 0365179297"
								+ "						</tr>"
								+ "						<tr>"
								+ "							<td colspan='2'>Địa chỉ: Số 76, ngõ 66 Nguyễn Hoàng, Nam Từ Niêm, Hà Nội </td>"
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

			// gửi mail khi có kết quả khám
			if (bookingDTO.getKetqua() != null && !bookingDTO.getKetqua().equals("")) {
				try {
					s=scheduleTimeRepository.test(bookingDTO.getId());
					customer = customerService.getById(bookingDTO.getCustomerProfile().getId());
				} catch (NotFoundException e) {
					System.out.println("Lỗi ở update in bookingImpl services");
					e.printStackTrace();
				}

				mailServices.push(email, "NHA KHOA SMAIL DENTAL - KÊT QUẢ",
						"<html xmlns='http://www.w3.org/1999/xhtml'>"
								+ "	<body style='margin: 0; padding: 0;'>"
								+ "	    <table align='center' cellpadding='0' cellspacing='0' width='600'>"
								+ "	        <td align='center' bgcolor='#70bbd9' >"
								+ "	            <img src='https://png.pngtree.com/template/20190717/ourlarge/pngtree-dental-logo-template-vector-blue-image_229151.jpg' alt='Creating Email Magic' width='600' height='300' style='display: block;' />"
								+ "	        </td>"
								+ "	        <tr>"
								+ "	            <td bgcolor='#ffffff' style='padding: 40px 30px 40px 30px;'>"
								+ "	                <table cellpadding='0' cellspacing='0' width='100%'>"
								+"                      <tr><td>Xin chào "+customer.getFullname()+"</td></tr>"
								+"                      <tr  width='100%'><td colspan='2'>Kết quả khám của bạn tại SMAIL DENTAIL</td></tr>"
								+ "	                    <tr>"
								+ "	                        <td width='40%'>"
								+ "                             Thời gian: "+ s.getDayOfWeek().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
								+ "	                        </td>"
								+ "	                        <td>"
								+ "	                            Khung giờ: "+ "Từ " + s.getStart().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + s.getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
								+ "	                        </td>"
								+ "	                    </tr>"
								+"          			<tr width='40%'>"
								+ "							<td width='40%'>Kết luận của nha sĩ: "+ bookingDTO.getKetqua()+"</td>"
								+ "							<td width='60%'>Nha sĩ: "+ bookingDTO.getDentistProfile().getFullName()+"</td>"
								+ "         			</tr>"
								+ "						<tr width='100%'>"
								+ "							<td colspan='2'>SMAIL DENTAL NÂNG NIU HÀM RĂNG VIỆT</td>"
								+ "						</tr>"
								+ "						<tr width='100%'>"
								+ "							SĐT: 0365179297"
								+ "						</tr>"
								+ "						<tr>"
								+ "							<td colspan='2'>Địa chỉ: Số 76, ngõ 66 Nguyễn Hoàng, Nam Từ Niêm, Hà Nội </td>"
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

	// gửi voucher
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
//				mailServices.push(email, "NHA KHOA SMAIL DENTAL - THÔNG BÁO", "<html>" + "<body> <h2>Xác nhận đặt lịch</h2> <br/>"
//						+ "Cảm ơn quý khách đã sử dụng dịch vụ của chúng tôi <br/> Quý khách đã đặt lịch khám vào ngày : "
//						+ new SimpleDateFormat("dd-MM-yyyy").format(dto.getBookingDate()) + "<br/>Khung giờ khám: "
//						+ dto.getScheduleTime().getStart().format(DateTimeFormatter.ofPattern("HH:mm")) + " - "
//						+ dto.getScheduleTime().getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
//						+ "<br/> Quý khách nhớ đến khám đúng giờ</body>" + "</html>");
//				voucherServiceImpl.sentVoucher(
//						Integer.parseInt(bookingRepository.findById(id).get().getCustomerProfile().getId() + ""),
//						email);
				try {
					s=scheduleTimeRepository.test(dto.getId());
					customer = customerService.getById(dto.getCustomerProfile().getId());
				} catch (NotFoundException e) {
					System.out.println("Lỗi ở update in bookingImpl services");
					e.printStackTrace();
				}
				mailServices.push(email, "NHA KHOA SMAIL DENTAL - XÁC NHẬN LỊCH KHÁM",
								"<html xmlns='http://www.w3.org/1999/xhtml'>"
								+ "	<body style='margin: 0; padding: 0;'>"
								+ "	    <table align='center' cellpadding='0' cellspacing='0' width='600'>"
								+ "	        <td align='center' bgcolor='#70bbd9' >"
								+ "	            <img src='https://png.pngtree.com/template/20190717/ourlarge/pngtree-dental-logo-template-vector-blue-image_229151.jpg' alt='Creating Email Magic' width='600' height='300' style='display: block;' />"
								+ "	        </td>"
								+ "	        <tr>"
								+ "	            <td bgcolor='#ffffff' style='padding: 40px 30px 40px 30px;'>"
								+ "	                <table cellpadding='0' cellspacing='0' width='100%'>"
								+"                      <tr><td>Xin chào "+customer.getFullname()+"</td></tr>"
								+"                      <tr  width='100%'><td colspan='2'>Chúng tôi đã xác nhận lịch khám của bạn</td></tr>"
								+ "	                    <tr>"
								+ "	                        <td width='40%'>"
								+ "                             Thời gian:  "+ s.getDayOfWeek().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
								+ "	                        </td>"
								+ "	                        <td>"
								+ "	                            Khung giờ:  "+ "Từ:  " + s.getStart().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + s.getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
								+ "	                        </td>"
								+ "	                    </tr>"
								+"          			<tr width='100%'>"
								+ "							<td colspan='2'>Quý khách nhớ đến khám đúng giờ</td>"
								+ "         			</tr>"
								+ "						<tr width='100%'>"
								+ "							<td colspan='2'>SMAIL DENTAL NÂNG NIU HÀM RĂNG VIỆT</td>"
								+ "						</tr>"
								+ "						<tr width='100%'>"
								+ "							SĐT: 0365179297"
								+ "						</tr>"
								+ "						<tr>"
								+ "							<td colspan='2'>Địa chỉ: Số 76, ngõ 66 Nguyễn Hoàng, Nam Từ Niêm, Hà Nội </td>"
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
//				mailServices.push(email, "NHA KHOA SMAIL DENTAL - THƯ CẢM ƠN",
//						"<html>" + "<body>" + "Cảm ơn quý khách đã sử dụng dịch vụ của chúng tôi, <br/>"
//								+ "kết quả khám sẽ được cập nhật và thông báo đến bạn sớm nhất !" + "</body>"
//								+ "</html>");
				try {
					s=scheduleTimeRepository.test(dto.getId());
					customer = customerService.getById(dto.getCustomerProfile().getId());
				} catch (NotFoundException e) {
					System.out.println("Lỗi ở update in bookingImpl services");
					e.printStackTrace();
				}
				mailServices.push(email, "NHA KHOA SMAIL DENTAL - THƯ CẢM ƠN",
								"<html xmlns='http://www.w3.org/1999/xhtml'>"
								+ "	<body style='margin: 0; padding: 0;'>"
								+ "	    <table align='center' cellpadding='0' cellspacing='0' width='600'>"
								+ "	        <td align='center' bgcolor='#70bbd9' >"
								+ "	            <img src='https://png.pngtree.com/template/20190717/ourlarge/pngtree-dental-logo-template-vector-blue-image_229151.jpg' alt='Creating Email Magic' width='600' height='300' style='display: block;' />"
								+ "	        </td>"
								+ "	        <tr>"
								+ "	            <td bgcolor='#ffffff' style='padding: 40px 30px 40px 30px;'>"
								+ "	                <table cellpadding='0' cellspacing='0' width='100%'>"
								+"                      <tr><td>Xin chào "+customer.getFullname()+"</td></tr>"
								+"                      <tr  width='100%'><td colspan='2'>Cảm ơn quý khách đã sử dụng dịch vụ của chúng tôi</td></tr>"
								+ "	                    <tr>"
								+ "	                        <td width='40%'>"
								+ "                             Thời gian:  "+ s.getDayOfWeek().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
								+ "	                        </td>"
								+ "	                        <td>"
								+ "	                            Khung giờ:  "+ "Từ:  " + s.getStart().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + s.getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
								+ "	                        </td>"
								+ "	                    </tr>"
								+"          			<tr width='100%'>"
								+ "							<td colspan='2'>Kết quả khám sẽ được cập nhật và thông báo đến bạn sớm nhất</td>"
								+ "         			</tr>"
								+ "						<tr width='100%'>"
								+ "							<td colspan='2'>SMAIL DENTAL NÂNG NIU HÀM RĂNG VIỆT</td>"
								+ "						</tr>"
								+ "						<tr width='100%'>"
								+ "							SĐT: 0365179297"
								+ "						</tr>"
								+ "						<tr>"
								+ "							<td colspan='2'>Địa chỉ: Số 76, ngõ 66 Nguyễn Hoàng, Nam Từ Niêm, Hà Nội </td>"
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
//				mailServices.push(email, "NHA KHOA SMAIL DENTAL - HỦY LỊCH",
//						"<html>" + "<body>" + "Lịch khám của quý khách vừa bị hủy <br/> Lí do hủy: " + dto.getGhichu()
//								+ "</body>" + "</html>");
				
				try {
					s=scheduleTimeRepository.test(dto.getId());
					customer = customerService.getById(dto.getCustomerProfile().getId());
				} catch (NotFoundException e) {
					System.out.println("Lỗi ở update in bookingImpl services");
					e.printStackTrace();
				}
				mailServices.push(email, "NHA KHOA SMAIL DENTAL - HỦY LỊCH",
								"<html xmlns='http://www.w3.org/1999/xhtml'>"
								+ "	<body style='margin: 0; padding: 0;'>"
								+ "	    <table align='center' cellpadding='0' cellspacing='0' width='600'>"
								+ "	        <td align='center' bgcolor='#70bbd9' >"
								+ "	            <img src='https://png.pngtree.com/template/20190717/ourlarge/pngtree-dental-logo-template-vector-blue-image_229151.jpg' alt='Creating Email Magic' width='600' height='300' style='display: block;' />"
								+ "	        </td>"
								+ "	        <tr>"
								+ "	            <td bgcolor='#ffffff' style='padding: 40px 30px 40px 30px;'>"
								+ "	                <table cellpadding='0' cellspacing='0' width='100%'>"
								+"                      <tr><td>Xin chào "+customer.getFullname()+"</td></tr>"
								+"                      <tr  width='100%'><td colspan='2'>Lịch khám của quý khách vừa bị hủy</td></tr>"
								+"                      <tr  width='100%'><td colspan='2'>Lý do: " +dto.getGhichu()+"</td></tr>"
								+ "	                    <tr>"
								+ "	                        <td width='40%'>"
								+ "                             Thời gian: "+ s.getDayOfWeek().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
								+ "	                        </td>"
								+ "	                        <td>"
								+ "	                            Khung giờ:  "+ "Từ:  " + s.getStart().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + s.getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
								+ "	                        </td>"
								+ "	                    </tr>"
								+"          			<tr width='100%'>"
								+ "							<td colspan='2'>Nếu quý khách không hài lòng vui lòng đóng góp ý kiến với chúng tôi</td>"
								+ "         			</tr>"
								+ "						<tr width='100%'>"
								+ "							<td colspan='2'>SMAIL DENTAL NÂNG NIU HÀM RĂNG VIỆT</td>"
								+ "						</tr>"
								+ "						<tr width='100%'>"
								+ "							SĐT: 0365179297"
								+ "						</tr>"
								+ "						<tr>"
								+ "							<td colspan='2'>Địa chỉ: Số 76, ngõ 66 Nguyễn Hoàng, Nam Từ Niêm, Hà Nội </td>"
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
					System.out.println("Lỗi ở update in bookingImpl services");
					e.printStackTrace();
				}
				mailServices.push(email, "NHA KHOA SMAIL DENTAL - THÔNG BÁO",
						"<html xmlns='http://www.w3.org/1999/xhtml'>"
						+ "	<body style='margin: 0; padding: 0;'>"
						+ "	    <table align='center' cellpadding='0' cellspacing='0' width='600'>"
						+ "	        <td align='center' bgcolor='#70bbd9' >"
						+ "	            <img src='https://png.pngtree.com/template/20190717/ourlarge/pngtree-dental-logo-template-vector-blue-image_229151.jpg' alt='Creating Email Magic' width='600' height='300' style='display: block;' />"
						+ "	        </td>"
						+ "	        <tr>"
						+ "	            <td bgcolor='#ffffff' style='padding: 40px 30px 40px 30px;'>"
						+ "	                <table cellpadding='0' cellspacing='0' width='100%'>"
						+"                      <tr><td>Xin chào "+customer.getFullname()+"</td></tr>"
						+"                      <tr  width='100%'>Bạn vừa đặt lịch ở phòng khám Smail Dental</tr>"
						+ "	                    <tr>"
						+ "	                        <td width='40%'>"
						+ "                             Thời gian: "+ s.getDayOfWeek().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
						+ "	                        </td>"
						+ "	                        <td>"
						+ "	                            Khung giờ: "+ "Từ: " + s.getStart().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + s.getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))
						+ "	                        </td>"
						+ "	                    </tr>"
						+"          			<tr width='40%'>"
						+ "							<td colspan='2'>Xin vui lòng chờ xác nhận của cúng tôi</td>"
						+ "         			</tr>"
						+ "						<tr width='100%'>"
						+ "							<td colspan='2'>SMAIL DENTAL NÂNG NIU HÀM RĂNG VIỆT</td>"
						+ "						</tr>"
						+ "						<tr width='100%'>"
						+ "							SĐT: 0365179297"
						+ "						</tr>"
						+ "						<tr>"
						+ "							<td colspan='2'>Địa chỉ: Số 76, ngõ 66 Nguyễn Hoàng, Nam Từ Niêm, Hà Nội </td>"
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

	// check trùng lịch
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
