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

 Date: 12/08/2020 17:30:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for player
-- ----------------------------
DROP TABLE IF EXISTS `player`;
CREATE TABLE `player`  (
  `player_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '玩家角色的id',
  `player_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '化身的名字',
  `user_id` int(11) NOT NULL COMMENT '所属用户',
  `now_at` int(11) NOT NULL COMMENT '所处的场景',
  `exp` int(255) NOT NULL COMMENT '经验值',
  `state` int(255) NOT NULL COMMENT '状态',
  `money` int(255) NULL DEFAULT NULL COMMENT '金币',
  `guild_id` int(255) NULL DEFAULT NULL COMMENT '公会id',
  `role_class` int(255) NOT NULL COMMENT '角色类型',
  `guild_class` int(255) NULL DEFAULT NULL COMMENT '公会类型',
  `friends` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '好友',
  PRIMARY KEY (`player_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
