package wit.calc.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wit.calc.common.dto.CalcResponse;
import wit.calc.rest.service.CalcService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/calc")
public class CalcController {

    private CalcService calcService;

    public CalcController(CalcService calcService) {
        this.calcService = calcService;
    }

    @GetMapping("/sum")
    public ResponseEntity<Map<String, BigDecimal>> requestSum(@RequestParam BigDecimal a, @RequestParam BigDecimal b) throws Exception {
        CalcResponse response = calcService.sendRequest(a,b,'+');
        return ResponseEntity.ok()
                .header("X-Request-ID", response.getUid())
                .body(Map.of("result", response.getResult()));
    }

    @GetMapping("/subtraction")
    public ResponseEntity<Map<String, BigDecimal>> requestSubtract(@RequestParam BigDecimal a, @RequestParam BigDecimal b) throws Exception {
        CalcResponse response = calcService.sendRequest(a,b,'-');
        return ResponseEntity.ok()
                .header("X-Request-ID", response.getUid())
                .body(Map.of("result", response.getResult()));
    }

    @GetMapping("/multiplication")
    public ResponseEntity<Map<String, BigDecimal>> requestMultiply(@RequestParam BigDecimal a, @RequestParam BigDecimal b) throws Exception {
        CalcResponse response = calcService.sendRequest(a,b,'*');
        return ResponseEntity.ok()
                .header("X-Request-ID", response.getUid())
                .body(Map.of("result", response.getResult()));
    }

    @GetMapping("/division")
    public ResponseEntity<Map<String, BigDecimal>> requestDivide(@RequestParam BigDecimal a, @RequestParam BigDecimal b) throws Exception {
        CalcResponse response = calcService.sendRequest(a,b,'/');
        return ResponseEntity.ok()
                .header("X-Request-ID", response.getUid())
                .body(Map.of("result", response.getResult()));
    }

}
