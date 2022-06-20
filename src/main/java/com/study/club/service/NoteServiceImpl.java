package com.study.club.service;

import com.study.club.dto.NoteDto;
import com.study.club.entity.Note;
import com.study.club.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    @Override
    public Long register(NoteDto noteDto) {
        Note note = dtoToEntity(noteDto);

        noteRepository.save(note);

        return note.getNum();
    }

    @Override
    public NoteDto get(Long num) {
        Optional<Note> noteOptional = noteRepository.getWithWriter(num);
        return noteOptional.map(this::entityToDto).orElse(null);
    }

    @Override
    public void modify(Long num, NoteDto noteDto) {
        Optional<Note> noteOptional = noteRepository.findById(num);
        if (noteOptional.isPresent()) {
            Note note = noteOptional.get();
            note.changeTitle(noteDto.getTitle());
            note.changeContent(noteDto.getContent());
            noteRepository.save(note);
        }
    }

    @Override
    public void remove(Long num) {
        noteRepository.deleteById(num);
    }

    @Override
    public List<NoteDto> getAllWithWriter(String writerEmail) {
        List<Note> noteList = noteRepository.getList(writerEmail);
        return noteList.stream().map(this::entityToDto).collect(Collectors.toList());
    }
}
