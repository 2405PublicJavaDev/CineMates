package com.filmfellows.cinemates.domain.mypage.model.service;

import com.filmfellows.cinemates.app.mypage.dto.QnaDTO;
import com.filmfellows.cinemates.domain.mypage.model.vo.Qna;
import com.filmfellows.cinemates.domain.mypage.model.vo.QnaFile;
import org.apache.ibatis.session.RowBounds;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MyPageService {
    String selectOrderList();
    List<QnaDTO> selectAllQnaById(Integer currentPage, String memberId, RowBounds rBounds);
    List<QnaDTO> selectAllQna(RowBounds rBounds, Integer currentPage);
    Qna selectOneQnaByNo(int qnaNo);
    QnaFile selectQnaFileByNo(int qnaNo);
    Qna selectOneReplyByNo(Integer parentQnaNo);
    int insertQna(Qna qna, MultipartFile qnaFile) throws IOException;
    int deleteQna(int qnaNo);
    int insertReply(Qna qna);
    int getTotalQnaCountById(String memberId);
    int getTotalQnaCount();

}
