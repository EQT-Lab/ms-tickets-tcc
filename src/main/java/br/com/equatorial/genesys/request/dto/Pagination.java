package br.com.equatorial.genesys.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {
	
	 private int current;
     private Integer next;
     private Integer previous;
     private int last;
     private int first;
     private int pageLimit;
     private int totalPage;

}
