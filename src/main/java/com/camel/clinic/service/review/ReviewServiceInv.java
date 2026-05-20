package com.camel.clinic.service.review;

import com.camel.clinic.entity.Review;
import com.camel.clinic.repository.ReviewRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ReviewServiceInv extends BaseService<Review, ReviewRepository> {
    public ReviewServiceInv(ReviewRepository repository) {
        super(Review::new, repository);
    }

    @Override
    protected Specification<Review> buildSpec(Map<String, Object> queryParams) {
        return Specification.<Review>unrestricted()
                .and(notDeleted())
                .and(multiFieldEquals(queryParams.get("appointmentId"),
                        new String[]{"appointment", "id"}
                ));
    }

    public ResponseEntity<?> retrieveByAppointmentId(String appointmentId) {
        Map<String, Object> params = new HashMap<>();
        params.put("appointmentId", CommonService.parseToUuid(appointmentId));

        Review review = repository.findOne(buildSpec(params)).orElse(null);

        return ResponseEntity.ok(review);
    }
}