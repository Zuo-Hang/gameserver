/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Host           : localhost:3306
 Source Schema         : game

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 21/09/2020 18:33:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_Id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `nick_Name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'nick_Name',
  `password` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'password',
  `disable` int(11) NOT NULL DEFAULT 0 COMMENT 'id',
  `creat_Time` datetime(0) NULL DEFAULT NULL COMMENT 'register_time',
  `last_Login_Time` datetime(0) NULL DEFAULT NULL COMMENT 'last_Login_Time',
  `phone_Number` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'phoneNumber',
  PRIMARY KEY (`user_Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'user' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
