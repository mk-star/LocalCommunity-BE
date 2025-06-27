package com.example.backend;

import com.example.backend.domain.post.service.PostQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FoundationApplicationTests {
	@Test
	void contextLoads() {
		assertTrue(true);
	}

	@Autowired
	private PostQueryService postQueryService;

	@Test
	void fail_updateViewCount() {
		ExecutorService executor = Executors.newFixedThreadPool(20);

		List<CompletableFuture<Void>> futures = LongStream.range(1, 101)
				.mapToObj(i -> CompletableFuture.runAsync(() ->
						postQueryService.getPost(1L, i), executor))
				.toList();

		CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
		executor.shutdown();
	}
}

