import React from "react";
import { ThemeProvider, ColorModeProvider } from "@chakra-ui/core";
import { SnackbarProvider } from "notistack";
import { BrowserRouter as Router } from "react-router-dom";

export const wrapInTheme = (component) => {
  return (
    <ThemeProvider>
      <ColorModeProvider>{component}</ColorModeProvider>
    </ThemeProvider>
  );
};
export const wrapInRouter = (component) => <Router>{component}</Router>;

export const wrapInSnackbar = (component) => (
  <SnackbarProvider>{component}</SnackbarProvider>
);

export const wrapInAll = (component) => {
  return wrapInTheme(wrapInSnackbar(wrapInRouter(component)));
};
