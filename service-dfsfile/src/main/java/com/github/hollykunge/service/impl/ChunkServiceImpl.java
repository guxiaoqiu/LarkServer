package com.github.hollykunge.service.impl;

import com.github.hollykunge.entity.Chunk;
import com.github.hollykunge.service.ChunkService;
import org.springframework.stereotype.Service;


/**
 * @description: 文件分块实现
 * @author: dd
 * @since: 2019-08-01
 */
@Service
public class ChunkServiceImpl implements ChunkService {

    @Override
    public boolean checkChunk(String identifier, Integer chunkNumber) {
        return false;
    }
}
