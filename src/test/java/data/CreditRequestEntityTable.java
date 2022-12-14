package data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditRequestEntityTable {
    private String id;
    private String bank_id;
    private String created;
    private String status;
}
