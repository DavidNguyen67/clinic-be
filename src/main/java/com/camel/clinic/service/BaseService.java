package com.camel.clinic.service;

import com.camel.clinic.entity.BaseEntity;
import com.camel.clinic.util.MapperUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class BaseService<T extends BaseEntity> {

    protected final Supplier<T> entityFactory;
    protected final ObjectMapper objectMapper;

    public BaseService(Supplier<T> entityFactory) {
//        this.tmfClient = new TmfClient<>(clazz);
        this.entityFactory = entityFactory;
        this.objectMapper = new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Khi disable tính năng này, Jackson sẽ serialize Date thành chuỗi ngày giờ ISO-8601
    }

    /**
     * Hàm tạo mới bản ghi
     *
     * @param data dữ liệu nhận được từ client để tạo mới bản ghi
     * @return dữ liệu bản ghi được tạo mới
     */
    public ResponseEntity<?> create(T data) throws JsonProcessingException {
        T initObject = entityFactory.get();
        MapperUtils.convertModelToEntity(data, initObject);
        log.info("============= BaseService : ======= body : {}", data);
        return new ResponseEntity<>(initObject, HttpStatus.CREATED);
    }
    /**
     * Hàm lấy danh sách bản ghi
     *
     * @param queryParams truy vấn lọc dữ liệu
     * @return danh sách bản ghi truy vấn hợp lệ
     */
    public ResponseEntity<?> list(Map<String, Object> queryParams) throws JsonProcessingException {
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    /**
     * Hàm lấy bản ghi theo id
     *
     * @param id id của bản ghi
     * @param fields danh sách các trường muốn nhận lại
     * @return thông tin bản ghi theo id
     */
    public ResponseEntity<?> retrieve(String id, String fields) throws JsonProcessingException {
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    /**
     * Hàm cập nhật bản ghi theo id
     *
     * @param data dữ liệu nhận được từ client để cập nhật bản ghi
     * @param fields danh sách các trường muốn nhận lại
     * @param params danh sách các trường muốn filter/search
     * @return dữ liệu bản ghi được cập nhật
     */
    public ResponseEntity<?> update(String id, T data, String fields, Map<String, Object> params) throws JsonProcessingException {
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    /**
     * Hàm xóa bản ghi theo id
     *
     * @param id id của bản ghi
     * @return no content
     */
    public ResponseEntity<?> delete(String id) {
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> getByIds(List<String> ids, String fields) throws JsonProcessingException {
        return ResponseEntity.noContent().build();
    }
}
