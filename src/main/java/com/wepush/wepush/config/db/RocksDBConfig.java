package com.wepush.wepush.config.db;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class RocksDBConfig {

    @Bean
    public RocksDB Rocksdb() throws RocksDBException, IOException {
        RocksDB.loadLibrary();
        Options options = new Options();
        options.setCreateIfMissing(true);
        options.setCreateMissingColumnFamilies(true);
        String path = System.getProperty("user.dir") + "/config/data";
        if (!Files.isSymbolicLink(Paths.get(path))) Files.createDirectories(Paths.get(path));
        return RocksDB.open(options, path);
    }
}
