package com.study.club.controller;

import com.study.club.dto.NoteDto;
import com.study.club.service.NoteService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/notes/")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping("")
    public ResponseEntity<Long> register(@RequestBody NoteDto noteDto) {

        Long num = noteService.register(noteDto);
        return new ResponseEntity<>(num, HttpStatus.OK);
    }

    @GetMapping(value = "/{num}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NoteDto> read(@PathVariable("num") Long num) {
        return new ResponseEntity<>(noteService.get(num), HttpStatus.OK);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NoteDto>> getList(@RequestParam("email") String email) {
        return new ResponseEntity<>(noteService.getAllWithWriter(email), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{num}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> remove(@PathVariable("num") Long num) {
        noteService.remove(num);
        return new ResponseEntity<>("removed", HttpStatus.OK);
    }

    @PutMapping(value = "/{num}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NoteDto> modify(@PathVariable("num") Long num, @RequestBody NoteDto noteDto) {
        noteService.modify(num, noteDto);
        NoteDto modifiedNoteDto = noteService.get(num);

        return new ResponseEntity<>(modifiedNoteDto, HttpStatus.OK);
    }
}
