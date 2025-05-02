package br.com.equatorial.genesys.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndicatorRequestDto {
	
	private Long id;
	private String name;
	private String value;
	private String status_color;

}
