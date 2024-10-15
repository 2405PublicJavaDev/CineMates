package com.filmfellows.cinemates.domain.chat.model.vo;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatRoom {
    // 채팅방 정보
    private Integer roomNo;
    private String roomWriter;
    private String roomName;
    private Timestamp roomDate;
    private String roomCategory;

    // 채팅방의 영화정보
    private Integer movieNo;
    private String title;
    private String posterUrl;
    private Integer cinemaLocationCode;
    private String cinemaAddress;
    private Integer cinemaNo;
    private String cinemaName;

    // 회원정보
    private String filePath;
    private String fileRename;
}
