package com.filmfellows.cinemates.domain.chat.model.service;


import com.filmfellows.cinemates.app.chat.dto.*;
import com.filmfellows.cinemates.domain.chat.model.vo.ChatJoin;
import com.filmfellows.cinemates.domain.chat.model.vo.ChatMessage;
import com.filmfellows.cinemates.domain.chat.model.vo.ChatRoom;
import com.filmfellows.cinemates.domain.chat.model.vo.ChatTag;
import com.filmfellows.cinemates.domain.member.model.vo.ProfileImg;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface ChatService {

    /**
     * 담당자 : 이충무
     * 기능 : 전체 영화 정보 조회
     * @return List<ChatRoomMovie>
     */
    List<ChatRoomMovie> selectMovieAll();

    /**
     * 담당자 : 이충무
     * 기능 : 지역 하나의 극장 리스트 조회
     * @return List<CinemaInfoByRegion> (극장 이름 반환)
     */
    List<CinemaInfoByRegion> selectCinemaByRegion(Integer cinemaLocationCode, String movieNo);

    /**
     * 담당자 : 이충무
     * 기능 : 영화별 상영 극장 개수 리스트 조회
     * @return regionAndCinemaCount (영화별 극장 개수 반화)
     */
    List<RegionAndCinemaCount> selectCinemaCountByRegionByMovie(String movieNo);

    /**
     * 담당자 : 이충무
     * 기능 : 채팅방 생성
     * @return int (성공여부)
     */
    int insertChatRoom(ChatRoom chatRoom);

    /**
     * 담당자 : 이충무
     * 기능 : 채팅방 태그 등록
     * @return int (성공여부)
     */
    int insertTag(ChatTag chatTag);

    /**
     * 담당자 : 이충무
     * 기능 : 채팅방 검색 리스트 조회
     * @return List<ChatRoom> (채팅방 검색 리스트)
     */
    Map<String, Object> selectChatRoomList(Integer currentPage, int boardLimit, String tagName, List<String> searchMovieList, List<String> searchRoomList, List<String> searchRegionList, Map<String,String> writerInfo);

    /**
     * 담당자 : 이충무
     * 기능 : 채팅방 태그 조회
     * @return List<ChatTag> (채팅방 태그 리스트)
     */
    List<ChatTag> selectChatTagList(String status);

    /**
     * 담당자 : 이충무
     * 기능 : 채팅방 프로필 조회
     * @return List<ProfileImg> (채팅방 프로필 리스트)
     */
    List<ProfileImg> selectProfileList();


    /**
     * 담당자 : 이충무
     * 기능 : 채팅방 참여 인원 조회
     * @return List<String> 참여 인원 리스트
     */
    List<ChatJoinProfile> selectChatJoinList(Integer roomNo);

    /**
     * 담당자 : 이충무
     * 기능 : 채팅방 참여
     * @return int
     */
    int insertChatJoin(Integer roomNo, String memberId);

    /**
     * 담당자 : 이충무
     * 기능 : 채팅 저장
     * @return int
     */
    int insertChatMessage(ChatMessage chatMessage);

    /**
     * 담당자 : 이충무
     * 기능 : 채팅 기록 조회
     * @return List<chatMessageAndProfile>
     */
    List<chatMessageAndProfile> selectChatMessageList(Timestamp myJoinDate, Integer roomNo);

    /**
     * 담당자 : 이충무
     * 기능 : 채팅방 최초 입장 날짜 조회
     * @return Timestamp
     */
    Timestamp selectMyJoinDate(String memberId, Integer roomNo);

    /**
     * 담당자 : 이충무
     * 기능 : 채팅방 나가기
     * @return Timestamp
     */
    void deleteMemberJoinByRoom(Integer roomNo, String memberId);

    /**
     * 담당자 : 이충무
     * 기능 : onOff 체크
     * @return String
     */
    List<ChatJoinProfile> checkOnOffStatus(Integer roomNo);

    /**
     * 담당자 : 이충무
     * 기능 : onOff 상태 업데이트
     * @return String
     */
    void updateOnOffStatus(Integer roomNo, String memberId, String onOffStatus);

    /**
     * 담당자 : 이충무
     * 기능 : 채팅방 삭제
     * @return String
     */
    int deleteChatRoom(Integer roomNo);

    /**
     * 담당자 : 이충무
     * 기능 : 채팅방 삭제 시 내용도 삭제
     * @return String
     */
    int deleteMessageOfChatRoom(Integer roomNo);

    /**
     * 담당자 : 이충무
     * 기능 : 예매 동의 여부 업데이트
     * @return String
     */
    int updateAcceptStatus(Integer roomNo, String memberId, String acceptStatus);

    /**
     * 담당자 : 이충무
     * 기능 : 극장별 상영관 리스트
     * @return String
     */
    List<finalReserveInfoByTicket> selectScreenByCinema(Integer cinemaNo, Integer movieNo);

    /**
     * 담당자 : 이충무
     * 기능 : 날짜별 상영시간
     * @return String
     */
    List<finalReserveInfoByTicket> selectShowtimeByScreen(Integer cinemaNo, Integer movieNo, String selectedDate);

    /**
     * 담당자 : 이충무
     * 기능 : 예매 동의 여부 체크 리스트
     * @return String
     */
    List<ChatJoin> selectAcceptAll(Integer roomNo);

    /**
     * 담당자 : 이충무
     * 기능 : TOP5 채팅방 리스트 조회
     * @return String
     */
    Map<String, Object> selectChatRoomListByTop(String status);
}
