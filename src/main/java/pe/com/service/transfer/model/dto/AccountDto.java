package pe.com.service.transfer.model.dto;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AccountDto {
	@Id
	private String id;
	private String typeAccount;
	private String numberAccount;
	private int keyAccount;
	private double availableBalanceAccount;
	@DateTimeFormat(pattern = "yyyy-mm-dd")
	private Date dateCreationAccount;
	private String statusAccount;
	private int idClerkCreation;
	private String customerId;
	private String idCard;
	private String idClient;
}

