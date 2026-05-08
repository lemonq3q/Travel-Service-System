-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: travel
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `travel`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `travel` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `travel`;

--
-- Table structure for table `aircraft`
--

DROP TABLE IF EXISTS `aircraft`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aircraft` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `aircraft_code` varchar(100) DEFAULT NULL COMMENT '航班编号',
  `aircraft_type` varchar(100) DEFAULT NULL COMMENT '飞机类型',
  `as_company` varchar(100) DEFAULT NULL COMMENT '所属航空公司',
  `start_area_code` varchar(100) DEFAULT NULL COMMENT '出发地行政编号',
  `end_area_code` varchar(100) DEFAULT NULL COMMENT '目的地行政编号',
  `start_time` time DEFAULT NULL COMMENT '出发时间',
  `end_time` time DEFAULT NULL COMMENT '到达时间',
  `start_station_name` varchar(100) DEFAULT NULL COMMENT '出发机场名称',
  `end_station_name` varchar(100) DEFAULT NULL COMMENT '到达机场名称',
  `economy_class_price` decimal(10,2) DEFAULT NULL COMMENT '经济舱价格',
  `economy_class_num` int DEFAULT NULL COMMENT '经济舱剩余票数',
  `business_class_price` decimal(10,2) DEFAULT NULL COMMENT '商务舱价格',
  `business_class_num` int DEFAULT NULL COMMENT '商务舱剩余票数',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='飞机表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aircraft`
--

