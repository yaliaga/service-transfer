package pe.com.service.transfer.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;


@Data
@Document(collection = "transfer")
public class Transfer {
	  @Id
	    private String id;
	    private String idAccountOrigin;
	    private String idAccountDestination;
	    private double amount;
	    private double commission;
	    private String description;
	    private Date date;

}
