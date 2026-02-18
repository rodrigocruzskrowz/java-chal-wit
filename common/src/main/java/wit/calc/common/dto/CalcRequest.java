package wit.calc.common.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalcRequest {
    private String uid;
    private char operation;
    private BigDecimal a;
    private BigDecimal b;
}