LOCK TABLES `aircraft` WRITE;
/*!40000 ALTER TABLE `aircraft` DISABLE KEYS */;
/*!40000 ALTER TABLE `aircraft` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_aircraft_insert` BEFORE INSERT ON `aircraft` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_aircraft_update` BEFORE UPDATE ON `aircraft` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `aircraft_order`
--

DROP TABLE IF EXISTS `aircraft_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aircraft_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `as_user` bigint DEFAULT NULL COMMENT '关联用户ID',
  `as_aircraft` varchar(100) DEFAULT NULL COMMENT '关联飞机ID',
  `start_station_name` varchar(100) DEFAULT NULL,
  `end_station_name` varchar(100) DEFAULT NULL,
  `type_name` varchar(100) DEFAULT NULL COMMENT '舱位类型',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `is_pay` tinyint DEFAULT NULL COMMENT '0：未支付 1：已支付',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='飞机订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aircraft_order`
--

LOCK TABLES `aircraft_order` WRITE;
/*!40000 ALTER TABLE `aircraft_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `aircraft_order` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_aircraft_order_insert` BEFORE INSERT ON `aircraft_order` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_aircraft_order_update` BEFORE UPDATE ON `aircraft_order` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `guide`
--

DROP TABLE IF EXISTS `guide`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `guide` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `as_user` bigint DEFAULT NULL COMMENT '关联用户ID',
  `title` varchar(100) DEFAULT NULL COMMENT '攻略标题',
  `content` longtext COMMENT '攻略内容',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='攻略表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `guide`
--

LOCK TABLES `guide` WRITE;
/*!40000 ALTER TABLE `guide` DISABLE KEYS */;
INSERT INTO `guide` VALUES (1,1,'测试一下','测试一下\n![](__GUIDE_IMG__24__)\n',1777090568864,1777278822,1,1),(2,1,'测试','哈哈哈哈哈哈哈哈哈\n![](__GUIDE_IMG__38__)\n\n- 条目 1\n- 条目 2\n**加粗文字**',1777278807975,1777278807975,1,0);
/*!40000 ALTER TABLE `guide` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_guide_insert` BEFORE INSERT ON `guide` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_guide_update` BEFORE UPDATE ON `guide` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `guide_img`
--

DROP TABLE IF EXISTS `guide_img`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `guide_img` (
  `as_id` bigint NOT NULL,
  `file_id` bigint NOT NULL,
  `sort_index` int NOT NULL,
  `create_time` bigint DEFAULT NULL,
  `update_time` bigint DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `is_delete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`as_id`,`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `guide_img`
--

LOCK TABLES `guide_img` WRITE;
/*!40000 ALTER TABLE `guide_img` DISABLE KEYS */;
INSERT INTO `guide_img` VALUES (2,38,0,1777278807975,1777278807975,1,0);
/*!40000 ALTER TABLE `guide_img` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_guide_img_insert` BEFORE INSERT ON `guide_img` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_guide_img_update` BEFORE UPDATE ON `guide_img` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `guide_review`
--

DROP TABLE IF EXISTS `guide_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `guide_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `as_guide` bigint DEFAULT NULL COMMENT '关联攻略ID',
  `as_user` bigint DEFAULT NULL COMMENT '关联用户ID',
  `content` varchar(1000) DEFAULT NULL COMMENT '评论内容',
  `like_count` int DEFAULT NULL COMMENT '点赞数',
  `dislike_count` int DEFAULT NULL COMMENT '点踩数',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='攻略评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `guide_review`
--

LOCK TABLES `guide_review` WRITE;
/*!40000 ALTER TABLE `guide_review` DISABLE KEYS */;
INSERT INTO `guide_review` VALUES (1,1,1,'哈哈',0,0,1777090579154,1777090579154,1,0),(2,1,1,'哈哈哈',0,0,1777278768435,1777278768435,1,0),(3,2,1,'测试',0,0,1777278832069,1777278832069,1,0);
/*!40000 ALTER TABLE `guide_review` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_guide_review_insert` BEFORE INSERT ON `guide_review` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_guide_review_update` BEFORE UPDATE ON `guide_review` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `guide_review_reply`
--

DROP TABLE IF EXISTS `guide_review_reply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `guide_review_reply` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `as_guide_review` bigint DEFAULT NULL COMMENT '关联攻略评论ID',
  `as_user` bigint DEFAULT NULL COMMENT '回复人ID',
  `reply_user` bigint DEFAULT NULL COMMENT '被回复人ID',
  `content` varchar(1000) DEFAULT NULL COMMENT '回复内容',
  `like_count` int DEFAULT NULL COMMENT '点赞数',
  `dislike_count` int DEFAULT NULL COMMENT '点踩数',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='攻略评论回复表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `guide_review_reply`
--

LOCK TABLES `guide_review_reply` WRITE;
/*!40000 ALTER TABLE `guide_review_reply` DISABLE KEYS */;
INSERT INTO `guide_review_reply` VALUES (1,1,1,1,'@用户1：666',0,0,1777090585504,1777090585504,1,0),(2,1,1,1,'@用户1：666',0,0,1777090589450,1777090589450,1,0),(3,1,1,1,'@用户1：会更好还会',0,0,1777278763227,1777278763227,1,0);
/*!40000 ALTER TABLE `guide_review_reply` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_guide_review_reply_insert` BEFORE INSERT ON `guide_review_reply` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_guide_review_reply_update` BEFORE UPDATE ON `guide_review_reply` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `hotel`
--

DROP TABLE IF EXISTS `hotel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) DEFAULT NULL COMMENT '酒店名称',
  `address` varchar(100) DEFAULT NULL COMMENT '详细地址描述',
  `area_code` varchar(100) DEFAULT NULL COMMENT '所在地区行政编号',
  `longitude` decimal(9,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(9,6) DEFAULT NULL COMMENT '纬度',
  `hotel_desc` varchar(1000) DEFAULT NULL COMMENT '酒店描述',
  `feature_json` varchar(1000) DEFAULT NULL COMMENT '特色JSON',
  `facility_json` varchar(1000) DEFAULT NULL COMMENT '设施JSON',
  `service_json` varchar(1000) DEFAULT NULL COMMENT '服务JSON',
  `rating` decimal(2,1) DEFAULT '5.0' COMMENT '评分',
  `check_in_time` time DEFAULT NULL COMMENT '入住时间',
  `check_out_time` time DEFAULT NULL COMMENT '退房时间',
  `star_level` tinyint DEFAULT NULL COMMENT '星级',
  `cover_img` bigint DEFAULT NULL COMMENT '封面图的id',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='酒店表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel`
--

LOCK TABLES `hotel` WRITE;
/*!40000 ALTER TABLE `hotel` DISABLE KEYS */;
INSERT INTO `hotel` VALUES (2,'测试','测试','110102000000',116.398268,39.905802,'测试','[\"拍照出片\",\"下午茶\",\"绝佳地理位置\"]','[\"餐厅\",\"24 小时前台\",\"免费 Wi-Fi\"]','[\"充电宝租借服务\",\"会议室预订服务\",\"商务秘书服务\"]',5.0,'17:04:44','17:04:44',4,17,1776859405,1777960476,0,0),(3,'测试2','测试','110101000000',116.397428,39.909230,'测试','[\"拍照出片\",\"下午茶\",\"绝佳地理位置\"]','[\"私人停车场\",\"餐厅\",\"叫醒服务\"]','[\"会议室预订服务\",\"充电宝租借服务\",\"租车服务\"]',5.0,'14:00:00','12:00:00',5,NULL,1776944028,1776945870,1,1),(4,'测试2','测试2','110101000000',116.397428,39.909230,'测试2','[]','[]','[]',5.0,'14:00:00','12:00:00',5,NULL,1776945250,1776945870,1,1),(5,'测试2','测试2','110101000000',116.397428,39.909230,'','[]','[]','[]',5.0,'14:00:00','12:00:00',5,18,1776945492,1777960476,0,0),(6,'测试3','测试3','110101000000',116.397428,39.909230,'测试3','[\"绝美城市夜景\",\"丰盛早餐\",\"自助洗衣房\"]','[\"餐厅\",\"24 小时前台\",\"会议室\",\"商务中心\"]','[]',4.3,'14:00:00','12:00:00',5,19,1776945726,1777960476,0,0),(7,'测试测试测试','测试测试测试','110101000000',116.397428,39.909230,'测试测试测试','[\"丰盛早餐\",\"自助洗衣房\",\"专业健身室\",\"休闲咖啡厅\",\"贴心管家服务\"]','[\"私人停车场\",\"餐厅\",\"24 小时前台\",\"免费 Wi-Fi\",\"电梯\",\"行李寄存\"]','[\"客房送餐服务\",\"24 小时客房服务\",\"夜床服务\",\"免费瓶装水服务\"]',5.0,'14:00:00','12:00:00',5,25,1777278593,1777960476,0,0);
/*!40000 ALTER TABLE `hotel` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_hotel_insert` BEFORE INSERT ON `hotel` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_hotel_update` BEFORE UPDATE ON `hotel` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `hotel_img`
--

DROP TABLE IF EXISTS `hotel_img`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_img` (
  `as_id` bigint NOT NULL COMMENT '酒店ID',
  `file_id` bigint NOT NULL COMMENT '文件ID',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`as_id`,`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='酒店图片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel_img`
--

LOCK TABLES `hotel_img` WRITE;
/*!40000 ALTER TABLE `hotel_img` DISABLE KEYS */;
INSERT INTO `hotel_img` VALUES (2,17,1777041798,1777041798,NULL,0),(3,4,1776944053,1776944053,NULL,0),(4,6,1776945250,1776945250,1,0),(5,18,1777040370,1777040370,NULL,0),(6,19,1777040372,1777040372,NULL,0),(6,20,1777040372,1777040372,NULL,0),(7,25,1777278593,1777278593,1,0),(7,26,1777278593,1777278593,1,0),(7,27,1777278593,1777278593,1,0),(7,28,1777278593,1777278593,1,0),(7,29,1777278593,1777278593,1,0),(7,30,1777278593,1777278593,1,0),(7,31,1777278593,1777278593,1,0);
/*!40000 ALTER TABLE `hotel_img` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_hotel_img_insert` BEFORE INSERT ON `hotel_img` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_hotel_img_update` BEFORE UPDATE ON `hotel_img` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `hotel_order`
--

DROP TABLE IF EXISTS `hotel_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `as_user` bigint DEFAULT NULL COMMENT '关联用户ID',
  `as_hotel` bigint DEFAULT NULL COMMENT '关联酒店ID',
  `as_room` bigint DEFAULT NULL COMMENT '关联房间ID',
  `room_num` int DEFAULT NULL COMMENT '房间数量',
  `check_in_date` date DEFAULT NULL COMMENT '入住日期',
  `check_out_date` date DEFAULT NULL COMMENT '退房日期',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `is_pay` tinyint DEFAULT NULL COMMENT '0：未支付 1：已支付',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='酒店订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel_order`
--

LOCK TABLES `hotel_order` WRITE;
/*!40000 ALTER TABLE `hotel_order` DISABLE KEYS */;
INSERT INTO `hotel_order` VALUES (1,1,6,7,1,'2026-04-15','2026-05-27',4200.00,1,1777106321012,1777106325,1,0),(2,1,7,8,5,'2026-04-15','2026-05-13',14000.00,1,1777278624496,1777278629,1,0);
/*!40000 ALTER TABLE `hotel_order` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_hotel_order_insert` BEFORE INSERT ON `hotel_order` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_hotel_order_update` BEFORE UPDATE ON `hotel_order` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `hotel_review`
--

DROP TABLE IF EXISTS `hotel_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `as_hotel` bigint DEFAULT NULL COMMENT '关联酒店ID',
  `as_user` bigint DEFAULT NULL COMMENT '关联用户ID',
  `as_room` bigint DEFAULT NULL COMMENT '关联房间ID',
  `content` varchar(1000) DEFAULT NULL COMMENT '评价内容',
  `rating` decimal(2,1) DEFAULT NULL COMMENT '评分',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='酒店评价表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel_review`
--

LOCK TABLES `hotel_review` WRITE;
/*!40000 ALTER TABLE `hotel_review` DISABLE KEYS */;
INSERT INTO `hotel_review` VALUES (1,6,1,7,'很好',5.0,1777106351892,1777106351892,1,0),(2,6,1,7,'好',3.5,1777106380994,1777106380994,1,0),(3,7,1,8,'好好好好好好好好好好好好好好好好好好好好好好好好',5.0,1777278667760,1777278667760,1,0);
/*!40000 ALTER TABLE `hotel_review` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_hotel_review_insert` BEFORE INSERT ON `hotel_review` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_hotel_review_update` BEFORE UPDATE ON `hotel_review` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `hotel_review_img`
--

DROP TABLE IF EXISTS `hotel_review_img`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_review_img` (
  `hotel_review_id` bigint NOT NULL COMMENT '酒店评价ID',
  `file_id` bigint NOT NULL COMMENT '文件ID',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`hotel_review_id`,`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='酒店评价照片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel_review_img`
--

LOCK TABLES `hotel_review_img` WRITE;
/*!40000 ALTER TABLE `hotel_review_img` DISABLE KEYS */;
INSERT INTO `hotel_review_img` VALUES (3,36,1777278667771,1777278667771,1,0);
/*!40000 ALTER TABLE `hotel_review_img` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_hotel_review_img_insert` BEFORE INSERT ON `hotel_review_img` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_hotel_review_img_update` BEFORE UPDATE ON `hotel_review_img` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `hotel_room`
--

DROP TABLE IF EXISTS `hotel_room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_room` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `as_hotel` bigint DEFAULT NULL COMMENT '关联酒店ID',
  `name` varchar(100) DEFAULT NULL COMMENT '房间名称',
  `room_desc` varchar(1000) DEFAULT NULL COMMENT '房间描述',
  `feature_json` varchar(1000) DEFAULT NULL COMMENT '房间特色JSON',
  `tag_json` varchar(1000) DEFAULT NULL COMMENT '房间标签JSON',
  `bed_count` tinyint DEFAULT NULL COMMENT '床数量',
  `bed_size` decimal(3,2) DEFAULT NULL COMMENT '床尺寸',
  `max_people` tinyint DEFAULT NULL COMMENT '最大容纳人数',
  `total_room` int DEFAULT NULL COMMENT '总房间数',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='酒店房间表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel_room`
--

LOCK TABLES `hotel_room` WRITE;
/*!40000 ALTER TABLE `hotel_room` DISABLE KEYS */;
INSERT INTO `hotel_room` VALUES (1,2,'测试1','测试','[\"24 小时热水\",\"WiFi免费\",\"柔软浴袍\",\"静音设计\"]',NULL,1,1.50,2,10,1776937074,1777041798,1,0),(2,2,'测试2','','[\"24 小时热水\",\"品牌床品\",\"隔音良好\",\"遮光窗帘\"]',NULL,1,1.50,2,10,1776937074,1777041798,1,0),(3,3,'测试','测试','[\"WiFi免费\",\"干湿分离卫浴\",\"24 小时热水\"]',NULL,1,1.50,2,10,1776944028,1776945211,1,1),(4,4,'测试2','测试2','[]',NULL,1,1.50,2,10,1776945250,1776945467,1,1),(5,5,'测试2','','[]',NULL,1,1.50,2,10,1776945492,1777040370,1,0),(6,6,'测试3','测试3','[]',NULL,1,1.50,2,10,1776945726,1777040372,1,0),(7,6,'测试31','测试3','[]',NULL,1,1.50,2,10,1776945726,1777040372,1,0),(8,7,'测试','测试测试测试','[\"24 小时热水\",\"品牌床品\",\"隔音良好\",\"遮光窗帘\"]',NULL,1,1.50,2,10,1777278593,1777278593,1,0);
/*!40000 ALTER TABLE `hotel_room` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_hotel_room_insert` BEFORE INSERT ON `hotel_room` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_hotel_room_update` BEFORE UPDATE ON `hotel_room` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `perms` varchar(100) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `status` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu`
--

LOCK TABLES `menu` WRITE;
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;
INSERT INTO `menu` VALUES (1,'hello:select',NULL,1),(2,'upstream-downstream:upstream:select',NULL,1),(3,'upstream-downstream:downstream:select',NULL,1),(4,'all',NULL,1),(5,'merchant:select',NULL,1),(6,'merchant:update',NULL,1),(7,'workorder:select',NULL,1),(8,'workorder:update',NULL,1),(9,'user:select',NULL,1),(10,'user:update',NULL,1);
/*!40000 ALTER TABLE `menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `status` tinyint DEFAULT NULL,
  `is_delete` tinyint DEFAULT NULL,
  `create_time` bigint DEFAULT NULL,
  `update_time` bigint DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'admin',1,0,1768047911,1773832664,1,NULL),(3,'联系人',1,0,1768722220,1774924882,1,NULL),(6,'收款人',1,0,1773832658,1773832664,1,NULL),(7,'出单员',1,0,1774144837,1774144837,NULL,NULL),(8,'普通用户',1,0,1775051833,1775051833,NULL,NULL);
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_menu`
--

DROP TABLE IF EXISTS `role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint DEFAULT NULL,
  `menu_id` bigint DEFAULT NULL,
  `create_time` bigint DEFAULT NULL,
  `update_time` bigint DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `is_delete` tinyint DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_menu`
--

LOCK TABLES `role_menu` WRITE;
/*!40000 ALTER TABLE `role_menu` DISABLE KEYS */;
INSERT INTO `role_menu` VALUES (2,7,5,1774174955,1774174955,NULL,0),(3,7,7,1774174955,1774174955,NULL,0),(4,7,8,1774174955,1774174955,NULL,0),(5,7,9,1774174955,1774174955,NULL,0);
/*!40000 ALTER TABLE `role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room_img`
--

DROP TABLE IF EXISTS `room_img`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_img` (
  `as_id` bigint NOT NULL COMMENT '房间ID',
  `file_id` bigint NOT NULL COMMENT '文件ID',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`as_id`,`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房间图片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room_img`
--

LOCK TABLES `room_img` WRITE;
/*!40000 ALTER TABLE `room_img` DISABLE KEYS */;
INSERT INTO `room_img` VALUES (1,22,1777041798,1777041798,NULL,0),(2,21,1777041798,1777041798,NULL,0),(3,5,1776944053,1776944053,NULL,0),(6,9,1777040372,1777040372,NULL,0),(6,10,1777040372,1777040372,NULL,0),(7,11,1777040372,1777040372,NULL,0),(8,32,1777278593,1777278593,1,0),(8,33,1777278593,1777278593,1,0),(8,34,1777278593,1777278593,1,0),(8,35,1777278593,1777278593,1,0);
/*!40000 ALTER TABLE `room_img` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_room_img_insert` BEFORE INSERT ON `room_img` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_room_img_update` BEFORE UPDATE ON `room_img` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `room_stock`
--

DROP TABLE IF EXISTS `room_stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_stock` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `room_id` bigint DEFAULT NULL COMMENT '房间ID',
  `stock_date` date DEFAULT NULL COMMENT '库存日期',
  `occupied_num` int DEFAULT NULL COMMENT '已占用数量',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房间库存表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room_stock`
--

LOCK TABLES `room_stock` WRITE;
/*!40000 ALTER TABLE `room_stock` DISABLE KEYS */;
INSERT INTO `room_stock` VALUES (1,7,'2026-04-15',1,1777106320873,1777106320873,1,0),(2,7,'2026-04-16',1,1777106320873,1777106320873,1,0),(3,7,'2026-04-17',1,1777106320873,1777106320873,1,0),(4,7,'2026-04-18',1,1777106320873,1777106320873,1,0),(5,7,'2026-04-19',1,1777106320873,1777106320873,1,0),(6,7,'2026-04-20',1,1777106320873,1777106320873,1,0),(7,7,'2026-04-21',1,1777106320873,1777106320873,1,0),(8,7,'2026-04-22',1,1777106320873,1777106320873,1,0),(9,7,'2026-04-23',1,1777106320873,1777106320873,1,0),(10,7,'2026-04-24',1,1777106320873,1777106320873,1,0),(11,7,'2026-04-25',1,1777106320873,1777106320873,1,0),(12,7,'2026-04-26',1,1777106320873,1777106320873,1,0),(13,7,'2026-04-27',1,1777106320873,1777106320873,1,0),(14,7,'2026-04-28',1,1777106320873,1777106320873,1,0),(15,7,'2026-04-29',1,1777106320873,1777106320873,1,0),(16,7,'2026-04-30',1,1777106320873,1777106320873,1,0),(17,7,'2026-05-01',1,1777106320873,1777106320873,1,0),(18,7,'2026-05-02',1,1777106320873,1777106320873,1,0),(19,7,'2026-05-03',1,1777106320873,1777106320873,1,0),(20,7,'2026-05-04',1,1777106320873,1777106320873,1,0),(21,7,'2026-05-05',1,1777106320873,1777106320873,1,0),(22,7,'2026-05-06',1,1777106320873,1777106320873,1,0),(23,7,'2026-05-07',1,1777106320873,1777106320873,1,0),(24,7,'2026-05-08',1,1777106320873,1777106320873,1,0),(25,7,'2026-05-09',1,1777106320873,1777106320873,1,0),(26,7,'2026-05-10',1,1777106320873,1777106320873,1,0),(27,7,'2026-05-11',1,1777106320873,1777106320873,1,0),(28,7,'2026-05-12',1,1777106320873,1777106320873,1,0),(29,7,'2026-05-13',1,1777106320873,1777106320873,1,0),(30,7,'2026-05-14',1,1777106320873,1777106320873,1,0),(31,7,'2026-05-15',1,1777106320873,1777106320873,1,0),(32,7,'2026-05-16',1,1777106320873,1777106320873,1,0),(33,7,'2026-05-17',1,1777106320873,1777106320873,1,0),(34,7,'2026-05-18',1,1777106320873,1777106320873,1,0),(35,7,'2026-05-19',1,1777106320873,1777106320873,1,0),(36,7,'2026-05-20',1,1777106320873,1777106320873,1,0),(37,7,'2026-05-21',1,1777106320873,1777106320873,1,0),(38,7,'2026-05-22',1,1777106320873,1777106320873,1,0),(39,7,'2026-05-23',1,1777106320873,1777106320873,1,0),(40,7,'2026-05-24',1,1777106320873,1777106320873,1,0),(41,7,'2026-05-25',1,1777106320873,1777106320873,1,0),(42,7,'2026-05-26',1,1777106320873,1777106320873,1,0),(43,8,'2026-04-15',5,1777278624385,1777278624385,1,0),(44,8,'2026-04-16',5,1777278624385,1777278624385,1,0),(45,8,'2026-04-17',5,1777278624385,1777278624385,1,0),(46,8,'2026-04-18',5,1777278624385,1777278624385,1,0),(47,8,'2026-04-19',5,1777278624385,1777278624385,1,0),(48,8,'2026-04-20',5,1777278624385,1777278624385,1,0),(49,8,'2026-04-21',5,1777278624385,1777278624385,1,0),(50,8,'2026-04-22',5,1777278624385,1777278624385,1,0),(51,8,'2026-04-23',5,1777278624385,1777278624385,1,0),(52,8,'2026-04-24',5,1777278624385,1777278624385,1,0),(53,8,'2026-04-25',5,1777278624385,1777278624385,1,0),(54,8,'2026-04-26',5,1777278624385,1777278624385,1,0),(55,8,'2026-04-27',5,1777278624385,1777278624385,1,0),(56,8,'2026-04-28',5,1777278624385,1777278624385,1,0),(57,8,'2026-04-29',5,1777278624385,1777278624385,1,0),(58,8,'2026-04-30',5,1777278624385,1777278624385,1,0),(59,8,'2026-05-01',5,1777278624385,1777278624385,1,0),(60,8,'2026-05-02',5,1777278624385,1777278624385,1,0),(61,8,'2026-05-03',5,1777278624385,1777278624385,1,0),(62,8,'2026-05-04',5,1777278624385,1777278624385,1,0),(63,8,'2026-05-05',5,1777278624385,1777278624385,1,0),(64,8,'2026-05-06',5,1777278624385,1777278624385,1,0),(65,8,'2026-05-07',5,1777278624385,1777278624385,1,0),(66,8,'2026-05-08',5,1777278624385,1777278624385,1,0),(67,8,'2026-05-09',5,1777278624385,1777278624385,1,0),(68,8,'2026-05-10',5,1777278624385,1777278624385,1,0),(69,8,'2026-05-11',5,1777278624385,1777278624385,1,0),(70,8,'2026-05-12',5,1777278624385,1777278624385,1,0);
/*!40000 ALTER TABLE `room_stock` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_room_stock_insert` BEFORE INSERT ON `room_stock` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_room_stock_update` BEFORE UPDATE ON `room_stock` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `room_type_price`
--

DROP TABLE IF EXISTS `room_type_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_type_price` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `as_room` bigint DEFAULT NULL COMMENT '关联房间ID',
  `summary_json` varchar(1000) DEFAULT NULL COMMENT '价格说明JSON',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房型价格表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room_type_price`
--

LOCK TABLES `room_type_price` WRITE;
/*!40000 ALTER TABLE `room_type_price` DISABLE KEYS */;
INSERT INTO `room_type_price` VALUES (1,2,'[\"延迟退房至 14:00\",\"入住当天 20:00 前可免费取消\"]',100.00,1776942406,1776942439,1,1),(2,1,'[\"赠早餐升级权益\",\"赠房型升级券\"]',100.00,1776942406,1776942439,1,1),(3,2,'[\"延迟退房至 14:00\",\"入住当天 20:00 前可免费取消\"]',100.00,1776942439,1776942449,1,1),(4,1,'[\"赠早餐升级权益\",\"赠房型升级券\"]',100.00,1776942439,1776942449,1,1),(5,2,'[\"延迟退房至 14:00\",\"入住当天 20:00 前可免费取消\"]',100.00,1776942449,1777039418,1,1),(6,1,'[\"赠早餐升级权益\",\"赠房型升级券\"]',100.00,1776942449,1777039418,1,1),(7,3,'[\"赠早餐升级权益\",\"赠延迟退房权益\",\"含欢迎饮品\"]',100.00,1776944053,1776945211,1,1),(8,6,'[\"赠早餐升级权益\",\"赠 SPA 体验券\",\"赠房型升级券\"]',100.00,1776945727,1777039436,1,1),(9,6,'[\"节假日可用\",\"至多预订 5 间\",\"限时特惠价\"]',110.00,1776945727,1777039436,1,1),(10,7,'[\"赠早餐升级权益\",\"赠房型升级券\",\"赠免费加床服务\"]',100.00,1776945727,1777039436,1,1),(11,5,'[]',300.00,1777039411,1777039427,1,1),(12,2,'[\"延迟退房至 14:00\",\"入住当天 20:00 前可免费取消\"]',100.00,1777039418,1777040368,1,1),(13,1,'[\"赠早餐升级权益\",\"赠房型升级券\"]',100.00,1777039418,1777040368,1,1),(14,5,'[]',300.00,1777039427,1777040370,1,1),(15,7,'[\"赠早餐升级权益\",\"赠房型升级券\",\"赠免费加床服务\"]',100.00,1777039436,1777040372,1,1),(16,6,'[\"节假日可用\",\"至多预订 5 间\",\"限时特惠价\"]',110.00,1777039436,1777040372,1,1),(17,6,'[\"赠早餐升级权益\",\"赠 SPA 体验券\",\"赠房型升级券\"]',100.00,1777039436,1777040372,1,1),(18,2,'[\"延迟退房至 14:00\",\"入住当天 20:00 前可免费取消\"]',100.00,1777040368,1777041798,1,1),(19,1,'[\"赠早餐升级权益\",\"赠房型升级券\"]',100.00,1777040368,1777041798,1,1),(20,5,'[]',300.00,1777040370,1777040370,1,0),(21,7,'[\"赠早餐升级权益\",\"赠房型升级券\",\"赠免费加床服务\"]',100.00,1777040372,1777040372,1,0),(22,6,'[\"赠早餐升级权益\",\"赠 SPA 体验券\",\"赠房型升级券\"]',100.00,1777040372,1777040372,1,0),(23,6,'[\"节假日可用\",\"至多预订 5 间\",\"限时特惠价\"]',110.00,1777040372,1777040372,1,0),(24,2,'[\"延迟退房至 14:00\",\"入住当天 20:00 前可免费取消\"]',100.00,1777041798,1777041798,1,0),(25,1,'[\"赠早餐升级权益\",\"赠房型升级券\"]',100.00,1777041798,1777041798,1,0),(26,8,'[\"双早\",\"担保确认\",\"在线支付\"]',100.00,1777278593,1777278593,1,0);
/*!40000 ALTER TABLE `room_type_price` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_room_type_price_insert` BEFORE INSERT ON `room_type_price` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_room_type_price_update` BEFORE UPDATE ON `room_type_price` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `scenic`
--

DROP TABLE IF EXISTS `scenic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scenic` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) DEFAULT NULL COMMENT '景点名称',
  `address` varchar(100) DEFAULT NULL COMMENT '地址',
  `area_code` varchar(100) DEFAULT NULL COMMENT '地区行政编号',
  `longitude` decimal(9,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(9,6) DEFAULT NULL COMMENT '纬度',
  `scenic_desc` varchar(1000) DEFAULT NULL COMMENT '景点描述',
  `open_time_desc` varchar(1000) DEFAULT NULL COMMENT '开放时间说明',
  `rating` decimal(2,1) DEFAULT '5.0' COMMENT '评分',
  `hot_value` decimal(2,1) DEFAULT NULL COMMENT '景区热度',
  `need_ticket` tinyint DEFAULT '0' COMMENT '0：不需要门票 1：需要',
  `price` decimal(10,2) DEFAULT NULL COMMENT '需要门票时的价格',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='景点表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scenic`
--

LOCK TABLES `scenic` WRITE;
/*!40000 ALTER TABLE `scenic` DISABLE KEYS */;
INSERT INTO `scenic` VALUES (1,'测试','测试','110101000000',116.397428,39.909230,NULL,'测试',5.0,NULL,0,0.00,1776997956,1777960478,0,0),(3,'测试2','测试2','110102000000',116.397428,39.909230,'测试2','全天开放',4.0,NULL,1,40.00,1777000614,1777960478,0,0),(4,'测试6','测试','130304000000',114.523178,38.033265,'测试','测试',5.0,NULL,1,30.00,1777001906,1777001919,1,1);
/*!40000 ALTER TABLE `scenic` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_scenic_insert` BEFORE INSERT ON `scenic` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_scenic_update` BEFORE UPDATE ON `scenic` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `scenic_img`
--

DROP TABLE IF EXISTS `scenic_img`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scenic_img` (
  `as_id` bigint NOT NULL COMMENT '景点ID',
  `file_id` bigint NOT NULL COMMENT '文件ID',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='景点图片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scenic_img`
--

LOCK TABLES `scenic_img` WRITE;
/*!40000 ALTER TABLE `scenic_img` DISABLE KEYS */;
INSERT INTO `scenic_img` VALUES (3,13,1777001864,1777001864,NULL,0),(1,23,1777087041,1777087041,NULL,0);
/*!40000 ALTER TABLE `scenic_img` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_scenic_img_insert` BEFORE INSERT ON `scenic_img` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_scenic_img_update` BEFORE UPDATE ON `scenic_img` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `scenic_order`
--

DROP TABLE IF EXISTS `scenic_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scenic_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `as_user` bigint DEFAULT NULL COMMENT '关联用户ID',
  `as_scenic` bigint DEFAULT NULL COMMENT '关联景点ID',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `is_pay` tinyint DEFAULT NULL COMMENT '0：未支付 1：已支付',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='景区订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scenic_order`
--

LOCK TABLES `scenic_order` WRITE;
/*!40000 ALTER TABLE `scenic_order` DISABLE KEYS */;
INSERT INTO `scenic_order` VALUES (1,1,3,40.00,1,1777106374234,1777106376,1,0),(2,1,3,40.00,1,1777278736125,1777278736,1,0);
/*!40000 ALTER TABLE `scenic_order` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_scenic_order_insert` BEFORE INSERT ON `scenic_order` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_scenic_order_update` BEFORE UPDATE ON `scenic_order` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `scenic_review`
--

DROP TABLE IF EXISTS `scenic_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scenic_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `as_scenic` bigint DEFAULT NULL COMMENT '关联景点ID',
  `as_user` bigint DEFAULT NULL COMMENT '关联用户ID',
  `content` varchar(1000) DEFAULT NULL COMMENT '评价内容',
  `rating` decimal(2,1) DEFAULT NULL COMMENT '评分',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='景点评价表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scenic_review`
--

LOCK TABLES `scenic_review` WRITE;
/*!40000 ALTER TABLE `scenic_review` DISABLE KEYS */;
INSERT INTO `scenic_review` VALUES (1,3,1,'好',4.0,1777106410246,1777106410246,1,0),(2,1,1,'好好好',5.0,1777278728994,1777278728994,1,0);
/*!40000 ALTER TABLE `scenic_review` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_scenic_review_insert` BEFORE INSERT ON `scenic_review` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_scenic_review_update` BEFORE UPDATE ON `scenic_review` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `scenic_review_img`
--

DROP TABLE IF EXISTS `scenic_review_img`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scenic_review_img` (
  `scenic_review_id` bigint NOT NULL COMMENT '景点评价ID',
  `file_id` bigint NOT NULL COMMENT '文件ID',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`scenic_review_id`,`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='景点评价照片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scenic_review_img`
--

LOCK TABLES `scenic_review_img` WRITE;
/*!40000 ALTER TABLE `scenic_review_img` DISABLE KEYS */;
INSERT INTO `scenic_review_img` VALUES (2,37,1777278728999,1777278728999,1,0);
/*!40000 ALTER TABLE `scenic_review_img` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_scenic_review_img_insert` BEFORE INSERT ON `scenic_review_img` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_scenic_review_img_update` BEFORE UPDATE ON `scenic_review_img` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `system_file`
--

DROP TABLE IF EXISTS `system_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_file` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `path` varchar(500) DEFAULT NULL COMMENT '文件路径',
  `file_name` varchar(100) DEFAULT NULL COMMENT '文件名',
  `is_linked` tinyint DEFAULT '0' COMMENT '0：未被引用 1：被引用',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统文件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_file`
--

LOCK TABLES `system_file` WRITE;
/*!40000 ALTER TABLE `system_file` DISABLE KEYS */;
INSERT INTO `system_file` VALUES (1,'8bef65d1-9739-47f5-bc59-a0271ea71df4QQ20260413-193902.png','QQ20260413-193902.png',0,1776941626,1777039418,1,0),(2,'e2ad60af-125c-4428-b4ef-959a7e9ae70fp479223.png','p479223.png',0,1776942387,1777041798,1,0),(3,'bddbc449-8e93-453a-9670-ac4340ad25a0QQ20260413-193902.png','QQ20260413-193902.png',0,1776942392,1777041798,1,0),(4,'a63a68f0-7ed4-429e-86d2-ac3760371d93QQ20251213-000907.jpg','QQ20251213-000907.jpg',0,1776944012,1776945211,1,0),(5,'a4bd84f9-074e-434f-9618-df2512995db4QQ20251213-000907.png','QQ20251213-000907.png',0,1776944024,1776945211,1,0),(6,'b3808612-d506-4cf6-a9d7-090e1caf6f94QQ20251213-000907.jpg','QQ20251213-000907.jpg',0,1776945235,1776945467,1,0),(7,'411b14a7-fa84-40ef-aff2-0092e1f4a1f7QQ20251213-000907.jpg','QQ20251213-000907.jpg',0,1776945686,1777039436,1,0),(8,'9ce211fb-df5e-4144-a170-f5d60ae9caadp479223.png','p479223.png',0,1776945687,1777039436,1,0),(9,'e5ec2adf-f6bc-4c3b-a26e-5e12e0b56237p479223.png','p479223.png',1,1776945695,1777040372,1,0),(10,'f771e8c0-9f94-4a60-87e9-5717b102524a合格证.png','合格证.png',1,1776945698,1777040372,1,0),(11,'4239d8df-03a8-46f2-ad94-c7c23b7ce0cf发票.png','发票.png',1,1776945721,1777040372,1,0),(12,'3c42b936-ba01-4a4f-ab95-6d92d35ce231发票.png','发票.png',0,1777000476,1777000476,1,0),(13,'0ade8f6a-632a-4150-bacb-f9b76a254866QQ20251213-000907.png','QQ20251213-000907.png',1,1777000578,1777001864,1,0),(14,'c2aa82ce-8219-458c-bce7-d772e968407a3d2adef389a8febc6ef8e2d2adfa516a.png','3d2adef389a8febc6ef8e2d2adfa516a.png',0,1777001532,1777001532,1,0),(15,'98fd454a-4003-4968-92a3-76833bed7629QQ20251213-000907.png','QQ20251213-000907.png',0,1777001669,1777001669,1,0),(16,'3cde83eb-9648-4bda-957a-aa0c5f621f59测试.png','测试.png',0,1777001906,1777001919,1,0),(17,'fa9eecda-ac79-4ab7-b4cf-4863e3409081QQ20251213-000907.jpg','QQ20251213-000907.jpg',1,1777039418,1777041798,1,0),(18,'a3fa2ac0-b2b9-4524-a010-233467aaa15cd48ca653-6b19-4f94-8651-d1b458787d48.png','d48ca653-6b19-4f94-8651-d1b458787d48.png',1,1777039426,1777040370,1,0),(19,'dd490a78-b78b-472e-a86d-2ad6a87f267bQQ20260413-193902.png','QQ20260413-193902.png',1,1777039433,1777040372,1,0),(20,'d93a07af-642c-4282-adbb-346aa5413760QQ20251213-000907.png','QQ20251213-000907.png',1,1777039435,1777040372,1,0),(21,'80127d12-a1fd-4606-9a55-cbe91effb598QQ20251213-000907.png','QQ20251213-000907.png',1,1777041790,1777041798,1,0),(22,'b7b033de-4e74-4073-aabe-cfaf22d1f438QQ20251213-000907.jpg','QQ20251213-000907.jpg',1,1777041791,1777041798,1,0),(23,'0dec5758-fcf6-4372-846d-a90692794fc2QQ20260413-193902.png','QQ20260413-193902.png',1,1777087041,1777087041,1,0),(24,'06571a31-2291-4ba5-9d21-5d0a5ec11db7QQ20251213-000907.png','QQ20251213-000907.png',0,1777090563,1777278822,1,0),(25,'78b55d38-ad77-45dc-81f1-578f48f68b79QQ20251213-000907.jpg','QQ20251213-000907.jpg',1,1777278525,1777278593,1,0),(26,'f84a22a8-fa92-4f20-bd21-ad4a772b0fbfQQ20251213-000907.png','QQ20251213-000907.png',1,1777278526,1777278593,1,0),(27,'2c4e1cd5-1d52-4194-86d4-c22e44d54ef6QQ20251213-000907.png','QQ20251213-000907.png',1,1777278528,1777278593,1,0),(28,'17f3aba4-9f6c-44dd-9840-565c098c106fQQ20251213-000907.jpg','QQ20251213-000907.jpg',1,1777278529,1777278593,1,0),(29,'2dfa7a82-c6ad-4c50-b3cc-dc4a888ed710QQ20251213-000907.jpg','QQ20251213-000907.jpg',1,1777278531,1777278593,1,0),(30,'85389fa5-61aa-4e21-9ab8-91a3ed66d4cfQQ20251213-000907.jpg','QQ20251213-000907.jpg',1,1777278534,1777278593,1,0),(31,'6ecb08ff-b8ae-4d7f-b0aa-e3edbc8e9646QQ20251213-000907.png','QQ20251213-000907.png',1,1777278535,1777278593,1,0),(32,'68bab451-a7ec-44a8-91c8-a6afdadb0781QQ20251213-000907.png','QQ20251213-000907.png',1,1777278566,1777278593,1,0),(33,'2a6a1532-fa9b-4192-bce3-ed2083083be5QQ20251213-000907.jpg','QQ20251213-000907.jpg',1,1777278567,1777278593,1,0),(34,'995b1e17-615c-4d43-8dcc-12b2d8ecc8c8QQ20251213-000907.jpg','QQ20251213-000907.jpg',1,1777278569,1777278593,1,0),(35,'4cf80040-7fd2-45b2-8720-c9579be84629QQ20251213-000907.jpg','QQ20251213-000907.jpg',1,1777278570,1777278593,1,0),(36,'eb49eb94-34e3-4d8a-bf7a-854d3748bcfdQQ20251213-000907.png','QQ20251213-000907.png',1,1777278667,1777278667,1,0),(37,'1e459d1c-0f3d-463f-86f5-fadd1bf311ef测试.png','测试.png',1,1777278728,1777278729,1,0),(38,'b0f0e8ab-090c-4dd9-8389-4c46159c6d82合格证.png','合格证.png',1,1777278788,1777278808,1,0);
/*!40000 ALTER TABLE `system_file` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_system_file_insert` BEFORE INSERT ON `system_file` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_system_file_update` BEFORE UPDATE ON `system_file` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `train`
--

DROP TABLE IF EXISTS `train`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `train` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `train_code` varchar(100) DEFAULT NULL COMMENT '火车编号',
  `start_area_code` varchar(100) DEFAULT NULL COMMENT '出发地行政编号',
  `end_area_code` varchar(100) DEFAULT NULL COMMENT '目的地行政编号',
  `start_time` bigint DEFAULT NULL COMMENT '出发时间（秒级时间戳）',
  `end_time` bigint DEFAULT NULL COMMENT '到达时间（秒级时间戳）',
  `start_station_name` varchar(100) DEFAULT NULL COMMENT '出发站名称',
  `end_station_name` varchar(100) DEFAULT NULL COMMENT '到达站名称',
  `create_time` time NOT NULL COMMENT '创建时间',
  `update_time` time NOT NULL COMMENT '更新时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='火车表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `train`
--

LOCK TABLES `train` WRITE;
/*!40000 ALTER TABLE `train` DISABLE KEYS */;
/*!40000 ALTER TABLE `train` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_train_insert` BEFORE INSERT ON `train` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_train_update` BEFORE UPDATE ON `train` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `train_order`
--

DROP TABLE IF EXISTS `train_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `train_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `as_user` bigint DEFAULT NULL COMMENT '关联用户ID',
  `as_train` varchar(100) DEFAULT NULL COMMENT '关联火车',
  `start_station_name` varchar(100) DEFAULT NULL,
  `end_station_name` varchar(100) DEFAULT NULL,
  `type_name` varchar(100) DEFAULT NULL COMMENT '票种名称',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `is_pay` tinyint DEFAULT NULL COMMENT '0：未支付 1：已支付',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='火车票订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `train_order`
--

LOCK TABLES `train_order` WRITE;
/*!40000 ALTER TABLE `train_order` DISABLE KEYS */;
INSERT INTO `train_order` VALUES (1,1,'146152','天津市','大同市','二等座',301.00,1,1777178555221,1777178565,1,0),(2,1,'105152','北京市','天津市','二等座',311.00,1,1777278917556,1777278918,1,0);
/*!40000 ALTER TABLE `train_order` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_train_order_insert` BEFORE INSERT ON `train_order` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_train_order_update` BEFORE UPDATE ON `train_order` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `train_ticket`
--

DROP TABLE IF EXISTS `train_ticket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `train_ticket` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `as_train` bigint DEFAULT NULL COMMENT '关联火车ID',
  `type_name` varchar(100) DEFAULT NULL COMMENT '票种名称',
  `price` decimal(10,2) DEFAULT NULL COMMENT '票价',
  `num` int DEFAULT NULL COMMENT '剩余票数',
  `create_time` bigint NOT NULL COMMENT '创建时间（秒级时间戳）',
  `update_time` bigint NOT NULL COMMENT '更新时间（秒级时间戳）',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '0：未删除，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='火车票表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `train_ticket`
--

LOCK TABLES `train_ticket` WRITE;
/*!40000 ALTER TABLE `train_ticket` DISABLE KEYS */;
/*!40000 ALTER TABLE `train_ticket` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_train_ticket_insert` BEFORE INSERT ON `train_ticket` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_train_ticket_update` BEFORE UPDATE ON `train_ticket` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `create_time` bigint DEFAULT NULL,
  `update_time` bigint DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `is_delete` tinyint DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'15762502276','15762502276@163.com','$2a$10$P8NbzSgySa5MeZsAUShGHuWPuN.HQltfL9gmDHFLQ6DIF6kYkkUFG','李鸣',1768042833,1777960569,1,0),(2,'13334959024',NULL,'$2a$10$xUx0Uhs8xsVxUW5JEvcKauvJDRFCc1s7.bfoMeuWXTmoM5lb2.yIy','测试用户',1768739064,1777960547,1,1),(3,'13334959023',NULL,'$2a$10$xUx0Uhs8xsVxUW5JEvcKauvJDRFCc1s7.bfoMeuWXTmoM5lb2.yIy','测试收款人',1768819077,1777960545,1,1),(4,'13334959025',NULL,'$2a$10$xUx0Uhs8xsVxUW5JEvcKauvJDRFCc1s7.bfoMeuWXTmoM5lb2.yIy','测试用户',1768819077,1777960551,1,1),(5,'13334959026',NULL,'$2a$10$xUx0Uhs8xsVxUW5JEvcKauvJDRFCc1s7.bfoMeuWXTmoM5lb2.yIy','测试用户',1768819077,1777960550,1,1),(6,'13334959027',NULL,'$2a$10$xUx0Uhs8xsVxUW5JEvcKauvJDRFCc1s7.bfoMeuWXTmoM5lb2.yIy','测试用户',1768819077,1777960548,1,1),(7,'15762502277',NULL,'$2a$10$MSF816EiQuWAkSuG9UDN7eRawFW2x7oHOwkMtJKy3elqd6PWeMRdC','lemonqwq',1768826724,1777960543,1,1),(8,'15762502279',NULL,'$2a$10$c5ghzj8UCVg0Sb6pbts7z.4xiK6gBiwtdeTdoGR2hQQA7yHGgFs/S','teriri',1768827732,1777960522,1,1),(9,'13457294872',NULL,'$2a$10$36Cy5YaCArnAOicdAqbmnOyc4GgFSz7n72Dw08nBxt4kA6b0q2VMy','店长测试',1768881090,1777960542,1,1),(10,'13390342302',NULL,'$2a$10$5UmR64Hk58SmB3zaiDcqMeE.QqtEQ93zsWPhLwhjtwG0skP2Y6Tjm','lemon',1769884437,1777960541,1,1),(11,'13320418732',NULL,'$2a$10$8ht8eOlaD.yStuT/cI/1keejShrnDLTENIW4d4u8c6rcNSDTJSJIy','teririri',1769884544,1777960539,1,1),(14,'15965362512',NULL,'$2a$10$dfUGw5p1P4aYZQdJWahyNeGU8smW9Y1pLUdu3pWNImfq6yz2D8Poq','李梅',1774149496,1777960537,1,1),(15,'15965362513',NULL,'$2a$10$51lA5IzCYPPGNsVD6ECXaOiBiV1yKJsnXqPq0Mh1kLJ.PvBdDPiTO','李李',1774155772,1777960536,1,1),(22,'15763208194',NULL,'$2a$10$UfowJCD4LDj2XoAkq.LgDu5PRQ4wYjEanqcxpEzeFJemlxoBP6Mce','王明',1774580209,1777960534,1,1),(23,'15723010512',NULL,'$2a$10$cDO4pARS4XAYZH7br5gAF.SQcv1gC54IDYN3hG8aXapY56baqnw0W','331测试',1774924528,1774924540,1,1),(24,'15762502210',NULL,'$2a$10$u0APYVe.ru.0uL/J1oaEAeJDHHDqq7fII0bS4Sy5Ef54t6e9eiK1K','测试',1774949265,1774949297,1,1),(25,'15762502222','3041811312@qq.com','$2a$10$9muE7X2pfuW7rVhh3UrCh.8u/r31fVY0TDxygLmBXGd6QF2WQpaPW','测试邮箱',1775050026,1777960533,1,1),(26,'15720305981','15763502276@163.com','$2a$10$s22y5h0plO3PDal68XU.lOMsRohl/SUqkjTdYTSG12jWc3EJePzhq','小王',1775050208,1777960531,1,1),(27,'13329501294',NULL,'$2a$10$Apo8QaL6rdUHCHO6ZMHtT.VD0PpMBo4ej1eGqvtknqJ8ePSohgj1C','邮箱测试',1775051562,1777960518,1,1),(28,'13324310231',NULL,'$2a$10$lvPsbFzAz5qTTc/Fkrqo/OIQhwM..YtEqw.8BIBWYyQbDPNGqR6TC','自动商家测试',1775794271,1777960515,1,1),(29,'13345252983','3041811612@qq.com','$2a$10$UOzc.36B5D4GLYD.WEZtMu7pykiYvYUSBFsFx09VIEjPdb9uWNyuy','测试用户',1777960596,1777960596,1,0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_user_insert` BEFORE INSERT ON `user` FOR EACH ROW BEGIN
    IF NEW.create_time IS NULL THEN SET NEW.create_time = UNIX_TIMESTAMP(); END IF;
    IF NEW.update_time IS NULL THEN SET NEW.update_time = UNIX_TIMESTAMP(); END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_user_update` BEFORE UPDATE ON `user` FOR EACH ROW BEGIN
    SET NEW.update_time = UNIX_TIMESTAMP();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `role_id` bigint DEFAULT NULL,
  `update_time` bigint DEFAULT NULL,
  `create_time` bigint DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `is_delete` tinyint DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (3,2,3,1768739064,1768739064,1,1),(7,7,3,1774924922,1768827222,1,1),(8,8,3,1774924922,1768827732,1,1),(10,4,3,1774924922,1768828001,1,1),(11,5,3,1774924922,1768828007,1,1),(12,6,3,1768828017,1768828017,1,1),(13,9,3,1768881090,1768881090,1,1),(14,10,3,1769884437,1769884437,1,1),(15,11,3,1774924922,1769884544,1,1),(18,3,6,1773833318,1773833318,1,1),(19,14,7,1774149496,1774149496,1,1),(20,15,1,1774155772,1774155772,1,1),(21,1,1,1775050105,1774232387,1,1),(29,22,3,1774924922,1774580209,1,1),(30,23,3,1774924922,1774924528,1,1),(31,24,7,1774949300,1774949265,1,1),(32,25,7,1775050026,1775050026,1,1),(33,1,1,1775050113,1775050105,1,1),(34,1,1,1775050118,1775050113,1,1),(35,1,1,1775050122,1775050118,1,1),(36,1,1,1775050122,1775050122,1,0),(37,26,7,1775050208,1775050208,1,1),(38,27,6,1775051562,1775051562,1,1),(39,28,8,1775794271,1775794271,1,1),(40,29,8,NULL,NULL,1,0);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'travel'
--

--
-- Dumping routines for database 'travel'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-08 15:55:30
