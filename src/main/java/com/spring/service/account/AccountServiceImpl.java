package com.spring.service.account;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.spring.exception.NotParsableContentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.dto.model.AccountsDTO;
import com.spring.enumeration.VerificationEnum;
import com.spring.exception.NotFoundException;
import com.spring.model.Accounts;
import com.spring.model.VerificationToken;
import com.spring.repository.AccountRepository;
import com.spring.repository.VerificationTokenRepository;
import com.spring.service.email.MailServices;
import com.spring.service.verificationToken.VerificationTokenService;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final VerificationTokenService verificationTokenService;

    private final VerificationTokenRepository verificationTokenRepository;

    private MailServices mailServices;

    @Autowired
    public AccountServiceImpl
            (
                    AccountRepository accountRepository,
                    VerificationTokenService verificationTokenService,
                    VerificationTokenRepository verificationTokenRepository,
                    MailServices mailServices
            ) {
        this.accountRepository = accountRepository;
        this.verificationTokenService = verificationTokenService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.mailServices = mailServices;
    }


    /**
     * DeleteAt :
     * false : active
     * true : blocked
     *
     * @param userDTO
     * @return
     */
    @Override
    public AccountsDTO register(AccountsDTO userDTO) {
        Accounts entity = userDTO.convertDTOToEntity();
        entity.setDeleteAt(false);
        Accounts a = this.accountRepository.save(entity);
        sendRegistrationConfirmationEmail(a);
        return a.convertEntityToDTO();
    }

    @Override
    public AccountsDTO updatePassword(AccountsDTO userDTO) {
        Accounts entity = userDTO.convertDTOToEntity();
        sendResetPasswordEmail(entity);
        return this.accountRepository.save(entity).convertEntityToDTO();
    }
    
    @Override
    public AccountsDTO update(AccountsDTO userDTO) {
    	 Accounts entity =this.accountRepository.save( userDTO.convertDTOToEntity());
    	 return entity.convertEntityToDTO();
    }

    @Override
    public Optional<Accounts> checkIfEmailExistsAndDeletedAt(String email)throws NotParsableContentException {
        Optional<Accounts> account = this.accountRepository.checkIfEmailExistsAndDeletedAt(email);

        if(account.isPresent()){
        Optional<VerificationToken> verificationToken = this.verificationTokenRepository.findByAccountIdAndCerifiedIsNull(account.get().getId());

            if (verificationToken.isPresent()) {
                throw new NotParsableContentException("Account doesn't verify");
            }
        }
        return account;
    }

    @Override
    public Optional<Accounts> checkTelephone(String sdt) {
        return this.accountRepository.findByTelephone(sdt);
    }

    @Override
    public List<AccountsDTO> findAll() {
        List<AccountsDTO> itemsDTO = new ArrayList<>();
        accountRepository.findAllAccountCustomer().stream().forEach(t -> itemsDTO.add(t.convertEntityToDTO()));
        return itemsDTO;
    }

    @Override
    public AccountsDTO findById(Long id) throws NotFoundException {
        Optional<Accounts> entity = accountRepository.findById(id);
        if (entity.isPresent()) {
            return entity.get().convertEntityToDTO();
        }
        throw new NotFoundException("Id ko tồn tại!");
    }

    @Override
    public void delete(Long id) {
        accountRepository.deleteById(id);
    }

    @Override
    public void sendRegistrationConfirmationEmail(Accounts account) {

        VerificationToken verificationToken = this.verificationTokenService.createVerifiedToken();
        verificationToken.setAccounts(account);
        verificationToken.setCreateAt(LocalDateTime.now());
        verificationToken.setType(VerificationEnum.VERIFY_ACCOUNT);
//        https://www.facebook.com/
        String subject = "NHA KHOA SMAIL DENTAL - XÁC MINH TÀI KHOẢN";
//        String body = "localhost:8080/api/v1/accounts/verify?token=" + verificationToken.getToken();
        String body = verificationToken.getToken();
//        this.mailServices.push(account.getEmail(), subject, "<html><body>"
//        		+"<h1>Xin chào </h1>"+account.getEmail()
//        		+"Bạn vừa đăng ký tài trên hệ thống phòng khám Smail Dental <br/> Bấm "
//        		+ "<b><a href='http://localhost:8080/api/v1/accounts/verify?token="+body+"'>vào đây</a></b>"
//        				+ " Để kích hoạt tài khoản </body></html>");
        this.mailServices.push(account.getEmail(), subject,
				"<html xmlns='http://www.w3.org/1999/xhtml'>"
				+ "	<body style='margin: 0; padding: 0;'>"
				+ "	    <table align='center' cellpadding='0' cellspacing='0' width='600'>"
				+ "	        <td align='center' bgcolor='#70bbd9' >"
				+ "	            <img src='https://png.pngtree.com/template/20190717/ourlarge/pngtree-dental-logo-template-vector-blue-image_229151.jpg' alt='Creating Email Magic' width='600' height='300' style='display: block;' />"
				+ "	        </td>"
				+ "	        <tr>"
				+ "             <td><h1 >Xin chào "+account.getEmail()+"</h1></td>"
				+ "	        </tr>"
				+ "	        <tr>"
				+ "             <td>Bạn vừa đăng ký tài trên hệ thống phòng khám Smail Dental</td>"
				+ "	        </tr>"
				+ "	        <tr>"
				+ "             <td >Bấm <b><a style='text-decoration:none' href='http://localhost:8080/api/v1/accounts/verify?token="+body+"'>vào đây</a></b> để kích hoạt tài khoản</td>"
				+ "	        </tr>"
				+ "	    </table>"
				+ "	</body>"
				+ ""
				+ "	</html>");
//        this.mailServices.push(account.getEmail(), subject, body);
        this.verificationTokenRepository.save(verificationToken);

    }

    @Override
    public void sendResetPasswordEmail(Accounts account){
        VerificationToken verificationToken = this.verificationTokenService.createResetPasswordToken();
        verificationToken.setAccounts(account);
        verificationToken.setCreateAt(LocalDateTime.now());
        System.out.println("craeted at : "+verificationToken.getCreateAt());
        verificationToken.setType(VerificationEnum.VERIFY_CHANGE_PASSWORD);

        String subject = "Thay đổi mật khẩu!";
        String body = "http://localhost:8080/api/v1/accounts/verify-change-password?token=" + verificationToken.getToken();

        this.mailServices.push(account.getEmail(), subject, body);
        this.verificationTokenRepository.save(verificationToken);

    }

    @Override
    public boolean verifyAccount(Optional<VerificationToken> verifyToken) {
        LocalDateTime now = LocalDateTime.now();

        if (verifyToken.isPresent() && checkExpiresAt(verifyToken.get().getExpiresAt())) {

            Optional<Accounts> newAccount = this.accountRepository.findById(verifyToken.get().getAccounts().getId());

            this.accountRepository.save(newAccount.get());

            verifyToken.get().setCerified(now);
            this.verificationTokenRepository.save(verifyToken.get());

            return true;
        }

        return false;
    }

    @Override
    public boolean verifyChangePassword(Optional<VerificationToken> checkComfirmToken) {
        LocalDateTime now = LocalDateTime.now();
        if (checkComfirmToken.isPresent() && checkExpiresAt(checkComfirmToken.get().getExpiresAt())) {
            checkComfirmToken.get().setCerified(now);
            this.verificationTokenRepository.save(checkComfirmToken.get());

            return true;
        }

        return false;
    }

    public boolean checkExpiresAt(LocalDateTime expiresAt) {
        LocalDateTime now = LocalDateTime.now();

        if (expiresAt.compareTo(now) == -1) {
            return false;
        }
        return true;
    }
    @Override
    public String getRoleByAccountId(Long id) {
    	return accountRepository.getRoleById(id);
    }

}
