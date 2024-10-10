package com.filmfellows.cinemates.domain.payment.model.mapper;

import com.filmfellows.cinemates.domain.payment.model.vo.PaymentInfo;
import com.filmfellows.cinemates.domain.reservation.model.vo.ReservationDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PaymentMapper {
    void insertPaymentInfo(PaymentInfo paymentInfo);

    List<ReservationDTO> searchPayment(ReservationDTO rDto);

    void insertPaymentInfo2(Map<String, Object> buyerInfo);

    ReservationDTO selectImpUid(String reservationNo);
}
