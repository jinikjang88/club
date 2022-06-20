package com.study.club.service;

import com.study.club.dto.NoteDto;
import com.study.club.entity.ClubMember;
import com.study.club.entity.Note;

import java.util.List;

public interface NoteService {

    Long register(NoteDto noteDto);

    NoteDto get(Long num);

    void modify(Long num, NoteDto noteDto);

    void remove(Long num);

    List<NoteDto> getAllWithWriter(String writerEmail);


    default Note dtoToEntity(NoteDto dto) {
        return Note.builder()
                .num(dto.getNum())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(ClubMember.builder().email(dto.getWriterEmail()).build())
                .build();
    }

    default NoteDto entityToDto(Note note) {
        return NoteDto.builder()
                .num(note.getNum())
                .title(note.getTitle())
                .content(note.getContent())
                .writerEmail(note.getWriter().getEmail())
                .regDate(note.getRegDate())
                .modDate(note.getModDate())
                .build();
    }
}
