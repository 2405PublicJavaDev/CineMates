package com.filmfellows.cinemates.domain.chat.model.serviceImpl;

import com.filmfellows.cinemates.app.chat.dto.*;
import com.filmfellows.cinemates.common.Pagination;
import com.filmfellows.cinemates.domain.chat.model.mapper.ChatMapper;
import com.filmfellows.cinemates.domain.chat.model.service.ChatService;
import com.filmfellows.cinemates.domain.chat.model.vo.*;
import com.filmfellows.cinemates.domain.member.model.vo.ProfileImg;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {

    private ChatMapper cMapper;

    @Autowired
    public ChatServiceImpl(ChatMapper cMapper) {
        this.cMapper = cMapper;
    }


    @Override
    public List<ChatRoomMovie> selectMovieAll() {
        List<ChatRoomMovie> movieList = cMapper.selectMovieAll();
        return movieList;
    }

    @Override
    public List<CinemaInfoByRegion> selectCinemaByRegion(Integer cinemaLocationCode, String movieNo) {
        List<CinemaInfoByRegion> cinemaListByOne = cMapper.selectCinemaByRegion(cinemaLocationCode, movieNo);
        return cinemaListByOne;
    }

    @Override
    public List<RegionAndCinemaCount> selectCinemaCountByRegionByMovie(String movieNo) {
        List<RegionAndCinemaCount> regionAndCinemaCount = cMapper.selectCinemaCountByRegionByMovie(movieNo);
        return regionAndCinemaCount;
    }

    @Override
    public int insertChatRoom(ChatRoom chatRoom) {
        int result = cMapper.insertChatRoom(chatRoom);
        return result;
    }

    @Override
    public int insertTag(ChatTag chatTag) {
        int result = cMapper.insertTag(chatTag);
        return result;
    }

    @Override
    public Map<String, Object> selectChatRoomList(Integer currentPage, int boardLimit, String tagName, List<String> searchMovieList, List<String> searchRoomList, List<String> searchRegionList, Map<String, String> writerInfo) {
        // 전체 채팅방 수 계산
        int totalCount = 0;
        if (writerInfo == null) {
            totalCount = cMapper.getTotalCount(tagName, searchMovieList, searchRoomList, searchRegionList);
        } else if (writerInfo.get("status").equals("all")) {
            totalCount = cMapper.getTotalCount(tagName, searchMovieList, searchRoomList, searchRegionList);
        } else if (writerInfo.get("status").equals("my")) {
//            내 채팅방 리스트 개수
            totalCount = cMapper.getMyTotalCount(writerInfo.get("writer"));
        }


        Pagination pn = new Pagination(totalCount, currentPage, boardLimit);

        int limit = pn.getBoardLimit();
        int offset = (currentPage-1) * limit ;
        RowBounds rowBounds = new RowBounds(offset, limit);

        System.out.println("service : "+searchMovieList+", "+searchRoomList+", "+searchRegionList);


        // 채팅방 리스트 조회
        List<ChatRoom> cList = null;
        if (writerInfo == null) {
            cList = cMapper.selectChatRoomList(rowBounds, tagName, searchMovieList, searchRoomList, searchRegionList);
        } else if(writerInfo.get("status").equals("all")){
            cList = cMapper.selectChatRoomList(rowBounds, tagName, searchMovieList, searchRoomList, searchRegionList);
        }else if(writerInfo.get("status").equals("my")){
//            내 채팅방 리스트 개수
            cList = cMapper.selectMyChatRoomList(rowBounds, writerInfo.get("writer"));
        }

        System.out.println("cList 결과 : " + cList);



        // 채팅방 개설 상대 시간 계산
        List<RelativeTime> relativeTimeList = new ArrayList<>();

        for(ChatRoom item : cList){
            RelativeTime rTime = new RelativeTime();
            // 계산 메소드
            String relativeTime = ChatTimeUtils.getRelativeTime(item.getRoomDate());
            rTime.setRoomDate(item.getRoomDate());
            rTime.setRelativeTime(relativeTime);
            relativeTimeList.add(rTime);
            // 참여인원수 조회
            Integer joinCountByRoomNo = cMapper.selectJoinCountByRoomNo(item.getRoomNo());
            item.setJoinCount(joinCountByRoomNo);
            //최근대화내용조회
            String recentChatContent = cMapper.selectRecentChatContent(item.getRoomNo());
            item.setRecentContent(recentChatContent);
        }

        System.out.println(cList);
        // map 생성
        Map<String, Object> map = new HashMap<>();


        map.put("relativeTimeList", relativeTimeList);
        map.put("cList", cList);
        map.put("pn", pn);

        return map;
    }

    @Override
    public List<ChatTag> selectChatTagList(String status) {
        List<ChatTag> chatTagList = cMapper.selectChatTagList(status);
        return chatTagList;
    }

    @Override
    public List<ProfileImg> selectProfileList() {
        List<ProfileImg> profileList = cMapper.selectProfileList();
        return profileList;
    }

    @Override
    public List<ChatJoinProfile> selectChatJoinList(Integer roomNo) {
        List<ChatJoinProfile> chatJoinList = cMapper.selectChatJoinList(roomNo);
        return chatJoinList;
    }

    @Override
    public int insertChatJoin(Integer roomNo, String memberId) {
        int result = cMapper.insertChatJoin(roomNo, memberId);
        return result;
    }

    @Override
    public int insertChatMessage(ChatMessage chatMessage) {
        int result = cMapper.insertChatMessage(chatMessage);
        return result;
    }

    @Override
    public List<chatMessageAndProfile> selectChatMessageList(Timestamp myJoinDate, Integer roomNo) {
        List<chatMessageAndProfile> chatMessageList = cMapper.selectChatMessageList(myJoinDate, roomNo);
        return chatMessageList;
    }

    @Override
    public Timestamp selectMyJoinDate(String memberId, Integer roomNo) {
        return cMapper.selectMyJoinDate(memberId, roomNo);
    }

    @Override
    public void deleteMemberJoinByRoom(Integer roomNo, String memberId) {
        cMapper.deleteMemberJoinByRoom(roomNo, memberId);
    }

    @Override
    public List<ChatJoinProfile> checkOnOffStatus(Integer roomNo) {
        List<ChatJoinProfile> statusList = cMapper.checkOnOffStatus(roomNo);
        return statusList;
    }

    @Override
    public void updateOnOffStatus(Integer roomNo, String memberId, String onOffStatus) {
        cMapper.updateOnOffStatus(roomNo, memberId, onOffStatus);
    }

    @Override
    public int deleteChatRoom(Integer roomNo) {
        int result = cMapper.deleteChatRoom(roomNo);
        return result;
    }

    @Override
    public int deleteMessageOfChatRoom(Integer roomNo) {
        int result = cMapper.deleteMessageOfChatRoom(roomNo);
        return result;
    }

    @Override
    public int updateAcceptStatus(Integer roomNo, String memberId, String acceptStatus) {
        int result = cMapper.updateAcceptStatus(roomNo, memberId, acceptStatus);
        return result;
    }

    @Override
    public List<finalReserveInfoByTicket> selectScreenByCinema(Integer cinemaNo, Integer movieNo) {
        List<finalReserveInfoByTicket> finalReserveInfoByTicket = cMapper.selectScreenByCinema(cinemaNo, movieNo);
        return finalReserveInfoByTicket;
    }

    @Override
    public List<finalReserveInfoByTicket> selectShowtimeByScreen(Integer cinemaNo, Integer movieNo, String selectedDate) {
        List<finalReserveInfoByTicket> showtimeByScreen = cMapper.selectShowtimeByScreen(cinemaNo, movieNo, selectedDate);
        return showtimeByScreen;
    }

    @Override
    public List<ChatJoin> selectAcceptAll(Integer roomNo) {
        List<ChatJoin> chatJoinAcceptList = cMapper.selectAcceptAll(roomNo);
        return chatJoinAcceptList;
    }

    @Override
    public Map<String, Object> selectChatRoomListByTop(String status) {

        List<ChatRoom> chatRoomListByTop = null;

        if(status.equals("top5")){
            chatRoomListByTop = cMapper.selectChatRoomListByTop();
        }else if(status.equals("joinCountRank")){
            chatRoomListByTop = cMapper.selectChatRoomListByRank();
        }


        // 채팅방 개설 상대 시간 계산
        List<RelativeTime> relativeTimeList = new ArrayList<>();

        for(ChatRoom item : chatRoomListByTop){
            RelativeTime rTime = new RelativeTime();
            // 계산 메소드
            String relativeTime = ChatTimeUtils.getRelativeTime(item.getRoomDate());
            rTime.setRoomDate(item.getRoomDate());
            rTime.setRelativeTime(relativeTime);
            relativeTimeList.add(rTime);

            // 참여인원수 조회
            Integer joinCountByRoomNo = cMapper.selectJoinCountByRoomNo(item.getRoomNo());
            item.setJoinCount(joinCountByRoomNo);
            //최근대화내용조회
            String recentChatContent = cMapper.selectRecentChatContent(item.getRoomNo());
            item.setRecentContent(recentChatContent);
        }



        Map<String, Object> map = new HashMap<>();


        map.put("relativeTimeList", relativeTimeList);
        map.put("chatRoomListByTop", chatRoomListByTop);

        return map;
    }


}
