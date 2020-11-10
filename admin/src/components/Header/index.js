import React, { useState } from "react";
import {
  Box,
  Flex,
  FormLabel,
  Heading,
  Switch,
  Text,
  useColorMode,
} from "@chakra-ui/core";
import { FaMoon, FaRegMoon } from "react-icons/fa";
import { Link } from "react-router-dom";

const MenuItems = ({ children }) => (
  <Text mt={{ base: 4, md: 0 }} mr={6} display="block">
    {children}
  </Text>
);

const Header = () => {
  const [show, setShow] = useState(false);
  const { colorMode, toggleColorMode } = useColorMode();
  const color = { light: "teal.500", dark: "#243B53" };

  const handleToggle = () => setShow(!show);

  return (
    <Flex
      as="nav"
      align="center"
      justify="space-between"
      wrap="wrap"
      padding="1.5rem"
      bg={color[colorMode]}
      color="white"
    >
      <Flex align="center" mr={5}>
        <Heading as="h1" size="lg">
          <Link to="/">SQAM</Link>
        </Heading>
      </Flex>

      <Box display={{ sm: "block", md: "none" }} onClick={handleToggle}>
        <svg
          fill="white"
          width="12px"
          viewBox="0 0 20 20"
          xmlns="http://www.w3.org/2000/svg"
        >
          <title>Menu</title>
          <path d="M0 3h20v2H0V3zm0 6h20v2H0V9zm0 6h20v2H0v-2z" />
        </svg>
      </Box>

      <Box
        display={{ sm: show ? "block" : "none", md: "flex" }}
        width={{ sm: "full", md: "auto" }}
        alignItems="center"
        flexGrow={1}
      >
        <MenuItems>
          <Link to="/tasks">Tasks</Link>
        </MenuItems>
      </Box>

      <Box
        display={{ sm: show ? "block" : "none", md: "block" }}
        width={{ sm: "full", md: "auto" }}
        mt={{ base: 4, md: 0 }}
        alignItems="center"
      >
        <p className="smaller-text">Logged in as: liutmich</p>
        <FormLabel>
          {colorMode === "light" ? <FaRegMoon /> : <FaMoon />}
        </FormLabel>
        <Switch color="gray" onChange={() => toggleColorMode()} />
      </Box>
    </Flex>
  );
};

export default Header;
