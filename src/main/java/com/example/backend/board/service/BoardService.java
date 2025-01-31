package com.example.backend.board.service;

import com.example.backend.board.entity.Board;
import com.example.backend.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    // 게시글 작성
    public void write(Board board, @RequestParam(name = "file", required = false) MultipartFile file) throws Exception {

        if(file != null) {
            String projectPath = System.getProperty("user.dir") + "\\foundation\\src\\main\\resources\\static\\files";

            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_" + file.getOriginalFilename();

            File saveFile = new File(projectPath, fileName);
            file.transferTo(saveFile);

            board.setFilename(fileName);
            board.setFilepath("/files/" + fileName);
        }

        boardRepository.save(board);
    }

    // 게시글 리스트 처리
    public List<Board> boardList(){
        return boardRepository.findAll();
    }

    // 게시글 불러오기
    public Board boardView(Integer id) {

        return boardRepository.findById(id).get();
    }

    // 게시글 삭제
    public void delete(Integer id) {
        boardRepository.deleteById(id);
    }
}
