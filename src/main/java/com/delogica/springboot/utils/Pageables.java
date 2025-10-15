package com.delogica.springboot.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class Pageables {
  private static final int DEFAULT_SIZE = 20;

  public Pageable withDefaultSort(Pageable pageable, Sort fallback) {
    if (pageable == null) {
      return PageRequest.of(0, DEFAULT_SIZE, fallback);
    }
    if (pageable.getSort().isSorted()) {
      return pageable; 
    }
    return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), fallback);
  }

  public Sort orderDefaultSort() {
    return Sort.by(Sort.Direction.DESC, "orderDate")
        .and(Sort.by(Sort.Direction.DESC, "id"));
  }

  // CUSTOMERS

  public Sort customerDefaultSort() {
    return Sort.by(Sort.Direction.ASC, "fullName")
               .and(Sort.by(Sort.Direction.DESC, "id"));
}


  // PRODUCTS
  public Sort productDefaultSort() {
    return Sort.by(Sort.Direction.ASC, "name")
               .and(Sort.by(Sort.Direction.DESC, "id"));
}
}
