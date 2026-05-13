package com.spring.bank.investment.controller;

import com.spring.bank.investment.dto.ApplyProductInvestmentDTO;
import com.spring.bank.investment.dto.InvestmentPositionResponseDTO;
import com.spring.bank.investment.dto.InvestmentProductResponseDTO;
import com.spring.bank.investment.dto.RedeemProductDTO;
import com.spring.bank.investment.service.ApplyProductInvestmentService;
import com.spring.bank.investment.service.InvestmentProductCatalogService;
import com.spring.bank.investment.service.RedeemProductInvestmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/investment")
@RequiredArgsConstructor
public class InvestmentProductController {

    private final InvestmentProductCatalogService investmentProductCatalogService;
    private final ApplyProductInvestmentService applyProductInvestmentService;
    private final RedeemProductInvestmentService redeemProductInvestmentService;

    @GetMapping("/products")
    public ResponseEntity<List<InvestmentProductResponseDTO>> listProducts() {
        return ResponseEntity.ok(investmentProductCatalogService.listAll());
    }

    @PostMapping("/products/apply")
    public ResponseEntity<InvestmentPositionResponseDTO> applyToProduct(@Valid @RequestBody ApplyProductInvestmentDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new InvestmentPositionResponseDTO(applyProductInvestmentService.apply(body)));
    }

    @PostMapping("/products/redeem")
    public ResponseEntity<InvestmentPositionResponseDTO> redeemProduct(@Valid @RequestBody RedeemProductDTO body) {
        return ResponseEntity.ok(new InvestmentPositionResponseDTO(redeemProductInvestmentService.redeem(body)));
    }
}
