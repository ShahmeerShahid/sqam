import { Box, Stack } from "@chakra-ui/core";
import React from "react";
import ContributorsList from "../../components/ContributorsList";
import Header from "../../components/Header";
import Info from "../../components/Info";
import Instructions from "../../components/Instructions";

export function HomePage() {
  return (
    <div>
      <Header />
      <Stack direction="row">
        <Box w="75%">
          <Info />
          <Instructions />
        </Box>
        <Box w="25%">
          <ContributorsList />
        </Box>
      </Stack>
    </div>
  );
}
export default HomePage;
