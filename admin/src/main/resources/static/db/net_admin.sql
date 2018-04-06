/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50527
Source Host           : 127.0.0.1:3306
Source Database       : net_admin

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2018-04-06 13:25:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for biz_resource
-- ----------------------------
DROP TABLE IF EXISTS `biz_resource`;
CREATE TABLE `biz_resource` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `resource_name` varchar(50) DEFAULT NULL,
  `work_count` int(10) DEFAULT NULL,
  `url_prefix` varchar(100) DEFAULT NULL,
  `url_suffix` varchar(20) DEFAULT NULL,
  `parser_class` varchar(100) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of biz_resource
-- ----------------------------

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `permission_id` varchar(8) NOT NULL COMMENT '主键id',
  `parent_id` varchar(8) DEFAULT NULL COMMENT '上级权限',
  `permission_type` int(11) DEFAULT '0' COMMENT '权限类型：0菜单，1按钮',
  `permission_name` varchar(20) DEFAULT NULL COMMENT '权限名',
  `permission_value` varchar(50) DEFAULT NULL COMMENT '权限值',
  `permission_icon` varchar(20) DEFAULT NULL COMMENT '图标',
  `order_number` int(11) DEFAULT '0' COMMENT '菜单排序编号',
  `is_delete` int(11) DEFAULT '0' COMMENT '是否删除：0否,1是',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES ('1', '0', '0', '爬虫管理', null, '&#xe628;', '1', '0', '2018-04-05 13:42:46', '2018-04-05 12:45:35');
INSERT INTO `sys_permission` VALUES ('11', '1', '0', '监控引擎', 'spider/spiderlist', null, '1', '0', '2018-04-05 13:54:23', '2018-04-05 13:42:50');
INSERT INTO `sys_permission` VALUES ('2', '0', '0', '代理管理', null, '&#xe857;', '2', '0', '2018-04-04 18:10:53', '2018-04-05 12:45:42');
INSERT INTO `sys_permission` VALUES ('21', '2', '0', '管理资源', 'proxy/resourcelist', null, '1', '0', '2018-04-05 12:47:52', '2018-04-05 13:42:56');
