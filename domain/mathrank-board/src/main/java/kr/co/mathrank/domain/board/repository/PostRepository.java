package kr.co.mathrank.domain.board.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import kr.co.mathrank.domain.board.entity.Post;

public interface PostRepository extends MongoRepository<Post, String> {

}
