package wit.calc.common.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalcRequest {
    private String uid;
    private char operation;
    @NotNull(message = "Field 'a' must not be null")
    private BigDecimal a;
    @NotNull(message = "Field 'b' must not be null")
    private BigDecimal b;
}
