package com.example.backend.board.controller;

import com.example.backend.board.entity.Board;
import com.example.backend.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/board/write")
    public String boardWriteForm(){
        return "board/boardWrite";
    }

    @PostMapping("/board/writePost")
    public String boardWritePost(Board board, @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        boardService.write(board, file);
        return "redirect:/board/list";
    }

    @GetMapping("/board/list")
    public String boardList(Model model) {
        model.addAttribute("list", boardService.boardList());
        return "board/boardList";
    }

    @GetMapping("/board/view")
    public String boardView(Model model, @RequestParam Integer id) {
        model.addAttribute("board", boardService.boardView(id));
        return "board/boardView";
    }

    @GetMapping("/board/delete")
    public String boardDelete(@RequestParam Integer id){
        boardService.delete(id);
        return "redirect:/board/list";
    }

    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model){
        model.addAttribute("board", boardService.boardView(id));
        return "board/boardModify";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable Integer id, Board board,
                              @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {

        Board boardTemp = boardService.boardView(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        boardService.write(boardTemp, file);

        return "redirect:/board/view?id=" + id;
    }
}
