package com.bipocloud.spell.errorhandler.api;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TraceRecordRepository extends MongoRepository<TraceRecord, String> {
}
