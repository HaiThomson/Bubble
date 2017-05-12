-- MySQL dump 10.13  Distrib 5.5.15, for Win64 (x86)
--
-- Host: localhost    Database: bubble
-- ------------------------------------------------------
-- Server version	5.5.15

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `bubble`
--

-- CREATE DATABASE /*!32312 IF NOT EXISTS*/ `bubble` /*!40100 DEFAULT CHARACTER SET utf8 */;

-- USE `bubble`;

--
-- Table structure for table `common_cache`
--

DROP TABLE IF EXISTS `common_cache`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `common_cache` (
  `cachekey` varchar(255) NOT NULL DEFAULT '',
  `cachevalue` mediumblob NOT NULL,
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'ËøáÊúüÊó∂Èó¥',
  PRIMARY KEY (`cachekey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `common_cache`
--

LOCK TABLES `common_cache` WRITE;
/*!40000 ALTER TABLE `common_cache` DISABLE KEYS */;
/*!40000 ALTER TABLE `common_cache` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `common_cron`
--

DROP TABLE IF EXISTS `common_cron`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `common_cron` (
  `cronid` smallint(6) unsigned NOT NULL AUTO_INCREMENT,
  `type` enum('user','system','other','plugin') NOT NULL DEFAULT 'other',
  `name` varchar(100) NOT NULL DEFAULT '',
  `minute` char(36) NOT NULL DEFAULT '',
  `hour` char(36) NOT NULL DEFAULT '0',
  `day` char(36) NOT NULL DEFAULT '0',
  `month` char(36) NOT NULL DEFAULT '0',
  `weekday` char(36) NOT NULL DEFAULT '0',
  `available` tinyint(2) NOT NULL DEFAULT '0',
  `taskclass` varchar(100) NOT NULL DEFAULT '',
  `taskmethod` varchar(50) NOT NULL DEFAULT '',
  `lastrun` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`cronid`),
  KEY `nextrun` (`available`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `common_cron`
--

LOCK TABLES `common_cron` WRITE;
/*!40000 ALTER TABLE `common_cron` DISABLE KEYS */;
INSERT INTO `common_cron` VALUES (1,'system','ÂÆöÊó∂Ê∏ÖÁêÜSession','*','*/2','*','*','*',1,'source.include.cron.SessionClearTask','clearOutOfDateSession',1493283480),(2,'system','ÂÆöÊó∂Ê∏ÖÁêÜSession','*/1','*','*','*','*',1,'source.include.cron.SessionClearTask','clearOutOfDateSession',1493283480),(3,'system','ÂÆöÊó∂Ê∏ÖÁêÜSession','10-40','*','*','*','*',1,'source.include.cron.SessionClearTask','clearOutOfDateSession',1493282400),(24,'system','ÂÆöÊó∂Ê∏ÖÁêÜSession','10-40','*','*','*','*',1,'source.include.cron.SessionClearTask','clearOutOfDateSession',1493282400);
/*!40000 ALTER TABLE `common_cron` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `common_member`
--

DROP TABLE IF EXISTS `common_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `common_member` (
  `userid` varchar(30) NOT NULL,
  `email` char(40) NOT NULL DEFAULT '',
  `username` char(15) NOT NULL DEFAULT '',
  `password` char(32) NOT NULL DEFAULT '',
  `status` tinyint(2) NOT NULL DEFAULT '0',
  `emailstatus` tinyint(2) NOT NULL DEFAULT '0',
  `groupid` smallint(6) unsigned NOT NULL DEFAULT '0',
  `regdate` int(10) unsigned NOT NULL DEFAULT '0',
  `timeoffset` char(4) NOT NULL DEFAULT '',
  PRIMARY KEY (`userid`),
  UNIQUE KEY `username` (`username`),
  KEY `email` (`email`),
  KEY `groupid` (`groupid`),
  KEY `regdate` (`regdate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `common_member`
--

LOCK TABLES `common_member` WRITE;
/*!40000 ALTER TABLE `common_member` DISABLE KEYS */;
/*!40000 ALTER TABLE `common_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `common_session_struct`
--

DROP TABLE IF EXISTS `common_session_struct`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `common_session_struct` (
  `sessionid` char(32) NOT NULL,
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'ËøáÊúüÊó∂Èó¥',
  `ip1` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `ip2` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `ip3` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `ip4` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `userid` varchar(30) NOT NULL DEFAULT '0',
  `username` char(15) NOT NULL DEFAULT '',
  `groupid` smallint(6) unsigned NOT NULL DEFAULT '0',
  `invisible` tinyint(2) NOT NULL DEFAULT '0',
  `lastactivity` int(10) unsigned NOT NULL DEFAULT '0',
  `actionname` varchar(30) NOT NULL DEFAULT '',
  `cartid` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `xid` mediumint(8) unsigned NOT NULL DEFAULT '0',
  UNIQUE KEY `sid` (`sessionid`),
  KEY `uid` (`userid`)
) ENGINE=MEMORY DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `common_session_struct`
--

LOCK TABLES `common_session_struct` WRITE;
/*!40000 ALTER TABLE `common_session_struct` DISABLE KEYS */;
/*!40000 ALTER TABLE `common_session_struct` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `common_syscache`
--

DROP TABLE IF EXISTS `common_syscache`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `common_syscache` (
  `cachename` varchar(32) NOT NULL,
  `cachedata` mediumblob NOT NULL,
  `dateline` int(10) unsigned NOT NULL COMMENT 'ËøáÊúüÊó∂Èó¥',
  PRIMARY KEY (`cachename`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `common_syscache`
--

LOCK TABLES `common_syscache` WRITE;
/*!40000 ALTER TABLE `common_syscache` DISABLE KEYS */;
INSERT INTO `common_syscache` VALUES ('setting','¨Ì\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0\noltimespansr\0java.lang.Long;ã‰êÃè#ﬂ\0J\0valuexr\0java.lang.NumberÜ¨ïî‡ã\0\0xp\0\0\0\0\0\0\0\nx',1491667478);
/*!40000 ALTER TABLE `common_syscache` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test`
--

DROP TABLE IF EXISTS `test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test` (
  `a` char(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test`
--

LOCK TABLES `test` WRITE;
/*!40000 ALTER TABLE `test` DISABLE KEYS */;
/*!40000 ALTER TABLE `test` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-05-02 16:39:55
