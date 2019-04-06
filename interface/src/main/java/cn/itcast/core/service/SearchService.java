package cn.itcast.core.service;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface SearchService {

    public Map<String, Object> search(Map paramMap);
}
