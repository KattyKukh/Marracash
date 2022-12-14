package data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntityTable {
    private String id;
    private String amount;
    private String created;
    private String status;
    private String transaction_id;
}