// package com.project.pricing.config;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Configuration;
// import
// org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
// import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
//
// import com.mongodb.ConnectionString;
// import com.mongodb.MongoClientSettings;
// import com.mongodb.client.MongoClient;
// import com.mongodb.client.MongoClients;
//
/// **
// * MongoDB configuration with geospatial indexing support
// */
// @Configuration
// public class MongoConfig extends AbstractMongoClientConfiguration {
//
// @Value("${data.mongodb.uri}")
// private String mongoUri;
//
// @Override
// protected String getDatabaseName() {
// return "tanaka-db";
// }
//
// @Override
// public MongoClient mongoClient() {
// ConnectionString connectionString = new ConnectionString(mongoUri);
// MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
// .applyConnectionString(connectionString)
// .build();
// return MongoClients.create(mongoClientSettings);
// }
//
// @Override
// public MongoCustomConversions customConversions() {
// return new MongoCustomConversions(java.util.Collections.emptyList());
// }
//
// @Override
// protected boolean autoIndexCreation() {
// return true;
// }
// }
