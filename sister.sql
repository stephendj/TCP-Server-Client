-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Apr 13, 2015 at 10:05 AM
-- Server version: 5.6.21
-- PHP Version: 5.6.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `sister`
--

-- --------------------------------------------------------

--
-- Table structure for table `inventory`
--

CREATE TABLE IF NOT EXISTS `inventory` (
  `id_user` int(11) NOT NULL,
  `honey` int(11) NOT NULL DEFAULT '0',
  `herbs` int(11) NOT NULL DEFAULT '0',
  `clay` int(11) NOT NULL DEFAULT '0',
  `mineral` int(11) NOT NULL DEFAULT '0',
  `potion` int(11) NOT NULL DEFAULT '0',
  `incense` int(11) NOT NULL DEFAULT '0',
  `gems` int(11) NOT NULL DEFAULT '0',
  `elixir` int(11) NOT NULL DEFAULT '0',
  `crystal` int(11) NOT NULL DEFAULT '0',
  `stone` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `items`
--

CREATE TABLE IF NOT EXISTS `items` (
  `id_item` int(11) NOT NULL,
  `id_other` varchar(3) NOT NULL,
  `item_name` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `items`
--

INSERT INTO `items` (`id_item`, `id_other`, `item_name`) VALUES
(0, 'R11', 'Honey'),
(1, 'R12', 'Herbs'),
(2, 'R13', 'Clay'),
(3, 'R14', 'Mineral'),
(4, 'R21', 'Potion'),
(5, 'R22', 'Incense'),
(6, 'R23', 'Gems'),
(7, 'R31', 'Life  Elixir'),
(8, 'R32', 'Mana Crystal'),
(9, 'R41', 'Philosopher Stone');

-- --------------------------------------------------------

--
-- Table structure for table `mixitem`
--

CREATE TABLE IF NOT EXISTS `mixitem` (
  `item1` int(11) NOT NULL,
  `item2` int(11) NOT NULL,
  `item_result` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `mixitem`
--

INSERT INTO `mixitem` (`item1`, `item2`, `item_result`) VALUES
(0, 1, 4),
(1, 0, 4),
(1, 2, 5),
(2, 1, 5),
(2, 3, 6),
(3, 2, 6),
(4, 5, 7),
(5, 4, 7),
(5, 6, 8),
(6, 5, 8),
(7, 8, 9),
(8, 7, 9);

-- --------------------------------------------------------

--
-- Table structure for table `offer`
--

CREATE TABLE IF NOT EXISTS `offer` (
`id_offer` int(11) NOT NULL,
  `offered_item` int(11) NOT NULL,
  `n1` int(11) NOT NULL,
  `demanded_item` int(11) NOT NULL,
  `n2` int(11) NOT NULL,
  `self` tinyint(1) NOT NULL,
  `offer_token` varchar(35) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
`id` int(11) NOT NULL,
  `username` varchar(30) NOT NULL,
  `password` varchar(30) NOT NULL,
  `position_x` int(11) NOT NULL DEFAULT '1',
  `position_y` int(11) NOT NULL DEFAULT '1'
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `inventory`
--
ALTER TABLE `inventory`
 ADD PRIMARY KEY (`id_user`);

--
-- Indexes for table `items`
--
ALTER TABLE `items`
 ADD PRIMARY KEY (`id_item`);

--
-- Indexes for table `mixitem`
--
ALTER TABLE `mixitem`
 ADD PRIMARY KEY (`item1`,`item2`);

--
-- Indexes for table `offer`
--
ALTER TABLE `offer`
 ADD PRIMARY KEY (`id_offer`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
 ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `offer`
--
ALTER TABLE `offer`
MODIFY `id_offer` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `inventory`
--
ALTER TABLE `inventory`
ADD CONSTRAINT `constraint_inventory_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
