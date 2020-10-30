import React from "react";
import { ThemeProvider, ColorModeProvider } from "@chakra-ui/core";
import { BrowserRouter as Router } from "react-router-dom";

export const wrapInTheme = (component) => {
  return (
    <ThemeProvider>
      <ColorModeProvider>{component}</ColorModeProvider>
    </ThemeProvider>
  );
};
export const wrapComponentInRouter = (component) => (
  <Router>{component}</Router>
);
