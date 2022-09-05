package io.github.helio.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PedidoDTO {
    private Integer cliente;
    private BigDecimal total;
    private List<ItemPedidoDTO> items;

}


/**
 * {
 *     "cliente" : 1,
 *     "total" : 100,
 *
 *     "items" : [
 *         {
 *             "produto": 1,
 *             "quantidade" : 10
 *         }
 *     ]
 * }
 *
 */